<template>
  <MainLayout>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-6">
      <h1 class="text-xl font-bold text-gray-800">사용자 관리</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">사용자ID</label>
            <input v-model="searchUserId" @keydown.enter="handleSearch"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">사용자명</label>
            <input v-model="searchNickname" @keydown.enter="handleSearch"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">이메일</label>
            <input v-model="searchEmail" @keydown.enter="handleSearch"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div class="self-end">
            <button @click="handleSearch"
              class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">조회</button>
          </div>
        </div>
      </div>

      <!-- 목록 (DataGrid) -->
      <DataGrid
        :columns="columns"
        :data="users"
        :pinned-count="2"
        :page="page"
        :total-pages="totalPages"
        :total-elements="totalElements"
        :page-size="pageSize"
        :on-page-change="handlePageChange"
        row-id-accessor="userId"
        storage-key="userPage"
        title="사용자 목록"
      />

      <!-- 신규 등록 폼 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">신규 사용자 등록</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div v-for="field in formFields" :key="field.name">
            <label class="block text-xs text-gray-500 mb-1">{{ field.label }} <span class="text-blue-400">*</span></label>
            <input
              :type="field.type"
              v-model="form[field.name]"
              class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400"
            />
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar>
      <button @click="resetForm"
        class="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 transition-colors">취소</button>
      <button @click="onRegister"
        class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors">등록</button>
    </FloatingActionBar>
    <ConfirmDialog
      v-if="saveConfirmOpen"
      message="사용자를 등록하시겠습니까?"
      confirm-text="등록"
      confirm-type="primary"
      @confirm="handleRegisterConfirm"
      @cancel="saveConfirmOpen = false"
    />
  </MainLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { getUsersPage, register } from '../api/authApi'
import MainLayout from '../components/common/MainLayout.vue'
import Toast from '../components/common/Toast.vue'
import DataGrid from '../components/common/DataGrid.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'

const auth = useAuthStore()

const EMPTY_FORM = { userId: '', nickname: '', password: '', email: '' }
const formFields = [
  { name: 'userId', label: '아이디', type: 'text' },
  { name: 'nickname', label: '닉네임', type: 'text' },
  { name: 'password', label: '비밀번호', type: 'password' },
  { name: 'email', label: '이메일', type: 'email' },
]

const columns = [
  { key: 'userId', header: '아이디', size: 120, minSize: 60 },
  { key: 'nickname', header: '닉네임', size: 120, minSize: 60 },
  { key: 'email', header: '이메일', size: 200, minSize: 80 },
  { key: 'accountStatus', header: '상태', size: 80, minSize: 60, filterable: false },
  { key: 'createdBy', header: '등록자', size: 100, minSize: 60 },
  { key: 'createdDt', header: '등록일시', size: 150, minSize: 100, filterable: false },
]

const users = ref([])
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(10)
const form = reactive({ ...EMPTY_FORM })
const saveConfirmOpen = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const searchUserId = ref('')
const searchNickname = ref('')
const searchEmail = ref('')

const getSearchParams = () => {
  const params = {}
  if (searchUserId.value) params.userId = searchUserId.value
  if (searchNickname.value) params.nickname = searchNickname.value
  if (searchEmail.value) params.email = searchEmail.value
  return params
}

const fetchUsers = async (params = {}, p = 0) => {
  try {
    const result = await getUsersPage({ ...params, page: p, size: pageSize.value })
    users.value = result.content
    page.value = result.number
    totalPages.value = result.totalPages
    totalElements.value = result.totalElements
  } catch {
    errorMsg.value = '사용자 목록 조회에 실패했습니다.'
  }
}

onMounted(() => fetchUsers())

const handleSearch = () => {
  errorMsg.value = ''
  successMsg.value = ''
  fetchUsers(getSearchParams(), 0)
}

const handlePageChange = (newPage) => {
  fetchUsers(getSearchParams(), newPage)
}

const resetForm = () => Object.assign(form, EMPTY_FORM)

const onRegister = () => {
  errorMsg.value = ''
  successMsg.value = ''
  saveConfirmOpen.value = true
}

const handleRegisterConfirm = async () => {
  saveConfirmOpen.value = false
  try {
    await register({ ...form, createdBy: auth.user?.userId })
    successMsg.value = '등록이 완료되었습니다.'
    resetForm()
    fetchUsers(getSearchParams(), 0)
  } catch (err) {
    const msg = err?.response?.data?.message
    errorMsg.value = msg || '등록에 실패했습니다.'
  }
}
</script>
