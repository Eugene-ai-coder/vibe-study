# 1단계: 코드베이스 분석 (Research)

## 1. 현행 구조 요약

### 1.1 백엔드 패키지 구조
```
com.example.vibestudy/  (단일 패키지, 서브패키지 없음)
├── Entity: User, Subscription, BillStd, StudyLog
├── DTO: *RequestDto, *ResponseDto (Request/Response 분리)
├── Repository: *Repository (JpaRepository 상속)
├── Service: *Service (Interface) + *ServiceImpl (구현)
├── Controller: *Controller (REST API, @RestController)
└── Config/Exception: CorsConfig, GlobalExceptionHandler, *DataInitializer
```

**기술 스택**: Spring Boot 3.4.3, Java 17, Maven, H2 File DB (`./data/vibedb`), JPA (`ddl-auto=update`)

### 1.2 프론트엔드 폴더 구조
```
frontend/src/
├── pages/          (LoginPage, MainPage, SubscriptionPage, BillStdPage, UserPage, StudyLogPage)
├── components/
│   ├── common/     (Header, MainLayout, Layout(레거시), ProtectedRoute, Toast, ErrorMessage, Loading)
│   ├── main/       (Sidebar, DashboardContent)
│   ├── subscription/  (SearchBar, List, Form, ActionBar)
│   └── billstd/    (SearchBar, Form, ActionBar)
├── hooks/          (useSubscription, useBillStd, useStudyLogs)
├── api/            (apiClient, subscriptionApi, billStdApi, authApi, studyLogApi)
└── context/        (AuthContext)
```

**기술 스택**: React 18, React Router v7, Axios, Tailwind CSS, Vite (프록시: /api → localhost:8080)

### 1.3 현행 라우팅
| 경로 | 페이지 | 레이아웃 |
|---|---|---|
| /login | LoginPage | 없음 |
| /main | MainPage | MainLayout (헤더+LNB+본문) |
| /subscriptions | SubscriptionPage | Layout (레거시) |
| /bill-std | BillStdPage | Layout (레거시) |
| /users | UserPage | MainLayout |
| /study-logs | StudyLogPage | Layout (레거시) |

### 1.4 현행 메뉴 구조 (Sidebar.jsx)
```
가입관리 그룹
  ├── 가입관리        → /subscriptions
  ├── 과금기준        → /bill-std
  └── 특수가입관리    → null (비활성화)

시스템 설정 그룹
  ├── 사용자관리      → /users
  └── 공통코드관리    → null (비활성화)
```

---

## 2. 영향 범위

### 2.1 백엔드 신규 생성 파일 (8개)
```
SubscriptionMain.java                 (Entity)
SubscriptionMainRepository.java        (Repository)
SubscriptionMainRequestDto.java        (DTO)
SubscriptionMainResponseDto.java       (DTO)
SubscriptionMainService.java           (Interface)
SubscriptionMainServiceImpl.java       (ServiceImpl)
SubscriptionMainController.java        (REST: /api/subscription-main)
schema.sql                             (신규 DDL 추가)
```

### 2.2 백엔드 기존 수정 파일 (1개)
- `SubscriptionServiceImpl.java` → `delete()` 시 가입별대표가입 존재 여부 확인 추가

### 2.3 프론트엔드 신규 생성 파일 (7개)
```
pages/SubscriptionMainPage.jsx
components/subscription-main/SubscriptionMainSearchBar.jsx
components/subscription-main/SubscriptionMainList.jsx
components/subscription-main/SubscriptionMainForm.jsx
components/subscription-main/SubscriptionMainActionBar.jsx
hooks/useSubscriptionMain.js
api/subscriptionMainApi.js
```

### 2.4 프론트엔드 기존 수정 파일 (2개)
- `App.jsx` → 라우트 추가 (`/subscription-main`)
- `components/main/Sidebar.jsx` → "대표가입 관리" 메뉴 추가

### 2.5 레이아웃 통일 수정 파일 (3개)
- `SubscriptionPage.jsx`, `BillStdPage.jsx`, `StudyLogPage.jsx` → Layout → MainLayout 변경

---

## 3. 의존성 그래프

### 3.1 현행 DB 스키마 (H2)

#### tb_subscription (가입) - 핵심 기준 테이블
| 컬럼 | 타입 | 비고 |
|---|---|---|
| subs_id | VARCHAR(50) PK | 사용자 입력 |
| subs_nm | VARCHAR(100) | 가입자명 |
| svc_nm | VARCHAR(100) | 서비스명 |
| fee_prod_nm | VARCHAR(100) | 요금상품명 |
| subs_status_cd | VARCHAR(20) | ACTIVE/SUSPENDED/TERMINATED/PENDING |
| subs_dt | TIMESTAMP | 가입일시 |
| chg_dt | TIMESTAMP | 변경일시 |
| created_by | VARCHAR(50) NOT NULL | 시스템필드 |
| created_dt | TIMESTAMP NOT NULL | 시스템필드 |
| updated_by | VARCHAR(50) | 시스템필드 |
| updated_dt | TIMESTAMP | 시스템필드 |

#### tb_bill_std (과금기준)
- `subs_id` FK → tb_subscription

#### 신규: tb_subscription_main (가입별대표가입)
- `subs_id` FK → tb_subscription (1:N)
- `main_subs_id` FK → tb_subscription (대표가입 참조)

### 3.2 백엔드 의존성 흐름
```
SubscriptionMainController
  └── SubscriptionMainService (Interface)
        └── SubscriptionMainServiceImpl
              ├── SubscriptionMainRepository (신규)
              └── SubscriptionRepository (기존, 대표가입ID 유효성 검증)
```

---

## 4. 기존 구현 패턴

### 4.1 Entity 시스템 필드 표준
모든 Entity에 공통 적용:
```java
private String createdBy;       // NOT NULL
private LocalDateTime createdDt; // NOT NULL
private String updatedBy;       // NULLABLE
private LocalDateTime updatedDt; // NULLABLE
```

### 4.2 ID 채번 방식
| 엔티티 | 방식 |
|---|---|
| Subscription | 사용자 입력 (예: SUB001) |
| BillStd | 자동생성 "BS" + 타임스탬프 |
| **SubscriptionMain** | **요구사항 기준: 자동 채번** |

### 4.3 Service 공통 패턴
- create: `createdDt = LocalDateTime.now()` 자동
- update: `updatedBy/updatedDt` 자동, PK/createdBy/createdDt 불변
- delete: 관련 데이터 존재 시 409 Conflict
- 없음: `ResponseStatusException(HttpStatus.NOT_FOUND, ...)`

### 4.4 프론트엔드 페이지 구조
```
Page 컴포넌트
  ├── 상태: items, selectedItem, formData, searchKeyword, serverError, successMsg
  ├── SearchBar (검색 조건 입력 + 조회 버튼)
  ├── List (테이블, 행 클릭 시 Form 연동)
  ├── Form (조회/입력 영역)
  └── ActionBar (플로팅 버튼바: fixed bottom-0)
```

### 4.5 API 에러 응답 표준
```
400 Bad Request  → 유효성 검증 실패 (GlobalExceptionHandler)
404 Not Found    → 리소스 없음
409 Conflict     → FK 제약 충돌
```

---

## 5. 기술적 제약사항 및 리스크

| 항목 | 리스크 | 해결방안 |
|---|---|---|
| **레이아웃 이원화** | Layout vs MainLayout 혼용으로 UX 불일치 | 모든 페이지 MainLayout 통일 필요 |
| **JPA ddl-auto=update** | schema.sql과 DB 불일치 가능 | schema.sql 동기화 필수 |
| **단일 패키지** | 파일 수 증가로 관리 복잡 | 명명 규칙 엄수 (SubscriptionMain* 접두) |
| **저장 로직 복잡성** | INSERT or UPDATE+INSERT (이력 관리) | SubscriptionMainServiceImpl에 @Transactional 필수 |
| **대표가입ID 유효성** | 가입 테이블에 존재하는 가입ID여야 함 | 저장 전 SubscriptionRepository.existsById() 검증 |
| **가입 삭제 제약** | SubscriptionMain 존재 시 가입 삭제 방지 필요 | SubscriptionServiceImpl.delete() 수정 필요 |
| **팝업 재사용성** | 가입검색 팝업 → 재활용 가능한 컴포넌트 | 별도 SearchPopup 컴포넌트로 분리 |

---

## 6. 핵심 설계 결정 필요 사항

1. **가입별대표가입 PK 채번**: 자동생성 (요구사항에 "자동 채번" 명시됨) → `SM` + 타임스탬프 패턴 사용
2. **저장 로직**: 기존 유효 레코드 종료(UPDATE) + 신규 INSERT 트랜잭션 처리 → BillStd의 이력 관리 패턴과 유사
3. **가입검색 팝업**: 재활용 가능 컴포넌트 → `components/common/SubscriptionSearchPopup.jsx`로 생성
4. **레이아웃 통일**: 요구사항 3.1에서 명시 → 모든 메인화면 MainLayout 적용
