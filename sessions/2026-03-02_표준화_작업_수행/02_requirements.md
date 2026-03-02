# 2단계: 요구사항 확정 (Requirements)

---

## 1. 기능 명세

### 1.1 Backend 표준화

| ID | 항목 | 내용 |
|---|---|---|
| BE-01 | StudyLog Service 계층 신설 | `StudyLogService` (Interface) + `StudyLogServiceImpl` 신설. Controller → Service → Repository 레이어 준수. |
| BE-02 | StudyLog Entity 시스템 필드 추가 | `createdBy`, `createdDt`, `updatedBy`, `updatedDt` 4개 필드 추가. **PK 타입 `Long` 유지** (타임스탬프 변환 제외). |
| BE-03 | StudyLog Entity @Table 추가 | `@Table(name = "tb_study_log")` 어노테이션 추가. |
| BE-04 | AuthController UserRepository 의존 제거 | `UserService`에 `listUsers()` 추가. `AuthController`의 `UserRepository` 직접 주입 제거. |
| BE-05 | GlobalExceptionHandler 확장 | `ResponseStatusException` 처리 추가. 응답 포맷: `{"errors": [...], "message": "..."}`. `"message"` 키 유지 (Frontend 호환). |
| BE-06 | HelloController 삭제 | `HelloController.java` 파일 삭제. |

### 1.2 Frontend 표준화

| ID | 항목 | 내용 |
|---|---|---|
| FE-01 | StudyLogPage 메시지 표준화 | `errorMsg`/`successMsg` 변수명 사용. `<Toast>` 컴포넌트 적용. `useStudyLogs` hook에서 `errorMsg`/`successMsg` 반환. |
| FE-03 | StudyLog 컴포넌트 디렉토리 이동 | `StudyLogForm`, `StudyLogTable`, `StudyLogItem`, `EditModal` → `components/studylog/`로 이동. import 경로 일괄 수정. |
| FE-04 | SubscriptionPage ConfirmDialog 추가 | 삭제 액션에 `<ConfirmDialog>` 적용. |
| FE-05 | BillStdPage ConfirmDialog 추가 | 삭제 액션에 `<ConfirmDialog>` 적용. |
| FE-06~13 | 입력/버튼 높이 h-8 통일 | `SubscriptionForm`, `SubscriptionActionBar`, `SubscriptionSearchBar`, `BillStdForm`, `BillStdActionBar`, `BillStdSearchBar`, `UserPage`의 `h-10` → `h-8`. **LoginPage 제외** (현행 `h-10` 유지). |
| FE-14 | SubscriptionPage clearMessages 수정 | `useEffect` 초기 조회 시 `clearMessages()` 호출 추가. |
| FE-15 | SubscriptionMainPage searchError 표준화 | `searchError` state를 인라인 유효성 검사(computed)로 전환. `errorMsg` 단일 사용으로 정리. |
| FE-16 | 미사용 컴포넌트 삭제 | `components/common/Header.jsx`, `Layout.jsx` 삭제. |
| FE-17 | SubscriptionSearchPopup Hook 경유 | `useSubscriptionSearch` Hook 신설. 팝업 컴포넌트에서 직접 API 호출 제거. |
| FE-18 | UserPage Hook 신설 | `useUser` Hook 신설 (`getUsers`, `register` 포함). `UserPage`에서 `authApi` 직접 import 제거. |

### 1.3 표준 문서 수정

| ID | 항목 | 내용 |
|---|---|---|
| DOC-01 | backend-rules.md ID 접두사 등록 | `SubscriptionMain(대표가입)` 도메인 ID 접두사 `SM` 등록. |

---

## 2. 비기능 요건

- **DB 스키마 변경 최소화:** StudyLog PK `Long` 유지로 기존 `study_log` 테이블 데이터 무결성 유지.
- **Frontend 에러 처리 호환:** `GlobalExceptionHandler` 변경 후 `"message"` 키 유지 → 기존 Frontend 코드 수정 없음.
- **신규 기능 없음:** 모든 작업은 표준화 및 리팩토링 범위에 한정.

---

## 3. 수용 기준

- [ ] `StudyLog` CRUD가 Service 계층을 경유하여 동작한다.
- [ ] `StudyLog` Entity에 시스템 필드 4개가 존재하고 DB 테이블명이 `tb_study_log`다.
- [ ] `AuthController`가 `UserRepository`를 직접 주입하지 않는다.
- [ ] `ResponseStatusException` 발생 시 표준 JSON 포맷으로 응답된다.
- [ ] `HelloController`가 존재하지 않는다.
- [ ] `StudyLogPage`에서 에러/성공 메시지가 `<Toast>`로 표시된다.
- [ ] StudyLog 관련 컴포넌트가 `components/studylog/`에 위치한다.
- [ ] `SubscriptionPage`, `BillStdPage` 삭제 시 `<ConfirmDialog>`가 표시된다.
- [ ] 대상 페이지의 입력/버튼 높이가 `h-8`이다 (LoginPage 제외).
- [ ] `SubscriptionSearchPopup`이 `useSubscriptionSearch` Hook을 경유한다.
- [ ] `UserPage`가 `useUser` Hook을 경유한다.
- [ ] `backend-rules.md`에 `SM` 접두사가 등록되어 있다.

---

## 4. 제외 범위

- StudyLog PK 타입 변경 (Long → String) 및 DB 마이그레이션
- LoginPage 입력/버튼 높이 변경
- 신규 비즈니스 기능 추가
- 단위/통합 테스트 작성
- 패키지 구조 변경 (flat 구조 유지)

---

## 5. 용어 정의 (glossary 기준)

| 한국어 | 코드명 | DB 테이블 |
|---|---|---|
| 가입 | Subscription | `tb_subscription` |
| 과금기준 | BillStd | `tb_bill_std` |
| 학습로그 | StudyLog | `tb_study_log` |
| 사용자 | User | `tb_user` |
| 대표정보 | Main (접미사) | — |
