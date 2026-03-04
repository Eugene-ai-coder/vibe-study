# 1단계: 코드베이스 분석 (Research)

## 1. 현행 구조 요약

### 1.1 프로젝트 기술 스택
- **Backend**: Spring Boot 3.4.3, Java 17, H2 File DB, JPA(`ddl-auto=update`)
- **Frontend**: React (Vite 5173) + TailwindCSS, axios via `apiClient.js`
- **인증**: Spring Security + 세션 기반, `SecurityUtils.getCurrentUserId()`로 현재 사용자 추출

### 1.2 백엔드 레이어 구조 (참조 패턴: BillStd)
```
Controller (@RestController, /api/{domain})
  └→ Service (Interface + Impl)
       └→ Repository (JpaRepository)
            └→ Entity (@Entity, @Table)
```
- **DTO 분리**: RequestDto (입력), ResponseDto (출력), 필요 시 ListResponseDto (목록용 축약 DTO)
- **ID 생성**: 접두사 + `yyyyMMddHHmmssSSS` 타임스탬프 (BillStd: `BS`, SubscriptionMain: `SM`)
- **시스템 필드**: `created_by`, `created_dt`, `updated_by`, `updated_dt` — 모든 테이블 필수
- **에러 처리**: `ResponseStatusException` → `GlobalExceptionHandler`가 표준 JSON `{message, errors}` 응답

### 1.3 프론트엔드 구조 (참조 패턴: SubscriptionMainPage — 목록+폼+액션바)
```
Page (상태 소유, 이벤트 조율)
  ├→ SearchBar (조회조건 UI)
  ├→ List (DataGrid 또는 직접 테이블)
  ├→ Form (입력/상세 표시)
  └→ ActionBar (FloatingActionBar 래핑)
```
- **Hook**: `use{Domain}.js` — API 호출 캡슐화, loading 상태 관리
- **API**: `{domain}Api.js` — `apiClient` 경유, `.then(r => r.data)` 패턴
- **상태**: `EMPTY_FORM` 상수 패턴, `errorMsg`/`successMsg` 고정 변수명
- **메뉴**: `Sidebar.jsx`의 `MENU` 배열에 라우트 등록
- **라우터**: `App.jsx`의 `<Routes>` + `<ProtectedRoute>` 래핑

## 2. 영향 범위

### 2.1 신규 생성 대상 (Backend — 7개 파일)
| 파일 | 역할 |
|---|---|
| `SpecialSubscription.java` | Entity (복합 PK: `subs_bill_std_id` + `eff_sta_dt`) |
| `SpecialSubscriptionId.java` | `@IdClass` 또는 `@EmbeddedId` 복합키 클래스 |
| `SpecialSubscriptionRepository.java` | JpaRepository |
| `SpecialSubscriptionService.java` | Service Interface |
| `SpecialSubscriptionServiceImpl.java` | Service 구현 |
| `SpecialSubscriptionController.java` | REST Controller |
| `SpecialSubscriptionRequestDto.java` | 요청 DTO |
| `SpecialSubscriptionResponseDto.java` | 응답 DTO |

### 2.2 신규 생성 대상 (Frontend — 7개 파일)
| 파일 | 역할 |
|---|---|
| `api/specialSubscriptionApi.js` | API 호출 모듈 |
| `hooks/useSpecialSubscription.js` | Hook |
| `pages/SpecialSubscriptionPage.jsx` | 페이지 (상태 소유) |
| `components/special-subscription/SpecialSubscriptionSearchBar.jsx` | 조회조건 |
| `components/special-subscription/SpecialSubscriptionList.jsx` | 목록 (DataGrid) |
| `components/special-subscription/SpecialSubscriptionForm.jsx` | 입력폼 |
| `components/special-subscription/SpecialSubscriptionActionBar.jsx` | 액션바 |

### 2.3 수정 대상 (기존 파일 — 3개)
| 파일 | 변경 내용 |
|---|---|
| `frontend/src/App.jsx` | `/special-subscription` 라우트 추가 |
| `frontend/src/components/main/Sidebar.jsx` | 특수가입관리 메뉴 `to` 값 활성화 (`null` → `'/special-subscription'`) |
| `src/main/resources/schema.sql` | `tb_special_subscription` DDL 추가 |

### 2.4 영향 받지 않는 영역
- 기존 Subscription, BillStd, SubscriptionMain, CommonCode, Qna 도메인 — 변경 없음
- SecurityConfig, GlobalExceptionHandler — 변경 없음 (`/api/**` authenticated 규칙으로 자동 커버)

## 3. 의존성 그래프

```
[특수가입 화면]
  │
  ├─► SpecialSubscriptionPage.jsx
  │     ├─► useSpecialSubscription.js (hook)
  │     │     └─► specialSubscriptionApi.js
  │     │           └─► apiClient.js (기존)
  │     ├─► SpecialSubscriptionSearchBar.jsx
  │     ├─► SpecialSubscriptionList.jsx
  │     │     └─► DataGrid.jsx (기존 공통 컴포넌트)
  │     ├─► SpecialSubscriptionForm.jsx
  │     │     └─► SubscriptionSearchPopup.jsx (가입ID 검색 — 기존 재사용 가능)
  │     └─► SpecialSubscriptionActionBar.jsx
  │           └─► FloatingActionBar.jsx (기존)
  │
  ├─► App.jsx (라우트 추가)
  └─► Sidebar.jsx (메뉴 활성화)

[Backend]
  SpecialSubscriptionController
    └─► SpecialSubscriptionService(Impl)
          └─► SpecialSubscriptionRepository
                └─► SpecialSubscription (Entity)
                      └─► SpecialSubscriptionId (복합키)
```

### 주요 외부 의존성
- `SecurityUtils.getCurrentUserId()` — created_by / updated_by 자동 설정
- `GlobalExceptionHandler` — 표준 에러 응답 처리
- `tb_subscription` (tb_bill_std 경유) — `subs_bill_std_id`가 과금기준ID를 참조할 경우 FK 관계 검토 필요

## 4. 기존 테스트 현황

- **단위/통합 테스트**: `VibeStudyApplicationTests.java` 1개만 존재 (Spring Boot 기본 컨텍스트 로드 테스트)
- **기존 도메인 테스트 없음**: Subscription, BillStd, SubscriptionMain 등 모두 테스트 미작성
- **결론**: 특수가입 기능도 별도 테스트 작성 불요 (기존 패턴 유지)

## 5. 기술적 제약사항

### 5.1 복합 PK 처리
- 요구사항: `subs_bill_std_id` + `eff_sta_dt` 복합 PK
- 기존 유사 패턴: `CommonDtlCode`가 `@EmbeddedId(CommonDtlCodeId)` 사용
  - `CommonDtlCodeId.java` — `@Embeddable`, `Serializable` 구현, `equals()`/`hashCode()` 오버라이드
  - Entity에서 `@EmbeddedId private CommonDtlCodeId id;` 선언
  - Repository: `JpaRepository<CommonDtlCode, CommonDtlCodeId>`
- **방식 결정**: `@EmbeddedId` 패턴 사용 (기존 프로젝트 패턴 준수)

### 5.2 `eff_sta_dt` 타입
- 요구사항: `VARCHAR(8)` — YYYYMMDD 문자열
- BillStd의 `eff_start_dt`는 `TIMESTAMP`를 사용 중 → 특수가입은 요구사항대로 `VARCHAR(8)`
- Java 측: `String` 타입으로 선언

### 5.3 기타 컬럼 결정
- 요구사항에 "(기타 컬럼은 리서치 단계에서 기존 유사 테이블 참고하여 결정)"이라 명시됨
- `tb_bill_std` 참조하여 특수가입에 적합한 컬럼 세트 도출 필요
- 과금 관련 핵심 필드들을 선별하여 포함: 서비스코드, 상태코드, 유효종료일, 비고 등

### 5.4 데이터 분류
- 특수가입은 설정성 데이터(건수 한정적) → **전건 목록 (페이징 없음)** 유형 적용
- BillStd와 동일한 패턴 (전건 조회 `List<T>` 반환)

### 5.5 DB
- H2 File DB, `ddl-auto=update` → JPA Entity 선언만으로 테이블 자동 생성
- `schema.sql`에 DDL 추가는 문서화 목적 (H2 재생성 시 보험)

### 5.6 Sidebar 메뉴
- 이미 `{ label: '특수가입관리', to: null }` 플레이스홀더 존재
- `to: null` → `to: '/special-subscription'`으로 변경만 필요

## 6. 리스크 식별

| # | 리스크 | 영향도 | 대응 |
|---|---|---|---|
| 1 | 복합 PK의 JPA 조회/수정/삭제 처리 복잡도 | 중 | `@IdClass` + `findById(new SpecialSubscriptionId(...))` 패턴으로 대응. CommonDtlCode 참조 |
| 2 | `subs_bill_std_id`와 `tb_bill_std.bill_std_id` 간 FK 무결성 | 중 | FK 선언 여부는 요구사항 확정 단계에서 결정. 없을 경우 Service 레벨에서 존재 여부 검증 |
| 3 | `eff_sta_dt` (VARCHAR(8))의 유효성 검증 | 저 | Service에서 YYYYMMDD 형식 검증 로직 추가 |
| 4 | 기타 컬럼 미확정 | 중 | 2단계(요구사항 확정)에서 설계자 승인 필요 |
| 5 | DataGrid 사용 시 복합 PK의 `rowIdAccessor` 처리 | 저 | 두 필드를 결합한 문자열 키 또는 row index 사용 |
