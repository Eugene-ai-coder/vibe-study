# 03. 구현 계획서 — 대표가입 관리 엑셀 업로드/다운로드

## 1. 구현 전략 개요

**접근 방식:** 백엔드(의존성/설정 → 엑셀 파싱/생성 → 벌크 저장 API) → 프론트엔드(DataGrid 체크박스 확장 → 페이지 UI/상태 → API 연결) 순서로 진행한다.

**핵심 설계 결정:**
- 엑셀 업로드는 서버에서 파싱+유효성 검증만 수행하고, 결과를 JSON으로 반환하여 프론트엔드 상태로 관리 (미저장 상태 유지)
- 벌크 저장은 기존 `save()` 단건 로직을 재사용하되, 단일 트랜잭션으로 묶음
- DataGrid 체크박스는 `selectable` prop으로 옵트인 (기존 페이지 영향 없음)
- ID 충돌 방지: `AtomicLong` 카운터를 `generateId()`에 추가

---

## 2. 변경 파일 목록

### 2.1 백엔드

| 파일 | 변경 유형 | 설명 |
|---|---|---|
| `pom.xml` | 수정 | Apache POI 의존성 추가 |
| `application.properties` | 수정 | Multipart 파일 크기 설정 추가 |
| `SubscriptionMainController.java` | 수정 | 엑셀 다운로드/업로드/벌크 저장 엔드포인트 3개 추가 |
| `SubscriptionMainService.java` | 수정 | 인터페이스에 메서드 3개 추가 |
| `SubscriptionMainServiceImpl.java` | 수정 | 엑셀 생성, 엑셀 파싱+검증, 벌크 저장 구현 |
| `SubscriptionMainBulkRequestDto.java` | **신규** | 벌크 저장 요청 DTO (List 래퍼) |
| `SubscriptionMainExcelResponseDto.java` | **신규** | 엑셀 파싱 결과 DTO (가입ID, 대표가입여부, 대표가입ID, 유효여부, 에러메시지) |

### 2.2 프론트엔드

| 파일 | 변경 유형 | 설명 |
|---|---|---|
| `DataGrid.vue` | 수정 | 체크박스 선택 기능 추가 (`selectable` prop) |
| `SubscriptionMainPage.vue` | 수정 | 엑셀 업/다운로드 UI, 변경여부/처리결과 컬럼, 벌크 저장, 상태 관리 |
| `subscriptionMainApi.js` | 수정 | 엑셀 다운로드/업로드/벌크 저장 API 함수 3개 추가 |

---

## 3. 단계별 구현 순서

### Step 1: 백엔드 — 의존성 및 설정

**pom.xml — Apache POI 추가**

```xml
<!-- Before: POI 의존성 없음 -->

<!-- After -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

**application.properties — Multipart 설정 추가**

```properties
# Before: 설정 없음

# After
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

---

### Step 2: 백엔드 — DTO 신규 생성

**SubscriptionMainExcelResponseDto.java** — 엑셀 파싱 결과 1건

```java
package com.example.vibestudy;

public class SubscriptionMainExcelResponseDto {
    private String subsId;
    private String mainSubsYn;
    private String mainSubsId;
    private boolean valid;       // 유효성 검증 통과 여부
    private String errorMessage; // 에러 메시지 (valid=false일 때)

    // getter/setter 수동 선언 (Lombok 미사용)
}
```

**SubscriptionMainBulkRequestDto.java** — 벌크 저장 요청

```java
package com.example.vibestudy;

import java.util.List;

public class SubscriptionMainBulkRequestDto {
    private List<SubscriptionMainRequestDto> items;

    // getter/setter 수동 선언
}
```

---

### Step 3: 백엔드 — Service 인터페이스 확장

**SubscriptionMainService.java**

```java
// Before
public interface SubscriptionMainService {
    Page<SubscriptionMainListResponseDto> findListPage(...);
    SubscriptionMainResponseDto save(SubscriptionMainRequestDto dto);
}

// After — 3개 메서드 추가
public interface SubscriptionMainService {
    Page<SubscriptionMainListResponseDto> findListPage(...);
    SubscriptionMainResponseDto save(SubscriptionMainRequestDto dto);

    byte[] generateExcel(List<SubscriptionMainRequestDto> items);
    List<SubscriptionMainExcelResponseDto> parseExcel(MultipartFile file);
    List<SubscriptionMainResponseDto> saveBulk(SubscriptionMainBulkRequestDto dto);
}
```

---

### Step 4: 백엔드 — ServiceImpl 구현

**SubscriptionMainServiceImpl.java — 추가 메서드 3개**

**(1) generateExcel()** — POI Workbook 생성, 헤더 3개(`가입ID`, `대표가입여부`, `대표가입ID`), 데이터 행 쓰기, byte[] 반환

**(2) parseExcel()** — MultipartFile → Workbook 파싱, 각 행을 `SubscriptionMainExcelResponseDto`로 변환, `subscriptionRepository.existsById(subsId)`로 유효성 검증, 미존재 시 `valid=false` + 에러 메시지 세팅

**(3) saveBulk()** — `@Transactional` 단일 트랜잭션. items 순회하며 기존 `save()` 단건 로직을 내부 메서드로 추출하여 재사용. 각 건의 결과를 List로 수집하여 반환.

**ID 충돌 방지 — generateId() 개선**

```java
// Before
private String generateId() {
    return "SM" + LocalDateTime.now().format(ID_FORMATTER);
}

// After — AtomicLong 카운터 추가
private static final AtomicLong ID_COUNTER = new AtomicLong(0);

private String generateId() {
    return "SM" + LocalDateTime.now().format(ID_FORMATTER)
           + String.format("%03d", ID_COUNTER.getAndIncrement() % 1000);
}
```

---

### Step 5: 백엔드 — Controller 엔드포인트 추가

**SubscriptionMainController.java**

```java
// After — 3개 엔드포인트 추가

@PostMapping("/excel/download")
public ResponseEntity<byte[]> downloadExcel(
        @RequestBody List<SubscriptionMainRequestDto> items) {
    byte[] bytes = service.generateExcel(items);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=subscription_main.xlsx")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(bytes);
}

@PostMapping("/excel/upload")
public ResponseEntity<List<SubscriptionMainExcelResponseDto>> uploadExcel(
        @RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(service.parseExcel(file));
}

@PostMapping("/bulk")
public ResponseEntity<List<SubscriptionMainResponseDto>> saveBulk(
        @RequestBody SubscriptionMainBulkRequestDto dto) {
    return ResponseEntity.status(201).body(service.saveBulk(dto));
}
```

**설계 결정 — 다운로드를 POST로 하는 이유:**
선택된 행의 데이터를 Request Body로 전달받아 엑셀 생성. 프론트엔드에서 체크된 행의 3개 필드만 전송하므로 GET 쿼리스트링보다 POST Body가 적합.

---

### Step 6: 프론트엔드 — DataGrid 체크박스 확장

**DataGrid.vue — `selectable` prop 추가**

```vue
<!-- Before: 체크박스 없음 -->

<!-- After: props 추가 -->
props: {
  selectable: { type: Boolean, default: false },
  selectedRowIds: { type: Set, default: () => new Set() },
  disabledRowIds: { type: Set, default: () => new Set() },
}

emits: ['rowClick', 'selectionChange']
```

**변경 내용:**
1. `selectable=true`일 때 헤더에 전체선택 체크박스, 각 행에 개별 체크박스 컬럼 추가
2. `disabledRowIds`에 포함된 행은 체크박스 `disabled` 처리 (에러 행 선택 방지)
3. 선택 변경 시 `@selectionChange` 이벤트로 선택된 ID Set 전달
4. 체크박스 컬럼은 `visibleColumns` 앞에 자동 삽입 (너비 40px 고정, 리사이즈/드래그 대상 아님)
5. `selectable=false`(기본값)인 기존 페이지는 영향 없음

---

### Step 7: 프론트엔드 — API 함수 추가

**subscriptionMainApi.js**

```javascript
// Before: 2개 함수

// After: 5개 함수
export const downloadSubscriptionMainExcel = (items) =>
  apiClient.post('/subscription-main/excel/download', items, {
    responseType: 'blob'
  }).then(r => r.data)

export const uploadSubscriptionMainExcel = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return apiClient.post('/subscription-main/excel/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }).then(r => r.data)
}

export const saveSubscriptionMainBulk = (items) =>
  apiClient.post('/subscription-main/bulk', { items }).then(r => r.data)
```

**apiClient 헤더 오버라이드:** 엑셀 업로드 시 개별 호출에서 `Content-Type: multipart/form-data`로 오버라이드. apiClient 기본 설정(`application/json`)은 변경하지 않음.

---

### Step 8: 프론트엔드 — SubscriptionMainPage.vue 전면 개편

**8.1 상태 추가**

```javascript
// 신규 상태
const selectedIds = ref(new Set())       // 체크박스 선택된 subsId Set
const originalDataMap = ref(new Map())   // subsId → 원본 데이터 (서버 조회 시점)
const uploadedRows = ref([])             // 업로드로 추가된 행 (기존 그리드에 없던 것)
const resultMap = ref(new Map())         // subsId → 처리결과 메시지
const disabledIds = ref(new Set())       // 유효성 에러 행의 subsId (선택 불가)
```

**8.2 컬럼 정의 확장**

```javascript
// Before: 6개 컬럼
// After: 8개 컬럼 (변경여부, 처리결과 추가)

{ key: 'changeYn', header: '변경여부', size: 80,
  cell: /* subsId로 originalDataMap과 비교하여 '변경'/''/null 표시 */ },
{ key: 'resultMsg', header: '처리결과', size: 150,
  cell: /* resultMap에서 해당 subsId의 메시지 표시 */ },
```

**8.3 그리드에 DataGrid selectable 적용**

```vue
<!-- Before -->
<DataGrid :columns="columns" :data="items" ... @row-click="handleRowClick" />

<!-- After -->
<DataGrid
  :columns="columns"
  :data="mergedData"
  selectable
  :selected-row-ids="selectedIds"
  :disabled-row-ids="disabledIds"
  @selection-change="selectedIds = $event"
  @row-click="handleRowClick"
  ...
/>
```

**8.4 데이터 병합 로직 (`mergedData` computed)**

```javascript
const mergedData = computed(() => {
  // 서버 조회 데이터(items) + 업로드로 신규 추가된 행(uploadedRows) 통합
  // 동일 subsId는 업로드 값이 우선 (덮어쓰기)
  // 각 행에 changeYn, resultMsg 필드 동적 추가
})
```

**8.5 엑셀 다운로드 핸들러**

```javascript
const handleExcelDownload = async () => {
  if (selectedIds.value.size === 0) { errorMsg.value = '다운로드할 행을 선택해 주세요.'; return }
  const items = mergedData.value
    .filter(r => selectedIds.value.has(r.subsId))
    .map(r => ({ subsId: r.subsId, mainSubsYn: r.mainSubsYn, mainSubsId: r.mainSubsId }))
  const blob = await downloadSubscriptionMainExcel(items)
  // Blob → URL.createObjectURL → <a> 클릭 → 다운로드 트리거
}
```

**8.6 엑셀 업로드 핸들러**

```javascript
const handleExcelUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  const results = await uploadSubscriptionMainExcel(file)
  // results: [{ subsId, mainSubsYn, mainSubsId, valid, errorMessage }]

  results.forEach(row => {
    // 기존 items에 동일 subsId가 있으면 → 해당 행의 mainSubsYn, mainSubsId 덮어쓰기
    // 없으면 → uploadedRows에 신규 추가
    // valid=false → disabledIds에 추가, resultMap에 에러 메시지 세팅
  })
  event.target.value = '' // input 초기화
}
```

**8.7 벌크 저장 핸들러**

```javascript
const handleBulkSave = async () => {
  if (selectedIds.value.size === 0) { errorMsg.value = '저장할 행을 선택해 주세요.'; return }
  const items = mergedData.value
    .filter(r => selectedIds.value.has(r.subsId))
    .map(r => ({ subsId: r.subsId, mainSubsYn: r.mainSubsYn, mainSubsId: r.mainSubsId }))
  const results = await saveSubscriptionMainBulk(items)
  // 성공/실패 결과를 resultMap에 반영
  // 성공 건은 originalDataMap 갱신 (변경여부 초기화)
  successMsg.value = `${results.length}건 저장이 완료되었습니다.`
  await fetchList(page.value) // 목록 갱신
}
```

**8.8 FloatingActionBar 버튼 배치**

```vue
<!-- Before -->
<FloatingActionBar>
  <button @click="handleSaveClick" ...>저장</button>
</FloatingActionBar>

<!-- After -->
<FloatingActionBar>
  <label class="h-8 px-4 border border-gray-300 text-gray-600 rounded text-sm
                cursor-pointer hover:bg-gray-50 flex items-center">
    엑셀 업로드
    <input type="file" accept=".xlsx" class="hidden" @change="handleExcelUpload" />
  </label>
  <button @click="handleExcelDownload"
    class="h-8 px-4 border border-blue-600 text-blue-600 rounded text-sm hover:bg-blue-50">
    엑셀 다운로드
  </button>
  <button @click="handleBulkSave"
    class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">
    저장
  </button>
</FloatingActionBar>
```

**8.9 기존 단건 저장 제거**

기존 `handleSaveClick`(단건 저장)과 하단 입력 폼 영역은 벌크 저장으로 대체하므로 제거한다. 행 클릭 시 폼 바인딩 로직도 제거.

---

## 4. 트레이드오프 기록

| 결정 | 대안 | 선택 이유 |
|---|---|---|
| 엑셀 파싱을 서버에서 수행 | 프론트에서 SheetJS로 파싱 | 서버 의존성(POI) 추가 비용이 있지만, 유효성 검증(`existsById`)을 서버에서 일괄 수행하여 API 호출 횟수 최소화 |
| 다운로드를 POST로 구현 | GET + 쿼리스트링 | 선택된 행 데이터를 Body로 전달해야 하므로 POST가 적합. GET의 URL 길이 제한 회피 |
| DataGrid에 `selectable` prop 추가 | 페이지에서 직접 체크박스 구현 | 공통 컴포넌트 확장이 재사용성 높음. `selectable=false` 기본값으로 기존 페이지 무영향 |
| 기존 `save()` 로직 재사용 (반복 호출) | 벌크 전용 SQL 작성 | 이력 관리 패턴(종료+INSERT)이 단건 로직에 집약되어 있어 재사용이 안전. 대량(1만건+)은 제외 범위이므로 성능 허용 |
| `AtomicLong` 카운터로 ID 충돌 방지 | nanoTime 사용 | 카운터가 더 예측 가능하고 확실한 유일성 보장 |
| 입력 폼 영역 제거 | 단건 저장과 벌크 저장 공존 | 체크박스 복수 선택 + 벌크 저장이 단건 저장의 상위 호환. 두 가지 저장 UX 공존은 혼란 유발 |

---

## 5. 테스트 계획

### 5.1 백엔드 수동 테스트

| 항목 | 시나리오 | 예상 결과 |
|---|---|---|
| 엑셀 다운로드 | 3건 선택 → POST `/excel/download` | .xlsx 파일, 헤더 3개 + 데이터 3행 |
| 엑셀 업로드 (정상) | 유효한 가입ID 3건 포함 파일 업로드 | 3건 모두 `valid=true` |
| 엑셀 업로드 (일부 에러) | 미존재 가입ID 1건 포함 | 해당 건 `valid=false`, `errorMessage` 세팅 |
| 벌크 저장 | 2건 POST `/bulk` | 2건 모두 저장, 기존 활성 레코드 종료 처리 |
| ID 충돌 방지 | 동시 벌크 저장 10건 | 모든 ID 고유 |

### 5.2 프론트엔드 수동 테스트

| 항목 | 시나리오 | 예상 결과 |
|---|---|---|
| 체크박스 선택 | 전체선택/개별선택/해제 | `selectedIds` 정확히 반영 |
| 엑셀 다운로드 | 2건 체크 → 다운로드 클릭 | .xlsx 파일 다운로드, 2행 |
| 엑셀 업로드 → 병합 | 기존 행 1건 + 신규 1건 포함 파일 | 기존 행 값 덮어쓰기, 신규 행 추가 |
| 변경여부 표시 | 업로드로 값 변경된 행 | '변경' 표시 |
| 에러 행 선택 불가 | 미존재 가입ID 행 | 체크박스 disabled, 처리결과에 에러 메시지 |
| 벌크 저장 | 변경된 2건 체크 → 저장 | 성공 메시지, 처리결과에 '저장완료' |
| 미선택 저장 | 체크 없이 저장 클릭 | 에러 Toast: '저장할 행을 선택해 주세요.' |

---

## 6. 롤백 방안

| 변경 | 롤백 방법 |
|---|---|
| `pom.xml` POI 의존성 | 해당 `<dependency>` 블록 삭제 |
| `application.properties` Multipart 설정 | 추가된 2줄 삭제 |
| Controller/Service/ServiceImpl 메서드 추가 | 추가된 메서드만 삭제 (기존 `getList`, `save`는 미수정) |
| 신규 DTO 2개 | 파일 삭제 |
| DataGrid.vue `selectable` 기능 | 추가된 props/emit/체크박스 렌더링 코드 삭제. `selectable=false` 기본값이므로 기존 페이지 복원 불필요 |
| SubscriptionMainPage.vue | Git에서 이전 버전 복원 (`git checkout HEAD -- frontend-vue/src/pages/SubscriptionMainPage.vue`) |
| subscriptionMainApi.js | 추가된 3개 함수 삭제 |

**전체 롤백:** `git revert` 단일 커밋으로 전체 원복 가능하도록 기능 완성 후 하나의 커밋으로 통합.

---

## Summary
- **구현 순서**: (1) POI 의존성+Multipart 설정 → (2) 신규 DTO 2개 → (3) Service 인터페이스 확장 → (4) ServiceImpl 구현(엑셀 생성/파싱/벌크저장, ID 충돌 방지) → (5) Controller 엔드포인트 3개 → (6) DataGrid 체크박스(`selectable` prop) → (7) API 함수 3개 → (8) SubscriptionMainPage 전면 개편(상태관리/병합/다운로드/업로드/벌크저장/입력폼 제거)
- **변경 파일**: `pom.xml`, `application.properties`, `SubscriptionMainController.java`, `SubscriptionMainService.java`, `SubscriptionMainServiceImpl.java`, `DataGrid.vue`, `SubscriptionMainPage.vue`, `subscriptionMainApi.js`
- **신규 파일**: `SubscriptionMainExcelResponseDto.java`, `SubscriptionMainBulkRequestDto.java`
- **핵심 설계**: 엑셀 파싱은 서버(POI), 미저장 상태는 프론트 관리, 벌크 저장은 기존 단건 로직 재사용(@Transactional), DataGrid 체크박스는 `selectable` 옵트인으로 기존 페이지 무영향
- **리스크 대응**: ID 충돌→AtomicLong 카운터, apiClient 헤더→개별 호출 오버라이드, 에러 행→disabledRowIds로 선택 차단
