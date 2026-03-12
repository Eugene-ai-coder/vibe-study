<template>
  <div>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4 pb-24">
      <h1 class="text-xl font-bold text-gray-800">특수가입별 월별빌링요소</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">특수가입ID</label>
            <input v-model="searchSpclSubsId" @keydown.enter="handleSearch"
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
        storage-key="spclSubsMthBillElemPage"
        title="특수가입별 월별빌링요소 목록"
      />

      <!-- 입력 폼 -->
      <div class="space-y-4">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">기본 정보</h3>
          <div class="grid grid-cols-3 gap-x-4 gap-y-3">
            <div>
              <label class="block text-xs text-gray-500 mb-1">특수가입ID <span class="text-blue-400">*</span></label>
              <input v-model="formData.spclSubsId" :readonly="!isNew"
                :class="fieldClass(!isNew)" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">청구월 <span class="text-blue-400">*</span></label>
              <input v-model="formData.billMth" :readonly="!isNew"
                :class="fieldClass(!isNew)" placeholder="YYYYMM" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">가입ID <span class="text-blue-400">*</span></label>
              <input v-model="formData.subsId" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">계산금액 <span class="text-blue-400">*</span></label>
              <input v-model="formData.calcAmt" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">청구금액 <span class="text-blue-400">*</span></label>
              <input v-model="formData.billAmt" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
            </div>
          </div>
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import {
  getSpclSubsMthBillElemList, createSpclSubsMthBillElem,
  updateSpclSubsMthBillElem, deleteSpclSubsMthBillElem,
} from '../api/spclSubsMthBillElemApi'
import Toast from '../components/common/Toast.vue'
import DataGrid from '../components/common/DataGrid.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'
import { fieldClass } from '../composables/useFieldClass'

const EMPTY_FORM = {
  spclSubsId: '', billMth: '', subsId: '', calcAmt: '', billAmt: '',
}

const columns = computed(() => [
  { key: 'spclSubsId', header: '특수가입ID', size: 160 },
  { key: 'billMth', header: '청구월', size: 100 },
  { key: 'subsId', header: '가입ID', size: 140 },
  { key: 'calcAmt', header: '계산금액', size: 140 },
  { key: 'billAmt', header: '청구금액', size: 140 },
])

const searchSpclSubsId = ref('')
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
  selected.value ? `${selected.value.spclSubsId}__${selected.value.billMth}` : null
)

const dataWithRowId = computed(() =>
  items.value.map(item => ({ ...item, _rowId: `${item.spclSubsId}__${item.billMth}` }))
)

const toFormData = (dto) => {
  Object.keys(EMPTY_FORM).forEach(key => {
    formData[key] = dto[key] != null ? String(dto[key]) : ''
  })
}

const toRequestDto = () => ({
  spclSubsId: formData.spclSubsId || null,
  billMth: formData.billMth || null,
  subsId: formData.subsId || null,
  calcAmt: formData.calcAmt ? parseFloat(formData.calcAmt) : null,
  billAmt: formData.billAmt ? parseFloat(formData.billAmt) : null,
})

const clearMessages = () => { errorMsg.value = ''; successMsg.value = '' }

const getSearchParams = () => {
  const params = {}
  if (searchSpclSubsId.value.trim()) params.spclSubsId = searchSpclSubsId.value.trim()
  return params
}

const fetchList = async (pageNum = 0) => {
  const data = await getSpclSubsMthBillElemList({ ...getSearchParams(), page: pageNum, size: pageSize })
  items.value = data.content
  page.value = data.number
  totalPages.value = data.totalPages
  totalElements.value = data.totalElements
}

const handlePageChange = (newPage) => fetchList(newPage)

onMounted(() => fetchList())

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
  if (!formData.spclSubsId || !formData.billMth || !formData.subsId || !formData.calcAmt || !formData.billAmt) {
    errorMsg.value = '필수 항목을 입력해 주세요.'
    return
  }
  if (isNew.value) {
    saveConfirmMessage.value = '특수가입별 월별빌링요소를 등록하시겠습니까?'
  } else {
    if (!selected.value) { errorMsg.value = '목록에서 항목을 선택해 주세요.'; return }
    saveConfirmMessage.value = '특수가입별 월별빌링요소를 변경하시겠습니까?'
  }
  saveConfirmOpen.value = true
}

const handleSaveConfirmAction = async () => {
  saveConfirmOpen.value = false
  try {
    if (isNew.value) {
      const created = await createSpclSubsMthBillElem(toRequestDto())
      toFormData(created)
      selected.value = created
      isNew.value = false
      successMsg.value = '저장이 완료되었습니다.'
    } else {
      const updated = await updateSpclSubsMthBillElem(selected.value.spclSubsId, selected.value.billMth, toRequestDto())
      toFormData(updated)
      selected.value = updated
      successMsg.value = '변경이 완료되었습니다.'
    }
    await fetchList(page.value)
  } catch (err) {
    const status = err?.response?.status
    if (status === 409) errorMsg.value = '이미 존재하는 특수가입별 월별빌링요소입니다.'
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
    await deleteSpclSubsMthBillElem(selected.value.spclSubsId, selected.value.billMth)
    selected.value = null
    Object.assign(formData, EMPTY_FORM)
    isNew.value = false
    successMsg.value = '삭제가 완료되었습니다.'
    await fetchList(page.value)
  } catch { errorMsg.value = '삭제에 실패했습니다.' }
}
</script>
