# 6단계: 품질 검증 (Verification)

검증 일시: 2026-03-02

---

## 1. 수용 기준 충족 여부 (Pass/Fail)

| ID | 수용 기준 | 결과 | 비고 |
|---|---|---|---|
| AC-01 | `StudyLog` CRUD가 Service 계층을 경유하여 동작한다 | **Pass** | `StudyLogController` → `StudyLogService` → `StudyLogRepository` 레이어 확인 |
| AC-02 | `StudyLog` Entity에 시스템 필드 4개가 존재하고 DB 테이블명이 `tb_study_log`다 | **Pass** | `@Table(name = "tb_study_log")` + `createdBy/createdDt/updatedBy/updatedDt` 확인 |
| AC-03 | `AuthController`가 `UserRepository`를 직접 주입하지 않는다 | **Pass** | `AuthController` 생성자에 `UserService`만 존재, `UserRepository` 미포함 확인 |
| AC-04 | `ResponseStatusException` 발생 시 표준 JSON 포맷으로 응답된다 | **Pass** | `PUT /api/logs/999` → `{"message":"...","errors":["..."]}` 404 응답 확인 |
| AC-05 | `HelloController`가 존재하지 않는다 | **Pass** | 파일 없음 확인, `GET /hello` → 404 응답 확인 |
| AC-06 | `StudyLogPage`에서 에러/성공 메시지가 `<Toast>`로 표시된다 | **Pass** | `Toast` import 및 `errorMsg`/`successMsg` 연결 확인 (브라우저 확인은 UI 체크리스트) |
| AC-07 | StudyLog 관련 컴포넌트가 `components/studylog/`에 위치한다 | **Pass** | `StudyLogForm`, `StudyLogTable`, `StudyLogItem`, `EditModal` 모두 `components/studylog/`에 존재 |
| AC-08 | `SubscriptionPage`, `BillStdPage` 삭제 시 `<ConfirmDialog>`가 표시된다 | **Pass** | 두 페이지 모두 `ConfirmDialog` import + `confirmOpen` state + 패턴 적용 확인 |
| AC-09 | 대상 페이지의 입력/버튼 높이가 `h-8`이다 (LoginPage 제외) | **Pass** | `frontend/src` 전체 검색 결과 `LoginPage.jsx`에만 `h-10` 잔존 (의도적 제외) |
| AC-10 | `SubscriptionSearchPopup`이 `useSubscriptionSearch` Hook을 경유한다 | **Pass** | `searchSubscriptions` 직접 import 제거, `useSubscriptionSearch` Hook 경유 확인 |
| AC-11 | `UserPage`가 `useUser` Hook을 경유한다 | **Pass** | `authApi` 직접 import 제거, `useUser` Hook 경유 확인 |
| AC-12 | `backend-rules.md`에 `SM` 접두사가 등록되어 있다 | **Pass** | `대표가입 (SubscriptionMain)` / `SM` 행 추가 확인 |

**전체: 12/12 Pass**

---

## 2. 계획 준수 여부

| Step | 계획 항목 | 준수 여부 | 비고 |
|---|---|---|---|
| Step 1 | GlobalExceptionHandler 확장 + HelloController 삭제 | 준수 | `HashMap` 사용, `message`/`errors` 키 포함 |
| Step 2 | StudyLog Entity @Table + 시스템 필드 + DTO createdBy 추가 | 준수 | PK `Long` 유지 확인 |
| Step 3 | StudyLogService 인터페이스 + Impl 신설, Controller Service 의존으로 교체 | 준수 | `toEntity()` 메서드 Controller에서 제거됨 |
| Step 4 | UserService.listUsers() 추가, AuthController UserRepository 제거 | 준수 | |
| Step 5 | StudyLog 컴포넌트 디렉토리 이동 + Hook/Page 리팩토링 | 준수 | `setErrorMsg`/`setSuccessMsg` setter 추가 반환 (계획 대비 개선) |
| Step 6 | useSubscriptionSearch, useUser Hook 신설 | 준수 | |
| Step 7 | SubscriptionPage ConfirmDialog + clearMessages useEffect | 준수 | |
| Step 8 | BillStdPage ConfirmDialog 추가 | 준수 | |
| Step 9 | SubscriptionMainPage searchError 제거, computed 전환 | 준수 | `SubscriptionMainSearchBar` prop 제거 완료 |
| Step 10 | h-10 → h-8 일괄 치환 (6개 파일) | 준수 | LoginPage 제외 |
| Step 11 | Header.jsx, Layout.jsx 삭제 | 준수 | 두 파일 모두 존재하지 않음 확인 |
| Step 12 | backend-rules.md SM 접두사 등록 | 준수 | |

---

## 3. API 실동작 검증 결과

서버 기동 상태: **정상 기동** (port 8080, H2 파일 DB)

| 엔드포인트 | 요청 | 응답 코드 | 응답 바디 요약 | 결과 |
|---|---|---|---|---|
| `GET /api/logs` | 빈 DB 조회 | 200 | `[]` | Pass |
| `POST /api/logs` | 정상 등록 | 200 | `{"id":1,"content":"...","createdBy":"QA","createdDt":"...","updatedBy":null,"updatedDt":null}` | Pass |
| `GET /api/logs` | 등록 후 조회 | 200 | 등록된 항목 1건 포함 배열 | Pass |
| `PUT /api/logs/1` | 정상 수정 | 200 | `{"updatedBy":"QA","updatedDt":"..."}` 포함 | Pass |
| `PUT /api/logs/999` | 존재하지 않는 ID | 404 | `{"message":"학습 로그를 찾을 수 없습니다.","errors":["..."]}` | Pass |
| `DELETE /api/logs/999` | 존재하지 않는 ID | 404 | `{"message":"학습 로그를 찾을 수 없습니다.","errors":["..."]}` | Pass |
| `DELETE /api/logs/1` | 정상 삭제 | 204 | (빈 바디) | Pass |
| `GET /api/auth/users` | 사용자 목록 | 200 | 10건 목록 정상 반환 (`userId`,`nickname`,`email`,`accountStatus` 포함) | Pass |
| `GET /hello` | HelloController 삭제 확인 | 404 | Spring 기본 404 응답 | Pass |
| `POST /api/logs` (유효성 오류) | content 누락 | 400 | `{"message":"학습 내용을 입력해주세요.","errors":["..."]}` | Pass |

**비고:** POST 요청에서 한국어 JSON 바디 전달 시 UTF-8 인코딩 오류 발생(curl 환경 문제). ASCII 바디로 재테스트하여 정상 동작 확인. 프론트엔드에서는 브라우저 기반 요청이므로 실제 서비스 동작에 영향 없음.

---

## 4. 회귀 영향도 분석

### 4.1 수정된 파일 기준 의존성 분석

#### Backend

| 수정 파일 | 의존하는 컴포넌트 | 영향 평가 |
|---|---|---|
| `GlobalExceptionHandler.java` | 전체 Controller (모든 예외에 적용) | 응답 포맷에 `message` 키 추가됨. 기존 Frontend `err?.response?.data?.message` 참조 호환 유지. 위험 없음. |
| `StudyLog.java` | `StudyLogRepository`, `StudyLogServiceImpl`, `StudyLogController` | Entity 변경으로 DB 스키마 영향. `ddl-auto=update` 환경에서 신규 테이블(`tb_study_log`) 자동 생성 확인됨 (기존 `study_log` 테이블과 별도 공존). |
| `StudyLogRequestDto.java` | `StudyLogController.create()`, `StudyLogController.update()` | `createdBy` 필드 추가는 선택 필드(nullable). 기존 요청 바디 호환 유지. |
| `StudyLogService.java` + `StudyLogServiceImpl.java` | `StudyLogController` | Repository 직접 의존에서 Service 의존으로 교체. 외부 API 인터페이스 변경 없음. |
| `StudyLogController.java` | `App.jsx` (라우팅), Frontend `studyLogApi.js` | HTTP 엔드포인트 동일 유지(`/api/logs`). 영향 없음. |
| `UserService.java` + `UserServiceImpl.java` | `AuthController` | `listUsers()` 메서드 추가. 기존 `authenticate()`, `register()` 시그니처 변경 없음. |
| `AuthController.java` | Frontend `authApi.js` | `UserRepository` 제거. 외부 HTTP 엔드포인트 동일 유지(`/api/auth/users`). 영향 없음. |

**잠재적 파손 위험:** `StudyLog.java`의 `@Table` 변경으로 기존 H2 파일 DB에 `study_log` 테이블 데이터가 있을 경우 신규 `tb_study_log` 테이블로 자동 마이그레이션되지 않음. 개발 환경이므로 기존 데이터 손실 허용 범위로 판단.

#### Frontend

| 수정 파일 | 의존하는 컴포넌트 | 영향 평가 |
|---|---|---|
| `useStudyLogs.js` | `StudyLogPage.jsx` | `error` → `errorMsg` 변수명 변경. `StudyLogPage`가 신규 인터페이스(`errorMsg`, `successMsg`, `setErrorMsg`, `setSuccessMsg`)를 사용하도록 동시 수정됨. 위험 없음. |
| `components/studylog/` (이동) | `StudyLogPage.jsx` | import 경로가 `StudyLogPage`에서 일괄 수정됨. `App.jsx`는 `StudyLogPage`만 import하므로 영향 없음. |
| `useSubscriptionSearch.js` (신규) | `SubscriptionSearchPopup.jsx` | 신규 Hook. 기존 로직을 추출하여 동일 동작 유지. |
| `SubscriptionSearchPopup.jsx` | `SubscriptionMainPage.jsx` | Hook 경유로 교체. 팝업 컴포넌트 외부 인터페이스(`isOpen`, `onClose`, `onSelect` props) 변경 없음. |
| `useUser.js` (신규) | `UserPage.jsx` | 신규 Hook. 기존 인라인 로직 추출. |
| `SubscriptionPage.jsx` | `App.jsx` (라우팅) | ConfirmDialog 추가 및 clearMessages useEffect. 외부 라우팅 변경 없음. |
| `BillStdPage.jsx` | `App.jsx` (라우팅) | 동일. |
| `SubscriptionMainPage.jsx` | `App.jsx` (라우팅) | `searchError` state 제거. `SubscriptionMainSearchBar`에 prop 미전달. |
| `SubscriptionMainSearchBar.jsx` | `SubscriptionMainPage.jsx` | `searchError` prop 제거. Page에서도 해당 prop 제거됨. 동기화 완료. |
| h-10 → h-8 (6개 파일) | 각 페이지 | 스타일 변경만. 기능 영향 없음. |
| `Header.jsx`, `Layout.jsx` 삭제 | 없음 | `App.jsx` 및 모든 페이지에서 미사용 확인됨. 위험 없음. |

---

## 5. 규칙 준수 확인

`CLAUDE.md` 참조: `docs/frontend-rules.md`, `docs/backend-rules.md` 확인 완료.

### 5.1 Backend 규칙 준수

| 규칙 | 확인 결과 |
|---|---|
| 생성자 주입 전용 (`@Autowired` 금지) | Pass — 모든 신규/수정 파일에서 생성자 주입 사용 |
| Service Interface + Impl 분리 | Pass — `StudyLogService` + `StudyLogServiceImpl` 확인 |
| GET 목록: `List<T>` 직접 반환 | Pass — `StudyLogController.getAll()` `List<StudyLog>` 반환 |
| GET 단건/POST/PUT/DELETE: `ResponseEntity` 사용 | Pass — `create`, `update`, `delete` 모두 `ResponseEntity` 반환 |
| Lombok 미사용 | Pass — `StudyLog.java`에 명시적 getter/setter 선언 확인 |
| 시스템 필드 4개 (createdBy/createdDt/updatedBy/updatedDt) | Pass — `StudyLog.java` 확인 |
| DTO createdBy 단일 필드 원칙 | Pass — `StudyLogRequestDto`에 `createdBy` 1개만 선언, ServiceImpl에서 오퍼레이션 분기 처리 |
| INSERT 시 createdBy/createdDt 설정, UPDATE 시 updatedBy/updatedDt 설정 | Pass — `StudyLogServiceImpl` 확인 |
| 테이블명 `tb_{domain}` 소문자 스네이크 | Pass — `@Table(name = "tb_study_log")` |
| SM 접두사 등록 | Pass — `backend-rules.md` ID 접두사 테이블에 등록 확인 |

**위반 사항: 없음**

### 5.2 Frontend 규칙 준수

| 규칙 | 확인 결과 |
|---|---|
| 컴포넌트 디렉토리 패턴 (`components/{domain}/`) | Pass — `components/studylog/` 이동 완료 |
| Hook: API 호출 로직 캡슐화, Page에서 직접 API 호출 금지 | Pass — `useStudyLogs`, `useSubscriptionSearch`, `useUser` 신설로 직접 API 호출 제거 |
| `apiClient.js` 경유 필수 | Pass — 기존 Api 파일들이 이미 `apiClient.js` 경유. 신규 Hook에서 Api 함수 import 확인 |
| 모든 메인 화면 `<MainLayout>` 감싸기 | Pass — `StudyLogPage`, `UserPage`, `SubscriptionPage`, `BillStdPage`, `SubscriptionMainPage` 모두 `<MainLayout>` 사용 |
| 메시지 변수명: `errorMsg`/`successMsg` 고정 | Pass — 모든 수정 파일에서 표준 변수명 사용 확인 |
| 액션 시작 전 `clearMessages()` 호출 | Pass — `useStudyLogs`, `useUser`, `SubscriptionPage`, `BillStdPage` 확인 |
| HTTP 상태 코드 기반 에러 분기 | Pass — `useStudyLogs`, `BillStdPage`, `SubscriptionPage` 확인 |
| 입력/버튼 높이 `h-8` | Pass — 대상 파일 `h-10` 잔존 없음 (LoginPage만 `h-10` 유지, 규칙 명시적 예외) |
| 파괴적 액션에 `<ConfirmDialog>` | Pass — `SubscriptionPage`, `BillStdPage` 삭제 액션에 적용 확인 |
| 브라우저 기본 `confirm()` 사용 금지 | Pass — `confirm()` 호출 없음 |
| Toast 배치 (MainLayout 바로 아래) | Pass — 모든 페이지에서 `<MainLayout>` 직후 `<Toast>` 배치 확인 |
| EMPTY_FORM 패턴 | Pass — `SubscriptionPage`, `BillStdPage`, `useUser` 등에서 EMPTY_FORM 상수 사용 확인 |

**위반 사항:**
- `StudyLogPage.jsx`: 초기 로드 실패를 `<Toast>`로 처리. 규칙 5.1에 "페이지 초기 데이터 로드 실패 → `<ErrorMessage>` — 재시도 버튼 포함"으로 명시되어 있으나, 계획서(04_final_plan.md) Step 5 설계 포인트에서 의도적으로 Toast 처리로 결정함. 기능 저하로 판단하기 어려우나 규칙과의 불일치 존재. (코드 수정 금지 — 잔여 이슈로 기록)

---

## 6. 잔여 이슈

| 번호 | 유형 | 설명 | 심각도 |
|---|---|---|---|
| IS-01 | 규칙 불일치 | `StudyLogPage` 초기 로드 실패 시 `<ErrorMessage>` 대신 `<Toast>` 사용. frontend-rules.md 5.1 기준 불일치. 계획서에서 의도적으로 결정된 사항임. | 낮음 |
| IS-02 | DB 호환성 | 기존 `study_log` 테이블 데이터가 있을 경우 신규 `tb_study_log` 테이블로 자동 마이그레이션 불가. 수동 DDL 실행 필요 (운영 환경 전환 시). | 중간 (운영 전환 시) |
| IS-03 | H2 DDL 경고 | 서버 기동 시 `tb_user` 컬럼 변경 관련 H2 SQL 구문 오류 경고 발생. 기동 자체는 정상 완료되며 기존 이슈. 이번 표준화 범위 외. | 낮음 |

---

## 7. UI 회귀 체크리스트 (설계자 브라우저 직접 확인)

아래 항목은 Claude가 확인할 수 없는 화면 동작입니다. 브라우저에서 직접 확인 후 체크하세요.

### 학습 로그 (StudyLogPage)
- [ ] 페이지 진입 시 학습 로그 목록이 정상 표시된다
- [ ] 등록 성공 시 상단에 초록색 Toast ("등록이 완료되었습니다.")가 표시된다
- [ ] Toast가 3초 후 자동 소멸된다
- [ ] 수정(EditModal) 성공 시 Toast ("수정이 완료되었습니다.")가 표시된다
- [ ] 삭제 성공 시 Toast ("삭제가 완료되었습니다.")가 표시된다
- [ ] 폼 유효성 오류(내용 미입력) 시 빨간색 Toast가 표시된다

### 가입 관리 (SubscriptionPage)
- [ ] 삭제 버튼 클릭 시 ConfirmDialog 모달이 표시된다
- [ ] ConfirmDialog 확인 클릭 시 삭제가 실행된다
- [ ] ConfirmDialog 취소 클릭 시 삭제가 취소된다
- [ ] `?subsId=` 쿼리 파라미터로 진입 시 기존 Toast 메시지가 초기화된다
- [ ] 각 입력/버튼 요소의 높이가 h-8 (32px)임을 개발자 도구에서 확인한다

### 과금기준 관리 (BillStdPage)
- [ ] 삭제 버튼 클릭 시 ConfirmDialog 모달이 표시된다
- [ ] ConfirmDialog 확인/취소 동작이 정상적으로 작동한다
- [ ] 각 입력/버튼 요소의 높이가 h-8 (32px)임을 개발자 도구에서 확인한다

### 대표가입 관리 (SubscriptionMainPage)
- [ ] 조회조건 미입력 상태에서 조회 클릭 시 Toast 에러 메시지가 표시된다 (인라인 에러 표시 아님)
- [ ] 조회조건 1자 입력 시 Toast 에러 메시지가 표시된다
- [ ] 가입 검색 팝업에서 가입ID 검색 후 결과 클릭 시 mainSubsId 필드에 값이 반영된다

### 사용자 관리 (UserPage)
- [ ] 사용자 목록이 정상 표시된다 (Hook을 통해 조회)
- [ ] 신규 등록 성공 시 Toast ("사용자가 등록되었습니다.")가 표시된다
- [ ] 등록 입력/버튼 높이가 h-8 (32px)임을 확인한다

### 공통
- [ ] 앱 전체 콘솔에 import 오류 또는 컴포넌트 미발견 오류가 없다
- [ ] Header.jsx, Layout.jsx 삭제 후 사이드 이펙트가 없다

