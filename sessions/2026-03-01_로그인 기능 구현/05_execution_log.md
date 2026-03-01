# 5단계: 실행 로그 (Execution Log)
> 세션: 2026-03-01_로그인 기능 구현
> 실행일: 2026-03-01

---

## 구현 완료 목록

### STEP 1: Backend 인프라 수정 ✅
| 파일 | 변경 내용 |
|---|---|
| `pom.xml` | `spring-security-crypto` 의존성 추가 |
| `CorsConfig.java` | `OPTIONS` 메서드 추가, `allowCredentials(true)` 추가 |
| `frontend/vite.config.js` | `cookieDomainRewrite: 'localhost'` 추가 |

### STEP 2: Backend 도메인 계층 신규 ✅
| 파일 | 내용 |
|---|---|
| `User.java` | `tb_user` Entity (userId, nickname, password, email, accountStatus, 시스템필드) |
| `UserRepository.java` | JpaRepository, existsByEmail, findByUserId |
| `UserSessionDto.java` | 세션 저장 DTO (Serializable), userId/nickname/accountStatus |
| `LoginRequestDto.java` | @NotBlank 검증 |
| `RegisterRequestDto.java` | @NotBlank/@Size/@Email 검증 |

### STEP 3: Backend 서비스 계층 신규 ✅
| 파일 | 내용 |
|---|---|
| `UserService.java` | authenticate, register 인터페이스 |
| `UserServiceImpl.java` | BCrypt 인증, accountStatus 검증, 중복 아이디/이메일 체크 |
| `UserDataInitializer.java` | 앱 기동 시 user01~user10 자동 생성 (활성8, 정지1, 탈퇴1) |

### STEP 4: Backend API 신규 ✅
| 파일 | 엔드포인트 |
|---|---|
| `AuthController.java` | POST /api/auth/login, POST /api/auth/logout, GET /api/auth/me, POST /api/auth/register, GET /api/auth/users |

> **비고**: `GET /api/users`는 계획 명시에 따라 `AuthController`에 `/api/auth/users`로 통합. 비밀번호 노출 방지를 위해 `LinkedHashMap` 방식으로 userId/nickname/email/accountStatus만 반환.

### STEP 5: Frontend 기반 수정 ✅
| 파일 | 변경 내용 |
|---|---|
| `api/apiClient.js` | `withCredentials: true` 추가 |
| `api/authApi.js` | 신규 — login, logout, getMe, register, getUsers |
| `context/AuthContext.jsx` | 전면 교체 — API 연동, 앱 기동 시 세션 복원, loading 상태 추가 |
| `frontend/package.json` | `recharts` 설치 완료 |
| `frontend/vite.config.js` | cookieDomainRewrite 추가 |

### STEP 6: Frontend 라우팅 보호 ✅
| 파일 | 변경 내용 |
|---|---|
| `components/common/ProtectedRoute.jsx` | 신규 — 세션 없으면 /login으로 리다이렉트 |
| `App.jsx` | 전면 개편 — 7개 라우트, 모든 보호 라우트에 ProtectedRoute 적용 |

### STEP 7: 로그인·사용자 페이지 신규 ✅
| 파일 | 내용 |
|---|---|
| `pages/LoginPage.jsx` | 아이디/비밀번호 입력, 아이디 저장, Enter 키 로그인, 에러 메시지 |
| `pages/UserPage.jsx` | 사용자 목록 테이블 + 신규 등록 폼, MainLayout 사용 |

### STEP 8: 메인 레이아웃 신규 ✅
| 파일 | 내용 |
|---|---|
| `components/common/MainLayout.jsx` | 헤더(닉네임+로그아웃) + 사이드바 + main 3분할 레이아웃 |
| `components/main/Sidebar.jsx` | 가입관리/시스템설정 그룹, 비활성 메뉴 disabled 처리, 현재 경로 강조 |
| `components/main/DashboardContent.jsx` | 현황 카드 4개, recharts BarChart(월별 신규가입), 미납자 리스트(클릭→/subscriptions) |
| `pages/MainPage.jsx` | MainLayout + DashboardContent 조합 |
| `pages/SubscriptionPage.jsx` | URL ?subsId= 파라미터 처리 추가 (대시보드 미납자 클릭 연동) |

### STEP 9: 기존 파일 수정 ✅
| 파일 | 변경 내용 |
|---|---|
| `components/common/Header.jsx` | DEMO_USER/더미 login 버튼 제거, 실 user.nickname 표시, logout→/login 이동 |

---

## 특이사항

1. **`Map.of()` 타입 추론 이슈**: `AuthController.listUsers()`에서 `Map.of()`의 제네릭 타입 추론 오류 발생 → `LinkedHashMap`으로 교체하여 해결.
2. **API 경로 조정**: 계획의 `GET /api/users`를 `GET /api/auth/users`로 통합 구현. `authApi.js`의 `getUsers()`도 동일하게 `/auth/users`로 호출.

---

## 다음 단계: 수동 테스트 시나리오 (6단계 검증)

계획서 5절 테스트 계획 참조.
- Backend 서버 재기동 후 H2 Console에서 `tb_user` 테이블 10건 확인
- `POST /api/auth/login` → user01 / password123
- 비로그인 상태 `/main` 접근 → `/login` 리다이렉트 확인
