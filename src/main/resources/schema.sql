-- ================================================================
-- MySQL 호환 스키마 (JPA ddl-auto=update가 테이블을 자동 생성하므로
-- 이 파일은 참조용/보조용입니다)
-- ================================================================

-- ================================================================
-- 테이블명 : TB_SUBSCRIPTION (가입)
-- 설명     : 서비스 가입 계약 단위 관리
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_subscription
(
    subs_id                 VARCHAR(50)    NOT NULL,               -- 가입ID (사용자 직접 입력)
    subs_nm                 VARCHAR(100)   NULL,                   -- 가입자명
    svc_cd                  VARCHAR(10)    NULL,                   -- 서비스코드
    fee_prod_cd             VARCHAR(10)    NULL,                   -- 요금상품코드
    subs_status_cd          VARCHAR(20)    NULL,                   -- 가입상태코드
    subs_dt                 DATETIME       NULL,                   -- 가입일시
    chg_dt                  DATETIME       NULL,                   -- 변경일시
    admin_id                VARCHAR(50)    NULL,                   -- 관리자ID
    created_by              VARCHAR(50)    NOT NULL,               -- 생성자ID
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,                   -- 수정자ID
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_subscription PRIMARY KEY (subs_id)
);

-- ================================================================
-- 테이블명 : TB_BILL_STD (과금기준)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_bill_std
(
    bill_std_id             VARCHAR(20)    NOT NULL,               -- 과금기준ID
    subs_id                 VARCHAR(20)    NOT NULL,               -- 가입ID
    bill_std_reg_dt         DATETIME       NULL,                   -- 과금기준등록일시
    svc_cd                  VARCHAR(10)    NULL,                   -- 서비스코드
    last_eff_yn             CHAR(1)        DEFAULT 'Y',            -- 최종유효여부
    eff_start_dt            DATETIME       NULL,                   -- 유효시작일시
    eff_end_dt              DATETIME       DEFAULT '9999-12-31 23:59:59',  -- 유효종료일시
    std_reg_stat_cd         VARCHAR(10)    NULL,                   -- 과금기준등록진행상태코드
    bill_std_stat_cd        VARCHAR(10)    NULL,                   -- 과금기준상태코드
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_bill_std PRIMARY KEY (bill_std_id),
    CONSTRAINT fk_tb_bill_std_subs FOREIGN KEY (subs_id) REFERENCES tb_subscription (subs_id)
);

-- ================================================================
-- 테이블명 : TB_BILL_STD_FIELD_CONFIG (과금기준 필드설정)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_bill_std_field_config
(
    svc_cd                  VARCHAR(10)    NOT NULL,               -- 서비스코드
    field_cd                VARCHAR(50)    NOT NULL,               -- 필드코드
    eff_start_dt            VARCHAR(8)     NOT NULL,               -- 유효시작일 (YYYYMMDD)
    field_nm                VARCHAR(100)   NOT NULL,               -- 필드명
    field_type              VARCHAR(10)    NOT NULL,               -- 필드타입
    required_yn             CHAR(1)        DEFAULT 'N',            -- 필수여부
    sort_order              INTEGER        DEFAULT 0,              -- 정렬순서
    common_code             VARCHAR(50)    NULL,                   -- 공통코드
    default_value           VARCHAR(200)   NULL,                   -- 기본값
    eff_end_dt              VARCHAR(8)     DEFAULT '99991231',     -- 유효종료일
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_bill_std_field_config PRIMARY KEY (svc_cd, field_cd, eff_start_dt)
);

-- ================================================================
-- 테이블명 : TB_BILL_STD_FIELD_VALUE (과금기준 필드값)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_bill_std_field_value
(
    bill_std_id             VARCHAR(20)    NOT NULL,               -- 과금기준ID
    field_cd                VARCHAR(50)    NOT NULL,               -- 필드코드
    field_value             VARCHAR(500)   NULL,                   -- 필드값
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_bill_std_field_value PRIMARY KEY (bill_std_id, field_cd),
    CONSTRAINT fk_tb_bill_std_field_value_bs FOREIGN KEY (bill_std_id) REFERENCES tb_bill_std (bill_std_id)
);

-- ================================================================
-- 테이블명 : TB_COMMON_CODE (공통코드 헤더)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_common_code
(
    common_code         VARCHAR(50)     NOT NULL,
    common_code_nm      VARCHAR(100)    NULL,
    eff_start_dt        DATETIME        NULL,
    eff_end_dt          DATETIME        DEFAULT '9999-12-31 23:59:59',
    remark              VARCHAR(500)    NULL,
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          DATETIME        NULL,
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
    eff_start_dt        DATETIME        NULL,
    eff_end_dt          DATETIME        DEFAULT '9999-12-31 23:59:59',
    remark              VARCHAR(500)    NULL,
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          DATETIME        NULL,
    CONSTRAINT pk_tb_common_dtl_code PRIMARY KEY (common_code, common_dtl_code),
    CONSTRAINT fk_common_dtl_code_hdr FOREIGN KEY (common_code) REFERENCES tb_common_code (common_code)
);

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
    notice_yn           CHAR(1)         DEFAULT 'N',
    notice_start_dt     DATETIME        NULL,
    notice_end_dt       DATETIME        NULL,
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          DATETIME        NULL,
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
    created_dt          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          DATETIME        NULL,
    CONSTRAINT pk_tb_qna_comment PRIMARY KEY (comment_id),
    CONSTRAINT fk_qna_comment_qna FOREIGN KEY (qna_id) REFERENCES tb_qna (qna_id)
);

-- ================================================================
-- 테이블명 : TB_SPECIAL_SUBSCRIPTION (특수가입)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_special_subscription
(
    subs_bill_std_id        VARCHAR(20)    NOT NULL,
    eff_sta_dt              VARCHAR(8)     NOT NULL,
    subs_id                 VARCHAR(20)    NOT NULL,
    svc_cd                  VARCHAR(10)    NULL,
    eff_end_dt              VARCHAR(8)     DEFAULT '99991231',
    last_eff_yn             VARCHAR(1)     NULL,
    spec_subs_stat_cd       VARCHAR(10)    NULL,
    cntrc_cap_kmh           DECIMAL(18,4)  NULL,
    cntrc_amt               DECIMAL(18,2)  NULL,
    dsc_rt                  DECIMAL(18,4)  NULL,
    rmk                     VARCHAR(500)   NULL,
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_special_subscription PRIMARY KEY (subs_bill_std_id, eff_sta_dt)
);

-- ================================================================
-- 테이블명 : TB_MENU (메뉴)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_menu
(
    menu_id             VARCHAR(20)    NOT NULL,
    menu_nm             VARCHAR(100)   NOT NULL,
    menu_url            VARCHAR(200)   NULL,
    parent_menu_id      VARCHAR(20)    NULL,
    sort_order          INTEGER        DEFAULT 0,
    use_yn              CHAR(1)        DEFAULT 'Y',
    menu_level          INTEGER        DEFAULT 1,
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_menu PRIMARY KEY (menu_id),
    CONSTRAINT fk_tb_menu_parent FOREIGN KEY (parent_menu_id) REFERENCES tb_menu (menu_id)
);

-- ================================================================
-- 테이블명 : TB_USER_ROLE (사용자역할)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_user_role
(
    user_id             VARCHAR(50)    NOT NULL,
    role_cd             VARCHAR(20)    NOT NULL,
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_user_role PRIMARY KEY (user_id, role_cd),
    CONSTRAINT fk_tb_user_role_user FOREIGN KEY (user_id) REFERENCES tb_user (user_id)
);

-- ================================================================
-- 테이블명 : TB_MENU_ROLE (메뉴역할)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_menu_role
(
    menu_id             VARCHAR(20)    NOT NULL,
    role_cd             VARCHAR(20)    NOT NULL,
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_menu_role PRIMARY KEY (menu_id, role_cd),
    CONSTRAINT fk_tb_menu_role_menu FOREIGN KEY (menu_id) REFERENCES tb_menu (menu_id)
);
