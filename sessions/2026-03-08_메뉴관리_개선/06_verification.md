# 6단계: 품질 검증 (06_verification)

## 1. 수용 기준 충족 여부

| 수용 기준 | 결과 | 비고 |
|---|---|---|
| 메뉴 트리에서 ▲▼ 버튼으로 같은 레벨 내 순서 이동 가능 | **Pass** | API 검증 완료: move-up/move-down 정상 동작, sortOrder 교환 확인 |
| 메뉴 생성/삭제 시 sortOrder 자동 관리 | **Pass** | 생성 시 max+1 자동배치(sort:4), 삭제 후 1,2,3 자동압축 확인 |
| 편집 폼에 sortOrder 입력 필드 없음 | **Pass** | UI 스크린샷으로 확인 — 메뉴ID, 메뉴명, URL, 상위메뉴, 사용여부만 표시 |
| 권한관리 화면에서 역할별 사용자 조회/추가/제거 가능 | **Pass** | API 검증 완료. UI에서 버그 발견(아래 잔여이슈 참고) → 수정 완료 |
| ADMIN만 권한관리 접근 가능 | **Pass** | USER 역할로 /api/roles 접근 시 403 반환 확인 |

## 2. 계획 준수 여부
- Step 1~4 모두 계획서(`04_final_plan.md`) 대로 구현됨
- 추가/누락 없음

## 3. API 실동작 테스트 결과

### 메뉴 정렬 API
| 테스트 | 응답 | 결과 |
|---|---|---|
| POST /api/menus (auto sortOrder) | 201, sortOrder:4 | **Pass** |
| POST /api/menus/{id}/move-up | 200, sortOrder 교환 | **Pass** |
| POST /api/menus/{id}/move-down | 200, sortOrder 복원 | **Pass** |
| DELETE /api/menus/{id} (compaction) | 204, sort 1,2,3 | **Pass** |

### 권한관리 API
| 테스트 | 응답 | 결과 |
|---|---|---|
| GET /api/roles | 200, [ADMIN, USER] | **Pass** |
| GET /api/roles/ADMIN/users | 200, [user01] | **Pass** |
| GET /api/roles/available-users?roleCd=ADMIN | 200, [user02~10] | **Pass** |
| PUT /api/roles/ADMIN/users (add user02) | 200, 반영 확인 | **Pass** |
| PUT /api/roles/ADMIN/users (restore) | 200, 복원 확인 | **Pass** |
| GET /api/roles (non-ADMIN) | 403 Forbidden | **Pass** |

## 4. UI 자동 검증 결과 (agent-browser)

### 메뉴관리 화면
| 항목 | 결과 |
|---|---|
| 트리 렌더링 | **Pass** — 전체 메뉴 트리 정상 표시 |
| ▲▼ 버튼 표시 | **Pass** — 첫번째: ▼만, 마지막: ▲만, 중간: ▲▼ |
| sortOrder 필드 제거 | **Pass** — 편집 폼에 정렬순서 입력 없음 |
| 편집 폼 기능 | **Pass** — 메뉴 선택 시 수정 폼 정상 로드 |

### 권한관리 화면
| 항목 | 결과 |
|---|---|
| 페이지 로드 | **Pass** — /role 라우트 정상 |
| 역할 목록 표시 | **Pass** — 좌측에 관리자/일반사용자 표시 |
| 역할 선택 → 사용자 로드 | **Fail → 수정 완료** — CommonDtlCode 응답의 id 구조 미반영 → 수정 |
| 하이라이트 | **Pass** — 선택된 역할 파란색 배경 |

## 5. 회귀 영향도 분석

| 수정 파일 | 의존하는 기존 기능 | 영향도 |
|---|---|---|
| MenuServiceImpl.java | 메뉴 CRUD, 트리 조회 | **안전** — createMenu sortOrder 변경은 기존 로직에 영향 없음 |
| MenuController.java | 기존 6개 엔드포인트 | **안전** — 신규 2개 추가만 |
| SecurityConfig.java | 전체 인증/인가 | **안전** — /api/roles/** 규칙 추가만 |
| TreeNode.vue | 메뉴관리 트리 렌더링 | **안전** — isFirst prop 추가, 기존 동작 변경 없음 |
| MenuPage.vue | 메뉴 CRUD UI | **안전** — sortOrder 필드 제거, 이동 핸들러 추가 |
| menuApi.js | 메뉴 API 호출 | **안전** — 2개 함수 추가만 |
| MenuDataInitializer.java | 초기 데이터 | **안전** — 기존 데이터 조건 분기(count > 0)로 영향 없음 |
| UserRoleRepository.java | 로그인, 메뉴 필터링 | **안전** — 2개 메서드 추가만 |

## 6. 발견 버그 및 수정 사항

### Bug-1: RolePage.vue — CommonDtlCode 응답 구조 불일치 (수정 완료)
- **원인**: `/api/roles` 응답의 `commonDtlCode`가 `role.id.commonDtlCode` 구조인데, 코드에서 `role.commonDtlCode`로 접근
- **영향**: 역할 선택 시 roleCd가 undefined → 사용자 목록 로드 실패
- **수정**: `role.commonDtlCode` → `role.id.commonDtlCode`, `role.commonDtlCodeNm` → `role.commonDtlCodeNm` (이건 정상)

## 7. 설계자 수동 확인 체크리스트
- [ ] ▲▼ 버튼 호버 시 표시/숨김 애니메이션 자연스러운지
- [ ] 권한관리 사용자 추가 팝업의 디자인/정렬 확인
- [ ] 메뉴 이동 후 트리 깜빡임 정도 확인
- [ ] 사이드바에 권한관리 메뉴 추가 필요 (DB 초기화 또는 수동 메뉴 생성)