# 02. 확정 요구사항 명세

## 1. 기능 명세

### [A] 대표가입 관리 화면 버그 수정

| # | 항목 | 상세 |
|---|---|---|
| A-1 | 서비스명 미표시 | `SubscriptionMainList`의 서비스코드 컬럼에 SVC_MAP 역방향 매핑 적용하여 서비스명으로 표시 |
| A-2 | 헤더 고정 이탈 | `MainLayout` 헤더 영역에 `sticky top-0 z-10` 적용. 스크롤 시 헤더(시스템명+로그아웃 버튼)가 항상 상단 고정 |
| A-3 | 상세폼 3열 미달 | `SubscriptionMainForm`의 상세 입력 폼을 `grid-cols-6` (레이블+값 × 3쌍) 구조로 변경 |
| A-4 | 목록 경계선 없음 | 모든 `List` 컴포넌트의 tbody tr에 `border-b border-gray-200` 적용, thead/tbody 상단에 `border-t border-gray-300` 적용 |
| A-5 | 컬럼 폭 변경 | th 우측에 드래그 핸들 추가, `onMouseDown/Move/Up` 이벤트로 columnWidths state 관리. 대표가입 관리 화면 목록에 우선 적용 |

### [B] 사용자 관리 화면 버그 수정

| # | 항목 | 상세 |
|---|---|---|
| B-1 | 검색영역 추가 | `UserPage` 상단에 조회영역 추가. 조회 필드: 사용자ID(equals), 사용자명(LIKE), 이메일(LIKE). 조회 버튼 클릭 시 API 호출 |
| B-2 | 버튼영역 고정 | `UserPage`의 저장/취소 버튼을 `ActionBar` 패턴(화면 하단 플로팅 바)으로 이동 |

### [C] 모든 화면 공통 버그 수정

| # | 항목 | 상세 |
|---|---|---|
| C-1 | UI 표준 적용 | 모든 List/Form 컴포넌트에 CLAUDE.md 표준 적용: 행 높이 `h-8`, 폰트 `text-sm`, 줄간격 `leading-tight` |
| C-2 | 폼 3열 통일 | 상세 폼 영역이 2열로 구성된 컴포넌트를 `grid-cols-6` (레이블+값 × 3쌍) 구조로 통일. 대상: `SubscriptionMainForm` |
| C-3 | created_by/updated_by | 프론트엔드: `useAuth()` hook의 `user.userId`를 API 호출 시 전달. 백엔드: `UserServiceImpl`의 register 메서드에서 `HttpSession`으로 로그인 사용자 ID 추출 |
| C-4 | 목록 높이 고정 | 모든 List 컴포넌트의 tbody 영역을 `max-h-[40rem] overflow-y-auto`로 고정 (약 10행 기준) |

### [D] 공통코드 관리 (신규)

#### D-1. 데이터베이스 (테이블 2개 신규)

**tb_common_code (헤더)**
| 컬럼 | 타입 | 설명 |
|---|---|---|
| common_code | VARCHAR(50) PK | 공통코드 (그룹코드) |
| common_code_nm | VARCHAR(100) | 공통코드명 |
| eff_start_dt | TIMESTAMP | 유효시작일시 |
| eff_end_dt | TIMESTAMP | 유효종료일시 (기본: 9999-12-31 23:59:59) |
| remark | VARCHAR(500) | 비고 |
| created_by | VARCHAR(50) | 등록자 |
| updated_by | VARCHAR(50) | 수정자 |
| created_at | TIMESTAMP | 등록일시 |
| updated_at | TIMESTAMP | 수정일시 |

**tb_common_dtl_code (디테일)**
| 컬럼 | 타입 | 설명 |
|---|---|---|
| common_code | VARCHAR(50) PK, FK | 공통코드 (tb_common_code 참조) |
| common_dtl_code | VARCHAR(50) PK | 공통상세코드 |
| common_dtl_code_nm | VARCHAR(100) | 공통상세코드명 |
| sort_order | INTEGER | 정렬순서 |
| eff_start_dt | TIMESTAMP | 유효시작일시 |
| eff_end_dt | TIMESTAMP | 유효종료일시 (기본: 9999-12-31 23:59:59) |
| remark | VARCHAR(500) | 비고 |
| created_by | VARCHAR(50) | 등록자 |
| updated_by | VARCHAR(50) | 수정자 |
| created_at | TIMESTAMP | 등록일시 |
| updated_at | TIMESTAMP | 수정일시 |

#### D-2. 백엔드 API

| 엔드포인트 | 설명 |
|---|---|
| GET /api/common-codes | 공통코드 그룹 목록 조회 |
| POST /api/common-codes | 공통코드 그룹 등록 |
| PUT /api/common-codes/{commonCode} | 공통코드 그룹 수정 |
| DELETE /api/common-codes/{commonCode} | 공통코드 그룹 삭제 |
| GET /api/common-codes/{commonCode}/details | 공통상세코드 목록 조회 |
| POST /api/common-codes/{commonCode}/details | 공통상세코드 등록 |
| PUT /api/common-codes/{commonCode}/details/{dtlCode} | 공통상세코드 수정 |
| DELETE /api/common-codes/{commonCode}/details/{dtlCode} | 공통상세코드 삭제 |

#### D-3. 화면 구성 (마스터-디테일 레이아웃)

```
┌──────────────────────────────────────────────────────────┐
│ 조회조건: [공통코드] [공통코드명]  [조회]                   │
├────────────────────┬─────────────────────────────────────┤
│  [공통코드 목록]    │  [공통상세코드 목록]                  │
│  공통코드 │ 코드명  │  상세코드 │ 상세코드명 │ 정렬 │ 유효│
│  ──────── │ ─────  │  ──────── │ ────────── │ ──── │ ── │
│  SVC_TYPE │서비스.. │  01       │ 기본서비스  │ 1    │   │
│  STATUS   │상태코..  │  02       │ 프리미엄   │ 2    │   │
│  [등록]             │                    [등록] [수정] [삭제]│
└────────────────────┴─────────────────────────────────────┘
```

- 좌측 공통코드 선택 시 우측 상세코드 목록 자동 갱신
- 마우스 드래그 컬럼 리사이즈 적용 (양측 목록 모두)
- 라우트: `/code`, 사이드바 '공통코드관리' 링크 활성화

### [E] Q&A 게시판 (신규)

#### E-1. 데이터베이스

**tb_qna (질문 게시글)**
| 컬럼 | 타입 | 설명 |
|---|---|---|
| qna_id | VARCHAR(50) PK | QNA+타임스탬프 ID |
| title | VARCHAR(200) | 제목 |
| content | TEXT | 내용 |
| view_cnt | INTEGER | 조회수 (기본 0) |
| answer_yn | VARCHAR(1) | 답변여부 Y/N (기본 N) |
| created_by | VARCHAR(50) | 등록자 |
| updated_by | VARCHAR(50) | 수정자 |
| created_at | TIMESTAMP | 등록일시 |
| updated_at | TIMESTAMP | 수정일시 |

**tb_qna_comment (답변/댓글)**
| 컬럼 | 타입 | 설명 |
|---|---|---|
| comment_id | VARCHAR(50) PK | CMT+타임스탬프 ID |
| qna_id | VARCHAR(50) FK | 질문 ID |
| parent_comment_id | VARCHAR(50) | 부모댓글ID (null=최상위) |
| content | TEXT | 내용 |
| created_by | VARCHAR(50) | 등록자 |
| updated_by | VARCHAR(50) | 수정자 |
| created_at | TIMESTAMP | 등록일시 |
| updated_at | TIMESTAMP | 수정일시 |

#### E-2. 백엔드 API

| 엔드포인트 | 설명 |
|---|---|
| GET /api/qna | 게시글 목록 (페이지네이션, 제목/내용 검색) |
| POST /api/qna | 게시글 등록 |
| GET /api/qna/{qnaId} | 게시글 상세 (조회수 +1) |
| PUT /api/qna/{qnaId} | 게시글 수정 |
| DELETE /api/qna/{qnaId} | 게시글 삭제 |
| GET /api/qna/{qnaId}/comments | 댓글 목록 |
| POST /api/qna/{qnaId}/comments | 댓글 등록 |
| DELETE /api/qna/{qnaId}/comments/{commentId} | 댓글 삭제 |

#### E-3. 화면 구성

**목록 화면** `/qna`
- 조회조건: 검색어(제목+내용 LIKE), 검색 버튼
- 목록 컬럼: 번호, 제목(클릭→상세), 작성자, 답변여부, 조회수, 등록일
- 페이지네이션: 10건/페이지, 페이지 번호 버튼
- 등록 버튼: 등록 폼으로 이동

**상세/등록/수정 화면** `/qna/:id` / `/qna/new`
- 폼: 제목, 내용(textarea)
- 하단: 댓글/답변 목록 + 댓글 입력 영역
- ActionBar: 저장/수정/삭제/목록 버튼

### [F] 좌측 메뉴 개선

| # | 항목 | 변경 내용 |
|---|---|---|
| F-1 | Main 메뉴 추가 | MENU 배열 최상단에 `{ label: 'Main', to: '/main', icon: HomeIcon }` 추가 |
| F-2 | 그룹명 글자크기 | `text-xs` → `text-sm font-semibold` 로 변경 |

---

## 2. 비기능 요건

| 항목 | 내용 |
|---|---|
| 응답 포맷 | GlobalExceptionHandler 표준 JSON `{ errors, message }` 유지 |
| 인증 | 세션 기반 유지. created_by/updated_by는 `HttpSession`의 `UserSessionDto.userId` 사용 |
| 패키지 구조 | 단일 패키지 구조 유지 (`com.example.vibestudy`) |
| Lombok 금지 | Entity/DTO 명시적 getter/setter 필수 |
| DB 관리 | 신규 테이블 DDL은 `schema.sql`에 `CREATE TABLE IF NOT EXISTS`로 추가 |

---

## 3. 수용 기준 (Acceptance Criteria)

- [ ] 대표가입 목록에서 서비스코드 대신 서비스명이 표시된다
- [ ] 페이지 스크롤 시 상단 헤더(시스템명+로그아웃)가 고정된다
- [ ] 상세 폼 영역이 레이블+값 3쌍씩 3열로 배치된다
- [ ] 모든 목록에 행 경계선이 표시된다
- [ ] 대표가입 목록 컬럼 헤더를 드래그하여 폭을 조절할 수 있다
- [ ] 사용자 관리 화면 상단에 ID/이름/이메일 조회영역이 표시된다
- [ ] 모든 화면의 버튼 영역이 하단 플로팅 ActionBar로 고정된다
- [ ] 모든 목록/폼에 h-8 행 높이, text-sm 폰트가 적용된다
- [ ] 신규 레코드의 created_by/updated_by에 로그인 사용자 ID가 저장된다
- [ ] 모든 목록이 최대 10행 높이로 고정되고 초과 시 세로 스크롤된다
- [ ] /code 경로에서 공통코드 그룹 목록과 상세코드 목록이 좌우 분할 표시된다
- [ ] 공통코드 그룹 선택 시 우측 상세코드 목록이 갱신된다
- [ ] /qna 목록에서 제목/내용 검색 및 페이지네이션이 동작한다
- [ ] Q&A 상세 화면에서 댓글 등록/조회가 가능하다
- [ ] 사이드바 Main 메뉴 클릭 시 /main으로 이동한다
- [ ] 사이드바 메뉴 그룹명 글자크기가 기존보다 크게 표시된다

---

## 4. 제외 범위

- 파일 첨부 기능 (Q&A 게시판)
- 공통코드 export/import 기능
- 컬럼 리사이즈 상태 localStorage 영속화
- 권한별 메뉴 노출 제어

---

## 5. 용어 정의

| 한국어 | 영어(코드) | DB 테이블 | 비고 |
|---|---|---|---|
| 공통코드 | CommonCode | `tb_common_code` | 코드 그룹 단위 |
| 공통상세코드 | CommonDtlCode | `tb_common_dtl_code` | 코드 그룹 내 개별 값 |
| Q&A 게시판 | Qna | `tb_qna` | 질문+답변 게시판 |
| Q&A 댓글 | QnaComment | `tb_qna_comment` | 게시글 내 댓글/답변 |
