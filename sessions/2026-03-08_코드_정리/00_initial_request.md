# 공통코드 연동 — _cd 필드 표준화

## 1. 개요
- **목적**: 모든 _cd 필드는 공통코드 테이블 기반으로 입력·선택·표시한다.
- **범위**: _cd로 끝나는 모든 필드 대상. React/Vue 프론트엔드 공통 적용.

## 2. 데이터
- 기존 TB_COMMON_CODE / TB_COMMON_DTL_CODE를 활용한다 (스키마 변경 없음).
- 아래 코드 그룹의 초기 데이터를 등록한다:
  SUBS_STATUS(가입상태), SVC(서비스), STD_REG_STAT(등록진행상태),
  BILL_STD_STAT(과금기준상태), PWR_MET_CALC_METH(전력종량계산방식),
  UPRC_DET_METH(단가결정방식), PUE_DET_METH(PUE결정방식),
  SPEC_SUBS_STAT(특수가입상태), ROLE(역할)

## 3. UI 원칙
- _cd 필드의 입력은 공통코드 SELECT(드롭다운)로 제공한다.
- 그리드·목록에서는 코드값이 아닌 코드명을 표시한다.
- 공통 코드 SELECT 컴포넌트를 React/Vue 각각 하나씩 제공한다.

## 4. 적용 대상
- SubscriptionForm: subs_status_cd
- BillStdForm: svc_cd, std_reg_stat_cd, bill_std_stat_cd,
  pwr_met_calc_meth_cd, uprc_det_meth_cd, pue_det_meth_cd
- SpecialSubscriptionForm: svc_cd, spec_subs_stat_cd
- RolePage: role_cd

## 5. 조건
- 유효기간(eff_start_dt ~ eff_end_dt) 내 항목만 노출한다.
- sort_order 순서로 정렬한다.
- 기존 하드코딩 SELECT 및 텍스트 자유입력은 모두 제거한다.

## 6. 설계 요구
- _cd 필드와 공통코드 그룹 간의 매핑을 쉽게 선언·관리할 수 있는 구조를 제공한다.
