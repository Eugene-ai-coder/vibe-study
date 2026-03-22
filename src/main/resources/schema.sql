-- ================================================================
-- MySQL 호환 스키마 (JPA ddl-auto=update가 테이블을 자동 생성하므로
-- 이 파일은 참조용/보조용입니다)
-- ================================================================

-- ================================================================
-- 테이블명 : TB_USER (사용자)
-- 설명     : 시스템 사용자 계정 관리
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_user
(
    user_id                 VARCHAR(50)    NOT NULL,               -- 사용자ID
    nickname                VARCHAR(50)    NOT NULL,               -- 닉네임
    password                VARCHAR(255)   NOT NULL,               -- 비밀번호
    email                   VARCHAR(100)   NOT NULL UNIQUE,        -- 이메일
    account_status          TINYINT        NOT NULL DEFAULT 1,     -- 계정상태 (1=활성)
    created_by              VARCHAR(50)    NOT NULL,               -- 생성자ID
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,                   -- 수정자ID
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_user PRIMARY KEY (user_id)
);

-- ================================================================
-- 테이블명 : TB_SUBSCRIPTION (가입)
-- 설명     : 서비스 가입 계약 단위 관리
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_subscription
(
    subs_id                 VARCHAR(50)    NOT NULL,               -- 가입ID (사용자 직접 입력)
    subs_nm                 VARCHAR(100)   NULL,                   -- 가입자명
    svc_cd                  VARCHAR(10)    NULL,                   -- 서비스코드
    basic_prod_cd           VARCHAR(20)    NULL,                   -- 기본상품코드
    subs_status_cd          VARCHAR(20)    NULL,                   -- 가입상태코드
    subs_dt                 DATETIME       NULL,                   -- 가입일시
    chg_dt                  DATETIME       NULL,                   -- 변경일시
    admin_id                VARCHAR(50)    NULL,                   -- 관리자ID
    created_by              VARCHAR(50)    NOT NULL,               -- 생성자ID
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version                 BIGINT         DEFAULT 0,              -- 낙관적잠금버전
    updated_by              VARCHAR(50)    NULL,                   -- 수정자ID
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_subscription PRIMARY KEY (subs_id)
);

-- ================================================================
-- 테이블명 : TB_BILL_STD (과금기준)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_bill_std
(
    bill_std_id             VARCHAR(30)    NOT NULL,               -- 과금기준ID
    subs_id                 VARCHAR(50)    NOT NULL,               -- 가입ID
    bill_std_reg_dt         DATETIME       NULL,                   -- 과금기준등록일시
    svc_cd                  VARCHAR(10)    NULL,                   -- 서비스코드
    basic_prod_cd           VARCHAR(20)    NULL,                   -- 기본상품코드
    last_eff_yn             CHAR(1)        DEFAULT 'Y',            -- 최종유효여부
    eff_start_dt            DATETIME       NULL,                   -- 유효시작일시
    eff_end_dt              DATETIME       DEFAULT '9999-12-31 23:59:59',  -- 유효종료일시
    bill_std_stat_cd        VARCHAR(10)    NULL,                   -- 과금기준상태코드
    version                 BIGINT         DEFAULT 0,              -- 낙관적잠금버전
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_bill_std PRIMARY KEY (bill_std_id),
    CONSTRAINT fk_tb_bill_std_subs FOREIGN KEY (subs_id) REFERENCES tb_subscription (subs_id)
);
CREATE INDEX idx_tb_bill_std_subs_id ON tb_bill_std (subs_id);
CREATE INDEX idx_tb_bill_std_temporal ON tb_bill_std (eff_start_dt, eff_end_dt);

-- ================================================================
-- 테이블명 : TB_BILL_STD_FIELD_CONFIG (과금기준 필드설정)
-- 참고     : eff_start_dt/eff_end_dt는 일(日) 단위 유효기간으로 VARCHAR(8) YYYYMMDD 사용 (의도된 설계)
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
    bill_std_id             VARCHAR(30)    NOT NULL,               -- 과금기준ID
    field_cd                VARCHAR(50)    NOT NULL,               -- 필드코드
    field_value             VARCHAR(500)   NULL,                   -- 필드값
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_bill_std_field_value PRIMARY KEY (bill_std_id, field_cd),
    CONSTRAINT fk_tb_bill_std_field_value_bs FOREIGN KEY (bill_std_id) REFERENCES tb_bill_std (bill_std_id)
);
CREATE INDEX idx_tb_bill_std_fv_field_cd ON tb_bill_std_field_value (field_cd);

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
CREATE INDEX idx_tb_common_dtl_code_sort ON tb_common_dtl_code (common_code, sort_order);

-- ================================================================
-- 테이블명 : TB_QNA (Q&A 게시글)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_qna
(
    qna_id              VARCHAR(50)     NOT NULL,
    title               VARCHAR(200)    NULL,
    content             TEXT            NULL,
    view_cnt            INTEGER         DEFAULT 0,
    answer_yn           CHAR(1)         DEFAULT 'N',
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
    CONSTRAINT fk_qna_comment_qna FOREIGN KEY (qna_id) REFERENCES tb_qna (qna_id),
    CONSTRAINT fk_qna_comment_parent FOREIGN KEY (parent_comment_id) REFERENCES tb_qna_comment (comment_id)
);
CREATE INDEX idx_tb_qna_comment_parent ON tb_qna_comment (parent_comment_id);

-- ================================================================
-- 테이블명 : TB_SPECIAL_SUBSCRIPTION (특수가입)
-- 참고     : eff_start_dt/eff_end_dt는 일(日) 단위 유효기간으로 VARCHAR(8) YYYYMMDD 사용 (의도된 설계)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_special_subscription
(
    subs_bill_std_id        VARCHAR(30)    NOT NULL,
    eff_start_dt            VARCHAR(8)     NOT NULL,
    subs_id                 VARCHAR(50)    NOT NULL,
    svc_cd                  VARCHAR(10)    NULL,
    eff_end_dt              VARCHAR(8)     DEFAULT '99991231',
    last_eff_yn             CHAR(1)        NOT NULL DEFAULT 'Y',
    spec_subs_stat_cd       VARCHAR(10)    NULL,
    cntrc_cap_kmh           DECIMAL(18,4)  NULL,
    cntrc_amt               DECIMAL(18,2)  NULL,
    dsc_rt                  DECIMAL(18,4)  NULL,
    remark                  VARCHAR(500)   NULL,
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_special_subscription PRIMARY KEY (subs_bill_std_id, eff_start_dt),
    CONSTRAINT fk_tb_spcl_subs_subs FOREIGN KEY (subs_id) REFERENCES tb_subscription (subs_id),
    CONSTRAINT fk_tb_spcl_subs_bill_std FOREIGN KEY (subs_bill_std_id) REFERENCES tb_bill_std (bill_std_id)
);

-- ================================================================
-- 테이블명 : TB_MENU (메뉴)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_menu
(
    menu_id             VARCHAR(30)    NOT NULL,
    menu_nm             VARCHAR(100)   NOT NULL,
    menu_url            VARCHAR(200)   NULL,
    parent_menu_id      VARCHAR(30)    NULL,
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
    -- role_cd는 공통코드(role_cd 그룹) 참조 — 복합PK 구조상 단순 FK 불가, 애플리케이션 레벨 검증
);

-- ================================================================
-- 테이블명 : TB_MENU_ROLE (메뉴역할)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_menu_role
(
    menu_id             VARCHAR(30)    NOT NULL,
    role_cd             VARCHAR(20)    NOT NULL,
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_menu_role PRIMARY KEY (menu_id, role_cd),
    CONSTRAINT fk_tb_menu_role_menu FOREIGN KEY (menu_id) REFERENCES tb_menu (menu_id)
    -- role_cd는 공통코드(role_cd 그룹) 참조 — 복합PK 구조상 단순 FK 불가, 애플리케이션 레벨 검증
);

-- ================================================================
-- 테이블명 : TB_WF_PROCESS_DEF (워크플로우 프로세스 정의)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_wf_process_def
(
    process_def_id      VARCHAR(30)    NOT NULL,               -- 프로세스정의ID
    process_nm          VARCHAR(100)   NOT NULL,               -- 프로세스명
    process_desc        VARCHAR(500)   NULL,                   -- 프로세스설명
    entity_type         VARCHAR(50)    NULL,                   -- 업무유형 (SUBSCRIPTION, QNA 등)
    use_yn              CHAR(1)        DEFAULT 'Y',            -- 사용여부
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_wf_process_def PRIMARY KEY (process_def_id)
);
CREATE INDEX idx_tb_wf_process_def_entity ON tb_wf_process_def (entity_type, use_yn);

-- ================================================================
-- 테이블명 : TB_WF_STATE_DEF (워크플로우 상태 정의)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_wf_state_def
(
    state_def_id        VARCHAR(30)    NOT NULL,               -- 상태정의ID
    process_def_id      VARCHAR(30)    NOT NULL,               -- 프로세스정의ID
    state_nm            VARCHAR(100)   NOT NULL,               -- 상태명
    state_type          VARCHAR(20)    NOT NULL,               -- 상태유형 (START/NORMAL/END)
    sort_order          INTEGER        DEFAULT 0,              -- 정렬순서
    entity_status_cd    VARCHAR(50)    NULL,                    -- 엔티티상태코드
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_wf_state_def PRIMARY KEY (state_def_id),
    CONSTRAINT fk_tb_wf_state_def_process FOREIGN KEY (process_def_id) REFERENCES tb_wf_process_def (process_def_id)
);
CREATE INDEX idx_tb_wf_state_def_process ON tb_wf_state_def (process_def_id);

-- ================================================================
-- 테이블명 : TB_WF_TRANSITION_DEF (워크플로우 전이 정의)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_wf_transition_def
(
    transition_def_id   VARCHAR(30)    NOT NULL,               -- 전이정의ID
    process_def_id      VARCHAR(30)    NOT NULL,               -- 프로세스정의ID
    from_state_def_id   VARCHAR(30)    NOT NULL,               -- 출발상태ID
    to_state_def_id     VARCHAR(30)    NOT NULL,               -- 도착상태ID
    transition_code     VARCHAR(50)    NULL,                   -- 전이코드 (APPROVE/REJECT/SUBMIT)
    event_nm            VARCHAR(100)   NULL,                   -- 이벤트명 (버튼 레이블 등)
    sort_order          INTEGER        DEFAULT 0,              -- 정렬순서
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_wf_transition_def PRIMARY KEY (transition_def_id),
    CONSTRAINT fk_tb_wf_transition_def_process FOREIGN KEY (process_def_id) REFERENCES tb_wf_process_def (process_def_id),
    CONSTRAINT fk_tb_wf_transition_def_from FOREIGN KEY (from_state_def_id) REFERENCES tb_wf_state_def (state_def_id),
    CONSTRAINT fk_tb_wf_transition_def_to FOREIGN KEY (to_state_def_id) REFERENCES tb_wf_state_def (state_def_id)
);
CREATE INDEX idx_tb_wf_transition_def_process ON tb_wf_transition_def (process_def_id);
CREATE INDEX idx_tb_wf_transition_def_from_code ON tb_wf_transition_def (from_state_def_id, transition_code);

-- ================================================================
-- 테이블명 : TB_WF_TASK_TEMPLATE (워크플로우 Task 템플릿)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_wf_task_template
(
    task_template_id    VARCHAR(30)    NOT NULL,               -- Task템플릿ID
    state_def_id        VARCHAR(30)    NOT NULL,               -- 상태정의ID
    task_nm             VARCHAR(100)   NOT NULL,               -- Task명
    task_desc           VARCHAR(500)   NULL,                   -- Task설명
    assignee_type       VARCHAR(20)    NULL,                   -- 담당자유형 (ROLE/USER/AUTO)
    assignee_value      VARCHAR(50)    NULL,                   -- 담당자값
    task_type           VARCHAR(20)    NULL,                   -- Task유형 (REVIEW/APPROVAL/DATA_ENTRY)
    priority            INTEGER        DEFAULT 5,              -- 우선순위 (1~10)
    sla_hours           INTEGER        NULL,                   -- SLA(시간)
    sort_order          INTEGER        DEFAULT 0,              -- 정렬순서
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_wf_task_template PRIMARY KEY (task_template_id),
    CONSTRAINT fk_tb_wf_task_template_state FOREIGN KEY (state_def_id) REFERENCES tb_wf_state_def (state_def_id)
);
CREATE INDEX idx_tb_wf_task_template_state ON tb_wf_task_template (state_def_id);

-- ================================================================
-- 테이블명 : TB_WF_PROCESS_INST (워크플로우 프로세스 인스턴스)
-- 설명     : 2회차 구현 — 스키마만 선행 생성
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_wf_process_inst
(
    process_inst_id     VARCHAR(30)    NOT NULL,               -- 프로세스인스턴스ID
    process_def_id      VARCHAR(30)    NOT NULL,               -- 프로세스정의ID
    current_state_def_id VARCHAR(30)   NULL,                   -- 현재상태ID
    entity_type         VARCHAR(50)    NULL,                   -- 업무유형 (SUBSCRIPTION, QNA 등)
    entity_id           VARCHAR(50)    NULL,                   -- 업무ID (가입ID 등)
    status              VARCHAR(20)    DEFAULT 'ACTIVE',       -- 인스턴스상태 (ACTIVE/COMPLETED/CANCELLED)
    started_by          VARCHAR(50)    NULL,                   -- 시작자ID
    completed_dt        DATETIME       NULL,                   -- 완료일시
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_wf_process_inst PRIMARY KEY (process_inst_id),
    CONSTRAINT fk_tb_wf_process_inst_def FOREIGN KEY (process_def_id) REFERENCES tb_wf_process_def (process_def_id),
    CONSTRAINT fk_tb_wf_process_inst_state FOREIGN KEY (current_state_def_id) REFERENCES tb_wf_state_def (state_def_id)
);
CREATE INDEX idx_tb_wf_process_inst_entity ON tb_wf_process_inst (entity_type, entity_id);

-- ================================================================
-- 테이블명 : TB_WF_TASK_INST (워크플로우 Task 인스턴스)
-- 설명     : 2회차 구현 — 스키마만 선행 생성
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_wf_task_inst
(
    task_inst_id        VARCHAR(30)    NOT NULL,               -- TaskID
    process_inst_id     VARCHAR(30)    NOT NULL,               -- 프로세스인스턴스ID
    task_template_id    VARCHAR(30)    NULL,                   -- 원본 템플릿ID
    state_def_id        VARCHAR(30)    NOT NULL,               -- 상태정의ID
    task_nm             VARCHAR(100)   NOT NULL,               -- Task명
    task_type           VARCHAR(20)    NULL,                   -- Task유형 (REVIEW/APPROVAL/DATA_ENTRY)
    priority            INTEGER        DEFAULT 5,              -- 우선순위 (1~10)
    assignee_id         VARCHAR(50)    NULL,                   -- 담당자ID
    status              VARCHAR(20)    DEFAULT 'PENDING',      -- Task상태 (PENDING/IN_PROGRESS/COMPLETED/CANCELLED)
    result              VARCHAR(50)    NULL,                   -- 완료결과 (APPROVED/REJECTED/DONE)
    comment             VARCHAR(1000)  NULL,                   -- 완료코멘트
    completed_by        VARCHAR(50)    NULL,                   -- 완료처리자
    due_dt              DATETIME       NULL,                   -- 기한일시
    completed_dt        DATETIME       NULL,                   -- 완료일시
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_wf_task_inst PRIMARY KEY (task_inst_id),
    CONSTRAINT fk_tb_wf_task_inst_proc FOREIGN KEY (process_inst_id) REFERENCES tb_wf_process_inst (process_inst_id),
    CONSTRAINT fk_tb_wf_task_inst_state FOREIGN KEY (state_def_id) REFERENCES tb_wf_state_def (state_def_id)
);
CREATE INDEX idx_tb_wf_task_inst_proc ON tb_wf_task_inst (process_inst_id);
CREATE INDEX idx_tb_wf_task_inst_assignee ON tb_wf_task_inst (assignee_id, status);

-- ================================================================
-- 테이블명 : TB_WF_TRANSITION_LOG (워크플로우 전이 로그)
-- 설명     : 2회차 구현 — 스키마만 선행 생성
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_wf_transition_log
(
    transition_log_id   VARCHAR(30)    NOT NULL,               -- 전이로그ID
    process_inst_id     VARCHAR(30)    NOT NULL,               -- 프로세스인스턴스ID
    transition_def_id   VARCHAR(30)    NULL,                   -- 전이정의ID
    from_state_def_id   VARCHAR(30)    NOT NULL,               -- 출발상태ID
    to_state_def_id     VARCHAR(30)    NOT NULL,               -- 도착상태ID
    transitioned_dt     DATETIME       NOT NULL,               -- 전이일시
    remark              VARCHAR(500)   NULL,                   -- 비고
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_wf_transition_log PRIMARY KEY (transition_log_id),
    CONSTRAINT fk_tb_wf_transition_log_proc FOREIGN KEY (process_inst_id) REFERENCES tb_wf_process_inst (process_inst_id)
);
CREATE INDEX idx_tb_wf_transition_log_proc ON tb_wf_transition_log (process_inst_id);

-- ================================================================
-- 테이블명 : TB_WF_ENTITY_TYPE_DEF (워크플로우 엔티티 유형 정의)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_wf_entity_type_def
(
    entity_type_cd          VARCHAR(50)    NOT NULL,               -- 엔티티유형코드
    entity_type_nm          VARCHAR(100)   NOT NULL,               -- 엔티티유형명
    table_nm                VARCHAR(100)   NOT NULL,               -- 테이블명
    pk_column               VARCHAR(100)   NOT NULL,               -- PK컬럼명
    status_column           VARCHAR(100)   NULL,                   -- 상태컬럼명
    status_cd_group         VARCHAR(100)   NULL,                   -- 상태공통코드그룹
    biz_key_column          VARCHAR(100)   NULL,                   -- 업무키컬럼명
    biz_key_label           VARCHAR(100)   NULL,                   -- 업무키라벨
    route_path              VARCHAR(200)   NULL,                   -- 프론트라우트경로
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_wf_entity_type_def PRIMARY KEY (entity_type_cd)
);

-- ================================================================
-- 테이블명 : TB_BILL_STD_APPRV_REQ (과금기준 결재요청)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_bill_std_apprv_req
(
    apprv_req_id        VARCHAR(25)    NOT NULL,               -- 결재요청ID
    bill_std_req_id     VARCHAR(25)    NOT NULL,               -- 과금기준신청ID
    subs_id             VARCHAR(50)    NOT NULL,               -- 가입ID
    apprv_req_content   TEXT           NOT NULL,               -- 결재요청내용 (스냅샷 JSON)
    apprv_remarks       VARCHAR(500)   NULL,                   -- 결재 특기사항
    approver_id         VARCHAR(50)    NULL,                   -- 결재자ID
    created_by          VARCHAR(50)    NOT NULL,
    created_dt          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)    NULL,
    updated_dt          DATETIME       NULL,
    CONSTRAINT pk_tb_bill_std_apprv_req PRIMARY KEY (apprv_req_id),
    CONSTRAINT fk_tb_apprv_req_subs FOREIGN KEY (subs_id) REFERENCES tb_subscription (subs_id)
);
CREATE INDEX idx_tb_bill_std_apprv_req_bill_std_req ON tb_bill_std_apprv_req (bill_std_req_id);
CREATE INDEX idx_tb_bill_std_apprv_req_subs ON tb_bill_std_apprv_req (subs_id);

-- ================================================================
-- 테이블명 : TB_BILL_STD_REQ (과금기준신청)
-- 설명     : Temporal 패턴 — 동일 신청의 변경이력을 유효기간으로 관리
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_bill_std_req
(
    bill_std_req_seq        BIGINT         NOT NULL AUTO_INCREMENT,
    bill_std_req_id         VARCHAR(25)    NOT NULL,
    first_req_dt            DATETIME       NOT NULL,
    eff_start_dt              DATETIME       NOT NULL,
    eff_end_dt              DATETIME       NOT NULL DEFAULT '9999-12-31 23:59:59',
    req_type_cd             VARCHAR(20)    NOT NULL,
    std_reg_stat_cd         VARCHAR(20)    NOT NULL,
    bill_std_id             VARCHAR(30)    NOT NULL,
    subs_id                 VARCHAR(50)    NOT NULL,
    svc_cd                  VARCHAR(10)    NOT NULL,
    basic_prod_cd           VARCHAR(20)    NULL,                   -- 기본상품코드
    version                 BIGINT         DEFAULT 0,              -- 낙관적잠금버전
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_bill_std_req PRIMARY KEY (bill_std_req_seq),
    CONSTRAINT uk_tb_bill_std_req UNIQUE (bill_std_req_id, eff_start_dt)
);
CREATE INDEX idx_tb_bill_std_req_subs ON tb_bill_std_req (subs_id);
CREATE INDEX idx_tb_bill_std_req_current ON tb_bill_std_req (bill_std_req_id, eff_end_dt);
CREATE INDEX idx_tb_bill_std_req_temporal ON tb_bill_std_req (eff_start_dt, eff_end_dt);

-- ================================================================
-- 테이블명 : TB_BILL_STD_REQ_FIELD_VALUE (과금기준신청 필드값)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_bill_std_req_field_value
(
    bill_std_req_seq        BIGINT         NOT NULL,
    field_cd                VARCHAR(50)    NOT NULL,
    field_value             VARCHAR(500)   NULL,
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_bill_std_req_fv PRIMARY KEY (bill_std_req_seq, field_cd),
    CONSTRAINT fk_tb_bill_std_req_fv FOREIGN KEY (bill_std_req_seq) REFERENCES tb_bill_std_req (bill_std_req_seq)
);
CREATE INDEX idx_tb_bill_std_req_fv_field_cd ON tb_bill_std_req_field_value (field_cd);

-- ================================================================
-- 테이블명 : TB_SUBS_MTH_BILL_QTY (가입별 월별과금량)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_subs_mth_bill_qty
(
    subs_id                 VARCHAR(50)    NOT NULL,
    use_mth                 VARCHAR(6)     NOT NULL,
    bill_std_id             VARCHAR(30)    NOT NULL,
    use_qty                 DECIMAL(18,4)  NOT NULL,
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_subs_mth_bill_qty PRIMARY KEY (subs_id, use_mth),
    CONSTRAINT fk_tb_subs_mth_bill_qty_subs FOREIGN KEY (subs_id) REFERENCES tb_subscription (subs_id),
    CONSTRAINT fk_tb_subs_mth_bill_qty_bs FOREIGN KEY (bill_std_id) REFERENCES tb_bill_std (bill_std_id)
);
CREATE INDEX idx_tb_subs_mth_bill_qty_bill_std ON tb_subs_mth_bill_qty (bill_std_id);

-- ================================================================
-- 테이블명 : TB_SPCL_SUBS_MTH_BILL_QTY (특수가입별 월별과금량)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_spcl_subs_mth_bill_qty
(
    spcl_subs_id            VARCHAR(30)    NOT NULL,
    use_mth                 VARCHAR(6)     NOT NULL,
    subs_id                 VARCHAR(50)    NOT NULL,
    bill_std_id             VARCHAR(30)    NOT NULL,
    pue                     DECIMAL(10,4)  NOT NULL,
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_spcl_subs_mth_bill_qty PRIMARY KEY (spcl_subs_id, use_mth),
    CONSTRAINT fk_tb_spcl_subs_mth_bq_subs FOREIGN KEY (subs_id) REFERENCES tb_subscription (subs_id),
    CONSTRAINT fk_tb_spcl_subs_mth_bq_bs FOREIGN KEY (bill_std_id) REFERENCES tb_bill_std (bill_std_id)
);

-- ================================================================
-- 테이블명 : TB_SPCL_SUBS_MTH_BILL_ELEM (특수가입별 월별빌링요소)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_spcl_subs_mth_bill_elem
(
    spcl_subs_id            VARCHAR(30)    NOT NULL,
    bill_mth                VARCHAR(6)     NOT NULL,
    subs_id                 VARCHAR(50)    NOT NULL,
    calc_amt                DECIMAL(18,2)  NOT NULL,
    bill_amt                DECIMAL(18,2)  NOT NULL,
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              DATETIME       NULL,
    CONSTRAINT pk_tb_spcl_subs_mth_bill_elem PRIMARY KEY (spcl_subs_id, bill_mth),
    CONSTRAINT fk_tb_spcl_subs_mth_be_subs FOREIGN KEY (subs_id) REFERENCES tb_subscription (subs_id)
);

-- ================================================================
-- 테이블명 : TB_TODO (할일)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_todo
(
    todo_id                 BIGINT         NOT NULL AUTO_INCREMENT,
    entity_type             VARCHAR(50)    NOT NULL,
    entity_key1             VARCHAR(50)    NOT NULL,
    entity_key2             VARCHAR(50)    NULL,
    todo_title              VARCHAR(200)   NOT NULL,
    assignee_id             VARCHAR(50)    NOT NULL,
    todo_status_cd          VARCHAR(20)    NOT NULL DEFAULT 'OPEN',
    eff_start_dt            DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    eff_end_dt              DATETIME       NOT NULL DEFAULT '9999-12-31 23:59:59',
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_tb_todo PRIMARY KEY (todo_id),
    CONSTRAINT uq_tb_todo_entity_eff UNIQUE (entity_type, entity_key1, entity_key2, eff_start_dt, eff_end_dt)
);
CREATE INDEX idx_tb_todo_assignee ON tb_todo (assignee_id, todo_status_cd);
CREATE INDEX idx_tb_todo_entity ON tb_todo (entity_type, entity_key1, entity_key2, eff_start_dt, eff_end_dt);
