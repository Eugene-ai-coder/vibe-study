# 1단계: 분석 (01_research)

## 1. 현행 구조 요약

### 메뉴관리 아키텍처
```
MenuController (@RestController, /api/menus)
├── MenuService (interface)
│   └── MenuServiceImpl
│       ├── MenuRepository (JPA, tb_menu)
│       └── MenuRoleRepository (JPA, tb_menu_role)
└── UserRoleRepository (역할 조회용)
```

### 프론트엔드 구조
```
MenuPage.vue (상태관리, API호출)
├── TreeNode.vue (재귀 트리 렌더링, 편집용)
├── FloatingActionBar.vue (저장/삭제/취소)
├── ConfirmDialog.vue (삭제 확인)
└── Toast.vue (성공/에러 메시지)

Sidebar.vue (런타임 네비게이션)
└── MenuTreeItem.vue (재귀 메뉴 아이템)
```

### 데이터 모델
```
tb_menu: menu_id(PK), menu_nm, menu_url, parent_menu_id(FK-self),
         sort_order(INT), menu_level(INT), use_yn
tb_menu_role: menu_id + role_cd (복합PK) — 메뉴-역할 N:M
tb_user_role: user_id + role_cd (복합PK) — 사용자-역할 N:M
```
- 역할 코드(ADMIN, USER)는 `tb_common_dtl_code`의 ROLE 그룹에 저장
- 별도 `tb_role` 마스터 테이블 없음

### 현행 정렬 순서 처리
- `sort_order`는 **수동 입력** — 사용자가 숫자를 직접 입력
- Repository: `ORDER BY sort_order ASC`로 조회
- 삽입/삭제 시 자동 재정렬 없음 → 번호 중복·갭 발생 가능
- 위/아래 이동 기능 없음

### 현행 권한 모델
- **암묵적 권한**: 메뉴 가시성 = 권한 (메뉴에 역할 할당 → 해당 역할 사용자만 접근)
- Spring Security: `SimpleGrantedAuthority(roleCd)` → `@hasAuthority("ADMIN")` 체크
- 권한관리 전용 화면 없음 — 역할은 공통코드에서만 관리
- 사용자-역할 할당 UI 없음 (코드 초기화에서만 설정)

## 2. 영향 범위

### 요구사항 1: 정렬 순서 자동 재정렬 + 위/아래 이동
| 계층 | 파일 | 변경 내용 |
|---|---|---|
| Backend | MenuService / MenuServiceImpl | moveUp/moveDown 메서드 추가, reorder 로직 |
| Backend | MenuController | `POST /api/menus/{menuId}/move-up`, `/move-down` 엔드포인트 |
| Backend | MenuRepository | 같은 부모 내 메뉴 조회 쿼리 (이미 존재) |
| Frontend | MenuPage.vue | sortOrder 입력 제거 또는 읽기전용, 위/아래 버튼 이벤트 |
| Frontend | TreeNode.vue | 위/아래 이동 버튼 UI 추가 |
| Frontend | menuApi.js | moveUp/moveDown API 호출 함수 추가 |

### 요구사항 2: 권한관리 화면
| 계층 | 파일 | 변경 내용 |
|---|---|---|
| Backend | 신규: RoleController | 역할 CRUD + 사용자-역할 할당 API |
| Backend | 신규: RoleService / RoleServiceImpl | 비즈니스 로직 |
| Backend | UserRoleRepository | 기존 활용 (findByUserId 등) |
| Backend | SecurityConfig | 새 엔드포인트 권한 설정 |
| Frontend | 신규: RolePage.vue | 권한관리 화면 |
| Frontend | 신규: roleApi.js | API 클라이언트 |
| Frontend | router | 라우트 추가 |
| DB | schema.sql | 필요 시 테이블 변경 |

## 3. 의존성 그래프

```
[메뉴 정렬 개선]
  MenuController → MenuService → MenuServiceImpl → MenuRepository
  TreeNode.vue → MenuPage.vue → menuApi.js → MenuController

[권한관리 신규]
  RoleController → RoleService → RoleServiceImpl → UserRoleRepository
                                                  → CommonDtlCodeRepository (역할 목록)
  RolePage.vue → roleApi.js → RoleController
  SecurityConfig ← 새 엔드포인트 등록
  MenuDataInitializer ← 초기 데이터 연동 (필요시)
```

## 4. 기존 테스트 현황
- 자동화 테스트 파일 없음 (테스트 디렉토리 미확인)
- 수동 검증 기반 개발 환경

## 5. 기술적 제약사항
- **H2 DB 사용** — DDL 호환성 주의 (schema.sql)
- **역할 = 공통코드** — 별도 tb_role 테이블 없이 `tb_common_dtl_code`에 의존
- **ID 생성 패턴** — 타임스탬프 기반 (`MNU` + yyyyMMddHHmmssSSS)
- **No Lombok** — Entity에 명시적 getter/setter 필요
- **Frontend 규칙** — DataGrid, FloatingActionBar, EMPTY_FORM 패턴 준수 필수

## 6. 리스크 식별
| 리스크 | 영향도 | 대응 |
|---|---|---|
| 정렬 순서 동시성 — 다중 사용자가 동시에 이동 시 충돌 | 낮음 (학습 프로젝트) | 낙관적 처리 |
| 권한관리 범위 확대 — 세분화된 권한(CRUD별) 요구 가능 | 중간 | 1차는 역할-사용자 매핑 + 역할-메뉴 매핑 수준으로 한정 |
| 공통코드 기반 역할 vs 전용 테이블 — 구조 결정 필요 | 중간 | 2단계에서 설계자 확인 |

## 7. 주요 파일 경로 참조

### Backend
- `src/main/java/com/example/vibestudy/Menu.java`
- `src/main/java/com/example/vibestudy/MenuController.java`
- `src/main/java/com/example/vibestudy/MenuService.java` / `MenuServiceImpl.java`
- `src/main/java/com/example/vibestudy/MenuRepository.java`
- `src/main/java/com/example/vibestudy/MenuRequestDto.java` / `MenuResponseDto.java`
- `src/main/java/com/example/vibestudy/MenuRole.java` / `MenuRoleId.java` / `MenuRoleRepository.java`
- `src/main/java/com/example/vibestudy/UserRole.java` / `UserRoleId.java` / `UserRoleRepository.java`
- `src/main/java/com/example/vibestudy/SecurityConfig.java`
- `src/main/java/com/example/vibestudy/CustomUserDetailsService.java`
- `src/main/java/com/example/vibestudy/MenuDataInitializer.java`
- `src/main/resources/schema.sql`

### Frontend
- `frontend-vue/src/pages/MenuPage.vue`
- `frontend-vue/src/components/menu/TreeNode.vue`
- `frontend-vue/src/components/main/Sidebar.vue` / `MenuTreeItem.vue`
- `frontend-vue/src/api/menuApi.js`
- `frontend-vue/src/stores/menu.js`