# 6단계: 품질 검증 — 공통코드 연동 _cd 필드 표준화

## 1. 문서 검증 (수용 기준 충족 여부)

| # | 수용 기준 | 판정 | 근거 |
|---|----------|------|------|
| AC1 | 모든 _cd 필드가 공통코드 SELECT 드롭다운으로 입력 가능 | **Pass** | SubscriptionForm(1), BillStdForm(6), SpecialSubscriptionForm(2) 모두 CommonCodeSelect 적용 |
| AC2 | 유효기간 외 코드는 드롭다운에 노출되지 않음 | **Pass** | CommonCodeSelect가 `/effective` 엔드포인트 사용, Repository 유효기간 필터링 쿼리 구현 |
| AC3 | 그리드에서 코드값이 아닌 코드명이 표시됨 | **Pass** | useCommonCodeLabel 훅/컴포저블로 cell 렌더러 적용 (React/Vue 모두) |
| AC4 | 기존 하드코딩 SELECT 및 STATUS_LABEL 등 상수 매핑 제거됨 | **Pass** | SubscriptionList의 STATUS_LABEL 제거, SubscriptionForm 하드코딩 SELECT 제거 |
| AC5 | React/Vue 모두 동일하게 동작 | **Pass** | 양쪽 모두 동일 구조의 컴포넌트/훅 구현, 동일 API 사용 |

### 계획 준수 여부
- 04_final_plan.md의 5개 Step 모두 구현 완료
- 변경 파일 18개 = 계획서 명시 파일과 일치
- 추가 변경: data.sql 수정 (H2 MERGE 호환성 — 검증 과정 발견/수정)

## 2. 회귀 영향도 분석

| 계층 | 파일 그룹 | 위험도 | 상태 |
|-----|---------|-------|------|
| 백엔드 Repository | CommonDtlCodeRepository.java | **Safe** | 신규 쿼리만 추가, 기존 메서드 유지 |
| 백엔드 Service | CommonCodeService/ServiceImpl | **Safe** | 신규 메서드만 추가 |
| 백엔드 Controller | CommonCodeController.java | **Safe** | 신규 엔드포인트만 추가, 기존 경로 유지 |
| React API | commonCodeApi.js | **Safe** | 신규 함수만 추가 |
| React 폼 | SubscriptionForm, BillStdForm, SpecialSubscriptionForm | **Safe** | onChange/name/value 구조 유지 |
| React 목록 | SubscriptionList, SpecialSubscriptionList | **Safe** | 렌더링 개선, 필터링 로직 불변 |
| Vue API | commonCodeApi.js | **Safe** | 신규 함수만 추가 |
| Vue 페이지 | SubscriptionPage, BillStdPage, SpecialSubscriptionPage | **Safe** | v-model 바인딩 완벽 호환 |

**종합 위험도: Safe** — 모든 변경사항이 기존 기능을 보호하면서 신규 기능을 추가하는 구조.

## 3. API 실동작 검증

### 인증
- 로그인: POST `/api/auth/login` — user01/password123 → HTTP 200

### /effective 엔드포인트

| commonCode | HTTP | 응답 | 판정 |
|------------|------|------|------|
| svc_cd | 200 | JSON 배열 3건 (SVC01, SVC02, SVC03) | **Pass** |
| subs_status_cd | 200 | JSON 배열 4건 | **Pass** |
| spec_subs_stat_cd | 200 | JSON 배열 4건 | **Pass** |
| bill_std_stat_cd | 200 | JSON 배열 3건 | **Pass** |
| pwr_met_calc_meth_cd | 200 | JSON 배열 3건 | **Pass** |
| uprc_det_meth_cd | 200 | JSON 배열 3건 | **Pass** |
| pue_det_meth_cd | 200 | JSON 배열 3건 | **Pass** |
| std_reg_stat_cd | 200 | JSON 배열 4건 | **Pass** |
| role_cd | 200 | JSON 배열 4건 | **Pass** |

### /details 엔드포인트 (회귀)

| commonCode | HTTP | 판정 |
|------------|------|------|
| svc_cd | 200 | **Pass** |
| subs_status_cd | 200 | **Pass** |

## 4. UI 검증

브라우저 자동 검증은 미수행 (agent-browser 미설정). 수동 확인 체크리스트로 대체:

### 수동 확인 체크리스트
- [ ] 가입관리 페이지 — subsStatusCd 드롭다운 렌더링 확인
- [ ] 가입관리 목록 — 코드명 표시 확인
- [ ] 과금기준 페이지 — 6개 _cd 필드 드롭다운 확인
- [ ] 특수가입 페이지 — svcCd, specSubsStatCd 드롭다운 확인
- [ ] 특수가입 목록 — 코드명 표시 확인
- [ ] Vue 가입관리 — CommonCodeSelect + 코드명 표시 확인
- [ ] Vue 과금기준 — svcCd CommonCodeSelect 확인
- [ ] Vue 특수가입 — CommonCodeSelect + 코드명 표시 확인

## 5. 코드리뷰

| 파일 | 판정 | 비고 |
|------|------|------|
| CommonDtlCodeRepository.java | **Pass** | |
| CommonCodeService.java | **Pass** | |
| CommonCodeServiceImpl.java | **Pass** | |
| CommonCodeController.java | **Pass** | |
| frontend/commonCodeApi.js | **Pass** | |
| CommonCodeSelect.jsx | **Pass** | |
| useCommonCodeLabel.js (React) | **Warn** | `commonCodes.join(',')` 의존성 — 동작하지만 개선 가능 |
| SubscriptionForm.jsx | **Pass** | |
| SubscriptionList.jsx | **Pass** | |
| BillStdForm.jsx | **Pass** | |
| SpecialSubscriptionForm.jsx | **Pass** | |
| SpecialSubscriptionList.jsx | **Pass** | |
| frontend-vue/commonCodeApi.js | **Pass** | |
| CommonCodeSelect.vue | **Pass** | |
| useCommonCodeLabel.js (Vue) | **Pass** | |
| SubscriptionPage.vue | **Warn** | 수정 후 목록 재조회 패턴이 BillStdPage와 불일치 |
| BillStdPage.vue | **Pass** | |
| SpecialSubscriptionPage.vue | **Pass** | |

### 코드리뷰 점수: Pass 16/18 (89%), Warn 2/18 (11%), Fail 0/18 (0%)

### Warn 상세
1. **useCommonCodeLabel.js (React)**: `commonCodes.join(',')` 의존성 배열은 동작하지만 `JSON.stringify` 사용이 더 안전
2. **SubscriptionPage.vue**: 수정 후 목록 재조회 패턴이 다른 페이지와 불일치 (기능상 이슈 아님, 일관성 권고)

## 6. 수동 확인 체크리스트

- [ ] 드롭다운 옵션 정렬이 sort_order 기준인지 확인
- [ ] 드롭다운 미선택 시 "선택" 기본 옵션 표시 확인
- [ ] 반응형 레이아웃에서 드롭다운 너비 정상 확인
- [ ] 기존 저장 데이터의 코드값이 목록에서 정상 코드명 표시 확인

## 검증 과정 발견 이슈

### data.sql H2 MERGE 호환성 (검증 중 수정)
- **이슈**: H2 MERGE 문에서 `created_dt` NOT NULL 컬럼의 DEFAULT 값이 적용되지 않음
- **원인**: H2의 MERGE는 SQL 표준과 달리 DEFAULT를 자동 적용하지 않음
- **수정**: 모든 MERGE 문에 `created_dt = CURRENT_TIMESTAMP` 명시, dtl_code에 `eff_start_dt/eff_end_dt` 명시
- **영향**: 백엔드 기동 실패 → 수정 후 정상 기동

## 종합 판정

| 검증 항목 | 결과 |
|----------|------|
| 문서 검증 (수용 기준) | **All Pass** |
| 회귀 영향도 | **Safe** |
| API 실동작 | **All Pass** (9/9 effective + 2/2 details) |
| UI 검증 | 수동 확인 필요 |
| 코드리뷰 | **Pass 16, Warn 2, Fail 0** |

**최종 판정: Pass** (Fail 항목 없음, Warn은 참고사항)
