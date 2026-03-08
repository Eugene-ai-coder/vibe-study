# 3단계: 구현 설계 — 공통코드 연동 _cd 필드 표준화

## 구현 전략 개요

1. **백엔드**: Repository에 유효기간 필터링 쿼리 추가, Service에 유효 상세코드 조회 메서드 추가
2. **프론트 공통**: React/Vue 각각 CommonCodeSelect 컴포넌트 + useCommonCodeLabel 훅/합성함수 신규 생성
3. **폼 전환**: 각 폼의 하드코딩 SELECT/텍스트 입력을 CommonCodeSelect로 교체
4. **목록 전환**: 그리드에서 코드값 대신 코드명 표시

## 변경 파일 목록

### 백엔드 (2파일 수정)
| 파일 | 변경 유형 |
|------|----------|
| CommonDtlCodeRepository.java | 수정 — 유효기간 필터링 쿼리 추가 |
| CommonCodeServiceImpl.java | 수정 — findEffectiveDetails() 메서드 추가 |

### React (7파일: 신규 2 + 수정 5)
| 파일 | 변경 유형 |
|------|----------|
| frontend/src/components/common/CommonCodeSelect.jsx | **신규** |
| frontend/src/hooks/useCommonCodeLabel.js | **신규** |
| frontend/src/components/subscription/SubscriptionForm.jsx | 수정 |
| frontend/src/components/subscription/SubscriptionList.jsx | 수정 |
| frontend/src/components/billstd/BillStdForm.jsx | 수정 |
| frontend/src/components/special-subscription/SpecialSubscriptionForm.jsx | 수정 |
| frontend/src/components/special-subscription/SpecialSubscriptionList.jsx | 수정 |

### Vue (5파일: 신규 1 + 수정 4)
| 파일 | 변경 유형 |
|------|----------|
| frontend-vue/src/components/common/CommonCodeSelect.vue | **신규** |
| frontend-vue/src/composables/useCommonCodeLabel.js | **신규** |
| frontend-vue/src/pages/SubscriptionPage.vue | 수정 |
| frontend-vue/src/pages/BillStdPage.vue | 수정 |
| frontend-vue/src/pages/SpecialSubscriptionPage.vue | 수정 |

## 단계별 구현 순서

---

### Step 1. 백엔드 — 유효기간 필터링

#### 1-1. CommonDtlCodeRepository.java

**Before:**
```java
public interface CommonDtlCodeRepository extends JpaRepository<CommonDtlCode, CommonDtlCodeId> {
    List<CommonDtlCode> findByIdCommonCodeOrderBySortOrder(String commonCode);
    boolean existsByIdCommonCode(String commonCode);
}
```

**After:**
```java
public interface CommonDtlCodeRepository extends JpaRepository<CommonDtlCode, CommonDtlCodeId> {
    List<CommonDtlCode> findByIdCommonCodeOrderBySortOrder(String commonCode);
    boolean existsByIdCommonCode(String commonCode);

    List<CommonDtlCode> findByIdCommonCodeAndEffStartDtLessThanEqualAndEffEndDtGreaterThanEqualOrderBySortOrder(
            String commonCode, LocalDateTime now1, LocalDateTime now2);
}
```

#### 1-2. CommonCodeServiceImpl.java — findEffectiveDetails() 추가

**Before:** `findDetails()` — 전체 반환, 유효기간 무시

**After:** 기존 `findDetails()` 유지 + 새 메서드 추가
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

> CommonCodeService 인터페이스에도 `findEffectiveDetails(String commonCode)` 추가 필요.
> CommonCodeController에 `GET /{commonCode}/details/effective` 엔드포인트 추가.

---

### Step 2. React — 공통 컴포넌트 신규 생성

#### 2-1. CommonCodeSelect.jsx (신규)

```jsx
import { useState, useEffect } from 'react'
import { commonCodeApi } from '../../api/commonCodeApi'

export default function CommonCodeSelect({ commonCode, name, value, onChange, className, disabled }) {
  const [options, setOptions] = useState([])

  useEffect(() => {
    if (!commonCode) return
    commonCodeApi.getDetails(commonCode).then(setOptions).catch(() => setOptions([]))
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

> 유효기간 필터링은 `/effective` 엔드포인트 사용으로 전환 가능. 우선 기존 API 사용 후, 백엔드 배포 후 전환.

#### 2-2. useCommonCodeLabel.js (신규)

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

---

### Step 3. React — 폼/목록 전환

#### 3-1. SubscriptionForm.jsx

**Before (라인 32-40):**
```jsx
<select name="subsStatusCd" value={data.subsStatusCd} onChange={onChange}
  className="w-full h-8 border border-gray-300 rounded-lg px-3 text-sm">
  <option value="">선택</option>
  <option value="ACTIVE">ACTIVE (활성)</option>
  <option value="SUSPENDED">SUSPENDED (정지)</option>
  <option value="TERMINATED">TERMINATED (해지)</option>
  <option value="PENDING">PENDING (가입대기)</option>
</select>
```

**After:**
```jsx
import CommonCodeSelect from '../common/CommonCodeSelect'

<CommonCodeSelect commonCode="subs_status_cd" name="subsStatusCd"
  value={data.subsStatusCd} onChange={onChange} />
```

#### 3-2. SubscriptionList.jsx

**Before (라인 3-5):**
```jsx
const STATUS_LABEL = {
  ACTIVE: '활성', SUSPENDED: '정지', TERMINATED: '해지', PENDING: '가입대기'
}
```

**After:**
```jsx
import useCommonCodeLabel from '../../hooks/useCommonCodeLabel'

// 컴포넌트 내부
const CODE_GROUPS = ['subs_status_cd']
const { getLabel } = useCommonCodeLabel(CODE_GROUPS)

// 라인 62 변경
{getLabel('subs_status_cd', item.subsStatusCd)}
```

> STATUS_LABEL 상수 제거. 배지 색상 로직은 코드값 기반이므로 유지.

#### 3-3. BillStdForm.jsx

기존 `Field` 컴포넌트의 `options` prop 대신, _cd 필드에만 CommonCodeSelect 사용.

**Before (라인 55):**
```jsx
<Field label="서비스코드" name="svcCd" value={data.svcCd || ''} onChange={handleChange} required />
```

**After:**
```jsx
import CommonCodeSelect from '../common/CommonCodeSelect'

<div>
  <label className="block text-xs text-gray-500 mb-1">서비스코드 <span className="text-blue-400 ml-0.5">*</span></label>
  <CommonCodeSelect commonCode="svc_cd" name="svcCd" value={data.svcCd || ''} onChange={handleChange} />
</div>
```

동일 패턴으로 6개 필드 모두 교체:
- `svcCd` → `commonCode="svc_cd"`
- `stdRegStatCd` → `commonCode="std_reg_stat_cd"`
- `billStdStatCd` → `commonCode="bill_std_stat_cd"`
- `pwrMetCalcMethCd` → `commonCode="pwr_met_calc_meth_cd"`
- `uprcDetMethCd` → `commonCode="uprc_det_meth_cd"`
- `pueDetMethCd` → `commonCode="pue_det_meth_cd"`

#### 3-4. SpecialSubscriptionForm.jsx

`Field` 컴포넌트가 `options` prop을 지원하지 않으므로 직접 교체.

**Before (라인 47, 50):**
```jsx
<Field label="서비스코드" name="svcCd" value={data.svcCd || ''} onChange={handleChange} />
<Field label="특수가입상태" name="specSubsStatCd" value={data.specSubsStatCd || ''} onChange={handleChange} />
```

**After:**
```jsx
import CommonCodeSelect from '../common/CommonCodeSelect'

<div>
  <label className="block text-xs text-gray-500 mb-1">서비스코드</label>
  <CommonCodeSelect commonCode="svc_cd" name="svcCd" value={data.svcCd || ''} onChange={handleChange} />
</div>
<div>
  <label className="block text-xs text-gray-500 mb-1">특수가입상태</label>
  <CommonCodeSelect commonCode="spec_subs_stat_cd" name="specSubsStatCd" value={data.specSubsStatCd || ''} onChange={handleChange} />
</div>
```

#### 3-5. SpecialSubscriptionList.jsx

**Before (라인 9-10):**
```jsx
{ accessorKey: 'svcCd', header: '서비스코드', size: 100, minSize: 60 },
{ accessorKey: 'specSubsStatCd', header: '특수가입상태', size: 100, minSize: 60 },
```

**After:** DataGrid의 cell 렌더러에 useCommonCodeLabel 적용
```jsx
import useCommonCodeLabel from '../../hooks/useCommonCodeLabel'

const CODE_GROUPS = ['svc_cd', 'spec_subs_stat_cd']
const { getLabel } = useCommonCodeLabel(CODE_GROUPS)

// columns 정의에 cell 추가
{ accessorKey: 'svcCd', header: '서비스코드', size: 100, minSize: 60,
  cell: ({ getValue }) => getLabel('svc_cd', getValue()) },
{ accessorKey: 'specSubsStatCd', header: '특수가입상태', size: 100, minSize: 60,
  cell: ({ getValue }) => getLabel('spec_subs_stat_cd', getValue()) },
```

> DataGrid가 cell 렌더러를 지원하는지 확인 필요. 미지원 시 columns에 formatter 함수 추가 방식으로 변경.

---

### Step 4. Vue — 공통 컴포넌트 신규 생성

#### 4-1. CommonCodeSelect.vue (신규)

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
  try { options.value = await commonCodeApi.getDetails(code) }
  catch { options.value = [] }
}, { immediate: true })
</script>
```

#### 4-2. useCommonCodeLabel.js (신규 — Vue 합성함수)

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

### Step 5. Vue — 페이지 전환

#### 5-1. SubscriptionPage.vue

**Before (라인 64-71):**
```vue
<select v-model="formData.subsStatusCd" class="w-full h-8 px-2 border border-gray-300 rounded text-sm ...">
  <option value="">선택</option>
  <option value="ACTIVE">활성</option>
  <option value="SUSPENDED">정지</option>
  <option value="TERMINATED">탈퇴</option>
  <option value="PENDING">미납</option>
</select>
```

**After:**
```vue
import CommonCodeSelect from '../components/common/CommonCodeSelect.vue'

<CommonCodeSelect common-code="subs_status_cd" v-model="formData.subsStatusCd" />
```

**목록 코드명 표시 (라인 117):**
```js
import { useCommonCodeLabel } from '../composables/useCommonCodeLabel'
const { getLabel } = useCommonCodeLabel(['subs_status_cd'])

// columns에서 formatter 활용 또는 DataGrid 셀에서 getLabel 호출
{ key: 'subsStatusCd', header: '상태', size: 80,
  formatter: (val) => getLabel('subs_status_cd', val) },
```

> Vue DataGrid에 formatter 지원 확인 필요. 미지원 시 computed로 데이터 자체를 변환.

#### 5-2. BillStdPage.vue

**Before (라인 44-45):**
```vue
<label class="block text-xs text-gray-500 mb-1">서비스코드</label>
<input v-model="formData.svcCd" class="w-full h-8 ..." />
```

**After:**
```vue
<label class="block text-xs text-gray-500 mb-1">서비스코드</label>
<CommonCodeSelect common-code="svc_cd" v-model="formData.svcCd" />
```

> BillStdPage.vue에는 _cd 필드가 `svcCd` 1개만 표시됨 (나머지는 폼에 없음). 실제 화면 구성 확인 후 추가 필드 적용 여부 결정.

#### 5-3. SpecialSubscriptionPage.vue

**Before (라인 63-64, 74-76):**
```vue
<label class="block text-xs text-gray-500 mb-1">서비스코드</label>
<input v-model="formData.svcCd" class="..." />

<label class="block text-xs text-gray-500 mb-1">특수가입상태</label>
<input v-model="formData.specSubsStatCd" class="..." />
```

**After:**
```vue
<label class="block text-xs text-gray-500 mb-1">서비스코드</label>
<CommonCodeSelect common-code="svc_cd" v-model="formData.svcCd" />

<label class="block text-xs text-gray-500 mb-1">특수가입상태</label>
<CommonCodeSelect common-code="spec_subs_stat_cd" v-model="formData.specSubsStatCd" />
```

**목록 코드명 표시 (라인 152-153):**
```js
const { getLabel } = useCommonCodeLabel(['svc_cd', 'spec_subs_stat_cd'])

{ key: 'svcCd', header: '서비스코드', size: 100,
  formatter: (val) => getLabel('svc_cd', val) },
{ key: 'specSubsStatCd', header: '특수가입상태', size: 100,
  formatter: (val) => getLabel('spec_subs_stat_cd', val) },
```

---

## 트레이드오프 기록

| 결정 | 선택 | 대안 | 이유 |
|------|------|------|------|
| 코드명 표시 | 프론트 매핑 | 백엔드 DTO 확장 | 백엔드 도메인 서비스의 공통코드 의존 방지 |
| 매핑 선언 | 폼에서 직접 선언 | 중앙 매핑 파일 | 9개 그룹이라 미니멀하게. 향후 리팩터링 고려 |
| 유효기간 필터링 | 새 API 엔드포인트 | 기존 API + 프론트 필터링 | 서버에서 필터링이 정확. 클라이언트 시간 불일치 방지 |

## 확인 필요 사항

1. **DataGrid cell 렌더러 지원 여부**: React DataGrid가 `cell` prop을, Vue DataGrid가 `formatter` prop을 지원하는지 구현 시 확인
2. **BillStdPage.vue 추가 _cd 필드**: Vue 화면에 stdRegStatCd 등 추가 필드가 있는지 확인 (현재 svcCd만 보임)

## 테스트 계획

1. 백엔드 빌드 성공 확인 (`./mvnw compile`)
2. 프론트 빌드 성공 확인 (`npm run build`)
3. 각 화면 진입 시 CommonCodeSelect 드롭다운에 코드 목록 로딩 확인
4. 드롭다운 선택 → 저장 → 재조회 시 값 유지 확인
5. 그리드에서 코드명 표시 확인

## 롤백 방안

- Git 커밋 단위로 롤백 가능. 백엔드(Step 1)과 프론트엔드(Step 2~5)를 별도 커밋으로 분리.
