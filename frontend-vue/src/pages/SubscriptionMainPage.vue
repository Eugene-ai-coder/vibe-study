<template>
  <div>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">대표가입 관리</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스</label>
            <select v-model="svcCd" class="h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option value="">전체</option>
              <option v-for="opt in svcOptions" :key="opt.commonDtlCode" :value="opt.commonDtlCode">
                {{ opt.commonDtlCodeNm }}
              </option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색유형</label>
            <select v-model="searchType" class="h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option>가입ID</option>
              <option>가입명</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색어</label>
            <input v-model="keyword" @keydown.enter="handleSearch"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div class="self-end">
            <button @click="handleSearch" :disabled="isLoading"
              class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-60">조회</button>
          </div>
        </div>
      </div>

      <!-- 목록 -->
      <DataGrid
        :columns="columns"
        :data="mergedData"
        :page="page"
        :total-pages="totalPages"
        :total-elements="totalElements"
        :page-size="pageSize"
        :on-page-change="handlePageChange"
        row-id-accessor="subsId"
        :selected-row-id="focusedSubsId"
        selectable
        :selected-row-ids="selectedIds"
        :disabled-row-ids="disabledIds"
        @selection-change="selectedIds = $event"
        @row-click="handleRowClick"
        storage-key="subscriptionMainPage"
        title="대표가입 목록"
      />
      <!-- 입력 폼 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">대표가입 정보</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID</label>
            <input :value="formData.subsId" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">대표가입여부</label>
            <select v-model="formData.mainSubsYn" @change="syncFormToGrid"
              class="w-full h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option value="Y">Y</option>
              <option value="N">N</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">대표가입ID</label>
            <div class="flex gap-1">
              <input v-model="formData.mainSubsId" @input="syncFormToGrid"
                :readonly="formData.mainSubsYn === 'Y'"
                :class="['flex-1 h-8 px-2 border rounded text-sm focus:outline-none focus:border-blue-400',
                  formData.mainSubsYn === 'Y' ? 'bg-gray-50 text-gray-400 border-gray-200' : 'border-gray-300']" />
              <button v-if="formData.mainSubsYn === 'N'" @click="popupOpen = true"
                class="h-8 px-2 border border-gray-300 rounded text-xs text-gray-600 hover:bg-gray-50">검색</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <SubscriptionSearchPopup
      v-if="popupOpen"
      @close="popupOpen = false"
      @select="handlePopupSelect"
    />

    <ConfirmDialog
      v-if="saveConfirmOpen"
      :message="`${selectedIds.size}건을 저장하시겠습니까?`"
      confirm-text="저장"
      confirm-type="primary"
      @confirm="handleBulkSaveConfirm"
      @cancel="saveConfirmOpen = false"
    />

    <FloatingActionBar>
      <label class="h-8 px-4 border border-gray-300 text-gray-600 rounded text-sm
                    cursor-pointer hover:bg-gray-50 flex items-center">
        엑셀 업로드
        <input type="file" accept=".xlsx" class="hidden" @change="handleExcelUpload" ref="fileInputRef" />
      </label>
      <button @click="handleExcelDownload"
        class="h-8 px-4 border border-blue-600 text-blue-600 rounded text-sm hover:bg-blue-50">
        엑셀 다운로드
      </button>
      <button @click="handleBulkSave"
        class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">
        저장
      </button>
    </FloatingActionBar>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import {
  getSubscriptionMainList,
  downloadSubscriptionMainExcel,
  uploadSubscriptionMainExcel,
  saveSubscriptionMainBulk
} from '../api/subscriptionMainApi'
import { commonCodeApi } from '../api/commonCodeApi'
import Toast from '../components/common/Toast.vue'
import DataGrid from '../components/common/DataGrid.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'
import SubscriptionSearchPopup from '../components/common/SubscriptionSearchPopup.vue'
import { useCommonCodeLabel } from '../composables/useCommonCodeLabel'

const auth = useAuthStore()
const { getLabel } = useCommonCodeLabel(['svc_cd', 'fee_prod_cd'])

const columns = computed(() => [
  { key: 'subsId', header: '가입ID', size: 120 },
  { key: 'subsNm', header: '가입명', size: 150 },
  { key: 'svcCd', header: '서비스', size: 120,
    cell: { props: ['value'], setup(props) { return () => getLabel('svc_cd', props.value) } } },
  { key: 'feeProdCd', header: '요금상품', size: 120,
    cell: { props: ['value'], setup(props) { return () => getLabel('fee_prod_cd', props.value) } } },
  { key: 'mainSubsYn', header: '대표가입여부', size: 100 },
  { key: 'mainSubsId', header: '대표가입ID', size: 120 },
  { key: 'changeYn', header: '변경여부', size: 80, filterable: false, sortable: false },
  { key: 'resultMsg', header: '처리결과', size: 150, filterable: false, sortable: false },
])

const svcCd = ref('')
const svcOptions = ref([])
const searchType = ref('가입ID')
const keyword = ref('')
const items = ref([])
const isLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const fileInputRef = ref(null)

const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 10

const selectedIds = ref(new Set())
const uploadedRows = ref([])
const resultMap = ref(new Map())
const disabledIds = ref(new Set())
const saveConfirmOpen = ref(false)
const popupOpen = ref(false)
const focusedSubsId = ref(null)

const EMPTY_FORM = { subsId: '', mainSubsYn: 'Y', mainSubsId: '' }
const formData = reactive({ ...EMPTY_FORM })

const clearMessages = () => { errorMsg.value = ''; successMsg.value = '' }

const getSearchParams = () => ({
  svcCd: svcCd.value || undefined,
  searchType: searchType.value || undefined,
  keyword: keyword.value.trim(),
})

const mergedData = computed(() => {
  const originalMap = new Map()
  const map = new Map()

  items.value.forEach(row => {
    originalMap.set(row.subsId, { mainSubsYn: row.mainSubsYn, mainSubsId: row.mainSubsId || '' })
    map.set(row.subsId, { ...row })
  })

  uploadedRows.value.forEach(row => {
    if (map.has(row.subsId)) {
      const existing = map.get(row.subsId)
      existing.mainSubsYn = row.mainSubsYn
      existing.mainSubsId = row.mainSubsId
    } else {
      map.set(row.subsId, { ...row })
    }
  })

  const result = []
  map.forEach((row, subsId) => {
    const original = originalMap.get(subsId)
    let changeYn = ''
    if (original) {
      if (original.mainSubsYn !== row.mainSubsYn || original.mainSubsId !== (row.mainSubsId || '')) {
        changeYn = '변경'
      }
    } else {
      changeYn = '신규'
    }
    row.changeYn = changeYn
    row.resultMsg = resultMap.value.get(subsId) || ''
    result.push(row)
  })

  return result
})

const fetchList = async (pageNum = 0) => {
  const data = await getSubscriptionMainList({ ...getSearchParams(), page: pageNum, size: pageSize })
  items.value = data.content
  page.value = data.number
  totalPages.value = data.totalPages
  totalElements.value = data.totalElements

  uploadedRows.value = []
  resultMap.value = new Map()
  disabledIds.value = new Set()
  selectedIds.value = new Set()

  if (focusedSubsId.value) {
    const updated = data.content.find(r => r.subsId === focusedSubsId.value)
    if (updated) {
      Object.assign(formData, {
        subsId: updated.subsId || '',
        mainSubsYn: updated.mainSubsYn || 'Y',
        mainSubsId: updated.mainSubsId || '',
      })
    } else {
      focusedSubsId.value = null
      Object.assign(formData, EMPTY_FORM)
    }
  }
}

const handlePageChange = (newPage) => fetchList(newPage)

onMounted(async () => {
  try {
    svcOptions.value = await commonCodeApi.getEffectiveDetails('svc_cd')
  } catch { /* ignore */ }
})

const handleSearch = async () => {
  if (!keyword.value.trim()) { errorMsg.value = '조회조건을 입력해 주세요.'; return }
  if (keyword.value.trim().length < 2) { errorMsg.value = '조회조건은 2자 이상 입력해 주세요.'; return }
  clearMessages()
  isLoading.value = true
  try {
    await fetchList(0)
  } catch { errorMsg.value = '조회에 실패했습니다.' }
  finally { isLoading.value = false }
}

const handleRowClick = (row) => {
  focusedSubsId.value = row.subsId
  Object.assign(formData, {
    subsId: row.subsId || '',
    mainSubsYn: row.mainSubsYn || 'Y',
    mainSubsId: row.mainSubsId || '',
  })
}

const upsertUploadedRow = (subsId, mainSubsYn, mainSubsId, extra = {}) => {
  const existing = uploadedRows.value.find(r => r.subsId === subsId)
  if (existing) {
    existing.mainSubsYn = mainSubsYn
    existing.mainSubsId = mainSubsId
  } else {
    uploadedRows.value.push({ subsId, mainSubsYn, mainSubsId, ...extra })
  }
}

const syncFormToGrid = () => {
  if (!focusedSubsId.value) return
  const mainSubsId = formData.mainSubsYn === 'Y' ? '' : formData.mainSubsId
  upsertUploadedRow(focusedSubsId.value, formData.mainSubsYn, mainSubsId)
  if (formData.mainSubsYn === 'Y') formData.mainSubsId = ''
}

const handlePopupSelect = (subsId) => {
  formData.mainSubsId = subsId
  syncFormToGrid()
  popupOpen.value = false
}

const handleExcelDownload = async () => {
  clearMessages()
  if (selectedIds.value.size === 0) { errorMsg.value = '다운로드할 행을 선택해 주세요.'; return }
  try {
    const selectedItems = mergedData.value
      .filter(r => selectedIds.value.has(r.subsId))
      .map(r => ({ subsId: r.subsId, mainSubsYn: r.mainSubsYn, mainSubsId: r.mainSubsId }))
    const blob = await downloadSubscriptionMainExcel(selectedItems)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'subscription_main.xlsx'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    successMsg.value = '엑셀 다운로드가 완료되었습니다.'
  } catch {
    errorMsg.value = '엑셀 다운로드에 실패했습니다.'
  }
}

const handleExcelUpload = async (event) => {
  clearMessages()
  const file = event.target.files[0]
  if (!file) return
  try {
    const results = await uploadSubscriptionMainExcel(file)
    let validCount = 0
    results.forEach(row => {
      const inGrid = items.value.some(r => r.subsId === row.subsId)
      const extra = inGrid ? {} : { subsNm: '', svcCd: '', feeProdCd: '' }
      upsertUploadedRow(row.subsId, row.mainSubsYn, row.mainSubsId, extra)

      if (!row.valid) {
        disabledIds.value.add(row.subsId)
        resultMap.value.set(row.subsId, row.errorMessage)
      } else {
        disabledIds.value.delete(row.subsId)
        resultMap.value.delete(row.subsId)
        validCount++
      }
    })
    successMsg.value = `${results.length}건 업로드 완료 (유효: ${validCount}건, 오류: ${results.length - validCount}건)`
  } catch {
    errorMsg.value = '엑셀 업로드에 실패했습니다.'
  }
  event.target.value = ''
}

const handleBulkSave = () => {
  clearMessages()
  if (selectedIds.value.size === 0) { errorMsg.value = '저장할 행을 선택해 주세요.'; return }
  saveConfirmOpen.value = true
}

const handleBulkSaveConfirm = async () => {
  saveConfirmOpen.value = false
  try {
    const selectedItems = mergedData.value
      .filter(r => selectedIds.value.has(r.subsId))
      .map(r => ({
        subsId: r.subsId,
        mainSubsYn: r.mainSubsYn,
        mainSubsId: r.mainSubsYn === 'Y' ? null : r.mainSubsId,
        createdBy: auth.user?.userId ?? 'SYSTEM',
      }))
    const results = await saveSubscriptionMainBulk(selectedItems)

    results.forEach(r => {
      resultMap.value.set(r.subsId, r.valid ? '저장완료' : r.errorMessage)
    })

    const successCount = results.filter(r => r.valid).length
    const failCount = results.length - successCount
    if (failCount === 0) {
      successMsg.value = `${successCount}건 저장이 완료되었습니다.`
    } else {
      successMsg.value = `${successCount}건 저장 완료, ${failCount}건 실패`
    }
    await fetchList(page.value)
  } catch {
    errorMsg.value = '저장에 실패했습니다.'
  }
}
</script>
