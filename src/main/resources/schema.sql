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

-- ================================================================
-- 테이블명 : TB_COMMON_CODE (공통코드 헤더)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_common_code
(
    common_code         VARCHAR(50)     NOT NULL,
    common_code_nm      VARCHAR(100)    NULL,
    eff_start_dt        TIMESTAMP       NULL,
    eff_end_dt          TIMESTAMP       DEFAULT '9999-12-31 23:59:59',
    remark              VARCHAR(500)    NULL,
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          TIMESTAMP       NULL,
    CONSTRAINT pk_tb_common_code PRIMARY KEY (common_code)
);

-- ================================================================
-- 테이블명 : TB_COMMON_DTL_CODE (공통상세코드)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_common_dtl_code
(
    common_code         VARCHAR(50)     NOT NULL,
    common_dtl_code     VARCHAR(50)     NOT NULL,
    common_dtl_code_nm  VARCHAR(100)    NULL,
    sort_order          INTEGER         DEFAULT 0,
    eff_start_dt        TIMESTAMP       NULL,
    eff_end_dt          TIMESTAMP       DEFAULT '9999-12-31 23:59:59',
    remark              VARCHAR(500)    NULL,
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          TIMESTAMP       NULL,
    CONSTRAINT pk_tb_common_dtl_code PRIMARY KEY (common_code, common_dtl_code),
    CONSTRAINT fk_common_dtl_code_hdr FOREIGN KEY (common_code) REFERENCES tb_common_code (common_code)
);

CREATE INDEX IF NOT EXISTS idx_tb_common_dtl_code_code
    ON tb_common_dtl_code (common_code, sort_order);

-- ================================================================
-- 테이블명 : TB_QNA (Q&A 게시글)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_qna
(
    qna_id              VARCHAR(50)     NOT NULL,
    title               VARCHAR(200)    NULL,
    content             TEXT            NULL,
    view_cnt            INTEGER         DEFAULT 0,
    answer_yn           VARCHAR(1)      DEFAULT 'N',
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          TIMESTAMP       NULL,
    CONSTRAINT pk_tb_qna PRIMARY KEY (qna_id)
);

-- ================================================================
-- 테이블명 : TB_QNA_COMMENT (Q&A 댓글)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_qna_comment
(
    comment_id          VARCHAR(50)     NOT NULL,
    qna_id              VARCHAR(50)     NOT NULL,
    parent_comment_id   VARCHAR(50)     NULL,
    content             TEXT            NULL,
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          TIMESTAMP       NULL,
    CONSTRAINT pk_tb_qna_comment PRIMARY KEY (comment_id),
    CONSTRAINT fk_qna_comment_qna FOREIGN KEY (qna_id) REFERENCES tb_qna (qna_id)
);

CREATE INDEX IF NOT EXISTS idx_tb_qna_comment_qna_id
    ON tb_qna_comment (qna_id);

-- ================================================================
-- 테이블명 : TB_SPECIAL_SUBSCRIPTION (특수가입)
-- 설명     : 가입별 특수 과금 기준 관리
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_special_subscription
(
    /* ── Key (복합 PK) ──────────────────────────────── */
    subs_bill_std_id        VARCHAR(20)    NOT NULL,               -- 가입별과금기준ID
    eff_sta_dt              VARCHAR(8)     NOT NULL,               -- 유효시작일 (YYYYMMDD)

    /* ── 기본 정보 ──────────────────────────────────── */
    subs_id                 VARCHAR(20)    NOT NULL,               -- 가입ID
    svc_cd                  VARCHAR(10)    NULL,                   -- 서비스코드
    eff_end_dt              VARCHAR(8)     DEFAULT '99991231',     -- 유효종료일 (YYYYMMDD)
    last_eff_yn             VARCHAR(1)     NULL,                   -- 최종유효여부
    stat_cd                 VARCHAR(10)    NULL,                   -- 상태코드

    /* ── 약정 정보 ──────────────────────────────────── */
    cntrc_cap_kmh           NUMERIC(18,4)  NULL,                   -- 계약용량(kMh)
    cntrc_amt               NUMERIC(18,2)  NULL,                   -- 계약금액
    dsc_rt                  NUMERIC(18,4)  NULL,                   -- 할인율

    /* ── 비고 ───────────────────────────────────────── */
    rmk                     VARCHAR(500)   NULL,                   -- 비고

    /* ── System Fields ──────────────────────────────── */
    created_by              VARCHAR(50)    NOT NULL,               -- 생성자ID
    created_dt              TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,                   -- 수정자ID
    updated_dt              TIMESTAMP      NULL,

    CONSTRAINT pk_tb_special_subscription PRIMARY KEY (subs_bill_std_id, eff_sta_dt)
);

CREATE INDEX IF NOT EXISTS idx_tb_special_subscription_subs
    ON tb_special_subscription (subs_id);

-- ================================================================
-- 테이블명 : TB_MENU (메뉴)
-- 설명     : 사이드바 메뉴 계층 구조 관리
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_menu
(
    menu_id             VARCHAR(20)    NOT NULL,               -- 메뉴ID
    menu_nm             VARCHAR(100)   NOT NULL,               -- 메뉴명
    menu_url            VARCHAR(200)   NULL,                   -- 메뉴URL
    parent_menu_id      VARCHAR(20)    NULL,                   -- 상위메뉴ID
    sort_order          INTEGER        DEFAULT 0,              -- 정렬순서
    use_yn              CHAR(1)        DEFAULT 'Y',            -- 사용여부
    menu_level          INTEGER        DEFAULT 1,              -- 메뉴레벨

    /* ── System Fields ──────────────────────────────── */
    created_by          VARCHAR(50)    NOT NULL,               -- 생성자ID
    created_dt          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,                   -- 수정자ID
    updated_dt          TIMESTAMP      NULL,

    CONSTRAINT pk_tb_menu PRIMARY KEY (menu_id),
    CONSTRAINT fk_tb_menu_parent FOREIGN KEY (parent_menu_id) REFERENCES tb_menu (menu_id)
);

CREATE INDEX IF NOT EXISTS idx_tb_menu_parent
    ON tb_menu (parent_menu_id, sort_order);

-- ================================================================
-- 테이블명 : TB_USER_ROLE (사용자역할)
-- 설명     : 사용자-역할 다대다 매핑
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_user_role
(
    user_id             VARCHAR(50)    NOT NULL,               -- 사용자ID
    role_cd             VARCHAR(20)    NOT NULL,               -- 역할코드

    /* ── System Fields ──────────────────────────────── */
    created_by          VARCHAR(50)    NOT NULL,               -- 생성자ID
    created_dt          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,                   -- 수정자ID
    updated_dt          TIMESTAMP      NULL,

    CONSTRAINT pk_tb_user_role PRIMARY KEY (user_id, role_cd),
    CONSTRAINT fk_tb_user_role_user FOREIGN KEY (user_id) REFERENCES tb_user (user_id)
);

-- ================================================================
-- 테이블명 : TB_MENU_ROLE (메뉴역할)
-- 설명     : 메뉴-역할 다대다 매핑
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_menu_role
(
    menu_id             VARCHAR(20)    NOT NULL,               -- 메뉴ID
    role_cd             VARCHAR(20)    NOT NULL,               -- 역할코드

    /* ── System Fields ──────────────────────────────── */
    created_by          VARCHAR(50)    NOT NULL,               -- 생성자ID
    created_dt          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,                   -- 수정자ID
    updated_dt          TIMESTAMP      NULL,

    CONSTRAINT pk_tb_menu_role PRIMARY KEY (menu_id, role_cd),
    CONSTRAINT fk_tb_menu_role_menu FOREIGN KEY (menu_id) REFERENCES tb_menu (menu_id)
);
