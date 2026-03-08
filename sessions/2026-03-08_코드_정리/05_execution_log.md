# 5단계: 실행 기록

## 완료 항목

### Step 1. 백엔드 (4파일)
- [x] CommonDtlCodeRepository.java — 유효기간 필터링 쿼리 추가
- [x] CommonCodeService.java — findEffectiveDetails() 인터페이스 추가
- [x] CommonCodeServiceImpl.java — findEffectiveDetails() 구현
- [x] CommonCodeController.java — GET /{commonCode}/details/effective 엔드포인트 추가

### Step 2. 프론트 API (2파일)
- [x] frontend/src/api/commonCodeApi.js — getEffectiveDetails 추가
- [x] frontend-vue/src/api/commonCodeApi.js — getEffectiveDetails 추가

### Step 3. React 공통 컴포넌트 (2파일 신규)
- [x] frontend/src/components/common/CommonCodeSelect.jsx — 신규 생성
- [x] frontend/src/hooks/useCommonCodeLabel.js — 신규 생성

### Step 4. React 폼/목록 전환 (5파일)
- [x] SubscriptionForm.jsx — 하드코딩 SELECT → CommonCodeSelect
- [x] SubscriptionList.jsx — STATUS_LABEL 제거 → useCommonCodeLabel
- [x] BillStdForm.jsx — 6개 _cd 필드 Field → CodeField(CommonCodeSelect)
- [x] SpecialSubscriptionForm.jsx — 2개 _cd 필드 → CommonCodeSelect
- [x] SpecialSubscriptionList.jsx — DataGrid cell 렌더러로 코드명 표시

### Step 5. Vue 공통 컴포넌트 (2파일 신규)
- [x] frontend-vue/src/components/common/CommonCodeSelect.vue — 신규 생성
- [x] frontend-vue/src/composables/useCommonCodeLabel.js — 신규 생성

### Step 6. Vue 페이지 전환 (3파일)
- [x] SubscriptionPage.vue — 하드코딩 SELECT → CommonCodeSelect + 목록 코드명 표시
- [x] BillStdPage.vue — svcCd input → CommonCodeSelect
- [x] SpecialSubscriptionPage.vue — 2개 input → CommonCodeSelect + 목록 코드명 표시

## 빌드 결과
- [x] 백엔드: `./mvnw compile` — BUILD SUCCESS (1.774s)
- [x] React: `npm run build` — 성공 (6.70s)
- [x] Vue: `npm run build` — 성공 (3.05s)

## 총 변경 파일: 18파일 (백엔드 4 + React 8 + Vue 6)
