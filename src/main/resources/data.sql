-- ================================================================
-- 공통코드 초기 데이터 (INSERT ON DUPLICATE KEY UPDATE = 멱등성 보장)
-- ================================================================

-- ── 1. 공통코드 헤더 ─────────────────────────────────────────────
INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES
('subs_status_cd', '가입상태코드', 'tb_subscription.subs_status_cd', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);

INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES
('svc_cd', '서비스코드', 'tb_bill_std.svc_cd, tb_special_subscription.svc_cd', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);

INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES
('fee_prod_cd', '요금상품코드', 'tb_subscription.fee_prod_cd', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);

INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES
('std_reg_stat_cd', '과금기준등록진행상태코드', 'tb_bill_std.std_reg_stat_cd', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);

INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES
('bill_std_stat_cd', '과금기준상태코드', 'tb_bill_std.bill_std_stat_cd', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);

INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES
('pwr_met_calc_meth_cd', '전력종량계산방식코드', 'tb_bill_std.pwr_met_calc_meth_cd', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);

INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES
('uprc_det_meth_cd', '단가결정방식코드', 'tb_bill_std.uprc_det_meth_cd', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);

INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES
('pue_det_meth_cd', 'PUE결정방식코드', 'tb_bill_std.pue_det_meth_cd', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);

INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES
('spec_subs_stat_cd', '특수가입상태코드', 'tb_special_subscription.spec_subs_stat_cd', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);

INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES
('role_cd', '역할코드', 'tb_user_role.role_cd, tb_menu_role.role_cd', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);

-- ── 2. 공통상세코드 ──────────────────────────────────────────────

-- 가입상태코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('subs_status_cd', 'ACTIVE', '활성', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('subs_status_cd', 'SUSPENDED', '정지', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('subs_status_cd', 'TERMINATED', '해지', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('subs_status_cd', 'PENDING', '대기', 4, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- 서비스코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('svc_cd', 'SVC01', '전력', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('svc_cd', 'SVC02', '냉방', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('svc_cd', 'SVC03', '통신', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- 요금상품코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('fee_prod_cd', 'FP_A', '요금상품 A', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('fee_prod_cd', 'FP_B', '요금상품 B', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('fee_prod_cd', 'FP_C', '요금상품 C', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('fee_prod_cd', 'FP_D', '요금상품 D', 4, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('fee_prod_cd', 'FP_E', '요금상품 E', 5, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('fee_prod_cd', 'FP_F', '요금상품 F', 6, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- 과금기준등록진행상태코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('std_reg_stat_cd', 'DRAFT', '임시저장', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('std_reg_stat_cd', 'REVIEW', '검토중', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('std_reg_stat_cd', 'APPROVED', '승인', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('std_reg_stat_cd', 'REJECTED', '반려', 4, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- 과금기준상태코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('bill_std_stat_cd', 'ACTIVE', '유효', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('bill_std_stat_cd', 'EXPIRED', '만료', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('bill_std_stat_cd', 'CANCELLED', '취소', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- 전력종량계산방식코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('pwr_met_calc_meth_cd', 'METERED', '종량', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('pwr_met_calc_meth_cd', 'FIXED', '정액', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('pwr_met_calc_meth_cd', 'HYBRID', '혼합', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- 단가결정방식코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('uprc_det_meth_cd', 'FIXED', '고정단가', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('uprc_det_meth_cd', 'MARKET', '시장연동', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('uprc_det_meth_cd', 'CONTRACT', '계약단가', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- PUE결정방식코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('pue_det_meth_cd', 'FIXED', '고정PUE', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('pue_det_meth_cd', 'MEASURED', '실측PUE', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('pue_det_meth_cd', 'SEASONAL', '계절별PUE', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- 특수가입상태코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('spec_subs_stat_cd', 'ACTIVE', '유효', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('spec_subs_stat_cd', 'EXPIRED', '만료', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('spec_subs_stat_cd', 'CANCELLED', '취소', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('spec_subs_stat_cd', 'PENDING', '대기', 4, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- 역할코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('role_cd', 'ADMIN', '관리자', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('role_cd', 'USER', '일반사용자', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('role_cd', 'MANAGER', '매니저', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('role_cd', 'VIEWER', '조회자', 4, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- ================================================================
-- 과금기준 필드설정 초기 데이터 (SVC01: 전력 - 14개 전체)
-- ================================================================
INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'pwr_met_calc_meth_cd', '20000101', '전력종량계산방식', 'SELECT', 'N', 1, 'pwr_met_calc_meth_cd', NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'uprc_det_meth_cd', '20000101', '단가결정방식', 'SELECT', 'N', 2, 'uprc_det_meth_cd', NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'metering_unit_price_amt', '20000101', '종량단가', 'NUMBER', 'N', 3, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'bill_qty', '20000101', '과금량갯수', 'NUMBER', 'N', 4, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'pue_det_meth_cd', '20000101', 'PUE결정방식', 'SELECT', 'N', 5, 'pue_det_meth_cd', NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'pue1_rt', '20000101', 'PUE1', 'NUMBER', 'N', 6, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'pue2_rt', '20000101', 'PUE2', 'NUMBER', 'N', 7, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'first_dsc_rt', '20000101', '1차할인율', 'NUMBER', 'N', 8, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'second_dsc_rt', '20000101', '2차할인율', 'NUMBER', 'N', 9, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'loss_comp_rt', '20000101', '손실보상율', 'NUMBER', 'N', 10, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'cntrc_cap_kmh', '20000101', '약정용량(Kwh)', 'NUMBER', 'N', 11, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'cntrc_amt', '20000101', '약정요금', 'NUMBER', 'N', 12, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'dsc_amt', '20000101', '할인액', 'NUMBER', 'N', 13, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC01', 'daily_unit_price', '20000101', '일별단가', 'NUMBER', 'N', 14, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

-- ================================================================
-- 과금기준 필드설정 초기 데이터 (SVC02: 냉방)
-- ================================================================
INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC02', 'cntrc_cap_kmh', '20000101', '약정용량(Kwh)', 'NUMBER', 'N', 1, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC02', 'cntrc_amt', '20000101', '약정요금', 'NUMBER', 'N', 2, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC02', 'metering_unit_price_amt', '20000101', '종량단가', 'NUMBER', 'N', 3, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

-- ================================================================
-- 과금기준 필드설정 초기 데이터 (SVC03: 통신)
-- ================================================================
INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC03', 'cntrc_amt', '20000101', '약정요금', 'NUMBER', 'N', 1, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);

INSERT INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) VALUES
('SVC03', 'bill_qty', '20000101', '과금량갯수', 'NUMBER', 'N', 2, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE field_nm = VALUES(field_nm), field_type = VALUES(field_type), sort_order = VALUES(sort_order);
