<template>
  <div>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">내 할일</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">상태</label>
            <select v-model="statusFilter" class="h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 bg-white">
              <option value="">전체</option>
              <option value="PENDING">PENDING</option>
              <option value="IN_PROGRESS">IN_PROGRESS</option>
              <option value="COMPLETED">COMPLETED</option>
            </select>
          </div>
          <div v-if="isAdmin">
            <label class="block text-xs text-gray-500 mb-1">담당자</label>
            <select v-model="assigneeFilter" class="h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 bg-white">
              <option value="">본인</option>
              <option value="ALL">전체</option>
            </select>
          </div>
          <div class="self-end">
            <button @click="onSearch"
              class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">조회</button>
          </div>
        </div>
      </div>

      <!-- 목록 -->
      <DataGrid
        :columns="columns"
        :data="items"
        row-id-accessor="taskInstId"
        :selected-row-id="selectedTask?.taskInstId"
        @row-click="onRowSelect"
        title="할일 목록"
      />

      <!-- Task 상세 -->
      <div v-if="selectedTask" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">Task 상세</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">Task명</label>
            <input :value="selectedTask.taskNm" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">상태</label>
            <input :value="selectedTask.status" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">Task유형</label>
            <input :value="selectedTask.taskType" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">현재상태</label>
            <input :value="selectedTask.stateNm" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">담당자</label>
            <input :value="selectedTask.assigneeId" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">우선순위</label>
            <input :value="selectedTask.priority" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">마감일</label>
            <input :value="selectedTask.dueDt" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">결과</label>
            <input :value="selectedTask.result" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">코멘트</label>
            <input :value="selectedTask.comment" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
        </div>

        <!-- IN_PROGRESS일 때만: 코멘트 입력 -->
        <div v-if="selectedTask.status === 'IN_PROGRESS'" class="mt-3">
          <label class="block text-xs text-gray-500 mb-1">코멘트</label>
          <input v-model="actionComment" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
        </div>
      </div>
    </div>

    <FloatingActionBar>
      <!-- PENDING -->
      <button v-if="selectedTask?.status === 'PENDING'"
        @click="onClaim"
        class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors">
        담당 지정
      </button>

      <!-- IN_PROGRESS -->
      <template v-if="selectedTask?.status === 'IN_PROGRESS'">
        <button @click="onComplete('APPROVED')"
          class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors">승인</button>
        <button @click="onComplete('REJECTED')"
          class="h-8 px-4 border border-red-300 text-sm rounded text-red-600 hover:bg-red-50 transition-colors">반려</button>
        <button @click="onComplete('DONE')"
          class="h-8 px-4 border border-blue-600 text-sm rounded text-blue-600 hover:bg-blue-50 transition-colors">완료</button>
      </template>

      <!-- 업무 화면 이동 (상태 무관, 선택 시 항상 표시) -->
      <button v-if="selectedTask"
        @click="goToEntity"
        class="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 transition-colors">
        업무 화면 이동
      </button>
    </FloatingActionBar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { wfTaskApi } from '../api/wfTaskApi'
import { wfEngineApi } from '../api/wfEngineApi'
import { wfEntityTypeApi } from '../api/wfEntityTypeApi'
import Toast from '../components/common/Toast.vue'
import DataGrid from '../components/common/DataGrid.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'

const auth = useAuthStore()
const router = useRouter()

const currentUserId = computed(() => auth.user?.userId ?? '')
const isAdmin = computed(() => auth.user?.roles?.includes('ADMIN') ?? false)

const statusFilter = ref('')
const assigneeFilter = ref('')
const items = ref([])
const selectedTask = ref(null)
const actionComment = ref('')
const errorMsg = ref('')
const successMsg = ref('')

const clearMessages = () => { errorMsg.value = ''; successMsg.value = '' }

const priorityCell = {
  props: ['value'],
  setup(props) {
    return () => {
      const v = props.value
      if (v != null && v <= 3) {
        return h('span', { class: 'text-red-600 font-bold' }, v)
      }
      return h('span', null, v)
    }
  }
}

const statusBadgeCell = {
  props: ['value'],
  setup(props) {
    return () => {
      const v = props.value
      const cls = {
        'PENDING': 'bg-gray-100 text-gray-600',
        'IN_PROGRESS': 'bg-blue-100 text-blue-600',
        'COMPLETED': 'bg-green-100 text-green-600',
        'CANCELLED': 'bg-red-100 text-red-600',
      }[v] || 'bg-gray-100 text-gray-600'
      return h('span', { class: `inline-block px-2 py-0.5 rounded text-xs font-medium ${cls}` }, v)
    }
  }
}

const columns = [
  { key: 'priority', header: '우선순위', size: 80, cell: priorityCell },
  { key: 'taskNm', header: 'Task명', size: 200 },
  { key: 'status', header: '상태', size: 100, cell: statusBadgeCell },
  { key: 'taskType', header: 'Task유형', size: 100 },
  { key: 'stateNm', header: '현재상태', size: 120 },
  { key: 'assigneeId', header: '담당자', size: 100 },
  { key: 'dueDt', header: '마감일', size: 160 },
  { key: 'createdDt', header: '생성일', size: 160 },
]

const onSearch = async () => {
  clearMessages()
  try {
    const params = {}
    if (assigneeFilter.value !== 'ALL') {
      params.assigneeId = currentUserId.value
    }
    if (statusFilter.value) params.status = statusFilter.value
    items.value = await wfTaskApi.getMyTasks(params)
    selectedTask.value = null
  } catch { errorMsg.value = '조회에 실패했습니다.' }
}

const onRowSelect = (item) => {
  selectedTask.value = item
  actionComment.value = ''
}

const onClaim = async () => {
  clearMessages()
  try {
    await wfTaskApi.claimTask(selectedTask.value.taskInstId, { createdBy: currentUserId.value })
    successMsg.value = '담당이 지정되었습니다.'
    await onSearch()
  } catch (err) {
    errorMsg.value = err?.response?.data?.message || '담당 지정에 실패했습니다.'
  }
}

const onComplete = async (result) => {
  clearMessages()
  try {
    await wfTaskApi.completeTask(selectedTask.value.taskInstId, {
      result,
      comment: actionComment.value || null,
      createdBy: currentUserId.value,
    })
    successMsg.value = '처리가 완료되었습니다.'
    actionComment.value = ''
    await onSearch()
  } catch (err) {
    errorMsg.value = err?.response?.data?.message || '처리에 실패했습니다.'
  }
}

const goToEntity = async () => {
  clearMessages()
  try {
    const pi = await wfEngineApi.getProcessInst(selectedTask.value.processInstId)
    const { entityType, entityId } = pi
    const et = entityTypes.value.find(e => e.entityTypeCd === entityType)
    if (!et?.routePath) {
      errorMsg.value = `엔티티유형(${entityType})의 화면 경로가 정의되지 않았습니다.`
      return
    }
    router.push({ path: et.routePath, query: { entityId } })
  } catch {
    errorMsg.value = '업무 화면 이동에 실패했습니다.'
  }
}

const entityTypes = ref([])

onMounted(async () => {
  try {
    entityTypes.value = await wfEntityTypeApi.getAll()
  } catch {
    // 엔티티유형 로드 실패 시 goToEntity에서 개별 처리
  }
  onSearch()
})
</script>
