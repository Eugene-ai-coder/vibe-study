# 2단계: 요구사항 확정 — 공통코드 연동 _cd 필드 표준화

## 기능 명세

### F1. 공통 코드 SELECT 컴포넌트 (React/Vue 각 1개)
- `commonCode` prop으로 코드 그룹을 지정하면, 해당 상세코드를 API 조회하여 드롭다운 옵션으로 렌더링
- 유효기간(eff_start_dt ~ eff_end_dt) 내 항목만 노출
- sort_order 기준 정렬
- value/onChange(React), v-model(Vue) 바인딩 지원

### F2. 폼 입력 전환
- 기존 하드코딩 SELECT 및 텍스트 자유입력을 공통 코드 SELECT 컴포넌트로 교체
- 적용 대상:
  - SubscriptionForm: subsStatusCd (1개)
  - BillStdForm: svcCd, stdRegStatCd, billStdStatCd, pwrMetCalcMethCd, uprcDetMethCd, pueDetMethCd (6개)
  - SpecialSubscriptionForm: svcCd, specSubsStatCd (2개)
- RolePage(roleCd)는 이미 공통코드 기반 → 제외

### F3. 목록 코드명 표시
- 프론트 매핑 방식 채택: 공통코드를 한번 조회해 캐시, 코드값→코드명 변환
- 그리드/목록에서 코드값 대신 코드명을 표시
- 적용 대상: SubscriptionList, BillStdList, SpecialSubscriptionList (React/Vue)

### F4. 백엔드 유효기간 필터링
- CommonDtlCodeRepository에 유효기간 기준 필터링 쿼리 메서드 추가
- CommonCodeService에서 유효한 상세코드만 반환하는 메서드 제공

### F5. 매핑 선언 방식
- 프론트 상수 매핑(A 방식): 각 폼에서 `commonCode="subs_status_cd"` 직접 선언
- 향후 코드 그룹이 대폭 증가 시 중앙 매핑 파일(B 방식)로 리팩터링 고려

## 비기능 요건
- 코드 캐시는 화면 진입 시 조회 (앱 전역 캐시까지는 불요)
- 기존 API 응답 형식(DTO) 변경 없음

## 수용 기준
- [ ] 모든 _cd 필드가 공통코드 SELECT 드롭다운으로 입력 가능
- [ ] 유효기간 외 코드는 드롭다운에 노출되지 않음
- [ ] 그리드에서 코드값이 아닌 코드명이 표시됨
- [ ] 기존 하드코딩 SELECT 및 STATUS_LABEL 등 상수 매핑 제거됨
- [ ] React/Vue 모두 동일하게 동작

## 제외 범위
- 백엔드 DTO에 코드명 필드 추가 (프론트 매핑으로 대체)
- 기존 저장 데이터 마이그레이션 (신규/수정 시에만 공통코드 적용)
- RolePage roleCd (이미 공통코드 기반 완료)
- SecurityConfig 경로 권한 변경 (현행 유지)

## 용어 정의
| 용어 | 정의 |
|------|------|
| 공통코드 그룹 | TB_COMMON_CODE의 common_code (예: subs_status_cd) |
| 상세코드 | TB_COMMON_DTL_CODE의 common_dtl_code (예: ACTIVE) |
| 코드명 | common_dtl_code_nm (예: 활성) |
| 프론트 매핑 | 공통코드를 프론트에서 조회·캐시하여 코드값→코드명 변환하는 방식 |

## 향후 고려사항
- 코드 그룹 증가 시 중앙 매핑 설정 파일(codeFieldMap.js) 도입 검토
- 앱 전역 코드 캐시 (Context/Store) 도입 검토

---
## Summary (다음 단계 전달용)
- **핵심 기능**: 공통 CodeSelect 컴포넌트(React/Vue), 폼 9개 필드 전환, 목록 코드명 표시, 유효기간 필터링
- **코드명 표시**: 프론트 매핑 방식 (백엔드 DTO 변경 없음)
- **매핑 선언**: 각 폼에서 commonCode prop 직접 선언 (향후 중앙 매핑 리팩터링 고려)
- **수용 기준**: SELECT 드롭다운 동작, 유효기간 필터링, 그리드 코드명 표시, 하드코딩 제거
- **제외**: DTO 확장, 기존 데이터 마이그레이션, RolePage, SecurityConfig 변경
