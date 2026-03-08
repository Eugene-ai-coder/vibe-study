<template>
  <MainLayout>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">가입 관리</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색유형</label>
            <select v-model="searchType" class="h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 bg-white">
              <option value="SUBS_ID">가입ID</option>
              <option value="SUBS_NM">가입명</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색어</label>
            <input v-model="keyword" @keydown.enter="onSearch"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div class="self-end">
            <button @click="onSearch" :disabled="isSearching"
              class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-60">조회</button>
          </div>
        </div>
      </div>

      <!-- 목록 -->
      <DataGrid
        :columns="columns"
        :data="items"
        row-id-accessor="subsId"
        :selected-row-id="selectedSubs?.subsId"
        @row-click="onRowSelect"
        storage-key="subscriptionPage"
        title="가입 목록"
      />

      <!-- 입력 폼 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">가입 정보</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID <span class="text-blue-400">*</span></label>
            <input v-model="formData.subsId" :readonly="isRowSelected"
              :class="['w-full h-8 px-2 border rounded text-sm focus:outline-none focus:border-blue-400', isRowSelected ? 'bg-gray-50 text-gray-400 border-gray-200' : 'border-gray-300']" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입명</label>
            <input v-model="formData.subsNm" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스명</label>
            <input v-model="formData.svcNm" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">요금제명</label>
            <input v-model="formData.feeProdNm" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">상태</label>
            <select v-model="formData.subsStatusCd" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 bg-white">
              <option value="">선택</option>
              <option value="ACTIVE">활성</option>
              <option value="SUSPENDED">정지</option>
              <option value="TERMINATED">탈퇴</option>
              <option value="PENDING">미납</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입일시</label>
            <input type="datetime-local" v-model="formData.subsDt" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar>
      <button @click="onRegister" class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors">등록</button>
      <button @click="onUpdate" class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors">변경</button>
      <button @click="onDeleteClick" class="h-8 px-4 border border-red-300 text-sm rounded text-red-600 hover:bg-red-50 transition-colors">삭제</button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="confirmOpen"
      message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
      @confirm="onDelete"
      @cancel="confirmOpen = false"
    />
  </MainLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { searchSubscriptions, createSubscription, updateSubscription, deleteSubscription } from '../api/subscriptionApi'
import MainLayout from '../components/common/MainLayout.vue'
import Toast from '../components/common/Toast.vue'
import DataGrid from '../components/common/DataGrid.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'

const auth = useAuthStore()
const route = useRoute()

const EMPTY_FORM = { subsId: '', subsNm: '', svcNm: '', feeProdNm: '', subsStatusCd: '', subsDt: '', chgDt: '' }

const columns = [
  { key: 'subsId', header: '가입ID', size: 120 },
  { key: 'subsNm', header: '가입명', size: 150 },
  { key: 'svcNm', header: '서비스명', size: 150 },
  { key: 'feeProdNm', header: '요금제명', size: 150 },
  { key: 'subsStatusCd', header: '상태', size: 80 },
  { key: 'subsDt', header: '가입일시', size: 160 },
]

const items = ref([])
const selectedSubs = ref(null)
const formData = reactive({ ...EMPTY_FORM })
const keyword = ref('')
const searchType = ref('SUBS_ID')
const errorMsg = ref('')
const successMsg = ref('')
const confirmOpen = ref(false)
const isSearching = ref(false)

const isRowSelected = ref(false)

const toFormData = (dto) => ({
  subsId: dto.subsId || '',
  subsNm: dto.subsNm || '',
  svcNm: dto.svcNm || '',
  feeProdNm: dto.feeProdNm || '',
  subsStatusCd: dto.subsStatusCd || '',
  subsDt: dto.subsDt ? dto.subsDt.slice(0, 16) : '',
  chgDt: dto.chgDt ? dto.chgDt.slice(0, 16) : '',
})

const toRequestDto = () => ({
  subsId: formData.subsId || null,
  subsNm: formData.subsNm || null,
  svcNm: formData.svcNm || null,
  feeProdNm: formData.feeProdNm || null,
  subsStatusCd: formData.subsStatusCd || null,
  subsDt: formData.subsDt || null,
  chgDt: formData.chgDt || null,
  createdBy: auth.user?.userId ?? 'SYSTEM',
})

const clearMessages = () => { errorMsg.value = ''; successMsg.value = '' }

onMounted(async () => {
  const subsId = route.query.subsId
  if (subsId) {
    keyword.value = subsId
    searchType.value = 'SUBS_ID'
    try {
      items.value = await searchSubscriptions('SUBS_ID', subsId)
    } catch { errorMsg.value = '조회에 실패했습니다.' }
  }
})

const onSearch = async () => {
  clearMessages()
  isSearching.value = true
  try {
    items.value = await searchSubscriptions(searchType.value, keyword.value.trim())
    selectedSubs.value = null
    Object.assign(formData, EMPTY_FORM)
    isRowSelected.value = false
  } catch { errorMsg.value = '조회에 실패했습니다.' }
  finally { isSearching.value = false }
}

const onRowSelect = (item) => {
  selectedSubs.value = item
  Object.assign(formData, toFormData(item))
  isRowSelected.value = true
}

const onRegister = async () => {
  clearMessages()
  if (!formData.subsId.trim()) { errorMsg.value = '가입ID는 필수값입니다.'; return }
  try {
    const created = await createSubscription(toRequestDto())
    items.value = [...items.value, created]
    selectedSubs.value = created
    Object.assign(formData, toFormData(created))
    isRowSelected.value = true
    successMsg.value = '등록이 완료되었습니다.'
  } catch (err) {
    errorMsg.value = err?.response?.data?.message || '등록에 실패했습니다.'
  }
}

const onUpdate = async () => {
  clearMessages()
  if (!selectedSubs.value) { errorMsg.value = '가입을 선택해 주세요.'; return }
  try {
    const updated = await updateSubscription(selectedSubs.value.subsId, toRequestDto())
    items.value = items.value.map(i => i.subsId === updated.subsId ? updated : i)
    selectedSubs.value = updated
    Object.assign(formData, toFormData(updated))
    successMsg.value = '변경이 완료되었습니다.'
  } catch { errorMsg.value = '변경에 실패했습니다.' }
}

const onDeleteClick = () => {
  clearMessages()
  if (!selectedSubs.value) { errorMsg.value = '가입을 선택해 주세요.'; return }
  confirmOpen.value = true
}

const onDelete = async () => {
  confirmOpen.value = false
  try {
    await deleteSubscription(selectedSubs.value.subsId)
    items.value = items.value.filter(i => i.subsId !== selectedSubs.value.subsId)
    selectedSubs.value = null
    Object.assign(formData, EMPTY_FORM)
    isRowSelected.value = false
    successMsg.value = '삭제가 완료되었습니다.'
  } catch (err) {
    errorMsg.value = err?.response?.status === 409
      ? '과금기준이 존재하는 가입은 삭제할 수 없습니다.'
      : '삭제에 실패했습니다.'
  }
}
</script>
