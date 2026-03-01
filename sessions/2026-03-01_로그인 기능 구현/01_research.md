# 1단계: 분석 보고서 (Research)
> 세션: 2026-03-01_로그인 기능 구현
> 분석 기준일: 2026-03-01

---

## 1. 현행 구조 요약

### Backend
- **기술**: Spring Boot 3.4.3 / Java 17 / H2 File DB (`./data/vibedb`, `ddl-auto=update`)
- **패키지**: `com.example.vibestudy` — **단일 패키지** (도메인 계층 분리 없음)
- **기존 엔티티**: `StudyLog`, `BillStd`, `Subscription`
- **API 엔드포인트**: `/api/logs`, `/api/bill-std`, `/api/subscriptions`
- **인증**: **전혀 없음.** Spring Security 미도입, 세션 없음.
- **CORS**: `CorsConfig.java` — `/api/**` 에 대해 `http://localhost:5173` 허용 (credentials 설정 없음)
- **예외처리**: `GlobalExceptionHandler` — Validation 에러 → `{"errors":[...]}` JSON

### Frontend
- **기술**: React 18 / Vite 6 / Tailwind CSS v3 / Axios / react-router-dom v7
- **라우팅**: `App.jsx` — `/` → `/study-logs`, `/bill-std`, `/subscriptions` 3개 라우트
- **레이아웃**: `Layout.jsx` (Header 포함) — 단순 상단 Header + 본문 구조
- **AuthContext**: `context/AuthContext.jsx` — 이미 존재하나 **더미(DEMO_USER) 방식**. 실제 API 연동 없음.
- **Header.jsx**: 로그인 버튼 클릭 시 DEMO_USER로 하드코딩 설정. 실 인증 없음.
- **apiClient**: `api/apiClient.js` — Axios 인스턴스 (`baseURL: '/api'`)

---

## 2. 영향 범위 분석

### 신규 개발 대상
| 구분 | 파일 | 설명 |
|---|---|---|
| Backend | `User.java` | 사용자 Entity (신규 테이블 `tb_user`) |
| Backend | `UserRepository.java` | JpaRepository |
| Backend | `UserService.java` / `UserServiceImpl.java` | 인증 로직 (비밀번호 검증) |
| Backend | `AuthController.java` | `POST /api/auth/login`, `POST /api/auth/logout` |
| Backend | `UserDataInitializer.java` | 가상 10건 초기 데이터 |
| Frontend | `api/authApi.js` | 로그인/로그아웃 API 호출 |
| Frontend | `pages/LoginPage.jsx` | 로그인 화면 (중앙 집중형) |
| Frontend | `pages/MainPage.jsx` | 종량가입관리 메인 대시보드 |
| Frontend | `components/common/MainLayout.jsx` | 3분할 레이아웃 (Header + LNB + Content) |
| Frontend | `components/main/Sidebar.jsx` | LNB 네비게이션 |
| Frontend | `components/main/DashboardContent.jsx` | 대시보드 본문 (현황판) |

### 수정 대상 (기존 파일)
| 파일 | 수정 내용 |
|---|---|
| `App.jsx` | `/login`, `/main` 라우트 추가. 인증 보호 라우트(ProtectedRoute) 추가. |
| `context/AuthContext.jsx` | 더미 방식 → 실제 API 연동으로 완전 교체. localStorage 아이디 저장 연동. |
| `Header.jsx` | 더미 로그인 버튼 제거. 로그인 사용자 정보/로그아웃 실 연동. |
| `CorsConfig.java` | 세션 쿠키 전달을 위해 `allowCredentials(true)` 추가 필요 (세션 방식 선택 시) |

---

## 3. 의존성 그래프

```
[LoginPage]
    └─ authApi.js ──→ POST /api/auth/login
                           └─ AuthController
                                 └─ UserServiceImpl
                                       └─ UserRepository → tb_user

[AuthContext]
    ├─ 공급: user 상태, login(), logout()
    └─ 소비: LoginPage, Header, ProtectedRoute, MainPage

[App.jsx]
    ├─ /login  → LoginPage
    ├─ /main   → MainPage (ProtectedRoute)
    ├─ /bill-std → BillStdPage (ProtectedRoute)
    └─ /subscriptions → SubscriptionPage (ProtectedRoute)

[MainPage]
    └─ 대시보드 통계: GET /api/subscriptions (상태별 count)
```

---

## 4. 기존 테스트 현황

- **테스트 코드 없음.** 기존 기능 모두 테스트 미작성.
- `src/test/java/com/example/vibestudy/VibeStudyApplicationTests.java` 기본 생성 파일만 존재 가능성.

---

## 5. 기술적 제약사항

### 5-1. 인증 방식 결정 필요 (핵심 제약)
현재 프로젝트는 Spring Security 미도입. 세션 기반 인증 구현 시 두 가지 경로가 있음:

| 방식 | 장점 | 단점 |
|---|---|---|
| **A. 커스텀 세션** (`HttpSession`) | Spring Security 없이 구현 가능 | CSRF, 필터 체인 직접 구현해야 함 |
| **B. Spring Security 도입** | 표준 방식, BCrypt 내장, CSRF 처리 | 의존성 추가, 설정 복잡도 증가 |

→ **단순성 원칙(CLAUDE.md)** 상 커스텀 세션 방식(A) 권장. `HttpSession`으로 사용자 정보 저장.

### 5-2. 비밀번호 암호화
Spring Security 없이 BCrypt 적용 시 `spring-security-crypto` 단독 의존성 추가 가능:
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

### 5-3. 레이아웃 이중 구조
- 기존 화면(`StudyLog`, `BillStd`, `Subscription`): 현행 `Layout.jsx` (상단 Header 방식) 유지
- 신규 메인 화면(`MainPage`): 3분할 구조(`MainLayout.jsx`) 별도 신규 작성
→ 두 레이아웃 공존 가능. `App.jsx` 라우트에서 분기.

### 5-4. 대시보드 통계 API
요구사항의 현황 지표(가입자 수, 상태별 요약)는 기존 `/api/subscriptions` 조회 데이터로 클라이언트 집계 가능. 신규 전용 API 없이 구현 가능.

### 5-5. H2 DDL 자동 생성
`ddl-auto=update` 설정으로 `User.java` Entity 추가 시 `tb_user` 테이블 자동 생성됨.

---

## 6. 리스크 식별

| 순위 | 리스크 | 수준 | 대응 방안 |
|---|---|---|---|
| 1 | **인증 방식 선택**: Spring Security 도입 여부 미결 | 중 | 커스텀 HttpSession 방식으로 단순 구현 권장 |
| 2 | **AuthContext 교체**: 기존 더미 방식 의존 컴포넌트(`Header`, `App`) 동반 수정 필요 | 중 | 인터페이스(user, login, logout) 시그니처 유지하면 하위 영향 최소화 |
| 3 | **레이아웃 이중화**: 기존 Header 네비와 새 LNB 구조 공존 | 중 | `MainLayout.jsx` 별도 작성으로 기존 Layout 영향 없음 |
| 4 | **`사용자` 필드 미확정**: `닉네임` 컬럼이 요구사항 테이블에 미포함 (Header에서 닉네임 표시 요구) | 중 | 2단계 요구사항 확정 시 컬럼 추가 확정 필요 |
| 5 | **CORS credentials**: 세션 쿠키 방식 사용 시 `allowCredentials(true)` + Axios `withCredentials: true` 쌍으로 설정 필요 | 저 | CorsConfig + apiClient 동반 수정으로 해결 |

---

## 7. 미확정 항목 (2단계 요구사항 확정 필요)

1. **`닉네임` 컬럼**: 요구사항 2.1 테이블 명세에 없으나 3.2(Header), 4.1(로그인 로직)에서 "닉네임" 표시 언급 → 컬럼 추가 필요 여부 확인
2. **인증 방식**: 세션(HttpSession) vs JWT 토큰 중 어느 방식으로 구현할지
3. **라우팅 보호 범위**: 기존 화면(`/bill-std`, `/subscriptions` 등)에도 로그인 강제 여부
4. **로그아웃 세션**: 서버 측 세션 무효화 API 필요 여부, 또는 클라이언트 상태 초기화만으로 충분한지
5. **대시보드 그래프**: "이번 달 사용량 그래프" 구현 수준 (실제 차트 vs 현황 카드만)
6. **회원가입 화면**: 요구사항 3.1에 "회원가입 버튼" 언급 — 이번 범위에 포함인지
