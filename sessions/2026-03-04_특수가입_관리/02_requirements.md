# 2단계: 요구사항 확정 (Requirements)

## 1. 기능 명세

### 1.1 데이터 모델 — `tb_special_subscription`

| 항목명 (한) | 컬럼명 (DB) | Java 필드 | 타입 | Key/필수 | 설명 |
|---|---|---|---|---|---|
| 가입별과금기준ID | `subs_bill_std_id` | `subsBillStdId` | VARCHAR(20) | PK, 필수 | 과금기준 식별자 |
| 유효시작일 | `eff_sta_dt` | `effStaDt` | VARCHAR(8) | PK, 필수 | YYYYMMDD 형식 |
| 가입ID | `subs_id` | `subsId` | VARCHAR(20) | 필수 | 가입 식별자 |
| 서비스코드 | `svc_cd` | `svcCd` | VARCHAR(10) | | 서비스 구분 |
| 유효종료일 | `eff_end_dt` | `effEndDt` | VARCHAR(8) | | YYYYMMDD, 기본 '99991231' |
| 최종유효여부 | `last_eff_yn` | `lastEffYn` | VARCHAR(1) | | Y/N |
| 상태코드 | `stat_cd` | `statCd` | VARCHAR(10) | | 상태 구분 |
| 계약용량(kMh) | `cntrc_cap_kmh` | `cntrcCapKmh` | DECIMAL(18,4) | | |
| 계약금액 | `cntrc_amt` | `cntrcAmt` | DECIMAL(18,2) | | |
| 할인율 | `dsc_rt` | `dscRt` | DECIMAL(18,4) | | |
| 비고 | `rmk` | `rmk` | VARCHAR(500) | | 자유 입력 |
| 생성자 | `created_by` | `createdBy` | VARCHAR(50) | 필수 | 시스템 자동 |
| 생성일시 | `created_dt` | `createdDt` | TIMESTAMP | 필수 | 시스템 자동 |
| 수정자 | `updated_by` | `updatedBy` | VARCHAR(50) | | 시스템 자동 |
| 수정일시 | `updated_dt` | `updatedDt` | TIMESTAMP | | 시스템 자동 |

- **복합 PK**: `@EmbeddedId` 패턴 (`SpecialSubscriptionId` 클래스)
- **FK 없음**: `subs_bill_std_id`는 문자열 ID로만 관리 (tb_bill_std FK 미설정)

### 1.2 백엔드 API

| Method | URI | 설명 |
|---|---|---|
| GET | `/api/special-subscriptions` | 전건 조회 (페이징 없음) |
| GET | `/api/special-subscriptions?subsBillStdId=&subsId=` | 조건부 필터 조회 |
| GET | `/api/special-subscriptions/{subsBillStdId}/{effStaDt}` | 단건 조회 (복합 PK) |
| POST | `/api/special-subscriptions` | 신규 등록 |
| PUT | `/api/special-subscriptions/{subsBillStdId}/{effStaDt}` | 수정 |
| DELETE | `/api/special-subscriptions/{subsBillStdId}/{effStaDt}` | 삭제 |

### 1.3 화면 구성 — 특수가입 관리

**레이아웃**: 상단(조회조건) → 중간상단(목록 DataGrid) → 중간하단(입력/조회 폼) → 하단(FloatingActionBar)

#### 조회 영역 (SearchBar)
- 가입별과금기준ID (텍스트), 가입ID (텍스트), 조회 버튼

#### 목록 영역 (List — DataGrid)
- 컬럼: 가입별과금기준ID, 유효시작일, 가입ID, 서비스코드, 상태코드, 최종유효여부
- 행 클릭 → 하단 폼 바인딩

#### 입력/조회 폼 (Form)
- 전체 필드 표시 (시스템 필드 제외)
- PK 필드(가입별과금기준ID, 유효시작일): 신규 시 입력 가능, 수정 시 읽기전용

#### 액션바 (ActionBar — FloatingActionBar)
- 신규, 저장, 삭제 버튼

## 2. 비기능 요건
- 기존 BillStd/SubscriptionMain 패턴과 동일한 코드 구조
- 전건 목록 조회 (설정성 데이터, 페이징 불필요)
- `eff_sta_dt` YYYYMMDD 형식 검증

## 3. 수용 기준
- 특수가입 CRUD 정상 동작
- 목록 행 선택 → 폼 바인딩 동작
- 사이드바 메뉴에 "특수가입관리" 표시 및 라우팅

## 4. 제외 범위
- 이력화 로직 (최종유효 → 이력 전환) — 별도 요건 시 구현
- FK 제약 조건
- 테스트 코드 (기존 패턴에 테스트 없음)

## 5. 용어 정의

| 한국어 | 영어(코드) | DB 테이블 | 컬럼/필드 접두사 |
|---|---|---|---|
| 특수가입 | SpecialSubscription | `tb_special_subscription` | `spcl_subs_` / `spclSubs` |
