<template>
  <div>
    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">가입별 과금기준 목록</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색어</label>
            <input v-model="keyword" @keydown.enter="handleSearch"
              placeholder="가입ID 또는 가입명"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 w-48" />
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
        row-id-accessor="subsId"
        @row-click="handleRowClick"
        storage-key="subsBillStdListPage"
        title="가입별 과금기준 목록"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSubsBillStdList } from '../api/subsBillStdApi'
import { useToast } from '../composables/useToast'
import DataGrid from '../components/common/DataGrid.vue'
import { useCommonCodeLabel } from '../composables/useCommonCodeLabel'

const router = useRouter()
const { getLabel } = useCommonCodeLabel(['subs_status_cd', 'svc_cd', 'basic_prod_cd'])

const columns = computed(() => [
  { key: 'subsId', header: '가입ID', size: 120 },
  { key: 'subsNm', header: '가입명', size: 150 },
  { key: 'subsStatusCd', header: '가입상태', size: 100,
    cell: { props: ['value'], setup(props) { return () => getLabel('subs_status_cd', props.value) } } },
  { key: 'svcCd', header: '서비스코드', size: 100,
    cell: { props: ['value'], setup(props) { return () => getLabel('svc_cd', props.value) } } },
  { key: 'basicProdCd', header: '기본상품코드', size: 100,
    cell: { props: ['value'], setup(props) { return () => getLabel('basic_prod_cd', props.value) } } },
  { key: 'billStdId', header: '과금기준ID', size: 160 },
  { key: 'billStdNm', header: '과금기준명', size: 150 },
  { key: 'effStartDt', header: '유효시작일', size: 160 },
  { key: 'effEndDt', header: '유효종료일', size: 160 },
])

const items = ref([])
const keyword = ref('')
const { showError } = useToast()
const isSearching = ref(false)

const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 10

const fetchList = async (pageNum = 0) => {
  isSearching.value = true
  try {
    const data = await getSubsBillStdList({ keyword: keyword.value.trim() || undefined, page: pageNum, size: pageSize })
    items.value = data.content
    page.value = data.number
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
  } catch {
    showError('조회에 실패했습니다.')
  } finally {
    isSearching.value = false
  }
}

const handleSearch = () => fetchList(0)
const handlePageChange = (p) => fetchList(p)

const handleRowClick = (row) => {
  router.push({ path: '/bill-std', query: { subsId: row.subsId } })
}

onMounted(() => fetchList())
</script>
