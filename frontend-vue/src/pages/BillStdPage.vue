<template>
  <MainLayout>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">가입별 과금기준 관리</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색유형</label>
            <select v-model="searchType" class="h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option value="subsId">가입ID</option>
              <option value="billStdId">과금기준ID</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색어</label>
            <input v-model="keyword" @keydown.enter="handleSearch"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div class="self-end">
            <button @click="handleSearch" :disabled="isSearching"
              class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-60">조회</button>
          </div>
        </div>
      </div>

      <!-- 입력 폼 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">과금기준 정보</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">과금기준ID</label>
            <input v-model="formData.billStdId" readonly class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID</label>
            <input v-model="formData.subsId" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스코드</label>
            <input v-model="formData.svcCd" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">적용시작일</label>
            <input v-model="formData.effStartDt" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">적용종료일</label>
            <input v-model="formData.effEndDt" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">계약용량(KMH)</label>
            <input v-model="formData.cntrcCapKmh" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">계약금액</label>
            <input v-model="formData.cntrcAmt" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">계량단가</label>
            <input v-model="formData.meteringUnitPriceAmt" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">최종적용여부</label>
            <select v-model="formData.lastEffYn" class="w-full h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option value="Y">Y</option>
              <option value="N">N</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar>
      <button @click="handleSave" class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">저장</button>
      <button @click="handleChange" class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">변경</button>
      <button @click="handleDeleteClick" class="h-8 px-4 border border-red-300 text-sm rounded text-red-600 hover:bg-red-50">삭제</button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="confirmOpen"
      message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
      @confirm="executeDelete"
      @cancel="confirmOpen = false"
    />
  </MainLayout>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useAuthStore } from '../stores/auth'
import { getBillStdBySubsId, getBillStd, createBillStd, updateBillStd, deleteBillStd } from '../api/billStdApi'
import MainLayout from '../components/common/MainLayout.vue'
import Toast from '../components/common/Toast.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'

const auth = useAuthStore()

const EMPTY_FORM = {
  billStdId: '', subsId: '', svcCd: '', lastEffYn: 'Y',
  effStartDt: '', effEndDt: '', meteringUnitPriceAmt: '',
  cntrcCapKmh: '', cntrcAmt: '',
}

const formData = reactive({ ...EMPTY_FORM })
const keyword = ref('')
const searchType = ref('subsId')
const errorMsg = ref('')
const successMsg = ref('')
const confirmOpen = ref(false)
const isSearching = ref(false)

const clearMessages = () => { errorMsg.value = ''; successMsg.value = '' }

const toFormData = (dto) => {
  Object.keys(EMPTY_FORM).forEach(key => {
    formData[key] = dto[key] != null ? String(dto[key]) : ''
  })
}

const toRequestDto = () => ({
  ...formData,
  meteringUnitPriceAmt: formData.meteringUnitPriceAmt ? parseFloat(formData.meteringUnitPriceAmt) : null,
  cntrcCapKmh: formData.cntrcCapKmh ? parseFloat(formData.cntrcCapKmh) : null,
  cntrcAmt: formData.cntrcAmt ? parseFloat(formData.cntrcAmt) : null,
  createdBy: auth.user?.userId ?? 'SYSTEM',
})

const handleSearch = async () => {
  clearMessages()
  if (!keyword.value.trim()) { errorMsg.value = '검색어를 입력해 주세요.'; return }
  isSearching.value = true
  try {
    const found = searchType.value === 'subsId'
      ? await getBillStdBySubsId(keyword.value.trim())
      : await getBillStd(keyword.value.trim())
    toFormData(found)
    successMsg.value = '조회가 완료되었습니다.'
  } catch (err) {
    errorMsg.value = err?.response?.status === 404 ? '조회 결과가 없습니다.' : '서버와 연결할 수 없습니다.'
    Object.assign(formData, EMPTY_FORM)
  } finally { isSearching.value = false }
}

const handleSave = async () => {
  clearMessages()
  try {
    const created = await createBillStd(toRequestDto())
    toFormData(created)
    successMsg.value = '저장이 완료되었습니다.'
  } catch (err) {
    errorMsg.value = err?.response?.data?.message || '저장에 실패했습니다.'
  }
}

const handleChange = async () => {
  if (!formData.billStdId) { errorMsg.value = '조회 후 변경할 수 있습니다.'; return }
  clearMessages()
  try {
    const updated = await updateBillStd(formData.billStdId, toRequestDto())
    toFormData(updated)
    successMsg.value = '변경이 완료되었습니다.'
  } catch (err) {
    errorMsg.value = err?.response?.data?.message || '변경에 실패했습니다.'
  }
}

const handleDeleteClick = () => {
  if (!formData.billStdId) { errorMsg.value = '조회 후 삭제할 수 있습니다.'; return }
  clearMessages()
  confirmOpen.value = true
}

const executeDelete = async () => {
  confirmOpen.value = false
  try {
    await deleteBillStd(formData.billStdId)
    Object.assign(formData, EMPTY_FORM)
    successMsg.value = '삭제가 완료되었습니다.'
  } catch (err) {
    errorMsg.value = err?.response?.status === 409 ? '다른 이력이 존재하여 삭제할 수 없습니다.' : '삭제에 실패했습니다.'
  }
}
</script>
