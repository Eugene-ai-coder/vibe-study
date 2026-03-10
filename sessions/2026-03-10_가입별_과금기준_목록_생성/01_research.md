# 리서치 결과

## 1. 현행 구조 요약

### 백엔드 (Spring Boot)
- 단일 패키지 `com.example.vibestudy`에 모든 클래스 존재 (Controller/Service/Repository/Entity/DTO)
- 표준 레이어: `@RestController` -> `Service(Interface)` -> `ServiceImpl` -> `JpaRepository`
- 보안: `SecurityUtils.getCurrentUserId()`로 현재 사용자 식별, Spring Security 기반 세션 인증
- DB: H2 (schema.sql + data.sql), JPA/Hibernate 자동 매핑

### 프론트엔드 (Vue 3 + Composition API)
- SPA: Vue Router, Pinia(auth/menu 스토어)
- API 계층: `api/` 디렉토리의 개별 API 파일 -> `apiClient.js`(axios wrapper)
- 공통 컴포넌트: `DataGrid`, `FloatingActionBar`, `Toast`, `ConfirmDialog`, `CommonCodeSelect`, `SubscriptionSearchPopup`, `MainLayout`
- 화면 패턴: Page(상태 소유) -> Component(렌더링), composable(`useCommonCodeLabel`) 활용

---

## 2. 요구사항별 영향 범위 분석

### 요구사항 1: 가입 테이블에 관리자ID 추가 + 사용자 팝업 조회

**현행:**
- `Subscription` 엔티티에 `adminId`(관리자ID) 필드 없음
- `tb_subscription` 테이블에 해당 컬럼 없음
- 프론트엔드에 사용자 검색 팝업 없음 (가입 검색 팝업 `SubscriptionSearchPopup.vue`는 존재)

**수정 대상:**
- `Subscription.java` — `adminId` 필드/getter/setter 추가
- `SubscriptionRequestDto.java` — `adminId` 필드 추가
- `SubscriptionResponseDto.java` — `adminId` 필드 추가
- `SubscriptionServiceImpl.java` — `toDto()`, `create()`, `update()` 매핑 추가
- `schema.sql` — `tb_subscription`에 `admin_id VARCHAR(50) NULL` 컬럼 추가
- `SubscriptionPage.vue` — 폼에 관리자ID 필드 + 사용자 팝업 버튼 추가
- **신규 파일**: `UserSearchPopup.vue` — `SubscriptionSearchPopup.vue` 패턴 참고하여 사용자 검색 팝업 생성
- **백엔드 사용자 조회 API**: `UserServiceImpl.listUsersPage()` 이미 존재 (`/api/users/page` 엔드포인트). 팝업에서 재활용 가능

### 요구사항 2: Main 페이지에 TODO 추가

**현행:**
- `MainPage.vue`는 `DashboardContent.vue`만 렌더링
- `DashboardContent.vue`에 현황카드 4개(전체/활성/정지/탈퇴), 월별 신규 가입 차트, 미납자 리스트 존재
- TODO 영역 없음

**수정 대상:**
- `DashboardContent.vue` — TODO 섹션 추가 (카드 형태)
- TODO 데이터를 조회할 백엔드 API 필요 (요구사항 3과 연동)

### 요구사항 3: 과금기준 등록진행상태 TODO 연동

**현행:**
- `BillStd` 엔티티에 `stdRegStatCd` (등록진행상태코드) 필드 존재
- 공통코드 `std_reg_stat_cd`로 관리됨 (승인/반려 등의 상세코드)
- 현재 "승인/반려가 아닌" 과금기준을 조회하는 전용 API 없음

**수정 대상:**
- `BillStdRepository.java` — "승인/반려가 아닌 상태" 조건 쿼리 메서드 추가
- `BillStdService.java` / `BillStdServiceImpl.java` — TODO용 조회 메서드 추가
- `BillStdController.java` — TODO 조회 엔드포인트 추가 (예: `GET /api/bill-std/todo`)
- `billStdApi.js` — TODO 조회 함수 추가
- `DashboardContent.vue` — TODO 데이터 fetch 및 렌더링

**주의:** `std_reg_stat_cd`의 "승인"과 "반려" 상세코드값을 확인해야 함 (data.sql에서 확인 필요)

### 요구사항 4: 공통코드 관리 유효일자 기본값 처리

**현행:**
- `CommonCodePage.vue`의 `EMPTY_CODE_FORM`과 `EMPTY_DTL_FORM`에서 `effStartDt: ''`, `effEndDt: ''`로 초기화
- 기본값 로직 없음 — 빈 값 그대로 서버에 전송

**수정 대상:**
- `CommonCodePage.vue` — 등록 모드 진입 시 `effStartDt`에 오늘 날짜, `effEndDt`에 `9999-12-31T23:59` 기본값 설정
- 서버에서 null 처리하는 로직(`CommonCodeServiceImpl.create()`, `createDetail()`)은 현재 dto 값을 그대로 전달하므로 프론트엔드에서만 기본값 처리하면 됨
- `handleCodeRowClick`에서 기존 값이 있으면 기존 값 표시 (현재 이미 이렇게 동작)

### 요구사항 5: Q&A 공지사항 기능

**현행:**
- `Qna` 엔티티에 공지사항 여부 필드 없음
- `tb_qna` 테이블에 공지사항 관련 컬럼 없음
- `QnaServiceImpl.findAll()`은 `createdDt DESC` 정렬만 수행
- `QnaPage.vue`에 공지사항 표시 로직 없음

**수정 대상:**
- `Qna.java` — `noticeYn` (공지사항여부, CHAR(1), 기본값 'N') 필드 추가
- `QnaRequestDto.java` — `noticeYn` 필드 추가
- `QnaResponseDto.java` — `noticeYn` 필드 추가
- `QnaServiceImpl.java` — `toDto()` 매핑 추가, `create()`/`update()` 매핑 추가
- `QnaServiceImpl.findAll()` — 정렬 로직 변경: 공지사항을 최상단에 배치 (유효기간 내 공지사항만)
- `schema.sql` — `tb_qna`에 `notice_yn CHAR(1) DEFAULT 'N'` 컬럼 추가
- `QnaPage.vue` — 목록에 공지사항 표시 (아이콘 또는 라벨)
- `QnaDetailPage.vue` — 공지사항 등록 체크박스 추가
- **"유효한 공지사항"의 정의가 불명확**: 유효기간 필드가 현재 Qna에 없으므로 추가 필요할 수 있음. 또는 단순히 `noticeYn='Y'`인 것을 유효로 간주할 수 있음 — 설계 확인 필요

### 요구사항 6: 가입별 과금기준 목록 조회 신규 화면

**현행:**
- 가입(Subscription)과 과금기준(BillStd) 간 관계: `BillStd.subsId` -> `Subscription.subsId` (FK)
- `BillStdRepository`에 `findBySubsIdAndLastEffYn()` 메서드 존재
- "유효한 가입"의 기준: `subsStatusCd`가 무엇인 경우? (ACTIVE만? PENDING 포함?)
- 가입별 과금기준을 LEFT JOIN으로 한번에 조회하는 API 없음

**수정 대상:**
- **신규 백엔드 API**: 가입 LEFT JOIN 과금기준 조회
  - 방법 A: `SubscriptionRepository`에 네이티브 쿼리 추가 (SubscriptionMainRepository의 `findListRaw` 패턴 참고)
  - 방법 B: 전용 Service/Controller 생성
  - 응답 DTO: 가입정보 + 과금기준 상태/유효정보를 포함하는 조합 DTO 필요
- **신규 프론트엔드 파일**:
  - `SubsBillStdListPage.vue` (또는 유사 이름) — 신규 페이지
  - `subsBillStdApi.js` (또는 기존 API 확장)
- `router/index.js` — 새 라우트 추가
- 메뉴 등록 필요 (tb_menu에 INSERT 또는 MenuDataInitializer 수정)

### 요구사항 7: 가입별 과금기준 목록에서 과금기준 화면 연동

**현행:**
- `BillStdPage.vue`는 `subsId`로 조회 가능 (검색유형에 `subsId` 옵션 있음)
- `SubscriptionPage.vue`는 `route.query.subsId`를 읽어 초기 조회 수행하는 패턴이 이미 존재
- `BillStdPage.vue`에는 query param으로 초기 조회하는 로직 없음

**수정 대상:**
- `BillStdPage.vue` — `onMounted`에서 `route.query.subsId` 읽어 자동 조회 로직 추가 (SubscriptionPage.vue 패턴 참고)
- 요구사항 6의 신규 목록 화면에서 행 클릭 시 `router.push({ path: '/bill-std', query: { subsId: row.subsId } })` 호출

---

## 3. 의존성 그래프

```
요구사항 1 (관리자ID)
  └─ Subscription 엔티티/DTO/Service 수정
  └─ UserSearchPopup 신규 (User API 재활용)
  └─ SubscriptionPage 폼 수정

요구사항 2 (Main TODO) ←── 요구사항 3 (BillStd TODO 데이터)
  └─ DashboardContent 수정
  └─ BillStd TODO API 신규

요구사항 4 (공통코드 유효일자 기본값)
  └─ CommonCodePage 프론트엔드만 수정

요구사항 5 (Q&A 공지사항)
  └─ Qna 엔티티/DTO/Service 수정
  └─ QnaPage, QnaDetailPage 수정
  └─ schema.sql 수정

요구사항 6 (가입별 과금기준 목록) ──→ 요구사항 7 (화면 연동)
  └─ 신규 API (가입 LEFT JOIN 과금기준)
  └─ 신규 페이지 + API 파일
  └─ router, 메뉴 등록

요구사항 7 (과금기준 화면 연동)
  └─ BillStdPage query param 처리 추가
  └─ 요구사항 6 페이지에서 router.push
```

**선후 관계:**
- 요구사항 2와 3은 함께 작업해야 함 (3이 2의 데이터 소스)
- 요구사항 6이 완료되어야 7 작업 가능
- 나머지(1, 4, 5)는 독립적

---

## 4. 기존 테스트 현황

- `src/test/java/com/example/vibestudy/VibeStudyApplicationTests.java` — 유일한 테스트 파일 (Spring Boot 기본 컨텍스트 로드 테스트만 존재할 것으로 추정)
- 단위 테스트, 통합 테스트 없음
- 프론트엔드 테스트 없음

---

## 5. 기술적 제약사항

1. **단일 패키지 구조**: 모든 Java 클래스가 하나의 패키지에 있어 이름 충돌 주의. 신규 DTO/엔티티 이름이 기존과 겹치지 않아야 함
2. **H2 DB**: schema.sql 기반 DDL. 컬럼 추가 시 `ALTER TABLE`이 아닌 `CREATE TABLE IF NOT EXISTS` 구문 내에서 수정
3. **Lombok 미사용**: 모든 getter/setter 수동 작성 필수
4. **ID 생성 규칙**: 타임스탬프 기반 `{접두사}yyyyMMddHHmmssSSS`. UUID 사용 금지 (단, QnaServiceImpl에서 UUID 사용 중 — 규칙 위반 상태)
5. **프론트엔드 Vue 3**: frontend-rules.md에 "React" 언급이 있으나 실제는 Vue 3. DataGrid 등 공통 컴포넌트는 Vue로 구현됨
6. **createdBy 단일 필드 원칙**: RequestDto에 `createdBy` 하나만 선언, Service에서 INSERT/UPDATE 분기
7. **SecurityUtils 사용**: create/update 시 `SecurityUtils.getCurrentUserId()`로 createdBy/updatedBy 설정. DTO의 createdBy와 별도로 서버에서 세션 기반 설정
8. **공통코드 의존성**: `std_reg_stat_cd`(등록진행상태), `subs_status_cd`(가입상태) 등이 공통코드로 관리됨. 해당 상세코드 값(승인/반려/ACTIVE 등) 확인 후 로직 구현 필요

---

## 6. 리스크 식별

| # | 리스크 | 영향도 | 대응 |
|---|--------|--------|------|
| 1 | 요구사항 5 "유효한 공지사항"의 정의 불명확 — Qna에 유효기간 필드가 없음 | 중 | 설계자에게 확인: (a) 단순 `noticeYn='Y'`이면 항상 표시 (b) 유효기간 필드 추가 |
| 2 | 요구사항 6 "유효한 가입"의 기준 불명확 — `subsStatusCd`의 어떤 값이 유효인지 | 중 | `data.sql`의 `subs_status_cd` 공통코드 확인 또는 설계자 확인 필요 |
| 3 | 요구사항 3 "승인/반려"의 `std_reg_stat_cd` 상세코드 값 미확인 | 중 | `data.sql`에서 해당 공통코드 상세값 확인 필요 |
| 4 | schema.sql 컬럼 추가(요구사항 1, 5) 시 기존 data.sql INSERT와 정합성 | 저 | data.sql의 INSERT 구문에 신규 컬럼 기본값 반영 |
| 5 | 요구사항 6 신규 API가 Subscription + BillStd 조인 필요 — 성능 고려 | 저 | 인덱스 `idx_tb_bill_std_subs_eff` 이미 존재. 페이징 적용으로 대응 |
| 6 | QnaServiceImpl의 UUID 사용이 백엔드 규칙(타임스탬프 ID) 위반 | 저 | 이번 작업 범위에서 함께 수정 가능 (요구사항 5 작업 시) |
| 7 | 메뉴 등록: 요구사항 6 화면을 사이드바에 추가하려면 MenuDataInitializer 또는 data.sql 수정 필요 | 저 | 메뉴 데이터 추가 포함 |

---
## Summary (다음 단계 전달용)
- **수정 대상 파일**: `Subscription.java/RequestDto/ResponseDto/ServiceImpl`, `schema.sql`, `data.sql`, `SubscriptionPage.vue`, `DashboardContent.vue`, `BillStd(Repository/Service/ServiceImpl/Controller)`, `billStdApi.js`, `Qna.java/RequestDto/ResponseDto/ServiceImpl`, `QnaPage.vue`, `QnaDetailPage.vue`, `CommonCodePage.vue`, `BillStdPage.vue`, `router/index.js`, 신규: `UserSearchPopup.vue`, 가입별 과금기준 목록 페이지/API
- **핵심 의존성**: 요구사항 2<->3 (TODO 데이터 연동), 요구사항 6->7 (목록->상세 연동), BillStd.subsId->Subscription FK, 공통코드(`std_reg_stat_cd`, `subs_status_cd`) 상세값 의존
- **기술적 제약사항**: Lombok 금지(수동 getter/setter), UUID 금지(타임스탬프 ID), 단일 패키지 구조, createdBy 단일 필드 원칙, SecurityUtils로 사용자 식별, H2 schema.sql DDL 방식
- **리스크**: "유효한 공지사항"/"유효한 가입" 정의 불명확(설계자 확인 필요), `std_reg_stat_cd`의 승인/반려 코드값 미확인, QnaServiceImpl UUID 규칙 위반 존재
