# 3단계: 구현 계획서 (Plan)
> 세션: 2026-03-01_로그인 기능 구현
> 작성일: 2026-03-01

---

## 1. 구현 전략 개요

### 핵심 원칙
- **Backend 선행, Frontend 후행**: 인증 API가 확정되어야 Frontend 연동이 안정적.
- **기반 인프라 먼저**: 의존성·CORS·apiClient 수정 → 도메인 계층 → API → UI 순.
- **기존 화면 영향 최소화**: `Layout.jsx`·기존 Page 파일은 건드리지 않음. `ProtectedRoute` 감싸기만.
- **단방향 의존 흐름**: `AuthContext` → Header, ProtectedRoute, Pages (역방향 없음)

### 인증 흐름 다이어그램
```
브라우저
  │ POST /api/auth/login { userId, password }
  ▼
AuthController.login()
  │ UserServiceImpl.authenticate(userId, password)
  │   1. UserRepository.findById(userId) → User
  │   2. BCryptPasswordEncoder.matches(input, stored)
  │   3. accountStatus 검증 (1만 허용)
  │   4. HttpSession.setAttribute("SESSION_USER", UserSessionDto)
  ▼
응답: 200 OK { userId, nickname, accountStatus }

브라우저 (이후 모든 요청)
  │ 쿠키: JSESSIONID=xxx (자동 첨부)
  ▼
GET /api/auth/me → SessionUser 반환 or 401
```

---

## 2. 변경 파일 목록

### 신규 생성 — Backend (9개)
| 파일 | 위치 |
|---|---|
| `User.java` | `src/main/java/com/example/vibestudy/` |
| `UserRepository.java` | 동일 |
| `UserSessionDto.java` | 동일 |
| `LoginRequestDto.java` | 동일 |
| `RegisterRequestDto.java` | 동일 |
| `UserService.java` | 동일 |
| `UserServiceImpl.java` | 동일 |
| `AuthController.java` | 동일 |
| `UserDataInitializer.java` | 동일 |

### 신규 생성 — Frontend (8개)
| 파일 | 위치 |
|---|---|
| `authApi.js` | `frontend/src/api/` |
| `ProtectedRoute.jsx` | `frontend/src/components/common/` |
| `LoginPage.jsx` | `frontend/src/pages/` |
| `UserPage.jsx` | `frontend/src/pages/` |
| `MainLayout.jsx` | `frontend/src/components/common/` |
| `Sidebar.jsx` | `frontend/src/components/main/` |
| `DashboardContent.jsx` | `frontend/src/components/main/` |
| `MainPage.jsx` | `frontend/src/pages/` |

### 수정 — Backend (2개)
| 파일 | 수정 내용 |
|---|---|
| `pom.xml` | `spring-security-crypto` 의존성 추가 |
| `CorsConfig.java` | `allowCredentials(true)` 추가 |

### 수정 — Frontend (4개)
| 파일 | 수정 내용 |
|---|---|
| `frontend/package.json` | `recharts` 의존성 추가 |
| `frontend/vite.config.js` | proxy에 `cookieDomainRewrite` 추가 |
| `api/apiClient.js` | `withCredentials: true` 추가 |
| `context/AuthContext.jsx` | 더미 → API 연동 전면 교체 |
| `components/common/Header.jsx` | 더미 로그인 제거, 실 사용자 정보 표시 |
| `App.jsx` | 라우팅 전면 개편 (ProtectedRoute 적용) |

---

## 3. 단계별 구현 순서 및 상세 내용

---

### STEP 1: Backend 의존성 및 CORS 수정

#### 1-1. `pom.xml` — `spring-security-crypto` 추가

```xml
<!-- Before: spring-boot-starter-test 아래 추가 -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

> Spring Boot parent가 버전 관리하므로 version 태그 불필요.

#### 1-2. `CorsConfig.java` — credentials 허용

```java
// Before
registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowedHeaders("*");

// After
registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
```

#### 1-3. `vite.config.js` — 쿠키 도메인 재작성

```js
// Before
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  },
},

// After
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
    cookieDomainRewrite: 'localhost',
  },
},
```

---

### STEP 2: Backend 도메인 계층

#### 2-1. `User.java` — Entity

```java
@Entity
@Table(name = "tb_user")
public class User {

    @Id
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "account_status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private int accountStatus = 1;

    // 시스템 필드
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    // Getters / Setters ...
}
```

#### 2-2. `UserRepository.java`

```java
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    Optional<User> findByUserId(String userId);
}
```

#### 2-3. `UserSessionDto.java` — 세션 저장 DTO

```java
public class UserSessionDto implements Serializable {
    private String userId;
    private String nickname;
    private int accountStatus;
    // 생성자, Getters ...
}
```

#### 2-4. `LoginRequestDto.java`

```java
public class LoginRequestDto {
    @NotBlank(message = "아이디를 입력해 주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
    // Getters ...
}
```

#### 2-5. `RegisterRequestDto.java`

```java
public class RegisterRequestDto {
    @NotBlank @Size(max = 50)
    private String userId;

    @NotBlank @Size(max = 50)
    private String nickname;

    @NotBlank @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank @Email
    private String email;
    // Getters ...
}
```

---

### STEP 3: Backend 서비스 계층

#### 3-1. `UserService.java` — 인터페이스

```java
public interface UserService {
    UserSessionDto authenticate(String userId, String password);
    UserSessionDto register(RegisterRequestDto dto);
}
```

#### 3-2. `UserServiceImpl.java`

```java
@Service
public class UserServiceImpl implements UserService {

    private static final String SESSION_ERROR_MSG = "아이디 또는 비밀번호가 일치하지 않습니다.";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserSessionDto authenticate(String userId, String rawPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, SESSION_ERROR_MSG));

        if (!encoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, SESSION_ERROR_MSG);
        }

        if (user.getAccountStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용이 제한된 계정입니다.");
        }

        return new UserSessionDto(user.getUserId(), user.getNickname(), user.getAccountStatus());
    }

    @Override
    public UserSessionDto register(RegisterRequestDto dto) {
        if (userRepository.existsById(dto.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다.");
        }
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setNickname(dto.getNickname());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setAccountStatus(1);
        user.setCreatedBy("SYSTEM");
        user.setCreatedDt(LocalDateTime.now());
        userRepository.save(user);
        return new UserSessionDto(user.getUserId(), user.getNickname(), user.getAccountStatus());
    }
}
```

#### 3-3. `UserDataInitializer.java`

```java
@Component
public class UserDataInitializer implements CommandLineRunner {
    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        String hashedPwd = encoder.encode("password123");
        for (int i = 1; i <= 10; i++) {
            User u = new User();
            u.setUserId("user" + String.format("%02d", i));
            u.setNickname("사용자" + i);
            u.setPassword(hashedPwd);
            u.setEmail("user" + i + "@example.com");
            u.setAccountStatus(i <= 8 ? 1 : (i == 9 ? 2 : 0)); // 8활성, 1정지, 1탈퇴
            u.setCreatedBy("SYSTEM");
            u.setCreatedDt(LocalDateTime.now());
            repo.save(u);
        }
    }
}
```

---

### STEP 4: Backend API

#### 4-1. `AuthController.java`

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String SESSION_KEY = "SESSION_USER";
    private final UserService userService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<UserSessionDto> login(
            @Valid @RequestBody LoginRequestDto dto,
            HttpSession session) {
        UserSessionDto userInfo = userService.authenticate(dto.getUserId(), dto.getPassword());
        session.setAttribute(SESSION_KEY, userInfo);
        return ResponseEntity.ok(userInfo);
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    // GET /api/auth/me
    @GetMapping("/me")
    public ResponseEntity<UserSessionDto> me(HttpSession session) {
        UserSessionDto user = (UserSessionDto) session.getAttribute(SESSION_KEY);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(user);
    }

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<UserSessionDto> register(
            @Valid @RequestBody RegisterRequestDto dto) {
        return ResponseEntity.status(201).body(userService.register(dto));
    }
}
```

---

### STEP 5: Frontend 기반 수정

#### 5-1. `package.json` — recharts 추가
```bash
# 실행 명령
npm install recharts
```

#### 5-2. `api/apiClient.js` — withCredentials 추가

```js
// Before
const apiClient = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// After
const apiClient = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
})
```

#### 5-3. `api/authApi.js` — 신규

```js
import apiClient from './apiClient'

export const login = (userId, password) =>
  apiClient.post('/auth/login', { userId, password }).then(r => r.data)

export const logout = () =>
  apiClient.post('/auth/logout')

export const getMe = () =>
  apiClient.get('/auth/me').then(r => r.data)

export const register = (data) =>
  apiClient.post('/auth/register', data).then(r => r.data)
```

#### 5-4. `context/AuthContext.jsx` — 전면 교체

```jsx
// Before: 더미 방식 (useState만)
// After: API 연동 + 앱 시작 시 세션 복원

import { createContext, useContext, useState, useEffect } from 'react'
import { getMe, login as apiLogin, logout as apiLogout } from '../api/authApi'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)  // 세션 복원 완료 전 로딩

  useEffect(() => {
    getMe()
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setLoading(false))
  }, [])

  const login = async (userId, password) => {
    const userInfo = await apiLogin(userId, password)
    setUser(userInfo)
    return userInfo
  }

  const logout = async () => {
    try { await apiLogout() } catch {}
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
```

---

### STEP 6: Frontend 라우팅 보호

#### 6-1. `components/common/ProtectedRoute.jsx` — 신규

```jsx
import { Navigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export default function ProtectedRoute({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="min-h-screen flex items-center justify-center">로딩 중...</div>
  if (!user) return <Navigate to="/login" replace />
  return children
}
```

#### 6-2. `App.jsx` — 라우팅 전면 개편

```jsx
// Before: 3개 라우트, ProtectedRoute 없음
// After: 6개 라우트 + ProtectedRoute 적용

import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from './components/common/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import MainPage from './pages/MainPage'
import UserPage from './pages/UserPage'
import BillStdPage from './pages/BillStdPage'
import SubscriptionPage from './pages/SubscriptionPage'
import StudyLogPage from './pages/StudyLogPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<ProtectedRoute><Navigate to="/main" replace /></ProtectedRoute>} />
        <Route path="/main"          element={<ProtectedRoute><MainPage /></ProtectedRoute>} />
        <Route path="/users"         element={<ProtectedRoute><UserPage /></ProtectedRoute>} />
        <Route path="/subscriptions" element={<ProtectedRoute><SubscriptionPage /></ProtectedRoute>} />
        <Route path="/bill-std"      element={<ProtectedRoute><BillStdPage /></ProtectedRoute>} />
        <Route path="/study-logs"    element={<ProtectedRoute><StudyLogPage /></ProtectedRoute>} />
      </Routes>
    </BrowserRouter>
  )
}
```

---

### STEP 7: 로그인 화면

#### 7-1. `pages/LoginPage.jsx` — 신규

**레이아웃**: 전체 화면 회색 배경 + 중앙 흰색 카드 (400px 고정)

**상태 관리**:
- `userId`, `password`, `rememberMe` — 입력 필드
- `errorMsg` — 로그인 실패 메시지
- `loading` — 버튼 로딩 상태

**핵심 로직**:
```jsx
const { login } = useAuth()
const navigate = useNavigate()

// 마운트 시 저장된 아이디 복원
useEffect(() => {
  const saved = localStorage.getItem('savedUserId')
  if (saved) { setUserId(saved); setRememberMe(true) }
}, [])

const handleLogin = async () => {
  if (!userId.trim() || !password.trim()) {
    setErrorMsg('아이디와 비밀번호를 입력해 주세요.')
    return
  }
  try {
    await login(userId, password)
    if (rememberMe) localStorage.setItem('savedUserId', userId)
    else localStorage.removeItem('savedUserId')
    navigate('/main')
  } catch (err) {
    const status = err?.response?.status
    setErrorMsg(
      status === 403
        ? '사용이 제한된 계정입니다.'
        : '아이디 또는 비밀번호가 일치하지 않습니다.'
    )
  }
}
```

**UI 구조**:
```
전체화면 (bg-[#F8FAFC] flex items-center justify-center)
└── 카드 (bg-white rounded-lg shadow p-8 w-96)
    ├── 타이틀: "종량가입관리 시스템"
    ├── 아이디 입력 (h-10)
    ├── 비밀번호 입력 (h-10, type=password)
    ├── 아이디 저장 체크박스
    ├── 로그인 버튼 (bg-[#2563EB] h-10 w-full, Enter 키 동작)
    └── 계정 찾기 링크 (text-gray-400, 기능 미구현)
```

#### 7-2. `pages/UserPage.jsx` — 신규 (사용자관리 화면)

**위치**: LNB "사용자관리" 메뉴 → `/users` (ProtectedRoute)

**구성**:
- 상단: 사용자 목록 테이블 (userId, nickname, email, accountStatus)
- 하단: 신규 사용자 추가 폼 (아이디, 닉네임, 비밀번호, 이메일)

**핵심 로직**:
```jsx
// 목록 조회: GET /api/users (신규 엔드포인트 필요)
// 사용자 추가
const handleRegister = async () => {
  try {
    await register({ userId, nickname, password, email })
    setSuccessMsg('사용자가 등록되었습니다.')
    fetchUsers()  // 목록 갱신
  } catch (err) {
    setErrorMsg(err?.response?.data?.message || '사용자 등록에 실패했습니다.')
  }
}
```

> **추가 API 필요**: `GET /api/users` — 전체 사용자 목록 반환 (AuthController 또는 별도 UserController에 추가)

---

### STEP 8: 메인 화면 (3분할 레이아웃)

#### 8-1. `components/common/MainLayout.jsx` — 신규

```jsx
// 구조: flex-col min-h-screen
// ├── <MainHeader /> (h-14, bg-white border-b)
// └── flex flex-1
//     ├── <Sidebar /> (w-56, bg-white border-r)
//     └── <main> (flex-1, bg-[#F8FAFC] overflow-auto p-6)
//           {children}

export default function MainLayout({ children }) {
  return (
    <div className="flex flex-col min-h-screen">
      <MainHeader />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar />
        <main className="flex-1 overflow-auto bg-[#F8FAFC] p-6">
          {children}
        </main>
      </div>
    </div>
  )
}
```

#### 8-2. `components/main/Sidebar.jsx` — 신규

**메뉴 구조**:
```
[가입관리]
  ├── 가입관리 → /subscriptions
  ├── 과금기준 → /bill-std
  └── 특수가입관리 (disabled)
[시스템 설정]
  ├── 사용자관리 → /users
  └── 공통코드관리 (disabled)
```

현재 경로(`useLocation`)와 비교하여 활성 항목 강조 (`text-[#2563EB] bg-blue-50`).

#### 8-3. `components/main/DashboardContent.jsx` — 신규

**현황 카드** (4개 — `Subscription` 데이터 집계):
- 전체 가입자 / 활성(ACTIVE) / 정지(SUSPENDED) / 탈퇴(TERMINATED)

**차트**: `recharts` BarChart
- X축: 최근 6개월 (월별)
- Y축: 신규 가입 건수
- 데이터: `subsDt` 기준으로 클라이언트 집계

**미납자 리스트**:
- `subsStatusCd === 'PENDING'` 인 항목 필터링
- 클릭 → `navigate('/subscriptions?subsId=' + item.subsId)`

```jsx
// SubscriptionPage에서 URL 파라미터 처리 추가
const [searchParams] = useSearchParams()
useEffect(() => {
  const subsId = searchParams.get('subsId')
  if (subsId) {
    setKeyword(subsId)
    setSearchType('SUBS_ID')
    // 자동 조회 실행
    handleSearch('SUBS_ID', subsId).then(result => setItems(result))
  }
}, [])
```

#### 8-4. `pages/MainPage.jsx` — 신규

```jsx
export default function MainPage() {
  return (
    <MainLayout>
      <DashboardContent />
    </MainLayout>
  )
}
```

---

### STEP 9: 기존 파일 수정

#### 9-1. `components/common/Header.jsx` — 더미 제거, 실 사용자 표시

```jsx
// Before: DEMO_USER 하드코딩, login() 버튼 있음
// After: useAuth()의 실 user 정보 표시, 로그인 버튼 제거 (모든 페이지 보호됨)

const { user, logout } = useAuth()
const navigate = useNavigate()

const handleLogout = async () => {
  await logout()
  navigate('/login')
}

// user는 항상 존재 (ProtectedRoute 보장)
// 닉네임 + 로그아웃 버튼만 표시
```

---

## 4. 트레이드오프 기록

| 결정 | 선택 | 포기한 대안 | 이유 |
|---|---|---|---|
| 인증 방식 | HttpSession | JWT | Spring Security 불필요, 단순성 우선 |
| BCrypt 범위 | spring-security-crypto 단독 | Spring Security 전체 도입 | 최소 의존성 원칙 |
| 세션 복원 | `GET /api/auth/me` | localStorage에 user 정보 저장 | 보안상 민감정보 localStorage 비권장 |
| 미납자 집계 | `subsStatusCd=PENDING` 필터 | 전용 API 신규 개발 | 기존 API 재사용, 추가 엔드포인트 불필요 |
| 차트 라이브러리 | recharts | chart.js, victory | React 친화적, 번들 크기 적절 |

---

## 5. 테스트 계획

### 수동 테스트 시나리오

| 시나리오 | 예상 결과 |
|---|---|
| 비로그인 상태로 `/main` 직접 접근 | `/login`으로 리다이렉트 |
| 올바른 아이디/비밀번호로 로그인 | `/main`으로 이동, 헤더에 닉네임 표시 |
| 틀린 비밀번호로 로그인 | "아이디 또는 비밀번호가 일치하지 않습니다." |
| 계정상태=2(정지)로 로그인 | "사용이 제한된 계정입니다." |
| 아이디 저장 체크 후 로그인 → 로그아웃 → 재접속 | 아이디 자동 입력 |
| 사용자관리에서 중복 아이디로 등록 | "이미 사용 중인 아이디입니다." |
| LNB "사용자관리" 클릭 | `/users` 이동, 사용자 목록 표시 |
| 로그아웃 | `/login`으로 이동, 세션 무효화 |
| 로그인 후 새로고침 | 로그인 상태 유지 (세션 복원) |
| 대시보드 미납자 클릭 | `/subscriptions`로 이동 + 자동 조회 |

---

## 6. 롤백 방안

- **Git 미사용 환경** (현재): 수정 전 파일을 별도 백업하지 않음 (CLAUDE.md 원칙)
- 문제 발생 시 본 계획서의 Before/After 스니펫을 역으로 적용하여 복구
- `pom.xml` 추가 의존성 제거 후 `mvnw clean` 으로 초기화
- `frontend/package.json`에서 `recharts` 제거 후 `npm install`

---

## 7. 구현 전 체크리스트

- [ ] Spring Boot 서버 중지 후 `pom.xml` 의존성 추가
- [ ] Frontend `npm install recharts` 실행
- [ ] Backend 전체 구현 후 서버 재기동 → H2 Console에서 `tb_user` 테이블 확인
- [ ] 초기 데이터 10건 생성 확인 (`user01` ~ `user10`)
- [ ] `POST /api/auth/login` Postman/curl 테스트 선행
- [ ] Frontend 연동 테스트
