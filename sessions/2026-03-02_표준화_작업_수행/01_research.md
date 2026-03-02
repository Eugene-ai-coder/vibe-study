# 1단계: 코드베이스 분석 결과 (Research)

---

## 1. 현행 구조 요약

### 1.1 Backend 구조

**패키지 구조:** 단일 패키지 `com.example.vibestudy` — 모든 클래스가 flat하게 위치. 도메인별 하위 패키지 없음.

**도메인 목록:**

| 도메인 | Entity | Controller | Service(Interface) | ServiceImpl | Repository |
|---|---|---|---|---|---|
| Subscription (가입) | Subscription.java | SubscriptionController | SubscriptionService | SubscriptionServiceImpl | SubscriptionRepository |
| BillStd (과금기준) | BillStd.java | BillStdController | BillStdService | BillStdServiceImpl | BillStdRepository |
| SubscriptionMain (대표가입) | SubscriptionMain.java | SubscriptionMainController | SubscriptionMainService | SubscriptionMainServiceImpl | SubscriptionMainRepository |
| StudyLog (학습로그) | StudyLog.java | StudyLogController | 없음 (Service 계층 미존재) | 없음 | StudyLogRepository |
| User (사용자) | User.java | AuthController | UserService | UserServiceImpl | UserRepository |

**추가 인프라 클래스:**
- `GlobalExceptionHandler` — `@RestControllerAdvice`, `MethodArgumentNotValidException` 처리만 구현
- `CorsConfig` — CORS 설정
- `HelloController` — `/hello` 테스트 엔드포인트 (잔류 코드)
- `SubscriptionDataInitializer`, `UserDataInitializer` — 초기 데이터 투입용

### 1.2 Frontend 구조

**기술 스택:** React + Vite + Tailwind CSS, axios (apiClient.js 경유)

**디렉토리 구조:**
```
src/
  api/           - apiClient.js, authApi.js, billStdApi.js, subscriptionApi.js,
                   subscriptionMainApi.js, studyLogApi.js
  components/
    billstd/     - BillStdSearchBar, BillStdForm, BillStdActionBar
    common/      - ConfirmDialog, ErrorMessage, Header(미사용), Layout(미사용),
                   Loading, MainLayout, ProtectedRoute, SubscriptionSearchPopup, Toast
    main/        - DashboardContent, Sidebar
    subscription/ - SubscriptionActionBar, SubscriptionForm, SubscriptionList, SubscriptionSearchBar
    subscription-main/ - SubscriptionMainActionBar, SubscriptionMainForm,
                         SubscriptionMainList, SubscriptionMainSearchBar
    (루트)       - EditModal, StudyLogForm, StudyLogItem, StudyLogTable
  context/       - AuthContext.jsx
  hooks/         - useBillStd.js, useStudyLogs.js, useSubscription.js, useSubscriptionMain.js
  pages/         - BillStdPage, LoginPage, MainPage, StudyLogPage,
                   SubscriptionMainPage, SubscriptionPage, UserPage
```

---

## 2. 영향 범위 (표준 위반 목록)

### 2.1 Backend 표준 위반

#### [BE-01] StudyLog — Service 계층 미존재
- **위반:** `StudyLogController`가 `StudyLogRepository`를 직접 의존. `Controller → Repository` 직접 호출.
- **표준:** `Controller → Service → Repository` 레이어 준수 필요.
- **영향 파일:** `StudyLogController.java`, `StudyLog.java`, `StudyLogRepository.java`

#### [BE-02] StudyLog Entity — 시스템 필드 미존재
- **위반:** `StudyLog` Entity에 `createdBy`, `createdDt`, `updatedBy`, `updatedDt` 4개 시스템 필드 없음. `@GeneratedValue(strategy = GenerationType.IDENTITY)` 사용 (Long auto-increment PK).
- **표준:** 모든 Entity 필수 시스템 필드 4개 선언 요구. 테이블명도 `tb_study_log` 형식 필요 (`@Table` 어노테이션 없음 — JPA 기본명 사용 중).
- **영향 파일:** `StudyLog.java`

#### [BE-03] StudyLog Entity — `@Table` 어노테이션 및 DB 스키마 미준수
- **위반:** `@Table(name = "tb_study_log")` 없음. `id` 필드 타입 `Long` + `@GeneratedValue`. 타임스탬프 기반 ID 패턴 미적용.
- **표준:** `tb_{domain}` 소문자 스네이크 케이스, 타임스탬프 기반 ID.

#### [BE-04] AuthController — UserRepository 직접 주입
- **위반:** `AuthController`가 `UserService`와 `UserRepository` 두 가지를 동시에 의존. `listUsers()` 메서드에서 직접 `userRepository.findAll()` 호출.
- **표준:** Controller는 Service만 의존해야 함. Repository 직접 호출 금지.
- **영향 파일:** `AuthController.java`, `UserService.java`, `UserServiceImpl.java`

#### [BE-05] GlobalExceptionHandler — ResponseStatusException 처리 미구현
- **위반:** `GlobalExceptionHandler`가 `MethodArgumentNotValidException`만 처리. `ResponseStatusException`(서비스 계층에서 광범위 사용)은 Spring 기본 처리에 위임됨 — 응답 JSON 포맷이 표준 `{"errors": [...]}` 형식과 다름.
- **표준:** `GlobalExceptionHandler`를 통한 표준 JSON 응답 형식 준수.
- **영향 파일:** `GlobalExceptionHandler.java`

#### [BE-06] HelloController — 잔류 불필요 코드
- **위반:** `/hello` 테스트 엔드포인트가 프로덕션 코드에 잔류.
- **영향 파일:** `HelloController.java`

#### [BE-07] BillStdResponseDto — 미사용 (BillStdController.getAll() 미사용 확인 필요)
- **현황:** `BillStdController.getAll()` (`GET /api/bill-std`)은 전체 목록을 반환하나 Frontend에서 호출 없음 (`billStdApi.js`에 `getBillStds` 함수 선언은 있으나 hooks에서 미사용).

### 2.2 Frontend 표준 위반

#### [FE-01] StudyLogPage — Hook 위반 (상태 소유 위치 문제)
- **위반:** `StudyLogPage`가 `<MainLayout>` 내부 구조이지만, 에러/성공 메시지를 `errorMsg`/`successMsg` 변수명으로 관리하지 않고 hook 내부 `error` 상태를 `ErrorMessage` 컴포넌트로 직접 전달. `Toast` 컴포넌트 미사용.
- **표준:** 저장·변경·삭제·조회 성공/실패는 `<Toast>` 사용. 메시지 변수명 `errorMsg`/`successMsg` 고정.
- **영향 파일:** `StudyLogPage.jsx`, `useStudyLogs.js`

#### [FE-02] StudyLogItem — ConfirmDialog 준수 (이슈 없음)
- **현황:** `StudyLogItem.jsx`에서 `ConfirmDialog`를 올바르게 사용. 삭제 버튼 → `setConfirmOpen(true)` → ConfirmDialog에서 onConfirm/onCancel 처리. 표준 준수.
- **영향 파일:** 없음 (준수)

#### [FE-03] StudyLog 컴포넌트 — 도메인 디렉토리 미분리
- **위반:** `StudyLogForm.jsx`, `StudyLogTable.jsx`, `StudyLogItem.jsx`, `EditModal.jsx`가 `components/` 루트에 위치. `EditModal`은 도메인 특화 컴포넌트인데 `common/`이 아닌 루트에 존재.
- **표준:** 신규 기능: `components/{domain}/` 패턴 유지. StudyLog 관련 컴포넌트는 `components/studylog/`로 이동 필요.

#### [FE-04] SubscriptionPage — 삭제 시 ConfirmDialog 미사용
- **위반:** `SubscriptionPage`의 `onDelete` 함수가 즉시 `handleDelete` 실행. `ConfirmDialog` 없음.
- **표준:** 삭제 액션은 `<ConfirmDialog>` 필요.
- **영향 파일:** `SubscriptionPage.jsx`

#### [FE-05] BillStdPage — 삭제 시 ConfirmDialog 미사용
- **위반:** `BillStdPage`의 `handleDeleteClick`이 즉시 `handleDelete` 실행. `ConfirmDialog` 없음.
- **표준:** 삭제 액션은 `<ConfirmDialog>` 필요.
- **영향 파일:** `BillStdPage.jsx`

#### [FE-06] SubscriptionForm — 입력/버튼 높이 `h-10` 사용
- **위반:** `SubscriptionForm.jsx`의 모든 `input`/`select` 요소가 `h-10` 사용.
- **표준:** 입력/버튼 높이는 `h-8` (32px).
- **영향 파일:** `SubscriptionForm.jsx`

#### [FE-07] SubscriptionActionBar — 버튼 높이 `h-10` 사용
- **위반:** `SubscriptionActionBar.jsx`의 버튼들이 `h-10` 사용.
- **표준:** 버튼 높이 `h-8`.
- **영향 파일:** `SubscriptionActionBar.jsx`

#### [FE-08] BillStdForm — 입력 높이 `h-10` 사용
- **위반:** `BillStdForm.jsx`의 `Field` 컴포넌트 `inputClass`가 `h-10` 사용.
- **표준:** 입력 높이 `h-8`.
- **영향 파일:** `BillStdForm.jsx`

#### [FE-09] BillStdActionBar — 버튼 높이 `h-10` 사용
- **위반:** `BillStdActionBar.jsx`의 버튼들이 `h-10` 사용.
- **표준:** 버튼 높이 `h-8`.
- **영향 파일:** `BillStdActionBar.jsx`

#### [FE-10] UserPage — 입력/버튼 높이 `h-10` 사용
- **위반:** `UserPage.jsx`의 `input`과 `button`이 `h-10` 사용.
- **표준:** `h-8`.
- **영향 파일:** `UserPage.jsx`

#### [FE-11] LoginPage — 입력/버튼 높이 `h-10` 사용
- **위반:** `LoginPage.jsx`의 `input`/`button`이 `h-10` 사용. (로그인 페이지는 `<MainLayout>` 미사용 — 이 규칙은 준수.)
- **표준:** `h-8` (로그인 페이지도 동일 높이 기준 적용해야 하는지 별도 확인 필요 — 현재 규칙 문서상 로그인 페이지는 `<MainLayout>` 예외이나, 입력 높이 기준은 별도 명시 없음).

#### [FE-12] SubscriptionSearchBar — 입력/버튼 높이 `h-10` 사용
- **위반:** `h-10` 사용.
- **영향 파일:** `SubscriptionSearchBar.jsx`

#### [FE-13] BillStdSearchBar — 입력/버튼 높이 `h-10` 사용
- **위반:** `h-10` 사용.
- **영향 파일:** `BillStdSearchBar.jsx`

#### [FE-14] SubscriptionPage — `clearMessages` 미호출 케이스
- **현황:** `useEffect` 내 초기 조회 시 `clearMessages()` 미호출. 표준상 "액션 시작 전 항상 초기화" 요구.
- **영향 파일:** `SubscriptionPage.jsx`

#### [FE-15] SubscriptionMainPage — `searchError`와 `errorMsg` 이중 상태 사용
- **위반:** `searchError`(조회조건 오류용)와 `errorMsg`(저장 오류용) 두 상태를 분리 관리. `searchError`는 `<p>` 태그로 표시, `errorMsg`는 `<Toast>`로 표시.
- **표준:** `errorMsg` / `successMsg` 외 다른 명칭 금지. 조회조건 클라이언트 유효성 오류는 `<p className="text-sm text-red-600">` 사용 — 이 부분은 표준에 부합하나 변수명 `searchError`가 위반.
- **영향 파일:** `SubscriptionMainPage.jsx`

#### [FE-16] common/ 미사용 컴포넌트 잔류
- **현황:** `Header.jsx`, `Layout.jsx`가 `components/common/`에 존재하나 App.jsx 및 어느 페이지에서도 import 없음. 잔류 파일.

#### [FE-17] SubscriptionSearchPopup — apiClient 대신 subscriptionApi 직접 import
- **현황:** `SubscriptionSearchPopup.jsx`가 `../../api/subscriptionApi`를 직접 import. 이는 api 파일이 apiClient를 경유하므로 간접적으로는 준수. 단, 컴포넌트에서 직접 API 호출하는 구조 자체가 Hook 경유 원칙에 위배됨 (컴포넌트는 UI 렌더링 전담이어야 함).
- **영향 파일:** `SubscriptionSearchPopup.jsx`

---

## 3. 의존성 그래프

### 3.1 Backend 의존성

```
AuthController
  └── UserService (Interface)
        └── UserServiceImpl
              └── UserRepository
  └── UserRepository  ← [BE-04 위반: 직접 주입]

SubscriptionController
  └── SubscriptionService
        └── SubscriptionServiceImpl
              ├── SubscriptionRepository
              ├── BillStdRepository        ← 삭제 전 과금기준 존재 확인
              └── SubscriptionMainRepository ← 삭제 전 대표가입 존재 확인

BillStdController
  └── BillStdService
        └── BillStdServiceImpl
              └── BillStdRepository

SubscriptionMainController
  └── SubscriptionMainService
        └── SubscriptionMainServiceImpl
              ├── SubscriptionMainRepository
              └── SubscriptionRepository ← mainSubsId 존재 확인

StudyLogController  ← [BE-01 위반: Service 없음]
  └── StudyLogRepository

GlobalExceptionHandler
  └── MethodArgumentNotValidException 처리만 ← [BE-05 위반]
```

### 3.2 Frontend 의존성

```
App.jsx
  └── ProtectedRoute → AuthContext
  └── Pages: LoginPage, MainPage, UserPage, SubscriptionPage,
             BillStdPage, SubscriptionMainPage, StudyLogPage

SubscriptionPage
  └── useSubscription → subscriptionApi → apiClient
  └── Components: SubscriptionSearchBar, SubscriptionList, SubscriptionForm, SubscriptionActionBar

BillStdPage
  └── useBillStd → billStdApi → apiClient
  └── Components: BillStdSearchBar, BillStdForm, BillStdActionBar

SubscriptionMainPage
  └── useSubscriptionMain → subscriptionMainApi → apiClient
  └── SubscriptionSearchPopup → subscriptionApi → apiClient [FE-17: 컴포넌트 직접 호출]
  └── Components: SubscriptionMainSearchBar, SubscriptionMainList,
                  SubscriptionMainForm, SubscriptionMainActionBar

StudyLogPage
  └── useStudyLogs → studyLogApi → apiClient
  └── Components: StudyLogForm, StudyLogTable [→ StudyLogItem], EditModal

UserPage
  └── authApi 직접 import (getUsers, register) ← Hook 미사용
  └── Toast (공통)
```

---

## 4. 기존 테스트 현황

- **테스트 파일:** `src/test/java/com/example/vibestudy/VibeStudyApplicationTests.java` 1개만 존재.
- **내용:** `contextLoads()` — Spring Context 로딩 확인만 수행. 단위 테스트, 통합 테스트 전무.
- **결론:** 테스트 커버리지 0%. 리팩토링 시 테스트에 의한 안전망 없음.

---

## 5. 기술적 제약사항

### 5.1 StudyLog Entity 변경 시 DB 스키마 연동
- `StudyLog`는 `@Table` 어노테이션 없이 JPA 기본 테이블명 사용. 시스템 필드 추가 및 PK 타입 변경(`Long` → `String` 타임스탬프)은 DB 스키마(DDL) 변경을 수반함.
- H2 혹은 운영 DB의 기존 `study_log` 테이블 데이터가 있을 경우 마이그레이션 필요.

### 5.2 패키지 구조 flat 유지
- 현재 모든 Java 파일이 `com.example.vibestudy` 단일 패키지에 위치. 도메인별 하위 패키지 분리는 요구사항 문서(표준 규칙 문서)에 명시되지 않으므로, 현행 flat 구조 유지가 적절.

### 5.3 GlobalExceptionHandler 확장 시 ResponseStatusException 처리
- 현재 `ResponseStatusException`은 Spring 기본 에러 응답(`{"timestamp":...,"status":...,"message":...}`)으로 반환됨. Frontend 에러 처리 코드는 `err?.response?.status` 기반으로 분기하므로 상태코드 처리는 정상 작동.
- 단, `err?.response?.data?.message` 참조 시 응답 포맷이 Spring 기본값(`"message"` 키 포함)이어서 우연히 동작하고 있음. `GlobalExceptionHandler`에서 표준 포맷으로 통합 시 `"message"` 키 이름 유지 필요.

### 5.4 SubscriptionMain ID 접두사 미등록
- `backend-rules.md` ID 접두사 표에 `SubscriptionMain(대표가입)` 도메인이 누락되어 있으나, 코드에서는 `"SM"` 접두사 사용 중. 규칙 문서에는 등록되지 않은 상태.

---

## 6. 리스크 식별

| ID | 구분 | 리스크 내용 | 영향 범위 | 심각도 |
|---|---|---|---|---|
| R-01 | BE | StudyLog Service 계층 신설 시, StudyLog Entity PK 타입 변경(Long→String) 필요 — DB 스키마 변경 수반 | StudyLog 전체 + DB | 높음 |
| R-02 | BE | GlobalExceptionHandler에 ResponseStatusException 처리 추가 시, 기존 Frontend 에러 파싱 코드(`data.message`) 응답 포맷 호환 확인 필요 | 전체 에러 처리 경로 | 중간 |
| R-03 | FE | SubscriptionPage·BillStdPage에 ConfirmDialog 추가 시 기존 삭제 흐름 동작 변경 — QA 필요 | Subscription·BillStd 삭제 기능 | 중간 |
| R-04 | FE | h-10 → h-8 일괄 변경 시 레이아웃 밀도 변화 — 특히 SearchBar 행 높이 전체 영향 | 전체 UI | 낮음 |
| R-05 | FE | StudyLog 컴포넌트를 components/studylog/ 로 이동 시 import 경로 일괄 수정 필요 | StudyLogPage.jsx + 관련 import | 낮음 |
| R-06 | BE | HelloController 삭제 시 — 외부 연동 없음, 안전하게 삭제 가능 | HelloController.java | 없음 |
| R-07 | FE | Header.jsx, Layout.jsx 잔류 파일 삭제 시 — App.jsx에서 미사용 확인 완료, 안전하게 삭제 가능 | Header.jsx, Layout.jsx | 없음 |
| R-08 | BE | AuthController에서 UserRepository 직접 의존 제거 시 — UserService에 listUsers() 추가 필요 | UserService, UserServiceImpl, AuthController | 낮음 |
| R-09 | FE | UserPage — Hook 미사용 (authApi 직접 import). Hook 신설 시 파일 추가 필요 | UserPage.jsx | 낮음 |

---

## 7. 표준 준수 현황 요약

### 7.1 Backend — 표준 준수 항목

| 항목 | 상태 |
|---|---|
| Controller → Service → Repository 레이어 | Subscription/BillStd/SubscriptionMain 준수. StudyLog 미준수(R-01). |
| REST API @RestController 분리 | 준수 |
| 생성자 주입 | 전체 준수 |
| Service Interface + Impl 분리 | Subscription/BillStd/SubscriptionMain/User 준수. StudyLog 미구현. |
| Lombok 미사용 / 명시적 getter-setter | 전체 준수 |
| 시스템 필드 4개 | Subscription/BillStd/SubscriptionMain/User 준수. StudyLog 미준수. |
| DTO createdBy 단일 필드 원칙 | Subscription/BillStd/SubscriptionMain 준수. |
| toDto() ServiceImpl private 메서드 | BillStd/Subscription/SubscriptionMain 준수. |
| ID 자동생성 패턴 (타임스탬프) | BillStd(BS), SubscriptionMain(SM) 준수. Subscription은 수동입력(PK 수동). |
| DB 스키마 테이블명 `tb_{domain}` | Subscription/BillStd/SubscriptionMain/User 준수. StudyLog 미준수. |
| GlobalExceptionHandler 표준 응답 | 부분 준수 (Validation만, ResponseStatusException 미처리) |

### 7.2 Frontend — 표준 준수 항목

| 항목 | 상태 |
|---|---|
| Page/Component/Hook 역할 분리 | BillStd/Subscription/SubscriptionMain 준수. StudyLog 부분 위반(구조). UserPage Hook 미사용. |
| apiClient 경유 | 전체 준수 |
| MainLayout 적용 | 로그인 페이지 외 전체 준수 |
| errorMsg / successMsg 변수명 | BillStd/Subscription/SubscriptionMain/UserPage 준수. LoginPage는 `loading` 변수 혼용(표준과 무관). SubscriptionMainPage는 `searchError` 추가 사용(위반). |
| EMPTY_FORM 패턴 | BillStd/Subscription/SubscriptionMain/UserPage 준수. StudyLogPage 미사용(hook 내부 관리). |
| clearMessages 패턴 | BillStd/Subscription 준수. SubscriptionMainPage 일부 미준수. |
| HTTP 상태 코드 기반 에러 분기 | BillStd/Subscription 준수. SubscriptionMainPage `data.message` 일괄 처리(부분 위반). |
| Toast 배치 (MainLayout 바로 아래) | BillStd/Subscription/SubscriptionMain/UserPage 준수. StudyLogPage 미사용. |
| ConfirmDialog (삭제) | SubscriptionMainPage 해당 없음(삭제 없음). Subscription/BillStd 미사용(위반). StudyLog 미확인. |
| 입력/버튼 높이 h-8 | SubscriptionMainPage/SubscriptionSearchPopup 준수. Subscription/BillStd/UserPage/LoginPage h-10 위반. |
| 목록 행 높이 h-7 | SubscriptionMainList/SubscriptionSearchPopup 준수. SubscriptionList h-7 미사용(py-2 방식). |
| text-sm | 대부분 준수 |
| 색상 팔레트 (#F8FAFC, #2563EB) | MainLayout 배경 준수. 강조색 전체 준수. |
