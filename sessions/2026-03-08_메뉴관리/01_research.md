# 1단계: 분석 (01_research)

## 1. 현행 구조 요약

### 메뉴 시스템 현황
- **좌측 사이드바 메뉴**: `frontend-vue/src/components/main/Sidebar.vue`에 **정적 배열(MENU)**로 하드코딩
- 메뉴 구조: 단일 항목(Main) + 그룹(가입관리, 시스템 설정, 게시판) → 하위 항목
- DB 기반 메뉴 관리 기능 없음 — 메뉴 추가/변경 시 소스코드 직접 수정 필요

### 기술 스택
- **Backend**: Spring Boot 3.4.3 / Java 17 / Spring Data JPA / H2 File DB
- **Frontend**: Vue 3 + Vue Router 4 + Pinia + Tailwind CSS 3 + Vite
- **인증**: Spring Security (세션 기반)

### 레이어 아키텍처 (참조: 공통코드관리)
```
Controller(@RestController) → Service(Interface) → ServiceImpl → Repository(JpaRepository)
Entity / RequestDto / ResponseDto 분리
```

## 2. 영향 범위

### 신규 생성 필요
| 구분 | 파일 | 설명 |
|------|------|------|
| DB | schema.sql 추가 | `tb_menu` 테이블 |
| Entity | Menu.java | 메뉴 엔티티 (트리 구조) |
| DTO | MenuRequestDto.java, MenuResponseDto.java | 요청/응답 DTO |
| Repository | MenuRepository.java | JPA 리포지토리 |
| Service | MenuService.java, MenuServiceImpl.java | 비즈니스 로직 |
| Controller | MenuController.java | REST API (`/api/menus`) |
| Frontend API | menuApi.js | Axios 래퍼 |
| Frontend Page | MenuPage.vue | 메뉴관리 화면 |
| Router | index.js 수정 | `/menu` 라우트 추가 |

### 기존 파일 수정 필요
| 파일 | 변경 내용 |
|------|----------|
| `Sidebar.vue` | 정적 MENU 배열 → DB 조회 API 호출로 전환 |
| `router/index.js` | `/menu` 라우트 추가 |
| `schema.sql` | `tb_menu` DDL 추가 |

## 3. 의존성 그래프

```
MenuPage.vue ──→ menuApi.js ──→ MenuController.java
                                      │
                                MenuService.java
                                      │
                               MenuServiceImpl.java
                                      │
                               MenuRepository.java
                                      │
                                  Menu.java (Entity)
                                      │
                                tb_menu (H2 DB)

Sidebar.vue ──→ menuApi.js ──→ (동일 API, 트리 구조 조회)
```

## 4. 데이터 모델 분석

### tb_menu 테이블 설계 방향
- **트리 구조**: `parent_menu_id`로 부모-자식 관계 표현 (NULL이면 최상위 그룹)
- **필수 컬럼**: menu_id(PK), menu_nm, menu_url, parent_menu_id(FK, nullable), sort_order, use_yn
- **시스템 컬럼**: created_by, created_dt, updated_by, updated_dt (프로젝트 표준)
- **참조**: 공통코드의 master-detail 패턴과 유사하나, 자기참조(self-referencing) 구조

### 기존 메뉴 데이터 (Sidebar.vue 기준)
| 그룹 | 항목 | URL |
|------|------|-----|
| (없음) | Main | /main |
| 가입관리 | 가입관리 | /subscriptions |
| 가입관리 | 과금기준 | /bill-std |
| 가입관리 | 대표가입 관리 | /subscription-main |
| 가입관리 | 특수가입관리 | /special-subscription |
| 시스템 설정 | 사용자관리 | /users |
| 시스템 설정 | 공통코드관리 | /code |
| 게시판 | Q&A | /qna |

→ 초기 데이터로 INSERT 필요 (data.sql 또는 schema.sql)

## 5. 기존 테스트 현황
- 프로젝트 내 테스트 코드 미확인 (단위/통합 테스트 없음)

## 6. 기술적 제약사항
- H2 DB 사용 중 (`ddl-auto=update`) — 스키마 변경 시 기존 데이터 유지됨
- 자기참조 FK 사용 시 삭제 순서 주의 (하위 메뉴 먼저 삭제)
- Sidebar.vue의 정적 메뉴를 DB 기반으로 전환 시, API 호출 실패 시 fallback 필요

## 7. 리스크 식별
| 리스크 | 영향도 | 대응 |
|--------|--------|------|
| Sidebar 메뉴 전환 시 기존 메뉴 깨짐 | 높음 | 초기 데이터(INSERT)로 현행 메뉴 완전 복제 |
| 메뉴 삭제 시 하위 메뉴 고아 데이터 | 중간 | 하위 메뉴 존재 시 삭제 차단 로직 |
| API 호출 실패 시 빈 메뉴 | 높음 | 에러 시 정적 fallback 또는 캐싱 |
| 메뉴 URL과 라우터 불일치 | 낮음 | 메뉴관리 화면에서 URL 유효성은 관리자 책임 |
