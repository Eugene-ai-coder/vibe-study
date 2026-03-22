<template>
  <div>
    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">과금기준 필드설정 관리</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스코드</label>
            <CommonCodeSelect common-code="svc_cd" v-model="searchSvcCd" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">필드코드</label>
            <input v-model="searchFieldCd" @keydown.enter="handleSearch"
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
        row-id-accessor="_rowId"
        :selected-row-id="selectedId"
        @row-click="handleRowClick"
        title="필드설정 목록"
      />

      <!-- 입력 폼 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">필드설정 정보</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스코드 <span class="text-blue-400">*</span></label>
            <CommonCodeSelect common-code="svc_cd" v-model="formData.svcCd" :disabled="!isNew" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">필드코드 <span class="text-blue-400">*</span></label>
            <input v-model="formData.fieldCd" :readonly="!isNew"
              :class="fieldClass(!isNew)" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">유효시작일 <span class="text-blue-400">*</span></label>
            <input v-model="formData.effStartDt" :readonly="!isNew" placeholder="YYYYMMDD"
              :class="fieldClass(!isNew)" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">필드명 <span class="text-blue-400">*</span></label>
            <input v-model="formData.fieldNm"
              class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">필드타입 <span class="text-blue-400">*</span></label>
            <select v-model="formData.fieldType"
              class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 bg-white">
              <option value="">선택</option>
              <option value="TEXT">TEXT</option>
              <option value="NUMBER">NUMBER</option>
              <option value="SELECT">SELECT</option>
              <option value="DATE">DATE</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">필수여부</label>
            <select v-model="formData.requiredYn"
              class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 bg-white">
              <option value="N">N</option>
              <option value="Y">Y</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">정렬순서</label>
            <input v-model="formData.sortOrder" type="number"
              class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">공통코드 (SELECT 타입 전용)</label>
            <input v-model="formData.commonCode" :disabled="formData.fieldType !== 'SELECT'"
              :class="formData.fieldType !== 'SELECT'
                ? 'w-full h-8 border border-gray-200 rounded px-2 text-sm bg-gray-50 text-gray-400'
                : 'w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400'" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">기본값</label>
            <input v-model="formData.defaultValue"
              class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">유효종료일</label>
            <input v-model="formData.effEndDt" readonly
              class="w-full h-8 border border-gray-200 rounded px-2 text-sm bg-gray-50 text-gray-400" />
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar>
      <button @click="handleDeleteClick"
        class="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 rounded text-sm transition-colors">삭제</button>
      <button @click="handleExpireClick"
        class="h-8 px-4 border border-gray-300 text-gray-600 hover:bg-gray-50 rounded text-sm transition-colors">사용종료</button>
      <button @click="handleNewClick"
        class="h-8 px-4 border border-gray-300 text-gray-600 hover:bg-gray-50 rounded text-sm transition-colors">신규</button>
      <button v-if="!isNew && selected" @click="handleUpdateClick"
        class="h-8 px-4 border border-blue-600 text-blue-600 hover:bg-blue-50 rounded text-sm transition-colors">변경</button>
      <button v-if="isNew" @click="handleSaveClick"
        class="h-8 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded text-sm transition-colors">등록</button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="confirmOpen"
      :message="confirmMessage"
      :confirm-text="confirmText"
      :confirm-type="confirmType"
      @confirm="confirmAction"
      @cancel="confirmOpen = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import {
  getFieldConfigs, createFieldConfig, updateFieldConfig,
  deleteFieldConfig, expireFieldConfig,
} from '../api/billStdFieldConfigApi'
import { useToast } from '../composables/useToast'
import DataGrid from '../components/common/DataGrid.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'
import { fieldClass } from '../composables/useFieldClass'
import CommonCodeSelect from '../components/common/CommonCodeSelect.vue'

const EMPTY_FORM = {
  svcCd: '', fieldCd: '', effStartDt: '',
  fieldNm: '', fieldType: '', requiredYn: 'N',
  sortOrder: 0, commonCode: '', defaultValue: '', effEndDt: '99991231',
}

const columns = [
  { key: 'svcCd', header: '서비스코드', size: 100 },
  { key: 'fieldCd', header: '필드코드', size: 180 },
  { key: 'fieldNm', header: '필드명', size: 140 },
  { key: 'fieldType', header: '필드타입', size: 90 },
  { key: 'requiredYn', header: '필수', size: 60 },
  { key: 'sortOrder', header: '정렬순서', size: 80 },
  { key: 'effStartDt', header: '유효시작일', size: 100 },
  { key: 'effEndDt', header: '유효종료일', size: 100 },
]

const searchSvcCd = ref('')
const searchFieldCd = ref('')
const items = ref([])
const selected = ref(null)
const formData = reactive({ ...EMPTY_FORM })
const isNew = ref(false)
const isLoading = ref(false)
const { showSuccess, showError } = useToast()

const confirmOpen = ref(false)
const confirmMessage = ref('')
const confirmText = ref('')
const confirmType = ref('primary')
const confirmAction = ref(() => {})

const selectedId = computed(() =>
  selected.value ? `${selected.value.svcCd}__${selected.value.fieldCd}__${selected.value.effStartDt}` : null
)

const dataWithRowId = computed(() =>
  items.value.map(item => ({ ...item, _rowId: `${item.svcCd}__${item.fieldCd}__${item.effStartDt}` }))
)

const toFormData = (dto) => {
  Object.keys(EMPTY_FORM).forEach(key => {
    formData[key] = dto[key] != null ? String(dto[key]) : ''
  })
}

const toRequestDto = () => ({
  svcCd: formData.svcCd || null,
  fieldCd: formData.fieldCd || null,
  effStartDt: formData.effStartDt || null,
  fieldNm: formData.fieldNm || null,
  fieldType: formData.fieldType || null,
  requiredYn: formData.requiredYn || 'N',
  sortOrder: formData.sortOrder ? parseInt(formData.sortOrder) : 0,
  commonCode: formData.commonCode || null,
  defaultValue: formData.defaultValue || null,
  effEndDt: formData.effEndDt || '99991231',
})


const fetchList = async () => {
  const params = {}
  if (searchSvcCd.value) params.svcCd = searchSvcCd.value
  if (searchFieldCd.value.trim()) params.fieldCd = searchFieldCd.value.trim()
  items.value = await getFieldConfigs(params)
}

const handleSearch = async () => {
  isLoading.value = true
  try {
    await fetchList()
    selected.value = null
    Object.assign(formData, EMPTY_FORM)
    isNew.value = false
  } catch { showError('조회에 실패했습니다.') }
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
}

const handleSaveClick = () => {
  if (!formData.svcCd || !formData.fieldCd || !formData.effStartDt || !formData.fieldNm || !formData.fieldType) {
    showError('필수 항목을 입력해 주세요.')
    return
  }
  confirmMessage.value = '필드설정을 등록하시겠습니까?'
  confirmText.value = '등록'
  confirmType.value = 'primary'
  confirmAction.value = executeSave
  confirmOpen.value = true
}

const executeSave = async () => {
  confirmOpen.value = false
  try {
    const created = await createFieldConfig(toRequestDto())
    toFormData(created)
    selected.value = created
    isNew.value = false
    showSuccess('등록이 완료되었습니다.')
    await fetchList()
  } catch (err) {
    const status = err?.response?.status
    if (status === 409) showError('이미 존재하는 필드설정입니다.')
    else if (status === 400) showError(err?.response?.data?.message || '입력값을 확인해 주세요.')
    else showError('등록에 실패했습니다.')
  }
}

const handleUpdateClick = () => {
  if (!selected.value) { showError('목록에서 항목을 선택해 주세요.'); return }
  if (!formData.fieldNm || !formData.fieldType) {
    showError('필수 항목을 입력해 주세요.')
    return
  }
  confirmMessage.value = '필드설정을 변경하시겠습니까?'
  confirmText.value = '변경'
  confirmType.value = 'primary'
  confirmAction.value = executeUpdate
  confirmOpen.value = true
}

const executeUpdate = async () => {
  confirmOpen.value = false
  try {
    const updated = await updateFieldConfig(
      selected.value.svcCd, selected.value.fieldCd, selected.value.effStartDt, toRequestDto()
    )
    toFormData(updated)
    selected.value = updated
    showSuccess('변경이 완료되었습니다.')
    await fetchList()
  } catch (err) {
    showError(err?.response?.data?.message || '변경에 실패했습니다.')
  }
}

const handleDeleteClick = () => {
  if (!selected.value) { showError('목록에서 항목을 선택해 주세요.'); return }
  confirmMessage.value = '삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.'
  confirmText.value = '삭제'
  confirmType.value = 'danger'
  confirmAction.value = executeDelete
  confirmOpen.value = true
}

const executeDelete = async () => {
  confirmOpen.value = false
  try {
    await deleteFieldConfig(selected.value.svcCd, selected.value.fieldCd, selected.value.effStartDt)
    selected.value = null
    Object.assign(formData, EMPTY_FORM)
    isNew.value = false
    showSuccess('삭제가 완료되었습니다.')
    await fetchList()
  } catch (err) {
    const status = err?.response?.status
    if (status === 409) showError('사용 중인 필드는 삭제할 수 없습니다. 사용종료 기능을 이용해 주세요.')
    else showError('삭제에 실패했습니다.')
  }
}

const handleExpireClick = () => {
  if (!selected.value) { showError('목록에서 항목을 선택해 주세요.'); return }
  confirmMessage.value = '필드설정을 사용종료 처리하시겠습니까?'
  confirmText.value = '사용종료'
  confirmType.value = 'primary'
  confirmAction.value = executeExpire
  confirmOpen.value = true
}

const executeExpire = async () => {
  confirmOpen.value = false
  try {
    const expired = await expireFieldConfig(selected.value.svcCd, selected.value.fieldCd, selected.value.effStartDt)
    toFormData(expired)
    selected.value = expired
    showSuccess('사용종료 처리가 완료되었습니다.')
    await fetchList()
  } catch (err) {
    showError(err?.response?.data?.message || '사용종료 처리에 실패했습니다.')
  }
}
</script>
