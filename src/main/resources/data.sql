-- ================================================================
-- 초기 데이터 (INSERT ON DUPLICATE KEY UPDATE = 멱등성 보장)
-- 실행 순서: schema.sql → JPA ddl-auto → data.sql → CommandLineRunner
-- ================================================================

-- ================================================================
-- 사용자 초기 데이터 (user01~10, 패스워드: password123)
-- ================================================================
INSERT INTO tb_user (user_id, nickname, password, email, account_status, created_by, created_dt) VALUES
('user01', '사용자1', '$2a$10$PSEXbNtRKNlydpkirGjUhezTzPEukgwMi6Xdw.N1EiiCKvw8QMH1y', 'user1@example.com', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO tb_user (user_id, nickname, password, email, account_status, created_by, created_dt) VALUES
('user02', '사용자2', '$2a$10$PSEXbNtRKNlydpkirGjUhezTzPEukgwMi6Xdw.N1EiiCKvw8QMH1y', 'user2@example.com', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO tb_user (user_id, nickname, password, email, account_status, created_by, created_dt) VALUES
('user03', '사용자3', '$2a$10$PSEXbNtRKNlydpkirGjUhezTzPEukgwMi6Xdw.N1EiiCKvw8QMH1y', 'user3@example.com', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO tb_user (user_id, nickname, password, email, account_status, created_by, created_dt) VALUES
('user04', '사용자4', '$2a$10$PSEXbNtRKNlydpkirGjUhezTzPEukgwMi6Xdw.N1EiiCKvw8QMH1y', 'user4@example.com', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO tb_user (user_id, nickname, password, email, account_status, created_by, created_dt) VALUES
('user05', '사용자5', '$2a$10$PSEXbNtRKNlydpkirGjUhezTzPEukgwMi6Xdw.N1EiiCKvw8QMH1y', 'user5@example.com', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO tb_user (user_id, nickname, password, email, account_status, created_by, created_dt) VALUES
('user06', '사용자6', '$2a$10$PSEXbNtRKNlydpkirGjUhezTzPEukgwMi6Xdw.N1EiiCKvw8QMH1y', 'user6@example.com', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO tb_user (user_id, nickname, password, email, account_status, created_by, created_dt) VALUES
('user07', '사용자7', '$2a$10$PSEXbNtRKNlydpkirGjUhezTzPEukgwMi6Xdw.N1EiiCKvw8QMH1y', 'user7@example.com', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO tb_user (user_id, nickname, password, email, account_status, created_by, created_dt) VALUES
('user08', '사용자8', '$2a$10$PSEXbNtRKNlydpkirGjUhezTzPEukgwMi6Xdw.N1EiiCKvw8QMH1y', 'user8@example.com', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO tb_user (user_id, nickname, password, email, account_status, created_by, created_dt) VALUES
('user09', '사용자9', '$2a$10$PSEXbNtRKNlydpkirGjUhezTzPEukgwMi6Xdw.N1EiiCKvw8QMH1y', 'user9@example.com', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO tb_user (user_id, nickname, password, email, account_status, created_by, created_dt) VALUES
('user10', '사용자10', '$2a$10$PSEXbNtRKNlydpkirGjUhezTzPEukgwMi6Xdw.N1EiiCKvw8QMH1y', 'user10@example.com', 0, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

-- ================================================================
-- 공통코드 초기 데이터
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
('bill_std_req_type_cd', '과금기준신청구분코드', 'tb_bill_std_req.req_type_cd', 'SYSTEM', CURRENT_TIMESTAMP)
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
('std_reg_stat_cd', 'APPRV_REQ', '결재요청', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('std_reg_stat_cd', 'APPROVED', '승인', 4, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('std_reg_stat_cd', 'REJECTED', '반려', 5, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('std_reg_stat_cd', 'CANCELLED', '취소', 6, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

-- 과금기준신청구분코드
INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('bill_std_req_type_cd', 'NEW', '신규', 1, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('bill_std_req_type_cd', 'CHANGE', '변경', 2, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) VALUES
('bill_std_req_type_cd', 'CANCEL', '해지', 3, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)
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

-- ================================================================
-- 메뉴 초기 데이터 (부모 메뉴 → 자식 메뉴 순서, FK 제약 준수)
-- ================================================================

-- 최상위 메뉴 (Level 1)
INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU001', 'Main', '/main', NULL, 1, 'Y', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU002', '가입관리', NULL, NULL, 2, 'Y', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU017', '워크플로우', NULL, NULL, 3, 'Y', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU007', '시스템 설정', NULL, NULL, 4, 'Y', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU011', '게시판', NULL, NULL, 5, 'Y', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

-- 가입관리 하위 (Level 2)
INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU003', '가입관리', '/subscriptions', 'MNU002', 1, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU004', '과금기준', '/bill-std', 'MNU002', 2, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU005', '대표가입 관리', '/subscription-main', 'MNU002', 3, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU006', '특수가입관리', '/special-subscription', 'MNU002', 4, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU014', '과금기준필드설정', '/bill-std-field-config', 'MNU002', 5, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU015', '가입별과금기준', '/subs-bill-std', 'MNU002', 6, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU019', '결재관리', '/apprv-req', 'MNU002', 7, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU020', '가입별 월별과금량', '/subs-mth-bill-qty', 'MNU002', 8, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU021', '특수가입별 월별과금량', '/spcl-subs-mth-bill-qty', 'MNU002', 9, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU022', '특수가입별 월별빌링요소', '/spcl-subs-mth-bill-elem', 'MNU002', 10, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU024', '과금기준신청', '/bill-std-req', 'MNU002', 11, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU025', '과금기준신청목록', '/subs-bill-std-req', 'MNU002', 12, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

-- 시스템 설정 하위 (Level 2)
INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU008', '사용자관리', '/users', 'MNU007', 1, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU009', '공통코드관리', '/code', 'MNU007', 2, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU010', '메뉴관리', '/menu', 'MNU007', 3, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU013', '권한관리', '/role', 'MNU007', 4, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

-- 워크플로우 하위 (Level 2)
INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU018', '내 할일', '/wf/tasks', 'MNU017', 1, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU016', '워크플로우 정의', '/wf/process-def', 'MNU017', 2, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

-- 게시판 하위 (Level 2)
INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU012', 'Q&A', '/qna', 'MNU011', 1, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

-- ================================================================
-- 메뉴-역할 매핑 (ADMIN=전체, USER=메뉴관리/권한관리/WF정의 제외)
-- ================================================================
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU001', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU001', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU002', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU002', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU003', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU003', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU004', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU004', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU005', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU005', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU006', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU006', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU007', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU007', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU008', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU008', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU009', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU009', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU010', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU011', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU011', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU012', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU012', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU013', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU014', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU014', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU015', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU015', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU016', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU017', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU017', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU018', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU018', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU019', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU019', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU020', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU020', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU021', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU021', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU022', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU022', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU024', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU024', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU025', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU025', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);

-- ================================================================
-- 사용자-역할 매핑 (user01=ADMIN, 나머지=USER)
-- ================================================================
INSERT INTO tb_user_role (user_id, role_cd, created_by, created_dt) VALUES ('user01', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_user_role (user_id, role_cd, created_by, created_dt) VALUES ('user02', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_user_role (user_id, role_cd, created_by, created_dt) VALUES ('user03', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_user_role (user_id, role_cd, created_by, created_dt) VALUES ('user04', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_user_role (user_id, role_cd, created_by, created_dt) VALUES ('user05', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_user_role (user_id, role_cd, created_by, created_dt) VALUES ('user06', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_user_role (user_id, role_cd, created_by, created_dt) VALUES ('user07', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_user_role (user_id, role_cd, created_by, created_dt) VALUES ('user08', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_user_role (user_id, role_cd, created_by, created_dt) VALUES ('user09', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_user_role (user_id, role_cd, created_by, created_dt) VALUES ('user10', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);

-- ================================================================
-- TODO 공통코드
-- ================================================================
INSERT INTO tb_common_code (common_code, common_code_nm, created_by, created_dt) VALUES
('todo_status_cd', 'TODO상태코드', 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, created_by, created_dt) VALUES
('todo_status_cd', 'OPEN', '미완료', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm);

INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, created_by, created_dt) VALUES
('todo_status_cd', 'DONE', '완료', 2, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm);

-- 메뉴: MNU023 '내 할일' — 최상위 독립 메뉴, sort_order=2 (Main 바로 다음)
INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES
('MNU023', '내 할일', '/todos', NULL, 2, 'Y', 1, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);

-- 기존 메뉴 sort_order 재조정
UPDATE tb_menu SET sort_order = 3 WHERE menu_id = 'MNU002' AND sort_order = 2;
UPDATE tb_menu SET sort_order = 4 WHERE menu_id = 'MNU017' AND sort_order = 3;
UPDATE tb_menu SET sort_order = 5 WHERE menu_id = 'MNU007' AND sort_order = 4;
UPDATE tb_menu SET sort_order = 6 WHERE menu_id = 'MNU011' AND sort_order = 5;

-- 메뉴역할: ADMIN + USER 모두 접근
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU023', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);
INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES ('MNU023', 'USER', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);

-- ================================================================
-- 워크플로우 엔티티 유형 정의
-- ================================================================
INSERT INTO tb_wf_entity_type_def (entity_type_cd, entity_type_nm, table_nm, pk_column, status_column, status_cd_group, biz_key_column, biz_key_label, route_path, created_by, created_dt)
VALUES ('SUBSCRIPTION', '가입', 'tb_subscription', 'subs_id', 'subs_status_cd', 'subs_status_cd', 'subs_id', '가입ID', '/subscriptions', 'system', NOW())
ON DUPLICATE KEY UPDATE entity_type_nm=VALUES(entity_type_nm), table_nm=VALUES(table_nm), pk_column=VALUES(pk_column), status_column=VALUES(status_column), status_cd_group=VALUES(status_cd_group), biz_key_column=VALUES(biz_key_column), biz_key_label=VALUES(biz_key_label), route_path=VALUES(route_path);
INSERT INTO tb_wf_entity_type_def (entity_type_cd, entity_type_nm, table_nm, pk_column, status_column, status_cd_group, biz_key_column, biz_key_label, route_path, created_by, created_dt)
VALUES ('BILL_STD', '과금기준', 'tb_bill_std', 'bill_std_id', 'bill_std_stat_cd', 'bill_std_stat_cd', 'bill_std_id', '과금기준ID', '/bill-std', 'system', NOW())
ON DUPLICATE KEY UPDATE entity_type_nm=VALUES(entity_type_nm), table_nm=VALUES(table_nm), pk_column=VALUES(pk_column), status_column=VALUES(status_column), status_cd_group=VALUES(status_cd_group), biz_key_column=VALUES(biz_key_column), biz_key_label=VALUES(biz_key_label), route_path=VALUES(route_path);
