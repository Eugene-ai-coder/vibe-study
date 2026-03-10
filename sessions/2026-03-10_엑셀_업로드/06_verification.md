# 06. 검증 보고서 — 대표가입 관리 엑셀 업로드/다운로드

## 1. 수용 기준 충족 여부

| # | 수용 기준 | 판정 | 비고 |
|---|---|---|---|
| 1 | 엑셀 다운로드: 선택한 행의 3개 필드가 .xlsx로 정상 다운로드 | **Pass** | API 200, 유효한 xlsx 파일 생성 확인 (3,433 bytes) |
| 2 | 엑셀 업로드: 파일 업로드 후 그리드에 미저장 상태로 병합/추가 표시 | **Pass** | API 200, 파싱 결과 정상 반환. 프론트엔드 병합 로직 코드 확인 |
| 3 | 유효성 검증: 존재하지 않는 가입ID는 에러 표시 + 선택 불가 | **Pass** | `parseExcel()` 에서 `existsById` 검증, `disabledRowIds` 연동 코드 확인 |
| 4 | 변경 여부: 원본 대비 변경된 행에 상태 표시 | **Pass** | `mergedData` computed에서 `originalDataMap` 비교 → `changeYn` 세팅 확인 |
| 5 | 선택 저장: 체크된 행만 DB에 벌크 저장 성공 | **Fail** | `saveBulk` API 호출 시 500 에러. ID 컬럼 길이 초과 (아래 상세) |

---

## 2. 계획 준수 여부

| Step | 계획 항목 | 준수 여부 | 비고 |
|---|---|---|---|
| 1 | POI 의존성 + Multipart 설정 | 준수 | `pom.xml`, `application.properties` 정상 |
| 2 | DTO 신규 생성 2개 | 준수 | `SubscriptionMainExcelResponseDto`, `SubscriptionMainBulkRequestDto` |
| 3 | Service 인터페이스 확장 | 준수 | 3개 메서드 추가 |
| 4 | ServiceImpl 구현 | **부분 준수** | ID 길이 초과 문제 (아래 Fail #1 참조) |
| 5 | Controller 엔드포인트 3개 | 준수 | `/excel/download`, `/excel/upload`, `/bulk` |
| 6 | DataGrid 체크박스 확장 | 준수 | `selectable`, `selectedRowIds`, `disabledRowIds` props |
| 7 | API 함수 3개 추가 | 준수 | `subscriptionMainApi.js` |
| 8 | SubscriptionMainPage 전면 개편 | 준수 | 상태 관리, 병합 로직, 버튼 배치 |

---

## 3. API 실동작 검증

### 3.1 GET /api/subscription-main (기존 목록 조회)

- **상태코드:** 200
- **응답:** 페이징 정상 (totalElements: 30, 5건씩 6페이지)
- **판정:** Pass

### 3.2 POST /api/subscription-main/excel/download

- **요청:** `[{"subsId":"SUBS0001","mainSubsYn":"Y"},{"subsId":"SUBS0002","mainSubsYn":"N","mainSubsId":"SUBS0001"}]`
- **상태코드:** 200
- **응답:** 유효한 .xlsx 파일 (3,433 bytes, Microsoft Excel 2007+ 형식)
- **판정:** Pass

### 3.3 POST /api/subscription-main/excel/upload

- **요청:** 다운로드된 xlsx 파일 업로드
- **상태코드:** 200
- **응답:** `[{"subsId":"SUBS0001","mainSubsYn":"Y","mainSubsId":"","valid":true,"errorMessage":null},{"subsId":"SUBS0002","mainSubsYn":"N","mainSubsId":"SUBS0001","valid":true,"errorMessage":null}]`
- **판정:** Pass

### 3.4 POST /api/subscription-main/bulk

- **요청:** `{"items":[{"subsId":"SUBS0001","mainSubsYn":"Y","mainSubsId":null,"createdBy":"user01"}]}`
- **상태코드:** 500
- **에러:** `Value too long for column "SUBS_MAIN_ID CHARACTER VARYING(20)": "'SM20260310021714968000' (22)"`
- **원인:** `generateId()` 변경으로 ID가 22자로 생성 (`SM` 2자 + timestamp 17자 + counter 3자 = 22자), 컬럼 `subs_main_id`는 `VARCHAR(20)` 제한
- **판정:** **Fail**

---

## 4. Fail 항목 상세

### Fail #1: generateId() ID 길이 초과 — 벌크 저장 500 에러

- **파일:** `SubscriptionMainServiceImpl.java` line 94-97
- **현상:** `generateId()`가 AtomicLong 카운터 3자리를 추가하여 총 22자 ID 생성. Entity `@Column(length=20)` 위반
- **영향:** `/bulk` 엔드포인트 전체 실패. 기존 단건 `/` POST도 동일하게 영향받음
- **수정 방안:** (1) Entity 컬럼 길이를 25로 확장, 또는 (2) 카운터를 2자리로 줄이기, 또는 (3) timestamp에서 밀리초 자릿수 조정

**추가 영향 분석:** 이 generateId() 변경은 기존 단건 save() API에도 영향을 미침. 즉, 이 변경 이후 기존 대표가입 저장 기능도 모두 깨짐 (회귀).

### Fail #2 (잠재적): saveBulk() 예외 미포착

- **파일:** `SubscriptionMainServiceImpl.java` line 158-176
- **현상:** `saveBulk()`의 try-catch가 `ResponseStatusException`만 포착. 그러나 `save()` 내부에서 `repository.save()`가 `DataIntegrityViolationException` 등 JPA 예외를 throw할 수 있으며, 이는 catch되지 않아 전체 벌크 처리가 중단됨
- **수정 방안:** catch 범위를 `Exception`으로 확장하거나, `DataIntegrityViolationException`을 추가 catch

---

## 5. 회귀 영향도 분석

| 수정 파일 | 의존 컴포넌트 | 영향 여부 | 상세 |
|---|---|---|---|
| `pom.xml` | 전체 빌드 | 영향 없음 | POI 추가만, 기존 의존성 변경 없음 |
| `application.properties` | 전체 앱 | 영향 없음 | Multipart 설정 추가만 |
| `SubscriptionMainController.java` | SubscriptionMainPage | **영향 있음** | 기존 GET/POST 유지, 신규 3개 추가. 기존 기능 코드 미변경 |
| `SubscriptionMainService.java` | SubscriptionMainServiceImpl | 영향 없음 | 신규 메서드 추가만 |
| `SubscriptionMainServiceImpl.java` | SubscriptionMainController | **회귀 발생** | `generateId()` 변경으로 기존 단건 save도 ID 길이 초과 에러 발생 |
| `DataGrid.vue` | UserPage, QnaPage, SubscriptionPage, RolePage, SpecialSubscriptionPage | **영향 없음** | `selectable` 기본값 `false`, 기존 페이지는 해당 prop 미사용 |
| `subscriptionMainApi.js` | SubscriptionMainPage | 영향 없음 | 신규 함수 추가만, 기존 함수 미변경 |
| `SubscriptionMainPage.vue` | 해당 페이지만 | 해당 없음 | 전면 재작성 |

**회귀 위험:**
- `generateId()` 변경이 기존 단건 저장(`POST /api/subscription-main`) 기능을 파손시킴 (ID 22자 > 컬럼 20자)

---

## 6. 코드 리뷰

### 6.1 백엔드

| 파일 | 관점 | 판정 | 상세 |
|---|---|---|---|
| `SubscriptionMainExcelResponseDto.java` | 네이밍/구조 | **Pass** | Lombok 미사용, getter/setter 수동 선언, 프로젝트 표준 준수 |
| `SubscriptionMainBulkRequestDto.java` | 네이밍/구조 | **Pass** | 최소 구조 |
| `SubscriptionMainService.java` | 레이어 책임 | **Pass** | 인터페이스 확장만 |
| `SubscriptionMainServiceImpl.java` | 중복 코드 | **Pass** | `saveBulk()`가 기존 `save()` 재사용 |
| `SubscriptionMainServiceImpl.java` | 레이어 책임 | **Pass** | 비즈니스 로직이 Service에 집중 |
| `SubscriptionMainServiceImpl.java` | 하드코딩 | **Warn** | 엑셀 헤더 "가입ID", "대표가입여부", "대표가입ID" 문자열 하드코딩 (허용 범위) |
| `SubscriptionMainServiceImpl.java` | 미사용 import | **Pass** | 모든 import 사용됨 |
| `SubscriptionMainServiceImpl.java` | 버그 | **Fail** | `generateId()` ID 길이 초과 (22자 > 20자 컬럼) |
| `SubscriptionMainServiceImpl.java` | 에러 처리 | **Warn** | `saveBulk()` try-catch가 `ResponseStatusException`만 포착. `DataIntegrityViolationException` 등 미포착 |
| `SubscriptionMainController.java` | 레이어 책임 | **Pass** | Controller에 비즈니스 로직 없음, 위임만 수행 |

### 6.2 프론트엔드

| 파일 | 관점 | 판정 | 상세 |
|---|---|---|---|
| `DataGrid.vue` | 기존 기능 호환 | **Pass** | `selectable=false` 기본값으로 기존 페이지 무영향 |
| `DataGrid.vue` | 중복 코드 | **Pass** | 체크박스 로직 간결 |
| `DataGrid.vue` | 네이밍 | **Pass** | `selectedRowIds`, `disabledRowIds`, `selectionChange` 일관성 |
| `SubscriptionMainPage.vue` | 상태 관리 | **Pass** | `EMPTY_FORM` 패턴 불필요 (입력폼 제거됨), `clearMessages` 패턴 준수 |
| `SubscriptionMainPage.vue` | 에러 처리 | **Pass** | try-catch + errorMsg/successMsg 패턴 준수 |
| `SubscriptionMainPage.vue` | 불필요 코드 | **Warn** | `handleRowClick` 빈 함수 — 체크박스 모드에서 미사용이라면 제거 가능 |
| `SubscriptionMainPage.vue` | UI 표준 | **Pass** | FloatingActionBar 사용, 버튼 색상 계층 준수 |
| `subscriptionMainApi.js` | apiClient 경유 | **Pass** | `apiClient` 경유 필수 규칙 준수 |

---

## 7. UI 수동 확인 체크리스트 (설계자 수행)

agent-browser 도구가 사용 불가하여 아래 항목을 설계자가 직접 확인해야 합니다.

### 7.1 대표가입 관리 페이지 (`/subscription-main`)

- [ ] 페이지 진입 시 조회영역 + 빈 그리드 정상 렌더링
- [ ] 조회 후 그리드에 데이터 표시 + 체크박스 컬럼 노출
- [ ] 전체선택 체크박스 동작 (선택/해제)
- [ ] 개별 행 체크박스 동작
- [ ] 엑셀 다운로드 버튼: 선택 행 없을 때 에러 Toast
- [ ] 엑셀 다운로드 버튼: 선택 후 xlsx 파일 다운로드 확인
- [ ] 엑셀 업로드 버튼: 파일 선택 다이얼로그 동작
- [ ] 엑셀 업로드: 정상 파일 업로드 시 그리드 병합/추가
- [ ] 엑셀 업로드: 미존재 가입ID 포함 시 에러 행 처리결과 표시 + 체크박스 disabled
- [ ] 변경여부 컬럼: 업로드로 값 변경 시 '변경' 표시
- [ ] 변경여부 컬럼: 신규 추가 행 '신규' 표시
- [ ] 저장 버튼: 선택 행 없을 때 에러 Toast (현재 벌크 저장 500 에러이므로 ID 수정 후 재검증 필요)
- [ ] 페이징 동작 정상
- [ ] 기존 입력폼 영역 제거 확인

### 7.2 기존 페이지 회귀 확인

- [ ] 사용자 관리 페이지: DataGrid 체크박스 미노출 확인
- [ ] QnA 페이지: DataGrid 정상 동작
- [ ] 가입 관리 페이지: DataGrid 정상 동작
- [ ] 역할 관리 페이지: DataGrid 정상 동작
- [ ] 특수가입 관리 페이지: DataGrid 정상 동작

---

## 8. 검증 결과 요약

| 구분 | Pass | Warn | Fail |
|---|---|---|---|
| 수용 기준 | 4 | 0 | 1 |
| API 실동작 | 3 | 0 | 1 |
| 코드 리뷰 (백엔드) | 8 | 2 | 1 |
| 코드 리뷰 (프론트엔드) | 6 | 1 | 0 |
| **합계** | **21** | **3** | **2** |

### Fail 항목 요약

1. **Fail #1 — generateId() ID 길이 초과 (Critical)**
   - `SubscriptionMainServiceImpl.java` line 94-97
   - ID가 22자로 생성되나 컬럼은 VARCHAR(20)
   - 벌크 저장 API 전체 실패 + 기존 단건 저장도 회귀 파손
   - 수정 필요: Entity 컬럼 길이 확장 또는 ID 생성 로직 수정

2. **Fail #2 — saveBulk() 예외 포착 범위 부족 (Medium)**
   - `SubscriptionMainServiceImpl.java` line 158-176
   - `ResponseStatusException`만 catch하여 JPA 예외 미포착
   - 수정 필요: catch 범위 확장

---

## 9. 재검증 (2026-03-10)

### 9.1 Fail #1 재검증: generateId() ID 길이 초과

- **코드 확인:** `SubscriptionMainServiceImpl.java` line 92-94
  - `generateId()`가 `"SM" + LocalDateTime.now().format(ID_FORMATTER)` 패턴으로 복원됨
  - AtomicLong 카운터 코드 완전 제거 확인
  - `backend-rules.md` 4절 ID 자동생성 패턴(`{접두사} + yyyyMMddHHmmssSSS`)과 정확히 일치
  - 생성 ID 길이: `SM`(2자) + `yyyyMMddHHmmssSSS`(17자) = 19자 (VARCHAR(20) 이내)
- **실동작 확인:** 서버 재기동 후 단건 저장 시 생성된 ID `SM20260310023741239` = 19자
- **판정: Pass**

### 9.2 Fail #2 재검증: saveBulk() 예외 포착 범위 부족

- **코드 확인:** `SubscriptionMainServiceImpl.java` line 166
  - catch 범위가 `ResponseStatusException` → `Exception`으로 확장됨
  - JPA 예외(`DataIntegrityViolationException` 등) 포함 모든 예외 포착
- **판정: Pass**

### 9.3 API 실동작 재검증

#### POST /api/subscription-main/bulk (벌크 저장)

- **요청:** `{"items":[{"subsId":"SUBS0001","mainSubsYn":"Y","mainSubsId":null}]}`
- **상태코드:** 200
- **응답:** `[{"subsId":"SUBS0001","mainSubsYn":"Y","mainSubsId":null,"valid":true,"errorMessage":"저장완료"}]`
- **판정: Pass**

#### POST /api/subscription-main (단건 저장 — 회귀 확인)

- **요청:** `{"subsId":"SUBS0002","mainSubsYn":"N","mainSubsId":"SUBS0001"}`
- **상태코드:** 201
- **응답:** 정상 생성 (ID: `SM20260310023741239`, 19자)
- **판정: Pass**

### 9.4 재검증 결과 요약

| 구분 | 판정 |
|---|---|
| Fail #1 — generateId() ID 길이 초과 | **Pass** (AtomicLong 제거, 19자 생성 확인) |
| Fail #2 — saveBulk() 예외 포착 범위 | **Pass** (catch Exception 확장 확인) |
| POST /api/subscription-main/bulk | **Pass** (HTTP 200, 저장완료) |
| POST /api/subscription-main | **Pass** (HTTP 201, 회귀 없음) |

### 전체 Pass
