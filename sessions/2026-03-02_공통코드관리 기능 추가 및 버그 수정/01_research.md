# 01. 코드베이스 분석 결과

## 1. 현행 구조 요약

### 백엔드 (Spring Boot 3.4.3 / Java 17 / H2 File DB)
- 단일 패키지 구조: `com.example.vibestudy` — 모든 클래스가 평탄 구조로 배치됨
- 인증: Session 기반 (`HttpSession`) / `AuthController` → `UserServiceImpl`
- 도메인 모듈: Subscription, BillStd, SubscriptionMain, StudyLog, User
- 에러 처리: `GlobalExceptionHandler` → `{ errors, message }` JSON 표준 응답
- ID 생성: `{PREFIX}yyyyMMddHHmmssSSS` 타임스탬프 패턴
- DB: H2 File (`./data/vibedb`), `schema.sql`(DDL) + JPA `ddl-auto=update` 혼용
- **공통코드 테이블/엔티티 없음** — 신규 생성 필요

### 프론트엔드 (React + Vite + Tailwind CSS)
- Vite 프록시: `/api` → `http://localhost:8080`
- 구조: `pages/` → `components/{domain}/` ← `hooks/use{Domain}.js` ← `api/{domain}Api.js`
- 인증 상태: `AuthContext` (user, login, logout, loading)
- 라우터: `App.jsx` 에 등록된 경로 목록
  - `/main`, `/users`, `/subscriptions`, `/bill-std`, `/subscription-main`, `/study-logs`
  - **`/code` 경로 없음** — 공통코드 관리 화면 신규 등록 필요
- 레이아웃: `MainLayout` (헤더 + 사이드바) → `flex flex-col min-h-screen`
  - 헤더: `h-14 bg-white border-b` / 위치: `flex-shrink-0` (정적)
  - 바디: `flex flex-1 overflow-hidden` → main: `flex-1 overflow-auto`
- 사이드바: `Sidebar.jsx` — 메뉴 상수 배열(MENU) 하드코딩
  - Main 메뉴 항목 없음
  - '공통코드관리' → `to: null` (링크 없음)
  - 그룹명 클래스: `text-xs font-semibold text-gray-400` (작은 폰트)

---

## 2. 영향 범위 (요구사항별)

### [A] 대표가입 관리 화면 버그 수정
| 버그 | 현행 코드 위치 | 증상 |
|---|---|---|
| 서비스명 미변환 | `SubscriptionMainPage.jsx` `SVC_MAP` + `SubscriptionDataInitializer` | `svc_nm` 컬럼에 '서비스1/2/3'으로 저장, SVC_MAP 역방향 변환 없음 |
| 헤더 고정 이탈 | `MainLayout.jsx` | 헤더는 `flex-shrink-0`으로 구현되어 있어 이론상 고정됨. 실제 CSS 확인 필요 |
| 상세폼 3열 미달 | `SubscriptionMainForm.jsx` | `grid grid-cols-2`로 2열 배치 — 3열로 변경 필요 |
| 목록 경계선 없음 / 폭 고정 | `SubscriptionMainList.jsx` | `border-b border-gray-100` 만 있고 컬럼 리사이즈 없음 |

### [B] 사용자 관리 화면 버그 수정
| 버그 | 현행 코드 위치 | 증상 |
|---|---|---|
| 검색영역 없음 | `UserPage.jsx` | 상단 SearchBar 컴포넌트 자체가 없음 |
| 버튼 영역 스크롤 이동 | `UserPage.jsx` | `ActionBar` 패턴 없이 폼 내부에 버튼 배치됨 |

### [C] 모든 화면 공통 버그
| 버그 | 영향 파일 |
|---|---|
| 줄간격/줄크기/글자크기 미적용 | 전 도메인 컴포넌트 (`*List.jsx`, `*Form.jsx`) — `h-7` / `text-sm` 등 밀도 표준 일부 미반영 |
| 폼 상세영역 2열 → 3열 | `SubscriptionMainForm.jsx` (2열), `BillStdForm.jsx` (3열 OK), `SubscriptionForm.jsx` (3열 OK) |
| created_by/updated_by = 'SYSTEM' 고정 | `SubscriptionMainPage.jsx` L98, `SubscriptionPage.jsx` L35, `BillStdPage.jsx` L49, `UserServiceImpl.java` L55 — 세션 user.userId로 교체 필요 |
| 목록 높이 가변 | 전 도메인 List 컴포넌트 — 고정 높이(10줄 기준) 필요 |

### [D] 공통코드 테이블 및 관리 화면 (신규)
- 백엔드: Entity, Repository, Service, ServiceImpl, Controller, RequestDto, ResponseDto 전체 신규 생성
- DB: `tb_common_code` 테이블 DDL → `schema.sql` 추가
- 프론트엔드: `api/commonCodeApi.js`, `hooks/useCommonCode.js`, `components/common-code/*`, `pages/CommonCodePage.jsx` 신규 생성
- App.jsx: `/code` 경로 추가
- Sidebar.jsx: 공통코드관리 `to: '/code'` 활성화

### [E] Q&A 게시판 화면 (신규)
- 백엔드: `tb_qna` 테이블 + CRUD API 신규 생성
- 프론트엔드: 게시판 기본 형식 (목록/상세/등록) 신규 생성
- App.jsx: `/qna` 경로 추가
- Sidebar.jsx: 메뉴 추가 필요

### [F] 좌측 메뉴 개선
| 항목 | 현행 | 변경 |
|---|---|---|
| Main 메뉴 | 없음 | `{ label: 'Main', to: '/main' }` 상단 추가 |
| 그룹명 글자크기 | `text-xs` (12px) | `text-sm` (14px) 또는 더 크게 변경 |

---

## 3. 의존성 그래프

```
[Browser Session]
  └── AuthContext (user.userId) ──→ 모든 Page (createdBy 공급원)

[공통코드 신규]
  tb_common_code
  ← CommonCode.java (Entity)
  ← CommonCodeRepository.java
  ← CommonCodeService.java
  ← CommonCodeServiceImpl.java
  ← CommonCodeController.java (/api/common-code)
  ← commonCodeApi.js (/api/common-code)
  ← useCommonCode.js
  ← CommonCodePage.jsx (/code)
  ← App.jsx (route)
  ← Sidebar.jsx (menu link)

[Q&A 신규]
  tb_qna
  ← Qna.java (Entity)
  ← QnaRepository.java
  ← QnaService/Impl/Controller
  ← qnaApi.js / useQna.js / QnaPage.jsx
  ← App.jsx (route)
  ← Sidebar.jsx (menu link)

[createdBy 수정 영향]
  AuthContext.user.userId
  → SubscriptionMainPage.jsx (handleSaveClick)
  → SubscriptionPage.jsx (toRequestDto)
  → BillStdPage.jsx (toRequestDto)
  → UserServiceImpl.java (register) — 백엔드에서 세션으로 처리
```

---

## 4. 기존 테스트 현황

- `src/test/java/.../VibeStudyApplicationTests.java` — 스프링 컨텍스트 로드 테스트 1건만 존재
- 단위 테스트 / 통합 테스트 없음
- 프론트엔드 테스트 파일 없음 (jest/vitest 미설정)

---

## 5. 기술적 제약사항

| 항목 | 내용 |
|---|---|
| DB | H2 File DB — `schema.sql` 은 `CREATE TABLE IF NOT EXISTS` 방식으로 기존 테이블 보존. 새 테이블 DDL 추가만 하면 됨 |
| JPA | `ddl-auto=update` — Entity 추가 시 자동 테이블 생성되나 `schema.sql`과 이중 관리됨. 신규 테이블은 `schema.sql`에 명시적으로 추가해야 일관성 유지 |
| 인증 | 세션 기반 — `HttpSession`에서 `UserSessionDto` 꺼내 `userId` 추출 가능. 백엔드에서 세션 활용 시 `@SessionAttribute` 또는 `HttpSession` 파라미터 추가 필요 |
| 패키지 구조 | 단일 패키지 — 파일 수가 많아지나 현재 구조 유지 (리팩토링 요구사항 없음) |
| 컬럼 리사이즈 | 브라우저 기본 HTML table 에서는 `resizable` 속성 미지원 — CSS `resize` + `overflow` 또는 별도 라이브러리 필요. 단순 구현 시 `min-w` / `w` 직접 설정으로 대응 |
| Lombok 금지 | 모든 Entity/DTO에 명시적 getter/setter 작성 필수 |

---

## 6. 리스크 식별

| 리스크 | 수준 | 내용 |
|---|---|---|
| createdBy 'SYSTEM' 제거 | 중 | 프론트 4개 지점 + 백엔드 1개 지점 수정. `useAuth()` hook을 Page 계층에서 호출하여 `user.userId` 전달 필요. 로그인하지 않은 상태에서 저장 호출 시 null 방어 필요 |
| 대표가입 서비스명 버그 | 중 | `SubscriptionDataInitializer`가 svc_nm을 '서비스1/2/3'으로 저장하고, `SVC_MAP`은 '서비스명' → '서비스코드' 방향 매핑임. 데이터 자체가 '서비스1/2/3'이므로 실제로는 서비스코드(서비스명)가 그대로 표시되는 것. UI에서 역방향 매핑 필요 여부 확인 필요 |
| 목록 컬럼 리사이즈 | 하 | 기본 HTML table 에서는 간단히 구현 불가 — `colgroup`+`style` 또는 드래그 리사이즈 라이브러리 필요. 요구사항 구체화 필요 |
| Q&A 게시판 | 중 | 요구사항이 "게시판 기본 형식"으로 추상적. 목록/등록/수정/삭제 CRUD로 해석하고 구현 |
| schema.sql 수동 관리 | 하 | JPA ddl-auto=update와 schema.sql이 동시에 동작하므로 중복 DDL 주의 — schema.sql에만 추가하면 JPA가 IF NOT EXISTS 방식으로 충돌 없이 처리됨 |
