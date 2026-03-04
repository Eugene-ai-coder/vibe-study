# 05. 실행 로그 (BE)

실행일시: 2026-03-02
대상 계획서: `04_final_plan_be.md`

---

## Step 1. schema.sql DDL 추가

- 파일: `src/main/resources/schema.sql`
- 신규 테이블 4개 DDL 추가 (기존 내용 뒤에 append)
  - `tb_common_code`
  - `tb_common_dtl_code` (+ 인덱스 `idx_tb_common_dtl_code_code`)
  - `tb_qna`
  - `tb_qna_comment` (+ 인덱스 `idx_tb_qna_comment_qna_id`)
- 결과: 완료

---

## Step 2. createdBy 'SYSTEM' 제거

### 2-1. RegisterRequestDto.java
- `createdBy` 필드 및 getter/setter 추가
- 결과: 완료

### 2-2. UserServiceImpl.java
- `user.setCreatedBy("SYSTEM")` → `user.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : "SYSTEM")` 변경
- 결과: 완료

### 2-3. AuthController.java
- `register()` 메서드에 `HttpSession session` 파라미터 추가
- 세션에서 로그인 사용자 조회 후 `dto.createdBy` 미설정 시 주입 로직 추가
- 결과: 완료

---

## Step 3. 공통코드 백엔드 구현

| 파일 | 결과 |
|---|---|
| `CommonCode.java` | 신규 생성 |
| `CommonDtlCodeId.java` | 신규 생성 |
| `CommonDtlCode.java` | 신규 생성 |
| `CommonCodeRepository.java` | 신규 생성 |
| `CommonDtlCodeRepository.java` | 신규 생성 |
| `CommonCodeRequestDto.java` | 신규 생성 |
| `CommonDtlCodeRequestDto.java` | 신규 생성 |
| `CommonCodeResponseDto.java` | 신규 생성 |
| `CommonDtlCodeResponseDto.java` | 신규 생성 |
| `CommonCodeService.java` | 신규 생성 |
| `CommonCodeServiceImpl.java` | 신규 생성 |
| `CommonCodeController.java` | 신규 생성 |

- 헤더 삭제 시 상세코드 존재하면 409 CONFLICT 예외 처리
- 복합 PK `@EmbeddedId` 방식 적용
- 결과: 완료

---

## Step 4. Q&A 백엔드 구현

| 파일 | 결과 |
|---|---|
| `Qna.java` | 신규 생성 |
| `QnaComment.java` | 신규 생성 |
| `QnaRepository.java` | 신규 생성 |
| `QnaCommentRepository.java` | 신규 생성 |
| `QnaRequestDto.java` | 신규 생성 |
| `QnaCommentRequestDto.java` | 신규 생성 |
| `QnaResponseDto.java` | 신규 생성 |
| `QnaCommentResponseDto.java` | 신규 생성 |
| `QnaService.java` | 신규 생성 |
| `QnaServiceImpl.java` | 신규 생성 |
| `QnaController.java` | 신규 생성 |

- ID 자동생성: `QNA{yyyyMMddHHmmssSSS}`, `CMT{yyyyMMddHHmmssSSS}`
- `findById` 호출 시 `view_cnt +1` 처리
- 페이징: `Page<QnaResponseDto>`, 생성일시 내림차순 정렬
- 결과: 완료

---

## Step 5. backend-rules.md ID 접두사 등록

- 파일: `docs/backend-rules.md`
- 섹션 4 테이블에 Q&A 게시글(`QNA`), Q&A 댓글(`CMT`) 접두사 추가
- 결과: 완료

---

## 이슈 사항

- 없음

---

## 변경 파일 요약

**수정 (3):** `schema.sql`, `RegisterRequestDto.java`, `UserServiceImpl.java`, `AuthController.java`, `backend-rules.md`
**신규 (23):** 공통코드 12개 + Q&A 11개

---

# 05. 실행 로그 (FE)

실행일시: 2026-03-02
대상 계획서: `04_final_plan_fe.md`

---

## Step 1. 공통 레이아웃 수정

### 1-1. MainLayout.jsx — 헤더 sticky 적용
- `header` 클래스에 `sticky top-0 z-10` 추가
- 결과: 완료

### 1-2. Sidebar.jsx — Main 메뉴, 공통코드 링크, 그룹명 폰트
- MENU 배열에 단독 `{ label: 'Main', to: '/main' }` 항목 추가
- `공통코드관리` to: null → `/code` 변경
- `게시판` 그룹 + `Q&A` 항목 추가
- 그룹명 폰트: `text-xs font-semibold text-gray-400` → `text-sm font-semibold text-gray-500`
- 단독 항목 렌더 로직 분리
- 결과: 완료

---

## Step 2. createdBy 'SYSTEM' 교체

| 파일 | 변경 내용 | 결과 |
|---|---|---|
| `SubscriptionMainPage.jsx` | `useAuth` import, `user` 선언, `createdBy: user?.userId ?? 'SYSTEM'` | 완료 |
| `BillStdPage.jsx` | `useAuth` import, `toRequestDto(form, userId)` 시그니처 변경, 호출부에 `user?.userId` 전달 | 완료 |
| `SubscriptionPage.jsx` | `useAuth` import, `toRequestDto(form, userId)` 시그니처 변경, 호출부에 `user?.userId` 전달 | 완료 |
| `UserPage.jsx` | `useAuth` import, `handleRegister(user?.userId)` 전달, `useUser.handleRegister(createdBy)` 시그니처 변경 | 완료 |

---

## Step 3. 대표가입 관리 화면 버그 수정

### 3-1. SVC_MAP 역방향 매핑 (SubscriptionMainPage.jsx)
- `SVC_LABEL_MAP = { '서비스1': 'IDC 전력', ... }` 선언
- `items.map(item => ({ ...item, svcNm: SVC_LABEL_MAP[item.svcNm] || item.svcNm }))` 변환 후 List에 전달
- 결과: 완료

### 3-2. SubscriptionMainList.jsx — 경계선, max-h, 컬럼 리사이즈
- `useState`/`useRef`/`useCallback` 기반 드래그 리사이즈 구현
- thead: `border-t border-gray-300 border-b border-gray-300`
- tbody: `max-h-[40rem] overflow-y-auto`
- tr: `border-b border-gray-200`
- 결과: 완료

### 3-3. SubscriptionMainForm.jsx — grid-cols-6 3열 구조
- `grid grid-cols-2` → `grid grid-cols-6`
- Row1: 가입ID | 대표가입여부 | 유효시작일시 (레이블+값 × 3쌍)
- Row2: 유효종료일시 | 대표가입ID (col-span-3)
- 결과: 완료

---

## Step 4. 사용자 관리 화면 버그 수정 (UserPage.jsx)

### 4-1. 검색영역 추가
- `searchUserId` / `searchNickname` / `searchEmail` 상태 추가
- `handleSearch`: 프론트 필터링 (이름 포함 검색)
- 조회 버튼 + Enter 키 지원
- 결과: 완료

### 4-2. ActionBar 패턴
- 폼 내 등록 버튼 div 제거
- 하단 fixed ActionBar(취소/등록) 추가
- `pb-20` 본문 여백 확보
- 결과: 완료

---

## Step 5. 공통코드 관리 신규 화면

| 파일 | 결과 |
|---|---|
| `api/commonCodeApi.js` | 신규 생성 |
| `hooks/useCommonCode.js` | 신규 생성 |
| `components/common-code/CommonCodeList.jsx` | 신규 생성 |
| `components/common-code/CommonDtlCodeList.jsx` | 신규 생성 |
| `components/common-code/CommonCodeForm.jsx` | 신규 생성 |
| `components/common-code/CommonDtlCodeForm.jsx` | 신규 생성 |
| `components/common-code/CommonCodeActionBar.jsx` | 신규 생성 |
| `pages/CommonCodePage.jsx` | 신규 생성 |
| `App.jsx` | `/code` 라우트 추가 |

- 마스터-디테일 단일 Page 구조 (codeMode/dtlMode)
- 저장 버튼: codeMode + dtlMode 동시 처리
- 결과: 완료

---

## Step 6. Q&A 게시판 신규 화면

| 파일 | 결과 |
|---|---|
| `api/qnaApi.js` | 신규 생성 |
| `hooks/useQna.js` | 신규 생성 |
| `components/qna/QnaList.jsx` | 신규 생성 |
| `components/qna/QnaForm.jsx` | 신규 생성 |
| `components/qna/QnaCommentList.jsx` | 신규 생성 |
| `components/qna/QnaCommentForm.jsx` | 신규 생성 |
| `components/qna/QnaActionBar.jsx` | 신규 생성 |
| `pages/QnaPage.jsx` | 신규 생성 |
| `pages/QnaDetailPage.jsx` | 신규 생성 |
| `App.jsx` | `/qna`, `/qna/new`, `/qna/:id` 라우트 추가 |

- `isNew` 분기로 등록/수정/상세 단일 컴포넌트 처리
- 페이지네이션: `content`/`totalPages` 응답 + 배열 응답 fallback 처리
- 댓글: `parentCommentId` 여부로 `pl-8` 들여쓰기
- 결과: 완료

---

## 이슈 사항

- 없음

---

## 변경 파일 요약

**수정 (9):**
- `components/common/MainLayout.jsx`
- `components/main/Sidebar.jsx`
- `components/subscription-main/SubscriptionMainList.jsx`
- `components/subscription-main/SubscriptionMainForm.jsx`
- `pages/SubscriptionMainPage.jsx`
- `pages/BillStdPage.jsx`
- `pages/SubscriptionPage.jsx`
- `pages/UserPage.jsx`
- `hooks/useUser.js`
- `App.jsx`

**신규 (17):**
- `api/commonCodeApi.js`, `api/qnaApi.js`
- `hooks/useCommonCode.js`, `hooks/useQna.js`
- `components/common-code/` 5개
- `components/qna/` 5개
- `pages/CommonCodePage.jsx`, `pages/QnaPage.jsx`, `pages/QnaDetailPage.jsx`
