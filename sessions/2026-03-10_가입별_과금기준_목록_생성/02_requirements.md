# 요구사항 확정서

## 요구사항 1: 가입 테이블에 관리자ID 추가
- `tb_subscription`에 `admin_id VARCHAR(50)` 컬럼 추가
- 가입 화면의 입력 폼에 관리자ID 필드 추가
- 돋보기 버튼 클릭 시 사용자 검색 팝업(UserSearchPopup) 표시
- 팝업에서 사용자 선택 시 `adminId`에 사용자ID 반영
- 기존 `/api/users/page` API 재활용

### 수용 기준
- 가입 등록/수정 시 관리자ID가 정상 저장/조회됨
- 사용자 검색 팝업에서 이름/ID 검색 후 선택 가능

---

## 요구사항 2+3: Main 페이지 TODO + 과금기준 연동
- DashboardContent에 TODO 카드 섹션 추가
- `GET /api/bill-std/todo` API 신규: `std_reg_stat_cd`가 `APPROVED`, `CANCEL`이 **아닌** 과금기준 목록 조회
- TODO 목록에 과금기준명, 가입ID, 등록진행상태 표시
- TODO 항목 클릭 시 과금기준 화면으로 이동 (선택적)

### 수용 기준
- 등록진행상태가 APPROVED/CANCEL이 아닌 과금기준이 TODO에 표시됨
- 승인 또는 반려된 건은 TODO에 나타나지 않음

---

## 요구사항 4: 공통코드 관리 유효일자 기본값
- 공통코드/상세코드 신규 등록 시:
  - `effStartDt` 값이 없으면 → 오늘 날짜 (YYYY-MM-DD 형식)
  - `effEndDt` 값이 없으면 → `9999-12-31`
- 기존 값이 있으면 기존 값 그대로 표시
- 프론트엔드(CommonCodePage.vue)에서만 처리

### 수용 기준
- 신규 등록 폼 진입 시 기본값이 자동 입력됨
- 기존 코드 수정 시 기존 값 유지

---

## 요구사항 5: Q&A 공지사항 기능
- `tb_qna`에 `notice_yn CHAR(1) DEFAULT 'N'`, `notice_start_dt`, `notice_end_dt` 컬럼 추가
- 공지사항 등록: QnaDetailPage에 공지사항 여부 체크박스 + 유효기간(시작일/종료일) 입력
- **유효한 공지사항 정의**: `notice_yn='Y'` AND 현재 날짜가 `notice_start_dt` ~ `notice_end_dt` 범위 내
- 목록 정렬: 유효한 공지사항 → 최상단 고정, 나머지는 기존 정렬(작성일 DESC)
- 목록에서 공지사항 행은 시각적으로 구분 (아이콘 또는 라벨)

### 수용 기준
- 공지사항으로 등록한 Q&A가 유효기간 내에 목록 최상단에 표시됨
- 유효기간 경과 시 일반 게시글로 정렬됨

---

## 요구사항 6: 가입별 과금기준 목록 조회 화면
- 신규 페이지: `SubsBillStdListPage.vue`
- **유효한 가입**: `subs_status_cd`가 해지(`TERMINATED`)가 **아닌** 가입
- Subscription LEFT JOIN BillStd로 조회 → 과금기준 없어도 가입정보 표시
- 표시 컬럼: 가입ID, 가입명, 가입상태, 과금기준명, 등록진행상태, 유효시작일/종료일
- 메뉴 등록 필요 (사이드바에 추가)
- 신규 API: `GET /api/subs-bill-std/list`

### 수용 기준
- 해지되지 않은 가입만 목록에 표시됨
- 과금기준이 없는 가입도 가입정보는 표시됨 (과금기준 컬럼은 빈 값)

---

## 요구사항 7: 가입별 과금기준 목록에서 과금기준 화면 연동
- 요구사항 6 목록에서 행 클릭 → `router.push({ path: '/bill-std', query: { subsId: row.subsId } })`
- BillStdPage에 `onMounted`에서 `route.query.subsId` 읽어 자동 조회 로직 추가
- SubscriptionPage.vue의 기존 query param 패턴 참고

### 수용 기준
- 목록에서 행 클릭 시 과금기준 화면이 해당 가입ID로 조회된 상태로 열림

---

## 제외 범위
- QnaServiceImpl의 UUID → 타임스탬프 ID 변경 (이번 범위 아님)
- 권한별 TODO 필터링 (현재 전체 사용자 공통)
- 과금기준 TODO의 알림 기능

## 비기능 요건
- 기존 코드 패턴/컨벤션 유지
- 페이징 적용 (목록 API)

## 용어 정의
- **유효한 공지사항**: `notice_yn='Y'` AND 현재 날짜가 유효기간 내
- **유효한 가입**: `subs_status_cd != 'TERMINATED'`
- **TODO 대상 과금기준**: `std_reg_stat_cd NOT IN ('APPROVED', 'CANCEL')`

---
## Summary (다음 단계 전달용)
- **핵심 기능**: (1)가입에 관리자ID+사용자팝업 (2)메인 TODO+과금기준 연동 (3)공통코드 유효일자 기본값 (4)Q&A 공지사항(유효기간 기반) (5)가입별 과금기준 목록 신규화면 (6)목록→과금기준 화면 연동
- **수용 기준**: 각 요구사항별 Pass/Fail 기준 명시됨. 핵심은 유효한 가입=해지 아님, TODO=APPROVED/CANCEL 아닌 것, 유효 공지=기간 내
- **제외 범위**: UUID→타임스탬프 변환, 권한별 TODO 필터, 알림 기능
- **비기능 요건**: 기존 패턴 유지, 페이징 적용
