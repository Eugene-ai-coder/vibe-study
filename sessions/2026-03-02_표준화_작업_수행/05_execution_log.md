# 5단계 실행 로그

## Step 1 — BE GlobalExceptionHandler 개선
- `ResponseStatusException` 핸들러 추가
- 응답 타입을 `Map<String, Object>`로 변경 (`errors` 리스트 + `message` 키 동시 포함)
- `Map.of()` → `HashMap` 으로 변경 (null 안전성)
- `HelloController.java` 삭제

## Step 2 — BE StudyLog Entity/DTO 개선
- `StudyLog.java` — `@Table(name = "tb_study_log")` 추가, 시스템 필드 4개 추가
- `StudyLogRequestDto.java` — `createdBy` 필드 추가

## Step 3 — BE StudyLog Service 레이어 도입
- `StudyLogService.java` — 인터페이스 신설
- `StudyLogServiceImpl.java` — 구현체 신설 (CRUD + 시스템 필드 처리)
- `StudyLogController.java` — Repository 직접 의존 → Service 의존으로 교체

## Step 4 — BE UserService listUsers() 추가
- `UserService.java` — `listUsers()` 메서드 인터페이스에 추가
- `UserServiceImpl.java` — `listUsers()` 구현 추가
- `AuthController.java` — `UserRepository` 의존 제거, `userService.listUsers()` 위임

## Step 5 — FE StudyLog 컴포넌트 디렉토리 이동
- `components/StudyLogItem.jsx` → `components/studylog/StudyLogItem.jsx`
- `components/StudyLogTable.jsx` → `components/studylog/StudyLogTable.jsx`
- `components/StudyLogForm.jsx` → `components/studylog/StudyLogForm.jsx`
- `components/EditModal.jsx` → `components/studylog/EditModal.jsx`
- `hooks/useStudyLogs.js` — `error` → `errorMsg`/`successMsg`, `clearMessages()` 추가
- `pages/StudyLogPage.jsx` — import 경로 수정, Toast 적용

## Step 6 — FE Hook 신설
- `hooks/useSubscriptionSearch.js` 신설
- `hooks/useUser.js` 신설
- `components/common/SubscriptionSearchPopup.jsx` — hook 적용
- `pages/UserPage.jsx` — hook 적용

## Step 7 — FE SubscriptionPage ConfirmDialog 적용
- `ConfirmDialog` import 추가
- `confirmOpen` state 추가
- `clearMessages()` useEffect 적용
- `onDeleteClick` / `onDelete` 분리

## Step 8 — FE BillStdPage ConfirmDialog 적용
- `ConfirmDialog` import 추가
- `confirmOpen` state 추가
- `handleDeleteClick` / `executeDelete` 분리

## Step 9 — FE SubscriptionMainPage searchError 제거
- `searchError` state 제거 → `getSearchError()` computed 함수로 교체
- `SubscriptionMainSearchBar.jsx` — `searchError` prop 제거

## Step 10 — FE h-10 → h-8 표준화
- `subscription/SubscriptionForm.jsx`
- `subscription/SubscriptionActionBar.jsx`
- `subscription/SubscriptionSearchBar.jsx`
- `billstd/BillStdForm.jsx`
- `billstd/BillStdActionBar.jsx`
- `billstd/BillStdSearchBar.jsx`

## Step 11 — FE 미사용 파일 삭제
- `components/common/Header.jsx` 삭제
- `components/common/Layout.jsx` 삭제

## Step 12 — DOC backend-rules.md SM 접두사 등록
- `docs/backend-rules.md` — ID 접두사 테이블에 `대표가입 (SubscriptionMain)` / `SM` 행 추가
