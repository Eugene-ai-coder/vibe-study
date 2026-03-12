<template>
  <div>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">결재관리</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID</label>
            <input v-model="searchSubsId" @keydown.enter="handleSearch"
              placeholder="가입ID"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 w-40" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">등록진행상태</label>
            <CommonCodeSelect v-model="searchStdRegStatCd" common-code="std_reg_stat_cd"
              :select-class="'h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 bg-white w-36'" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">시작일</label>
            <input v-model="searchStartDt" type="date"
              class="h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">종료일</label>
            <input v-model="searchEndDt" type="date"
              class="h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div class="self-end">
            <button @click="handleSearch" :disabled="isSearching"
              class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-60">조회</button>
          </div>
        </div>
      </div>

      <!-- 목록 -->
      <DataGrid
        :columns="columns"
        :data="items"
        :page="page"
        :total-pages="totalPages"
        :total-elements="totalElements"
        :page-size="pageSize"
        :on-page-change="handlePageChange"
        :selected-row-id="selectedRow?.billStdId"
        row-id-accessor="billStdId"
        @row-click="handleRowClick"
        storage-key="apprvReqPage"
        title="결재관리 목록"
      />

      <!-- 대표정보 -->
      <div v-if="billStdDetail" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">과금기준 상세</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">과금기준ID</label>
            <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-400">{{ billStdDetail.billStdId }}</div>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID</label>
            <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-400">{{ billStdDetail.subsId }}</div>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스코드</label>
            <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-400">{{ billStdDetail.svcCd }}</div>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">유효시작일</label>
            <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-400">{{ billStdDetail.effStartDt }}</div>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">유효종료일</label>
            <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-400">{{ billStdDetail.effEndDt }}</div>
          </div>
        </div>
        <div v-if="billStdDetail.fieldValues && Object.keys(billStdDetail.fieldValues).length > 0" class="mt-3">
          <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2">동적 필드</h3>
          <div class="grid grid-cols-3 gap-x-4 gap-y-3">
            <div v-for="(val, key) in billStdDetail.fieldValues" :key="key">
              <label class="block text-xs text-gray-500 mb-1">{{ key }}</label>
              <div class="h-8 flex items-center px-2 bg-gray-50 border border-gray-200 rounded text-sm text-gray-400">{{ val }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 액션바 -->
      <FloatingActionBar>
        <button @click="openApprvReqPopup" :disabled="!canRequestApprv"
          class="h-8 px-4 bg-blue-600 text-white rounded text-sm hover:bg-blue-700 disabled:opacity-40 disabled:cursor-not-allowed">결재요청</button>
        <button @click="openApprvDetailPopup" :disabled="!canViewApprvDetail"
          class="h-8 px-4 border border-blue-600 text-blue-600 rounded text-sm hover:bg-blue-50 disabled:opacity-40 disabled:cursor-not-allowed">결재내용 상세</button>
      </FloatingActionBar>
    </div>

    <!-- 결재요청 팝업 -->
    <ApprvReqPopup
      v-if="showApprvReqPopup"
      :bill-std-id="selectedRow.billStdId"
      :subs-id="selectedRow.subsId"
      @close="showApprvReqPopup = false"
      @submitted="handleApprvSubmitted"
    />

    <!-- 결재내용 상세 팝업 -->
    <ApprvDetailPopup
      v-if="showApprvDetailPopup"
      :apprv-req-id="selectedRow.apprvReqId"
      @close="showApprvDetailPopup = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getApprvReqList } from '../api/apprvReqApi'
import { getBillStd } from '../api/billStdApi'
import Toast from '../components/common/Toast.vue'
import DataGrid from '../components/common/DataGrid.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import CommonCodeSelect from '../components/common/CommonCodeSelect.vue'
import ApprvReqPopup from '../components/apprv/ApprvReqPopup.vue'
import ApprvDetailPopup from '../components/apprv/ApprvDetailPopup.vue'
import { useCommonCodeLabel } from '../composables/useCommonCodeLabel'

const { getLabel } = useCommonCodeLabel(['std_reg_stat_cd'])

const columns = computed(() => [
  { key: 'subsId', header: '가입ID', size: 120 },
  { key: 'subsNm', header: '가입명', size: 150 },
  { key: 'billStdId', header: '과금기준ID', size: 160 },
  { key: 'stdRegStatCd', header: '등록진행상태', size: 120,
    cell: { props: ['value'], setup(props) { return () => getLabel('std_reg_stat_cd', props.value) } } },
  { key: 'billStdRegDt', header: '등록일시', size: 160 },
  { key: 'apprvReqDt', header: '결재요청일시', size: 160 },
])

const items = ref([])
const searchSubsId = ref('')
const searchStdRegStatCd = ref('')
const searchStartDt = ref('')
const searchEndDt = ref('')
const errorMsg = ref('')
const successMsg = ref('')
const isSearching = ref(false)

const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 10

const selectedRow = ref(null)
const billStdDetail = ref(null)
const showApprvReqPopup = ref(false)
const showApprvDetailPopup = ref(false)

const canRequestApprv = computed(() =>
  selectedRow.value && selectedRow.value.stdRegStatCd === 'REVIEW'
)

const canViewApprvDetail = computed(() =>
  selectedRow.value && selectedRow.value.apprvReqDt
)

const fetchList = async (pageNum = 0) => {
  isSearching.value = true
  try {
    const params = { page: pageNum, size: pageSize }
    if (searchSubsId.value.trim()) params.subsId = searchSubsId.value.trim()
    if (searchStdRegStatCd.value) params.stdRegStatCd = searchStdRegStatCd.value
    if (searchStartDt.value) params.startDt = searchStartDt.value
    if (searchEndDt.value) params.endDt = searchEndDt.value

    const data = await getApprvReqList(params)
    items.value = data.content
    page.value = data.number
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
  } catch {
    errorMsg.value = '조회에 실패했습니다.'
  } finally {
    isSearching.value = false
  }
}

const handleSearch = () => {
  selectedRow.value = null
  billStdDetail.value = null
  fetchList(0)
}

const handlePageChange = (p) => fetchList(p)

const handleRowClick = async (row) => {
  selectedRow.value = row
  billStdDetail.value = null
  try {
    billStdDetail.value = await getBillStd(row.billStdId)
  } catch {
    errorMsg.value = '과금기준 상세 조회에 실패했습니다.'
  }
}

const openApprvReqPopup = () => {
  if (!canRequestApprv.value) return
  showApprvReqPopup.value = true
}

const openApprvDetailPopup = () => {
  if (!canViewApprvDetail.value) return
  showApprvDetailPopup.value = true
}

const handleApprvSubmitted = () => {
  successMsg.value = '결재요청이 완료되었습니다.'
  selectedRow.value = null
  billStdDetail.value = null
  fetchList(page.value)
}

onMounted(() => fetchList())
</script>
