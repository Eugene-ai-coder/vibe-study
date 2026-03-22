import { ref, computed } from 'vue'
import { getEffectiveConfigs } from '../api/billStdFieldConfigApi'
import { commonCodeApi } from '../api/commonCodeApi'

/**
 * 서비스코드별 동적 필드 설정 로드 + SELECT 필드의 공통코드 라벨 매핑
 *
 * @returns {{ fieldConfigs, dynCodeMap, loadFieldConfigs, dynamicFieldPairs }}
 */
export function useDynamicFieldConfigs() {
  const fieldConfigs = ref([])
  const dynCodeMap = ref({})

  const loadFieldConfigs = async (svcCd) => {
    fieldConfigs.value = []
    dynCodeMap.value = {}
    if (!svcCd) return

    const configs = await getEffectiveConfigs(svcCd)
    fieldConfigs.value = configs

    const selectConfigs = configs.filter(c => c.fieldType === 'SELECT' && c.commonCode)
    if (selectConfigs.length === 0) return

    const results = await Promise.all(
      selectConfigs.map(c =>
        commonCodeApi.getDetails(c.commonCode).then(details => [c.fieldCd, details])
      )
    )
    const map = {}
    results.forEach(([fieldCd, details]) => {
      map[fieldCd] = {}
      details.forEach(d => { map[fieldCd][d.commonDtlCode] = d.commonDtlCodeNm })
    })
    dynCodeMap.value = map
  }

  const buildFieldPairs = (fieldValues) => {
    if (!fieldValues) return []
    const entries = Object.entries(fieldValues).map(([key, val]) => ({
      label: fieldConfigs.value.find(c => c.fieldCd === key)?.fieldNm || key,
      value: dynCodeMap.value[key]?.[val] || val,
    }))
    const pairs = []
    for (let i = 0; i < entries.length; i += 2) {
      pairs.push([entries[i], entries[i + 1] || null])
    }
    return pairs
  }

  return { fieldConfigs, dynCodeMap, loadFieldConfigs, buildFieldPairs }
}

/** 신청유형 라벨 */
export const REQ_TYPE_LABELS = { NEW: '신규', CHANGE: '변경', CANCEL: '해지' }

/** 신청유형 뱃지 CSS 클래스 */
export function getReqTypeBadgeClass(typeCd) {
  const base = 'inline-block px-2 py-0.5 rounded text-xs font-medium'
  if (typeCd === 'NEW') return `${base} bg-blue-100 text-blue-700`
  if (typeCd === 'CHANGE') return `${base} bg-amber-100 text-amber-700`
  if (typeCd === 'CANCEL') return `${base} bg-red-100 text-red-700`
  return `${base} bg-gray-100 text-gray-600`
}
