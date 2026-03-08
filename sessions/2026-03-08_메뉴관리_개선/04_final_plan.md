# 3단계: 구현 설계 (03_plan)

## 구현 전략 개요

두 기능을 독립적으로 구현한다:
- **기능 A** (메뉴 정렬 개선): 기존 파일 수정 위주 — Backend 3파일 + Frontend 3파일
- **기능 B** (권한관리): 신규 파일 생성 위주 — Backend 4파일 신규 + Frontend 2파일 신규 + 기존 3파일 수정

---

## 변경 파일 목록

### 기능 A: 메뉴 정렬 개선
| 파일 | 변경유형 |
|---|---|
| `MenuService.java` | 수정 — moveUp/moveDown 메서드 추가 |
| `MenuServiceImpl.java` | 수정 — 이동/자동정렬 로직, createMenu sortOrder 자동계산 |
| `MenuController.java` | 수정 — move-up/move-down 엔드포인트 추가 |
| `MenuPage.vue` | 수정 — sortOrder 필드 제거, 이동 이벤트 핸들러 |
| `TreeNode.vue` | 수정 — ▲▼ 호버 버튼 추가, 이벤트 emit |
| `menuApi.js` | 수정 — moveUp/moveDown 함수 추가 |

### 기능 B: 권한관리
| 파일 | 변경유형 |
|---|---|
| `RoleService.java` | **신규** — 인터페이스 |
| `RoleServiceImpl.java` | **신규** — 구현체 |
| `RoleController.java` | **신규** — REST API |
| `RoleUserDto.java` | **신규** — 응답 DTO |
| `UserRoleRepository.java` | 수정 — findByRoleCd, deleteByUserIdAndRoleCd 추가 |
| `SecurityConfig.java` | 수정 — /api/roles/** 권한 설정 |
| `MenuDataInitializer.java` | 수정 — 권한관리 메뉴 추가 |
| `RolePage.vue` | **신규** — 권한관리 화면 |
| `roleApi.js` | **신규** — API 클라이언트 |
| `router/index.js` | 수정 — /role 라우트 추가 |

---

## 단계별 구현 순서

### Step 1: 메뉴 정렬 — Backend

#### 1-1. MenuService.java — 메서드 추가
```java
// Before (line 17 앞에 추가)
void deleteMenu(String menuId);

// After
void moveMenuUp(String menuId);
void moveMenuDown(String menuId);
void deleteMenu(String menuId);
```

#### 1-2. MenuServiceImpl.java — createMenu 자동 sortOrder
```java
// Before (line 86)
menu.setSortOrder(dto.getSortOrder());

// After
int maxSort = getSiblingMenus(dto.getParentMenuId()).stream()
        .mapToInt(Menu::getSortOrder).max().orElse(0);
menu.setSortOrder(maxSort + 1);
```

#### 1-3. MenuServiceImpl.java — deleteMenu 후 재정렬
```java
// Before (line 137-138)
menuRoleRepository.deleteByMenuId(menuId);
menuRepository.deleteById(menuId);

// After
Menu menu = menuRepository.findById(menuId).get();
String parentMenuId = menu.getParentMenuId();
menuRoleRepository.deleteByMenuId(menuId);
menuRepository.deleteById(menuId);
compactSortOrders(parentMenuId);
```

#### 1-4. MenuServiceImpl.java — 신규 private/public 메서드
```java
// 추가할 메서드들

@Override
@Transactional
public void moveMenuUp(String menuId) {
    Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."));
    List<Menu> siblings = getSiblingMenus(menu.getParentMenuId());
    int idx = findIndex(siblings, menuId);
    if (idx <= 0) return;
    swapSortOrder(siblings.get(idx), siblings.get(idx - 1));
}

@Override
@Transactional
public void moveMenuDown(String menuId) {
    Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."));
    List<Menu> siblings = getSiblingMenus(menu.getParentMenuId());
    int idx = findIndex(siblings, menuId);
    if (idx < 0 || idx >= siblings.size() - 1) return;
    swapSortOrder(siblings.get(idx), siblings.get(idx + 1));
}

private List<Menu> getSiblingMenus(String parentMenuId) {
    if (parentMenuId == null) {
        return menuRepository.findByParentMenuIdIsNullOrderBySortOrder();
    }
    return menuRepository.findByParentMenuIdOrderBySortOrder(parentMenuId);
}

private int findIndex(List<Menu> menus, String menuId) {
    for (int i = 0; i < menus.size(); i++) {
        if (menus.get(i).getMenuId().equals(menuId)) return i;
    }
    return -1;
}

private void swapSortOrder(Menu a, Menu b) {
    int tmp = a.getSortOrder();
    a.setSortOrder(b.getSortOrder());
    b.setSortOrder(tmp);
    menuRepository.save(a);
    menuRepository.save(b);
}

private void compactSortOrders(String parentMenuId) {
    List<Menu> siblings = getSiblingMenus(parentMenuId);
    for (int i = 0; i < siblings.size(); i++) {
        if (siblings.get(i).getSortOrder() != i + 1) {
            siblings.get(i).setSortOrder(i + 1);
            menuRepository.save(siblings.get(i));
        }
    }
}
```

#### 1-5. MenuController.java — 엔드포인트 추가
```java
// deleteMenu 메서드 앞에 추가

@PostMapping("/{menuId}/move-up")
public ResponseEntity<Void> moveMenuUp(@PathVariable String menuId) {
    menuService.moveMenuUp(menuId);
    return ResponseEntity.ok().build();
}

@PostMapping("/{menuId}/move-down")
public ResponseEntity<Void> moveMenuDown(@PathVariable String menuId) {
    menuService.moveMenuDown(menuId);
    return ResponseEntity.ok().build();
}
```

### Step 2: 메뉴 정렬 — Frontend

#### 2-1. menuApi.js — API 함수 추가
```javascript
// Before
delete: (menuId) => apiClient.delete(`/menus/${menuId}`).then(r => r.data),

// After
delete:   (menuId)        => apiClient.delete(`/menus/${menuId}`).then(r => r.data),
moveUp:   (menuId)        => apiClient.post(`/menus/${menuId}/move-up`).then(r => r.data),
moveDown: (menuId)        => apiClient.post(`/menus/${menuId}/move-down`).then(r => r.data),
```

#### 2-2. TreeNode.vue — ▲▼ 호버 버튼 추가
```html
<!-- Before: 하위 추가 버튼 (line 81-85) -->
<button @click.stop="$emit('add-child', node)" ...>+</button>

<!-- After: 이동 버튼 + 하위 추가 버튼 -->
<div class="hidden group-hover:inline-flex items-center gap-0.5 mr-1">
  <button
    v-if="!isFirst"
    @click.stop="$emit('move-up', node)"
    class="w-5 h-5 text-xs text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded flex items-center justify-center"
    title="위로 이동"
  >▲</button>
  <button
    v-if="!isLast"
    @click.stop="$emit('move-down', node)"
    class="w-5 h-5 text-xs text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded flex items-center justify-center"
    title="아래로 이동"
  >▼</button>
  <button
    @click.stop="$emit('add-child', node)"
    class="w-5 h-5 text-xs text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded flex items-center justify-center"
    title="하위 메뉴 추가"
  >+</button>
</div>
```

- Props 추가: `isFirst: { type: Boolean, default: false }`
- Emits 추가: `['select', 'add-child', 'move-up', 'move-down']`
- 재귀 호출 시 `:is-first="idx === 0"` 전달
- 이벤트 버블: `@move-up="$emit('move-up', $event)"`, `@move-down="$emit('move-down', $event)"`

#### 2-3. MenuPage.vue — 변경사항

**편집 폼 sortOrder 필드 제거 (line 62-66 삭제):**
```html
<!-- 삭제 대상 -->
<div>
  <label class="block text-xs text-gray-500 mb-1">정렬순서</label>
  <input type="number" v-model.number="form.sortOrder" ... />
</div>
```

**TreeNode에 isFirst prop + 이동 이벤트 바인딩:**
```html
<!-- Before -->
<tree-node
  v-for="node in treeData"
  :key="node.menuId"
  :node="node"
  :depth="0"
  :selected-id="selectedMenuId"
  @select="handleNodeSelect"
  @add-child="handleAddChild"
/>

<!-- After -->
<tree-node
  v-for="(node, idx) in treeData"
  :key="node.menuId"
  :node="node"
  :depth="0"
  :selected-id="selectedMenuId"
  :is-first="idx === 0"
  :is-last="idx === treeData.length - 1"
  @select="handleNodeSelect"
  @add-child="handleAddChild"
  @move-up="handleMoveUp"
  @move-down="handleMoveDown"
/>
```

**이동 핸들러 추가:**
```javascript
const handleMoveUp = async (node) => {
  clearMessages()
  try {
    await menuApi.moveUp(node.menuId)
    await fetchTree()
    await menuStore.fetchMyMenu()
  } catch {
    errorMsg.value = '이동에 실패했습니다.'
  }
}

const handleMoveDown = async (node) => {
  clearMessages()
  try {
    await menuApi.moveDown(node.menuId)
    await fetchTree()
    await menuStore.fetchMyMenu()
  } catch {
    errorMsg.value = '이동에 실패했습니다.'
  }
}
```

**handleSave payload에서 sortOrder 제거:**
```javascript
// Before
const payload = {
  menuNm: form.menuNm,
  menuUrl: form.menuUrl || null,
  parentMenuId: form.parentMenuId || null,
  sortOrder: form.sortOrder,
  ...
}

// After
const payload = {
  menuNm: form.menuNm,
  menuUrl: form.menuUrl || null,
  parentMenuId: form.parentMenuId || null,
  ...
}
```

---

### Step 3: 권한관리 — Backend

#### 3-1. UserRoleRepository.java — 메서드 추가
```java
// Before
List<UserRole> findByUserId(String userId);

// After
List<UserRole> findByUserId(String userId);
List<UserRole> findByRoleCd(String roleCd);
void deleteByRoleCd(String roleCd);
```

#### 3-2. RoleUserDto.java — 신규
```java
package com.example.vibestudy;

public class RoleUserDto {
    private String userId;
    private String nickname;
    private int accountStatus;

    // 생성자, getter/setter
}
```

#### 3-3. RoleService.java — 신규 인터페이스
```java
package com.example.vibestudy;

import java.util.List;

public interface RoleService {
    List<RoleUserDto> getUsersByRole(String roleCd);
    List<RoleUserDto> getAvailableUsers(String roleCd);
    void saveRoleUsers(String roleCd, List<String> userIds);
}
```

#### 3-4. RoleServiceImpl.java — 신규 구현체
```java
@Service
public class RoleServiceImpl implements RoleService {
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    // Constructor DI

    @Override
    public List<RoleUserDto> getUsersByRole(String roleCd) {
        List<UserRole> userRoles = userRoleRepository.findByRoleCd(roleCd);
        Set<String> userIds = userRoles.stream()
                .map(UserRole::getUserId).collect(Collectors.toSet());
        return userRepository.findAllById(userIds).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<RoleUserDto> getAvailableUsers(String roleCd) {
        Set<String> assignedIds = userRoleRepository.findByRoleCd(roleCd).stream()
                .map(UserRole::getUserId).collect(Collectors.toSet());
        return userRepository.findAll().stream()
                .filter(u -> !assignedIds.contains(u.getUserId()))
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveRoleUsers(String roleCd, List<String> userIds) {
        userRoleRepository.deleteByRoleCd(roleCd);
        String currentUser = SecurityUtils.getCurrentUserId();
        for (String userId : userIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleCd(roleCd);
            ur.setCreatedBy(currentUser);
            ur.setCreatedDt(LocalDateTime.now());
            userRoleRepository.save(ur);
        }
    }

    private RoleUserDto toDto(User user) {
        RoleUserDto dto = new RoleUserDto();
        dto.setUserId(user.getUserId());
        dto.setNickname(user.getNickname());
        dto.setAccountStatus(user.getAccountStatus());
        return dto;
    }
}
```

#### 3-5. RoleController.java — 신규
```java
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;
    private final CommonDtlCodeRepository commonDtlCodeRepository;

    // Constructor DI

    @GetMapping
    public List<CommonDtlCode> getRoles() {
        return commonDtlCodeRepository.findByIdCommonCodeOrderBySortOrder("ROLE");
    }

    @GetMapping("/{roleCd}/users")
    public List<RoleUserDto> getUsersByRole(@PathVariable String roleCd) {
        return roleService.getUsersByRole(roleCd);
    }

    @GetMapping("/available-users")
    public List<RoleUserDto> getAvailableUsers(@RequestParam String roleCd) {
        return roleService.getAvailableUsers(roleCd);
    }

    @PutMapping("/{roleCd}/users")
    public ResponseEntity<Void> saveRoleUsers(@PathVariable String roleCd,
                                               @RequestBody List<String> userIds) {
        roleService.saveRoleUsers(roleCd, userIds);
        return ResponseEntity.ok().build();
    }
}
```

#### 3-6. SecurityConfig.java — 엔드포인트 추가
```java
// Before (line 49-50)
.requestMatchers("/api/menus/tree/my").authenticated()
.requestMatchers("/api/menus/**").hasAuthority("ADMIN")

// After
.requestMatchers("/api/menus/tree/my").authenticated()
.requestMatchers("/api/menus/**").hasAuthority("ADMIN")
.requestMatchers("/api/roles/**").hasAuthority("ADMIN")
```

#### 3-7. MenuDataInitializer.java — 권한관리 메뉴 추가
```java
// initMenuData()에서 시스템 설정 하위에 추가 (MNU010 다음)
createMenu("MNU013", "권한관리", "/role", "MNU007", 4, 2);

// allMenuIds 목록에 MNU013 추가
// MNU013에도 ADMIN만 역할 부여 (MNU010처럼)
```

### Step 4: 권한관리 — Frontend

#### 4-1. roleApi.js — 신규
```javascript
import apiClient from './apiClient'

export const roleApi = {
  getRoles:          ()              => apiClient.get('/roles').then(r => r.data),
  getUsersByRole:    (roleCd)        => apiClient.get(`/roles/${roleCd}/users`).then(r => r.data),
  getAvailableUsers: (roleCd)        => apiClient.get('/roles/available-users', { params: { roleCd } }).then(r => r.data),
  saveRoleUsers:     (roleCd, userIds) => apiClient.put(`/roles/${roleCd}/users`, userIds).then(r => r.data),
}
```

#### 4-2. RolePage.vue — 신규
좌우 분할 레이아웃:
- **좌측**: 역할 목록 리스트 (공통코드 ROLE 조회, 클릭 시 선택)
- **우측**: DataGrid로 할당된 사용자 표시 + 추가/제거 기능
- **하단**: FloatingActionBar (저장/취소)
- 컴포넌트: MainLayout, Toast, FloatingActionBar, ConfirmDialog, DataGrid 사용
- EMPTY_FORM 패턴 적용하지 않음 (폼이 아니라 선택+리스트 구조)

#### 4-3. router/index.js — 라우트 추가
```javascript
// Before (line 18)
{ path: '/menu', name: 'Menu', component: () => import('../pages/MenuPage.vue') },

// After
{ path: '/menu', name: 'Menu', component: () => import('../pages/MenuPage.vue') },
{ path: '/role', name: 'Role', component: () => import('../pages/RolePage.vue') },
```

---

## 트레이드오프 기록

| 항목 | 결정 | 이유 |
|---|---|---|
| sortOrder 교환 vs 전체 재정렬 | 교환 방식 | 이동 시 두 레코드만 UPDATE, 성능 우수 |
| 역할 저장 방식 (개별 추가/제거 vs 전체 교체) | 전체 교체 | syncMenuRoles 패턴과 일관성 유지, 구현 단순 |
| 역할 목록 소스 (별도 테이블 vs 공통코드) | 공통코드 유지 | 요구사항 범위 준수, 추후 확장 가능 |

## 테스트 계획

### 기능 A 테스트
- [ ] 최상위 메뉴 ▲▼ 이동 → sortOrder 교환 확인
- [ ] 하위 메뉴 ▲▼ 이동 → 같은 부모 내에서만 이동 확인
- [ ] 첫 번째 메뉴 ▲ 비표시, 마지막 메뉴 ▼ 비표시
- [ ] 신규 메뉴 생성 → 마지막 순서 자동 배치
- [ ] 메뉴 삭제 → sortOrder 자동 압축
- [ ] 이동 후 사이드바 즉시 반영

### 기능 B 테스트
- [ ] 역할 목록 조회 (ADMIN, USER)
- [ ] 역할 선택 → 할당된 사용자 목록 표시
- [ ] 사용자 추가 → 미할당 사용자 검색/선택
- [ ] 사용자 제거 → ConfirmDialog 후 해제
- [ ] 저장 → tb_user_role 반영 확인
- [ ] 비ADMIN 사용자 접근 차단

## 롤백 방안
- Git 기반: 기능별 커밋 분리로 개별 롤백 가능
- DB: H2 인메모리 → 서버 재시작 시 초기화
- 초기 데이터: MenuDataInitializer에 의해 자동 복구