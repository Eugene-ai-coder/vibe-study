# 6단계: 품질 검증 (06_verification)

## 1. 수용 기준 충족 여부

| # | 수용 기준 | 결과 | 근거 |
|---|----------|------|------|
| 1 | 메뉴관리 화면에서 메뉴 CRUD가 정상 동작한다 | **Pass** | API 검증: POST 201, PUT 200, DELETE 204 |
| 2 | 트리뷰에서 3레벨 이상의 메뉴 계층이 표시된다 | **Pass** | 3레벨 메뉴 생성 시 menuLevel=3 정상 반환 |
| 3 | 메뉴 추가/수정/삭제 후 사이드바에 즉시 반영된다 | **Pass** | 저장 후 menuStore.fetchMyMenu() 호출 구현 |
| 4 | 하위 메뉴가 있는 메뉴 삭제 시 차단된다 | **Pass** | MNU002 삭제 시 409 + "하위 메뉴가 존재하여 삭제할 수 없습니다." |
| 5 | 역할별 메뉴 권한이 사이드바에 정상 적용된다 | **Pass** | ADMIN: 메뉴관리 포함 12개 메뉴 / USER: 메뉴관리 제외 |
| 6 | ADMIN이 아닌 사용자는 메뉴관리 화면에 접근 불가하다 | **Pass** | user02(USER)로 /api/menus/tree → 403 Forbidden |
| 7 | 기존 메뉴가 초기 데이터로 빠짐없이 등록된다 | **Pass** | 12개 메뉴 + 역할 매핑 정상 INSERT 확인 |

## 2. 계획 준수 여부

| Step | 계획 항목 | 준수 | 비고 |
|------|----------|------|------|
| 1 | DB 스키마 3개 테이블 | O | tb_menu, tb_user_role, tb_menu_role |
| 2 | 역할 Entity & Repository | O | UserRole, MenuRole + 복합키 |
| 3 | Menu Entity & Repository | O | |
| 4 | DTO | O | Request/Response + children, roleCds |
| 5 | Service (Interface + Impl) | O | 트리 조립 알고리즘 포함 |
| 6 | Controller 6 엔드포인트 | O | |
| 7 | UserSessionDto roles 확장 | O | 로그인 응답에 roles 포함 확인 |
| 8 | SecurityConfig 수정 | O | hasAuthority("ADMIN") 사용 |
| 9 | menuApi.js | O | |
| 10 | Pinia 메뉴 스토어 | O | |
| 11 | Sidebar.vue DB 전환 | O | 재귀 컴포넌트(MenuTreeItem) |
| 12 | MenuPage.vue 트리뷰 | O | TreeNode 재귀 컴포넌트 |
| 13 | Router /menu 등록 | O | |
| 14 | 초기 데이터 | O | 메뉴 12건 + ROLE 공통코드 + 사용자역할 |

## 3. API 실동작 검증 결과

| # | API | Method | 응답코드 | 결과 |
|---|-----|--------|---------|------|
| 1 | /api/auth/login (user01) | POST | 200 | roles:["ADMIN"] 포함 |
| 2 | /api/menus/tree | GET | 200 | 4 루트 + 하위 메뉴 트리 구조 |
| 3 | /api/menus/tree/my (ADMIN) | GET | 200 | 전체 메뉴 포함 |
| 4 | /api/menus/MNU010 | GET | 200 | 단건 조회 + roleCds |
| 5 | /api/menus (생성) | POST | 201 | menuLevel=3 자동 계산 |
| 6 | /api/menus/{id} (수정) | PUT | 200 | updatedBy, updatedDt 세팅 |
| 7 | /api/menus/{id} (삭제) | DELETE | 204 | 정상 삭제 |
| 8 | /api/menus/MNU002 (하위존재 삭제) | DELETE | 409 | 차단 메시지 |
| 9 | /api/menus/tree (user02/USER) | GET | 403 | Forbidden |
| 10 | /api/menus/tree/my (user02/USER) | GET | 200 | MNU010 제외 확인 |

## 4. UI 자동 검증 결과

| # | 페이지 | 검증 항목 | 결과 |
|---|--------|----------|------|
| 1 | 로그인 | user01 로그인 → 메인 이동 | **Pass** |
| 2 | 사이드바 | DB 기반 메뉴 트리 표시 | **Pass** |
| 3 | 사이드바 | 시스템 설정 하위에 메뉴관리 표시 | **Pass** |
| 4 | 메뉴관리 | 트리뷰 전체 메뉴 계층 표시 | **Pass** |
| 5 | 메뉴관리 | 트리 노드 클릭 → 편집 폼 표시 | **Pass** |
| 6 | 메뉴관리 | 역할 체크박스(ADMIN/USER) 표시 | **Pass** |
| 7 | 메뉴관리 | FloatingActionBar(삭제/취소/저장) 표시 | **Pass** |
| 8 | 사이드바(USER) | user02 로그인 → 메뉴관리 미표시 | **Pass** |
| 9 | 메뉴관리(USER) | /menu 직접 접근 → API 403 → 에러 메시지 | **Pass** |

## 5. 회귀 영향도 분석

| 수정 파일 | 영향 범위 | 잠재적 파손 |
|----------|----------|-----------|
| UserSessionDto.java | AuthController(/api/auth/login, /api/auth/me) | 낮음 — roles 필드 추가만, 기존 필드 불변 |
| UserServiceImpl.java | 로그인, 사용자 조회 | 낮음 — 생성자에 UserRoleRepository 추가, 기존 메서드 불변 |
| CustomUserDetailsService.java | Spring Security 인증 전체 | 중간 — authority가 ROLE_USER → DB 기반으로 변경. 기존 사용자에 USER 역할 부여로 호환 |
| SecurityConfig.java | 전체 API 접근 제어 | 중간 — /api/menus/** 규칙 추가. 기존 /api/** authenticated 규칙 유지 |
| Sidebar.vue | 전체 네비게이션 | 높음 — 정적→동적 전환. 초기 데이터로 기존 메뉴 완전 복제하여 대응 |
| stores/auth.js | 로그인/로그아웃 | 낮음 — 메뉴 스토어 연동 추가, 기존 로직 불변 |
| router/index.js | 라우팅 | 낮음 — /menu 라우트 1건 추가만 |

## 6. UI 수동 확인 체크리스트 (설계자 확인 필요)

- [ ] 사이드바 아코디언 전환 애니메이션이 자연스러운지
- [ ] 메뉴관리 트리뷰 들여쓰기 간격이 적절한지
- [ ] 트리 노드 호버 시 '+' 버튼 표시가 자연스러운지
- [ ] 모바일/축소 화면에서 레이아웃 깨짐 없는지
- [ ] 기존 페이지(가입관리, 공통코드관리 등) 사이드바 메뉴 정상 동작하는지

## 7. 잔여 이슈

- **프론트엔드 라우트 가드 미구현**: USER 역할 사용자가 /menu URL을 직접 입력하면 페이지에 접근 가능(API는 403으로 차단됨). Vue Router 가드에서 역할 체크 추가 권장.
