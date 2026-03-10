<template>
  <MainLayout>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4 pb-20">
      <h1 class="text-xl font-bold text-gray-800">학습 로그</h1>

      <!-- 등록 폼 -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h2 class="text-lg font-semibold text-gray-800 mb-4">새 학습 내용 추가</h2>

        <div v-if="formErrors.length > 0" class="bg-red-50 border border-red-200 rounded-lg p-3 mb-4">
          <ul class="list-disc list-inside text-sm text-red-700 space-y-1">
            <li v-for="(msg, i) in formErrors" :key="i">{{ msg }}</li>
          </ul>
        </div>

        <form @submit.prevent="handleCreate" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">학습 내용</label>
            <input v-model="newContent" placeholder="오늘 공부한 내용을 입력하세요"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">날짜</label>
            <input type="date" v-model="newDate"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <button type="submit" :disabled="isSubmitting"
            class="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors flex items-center gap-2">
            <span v-if="isSubmitting" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
            {{ isSubmitting ? '저장 중...' : '저장' }}
          </button>
        </form>
      </div>

      <!-- 로딩 -->
      <Loading v-if="isLoading" />

      <!-- 목록 -->
      <template v-else>
        <div v-if="logs.length === 0" class="bg-blue-50 border border-blue-200 rounded-xl p-6 text-center text-sm text-blue-700">
          아직 저장된 학습 기록이 없습니다. 위 폼으로 첫 번째 학습 내용을 추가해보세요!
        </div>

        <div v-else class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          <div class="p-6 pb-4">
            <h2 class="text-lg font-semibold text-gray-800">학습 기록 목록</h2>
          </div>
          <table class="w-full text-sm">
            <thead class="bg-blue-50 text-blue-800 text-left">
              <tr>
                <th class="px-6 py-3 font-medium">#</th>
                <th class="px-6 py-3 font-medium">학습 내용</th>
                <th class="px-6 py-3 font-medium">날짜</th>
                <th class="px-6 py-3 font-medium">관리</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              <tr v-for="log in logs" :key="log.id" class="hover:bg-gray-50 transition-colors">
                <td class="px-6 py-3 text-gray-500">{{ log.id }}</td>
                <td class="px-6 py-3 text-gray-800">{{ log.content || '-' }}</td>
                <td class="px-6 py-3 text-gray-600">{{ log.date || '-' }}</td>
                <td class="px-6 py-3 flex gap-2">
                  <button @click="editingLog = log"
                    class="text-gray-600 hover:text-blue-600 border border-gray-300 hover:border-blue-400 px-2 py-1 rounded-md text-xs transition-colors">수정</button>
                  <button @click="confirmDeleteId = log.id"
                    class="text-gray-600 hover:text-red-600 border border-gray-300 hover:border-red-400 px-2 py-1 rounded-md text-xs transition-colors">삭제</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </template>
    </div>

    <!-- 수정 모달 -->
    <div v-if="editingLog" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl shadow-xl w-full max-w-md mx-4">
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h3 class="text-base font-semibold text-gray-800">학습 내용 수정</h3>
          <button @click="editingLog = null" class="text-gray-400 hover:text-gray-600 text-xl leading-none">&times;</button>
        </div>
        <form @submit.prevent="handleUpdate" class="p-6 space-y-4">
          <div v-if="editErrors.length > 0" class="bg-red-50 border border-red-200 rounded-lg p-3">
            <ul class="list-disc list-inside text-sm text-red-700 space-y-1">
              <li v-for="(msg, i) in editErrors" :key="i">{{ msg }}</li>
            </ul>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">학습 내용</label>
            <input v-model="editContent"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">날짜</label>
            <input type="date" v-model="editDate"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div class="flex justify-end gap-2 pt-2">
            <button type="button" @click="editingLog = null"
              class="text-sm text-gray-600 hover:text-gray-800 border border-gray-300 hover:border-gray-400 px-4 py-2 rounded-lg transition-colors">취소</button>
            <button type="submit" :disabled="isEditing"
              class="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors flex items-center gap-2">
              <span v-if="isEditing" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              {{ isEditing ? '저장 중...' : '저장' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 삭제 확인 -->
    <ConfirmDialog
      v-if="confirmDeleteId !== null"
      message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
      confirm-text="삭제"
      confirm-type="danger"
      @confirm="handleDelete"
      @cancel="confirmDeleteId = null"
    />

    <ConfirmDialog
      v-if="saveConfirmOpen"
      :message="saveConfirmMessage"
      confirm-text="저장"
      confirm-type="primary"
      @confirm="handleSaveConfirm"
      @cancel="saveConfirmOpen = false"
    />
  </MainLayout>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { getLogs, createLog, updateLog, deleteLog } from '../api/studyLogApi'
import MainLayout from '../components/common/MainLayout.vue'
import Toast from '../components/common/Toast.vue'
import Loading from '../components/common/Loading.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'

const logs = ref([])
const isLoading = ref(true)
const errorMsg = ref('')
const successMsg = ref('')

// 등록 폼
const newContent = ref('')
const newDate = ref('')
const formErrors = ref([])
const isSubmitting = ref(false)

// 수정 모달
const editingLog = ref(null)
const editContent = ref('')
const editDate = ref('')
const editErrors = ref([])
const isEditing = ref(false)

// 저장 확인
const saveConfirmOpen = ref(false)
const saveConfirmMessage = ref('')
const saveConfirmAction = ref(null)

// 삭제
const confirmDeleteId = ref(null)

watch(editingLog, (log) => {
  if (log) {
    editContent.value = log.content || ''
    editDate.value = log.date || ''
    editErrors.value = []
  }
})

const fetchLogs = async () => {
  isLoading.value = true
  try { logs.value = await getLogs() }
  catch { errorMsg.value = '서버와 연결할 수 없습니다.' }
  finally { isLoading.value = false }
}

onMounted(fetchLogs)

const handleCreate = () => {
  formErrors.value = []
  saveConfirmMessage.value = '학습 내용을 등록하시겠습니까?'
  saveConfirmAction.value = 'create'
  saveConfirmOpen.value = true
}

const handleUpdate = () => {
  editErrors.value = []
  saveConfirmMessage.value = '학습 내용을 수정하시겠습니까?'
  saveConfirmAction.value = 'update'
  saveConfirmOpen.value = true
}

const handleSaveConfirm = async () => {
  saveConfirmOpen.value = false
  if (saveConfirmAction.value === 'create') {
    isSubmitting.value = true
    try {
      const newLog = await createLog({ content: newContent.value, date: newDate.value })
      logs.value = [...logs.value, newLog]
      newContent.value = ''
      newDate.value = ''
      successMsg.value = '등록이 완료되었습니다.'
    } catch (err) {
      if (err.response?.data?.errors) formErrors.value = err.response.data.errors
      else formErrors.value = ['서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.']
    } finally { isSubmitting.value = false }
  } else {
    isEditing.value = true
    try {
      const updated = await updateLog(editingLog.value.id, { content: editContent.value, date: editDate.value })
      logs.value = logs.value.map(l => l.id === updated.id ? updated : l)
      editingLog.value = null
      successMsg.value = '수정이 완료되었습니다.'
    } catch (err) {
      if (err.response?.data?.errors) editErrors.value = err.response.data.errors
      else editErrors.value = ['서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.']
    } finally { isEditing.value = false }
  }
}

const handleDelete = async () => {
  const id = confirmDeleteId.value
  confirmDeleteId.value = null
  try {
    await deleteLog(id)
    logs.value = logs.value.filter(l => l.id !== id)
    successMsg.value = '삭제가 완료되었습니다.'
  } catch { errorMsg.value = '삭제에 실패했습니다.' }
}
</script>
