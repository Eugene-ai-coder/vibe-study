<template>
  <div>
    <div class="space-y-4">
      <div class="flex items-center justify-between">
        <h1 class="text-xl font-bold text-gray-800">Q&A</h1>
        <button @click="router.push('/qna/new')"
          class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">글쓰기</button>
      </div>

      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <input v-model="keyword" @keydown.enter="handleSearch" placeholder="검색어 입력..."
            class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 flex-1" />
          <button @click="handleSearch" class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">검색</button>
        </div>
      </div>

      <DataGrid :columns="columns" :data="items" :page="page" :total-pages="totalPages"
        :total-elements="totalElements" :page-size="pageSize" :on-page-change="handlePageChange"
        row-id-accessor="qnaId" @row-click="(row) => router.push(`/qna/${row.qnaId}`)"
        storage-key="qnaPage" title="Q&A 목록" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { qnaApi } from '../api/qnaApi'
import { useToast } from '../composables/useToast'
import DataGrid from '../components/common/DataGrid.vue'

const router = useRouter()

const columns = [
  { key: 'qnaId', header: '번호', size: 60 },
  { key: 'title', header: '제목', size: 300,
    cell: { props: ['row'], setup(props) {
      return () => {
        const r = props.row
        const isNotice = r.noticeYn === 'Y'
          && r.noticeStartDt && r.noticeEndDt
          && new Date() >= new Date(r.noticeStartDt)
          && new Date() <= new Date(r.noticeEndDt)
        return isNotice ? `[공지] ${r.title}` : r.title
      }
    }}
  },
  { key: 'createdBy', header: '작성자', size: 100 },
  { key: 'createdDt', header: '작성일', size: 150 },
]

const items = ref([])
const keyword = ref('')
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(10)
const { showError } = useToast()

const fetchList = async (p = 0) => {
  try {
    const result = await qnaApi.getAll({ keyword: keyword.value, page: p, size: pageSize.value })
    items.value = result.content
    page.value = result.number
    totalPages.value = result.totalPages
    totalElements.value = result.totalElements
  } catch { showError('목록 조회에 실패했습니다.') }
}

onMounted(() => fetchList())

const handleSearch = () => fetchList(0)
const handlePageChange = (p) => fetchList(p)
</script>
