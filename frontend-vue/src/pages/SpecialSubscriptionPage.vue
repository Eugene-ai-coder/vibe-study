<template>
  <MainLayout>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4 pb-24">
      <h1 class="text-xl font-bold text-gray-800">특수가입 관리</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입별과금기준ID</label>
            <input v-model="searchSubsBillStdId" @keydown.enter="handleSearch"
              class="w-48 h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID</label>
            <input v-model="searchSubsId" @keydown.enter="handleSearch"
              class="w-48 h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div class="self-end">
            <button @click="handleSearch" :disabled="isLoading"
              class="h-8 px-4 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white text-sm rounded transition-colors">
              {{ isLoading ? '조회 중...' : '조회' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 목록 -->
      <DataGrid
        :columns="columns"
        :data="dataWithRowId"
        :page="page"
        :total-pages="totalPages"
        :total-elements="totalElements"
        :page-size="pageSize"
        :on-page-change="handlePageChange"
        row-id-accessor="_rowId"
        :selected-row-id="selectedId"
        @row-click="handleRowClick"
        storage-key="specialSubscriptionPage"
        title="특수가입 목록"
      />

      <!-- 입력 폼 -->
      <div class="space-y-4">
        <!-- 기본 정보 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">기본 정보</h3>
          <div class="grid grid-cols-3 gap-x-4 gap-y-3">
            <div>
              <label class="block text-xs text-gray-500 mb-1">가입별과금기준ID <span class="text-blue-400">*</span></label>
              <input v-model="formData.subsBillStdId" :readonly="!isNew"
                :class="fieldClass(!isNew)" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">유효시작일 <span class="text-blue-400">*</span></label>
              <input v-model="formData.effStaDt" :readonly="!isNew"
                :class="fieldClass(!isNew)" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">가입ID <span class="text-blue-400">*</span></label>
              <input v-model="formData.subsId" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">서비스</label>
              <CommonCodeSelect common-code="svc_cd" v-model="formData.svcCd" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">유효종료일</label>
              <input v-model="formData.effEndDt" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">최종유효여부</label>
              <input v-model="formData.lastEffYn" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">특수가입상태</label>
              <CommonCodeSelect common-code="spec_subs_stat_cd" v-model="formData.specSubsStatCd" />
            </div>
          </div>
        </div>

        <!-- 약정 정보 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">약정 정보</h3>
          <div class="grid grid-cols-3 gap-x-4 gap-y-3">
            <div>
              <label class="block text-xs text-gray-500 mb-1">계약용량(kMh)</label>
              <input v-model="formData.cntrcCapKmh" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">계약금액</label>
              <input v-model="formData.cntrcAmt" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">할인율</label>
              <input v-model="formData.dscRt" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
            </div>
          </div>
        </div>

        <!-- 비고 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">비고</h3>
          <textarea v-model="formData.rmk"
            class="w-full h-20 border border-gray-300 rounded px-2 py-1 text-sm focus:outline-none focus:border-blue-400 resize-none" />
        </div>
      </div>
    </div>

    <FloatingActionBar>
      <button @click="handleDeleteClick"
        class="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 rounded text-sm transition-colors">삭제</button>
      <button @click="handleNewClick"
        class="h-8 px-4 border border-gray-300 text-gray-600 hover:bg-gray-50 rounded text-sm transition-colors">신규</button>
      <button @click="handleSaveClick"
        class="h-8 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded text-sm transition-colors">저장</button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="confirmOpen"
      message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
      confirm-text="삭제"
      confirm-type="danger"
      @confirm="executeDelete"
      @cancel="confirmOpen = false"
    />

    <ConfirmDialog
      v-if="saveConfirmOpen"
      :message="saveConfirmMessage"
      confirm-text="저장"
      confirm-type="primary"
      @confirm="handleSaveConfirmAction"
      @cancel="saveConfirmOpen = false"
    />
  </MainLayout>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import {
  getSpecialSubscriptions, createSpecialSubscription,
  updateSpecialSubscription, deleteSpecialSubscription,
} from '../api/specialSubscriptionApi'
import MainLayout from '../components/common/MainLayout.vue'
import Toast from '../components/common/Toast.vue'
import DataGrid from '../components/common/DataGrid.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'
import CommonCodeSelect from '../components/common/CommonCodeSelect.vue'
import { useCommonCodeLabel } from '../composables/useCommonCodeLabel'

const auth = useAuthStore()
const { getLabel } = useCommonCodeLabel(['svc_cd', 'spec_subs_stat_cd'])

const EMPTY_FORM = {
  subsBillStdId: '', effStaDt: '', subsId: '', svcCd: '',
  effEndDt: '', lastEffYn: '', specSubsStatCd: '',
  cntrcCapKmh: '', cntrcAmt: '', dscRt: '', rmk: '',
}

const columns = computed(() => [
  { key: 'subsBillStdId', header: '가입별과금기준ID', size: 160 },
  { key: 'effStaDt', header: '유효시작일', size: 100 },
  { key: 'subsId', header: '가입ID', size: 140 },
  { key: 'svcCd', header: '서비스', size: 100,
    cell: { props: ['value'], setup(props) { return () => getLabel('svc_cd', props.value) } } },
  { key: 'specSubsStatCd', header: '특수가입상태', size: 100,
    cell: { props: ['value'], setup(props) { return () => getLabel('spec_subs_stat_cd', props.value) } } },
  { key: 'lastEffYn', header: '최종유효여부', size: 100 },
])

const searchSubsBillStdId = ref('')
const searchSubsId = ref('')
const items = ref([])
const selected = ref(null)
const formData = reactive({ ...EMPTY_FORM })
const isNew = ref(false)
const isLoading = ref(false)
const confirmOpen = ref(false)
const saveConfirmOpen = ref(false)
const saveConfirmMessage = ref('')
const errorMsg = ref('')
const successMsg = ref('')

const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 10

const selectedId = computed(() =>
  selected.value ? `${selected.value.subsBillStdId}__${selected.value.effStaDt}` : null
)

const dataWithRowId = computed(() =>
  items.value.map(item => ({ ...item, _rowId: `${item.subsBillStdId}__${item.effStaDt}` }))
)

const fieldClass = (readOnly) => [
  'w-full h-8 border rounded px-2 text-sm',
  readOnly ? 'bg-gray-50 text-gray-400 border-gray-200' : 'bg-white border-gray-300 focus:outline-none focus:border-blue-400',
]

const toFormData = (dto) => {
  Object.keys(EMPTY_FORM).forEach(key => {
    formData[key] = dto[key] != null ? String(dto[key]) : ''
  })
}

const toRequestDto = () => ({
  subsBillStdId: formData.subsBillStdId || null,
  effStaDt: formData.effStaDt || null,
  subsId: formData.subsId || null,
  svcCd: formData.svcCd || null,
  effEndDt: formData.effEndDt || null,
  lastEffYn: formData.lastEffYn || null,
  specSubsStatCd: formData.specSubsStatCd || null,
  cntrcCapKmh: formData.cntrcCapKmh ? parseFloat(formData.cntrcCapKmh) : null,
  cntrcAmt: formData.cntrcAmt ? parseFloat(formData.cntrcAmt) : null,
  dscRt: formData.dscRt ? parseFloat(formData.dscRt) : null,
  rmk: formData.rmk || null,
})

const clearMessages = () => { errorMsg.value = ''; successMsg.value = '' }

const getSearchParams = () => {
  const params = {}
  if (searchSubsBillStdId.value.trim()) params.subsBillStdId = searchSubsBillStdId.value.trim()
  if (searchSubsId.value.trim()) params.subsId = searchSubsId.value.trim()
  return params
}

const fetchList = async (pageNum = 0) => {
  const data = await getSpecialSubscriptions({ ...getSearchParams(), page: pageNum, size: pageSize })
  items.value = data.content
  page.value = data.number
  totalPages.value = data.totalPages
  totalElements.value = data.totalElements
}

const handlePageChange = (newPage) => fetchList(newPage)

const handleSearch = async () => {
  clearMessages()
  isLoading.value = true
  try {
    await fetchList(0)
    selected.value = null
    Object.assign(formData, EMPTY_FORM)
    isNew.value = false
  } catch { errorMsg.value = '조회에 실패했습니다.' }
  finally { isLoading.value = false }
}

const handleRowClick = (item) => {
  selected.value = item
  toFormData(item)
  isNew.value = false
}

const handleNewClick = () => {
  selected.value = null
  Object.assign(formData, EMPTY_FORM)
  isNew.value = true
  clearMessages()
}

const handleSaveClick = () => {
  clearMessages()
  if (!formData.subsBillStdId || !formData.effStaDt || !formData.subsId) {
    errorMsg.value = '필수 항목을 입력해 주세요.'
    return
  }
  if (isNew.value) {
    saveConfirmMessage.value = '특수가입을 등록하시겠습니까?'
  } else {
    if (!selected.value) { errorMsg.value = '목록에서 항목을 선택해 주세요.'; return }
    saveConfirmMessage.value = '특수가입을 변경하시겠습니까?'
  }
  saveConfirmOpen.value = true
}

const handleSaveConfirmAction = async () => {
  saveConfirmOpen.value = false
  try {
    if (isNew.value) {
      const created = await createSpecialSubscription(toRequestDto())
      toFormData(created)
      selected.value = created
      isNew.value = false
      successMsg.value = '저장이 완료되었습니다.'
    } else {
      const updated = await updateSpecialSubscription(selected.value.subsBillStdId, selected.value.effStaDt, toRequestDto())
      toFormData(updated)
      selected.value = updated
      successMsg.value = '변경이 완료되었습니다.'
    }
    await fetchList(page.value)
  } catch (err) {
    const status = err?.response?.status
    if (status === 409) errorMsg.value = '이미 존재하는 특수가입입니다.'
    else if (status === 400) errorMsg.value = err?.response?.data?.message || '입력값을 확인해 주세요.'
    else errorMsg.value = '저장에 실패했습니다.'
  }
}

const handleDeleteClick = () => {
  if (!selected.value) { errorMsg.value = '목록에서 항목을 선택해 주세요.'; return }
  clearMessages()
  confirmOpen.value = true
}

const executeDelete = async () => {
  confirmOpen.value = false
  try {
    await deleteSpecialSubscription(selected.value.subsBillStdId, selected.value.effStaDt)
    selected.value = null
    Object.assign(formData, EMPTY_FORM)
    isNew.value = false
    successMsg.value = '삭제가 완료되었습니다.'
    await fetchList(page.value)
  } catch { errorMsg.value = '삭제에 실패했습니다.' }
}
</script>
