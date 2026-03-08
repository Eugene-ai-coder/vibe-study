# 5단계: 실행 기록 (05_execution_log)

## 구현 완료 항목

### Step 1: DB 스키마
- [x] `schema.sql`에 tb_menu, tb_user_role, tb_menu_role DDL 추가

### Step 2: 역할 관련 Entity & Repository
- [x] `UserRoleId.java` — 복합키 클래스
- [x] `UserRole.java` — 사용자-역할 엔티티
- [x] `UserRoleRepository.java` — findByUserId
- [x] `MenuRoleId.java` — 복합키 클래스
- [x] `MenuRole.java` — 메뉴-역할 엔티티
- [x] `MenuRoleRepository.java` — findByMenuId, findByRoleCdIn, deleteByMenuId

### Step 3: Menu Entity & Repository
- [x] `Menu.java` — 메뉴 엔티티 (self-referencing)
- [x] `MenuRepository.java` — 트리 조회, 존재 확인 등

### Step 4: DTO
- [x] `MenuRequestDto.java` — roleCds 포함
- [x] `MenuResponseDto.java` — children(트리), roleCds 포함

### Step 5: Service
- [x] `MenuService.java` — 인터페이스
- [x] `MenuServiceImpl.java` — CRUD + 트리 조립 + 역할 동기화

### Step 6: Controller
- [x] `MenuController.java` — /api/menus 엔드포인트 6개

### Step 7: UserSessionDto 확장
- [x] `UserSessionDto.java` — roles 필드 추가
- [x] `UserServiceImpl.java` — getUserSession()에서 역할 조회

### Step 8: SecurityConfig 수정
- [x] `/api/menus/tree/my` — 모든 인증 사용자
- [x] `/api/menus/**` — ADMIN authority만
- [x] `CustomUserDetailsService.java` — DB 역할 기반 authority 부여

### Step 9: Frontend API
- [x] `menuApi.js` — getTree, getMyTree, get, create, update, delete

### Step 10: Pinia Store
- [x] `stores/menu.js` — fetchMyMenu, clear

### Step 11: Sidebar 전환
- [x] `Sidebar.vue` — 정적 MENU → useMenuStore 기반 재귀 렌더링
- [x] `MenuTreeItem.vue` — 사이드바용 재귀 컴포넌트
- [x] `stores/auth.js` — login/init/logout에서 메뉴 스토어 연동

### Step 12: MenuPage
- [x] `MenuPage.vue` — 좌측 트리뷰 + 우측 편집 폼
- [x] `components/menu/TreeNode.vue` — 관리용 재귀 트리 컴포넌트

### Step 13: Router
- [x] `router/index.js` — `/menu` 라우트 추가

### Step 14: 초기 데이터
- [x] `MenuDataInitializer.java` — 메뉴 12건 + 역할 코드 + 사용자 역할 + 메뉴-역할 매핑
- [x] `UserDataInitializer.java` — @Order(1) 추가

## 빌드 결과
- **BUILD SUCCESS** — 89 소스파일 컴파일 완료, 에러 없음
