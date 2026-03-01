-- ================================================================
-- 테이블명 : TB_SUBSCRIPTION (가입)
-- 설명     : 서비스 가입 계약 단위 관리
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_subscription
(
    /* ── Key ─────────────────────────────────────────────────── */
    subs_id                 VARCHAR(50)    NOT NULL,               -- 가입ID (사용자 직접 입력)

    /* ── 기본 정보 ────────────────────────────────────────────── */
    subs_nm                 VARCHAR(100)   NULL,                   -- 가입자명
    svc_nm                  VARCHAR(100)   NULL,                   -- 서비스명
    fee_prod_nm             VARCHAR(100)   NULL,                   -- 요금상품명
    subs_status_cd          VARCHAR(20)    NULL,                   -- 가입상태코드 (ACTIVE/SUSPENDED/TERMINATED/PENDING)
    subs_dt                 TIMESTAMP      NULL,                   -- 가입일시 (비즈니스)
    chg_dt                  TIMESTAMP      NULL,                   -- 변경일시 (비즈니스)

    /* ── System Fields ───────────────────────────────────────── */
    created_by              VARCHAR(50)    NOT NULL,               -- 생성자ID
    created_dt              TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,                   -- 수정자ID
    updated_dt              TIMESTAMP      NULL,

    CONSTRAINT pk_tb_subscription PRIMARY KEY (subs_id)
);

-- ================================================================
-- 테이블명 : TB_BILL_STD (과금기준)
-- 설명     : 가입 건별 전력 과금 산정에 필요한 기준 정보 관리
-- 비고     : study_log 테이블은 JPA @Entity 자동 생성으로 관리
-- ================================================================

CREATE TABLE IF NOT EXISTS tb_bill_std
(
    /* ── Key ─────────────────────────────────────────────────── */
    bill_std_id             VARCHAR(20)    NOT NULL,               -- 과금기준ID
    subs_id                 VARCHAR(20)    NOT NULL,               -- 가입ID

    /* ── 등록·유효 제어 ──────────────────────────────────────── */
    bill_std_reg_dt         TIMESTAMP      NULL,                   -- 과금기준등록일시
    svc_cd                  VARCHAR(10)    NULL,                   -- 서비스코드
    last_eff_yn             CHAR(1)        DEFAULT 'Y',            -- 최종유효여부
    eff_start_dt            TIMESTAMP      NULL,                   -- 유효시작일시
    eff_end_dt              TIMESTAMP      DEFAULT '9999-12-31 23:59:59',  -- 유효종료일시

    /* ── 상태 코드 ───────────────────────────────────────────── */
    std_reg_stat_cd         VARCHAR(10)    NULL,                   -- 과금기준등록진행상태코드
    bill_std_stat_cd        VARCHAR(10)    NULL,                   -- 과금기준상태코드

    /* ── 과금 산정 방식 ──────────────────────────────────────── */
    pwr_met_calc_meth_cd    VARCHAR(10)    NULL,                   -- 전력종량계산방식코드
    uprc_det_meth_cd        VARCHAR(10)    NULL,                   -- 단가결정방식코드
    metering_unit_price_amt NUMERIC(18,4)  NULL,                   -- 종량단가
    bill_qty                NUMERIC(18,4)  NULL,                   -- 과금량갯수

    /* ── PUE ─────────────────────────────────────────────────── */
    pue_det_meth_cd         VARCHAR(10)    NULL,                   -- PUE결정방식코드
    pue1_rt                 NUMERIC(18,4)  NULL,                   -- PUE1
    pue2_rt                 NUMERIC(18,4)  NULL,                   -- PUE2

    /* ── 할인·손실 ───────────────────────────────────────────── */
    first_dsc_rt            NUMERIC(18,4)  NULL,                   -- 1차할인율
    second_dsc_rt           NUMERIC(18,4)  NULL,                   -- 2차할인율
    loss_comp_rt            NUMERIC(18,4)  NULL,                   -- 손실보상율

    /* ── 약정·정산 ───────────────────────────────────────────── */
    cntrc_cap_kmh           NUMERIC(18,4)  NULL,                   -- 약정용량(Kwh)
    cntrc_amt               NUMERIC(18,2)  NULL,                   -- 약정요금
    dsc_amt                 NUMERIC(18,2)  NULL,                   -- 할인액
    daily_unit_price        NUMERIC(18,4)  NULL,                   -- 일별단가

    /* ── System Fields ───────────────────────────────────────── */
    created_by              VARCHAR(50)    NOT NULL,               -- 생성자ID
    created_dt              TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 생성일시
    updated_by              VARCHAR(50)    NULL,                   -- 수정자ID
    updated_dt              TIMESTAMP      NULL,                   -- 수정일시

    CONSTRAINT pk_tb_bill_std PRIMARY KEY (bill_std_id)
    ,CONSTRAINT fk_tb_bill_std_subs FOREIGN KEY (subs_id) REFERENCES tb_subscription (subs_id)
);

-- ── 복합 인덱스 : 가입ID + 유효기간 범위 검색 최적화 ──────────
CREATE INDEX IF NOT EXISTS idx_tb_bill_std_subs_eff
    ON tb_bill_std (subs_id, eff_start_dt, eff_end_dt);
