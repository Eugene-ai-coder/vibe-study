-- ================================================================
-- 공통코드 초기 데이터 (MERGE = 멱등성 보장)
-- ================================================================

-- ── 1. 공통코드 헤더 ─────────────────────────────────────────────
MERGE INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) KEY (common_code) VALUES
('subs_status_cd',        '가입상태코드',             'tb_subscription.subs_status_cd',                  'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) KEY (common_code) VALUES
('svc_cd',                '서비스코드',               'tb_bill_std.svc_cd, tb_special_subscription.svc_cd', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) KEY (common_code) VALUES
('std_reg_stat_cd',       '과금기준등록진행상태코드',  'tb_bill_std.std_reg_stat_cd',                     'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) KEY (common_code) VALUES
('bill_std_stat_cd',      '과금기준상태코드',          'tb_bill_std.bill_std_stat_cd',                    'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) KEY (common_code) VALUES
('pwr_met_calc_meth_cd',  '전력종량계산방식코드',      'tb_bill_std.pwr_met_calc_meth_cd',                'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) KEY (common_code) VALUES
('uprc_det_meth_cd',      '단가결정방식코드',          'tb_bill_std.uprc_det_meth_cd',                    'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) KEY (common_code) VALUES
('pue_det_meth_cd',       'PUE결정방식코드',           'tb_bill_std.pue_det_meth_cd',                     'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) KEY (common_code) VALUES
('spec_subs_stat_cd',     '특수가입상태코드',          'tb_special_subscription.spec_subs_stat_cd',       'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) KEY (common_code) VALUES
('role_cd',               '역할코드',                 'tb_user_role.role_cd, tb_menu_role.role_cd',       'SYSTEM', CURRENT_TIMESTAMP);

-- ── 2. 공통상세코드 ──────────────────────────────────────────────

-- 가입상태코드
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('subs_status_cd', 'ACTIVE',     '활성', 1, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('subs_status_cd', 'SUSPENDED',  '정지', 2, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('subs_status_cd', 'TERMINATED', '해지', 3, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('subs_status_cd', 'PENDING',    '대기', 4, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);

-- 서비스코드 (업무별 정의 - 샘플)
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('svc_cd', 'SVC01', '전력', 1, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('svc_cd', 'SVC02', '냉방', 2, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('svc_cd', 'SVC03', '통신', 3, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);

-- 과금기준등록진행상태코드
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('std_reg_stat_cd', 'DRAFT',    '임시저장', 1, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('std_reg_stat_cd', 'REVIEW',   '검토중',   2, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('std_reg_stat_cd', 'APPROVED', '승인',     3, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('std_reg_stat_cd', 'REJECTED', '반려',     4, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);

-- 과금기준상태코드
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('bill_std_stat_cd', 'ACTIVE',    '유효', 1, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('bill_std_stat_cd', 'EXPIRED',   '만료', 2, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('bill_std_stat_cd', 'CANCELLED', '취소', 3, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);

-- 전력종량계산방식코드
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('pwr_met_calc_meth_cd', 'METERED', '종량', 1, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('pwr_met_calc_meth_cd', 'FIXED',   '정액', 2, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('pwr_met_calc_meth_cd', 'HYBRID',  '혼합', 3, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);

-- 단가결정방식코드
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('uprc_det_meth_cd', 'FIXED',    '고정단가',   1, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('uprc_det_meth_cd', 'MARKET',   '시장연동',   2, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('uprc_det_meth_cd', 'CONTRACT', '계약단가',   3, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);

-- PUE결정방식코드
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('pue_det_meth_cd', 'FIXED',    '고정PUE',   1, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('pue_det_meth_cd', 'MEASURED', '실측PUE',   2, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('pue_det_meth_cd', 'SEASONAL', '계절별PUE', 3, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);

-- 특수가입상태코드
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('spec_subs_stat_cd', 'ACTIVE',    '유효', 1, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('spec_subs_stat_cd', 'EXPIRED',   '만료', 2, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('spec_subs_stat_cd', 'CANCELLED', '취소', 3, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('spec_subs_stat_cd', 'PENDING',   '대기', 4, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);

-- 역할코드
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('role_cd', 'ADMIN',   '관리자',     1, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('role_cd', 'USER',    '일반사용자', 2, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('role_cd', 'MANAGER', '매니저',     3, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('role_cd', 'VIEWER',  '조회자',     4, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
