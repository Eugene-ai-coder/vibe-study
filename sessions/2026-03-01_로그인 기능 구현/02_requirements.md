# 2단계: 확정 요구사항 (Requirements)
> 세션: 2026-03-01_로그인 기능 구현
> 확정일: 2026-03-01

---

## 1. 용어 정의

| 용어 | 정의 |
|---|---|
| `user_id` | 사용자가 직접 입력하는 로그인 ID (PK, VARCHAR 50) |
| `nickname` | 화면 상단에 표시되는 사용자 표시명 |
| `account_status` | 계정 상태 코드 (1: 활성, 0: 탈퇴, 2: 정지) |
| 세션 | 서버 `HttpSession`에 저장된 로그인 사용자 정보 |
| ProtectedRoute | 비로그인 시 `/login`으로 리다이렉트하는 라우트 가드 컴포넌트 |
| LNB | Left Navigation Bar (좌측 메뉴 패널) |

---

## 2. 데이터 모델 확정

### 2.1. `tb_user` 테이블 (신규)

| 컬럼명 | DB 컬럼 | 타입 | Key | 필수 | 기본값 | 설명 |
|---|---|---|---|---|---|---|
| 아이디 | `user_id` | VARCHAR(50) | PK | Y | - | 로그인 ID |
| 닉네임 | `nickname` | VARCHAR(50) | - | Y | - | 화면 표시명 (`user_id` 다음 위치) |
| 비밀번호 | `password` | VARCHAR(255) | - | Y | - | BCrypt 해시값 |
| 이메일 | `email` | VARCHAR(100) | Unique | Y | - | 이메일 주소 |
| 계정상태 | `account_status` | TINYINT | - | Y | 1 | 1:활성 / 0:탈퇴 / 2:정지 |
| 생성자 | `created_by` | VARCHAR(50) | - | Y | - | 시스템 필드 |
| 생성일시 | `created_dt` | DATETIME | - | Y | - | 시스템 필드 |
| 수정자 | `updated_by` | VARCHAR(50) | - | N | - | 시스템 필드 |
| 수정일시 | `updated_dt` | DATETIME | - | N | - | 시스템 필드 |

> **비밀번호 암호화**: `spring-security-crypto` 의존성의 `BCryptPasswordEncoder` 사용 (Spring Security 전체 미도입)

### 2.2. 초기 데이터
- 사용자 데이터 0건 시 가상 10건 자동 생성 (`UserDataInitializer`)
- 초기 비밀번호: `password123` (BCrypt 해시 저장)

---

## 3. 인증 아키텍처 확정

- **방식**: 커스텀 세션 (`HttpSession`)
- **세션 키**: `SESSION_USER` → 로그인 사용자 정보 DTO 저장
- **세션 유지**: 브라우저 세션 쿠키 (`JSESSIONID`)
- **CORS 수정**: `CorsConfig` — `allowCredentials(true)` 추가, `allowedOrigins` 유지
- **Axios 수정**: `apiClient` — `withCredentials: true` 추가

---

## 4. 기능 명세

### 4.1. 로그인 (`POST /api/auth/login`)

**입력**: `{ userId, password }`

**처리 로직**:
1. `user_id`로 사용자 조회 → 미존재 시 오류
2. BCrypt로 비밀번호 대조 → 불일치 시 오류
3. `account_status` 확인 → 0(탈퇴) 또는 2(정지) 시 로그인 차단
4. 검증 통과 시 `HttpSession`에 사용자 정보 저장
5. 응답: `{ userId, nickname, accountStatus }`

**오류 응답**: 모든 실패 케이스 공통 → `"아이디 또는 비밀번호가 일치하지 않습니다."` (보안상 구분 불가)
- 단, `account_status` 비활성 시에는 별도 안내: `"사용이 제한된 계정입니다."`

**유효성 검사**: 아이디/비밀번호 미입력 시 클라이언트에서 경고 처리 (API 호출 전)

### 4.2. 로그아웃 (`POST /api/auth/logout`)

- 서버: `HttpSession.invalidate()` 로 세션 무효화
- 클라이언트: `AuthContext` 상태 초기화 → `/login`으로 리다이렉트
- 오류 발생 시에도 무조건 `/login`으로 이동

### 4.3. 세션 확인 (`GET /api/auth/me`)

- 현재 세션의 사용자 정보 반환 (앱 로드 시 로그인 상태 복원용)
- 세션 없으면 `401 Unauthorized`

### 4.4. 사용자 가입 (`POST /api/auth/register`)

**입력**: `{ userId, nickname, password, email }`

**처리 로직**:
1. `user_id` 중복 확인 → 중복 시 `400 Bad Request`
2. `email` 중복 확인 → 중복 시 `400 Bad Request`
3. 비밀번호 BCrypt 해시 후 저장
4. `account_status` 기본값 `1` (활성) 으로 저장
5. 응답: `{ userId, nickname }`

**유효성 검사 (DTO)**:
- `userId`: 필수, 최대 50자
- `nickname`: 필수, 최대 50자
- `password`: 필수, 최소 8자
- `email`: 필수, 이메일 형식

---

## 5. 화면 명세

### 5.1. 로그인 화면 (`/login`)

- **레이아웃**: 화면 중앙 집중형 박스 (Header·LNB 없음, 전체 화면)
- **구성 요소**:
  - 시스템 로고/타이틀
  - 아이디 입력 필드 (최대 50자)
  - 비밀번호 입력 필드 (마스킹)
  - '아이디 저장' 체크박스 → `localStorage`에 `savedUserId` 저장/삭제
  - 로그인 버튼 (Enter 키도 동작)
  - 계정 찾기 텍스트 링크 (이번 범위에서는 UI만, 기능 미구현)

### 5.2. 사용자관리 화면 (`/users`)

- **레이아웃**: `MainLayout` (3분할) 내 본문 영역
- **접근**: LNB "사용자관리" 메뉴 클릭
- **구성 요소**: 사용자 목록 테이블 + 신규 사용자 추가 폼 (아이디, 닉네임, 비밀번호, 이메일) + 가입 버튼
- 가입 성공 시 목록 자동 갱신

### 5.3. 종량가입관리 메인 화면 (`/main`)

- **레이아웃**: Header + LNB + 본문 3분할 (`MainLayout.jsx` 신규)
- **[Header 영역]**:
  - 시스템 로고 (클릭 시 `/main` 리프레시)
  - 로그인 사용자 닉네임 + 계정상태 표시
  - 로그아웃 버튼, 알림 아이콘 (UI만)
- **[LNB 영역]**:
  - 가입관리 → `/subscriptions`
  - 과금기준 → `/bill-std`
  - 특수가입관리 (UI만, 이동 없음)
  - 사용자관리 → `/users`
  - 공통코드관리 (UI만)
- **[본문 영역 — 대시보드]**:
  - 현황 카드: 전체 가입자 수, 상태별(활성/정지/탈퇴/대기) 건수
  - 이번 달 가입 추이 차트 (`recharts` BarChart 또는 LineChart)
  - 미납자 알림 리스트: 클릭 시 `/subscriptions?subsId={id}` 이동 → 자동 조회

---

## 6. 라우팅 및 접근 제어

| 경로 | 컴포넌트 | 보호 여부 |
|---|---|---|
| `/login` | `LoginPage` | 공개 (로그인 상태면 `/main` 리다이렉트) |
| `/main` | `MainPage` | **보호** |
| `/users` | `UserPage` | **보호** |
| `/subscriptions` | `SubscriptionPage` | **보호** |
| `/bill-std` | `BillStdPage` | **보호** |
| `/study-logs` | `StudyLogPage` | **보호** |
| `/` | - | `/main`으로 리다이렉트 (로그인 상태 시) |

---

## 7. 비기능 요건

- **보안**: 비밀번호 BCrypt 해시 저장. 오류 메시지에서 아이디/비밀번호 구분 불가하도록 처리.
- **UX**: 비로그인 접근 시 원래 접근하려던 경로 기억 후 로그인 완료 후 복원 (`redirect` 파라미터 활용)은 이번 범위 **제외** — 단순히 `/main`으로 이동.
- **상태 복원**: 앱 새로고침 시 `GET /api/auth/me` 호출로 세션 유지 상태 복원.

---

## 8. 제외 범위

- 비밀번호/아이디 찾기 기능 (UI 링크만 표시)
- 특수가입관리, 사용자관리, 공통코드관리 화면 (LNB 메뉴 UI만)
- 알림 아이콘 기능
- 로그인 실패 횟수 제한 / 계정 잠금
- 이메일 인증
- 원래 경로 복원 (로그인 후 무조건 `/main`)

---

## 9. 신규 파일 목록 (예상)

### Backend
| 파일 | 역할 |
|---|---|
| `User.java` | Entity (`tb_user`) |
| `UserRepository.java` | JpaRepository |
| `UserService.java` | 인터페이스 |
| `UserServiceImpl.java` | 로그인/사용자 가입/세션 로직 |
| `AuthController.java` | `POST /api/auth/login`, `/logout`, `/register`, `GET /api/auth/me` |
| `LoginRequestDto.java` | 로그인 요청 DTO |
| `RegisterRequestDto.java` | 사용자 가입 요청 DTO |
| `UserSessionDto.java` | 세션 저장용 DTO |
| `UserDataInitializer.java` | 초기 10건 데이터 생성 |

### Frontend
| 파일 | 역할 |
|---|---|
| `api/authApi.js` | login, logout, register, getMe |
| `pages/LoginPage.jsx` | 로그인 화면 |
| `pages/UserPage.jsx` | 사용자관리 화면 (목록 + 신규 추가) |
| `pages/MainPage.jsx` | 메인 대시보드 |
| `components/common/MainLayout.jsx` | 3분할 레이아웃 |
| `components/main/Sidebar.jsx` | LNB |
| `components/main/DashboardContent.jsx` | 대시보드 본문 |
| `components/common/ProtectedRoute.jsx` | 라우트 가드 |

### 수정 파일
| 파일 | 수정 내용 |
|---|---|
| `App.jsx` | 라우팅 전면 개편 (ProtectedRoute 적용) |
| `context/AuthContext.jsx` | 더미 → API 연동, 세션 복원 로직 |
| `components/common/Header.jsx` | 기존 화면용 헤더 (실 사용자 정보 표시) |
| `api/apiClient.js` | `withCredentials: true` 추가 |
| `CorsConfig.java` | `allowCredentials(true)` 추가 |
| `pom.xml` | `spring-security-crypto` 의존성 추가 |
| `frontend/package.json` | `recharts` 의존성 추가 |
