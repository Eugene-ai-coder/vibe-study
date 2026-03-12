<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
    <div class="bg-white rounded-lg shadow-xl w-[600px] flex flex-col max-h-[80vh]">
      <div class="flex items-center justify-between px-4 py-3 border-b border-gray-200">
        <h2 class="text-sm font-semibold text-gray-800">결재내용 상세</h2>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-lg leading-none">&times;</button>
      </div>

      <div class="overflow-auto flex-1 p-4 space-y-4">
        <div v-if="loading" class="text-center text-sm text-gray-400 py-8">불러오는 중...</div>
        <template v-else-if="apprvReq">
          <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide">결재요청 정보</h3>
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <label class="block text-xs text-gray-500 mb-1">결재요청ID</label>
              <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ apprvReq.apprvReqId }}</div>
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">결재요청자</label>
              <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ apprvReq.createdBy }}</div>
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">결재요청일시</label>
              <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ apprvReq.createdDt }}</div>
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">결재자</label>
              <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ apprvReq.approverId || '-' }}</div>
            </div>
          </div>

          <div v-if="apprvReq.apprvRemarks">
            <label class="block text-xs text-gray-500 mb-1">결재 특기사항</label>
            <div class="px-2 py-1.5 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600 whitespace-pre-wrap">{{ apprvReq.apprvRemarks }}</div>
          </div>

          <template v-if="snapshot">
            <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide">과금기준 스냅샷</h3>
            <div class="grid grid-cols-2 gap-3 text-sm">
              <div>
                <label class="block text-xs text-gray-500 mb-1">과금기준ID</label>
                <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ snapshot.billStdId }}</div>
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">가입ID</label>
                <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ snapshot.subsId }}</div>
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">서비스코드</label>
                <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ snapshot.svcCd }}</div>
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">등록진행상태</label>
                <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ snapshot.stdRegStatCd }}</div>
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">유효시작일</label>
                <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ snapshot.effStartDt }}</div>
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">유효종료일</label>
                <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ snapshot.effEndDt }}</div>
              </div>
            </div>

            <div v-if="snapshot.fieldValues && Object.keys(snapshot.fieldValues).length > 0">
              <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2">동적 필드</h3>
              <div class="grid grid-cols-2 gap-3 text-sm">
                <div v-for="(val, key) in snapshot.fieldValues" :key="key">
                  <label class="block text-xs text-gray-500 mb-1">{{ key }}</label>
                  <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-600">{{ val }}</div>
                </div>
              </div>
            </div>
          </template>
        </template>
        <div v-else class="text-center text-sm text-gray-400 py-8">결재요청 정보를 찾을 수 없습니다.</div>
      </div>

      <div class="px-4 py-3 border-t border-gray-200 flex justify-end">
        <button @click="$emit('close')"
          class="h-8 px-4 border border-gray-300 text-gray-600 rounded text-sm hover:bg-gray-50">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getApprvReq } from '../../api/apprvReqApi'

const props = defineProps({
  apprvReqId: { type: String, required: true },
})

defineEmits(['close'])

const apprvReq = ref(null)
const loading = ref(true)

const snapshot = computed(() => {
  if (!apprvReq.value?.apprvReqContent) return null
  try {
    return JSON.parse(apprvReq.value.apprvReqContent)
  } catch {
    return null
  }
})

onMounted(async () => {
  try {
    apprvReq.value = await getApprvReq(props.apprvReqId)
  } catch {
    apprvReq.value = null
  } finally {
    loading.value = false
  }
})
</script>
