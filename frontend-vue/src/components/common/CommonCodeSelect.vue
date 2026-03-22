<template>
  <select :value="modelValue" @change="$emit('update:modelValue', $event.target.value)"
    :disabled="disabled || loading" :class="selectClass">
    <option value="">{{ loading ? '로딩중...' : '선택' }}</option>
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
const loading = ref(false)

watch(() => props.commonCode, async (code) => {
  if (!code) return
  loading.value = true
  try { options.value = await commonCodeApi.getEffectiveDetails(code) }
  catch { options.value = [] }
  finally { loading.value = false }
}, { immediate: true })

defineExpose({ options })
</script>
