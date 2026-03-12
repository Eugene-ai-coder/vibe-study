<template>
  <div>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">가입별 월별과금량</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID</label>
            <input v-model="keyword" @keydown.enter="handleSearch"
              placeholder="가입ID"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 w-48" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">사용월(시작)</label>
            <input v-model="useMthFrom" @keydown.enter="handleSearch"
              placeholder="YYYYMM"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 w-32" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">사용월(종료)</label>
            <input v-model="useMthTo" @keydown.enter="handleSearch"
              placeholder="YYYYMM"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 w-32" />
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
        :data="dataWithRowId"
        :page="page"
        :total-pages="totalPages"
        :total-elements="totalElements"
        :page-size="pageSize"
        :on-page-change="handlePageChange"
        row-id-accessor="_rowId"
        storage-key="subsMthBillQtyPage"
        title="가입별 월별과금량 목록"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getSubsMthBillQtyList } from '../api/subsMthBillQtyApi'
import Toast from '../components/common/Toast.vue'
import DataGrid from '../components/common/DataGrid.vue'

const columns = computed(() => [
  { key: 'subsId', header: '가입ID', size: 150 },
  { key: 'useMth', header: '사용월', size: 100 },
  { key: 'billStdId', header: '과금기준ID', size: 160 },
  { key: 'useQty', header: '사용량', size: 120 },
])

const items = ref([])
const keyword = ref('')
const useMthFrom = ref('')
const useMthTo = ref('')
const errorMsg = ref('')
const successMsg = ref('')
const isSearching = ref(false)

const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 10

const dataWithRowId = computed(() =>
  items.value.map(item => ({ ...item, _rowId: `${item.subsId}__${item.useMth}` }))
)

const fetchList = async (pageNum = 0) => {
  isSearching.value = true
  try {
    const params = { page: pageNum, size: pageSize }
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    if (useMthFrom.value.trim()) params.useMthFrom = useMthFrom.value.trim()
    if (useMthTo.value.trim()) params.useMthTo = useMthTo.value.trim()
    const data = await getSubsMthBillQtyList(params)
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

const handleSearch = () => fetchList(0)
const handlePageChange = (p) => fetchList(p)

onMounted(() => fetchList())
</script>
