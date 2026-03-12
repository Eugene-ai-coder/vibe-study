<template>
  <div>
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />
    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">내 할일</h1>

      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <select v-model="todoStatusCd" class="h-8 border border-gray-300 rounded px-2 text-sm">
            <option value="">상태 전체</option>
            <option value="OPEN">미완료</option>
            <option value="DONE">완료</option>
          </select>
          <select v-model="entityType" class="h-8 border border-gray-300 rounded px-2 text-sm">
            <option value="">업무유형 전체</option>
            <option value="SUBSCRIPTION">가입</option>
            <option value="BILL_STD">과금기준</option>
          </select>
          <button @click="handleSearch" class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">조회</button>
        </div>
      </div>

      <DataGrid :columns="columns" :data="items" :page="page" :total-pages="totalPages"
        :total-elements="totalElements" :page-size="pageSize" :on-page-change="handlePageChange"
        row-id-accessor="todoId" @row-click="handleRowClick"
        storage-key="todoPage" title="할일 목록" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { todoApi } from '../api/todoApi'
import Toast from '../components/common/Toast.vue'
import DataGrid from '../components/common/DataGrid.vue'

const router = useRouter()

const columns = [
  { key: 'todoId', header: '번호', size: 60 },
  { key: 'entityType', header: '업무유형', size: 120,
    cell: { props: ['row'], setup(props) {
      return () => {
        const val = props.row.entityType
        if (val === 'SUBSCRIPTION') return '가입'
        if (val === 'BILL_STD') return '과금기준'
        return val
      }
    }}
  },
  { key: 'todoTitle', header: '할일 제목', size: 300 },
  { key: 'todoStatusCd', header: '상태', size: 80,
    cell: { props: ['row'], setup(props) {
      return () => {
        const val = props.row.todoStatusCd
        if (val === 'OPEN') return h('span', { class: 'inline-block px-2 py-0.5 text-xs font-medium rounded-full bg-blue-100 text-blue-700' }, '미완료')
        if (val === 'DONE') return h('span', { class: 'inline-block px-2 py-0.5 text-xs font-medium rounded-full bg-gray-100 text-gray-500' }, '완료')
        return val
      }
    }}
  },
  { key: 'effStartDt', header: '시작일시', size: 150 },
  { key: 'effEndDt', header: '종료일시', size: 150 },
]

const items = ref([])
const todoStatusCd = ref('')
const entityType = ref('')
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(10)
const errorMsg = ref('')

const fetchList = async (p = 0) => {
  try {
    const params = { page: p, size: pageSize.value }
    if (todoStatusCd.value) params.todoStatusCd = todoStatusCd.value
    if (entityType.value) params.entityType = entityType.value
    const result = await todoApi.getAll(params)
    items.value = result.content
    page.value = result.number
    totalPages.value = result.totalPages
    totalElements.value = result.totalElements
  } catch { errorMsg.value = '목록 조회에 실패했습니다.' }
}

onMounted(() => fetchList())

const handleSearch = () => fetchList(0)
const handlePageChange = (p) => fetchList(p)

const handleRowClick = (row) => {
  if (row.entityType === 'SUBSCRIPTION') {
    router.push('/subscriptions')
  } else if (row.entityType === 'BILL_STD') {
    router.push('/subs-bill-std')
  }
}
</script>
