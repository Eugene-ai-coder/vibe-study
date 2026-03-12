<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
    <div class="bg-white rounded-lg shadow-xl w-[600px] flex flex-col max-h-[80vh]">
      <div class="flex items-center justify-between px-4 py-3 border-b border-gray-200">
        <h2 class="text-sm font-semibold text-gray-800">결재요청</h2>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-lg leading-none">&times;</button>
      </div>

      <div class="overflow-auto flex-1 p-4 space-y-4">
        <div v-if="loading" class="text-center text-sm text-gray-400 py-8">불러오는 중...</div>
        <template v-else-if="detail">
          <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide">과금기준 정보</h3>
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <label class="block text-xs text-gray-500 mb-1">과금기준ID</label>
              <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ detail.billStdId }}</div>
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">가입ID</label>
              <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ detail.subsId }}</div>
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">서비스코드</label>
              <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ detail.svcCd }}</div>
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">유효시작일</label>
              <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ detail.effStartDt }}</div>
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">유효종료일</label>
              <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ detail.effEndDt }}</div>
            </div>
          </div>

          <div v-if="detail.fieldValues && Object.keys(detail.fieldValues).length > 0">
            <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2">동적 필드</h3>
            <div class="grid grid-cols-2 gap-3 text-sm">
              <div v-for="(val, key) in detail.fieldValues" :key="key">
                <label class="block text-xs text-gray-500 mb-1">{{ key }}</label>
                <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ val }}</div>
              </div>
            </div>
          </div>

          <div>
            <label class="block text-xs text-gray-500 mb-1">결재 특기사항</label>
            <textarea v-model="apprvRemarks" rows="3" placeholder="특기사항을 입력하세요 (선택)"
              class="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:border-blue-400 resize-none" />
          </div>
        </template>
      </div>

      <div class="px-4 py-3 border-t border-gray-200 flex justify-end gap-2">
        <button @click="$emit('close')"
          class="h-8 px-4 border border-gray-300 text-gray-600 rounded text-sm hover:bg-gray-50">취소</button>
        <button @click="handleSubmit" :disabled="submitting || !detail"
          class="h-8 px-4 bg-blue-600 text-white rounded text-sm hover:bg-blue-700 disabled:opacity-60">결재요청</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { createApprvReq } from '../../api/apprvReqApi'
import { getBillStd } from '../../api/billStdApi'
import { useAuthStore } from '../../stores/auth'

const props = defineProps({
  billStdId: { type: String, required: true },
  subsId: { type: String, required: true },
})

const emit = defineEmits(['close', 'submitted'])

const auth = useAuthStore()
const detail = ref(null)
const loading = ref(true)
const submitting = ref(false)
const apprvRemarks = ref('')

onMounted(async () => {
  try {
    detail.value = await getBillStd(props.billStdId)
  } catch {
    detail.value = null
  } finally {
    loading.value = false
  }
})

const handleSubmit = async () => {
  submitting.value = true
  try {
    await createApprvReq({
      billStdId: props.billStdId,
      subsId: props.subsId,
      apprvRemarks: apprvRemarks.value || null,
      createdBy: auth.user?.userId || 'SYSTEM',
    })
    emit('submitted')
    emit('close')
  } catch (err) {
    const status = err?.response?.status
    if (status === 400) {
      alert(err?.response?.data?.message || '결재요청에 실패했습니다.')
    } else {
      alert('결재요청에 실패했습니다.')
    }
  } finally {
    submitting.value = false
  }
}
</script>
