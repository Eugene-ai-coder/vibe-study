# 4단계: 최종 구현 계획 (Final Plan)

> 03_plan.md 기반. 검토 3건 반영 후 확정.

---

## 변경 요약 (03_plan.md → 04_final_plan.md)

| # | 항목 | 변경 내용 |
|---|---|---|
| 검토 1 | 조회 유효성 + LIKE 패턴 | 조회조건 항상 필수 / 2자 이상 / `keyword%` (전방 일치) |
| 검토 2 | LEFT JOIN 구조 명시 | 유효기간 조건 ON절 배치 강조. '대표가입ID' 검색 시 sm NULL 행 제외 유지 |
| 검토 3 | 현재시각 참조 통일 | `findListRaw`도 `:now` 파라미터 받기 (서비스에서 주입) |

---

## 1. 구현 전략 개요

**순서 원칙**: 백엔드(Entity → Repository → DTO → Service → Controller) → 프론트엔드(api → hook → components → page → router/sidebar) → 레이아웃 통일

**핵심 복잡도 포인트**:
- `tb_subscription_main` 이력 관리: 저장 시 기존 유효 레코드 종료(effEndDt=now) + 신규 INSERT
- 목록 조회: LEFT OUTER JOIN + 유효기간 조건 **ON절** 배치 (WHERE절 배치 시 INNER JOIN 효과 발생)
- 대표가입여부 Y/N 연동: Form 내 conditional UI (N → mainSubsId 필수, Y → null·비활성화)
- 조회조건 필수: 항상 keyword 입력 필요 (2자 이상, 전방 일치 LIKE)

---

## 2. 변경 파일 목록

### 2.1 백엔드 신규 생성 (8개)

| 파일 | 역할 |
|---|---|
| `SubscriptionMain.java` | Entity (`tb_subscription_main`) |
| `SubscriptionMainRepository.java` | JpaRepository + Native Query |
| `SubscriptionMainRequestDto.java` | 저장 요청 DTO |
| `SubscriptionMainResponseDto.java` | 단건 응답 DTO |
| `SubscriptionMainListResponseDto.java` | 목록 응답 DTO |
| `SubscriptionMainService.java` | Service Interface |
| `SubscriptionMainServiceImpl.java` | @Transactional 구현체 |
| `SubscriptionMainController.java` | REST `/api/subscription-main` |

### 2.2 백엔드 기존 수정 (1개)

| 파일 | 변경 내용 |
|---|---|
| `SubscriptionServiceImpl.java` | `delete()`: SubscriptionMainRepository.existsBySubsId() 체크 추가 → 409 반환 |

### 2.3 프론트엔드 신규 생성 (8개)

| 파일 | 역할 |
|---|---|
| `api/subscriptionMainApi.js` | API 함수 (목록조회, 저장) |
| `hooks/useSubscriptionMain.js` | 상태·API 연동 커스텀 훅 |
| `components/common/SubscriptionSearchPopup.jsx` | 재사용 가능 가입검색 팝업 |
| `components/subscription-main/SubscriptionMainSearchBar.jsx` | 영역 A: 조회조건 |
| `components/subscription-main/SubscriptionMainList.jsx` | 영역 B: 목록 |
| `components/subscription-main/SubscriptionMainForm.jsx` | 영역 C: 상세 입력 |
| `components/subscription-main/SubscriptionMainActionBar.jsx` | 영역 D: 플로팅 저장 버튼 |
| `pages/SubscriptionMainPage.jsx` | 페이지 통합 컴포넌트 |

### 2.4 프론트엔드 기존 수정 (5개)

| 파일 | 변경 내용 |
|---|---|
| `App.jsx` | `/subscription-main` 라우트 추가 |
| `components/main/Sidebar.jsx` | 과금기준 하위에 "대표가입 관리" 추가 |
| `pages/SubscriptionPage.jsx` | `Layout` → `MainLayout` |
| `pages/BillStdPage.jsx` | `Layout` → `MainLayout` |
| `pages/StudyLogPage.jsx` | `Layout` → `MainLayout` |

---

## 3. 단계별 구현 순서

```
Step 1: SubscriptionMain Entity
Step 2: SubscriptionMainRepository
Step 3: DTO 3종 (Request, Response, ListResponse)
Step 4: SubscriptionMainService + SubscriptionMainServiceImpl
Step 5: SubscriptionMainController
Step 6: SubscriptionServiceImpl.delete() 수정
Step 7: 프론트엔드 (api → hook → components → page → router/sidebar)
Step 8: 레이아웃 통일 (3개 페이지)
```

---

## 4. 단계별 상세 내용

### Step 1: SubscriptionMain.java (Entity)

```java
@Entity
@Table(name = "tb_subscription_main")
public class SubscriptionMain {
    @Id
    @Column(name = "subs_main_id", length = 20, nullable = false)
    private String subsMainId;              // SM + yyyyMMddHHmmssSSS

    @Column(name = "subs_id", length = 50, nullable = false)
    private String subsId;

    @Column(name = "main_subs_yn", length = 1, nullable = false)
    private String mainSubsYn;             // Y or N

    @Column(name = "main_subs_id", length = 50)
    private String mainSubsId;             // NULL if mainSubsYn=Y

    @Column(name = "eff_start_dt", nullable = false)
    private LocalDateTime effStartDt;

    @Column(name = "eff_end_dt", nullable = false)
    private LocalDateTime effEndDt;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;
    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;
    // Getters/Setters
}
```

---

### Step 2: SubscriptionMainRepository.java

```java
public interface SubscriptionMainRepository extends JpaRepository<SubscriptionMain, String> {

    // 유효 레코드 조회 (저장 시 기존 레코드 종료 처리에 사용)
    // ✅ now를 파라미터로 받아 트랜잭션 내 시각 일관성 보장
    @Query("SELECT sm FROM SubscriptionMain sm " +
           "WHERE sm.subsId = :subsId " +
           "AND sm.effStartDt <= :now AND sm.effEndDt >= :now")
    Optional<SubscriptionMain> findActiveBySubsId(
        @Param("subsId") String subsId,
        @Param("now") LocalDateTime now);

    // 가입 삭제 가드용
    boolean existsBySubsId(String subsId);

    // 목록 조회 (Native Query: LEFT OUTER JOIN)
    // ✅ 유효기간 조건은 ON절에 배치 → 대표가입 레코드 없는 가입도 조회됨
    // ✅ now를 파라미터로 받아 findActiveBySubsId와 현재시각 참조 방식 통일
    // ✅ LIKE 패턴: keyword% (전방 일치, 2자 이상은 프론트에서 보장)
    // ✅ '대표가입ID' 검색 시 sm.main_subs_id가 NULL인 행(대표가입 없는 가입)은 제외 (의도된 동작)
    @Query(value = """
        SELECT s.subs_id, s.svc_nm, s.fee_prod_nm,
               sm.main_subs_yn, sm.main_subs_id
        FROM tb_subscription s
        LEFT JOIN tb_subscription_main sm
          ON s.subs_id = sm.subs_id
         AND :now BETWEEN sm.eff_start_dt AND sm.eff_end_dt
        WHERE (:svcNm IS NULL OR s.svc_nm = :svcNm)
          AND (
            (:searchType = '서비스'    AND LOWER(s.svc_nm)        LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '상품'      AND LOWER(s.fee_prod_nm)   LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '가입ID'    AND LOWER(s.subs_id)       LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '대표가입ID' AND LOWER(sm.main_subs_id) LIKE LOWER(CONCAT(:keyword, '%')))
          )
        ORDER BY s.subs_id
        """, nativeQuery = true)
    List<Object[]> findListRaw(
        @Param("svcNm") String svcNm,
        @Param("searchType") String searchType,
        @Param("keyword") String keyword,
        @Param("now") LocalDateTime now);
}
```

> **설계 결정**: Native Query 사용 이유 — JPA 엔티티 간 @ManyToOne 관계가 정의되지 않아 JPQL LEFT JOIN 불가.

---

### Step 3: DTO

**SubscriptionMainRequestDto.java**
```java
public class SubscriptionMainRequestDto {
    private String subsId;
    private String mainSubsYn;   // Y or N
    private String mainSubsId;   // nullable when mainSubsYn=Y
    private String createdBy;
    // Getters/Setters
}
```

**SubscriptionMainResponseDto.java** (단건 저장 응답)
```java
public class SubscriptionMainResponseDto {
    private String subsMainId;
    private String subsId;
    private String mainSubsYn;
    private String mainSubsId;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;
    // Getters/Setters
}
```

**SubscriptionMainListResponseDto.java** (목록 행)
```java
public class SubscriptionMainListResponseDto {
    private String subsId;
    private String svcNm;
    private String feeProdNm;
    private String mainSubsYn;   // null → "N" 처리 (서비스에서)
    private String mainSubsId;
    // Getters/Setters/AllArgsConstructor
}
```

---

### Step 4: SubscriptionMainServiceImpl.java

```java
@Service
public class SubscriptionMainServiceImpl implements SubscriptionMainService {

    private static final LocalDateTime MAX_DT = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    private static final DateTimeFormatter ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final SubscriptionMainRepository repository;
    private final SubscriptionRepository subscriptionRepository;

    // 생성자 주입

    @Override
    public List<SubscriptionMainListResponseDto> findList(String svcNm, String searchType, String keyword) {
        LocalDateTime now = LocalDateTime.now();
        List<Object[]> rows = repository.findListRaw(
            (svcNm == null || svcNm.isEmpty()) ? null : svcNm,
            searchType,
            keyword,
            now   // ✅ now 파라미터 주입 (findActiveBySubsId와 방식 통일)
        );
        return rows.stream().map(r -> {
            SubscriptionMainListResponseDto dto = new SubscriptionMainListResponseDto();
            dto.setSubsId((String) r[0]);
            dto.setSvcNm((String) r[1]);
            dto.setFeeProdNm((String) r[2]);
            dto.setMainSubsYn(r[3] != null ? (String) r[3] : "N");
            dto.setMainSubsId((String) r[4]);
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public SubscriptionMainResponseDto save(SubscriptionMainRequestDto dto) {
        LocalDateTime now = LocalDateTime.now();

        // [유효성] 대표가입여부=N이면 대표가입ID 필수
        if ("N".equals(dto.getMainSubsYn())) {
            if (dto.getMainSubsId() == null || dto.getMainSubsId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대표가입ID를 입력해 주세요.");
            }
            // [유효성] 대표가입ID 실존 확인
            if (!subscriptionRepository.existsById(dto.getMainSubsId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "존재하지 않는 가입ID입니다: " + dto.getMainSubsId());
            }
        }

        // [기존 유효 레코드 종료]
        repository.findActiveBySubsId(dto.getSubsId(), now).ifPresent(existing -> {
            existing.setEffEndDt(now);
            existing.setUpdatedBy(dto.getCreatedBy());
            existing.setUpdatedDt(now);
            repository.save(existing);
        });

        // [신규 INSERT]
        SubscriptionMain sm = new SubscriptionMain();
        sm.setSubsMainId(generateId());
        sm.setSubsId(dto.getSubsId());
        sm.setMainSubsYn(dto.getMainSubsYn());
        sm.setMainSubsId("Y".equals(dto.getMainSubsYn()) ? null : dto.getMainSubsId());
        sm.setEffStartDt(now);
        sm.setEffEndDt(MAX_DT);
        sm.setCreatedBy(dto.getCreatedBy());
        sm.setCreatedDt(now);

        return toDto(repository.save(sm));
    }

    private String generateId() {
        return "SM" + LocalDateTime.now().format(ID_FORMATTER);
    }
    // toDto() 내부 변환 메서드
}
```

---

### Step 5: SubscriptionMainController.java

```java
@RestController
@RequestMapping("/api/subscription-main")
public class SubscriptionMainController {

    private final SubscriptionMainService service;

    public SubscriptionMainController(SubscriptionMainService service) {
        this.service = service;
    }

    // GET /api/subscription-main?svcNm=서비스1&searchType=가입ID&keyword=SUB
    @GetMapping
    public List<SubscriptionMainListResponseDto> getList(
        @RequestParam(required = false) String svcNm,
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String keyword) {
        return service.findList(svcNm, searchType, keyword);
    }

    @PostMapping
    public ResponseEntity<SubscriptionMainResponseDto> save(
        @RequestBody SubscriptionMainRequestDto dto) {
        return ResponseEntity.status(201).body(service.save(dto));
    }
}
```

> **설계 결정**: 가입검색 팝업의 가입 LIKE 검색은 기존 `/api/subscriptions?type=SUBS_ID&keyword=...` 엔드포인트 재사용.

---

### Step 6: SubscriptionServiceImpl.java 수정

```java
@Override
public void delete(String subsId) {
    findOrThrow(subsId);
    long billStdCount = billStdRepository.countBySubsId(subsId);
    if (billStdCount > 0) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "과금기준이 존재하는 가입은 삭제할 수 없습니다.");
    }
    if (subscriptionMainRepository.existsBySubsId(subsId)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "대표가입정보가 존재하는 가입은 삭제할 수 없습니다.");
    }
    repository.deleteById(subsId);
}
```

> 생성자에 `SubscriptionMainRepository subscriptionMainRepository` 의존성 추가 필요.

---

### Step 7: 프론트엔드

#### 7-1. api/subscriptionMainApi.js
```js
import apiClient from './apiClient'

export const getSubscriptionMainList = (params) =>
  apiClient.get('/subscription-main', { params }).then(r => r.data)

export const saveSubscriptionMain = (data) =>
  apiClient.post('/subscription-main', data).then(r => r.data)
```

#### 7-2. hooks/useSubscriptionMain.js
```js
import { useState, useCallback } from 'react'
import { getSubscriptionMainList, saveSubscriptionMain } from '../api/subscriptionMainApi'

export default function useSubscriptionMain() {
  const [isLoading, setIsLoading] = useState(false)

  const fetchList = useCallback(async (params) => {
    setIsLoading(true)
    try { return await getSubscriptionMainList(params) }
    finally { setIsLoading(false) }
  }, [])

  const handleSave = (data) => saveSubscriptionMain(data)

  return { isLoading, fetchList, handleSave }
}
```

#### 7-3. components/common/SubscriptionSearchPopup.jsx (재사용 팝업)
- props: `{ isOpen, onClose, onSelect }`
- 내부 상태: `keyword`, `results`
- 기존 `/api/subscriptions?type=SUBS_ID&keyword=...` 호출
- 목록 행 클릭 시 `onSelect(subsId)` 호출 후 `onClose()`

#### 7-4. components/subscription-main/ (4개 컴포넌트)

**SubscriptionMainSearchBar.jsx**
- props: `{ svcNm, onSvcNmChange, searchType, onSearchTypeChange, keyword, onKeywordChange, onSearch, isLoading }`
- 서비스 콤보: `['전체', 'IDC 전력', 'IDC NW', '비즈넷']`
- 조회조건 필드: Enter 키 이벤트 → `onSearch()` 호출

**SubscriptionMainList.jsx**
- props: `{ items, selectedId, onRowClick }`
- 컬럼: 가입ID / 서비스 / 상품 / 대표가입여부 / 대표가입ID
- 행 높이: `h-7`, 텍스트: `text-sm`
- 선택 행 강조: 파란색 배경

**SubscriptionMainForm.jsx**
- props: `{ data, onChange, onOpenPopup }`
- 가입ID, 유효시작일시, 유효종료일시: 읽기전용
- 대표가입여부: Y/N 콤보 (default Y)
- 대표가입ID: `mainSubsYn === 'N'` 일 때만 활성화, 검색 버튼 포함
- onChange 시 `mainSubsYn === 'Y'` → `mainSubsId = ''` 자동 클리어

**SubscriptionMainActionBar.jsx**
- props: `{ onSave }`
- `fixed bottom-0` 플로팅 바, 저장 버튼 1개

#### 7-5. pages/SubscriptionMainPage.jsx

서비스 콤보 → DB 매핑 상수:
```js
const SVC_MAP = { 'IDC 전력': '서비스1', 'IDC NW': '서비스2', '비즈넷': '서비스3' }
```

조회 유효성 로직 (검토 1 반영):
```js
const handleSearch = async () => {
  // ✅ 조회조건 항상 필수
  if (!keyword.trim()) {
    setSearchError('조회조건을 입력해 주세요.')
    return
  }
  // ✅ 2자 이상 필수
  if (keyword.trim().length < 2) {
    setSearchError('조회조건은 2자 이상 입력해 주세요.')
    return
  }
  const params = {
    svcNm: svcNm !== '전체' ? SVC_MAP[svcNm] : undefined,
    searchType: searchType || undefined,
    keyword: keyword.trim(),
  }
  const result = await fetchList(params)
  setItems(result)
}
```

#### 7-6. App.jsx 수정

```jsx
import SubscriptionMainPage from './pages/SubscriptionMainPage'
// ...
<Route path="/subscription-main" element={<ProtectedRoute><SubscriptionMainPage /></ProtectedRoute>} />
```

#### 7-7. Sidebar.jsx 수정

```js
{ label: '과금기준',     to: '/bill-std' },
{ label: '대표가입 관리', to: '/subscription-main' },  // 추가
{ label: '특수가입관리', to: null },
```

---

### Step 8: 레이아웃 통일 (3개 페이지)

```jsx
// Before
import Layout from '../components/common/Layout'
return <Layout maxWidth="max-w-6xl"> ... </Layout>

// After
import MainLayout from '../components/common/MainLayout'
return <MainLayout> ... </MainLayout>
```

대상: `BillStdPage.jsx`, `SubscriptionPage.jsx`, `StudyLogPage.jsx`

---

## 5. 트레이드오프 기록

| 결정 | 선택한 방안 | 대안 | 이유 |
|---|---|---|---|
| 목록 LEFT JOIN 쿼리 | Native Query + ON절 날짜 조건 | JPQL / WHERE절 | @ManyToOne 없음. WHERE절 배치 시 INNER JOIN 효과 발생 |
| 현재시각 참조 | 두 쿼리 모두 `:now` 파라미터 주입 | CURRENT_TIMESTAMP | 트랜잭션 내 시각 일관성 보장 |
| 조회조건 유효성 | 프론트 단에서만 처리 | 백엔드 400 반환 | API 직접 호출 유연성 유지 |
| LIKE 패턴 | `keyword%` (전방 일치) | `%keyword%` | 요구사항: 뒤에만 와일드카드 |
| 가입검색 팝업 API | 기존 `/api/subscriptions` 재사용 | 신규 endpoint | 중복 불필요 |
| 저장 endpoint | POST 단일 (신규/변경 통합) | PUT 분리 | 저장 로직이 항상 "종료+신규" 패턴으로 동일 |
| PK 채번 | SM + LocalDateTime (밀리초) | UUID | 기존 BS 패턴과 일관성 |

---

## 6. 테스트 계획

### 6.1 백엔드 시나리오

```
1. GET /api/subscription-main?searchType=가입ID&keyword=SU         → 전방일치 LIKE 확인
2. GET /api/subscription-main?svcNm=서비스1&searchType=가입ID&keyword=SU → 서비스 필터 + LIKE 복합
3. GET /api/subscription-main?searchType=대표가입ID&keyword=SM     → 대표가입 없는 가입 미포함 확인
4. POST /api/subscription-main (mainSubsYn=Y)                      → 유효 레코드 INSERT 확인
5. POST /api/subscription-main (mainSubsYn=Y) 재저장              → 기존 종료 + 신규 INSERT 확인
6. POST /api/subscription-main (mainSubsYn=N, mainSubsId 없음)    → 400 반환 확인
7. POST /api/subscription-main (mainSubsYn=N, mainSubsId=존재X)   → 400 반환 확인
8. DELETE /api/subscriptions/{subsId} (SM 있음)                   → 409 반환 확인
```

### 6.2 프론트엔드 시나리오

```
1. LNB "대표가입 관리" 클릭 → /subscription-main 이동
2. 조회조건 없이 조회 → "조회조건을 입력해 주세요." 안내
3. 조회조건 1자 입력 후 조회 → "2자 이상 입력해 주세요." 안내
4. 조회조건 2자 이상 + 전체 서비스 → 조건 기준 전체 목록 표시
5. 목록 행 클릭 → Form 영역에 데이터 표시
6. 대표가입여부 N 선택 → 대표가입ID 필드 활성화
7. 대표가입여부 Y 선택 → 대표가입ID 비활성화 + 초기화
8. 대표가입검색 버튼 → 팝업 오픈 → 검색 → 행 클릭 → 대표가입ID 셋팅 → 팝업 닫힘
9. 저장 버튼 → 성공 Toast 표시
10. SubscriptionPage / BillStdPage / StudyLogPage → MainLayout 표시 확인
```

---

## 7. 롤백 방안

| 단계 | 롤백 방법 |
|---|---|
| 백엔드 Entity 추가 | 파일 삭제 후 재시작 (테이블은 잔류하나 앱에 영향 없음) |
| SubscriptionServiceImpl 수정 | Git으로 해당 파일 revert |
| 프론트엔드 전체 | Git으로 변경 파일 revert |
| DB 데이터 | `data/vibedb.mv.db` 파일 교체 (Git 추적 중) |
