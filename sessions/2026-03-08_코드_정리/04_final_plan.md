# 4단계: 최종 구현 계획 — 공통코드 연동 _cd 필드 표준화

## Critic 반영 사항

| # | 관점 | 이슈 | 반영 |
|---|------|------|------|
| 1 | 누락 | 변경 파일 목록에 CommonCodeService.java, CommonCodeController.java 누락 | 백엔드 파일 목록에 4파일로 수정 |
| 2 | 의존성 모순 | CommonCodeSelect가 기존 `getDetails` API 사용 → 유효기간 필터링 미적용 (요구사항 F1 위반) | CommonCodeSelect에서 `/effective` 엔드포인트를 바로 사용하도록 변경. commonCodeApi에 `getEffectiveDetails` 추가 |
| 3 | 누락 | React/Vue commonCodeApi.js에 effective API 함수 추가 누락 | 변경 파일 목록에 추가 |

---

## 구현 전략 개요

1. **백엔드**: Repository 유효기간 쿼리 + Service/Controller effective 엔드포인트 추가
2. **프론트 API**: commonCodeApi에 `getEffectiveDetails` 함수 추가
3. **프론트 공통**: React/Vue 각각 CommonCodeSelect + useCommonCodeLabel 신규 생성
4. **폼 전환**: 하드코딩 SELECT/텍스트 입력을 CommonCodeSelect로 교체
5. **목록 전환**: 그리드에서 코드값 대신 코드명 표시

## 변경 파일 목록

### 백엔드 (4파일 수정)
| 파일 | 변경 유형 |
|------|----------|
| CommonDtlCodeRepository.java | 수정 — 유효기간 필터링 쿼리 추가 |
| CommonCodeService.java | 수정 — findEffectiveDetails() 인터페이스 추가 |
| CommonCodeServiceImpl.java | 수정 — findEffectiveDetails() 구현 추가 |
| CommonCodeController.java | 수정 — GET /{commonCode}/details/effective 엔드포인트 추가 |

### React (8파일: 신규 2 + 수정 6)
| 파일 | 변경 유형 |
|------|----------|
| frontend/src/components/common/CommonCodeSelect.jsx | **신규** |
| frontend/src/hooks/useCommonCodeLabel.js | **신규** |
| frontend/src/api/commonCodeApi.js | 수정 — getEffectiveDetails 추가 |
| frontend/src/components/subscription/SubscriptionForm.jsx | 수정 |
| frontend/src/components/subscription/SubscriptionList.jsx | 수정 |
| frontend/src/components/billstd/BillStdForm.jsx | 수정 |
| frontend/src/components/special-subscription/SpecialSubscriptionForm.jsx | 수정 |
| frontend/src/components/special-subscription/SpecialSubscriptionList.jsx | 수정 |

### Vue (6파일: 신규 2 + 수정 4)
| 파일 | 변경 유형 |
|------|----------|
| frontend-vue/src/components/common/CommonCodeSelect.vue | **신규** |
| frontend-vue/src/composables/useCommonCodeLabel.js | **신규** |
| frontend-vue/src/api/commonCodeApi.js | 수정 — getEffectiveDetails 추가 |
| frontend-vue/src/pages/SubscriptionPage.vue | 수정 |
| frontend-vue/src/pages/BillStdPage.vue | 수정 |
| frontend-vue/src/pages/SpecialSubscriptionPage.vue | 수정 |

**총 18파일** (백엔드 4 + React 8 + Vue 6)

## 단계별 구현 순서

---

### Step 1. 백엔드 — 유효기간 필터링

#### 1-1. CommonDtlCodeRepository.java

**After:**
```java
public interface CommonDtlCodeRepository extends JpaRepository<CommonDtlCode, CommonDtlCodeId> {
    List<CommonDtlCode> findByIdCommonCodeOrderBySortOrder(String commonCode);
    boolean existsByIdCommonCode(String commonCode);

    List<CommonDtlCode> findByIdCommonCodeAndEffStartDtLessThanEqualAndEffEndDtGreaterThanEqualOrderBySortOrder(
            String commonCode, LocalDateTime now1, LocalDateTime now2);
}
```

#### 1-2. CommonCodeService.java — 인터페이스 추가

```java
List<CommonDtlCodeResponseDto> findEffectiveDetails(String commonCode);
```

#### 1-3. CommonCodeServiceImpl.java — 구현 추가

```java
@Override
public List<CommonDtlCodeResponseDto> findEffectiveDetails(String commonCode) {
    LocalDateTime now = LocalDateTime.now();
    return commonDtlCodeRepository
            .findByIdCommonCodeAndEffStartDtLessThanEqualAndEffEndDtGreaterThanEqualOrderBySortOrder(
                    commonCode, now, now)
            .stream().map(this::toDtlDto).collect(Collectors.toList());
}
```

#### 1-4. CommonCodeController.java — 엔드포인트 추가

```java
@GetMapping("/{commonCode}/details/effective")
public List<CommonDtlCodeResponseDto> getEffectiveDetails(@PathVariable String commonCode) {
    return commonCodeService.findEffectiveDetails(commonCode);
}
```

---

### Step 2. 프론트 API 확장

#### 2-1. React commonCodeApi.js — 함수 추가

```js
getEffectiveDetails: (code) => apiClient.get(`/common-codes/${code}/details/effective`).then(r => r.data),
```

#### 2-2. Vue commonCodeApi.js — 동일 함수 추가

```js
getEffectiveDetails: (code) => apiClient.get(`/common-codes/${code}/details/effective`).then(r => r.data),
```

---

### Step 3. React — 공통 컴포넌트 신규 생성

#### 3-1. CommonCodeSelect.jsx (신규)

```jsx
import { useState, useEffect } from 'react'
import { commonCodeApi } from '../../api/commonCodeApi'

export default function CommonCodeSelect({ commonCode, name, value, onChange, className, disabled }) {
  const [options, setOptions] = useState([])

  useEffect(() => {
    if (!commonCode) return
    commonCodeApi.getEffectiveDetails(commonCode).then(setOptions).catch(() => setOptions([]))
  }, [commonCode])

  return (
    <select name={name} value={value || ''} onChange={onChange} disabled={disabled}
      className={className || 'w-full h-8 border border-gray-300 rounded-lg px-3 text-sm'}>
      <option value="">선택</option>
      {options.map(opt => (
        <option key={opt.commonDtlCode} value={opt.commonDtlCode}>
          {opt.commonDtlCodeNm}
        </option>
      ))}
    </select>
  )
}
```

#### 3-2. useCommonCodeLabel.js (신규)

```js
import { useState, useEffect, useCallback } from 'react'
import { commonCodeApi } from '../api/commonCodeApi'

export default function useCommonCodeLabel(commonCodes = []) {
  const [codeMap, setCodeMap] = useState({})

  useEffect(() => {
    if (commonCodes.length === 0) return
    Promise.all(
      commonCodes.map(code =>
        commonCodeApi.getDetails(code).then(details => [code, details])
      )
    ).then(results => {
      const map = {}
      results.forEach(([code, details]) => {
        map[code] = {}
        details.forEach(d => { map[code][d.commonDtlCode] = d.commonDtlCodeNm })
      })
      setCodeMap(map)
    })
  }, [commonCodes.join(',')])

  const getLabel = useCallback(
    (commonCode, codeValue) => codeMap[commonCode]?.[codeValue] || codeValue,
    [codeMap]
  )

  return { getLabel }
}
```

> 주의: useCommonCodeLabel은 `getDetails`(유효기간 무관 전체) 사용. 목록에서 이미 저장된 코드값의 코드명도 표시해야 하므로 만료 코드도 포함 필요.

---

### Step 4. React — 폼/목록 전환

#### 4-1. SubscriptionForm.jsx

하드코딩 `<select>` (라인 32-40) → `<CommonCodeSelect>` 교체

```jsx
import CommonCodeSelect from '../common/CommonCodeSelect'

<CommonCodeSelect commonCode="subs_status_cd" name="subsStatusCd"
  value={data.subsStatusCd} onChange={onChange} />
```

#### 4-2. SubscriptionList.jsx

`STATUS_LABEL` 상수 제거 → `useCommonCodeLabel` 사용

```jsx
import useCommonCodeLabel from '../../hooks/useCommonCodeLabel'

const CODE_GROUPS = ['subs_status_cd']
const { getLabel } = useCommonCodeLabel(CODE_GROUPS)

// 라인 62: {STATUS_LABEL[item.subsStatusCd] || item.subsStatusCd}
// → {getLabel('subs_status_cd', item.subsStatusCd)}
```

> 배지 색상 로직(ACTIVE→green, SUSPENDED→yellow 등)은 코드값 기반이므로 유지.

#### 4-3. BillStdForm.jsx

6개 `<Field>` → `<CommonCodeSelect>` 교체. 각 필드를 `<div>` + `<label>` + `<CommonCodeSelect>`로 변환.

| 필드 | commonCode |
|------|-----------|
| svcCd (라인 55) | svc_cd |
| stdRegStatCd (라인 59) | std_reg_stat_cd |
| billStdStatCd (라인 60) | bill_std_stat_cd |
| pwrMetCalcMethCd (라인 64) | pwr_met_calc_meth_cd |
| uprcDetMethCd (라인 65) | uprc_det_meth_cd |
| pueDetMethCd (라인 71) | pue_det_meth_cd |

#### 4-4. SpecialSubscriptionForm.jsx

2개 `<Field>` → `<CommonCodeSelect>` 교체.

| 필드 | commonCode |
|------|-----------|
| svcCd (라인 47) | svc_cd |
| specSubsStatCd (라인 50) | spec_subs_stat_cd |

#### 4-5. SpecialSubscriptionList.jsx

DataGrid columns에 코드명 변환 적용. DataGrid의 cell/formatter 지원 여부에 따라 구현 방식 결정.

---

### Step 5. Vue — 공통 컴포넌트 신규 생성

#### 5-1. CommonCodeSelect.vue (신규)

```vue
<template>
  <select :value="modelValue" @change="$emit('update:modelValue', $event.target.value)"
    :disabled="disabled" :class="selectClass">
    <option value="">선택</option>
    <option v-for="opt in options" :key="opt.commonDtlCode" :value="opt.commonDtlCode">
      {{ opt.commonDtlCodeNm }}
    </option>
  </select>
</template>

<script setup>
import { ref, watch } from 'vue'
import { commonCodeApi } from '../../api/commonCodeApi'

const props = defineProps({
  commonCode: { type: String, required: true },
  modelValue: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  selectClass: { type: String, default: 'w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 bg-white' },
})

defineEmits(['update:modelValue'])

const options = ref([])

watch(() => props.commonCode, async (code) => {
  if (!code) return
  try { options.value = await commonCodeApi.getEffectiveDetails(code) }
  catch { options.value = [] }
}, { immediate: true })
</script>
```

#### 5-2. useCommonCodeLabel.js (신규 — Vue 합성함수)

```js
import { ref, watch } from 'vue'
import { commonCodeApi } from '../api/commonCodeApi'

export function useCommonCodeLabel(commonCodes = []) {
  const codeMap = ref({})

  watch(() => commonCodes, async (codes) => {
    const results = await Promise.all(
      codes.map(code => commonCodeApi.getDetails(code).then(details => [code, details]))
    )
    const map = {}
    results.forEach(([code, details]) => {
      map[code] = {}
      details.forEach(d => { map[code][d.commonDtlCode] = d.commonDtlCodeNm })
    })
    codeMap.value = map
  }, { immediate: true })

  const getLabel = (commonCode, codeValue) =>
    codeMap.value[commonCode]?.[codeValue] || codeValue

  return { getLabel }
}
```

---

### Step 6. Vue — 페이지 전환

#### 6-1. SubscriptionPage.vue

하드코딩 `<select>` (라인 65-71) → `<CommonCodeSelect>` 교체. 목록 columns에 코드명 변환 적용.

#### 6-2. BillStdPage.vue

`svcCd` input (라인 45) → `<CommonCodeSelect>` 교체.

#### 6-3. SpecialSubscriptionPage.vue

`svcCd` (라인 64), `specSubsStatCd` (라인 76) input → `<CommonCodeSelect>` 교체. 목록 columns에 코드명 변환 적용.

---

## 확인 필요 사항 (구현 시 해결)

1. **DataGrid cell/formatter 지원 여부**: React DataGrid의 `cell` prop, Vue DataGrid의 `formatter` prop 지원 확인. 미지원 시 데이터 자체를 computed로 변환.
2. **Vue composables 폴더 존재 여부**: 없으면 신규 생성.
3. **BillStdPage.vue**: 현재 svcCd만 표시. stdRegStatCd 등 추가 _cd 필드가 Vue 폼에 없으므로, React와 동일 수준으로 맞출지 결정 (현재 범위: 있는 필드만 전환).

## 테스트 계획

1. 백엔드 빌드 성공 (`./mvnw compile`)
2. 프론트 빌드 성공 (`npm run build`)
3. 각 화면 CommonCodeSelect 드롭다운 로딩 확인
4. 드롭다운 선택 → 저장 → 재조회 시 값 유지
5. 그리드에서 코드명 표시 확인

## 롤백 방안

Git 커밋 단위로 롤백. 백엔드(Step 1)과 프론트엔드(Step 2~6)를 별도 커밋으로 분리.
