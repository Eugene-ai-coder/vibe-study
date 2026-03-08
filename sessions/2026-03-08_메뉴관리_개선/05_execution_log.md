# 5단계: 실행 기록 (05_execution_log)

## Step 1: 메뉴 정렬 — Backend ✅
- `MenuService.java` — moveMenuUp/moveMenuDown 인터페이스 추가
- `MenuServiceImpl.java` — 구현 완료:
  - createMenu: sortOrder 자동 계산 (같은 부모 내 max + 1)
  - updateMenu: sortOrder 수동 변경 제거 (기존 값 유지)
  - deleteMenu: 삭제 후 compactSortOrders 호출
  - moveMenuUp/moveMenuDown: 인접 형제와 sortOrder 교환
  - 헬퍼: getSiblingMenus, findIndex, swapSortOrder, compactSortOrders
- `MenuController.java` — POST /{menuId}/move-up, /move-down 엔드포인트 추가

## Step 2: 메뉴 정렬 — Frontend ✅
- `menuApi.js` — moveUp/moveDown 함수 추가
- `TreeNode.vue` — ▲▼ 호버 버튼 추가, isFirst prop, move-up/move-down emit
- `MenuPage.vue` — sortOrder 입력 필드 제거, payload에서 sortOrder 제거, handleMoveUp/Down 핸들러 추가, TreeNode에 isFirst/isLast/move 이벤트 바인딩

## Step 3: 권한관리 — Backend ✅
- `UserRoleRepository.java` — findByRoleCd, deleteByRoleCd 추가
- `RoleUserDto.java` — 신규 생성 (userId, nickname, accountStatus)
- `RoleService.java` — 신규 인터페이스 (getUsersByRole, getAvailableUsers, saveRoleUsers)
- `RoleServiceImpl.java` — 신규 구현체
- `RoleController.java` — 신규 REST API (/api/roles)
- `SecurityConfig.java` — /api/roles/** ADMIN 권한 설정
- `MenuDataInitializer.java` — MNU013 권한관리 메뉴 추가 (시스템설정 하위, ADMIN 전용)

## Step 4: 권한관리 — Frontend ✅
- `roleApi.js` — 신규 API 클라이언트
- `RolePage.vue` — 신규 권한관리 화면 (좌: 역할 목록, 우: DataGrid 사용자 목록, 추가 팝업, 제거 ConfirmDialog)
- `router/index.js` — /role 라우트 추가

## 빌드 결과
- Backend: `mvnw compile` 성공 (에러 없음)
- Frontend: `vite build` 성공 (에러 없음)