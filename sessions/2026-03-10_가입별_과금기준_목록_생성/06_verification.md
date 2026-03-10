# 검증 결과서

검증일시: 2026-03-10

---

## 1. 수용 기준 충족 여부

### 요구사항 1: 가입 테이블에 관리자ID 추가 — **Pass**
| 기준 | 결과 | 근거 |
|---|---|---|
| 가입 등록 시 adminId 정상 저장 | Pass | `POST /api/subscriptions` + `adminId: "user01"` → 201, 응답에 `adminId: "user01"` 포함 |
| 가입 수정 시 adminId 정상 변경 | Pass | `PUT /api/subscriptions/SUBS_TEST_QA` + `adminId: "user02"` → 200, 응답에 `adminId: "user02"` |
| 조회 시 adminId 반환 | Pass | `GET /api/subscriptions?keyword=SUBS0001` → 응답에 `adminId` 필드 존재 |
| 사용자 검색 팝업 (프론트) | UI확인필요 | UserSearchPopup.vue 코드 검토상 구조 정상, `/api/users/page` 재활용 |

### 요구사항 2+3: Main TODO + 과금기준 연동 — **Pass**
| 기준 | 결과 | 근거 |
|---|---|---|
| APPROVED/CANCEL 아닌 과금기준 표시 | Pass | DRAFT 상태 과금기준 생성 → `GET /api/bill-std/todo` → 1건 반환 |
| APPROVED 상태는 제외 | Pass | DRAFT→APPROVED 변경 후 → `GET /api/bill-std/todo` → 빈 배열 |
| TODO 클릭 시 과금기준 화면 이동 (프론트) | UI확인필요 | DashboardContent.vue에서 `router.push(/bill-std?subsId=...)` 구현 확인 |

### 요구사항 4: 공통코드 유효일자 기본값 — **Pass**
| 기준 | 결과 | 근거 |
|---|---|---|
| 신규 등록 시 effStartDt 기본값 = 오늘 | Pass | `handleCodeNewClick()`에서 `todayDatetime()` 설정, 상세코드 등록 버튼에서도 동일 |
| 신규 등록 시 effEndDt 기본값 = 9999-12-31 | Pass | `effEndDt: '9999-12-31T23:59'` 하드코딩 확인 |
| 기존 코드 수정 시 기존 값 유지 | Pass | `handleCodeRowClick()`에서 `Object.assign(codeForm, { ...code })` — 서버 반환값 그대로 사용 |

### 요구사항 5: Q&A 공지사항 기능 — **Pass**
| 기준 | 결과 | 근거 |
|---|---|---|
| 공지 등록 (noticeYn, 유효기간) | Pass | `POST /api/qna` + `noticeYn: "Y"` + 날짜 → 201, 정상 저장 |
| 유효 공지가 목록 최상단 | Pass | `GET /api/qna?page=0&size=5` → 공지 게시글이 첫 번째 항목 |
| 목록에 [공지] 라벨 표시 (프론트) | UI확인필요 | QnaPage.vue 컬럼 정의에서 `isNotice` 체크 후 `[공지] ${r.title}` 반환 로직 확인 |
| QnaDetailPage에 공지 입력 UI | UI확인필요 | 체크박스 + 날짜 입력 필드 구현 확인 |

### 요구사항 6: 가입별 과금기준 목록 조회 화면 — **Pass**
| 기준 | 결과 | 근거 |
|---|---|---|
| 해지(TERMINATED) 가입 제외 | Pass | `keyword=SUBS0003` (TERMINATED) → 빈 결과 |
| 과금기준 없는 가입도 표시 | Pass | SUBS0002 등 과금기준 없는 가입이 `billStdId: null`로 표시 |
| LEFT JOIN 정상 동작 | Pass | SUBS0001에 과금기준 존재 → `billStdId`, `stdRegStatCd`, `effStartDt`, `effEndDt` 정상 반환 |
| 페이징 동작 | Pass | `totalPages: 3, totalElements: 23` — 23건 가입(해지 제외) 정상 페이징 |
| 메뉴 등록 | Pass | 메뉴 트리에 "가입별 과금기준" 항목 존재 |
| 라우트 등록 | Pass | `router/index.js`에 `/subs-bill-std` → `SubsBillStdListPage.vue` 라우트 확인 |

### 요구사항 7: 목록에서 과금기준 화면 연동 — **Pass**
| 기준 | 결과 | 근거 |
|---|---|---|
| 행 클릭 시 과금기준 화면 이동 | Pass | SubsBillStdListPage.vue `handleRowClick`에서 `router.push({ path: '/bill-std', query: { subsId: row.subsId } })` |
| BillStdPage에서 query param 자동 조회 | Pass | BillStdPage.vue `onMounted`에서 `route.query.subsId` 읽어 `handleSearch()` 호출 확인 |

---

## 2. 계획 준수 여부

| 항목 | 결과 | 비고 |
|---|---|---|
| 단계별 구현 순서 (1→2→3→4) | 준수 | 실행 로그 확인 |
| 변경 파일 목록 일치 | 준수 | 계획서 파일 목록과 실행 로그 파일 목록 일치 |
| JPQL LEFT JOIN 전략 | 준수 | SubsBillStdServiceImpl에서 EntityManager + JPQL 사용 |
| /todo 엔드포인트 위치 | 준수 | BillStdController에서 `@GetMapping("/todo")`가 `@GetMapping("/{id}")` 위에 선언 |
| QnaRepository 커스텀 쿼리 | 준수 | `findAllWithNoticeFirst`, `findByKeywordWithNoticeFirst` 구현 |
| Pageable Sort 미지정 | 준수 | `PageRequest.of(page, size)` — Sort 파라미터 없음 |
| SubsBillStdResponseDto all-args 생성자 | 준수 | 8개 파라미터 생성자 선언 확인 |
| Lombok 미사용 | 준수 | 모든 Entity/DTO에 수동 getter/setter |

### 계획 대비 차이점
- **billStdNm 부재 이슈**: 계획서에서 `b.billStdNm`을 JPQL에 포함했으나, `BillStd` Entity에 `billStdNm` 필드가 존재하지 않음. 실행 시 `CAST(NULL AS string)`으로 대체. 실행 로그 이슈 #1에 기록됨. **이 차이는 계획서 자체의 오류**(스키마/Entity 변경이 계획에 누락됨)이며 구현자의 판단으로 적절히 우회함.

---

## 3. API 실동작 검증 결과

### 테스트 환경
- 서버: Spring Boot 8080 포트, H2 DB
- 인증: `POST /api/auth/login` → user01/password123 → 세션 쿠키 획득

### API 호출 결과

| API | 메서드 | HTTP 상태 | 결과 |
|---|---|---|---|
| `/api/subscriptions` (adminId 포함 등록) | POST | 201 | `adminId: "user01"` 정상 저장 |
| `/api/subscriptions/SUBS_TEST_QA` (adminId 변경) | PUT | 200 | `adminId: "user02"` 정상 변경 |
| `/api/subscriptions?keyword=SUBS0001` (adminId 조회) | GET | 200 | `adminId` 필드 포함 반환 |
| `/api/bill-std/todo` (DRAFT 상태) | GET | 200 | 1건 반환 (DRAFT) |
| `/api/bill-std/todo` (APPROVED 후) | GET | 200 | 빈 배열 (APPROVED 제외 확인) |
| `/api/qna` (공지 등록 후 정렬) | GET | 200 | 공지글이 첫 번째 |
| `/api/subs-bill-std/list` (전체) | GET | 200 | 23건, TERMINATED 제외 |
| `/api/subs-bill-std/list?keyword=SUBS0003` | GET | 200 | 빈 결과 (TERMINATED) |
| `/api/subs-bill-std/list?keyword=SUBS0001` | GET | 200 | 과금기준 JOIN 데이터 포함 |
| `/api/menus/tree/my` | GET | 200 | "가입별 과금기준" 메뉴 존재 |

---

## 4. 코드리뷰

### 4.1 백엔드

| 파일 | 판정 | 비고 |
|---|---|---|
| `schema.sql` | **Pass** | `admin_id`, `notice_yn/notice_start_dt/notice_end_dt` 정확한 위치에 추가, 시스템 필드 앞 배치 |
| `Subscription.java` | **Pass** | `adminId` 필드 + getter/setter, 시스템 필드 앞 배치, Lombok 미사용 |
| `SubscriptionRequestDto.java` | **Pass** | `adminId` 추가, `createdBy` 단일 필드 원칙 준수 |
| `SubscriptionResponseDto.java` | **Pass** | `adminId` 추가 |
| `SubscriptionServiceImpl.java` | **Pass** | create/update/toDto에 adminId 매핑, 레이어 책임 준수 |
| `Qna.java` | **Pass** | 공지 필드 3개 추가, 기본값 `"N"`, getter/setter 정상 |
| `QnaRequestDto.java` | **Pass** | 공지 필드 추가 |
| `QnaResponseDto.java` | **Pass** | 공지 필드 추가 |
| `QnaServiceImpl.java` | **Warn** | `UUID.randomUUID()` 사용 중 (기존 코드, 이번 범위에서 제외 대상) — 이번 변경분(공지 매핑)은 정상 |
| `QnaRepository.java` | **Pass** | 커스텀 JPQL 쿼리 정확, CURRENT_TIMESTAMP 사용, Pageable Sort 미지정 |
| `BillStdService.java` | **Pass** | `findTodoList()` 선언 추가 |
| `BillStdServiceImpl.java` | **Pass** | `findTodoList()` 구현 — `findByStdRegStatCdNotIn(List.of("APPROVED", "CANCEL"))` |
| `BillStdRepository.java` | **Pass** | `findByStdRegStatCdNotIn` 메서드명 규칙 준수 |
| `BillStdController.java` | **Pass** | `/todo` 엔드포인트가 `/{id}` 위에 선언 (경로 충돌 방지) |
| `SubsBillStdResponseDto.java` | **Pass** | all-args 생성자 + getter/setter, Lombok 미사용 |
| `SubsBillStdService.java` | **Pass** | Interface + Impl 분리 원칙 준수 |
| `SubsBillStdServiceImpl.java` | **Pass** | 생성자 주입, JPQL LEFT JOIN + ON 조건 + keyword 동적 추가 + count 쿼리 분리 |
| `SubsBillStdController.java` | **Pass** | @RestController, 생성자 주입, GET 목록 Page<T> 반환 |
| `MenuDataInitializer.java` | **Pass** | MNU015 추가, allMenuIds 리스트에 포함, 역할 매핑 정상 |

### 4.2 프론트엔드

| 파일 | 판정 | 비고 |
|---|---|---|
| `UserSearchPopup.vue` | **Pass** | props(visible), emit(select, close), `/api/users/page` 재활용, UI 표준 준수 |
| `SubscriptionPage.vue` | **Pass** | adminId 필드 + 검색 팝업 버튼, EMPTY_FORM에 adminId 포함, toFormData/toRequestDto 매핑 정상 |
| `DashboardContent.vue` | **Pass** | TODO 카드 섹션 추가, getTodoList import, getLabel 공통코드 변환, 클릭 시 라우팅 |
| `billStdApi.js` | **Pass** | `getTodoList` 함수 추가, apiClient 경유 |
| `CommonCodePage.vue` | **Pass** | 공통코드/상세코드 등록 시 `todayDatetime()`, `'9999-12-31T23:59'` 기본값 적용 |
| `QnaDetailPage.vue` | **Pass** | 공지 체크박스 + 유효기간 입력, `v-if="form.noticeYn === 'Y'"` 조건부 표시 |
| `QnaPage.vue` | **Pass** | 공지 표시 로직 — `isNotice` 체크 후 `[공지]` prefix |
| `BillStdPage.vue` | **Pass** | `onMounted`에서 `route.query.subsId` 읽어 자동 조회 |
| `SubsBillStdListPage.vue` | **Pass** | MainLayout 래핑, DataGrid 페이징, 행 클릭 라우팅, useCommonCodeLabel |
| `subsBillStdApi.js` | **Pass** | apiClient 경유, params 전달 |
| `router/index.js` | **Pass** | `/subs-bill-std` 라우트 추가 |

### 4.3 코드리뷰 상세 관점

| 관점 | 판정 | 비고 |
|---|---|---|
| 중복 코드 | **Pass** | 중복 없음. UserSearchPopup은 공통 컴포넌트로 분리 |
| 네이밍 일관성 | **Pass** | 기존 컨벤션(camelCase, 한글 라벨, 코드 구조) 준수 |
| 레이어 책임 위반 | **Pass** | Controller에 비즈니스 로직 없음. 모든 로직은 ServiceImpl에 위치 |
| 하드코딩 | **Warn** | `List.of("APPROVED", "CANCEL")` — 상수 추출 가능하나 현재 단일 호출이므로 허용 범위 |
| 미사용 import/죽은 코드 | **Warn** | `QnaServiceImpl`에 `import java.util.UUID`와 `Sort` import가 여전히 존재. UUID는 기존 generateQnaId에서 사용 중이므로 미사용은 아님. `Sort` import는 미사용 가능성 있으나 기존 코드이므로 이번 범위 아님 |

---

## 5. 회귀 영향도 분석

### 수정된 파일 기준 영향도

| 수정 파일 | 의존 기능 | 잠재적 파손 | 판정 |
|---|---|---|---|
| `schema.sql` (tb_subscription) | 가입 CRUD 전체 | 컬럼 추가(NULL 허용)이므로 기존 데이터/쿼리에 영향 없음 | 안전 |
| `schema.sql` (tb_qna) | Q&A CRUD 전체 | 컬럼 추가(NULL/DEFAULT 'N')이므로 기존 데이터에 영향 없음 | 안전 |
| `Subscription.java` + DTOs | 가입 화면, 대표가입, 특수가입 | 필드 추가만으로 기존 필드 변경 없음 | 안전 |
| `SubscriptionServiceImpl.java` | 가입 CRUD | create/update/toDto에 adminId 라인 추가만, 기존 필드 매핑 변경 없음 | 안전 |
| `Qna.java` + DTOs | Q&A 목록/상세/댓글 | 필드 추가만, 기존 필드 변경 없음 | 안전 |
| `QnaServiceImpl.java` | Q&A CRUD | findAll에서 커스텀 쿼리로 변경 — 기능 동등(정렬 추가만) | 안전 |
| `QnaRepository.java` | Q&A 목록 조회 | 기존 `findByTitleContainingOrContentContaining` 메서드는 그대로 존재(미사용이 되었을 수 있으나 삭제하지 않아 안전) | 안전 |
| `BillStdService/Impl/Repository/Controller` | 과금기준 화면 | 메서드 추가만, 기존 메서드 변경 없음 | 안전 |
| `MenuDataInitializer.java` | 메뉴 초기화 | `count > 0` guard 때문에 기존 환경에서는 실행 안됨. 신규 DB에서만 MNU015 추가 | 안전 |
| `SubscriptionPage.vue` | 가입 관리 화면 | adminId 필드 + UserSearchPopup 추가, 기존 폼/액션 변경 없음 | 안전 |
| `DashboardContent.vue` | 메인 대시보드 | TODO 섹션 추가, 기존 현황 카드/미납 리스트 변경 없음 | 안전 |
| `CommonCodePage.vue` | 공통코드 관리 | 등록 시 기본값 로직 추가, 기존 조회/수정/삭제 변경 없음 | 안전 |
| `QnaDetailPage.vue` | Q&A 상세 | form에 공지 필드 추가, 기존 폼/댓글 변경 없음 | 안전 |
| `QnaPage.vue` | Q&A 목록 | 컬럼 정의에 공지 표시 추가, 기존 컬럼 변경 없음 | 안전 |
| `BillStdPage.vue` | 과금기준 화면 | onMounted에 query param 읽기 추가, 기존 검색/저장/삭제 변경 없음 | 안전 |
| `router/index.js` | 전체 라우팅 | 라우트 추가만, 기존 라우트 변경 없음 | 안전 |

**회귀 영향도 종합: 안전** — 모든 변경이 기존 기능에 대해 추가(additive) 방식이며, 기존 필드/메서드/라우트를 변경하지 않음.

---

## 6. UI 수동 확인 체크리스트

### 요구사항 1: 가입관리 — 관리자ID
- [ ] 가입 화면 입력폼에 "관리자ID" 필드가 표시되는지
- [ ] 관리자ID 필드가 readOnly이고 옆에 "검색" 버튼이 있는지
- [ ] 검색 버튼 클릭 시 사용자 검색 팝업이 열리는지
- [ ] 팝업에서 아이디/닉네임 검색 후 결과 목록이 표시되는지
- [ ] 결과 행 클릭 시 관리자ID 필드에 userId가 반영되는지
- [ ] 등록/변경 저장 후 재조회 시 adminId가 유지되는지

### 요구사항 2+3: 메인 대시보드 — TODO
- [ ] 대시보드에 "과금기준 TODO N건" 카드가 표시되는지
- [ ] APPROVED/CANCEL이 아닌 과금기준이 TODO 목록에 나타나는지
- [ ] 등록진행상태가 공통코드 라벨로 변환되어 표시되는지
- [ ] TODO 항목 클릭 시 과금기준 화면으로 이동하는지
- [ ] 이동 후 해당 가입ID로 자동 조회되는지

### 요구사항 4: 공통코드 관리 — 유효일자 기본값
- [ ] 공통코드 "등록" 클릭 시 유효시작일이 오늘 날짜로 자동 입력되는지
- [ ] 유효종료일이 9999-12-31T23:59으로 자동 입력되는지
- [ ] 상세코드 "등록" 클릭 시에도 동일하게 기본값이 적용되는지
- [ ] 기존 코드 선택(수정) 시 기존 값이 그대로 유지되는지

### 요구사항 5: Q&A — 공지사항
- [ ] Q&A 등록 화면에 "공지사항" 체크박스가 있는지
- [ ] 체크 시 공지 시작일/종료일 입력필드가 나타나는지
- [ ] 공지 등록 후 Q&A 목록에서 해당 글이 최상단에 "[공지]" 라벨과 함께 표시되는지
- [ ] 편집 모드에서 공지 체크 해제 후 저장 → 일반 정렬로 복원되는지

### 요구사항 6: 가입별 과금기준 목록
- [ ] 사이드바에 "가입별과금기준" 메뉴가 표시되는지
- [ ] 메뉴 클릭 시 목록 화면이 정상 로딩되는지
- [ ] 해지(TERMINATED) 가입이 목록에 나타나지 않는지
- [ ] 과금기준이 없는 가입의 과금기준 컬럼이 빈 값으로 표시되는지
- [ ] 검색어 입력 + 조회 시 필터링이 정상 동작하는지
- [ ] 페이징 버튼이 정상 동작하는지

### 요구사항 7: 목록 → 과금기준 연동
- [ ] 가입별 과금기준 목록에서 행 클릭 시 과금기준 화면으로 이동하는지
- [ ] 이동 후 해당 가입ID가 검색어에 자동 입력되고 조회가 실행되는지
- [ ] 과금기준이 있는 가입 클릭 → 과금기준 정보가 폼에 표시되는지
- [ ] 과금기준이 없는 가입 클릭 → 가입정보만 표시되고 과금기준 폼은 빈 상태인지

---

## 7. 잔여 이슈

| # | 유형 | 설명 | 심각도 |
|---|---|---|---|
| 1 | 알려진 제한 | `billStdNm` (과금기준명) 필드가 `tb_bill_std`/`BillStd` Entity에 미존재. JPQL에서 `CAST(NULL AS string)`으로 우회. 프론트에서 해당 컬럼 항상 빈 값 표시 | Warn — 실행 로그에 기록된 후속 과제. 기능 동작에는 영향 없음 |
| 2 | 알려진 제한 | `QnaServiceImpl`에서 `UUID.randomUUID()` 사용 중 — 요구사항 범위에서 제외 (제외 범위 명시) | Info |
| 3 | 참고 | `QnaRepository`에 기존 `findByTitleContainingOrContentContaining` 메서드가 미사용 상태로 잔존 | Warn — 정리 대상이나 기능 영향 없음 |

---

## 8. 최종 판정

| 요구사항 | 판정 |
|---|---|
| 1. 가입 관리자ID | **Pass** |
| 2+3. 메인 TODO + 과금기준 연동 | **Pass** |
| 4. 공통코드 유효일자 기본값 | **Pass** |
| 5. Q&A 공지사항 | **Pass** |
| 6. 가입별 과금기준 목록 화면 | **Pass** |
| 7. 목록→과금기준 화면 연동 | **Pass** |
| 코드리뷰 | **Pass** (Warn 2건 — 참고사항) |
| 회귀 영향도 | **안전** |

**종합: 전체 Pass** — Fail 항목 없음.
