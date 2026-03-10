# [과금기준 동적필드 관리] 통합 개발 요구사항

## 1. 개요 (Overview)
- **목적**: 서비스코드별로 관리해야 하는 과금기준 필드를 Config로 정의하고, 가입별 과금기준 화면에서 가입의 서비스코드에 따라 동적으로 입력 폼을 구성하는 시스템 구축
- **개발 범위**:
  - 신규 테이블 2개 (tb_bill_std_field_config, tb_bill_std_field_value)
  - 신규 화면 1개 (서비스별 과금기준 Config 관리)
  - 기존 화면 수정 1개 (과금기준 관리 - 동적 폼 영역 추가)
  - 기존 테이블 수정 1개 (tb_bill_std에서 서비스별 고정 컬럼 제거, 공통 구조 필드만 유지)

## 2. 데이터 구조 (Data Models)

### 2.1. tb_bill_std_field_config (서비스별 과금기준 필드 설정) — 신규
- **유형/관계**: 신규. 서비스코드별로 어떤 과금기준 필드를 사용할지 정의하는 메타 테이블.
- **항목 명세**:
  | 항목명 | 타입/Key | 필수 | 설명 및 기본값 |
  |---|---|---|---|
  | svc_cd | VARCHAR(10), PK1 | Y | 서비스코드 |
  | field_cd | VARCHAR(50), PK2 | Y | 필드코드 (예: pue1_rt, cntrc_cap_kmh) |
  | eff_start_dt | TIMESTAMP, PK3 | Y | 유효시작일시 |
  | eff_end_dt | TIMESTAMP | N | 유효종료일시 (기본값: 9999-12-31 23:59:59) |
  | field_nm | VARCHAR(100) | N | 필드명 (화면 표시용) |
  | field_type | VARCHAR(20) | N | 필드 타입 (TEXT, NUMBER, SELECT, DATE 등) |
  | required_yn | CHAR(1) | N | 필수여부 (기본값: N) |
  | sort_order | INTEGER | N | 정렬순서 (기본값: 0) |
  | common_code | VARCHAR(50) | N | SELECT 타입일 때 참조할 공통코드 |
  | default_value | VARCHAR(200) | N | 기본값 |
  | created_by | VARCHAR(50) | Y | 생성자ID |
  | created_dt | TIMESTAMP | Y | 생성일시 (기본값: CURRENT_TIMESTAMP) |
  | updated_by | VARCHAR(50) | N | 수정자ID |
  | updated_dt | TIMESTAMP | N | 수정일시 |

### 2.2. tb_bill_std_field_value (과금기준 필드값) — 신규
- **유형/관계**: 신규. tb_bill_std의 동적 필드 값을 EAV 방식으로 저장. bill_std_id → tb_bill_std(FK)
- **항목 명세**:
  | 항목명 | 타입/Key | 필수 | 설명 및 기본값 |
  |---|---|---|---|
  | bill_std_id | VARCHAR(20), PK1 | Y | 과금기준ID (FK → tb_bill_std) |
  | field_cd | VARCHAR(50), PK2 | Y | 필드코드 |
  | field_value | VARCHAR(500) | N | 필드값 |
  | created_by | VARCHAR(50) | Y | 생성자ID |
  | created_dt | TIMESTAMP | Y | 생성일시 (기본값: CURRENT_TIMESTAMP) |
  | updated_by | VARCHAR(50) | N | 수정자ID |
  | updated_dt | TIMESTAMP | N | 수정일시 |

### 2.3. tb_bill_std (과금기준) — 수정
- **유형/관계**: 기존 테이블 수정. 서비스별 고정 컬럼(~15개)을 제거하고 공통 구조 필드만 유지.
- **유지할 공통 필드**: bill_std_id, subs_id, bill_std_reg_dt, svc_cd, last_eff_yn, eff_start_dt, eff_end_dt, std_reg_stat_cd, bill_std_stat_cd + 시스템 필드
- **제거할 서비스별 필드**: pwr_met_calc_meth_cd, uprc_det_meth_cd, metering_unit_price_amt, bill_qty, pue_det_meth_cd, pue1_rt, pue2_rt, first_dsc_rt, second_dsc_rt, loss_comp_rt, cntrc_cap_kmh, cntrc_amt, dsc_amt, daily_unit_price

## 3. 화면 및 UI 구성 (Screens & UI)

### 3.1. 서비스별 과금기준 Config 관리 화면 (신규)
- **레이아웃**: 표준 레이아웃 (조회 → 그리드 → 입력폼 → 플로팅 액션바)
- **영역별 구성 요소**:
  - **조회영역**: 서비스코드(공통코드 SELECT), 필드코드(TEXT), 조회 버튼
  - **그리드**: 서비스코드, 필드코드, 필드명, 필드타입, 필수여부, 정렬순서, 유효시작일, 유효종료일
  - **입력폼**: 서비스코드(공통코드 SELECT), 필드코드, 필드명, 필드타입(SELECT: TEXT/NUMBER/SELECT/DATE), 필수여부(Y/N), 정렬순서, 공통코드(field_type=SELECT일 때만), 기본값, 유효시작일시, 유효종료일시
  - **플로팅 액션바**: 등록, 변경, 삭제 버튼

### 3.2. 과금기준 관리 화면 (기존 수정)
- **레이아웃**: 기존 레이아웃에 동적 폼 영역 추가
- **변경 사항**:
  - 가입 선택 시 → 해당 가입의 서비스코드로 tb_bill_std_field_config에서 현재 유효한 Config 조회
  - 조회된 Config 목록에 따라 동적 입력 필드 렌더링
  - 저장 시 동적 필드 값은 tb_bill_std_field_value에 저장
  - 조회 시 동적 필드 값을 tb_bill_std_field_value에서 로드하여 표시

## 4. 이벤트 및 비즈니스 로직 (Events & Business Logic)

### 4.1. Config 관리 화면 관련 로직
- **조회**: 서비스코드, 필드코드로 검색. 페이징 처리.
- **등록**: PK(svc_cd + field_cd + eff_start_dt) 중복 체크 후 등록.
- **변경**: PK 외 필드 수정 가능. PK는 readonly.
- **삭제**: 선택한 Config 삭제. tb_bill_std_field_value에 해당 field_cd 데이터 존재 시 삭제 불가(또는 경고).

### 4.2. 과금기준 관리 화면 관련 로직
- **가입 선택 시**: 가입의 svc_cd로 현재 유효한(eff_start_dt <= NOW < eff_end_dt) Config 목록 조회 → 동적 폼 렌더링
- **과금기준 저장 시**: 공통 필드는 tb_bill_std에, 동적 필드값은 tb_bill_std_field_value에 함께 저장
- **과금기준 조회 시**: tb_bill_std + tb_bill_std_field_value 조인하여 동적 필드값 로드

### 4.3. 유효기간 로직
- Config 조회(화면 렌더링용)는 현재 시점 기준 유효한 레코드만 필터링
- Config 관리 화면에서는 전체 이력(과거/미래 포함) 조회 가능

## 5. 기타 요구사항 (Misc & Non-Functional)
- 기존 tb_bill_std의 서비스별 고정 컬럼 데이터는 마이그레이션 고려 (data.sql에 Config 초기 데이터 추가)
- 신규 화면은 메뉴에 등록 필요
