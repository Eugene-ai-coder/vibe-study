# 1단계: 분석 — 공통코드 연동 _cd 필드 표준화

## 현행 구조 요약

### 공통코드 인프라 (구축 완료)
- **테이블**: TB_COMMON_CODE(헤더) → TB_COMMON_DTL_CODE(상세) 1:N 관계
  - 복합PK: `(common_code, common_dtl_code)`
  - 유효기간: `eff_start_dt` ~ `eff_end_dt` (기본값 9999-12-31)
  - 정렬: `sort_order` (INTEGER, 인덱스 존재)
- **백엔드**: Entity → Repository → Service → Controller 완전 구현
  - API: `GET /api/common-codes/{commonCode}/details` → `List<CommonDtlCodeResponseDto>`
  - Repository: `findByIdCommonCodeOrderBySortOrder(String commonCode)` — sort_order 정렬 O, **유효기간 필터링 X**
- **프론트엔드**: `commonCodeApi.js` (React/Vue 모두), `useCommonCode.js` 훅 존재 → **실제 폼에서 미사용**
- **초기 데이터**: data.sql에 9개 그룹, 33개 상세코드 MERGE로 등록 완료

### _cd 필드 현황

| 도메인 | 필드 | 현재 입력 | 현재 표시 | 공통코드 그룹 |
|--------|------|----------|----------|-------------|
| Subscription | subsStatusCd | 하드코딩 SELECT | 하드코딩 STATUS_LABEL 매핑 | subs_status_cd |
| BillStd | svcCd | text input | 코드값 그대로 | svc_cd |
| BillStd | stdRegStatCd | text input | 코드값 그대로 | std_reg_stat_cd |
| BillStd | billStdStatCd | text input | 코드값 그대로 | bill_std_stat_cd |
| BillStd | pwrMetCalcMethCd | text input | 코드값 그대로 | pwr_met_calc_meth_cd |
| BillStd | uprcDetMethCd | text input | 코드값 그대로 | uprc_det_meth_cd |
| BillStd | pueDetMethCd | text input | 코드값 그대로 | pue_det_meth_cd |
| SpecialSubscription | svcCd | text input | 코드값 그대로 | svc_cd |
| SpecialSubscription | specSubsStatCd | text input | 코드값 그대로 | spec_subs_stat_cd |
| UserRole/MenuRole | roleCd | 이미 공통코드 기반 | 코드+코드명 표시 | role_cd (완료) |

## 영향 범위

### Backend 수정 대상
| 파일 | 수정 내용 |
|------|----------|
| CommonDtlCodeRepository.java | 유효기간 필터링 쿼리 메서드 추가 |
| CommonCodeServiceImpl.java | 유효한 상세코드만 반환하는 메서드 추가 |

### Frontend 수정 대상 (React)
| 파일 | 수정 내용 |
|------|----------|
| **신규** CommonCodeSelect.jsx | 공통 코드 SELECT 컴포넌트 |
| SubscriptionForm.jsx | 하드코딩 SELECT → CommonCodeSelect |
| SubscriptionList.jsx | STATUS_LABEL 제거 → 코드명 표시 |
| BillStdForm.jsx | 6개 text input → CommonCodeSelect |
| BillStdList.jsx | 코드값 → 코드명 표시 |
| SpecialSubscriptionForm.jsx | 2개 text input → CommonCodeSelect |
| SpecialSubscriptionList.jsx | 코드값 → 코드명 표시 |

### Frontend 수정 대상 (Vue)
| 파일 | 수정 내용 |
|------|----------|
| **신규** CommonCodeSelect.vue | 공통 코드 SELECT 컴포넌트 |
| SubscriptionPage.vue | 하드코딩 SELECT → CommonCodeSelect |
| BillStdPage.vue | text input → CommonCodeSelect |
| SpecialSubscriptionPage.vue | text input → CommonCodeSelect |

## 의존성 그래프

```
TB_COMMON_DTL_CODE
  ↑ API: /api/common-codes/{code}/details
  ↑ Repository: findByIdCommonCodeOrderBySortOrder()
  │
  ├─ React: CommonCodeSelect.jsx (신규)
  │   ├─ SubscriptionForm.jsx (subsStatusCd)
  │   ├─ BillStdForm.jsx (6개 필드)
  │   └─ SpecialSubscriptionForm.jsx (2개 필드)
  │
  └─ Vue: CommonCodeSelect.vue (신규)
      ├─ SubscriptionPage.vue (subsStatusCd)
      ├─ BillStdPage.vue (svcCd 등)
      └─ SpecialSubscriptionPage.vue (svcCd, specSubsStatCd)
```

## 기존 테스트 현황
- 테스트 코드 없음 (수동 검증 기반)

## 기술적 제약사항
- **DB**: H2 파일 DB, `ddl-auto=update`, SQL 초기화 순서: schema.sql → JPA → data.sql
- **보안**: `/api/common-codes/**`는 `/api/**` 규칙에 따라 인증 필요 (명시적 설정 없음)
- **React Field 컴포넌트**: 이미 `options` prop 지원 → SELECT 렌더링 가능
- **DTO 응답**: 현재 코드값만 반환, 코드명 미포함 (프론트에서 매핑 필요)

## 리스크 식별
1. **유효기간 필터링 미구현**: Repository에 쿼리 추가 필요 (낮음)
2. **기존 데이터 정합성**: 이미 저장된 코드값이 공통코드 상세코드와 불일치할 가능성 (중간)
3. **목록 코드명 표시 방식**: DTO에 코드명 추가 vs 프론트에서 매핑 — 설계 결정 필요 (중간)

---
## Summary (다음 단계 전달용)
- **인프라 완비**: 공통코드 테이블/API/초기데이터(9그룹 33코드) 모두 구축됨
- **핵심 갭**: Repository 유효기간 필터링 없음, 폼에서 공통코드 API 미사용
- **수정 대상**: Backend 2파일, React 7파일(신규1), Vue 4파일(신규1) = 총 13파일
- **roleCd는 이미 완료**: RoleController에서 공통코드 기반 조회 구현됨
- **설계 결정 필요**: 목록 코드명 표시를 DTO 확장 vs 프론트 매핑 중 선택
- **Field 컴포넌트**: React의 Field가 options prop 지원 → 활용 가능
- **리스크**: 기존 저장 데이터와 공통코드 불일치 가능성
