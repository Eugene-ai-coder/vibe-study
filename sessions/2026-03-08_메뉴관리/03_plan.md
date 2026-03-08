# 3단계: 구현 설계 (03_plan)

## 1. 구현 전략 개요

3개 작업 영역을 순서대로 구현한다:
1. **DB·백엔드**: 메뉴/역할 테이블 → Entity → Repository → Service → Controller
2. **프론트엔드**: menuApi → MenuPage(트리뷰) → 라우터 등록
3. **통합**: Sidebar DB 전환, 역할 기반 필터링, 초기 데이터 INSERT

## 2. 변경 파일 목록

### 신규 생성
| # | 파일 | 설명 |
|---|------|------|
| 1 | `schema.sql` (추가) | tb_menu, tb_user_role, tb_menu_role DDL |
| 2 | `Menu.java` | 메뉴 엔티티 |
| 3 | `MenuRequestDto.java` | 요청 DTO |
| 4 | `MenuResponseDto.java` | 응답 DTO (트리 구조 포함) |
| 5 | `MenuRepository.java` | JPA Repository |
| 6 | `MenuService.java` | Service Interface |
| 7 | `MenuServiceImpl.java` | Service 구현체 |
| 8 | `MenuController.java` | REST Controller (`/api/menus`) |
| 9 | `UserRole.java` | 사용자-역할 엔티티 |
| 10 | `UserRoleId.java` | 복합키 클래스 |
| 11 | `UserRoleRepository.java` | 사용자-역할 Repository |
| 12 | `MenuRole.java` | 메뉴-역할 엔티티 |
| 13 | `MenuRoleId.java` | 복합키 클래스 |
| 14 | `MenuRoleRepository.java` | 메뉴-역할 Repository |
| 15 | `menuApi.js` | 프론트엔드 API 모듈 |
| 16 | `MenuPage.vue` | 메뉴관리 화면 (트리뷰 + 편집 폼) |
| 17 | `stores/menu.js` | Pinia 메뉴 스토어 (사이드바 캐싱) |
| 18 | `MenuDataInitializer.java` | 초기 메뉴 데이터 등록 |

### 기존 수정
| # | 파일 | 변경 내용 |
|---|------|----------|
| 19 | `schema.sql` | tb_menu, tb_user_role, tb_menu_role DDL 추가 |
| 20 | `router/index.js` | `/menu` 라우트 추가 |
| 21 | `Sidebar.vue` | 정적 MENU → Pinia 스토어 기반으로 전환 |
| 22 | `UserSessionDto.java` | `roles` 필드 추가 (List<String>) |
| 23 | `UserServiceImpl.java` | getUserSession()에서 역할 조회 추가 |
| 24 | `SecurityConfig.java` | ADMIN 역할 기반 메뉴관리 API 접근 제한 |
| 25 | `stores/auth.js` | user 객체에 roles 포함 |

## 3. 단계별 구현 순서

### Step 1: DB 스키마 (schema.sql)

```sql
-- Before: 없음
-- After:
CREATE TABLE IF NOT EXISTS tb_menu
(
    menu_id             VARCHAR(20)    NOT NULL,
    menu_nm             VARCHAR(100)   NOT NULL,
    menu_url            VARCHAR(200)   NULL,
    parent_menu_id      VARCHAR(20)    NULL,
    sort_order          INTEGER        DEFAULT 0,
    use_yn              CHAR(1)        DEFAULT 'Y',
    menu_level          INTEGER        DEFAULT 1,
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          TIMESTAMP      NULL,
    CONSTRAINT pk_tb_menu PRIMARY KEY (menu_id),
    CONSTRAINT fk_tb_menu_parent FOREIGN KEY (parent_menu_id) REFERENCES tb_menu (menu_id)
);

CREATE INDEX IF NOT EXISTS idx_tb_menu_parent
    ON tb_menu (parent_menu_id, sort_order);

CREATE TABLE IF NOT EXISTS tb_user_role
(
    user_id             VARCHAR(50)    NOT NULL,
    role_cd             VARCHAR(20)    NOT NULL,
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          TIMESTAMP      NULL,
    CONSTRAINT pk_tb_user_role PRIMARY KEY (user_id, role_cd),
    CONSTRAINT fk_tb_user_role_user FOREIGN KEY (user_id) REFERENCES tb_user (user_id)
);

CREATE TABLE IF NOT EXISTS tb_menu_role
(
    menu_id             VARCHAR(20)    NOT NULL,
    role_cd             VARCHAR(20)    NOT NULL,
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          TIMESTAMP      NULL,
    CONSTRAINT pk_tb_menu_role PRIMARY KEY (menu_id, role_cd),
    CONSTRAINT fk_tb_menu_role_menu FOREIGN KEY (menu_id) REFERENCES tb_menu (menu_id)
);
```

### Step 2: 역할 관련 Entity & Repository

**UserRole.java** — 복합키 `@IdClass(UserRoleId.class)`
- 필드: userId, roleCd, createdBy, createdDt, updatedBy, updatedDt

**UserRoleRepository.java**
```java
List<UserRole> findByUserId(String userId);
```

**MenuRole.java** — 복합키 `@IdClass(MenuRoleId.class)`
- 필드: menuId, roleCd, createdBy, createdDt, updatedBy, updatedDt

**MenuRoleRepository.java**
```java
List<MenuRole> findByMenuId(String menuId);
List<MenuRole> findByRoleCdIn(List<String> roleCds);
```

### Step 3: Menu Entity & Repository

**Menu.java**
- 필드: menuId(PK), menuNm, menuUrl, parentMenuId, sortOrder, useYn, menuLevel + 시스템필드
- ID 접두사: `MNU` (예: `MNU20260308143022123`)

**MenuRepository.java**
```java
List<Menu> findByParentMenuIdIsNullOrderBySortOrder();  // 최상위 메뉴
List<Menu> findByParentMenuIdOrderBySortOrder(String parentMenuId);  // 하위 메뉴
List<Menu> findByUseYnOrderByMenuLevelAscSortOrderAsc(String useYn);  // 전체 활성 메뉴
List<Menu> findByMenuIdIn(List<String> menuIds);  // ID 목록으로 조회
boolean existsByParentMenuId(String parentMenuId);  // 하위 메뉴 존재 확인
```

### Step 4: DTO

**MenuRequestDto.java**
- menuId, menuNm, menuUrl, parentMenuId, sortOrder, useYn, menuLevel, createdBy
- roleCds (List<String>) — 메뉴에 할당할 역할 코드 목록

**MenuResponseDto.java**
- 모든 필드 + children (List<MenuResponseDto>) — 트리 구조 반환용
- roleCds (List<String>) — 메뉴에 할당된 역할 코드 목록

### Step 5: Service

**MenuService.java** (Interface)
```java
List<MenuResponseDto> getAllMenuTree();                    // 전체 트리 (관리용)
List<MenuResponseDto> getMenuTreeByRoles(List<String> roleCds);  // 역할 기반 트리 (사이드바용)
MenuResponseDto getMenu(String menuId);                   // 단건 조회
MenuResponseDto createMenu(MenuRequestDto dto);           // 생성
MenuResponseDto updateMenu(String menuId, MenuRequestDto dto);   // 수정
void deleteMenu(String menuId);                           // 삭제
```

**MenuServiceImpl.java** 핵심 로직:
- `getAllMenuTree()`: 전체 메뉴를 조회하고 parentMenuId 기준으로 트리 구조로 조립
- `getMenuTreeByRoles()`: tb_menu_role에서 해당 역할의 menu_id 조회 → 해당 메뉴 + 상위 메뉴 포함하여 트리 조립
- `deleteMenu()`: `existsByParentMenuId()` 체크 → 하위 메뉴 존재 시 409 에러
- `createMenu()` / `updateMenu()`: 메뉴 저장 + tb_menu_role 동기화 (기존 삭제 → 신규 INSERT)

트리 조립 알고리즘:
```java
private List<MenuResponseDto> buildTree(List<Menu> allMenus) {
    Map<String, MenuResponseDto> dtoMap = new LinkedHashMap<>();
    allMenus.forEach(m -> dtoMap.put(m.getMenuId(), toDto(m)));

    List<MenuResponseDto> roots = new ArrayList<>();
    dtoMap.values().forEach(dto -> {
        if (dto.getParentMenuId() == null) {
            roots.add(dto);
        } else {
            MenuResponseDto parent = dtoMap.get(dto.getParentMenuId());
            if (parent != null) parent.getChildren().add(dto);
        }
    });
    return roots;
}
```

### Step 6: Controller

**MenuController.java** — `@RestController @RequestMapping("/api/menus")`
```java
GET    /api/menus/tree          → getAllMenuTree()           // 관리용 전체 트리
GET    /api/menus/tree/my       → getMenuTreeByRoles()      // 사이드바용 (현재 사용자 역할 기반)
GET    /api/menus/{menuId}      → getMenu()                 // 단건
POST   /api/menus               → createMenu()              // 생성
PUT    /api/menus/{menuId}      → updateMenu()              // 수정
DELETE /api/menus/{menuId}      → deleteMenu()              // 삭제
```

`/api/menus/tree/my` 엔드포인트:
- SecurityContextHolder에서 현재 사용자 ID 획득
- UserRoleRepository로 사용자 역할 조회
- MenuService.getMenuTreeByRoles() 호출

### Step 7: UserSessionDto 확장

```java
// Before
public class UserSessionDto {
    private String userId;
    private String nickname;
    private int accountStatus;
}

// After
public class UserSessionDto {
    private String userId;
    private String nickname;
    private int accountStatus;
    private List<String> roles;  // 추가
}
```

UserServiceImpl.getUserSession()에서 UserRoleRepository 조회하여 roles 세팅.

### Step 8: SecurityConfig 수정

```java
// Before
.requestMatchers("/api/**").authenticated()

// After
.requestMatchers("/api/menus/tree/my").authenticated()       // 사이드바용 — 모든 인증 사용자
.requestMatchers("/api/menus/**").hasRole("ADMIN")           // 관리용 — ADMIN만
.requestMatchers("/api/**").authenticated()
```

주의: Spring Security의 `hasRole("ADMIN")`은 `ROLE_ADMIN` authority를 확인.
→ UserDetailsService에서 ROLE_ 접두사 처리 필요. 또는 `hasAuthority("ADMIN")` 사용.

### Step 9: Frontend — menuApi.js

```javascript
import apiClient from './apiClient'

export const menuApi = {
  getTree:     ()              => apiClient.get('/menus/tree').then(r => r.data),
  getMyTree:   ()              => apiClient.get('/menus/tree/my').then(r => r.data),
  get:         (menuId)        => apiClient.get(`/menus/${menuId}`).then(r => r.data),
  create:      (data)          => apiClient.post('/menus', data).then(r => r.data),
  update:      (menuId, data)  => apiClient.put(`/menus/${menuId}`, data).then(r => r.data),
  delete:      (menuId)        => apiClient.delete(`/menus/${menuId}`).then(r => r.data),
}
```

### Step 10: Frontend — stores/menu.js (Pinia)

```javascript
export const useMenuStore = defineStore('menu', () => {
  const menuTree = ref([])

  async function fetchMyMenu() {
    menuTree.value = await menuApi.getMyTree()
  }

  function clear() {
    menuTree.value = []
  }

  return { menuTree, fetchMyMenu, clear }
})
```

### Step 11: Frontend — Sidebar.vue 전환

```
Before: const MENU = [ ... ] (정적 하드코딩)
After:  useMenuStore()에서 menuTree를 가져와 재귀적 렌더링
```

핵심 변경:
- 정적 MENU 배열 제거
- `useMenuStore()`에서 `menuTree` computed 사용
- 재귀 컴포넌트 또는 재귀 함수로 N레벨 트리 렌더링
- 그룹 메뉴(url 없음): 클릭 시 아코디언 토글
- 리프 메뉴(url 있음): router-link로 렌더링
- 로그인 직후 `menuStore.fetchMyMenu()` 호출 (auth store의 login() 내에서)

### Step 12: Frontend — MenuPage.vue

**레이아웃: 좌측 트리뷰(40%) + 우측 편집 폼(60%)**

좌측 트리뷰:
- 재귀 컴포넌트로 메뉴 트리 표시
- 들여쓰기로 계층 표현 (level * 16px padding)
- 클릭 시 우측 폼에 상세 정보 로드
- 선택된 노드 하이라이트 (bg-blue-50)
- 트리 상단에 "메뉴 추가" 버튼 (최상위 메뉴 추가)
- 각 노드 옆에 "하위 추가" 버튼 (호버 시 표시)

우측 편집 폼:
- 메뉴 기본 정보: 메뉴명, URL, 상위메뉴(읽기전용), 정렬순서, 사용여부
- 역할 할당: 체크박스 목록 (공통코드에서 역할 코드 조회)
- FloatingActionBar: 저장/삭제/취소

### Step 13: Router 등록

```javascript
// Before: 없음
// After:
{ path: '/menu', name: 'Menu', component: () => import('../pages/MenuPage.vue') },
```

### Step 14: 초기 데이터 (MenuDataInitializer.java)

ApplicationRunner로 구현. 서버 시작 시 tb_menu가 비어있으면 현행 메뉴 데이터 INSERT:

| menu_id | menu_nm | menu_url | parent_menu_id | sort_order | level |
|---------|---------|----------|----------------|------------|-------|
| MNU001 | Main | /main | NULL | 1 | 1 |
| MNU002 | 가입관리 | NULL | NULL | 2 | 1 |
| MNU003 | 가입관리 | /subscriptions | MNU002 | 1 | 2 |
| MNU004 | 과금기준 | /bill-std | MNU002 | 2 | 2 |
| MNU005 | 대표가입 관리 | /subscription-main | MNU002 | 3 | 2 |
| MNU006 | 특수가입관리 | /special-subscription | MNU002 | 4 | 2 |
| MNU007 | 시스템 설정 | NULL | NULL | 3 | 1 |
| MNU008 | 사용자관리 | /users | MNU007 | 1 | 2 |
| MNU009 | 공통코드관리 | /code | MNU007 | 2 | 2 |
| MNU010 | 메뉴관리 | /menu | MNU007 | 3 | 2 |
| MNU011 | 게시판 | NULL | NULL | 4 | 1 |
| MNU012 | Q&A | /qna | MNU011 | 1 | 2 |

역할 초기 데이터:
- 공통코드 `ROLE` 그룹에 `ADMIN`, `USER` 등록
- tb_user_role: 기존 사용자에게 ADMIN 역할 부여
- tb_menu_role: 모든 메뉴에 ADMIN 역할 부여, 메뉴관리를 제외한 메뉴에 USER 역할 부여

## 4. 트레이드오프 기록

| 결정 | 선택 | 대안 | 이유 |
|------|------|------|------|
| 트리 조립 위치 | 서버(Java) | 클라이언트(JS) | 역할 필터링을 서버에서 처리해야 보안상 안전 |
| 메뉴 ID 방식 | 고정 ID(MNU001~) | 자동생성(타임스탬프) | 초기 데이터에서 parent_menu_id 참조에 고정 ID 필요. 이후 신규 메뉴는 타임스탬프 방식 |
| 역할 저장 위치 | 별도 tb_user_role | User 엔티티 role 컬럼 | 다중 역할 지원 요구사항 |
| Sidebar 메뉴 로드 시점 | 로그인 성공 직후 | 앱 초기화 시 | 로그인 전에는 메뉴 불필요, 로그인 후 역할 확정 |
| menu_level 관리 | 엔티티 필드로 저장 | 런타임 계산 | 조회 성능 + 쿼리 편의성 |

## 5. 테스트 계획

| # | 테스트 항목 | 검증 방법 |
|---|-----------|----------|
| 1 | 메뉴 CRUD | 메뉴관리 화면에서 생성/수정/삭제 수행 |
| 2 | 트리 구조 | 3레벨 메뉴 추가 후 사이드바 확인 |
| 3 | 역할 필터링 | ADMIN/USER 계정으로 각각 로그인 후 사이드바 비교 |
| 4 | 하위 메뉴 삭제 차단 | 하위 메뉴 있는 그룹 삭제 시 에러 메시지 확인 |
| 5 | 사용 여부 | use_yn='N' 메뉴가 사이드바에서 숨겨지는지 확인 |
| 6 | 초기 데이터 | 서버 재시작 후 기존 메뉴 정상 표시 확인 |
| 7 | 권한 제한 | USER 역할로 /menu 접근 시 차단 확인 |

## 6. 롤백 방안

- Git 커밋 단위로 롤백 가능
- Sidebar.vue 원본은 Git 이력에 보존 (정적 MENU 배열)
- tb_menu 테이블 DROP 후 서버 재시작 시 schema.sql로 재생성
- `ddl-auto=update` 설정으로 기존 테이블에 영향 없음
