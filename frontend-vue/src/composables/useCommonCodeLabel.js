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
