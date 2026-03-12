<template>
  <div>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">워크플로우 정의</h1>

      <div class="flex gap-4">
        <!-- 좌측 30%: 프로세스 목록 -->
        <div class="w-[30%] space-y-2">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-3">
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-semibold text-gray-500 uppercase tracking-wide">프로세스 목록</span>
              <button @click="handleAddProcess"
                class="h-7 px-3 bg-blue-600 text-white text-xs rounded hover:bg-blue-700">추가</button>
            </div>
            <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
              <table class="w-full text-sm border-collapse">
                <thead class="bg-gray-50 border-t border-gray-300 border-b border-gray-300">
                  <tr>
                    <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">프로세스명</th>
                    <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">엔티티유형</th>
                    <th class="px-3 py-1.5 text-center text-xs font-medium text-gray-500 w-14">사용</th>
                  </tr>
                </thead>
              </table>
              <div class="max-h-[40rem] overflow-y-auto">
                <table class="w-full text-sm border-collapse">
                  <tbody>
                    <tr v-if="processDefs.length === 0">
                      <td colspan="3" class="px-3 py-4 text-center text-xs text-gray-400">데이터가 없습니다.</td>
                    </tr>
                    <tr v-for="proc in processDefs" :key="proc.processDefId"
                      @click="handleProcessSelect(proc)"
                      :class="['h-7 cursor-pointer border-b border-gray-200',
                        selectedProcess?.processDefId === proc.processDefId ? 'bg-blue-50 text-blue-900' : 'hover:bg-gray-50 text-gray-800']">
                      <td class="px-3 text-xs">{{ proc.processNm }}</td>
                      <td class="px-3 text-xs">{{ getEntityTypeNm(proc.entityType) }}</td>
                      <td class="px-3 text-xs text-center">{{ proc.useYn }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <!-- 우측 70%: 상세 편집 -->
        <div class="w-[70%] space-y-4">
          <!-- 영역 A: 프로세스 기본정보 폼 -->
          <div v-if="selectedProcess || isNew" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
            <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">프로세스 기본정보</h3>
            <div class="grid grid-cols-3 gap-x-4 gap-y-3">
              <div>
                <label class="block text-xs text-gray-500 mb-1">프로세스명 <span class="text-blue-400">*</span></label>
                <input v-model="processForm.processNm"
                  class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">엔티티유형 <span class="text-blue-400">*</span></label>
                <select v-model="processForm.entityType"
                  class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400">
                  <option value="">선택</option>
                  <option v-for="et in entityTypes" :key="et.entityTypeCd" :value="et.entityTypeCd">
                    {{ et.entityTypeNm }}
                  </option>
                </select>
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">사용여부</label>
                <select v-model="processForm.useYn"
                  class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400">
                  <option value="Y">사용</option>
                  <option value="N">미사용</option>
                </select>
              </div>
              <div class="col-span-3">
                <label class="block text-xs text-gray-500 mb-1">설명</label>
                <textarea v-model="processForm.processDesc" rows="2"
                  class="w-full border border-gray-300 rounded px-2 py-1 text-sm focus:outline-none focus:border-blue-400 resize-none" />
              </div>
            </div>
          </div>

          <!-- 영역 B/C/D: 탭 (신규 미저장 시 비활성) -->
          <div v-if="selectedProcess && !isNew">
            <!-- 탭 헤더 -->
            <div class="flex border-b border-gray-200">
              <button @click="activeTab = 'states'"
                :class="['px-4 py-2 text-sm font-medium border-b-2 transition-colors',
                  activeTab === 'states' ? 'border-blue-600 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700']">
                상태 정의
              </button>
              <button @click="activeTab = 'transitions'"
                :class="['px-4 py-2 text-sm font-medium border-b-2 transition-colors',
                  activeTab === 'transitions' ? 'border-blue-600 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700']">
                전이 정의
              </button>
              <button @click="activeTab = 'tasks'"
                :class="['px-4 py-2 text-sm font-medium border-b-2 transition-colors',
                  activeTab === 'tasks' ? 'border-blue-600 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700']">
                Task 템플릿
              </button>
            </div>

            <!-- 상태 정의 탭 -->
            <div v-if="activeTab === 'states'" class="bg-white rounded-b-lg shadow-sm border border-t-0 border-gray-200 p-3">
              <div class="overflow-hidden">
                <table class="w-full text-sm border-collapse">
                  <thead class="bg-gray-50 border-t border-b border-gray-300">
                    <tr>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">상태명</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">상태유형</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-20">정렬순서</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">엔티티상태코드</th>
                      <th class="px-3 py-1.5 text-center text-xs font-medium text-gray-500 w-16">삭제</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-if="states.length === 0">
                      <td colspan="5" class="px-3 py-4 text-center text-xs text-gray-400">데이터가 없습니다.</td>
                    </tr>
                    <tr v-for="state in states" :key="state.stateDefId || state._tempId"
                      class="h-7 border-b border-gray-200">
                      <td @dblclick="startEdit(state, 'stateNm', 'state')" class="px-3 text-xs">
                        <input v-if="isEditing(state, 'stateNm', 'state')"
                          v-model="state.stateNm"
                          ref="editInput"
                          @blur="finishEdit(state)"
                          @keydown.enter="finishEdit(state)"
                          @keydown.esc="cancelEdit()"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none" />
                        <span v-else>{{ state.stateNm }}</span>
                      </td>
                      <td @dblclick="startEdit(state, 'stateType', 'state')" class="px-3 text-xs">
                        <select v-if="isEditing(state, 'stateType', 'state')"
                          v-model="state.stateType"
                          @change="finishEdit(state)"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none">
                          <option value="START">START</option>
                          <option value="NORMAL">NORMAL</option>
                          <option value="END">END</option>
                        </select>
                        <span v-else>{{ state.stateType }}</span>
                      </td>
                      <td @dblclick="startEdit(state, 'sortOrder', 'state')" class="px-3 text-xs">
                        <input v-if="isEditing(state, 'sortOrder', 'state')"
                          type="number" v-model.number="state.sortOrder"
                          @blur="finishEdit(state)"
                          @keydown.enter="finishEdit(state)"
                          @keydown.esc="cancelEdit()"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none" />
                        <span v-else>{{ state.sortOrder }}</span>
                      </td>
                      <td @dblclick="startEdit(state, 'entityStatusCd', 'state')" class="px-3 text-xs">
                        <select v-if="isEditing(state, 'entityStatusCd', 'state')"
                          v-model="state.entityStatusCd"
                          @change="finishEdit(state)"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none">
                          <option value="">미지정</option>
                          <option v-for="sc in entityStatusCodes" :key="sc.commonDtlCode" :value="sc.commonDtlCode">
                            {{ sc.commonDtlCode }}
                          </option>
                        </select>
                        <span v-else>{{ state.entityStatusCd }}</span>
                      </td>
                      <td class="px-3 text-xs text-center">
                        <button @click="handleDeleteState(state)" class="text-red-400 hover:text-red-600">X</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div class="flex justify-end mt-2">
                <button @click="handleAddState" class="h-7 px-3 bg-blue-600 text-white text-xs rounded hover:bg-blue-700">추가</button>
              </div>
            </div>

            <!-- 전이 정의 탭 -->
            <div v-if="activeTab === 'transitions'" class="bg-white rounded-b-lg shadow-sm border border-t-0 border-gray-200 p-3">
              <div class="overflow-hidden">
                <table class="w-full text-sm border-collapse">
                  <thead class="bg-gray-50 border-t border-b border-gray-300">
                    <tr>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">출발상태</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">도착상태</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">전이코드</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">이벤트명</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-20">정렬순서</th>
                      <th class="px-3 py-1.5 text-center text-xs font-medium text-gray-500 w-16">삭제</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-if="transitions.length === 0">
                      <td colspan="6" class="px-3 py-4 text-center text-xs text-gray-400">데이터가 없습니다.</td>
                    </tr>
                    <tr v-for="trans in transitions" :key="trans.transitionDefId || trans._tempId"
                      class="h-7 border-b border-gray-200">
                      <td @dblclick="startEdit(trans, 'fromStateDefId', 'transition')" class="px-3 text-xs">
                        <select v-if="isEditing(trans, 'fromStateDefId', 'transition')"
                          v-model="trans.fromStateDefId"
                          @change="finishEdit(trans)"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none">
                          <option v-for="s in states" :key="s.stateDefId" :value="s.stateDefId">{{ s.stateNm }}</option>
                        </select>
                        <span v-else>{{ getStateNm(trans.fromStateDefId) }}</span>
                      </td>
                      <td @dblclick="startEdit(trans, 'toStateDefId', 'transition')" class="px-3 text-xs">
                        <select v-if="isEditing(trans, 'toStateDefId', 'transition')"
                          v-model="trans.toStateDefId"
                          @change="finishEdit(trans)"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none">
                          <option v-for="s in states" :key="s.stateDefId" :value="s.stateDefId">{{ s.stateNm }}</option>
                        </select>
                        <span v-else>{{ getStateNm(trans.toStateDefId) }}</span>
                      </td>
                      <td @dblclick="startEdit(trans, 'transitionCode', 'transition')" class="px-3 text-xs">
                        <input v-if="isEditing(trans, 'transitionCode', 'transition')"
                          v-model="trans.transitionCode"
                          @blur="finishEdit(trans)"
                          @keydown.enter="finishEdit(trans)"
                          @keydown.esc="cancelEdit()"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none" />
                        <span v-else>{{ trans.transitionCode }}</span>
                      </td>
                      <td @dblclick="startEdit(trans, 'eventNm', 'transition')" class="px-3 text-xs">
                        <input v-if="isEditing(trans, 'eventNm', 'transition')"
                          v-model="trans.eventNm"
                          @blur="finishEdit(trans)"
                          @keydown.enter="finishEdit(trans)"
                          @keydown.esc="cancelEdit()"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none" />
                        <span v-else>{{ trans.eventNm }}</span>
                      </td>
                      <td @dblclick="startEdit(trans, 'sortOrder', 'transition')" class="px-3 text-xs">
                        <input v-if="isEditing(trans, 'sortOrder', 'transition')"
                          type="number" v-model.number="trans.sortOrder"
                          @blur="finishEdit(trans)"
                          @keydown.enter="finishEdit(trans)"
                          @keydown.esc="cancelEdit()"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none" />
                        <span v-else>{{ trans.sortOrder }}</span>
                      </td>
                      <td class="px-3 text-xs text-center">
                        <button @click="handleDeleteTransition(trans)" class="text-red-400 hover:text-red-600">X</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div class="flex justify-end mt-2">
                <button @click="handleAddTransition" class="h-7 px-3 bg-blue-600 text-white text-xs rounded hover:bg-blue-700">추가</button>
              </div>
            </div>

            <!-- Task 템플릿 탭 -->
            <div v-if="activeTab === 'tasks'" class="bg-white rounded-b-lg shadow-sm border border-t-0 border-gray-200 p-3">
              <div class="overflow-x-auto">
                <table class="w-full text-sm border-collapse">
                  <thead class="bg-gray-50 border-t border-b border-gray-300">
                    <tr>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">연결상태</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">Task명</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">Task유형</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">배정유형</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">배정대상</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-16">우선순위</th>
                      <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-16">SLA시간</th>
                      <th class="px-3 py-1.5 text-center text-xs font-medium text-gray-500 w-16">삭제</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-if="taskTemplates.length === 0">
                      <td colspan="8" class="px-3 py-4 text-center text-xs text-gray-400">데이터가 없습니다.</td>
                    </tr>
                    <tr v-for="task in taskTemplates" :key="task.taskTemplateId || task._tempId"
                      class="h-7 border-b border-gray-200">
                      <td @dblclick="startEdit(task, 'stateDefId', 'task')" class="px-3 text-xs">
                        <select v-if="isEditing(task, 'stateDefId', 'task')"
                          v-model="task.stateDefId"
                          @change="finishEdit(task)"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none">
                          <option v-for="s in states" :key="s.stateDefId" :value="s.stateDefId">{{ s.stateNm }}</option>
                        </select>
                        <span v-else>{{ getStateNm(task.stateDefId) }}</span>
                      </td>
                      <td @dblclick="startEdit(task, 'taskNm', 'task')" class="px-3 text-xs">
                        <input v-if="isEditing(task, 'taskNm', 'task')"
                          v-model="task.taskNm"
                          @blur="finishEdit(task)"
                          @keydown.enter="finishEdit(task)"
                          @keydown.esc="cancelEdit()"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none" />
                        <span v-else>{{ task.taskNm }}</span>
                      </td>
                      <td @dblclick="startEdit(task, 'taskType', 'task')" class="px-3 text-xs">
                        <select v-if="isEditing(task, 'taskType', 'task')"
                          v-model="task.taskType"
                          @change="finishEdit(task)"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none">
                          <option value="REVIEW">REVIEW</option>
                          <option value="APPROVAL">APPROVAL</option>
                          <option value="DATA_ENTRY">DATA_ENTRY</option>
                        </select>
                        <span v-else>{{ task.taskType }}</span>
                      </td>
                      <td @dblclick="startEdit(task, 'assigneeType', 'task')" class="px-3 text-xs">
                        <select v-if="isEditing(task, 'assigneeType', 'task')"
                          v-model="task.assigneeType"
                          @change="finishEdit(task)"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none">
                          <option value="ROLE">ROLE</option>
                          <option value="USER">USER</option>
                          <option value="FIELD">FIELD</option>
                        </select>
                        <span v-else>{{ task.assigneeType }}</span>
                      </td>
                      <td @dblclick="startEdit(task, 'assigneeValue', 'task')" class="px-3 text-xs">
                        <input v-if="isEditing(task, 'assigneeValue', 'task')"
                          v-model="task.assigneeValue"
                          @blur="finishEdit(task)"
                          @keydown.enter="finishEdit(task)"
                          @keydown.esc="cancelEdit()"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none" />
                        <span v-else>{{ task.assigneeValue }}</span>
                      </td>
                      <td @dblclick="startEdit(task, 'priority', 'task')" class="px-3 text-xs">
                        <input v-if="isEditing(task, 'priority', 'task')"
                          type="number" v-model.number="task.priority"
                          @blur="finishEdit(task)"
                          @keydown.enter="finishEdit(task)"
                          @keydown.esc="cancelEdit()"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none" />
                        <span v-else>{{ task.priority }}</span>
                      </td>
                      <td @dblclick="startEdit(task, 'slaHours', 'task')" class="px-3 text-xs">
                        <input v-if="isEditing(task, 'slaHours', 'task')"
                          type="number" v-model.number="task.slaHours"
                          @blur="finishEdit(task)"
                          @keydown.enter="finishEdit(task)"
                          @keydown.esc="cancelEdit()"
                          class="w-full h-6 border border-blue-400 rounded px-1 text-xs focus:outline-none" />
                        <span v-else>{{ task.slaHours }}</span>
                      </td>
                      <td class="px-3 text-xs text-center">
                        <button @click="handleDeleteTask(task)" class="text-red-400 hover:text-red-600">X</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div class="flex justify-end mt-2">
                <button @click="handleAddTask" class="h-7 px-3 bg-blue-600 text-white text-xs rounded hover:bg-blue-700">추가</button>
              </div>
            </div>
          </div>

          <!-- 미선택 안내 -->
          <div v-else-if="!isNew" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4 flex items-center justify-center h-48">
            <p class="text-sm text-gray-400">좌측 목록에서 프로세스를 선택하거나 추가해주세요.</p>
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar v-if="selectedProcess || isNew">
      <button v-if="selectedProcess && !isNew" @click="confirmOpen = true"
        class="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 text-sm rounded transition-colors">삭제</button>
      <button @click="handleCancel"
        class="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 transition-colors">취소</button>
      <button @click="handleSave"
        class="h-8 px-6 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded transition-colors">저장</button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="confirmOpen"
      :message="confirmMessage"
      confirm-text="삭제"
      confirm-type="danger"
      @confirm="handleDeleteConfirm"
      @cancel="confirmOpen = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useAuthStore } from '../stores/auth'
import { wfApi } from '../api/wfProcessDefApi'
import { wfEntityTypeApi } from '../api/wfEntityTypeApi'
import { commonCodeApi } from '../api/commonCodeApi'
import Toast from '../components/common/Toast.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'

const auth = useAuthStore()

// --- 상태 ---
const processDefs = ref([])
const selectedProcess = ref(null)
const isNew = ref(false)

const EMPTY_PROCESS_FORM = {
  processNm: '', entityType: '', processDesc: '', useYn: 'Y'
}
const processForm = reactive({ ...EMPTY_PROCESS_FORM })

const activeTab = ref('states')

// 엔티티유형 메타
const entityTypes = ref([])
const entityStatusCodes = ref([])

// 공유 상태 데이터
const states = ref([])
const transitions = ref([])
const taskTemplates = ref([])

// 인라인 편집 상태
const editingCell = reactive({ rowId: null, field: null, type: null })
const editInput = ref(null)

// 메시지 / 다이얼로그
const successMsg = ref('')
const errorMsg = ref('')
const confirmOpen = ref(false)
const confirmMessage = ref('')
let pendingDeleteAction = null

const clearMessages = () => { successMsg.value = ''; errorMsg.value = '' }

// --- 유틸 ---
let tempIdCounter = 0
const genTempId = () => `_temp_${++tempIdCounter}_${Date.now()}`

const getRowId = (row) => row.stateDefId || row.transitionDefId || row.taskTemplateId || row._tempId

const getStateNm = (stateDefId) => {
  const s = states.value.find(st => st.stateDefId === stateDefId)
  return s ? s.stateNm : stateDefId
}

const getEntityTypeNm = (entityTypeCd) => {
  const et = entityTypes.value.find(e => e.entityTypeCd === entityTypeCd)
  return et ? et.entityTypeNm : entityTypeCd
}

// --- 인라인 편집 ---
const isEditing = (row, field, type) =>
  editingCell.rowId === getRowId(row) && editingCell.field === field && editingCell.type === type

const startEdit = (row, field, type) => {
  editingCell.rowId = getRowId(row)
  editingCell.field = field
  editingCell.type = type
  nextTick(() => {
    const el = Array.isArray(editInput.value) ? editInput.value[0] : editInput.value
    el?.focus()
  })
}

const finishEdit = (row) => {
  editingCell.rowId = null
  editingCell.field = null
  editingCell.type = null
  row._dirty = true
}

const cancelEdit = () => {
  editingCell.rowId = null
  editingCell.field = null
  editingCell.type = null
}

// --- 프로세스 CRUD ---
const fetchProcessDefs = async () => {
  try {
    processDefs.value = await wfApi.getProcessDefs()
  } catch {
    errorMsg.value = '프로세스 목록 조회에 실패했습니다.'
  }
}

const handleProcessSelect = async (proc) => {
  clearMessages()
  isNew.value = false
  try {
    const detail = await wfApi.getProcessDef(proc.processDefId)
    selectedProcess.value = detail
    Object.assign(processForm, {
      processNm: detail.processNm || '',
      entityType: detail.entityType || '',
      processDesc: detail.processDesc || '',
      useYn: detail.useYn || 'Y'
    })
    // 엔티티 상태코드 목록 로드
    await loadEntityStatusCodes(detail.entityType)
    // 상태/전이 로드
    await refreshStates()
    await refreshTransitions()
    await refreshTaskTemplates()
    activeTab.value = 'states'
  } catch (err) {
    const status = err?.response?.status
    if (status === 404) {
      errorMsg.value = '대상을 찾을 수 없습니다. 목록을 갱신합니다.'
      await fetchProcessDefs()
    } else {
      errorMsg.value = '프로세스 조회에 실패했습니다.'
    }
  }
}

const handleAddProcess = () => {
  clearMessages()
  handleCancel()
  isNew.value = true
}

const handleCancel = () => {
  isNew.value = false
  selectedProcess.value = null
  Object.assign(processForm, { ...EMPTY_PROCESS_FORM })
  states.value = []
  transitions.value = []
  taskTemplates.value = []
}

const handleSave = async () => {
  clearMessages()
  if (!processForm.processNm.trim()) {
    errorMsg.value = '프로세스명은 필수입니다.'
    return
  }
  if (!processForm.entityType.trim()) {
    errorMsg.value = '엔티티유형은 필수입니다.'
    return
  }

  const createdBy = auth.user?.userId ?? 'SYSTEM'

  try {
    // 프로세스 기본정보 저장
    if (isNew.value) {
      const created = await wfApi.createProcessDef({ ...processForm, createdBy })
      isNew.value = false
      selectedProcess.value = created
      await fetchProcessDefs()
      successMsg.value = '프로세스가 등록되었습니다.'
      // 상태/전이/Task 초기화
      await refreshStates()
      await refreshTransitions()
      await refreshTaskTemplates()
    } else if (selectedProcess.value) {
      await wfApi.updateProcessDef(selectedProcess.value.processDefId, { ...processForm, createdBy })
      await fetchProcessDefs()

      // 활성 탭의 변경 행 일괄 저장
      await saveActiveTabRows(createdBy)

      // 상세 재조회
      const detail = await wfApi.getProcessDef(selectedProcess.value.processDefId)
      selectedProcess.value = detail
      successMsg.value = '저장되었습니다.'
    }
  } catch (err) {
    const status = err?.response?.status
    if (status === 409) {
      errorMsg.value = '충돌이 발생했습니다. 다시 시도해주세요.'
    } else if (status === 404) {
      errorMsg.value = '대상을 찾을 수 없습니다. 목록을 갱신합니다.'
      await fetchProcessDefs()
    } else if (status === 400) {
      errorMsg.value = err?.response?.data?.message || '입력값을 확인해 주세요.'
    } else {
      errorMsg.value = '처리에 실패했습니다.'
    }
  }
}

const saveActiveTabRows = async (createdBy) => {
  if (activeTab.value === 'states') {
    await saveStateRows(createdBy)
  } else if (activeTab.value === 'transitions') {
    await saveTransitionRows(createdBy)
  } else if (activeTab.value === 'tasks') {
    await saveTaskRows(createdBy)
  }
}

const loadEntityStatusCodes = async (entityTypeCd) => {
  entityStatusCodes.value = []
  const et = entityTypes.value.find(e => e.entityTypeCd === entityTypeCd)
  if (!et?.statusCdGroup) return
  try {
    entityStatusCodes.value = await commonCodeApi.getEffectiveDetails(et.statusCdGroup)
  } catch {
    // 실패 시 빈 배열 유지 — 드롭다운 비활성화 상태
  }
}

// --- 상태 정의 ---
const refreshStates = async () => {
  if (!selectedProcess.value) { states.value = []; return }
  try {
    states.value = await wfApi.getStates(selectedProcess.value.processDefId)
  } catch {
    states.value = []
  }
}

const handleAddState = () => {
  states.value.push({
    _tempId: genTempId(),
    _isNew: true,
    _dirty: false,
    stateNm: '',
    stateType: 'NORMAL',
    sortOrder: states.value.length + 1,
    entityStatusCd: ''
  })
}

const saveStateRows = async (createdBy) => {
  const pid = selectedProcess.value.processDefId
  for (const row of states.value) {
    try {
      if (row._isNew) {
        await wfApi.createState(pid, {
          stateNm: row.stateNm,
          stateType: row.stateType,
          sortOrder: row.sortOrder,
          entityStatusCd: row.entityStatusCd || null,
          createdBy
        })
      } else if (row._dirty) {
        await wfApi.updateState(row.stateDefId, {
          stateNm: row.stateNm,
          stateType: row.stateType,
          sortOrder: row.sortOrder,
          entityStatusCd: row.entityStatusCd || null,
          createdBy
        })
      }
    } catch (err) {
      throw err
    }
  }
  await refreshStates()
}

const handleDeleteState = (state) => {
  if (state._isNew) {
    states.value = states.value.filter(s => s._tempId !== state._tempId)
    return
  }
  confirmMessage.value = '상태를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.'
  pendingDeleteAction = async () => {
    try {
      await wfApi.deleteState(state.stateDefId)
      successMsg.value = '상태가 삭제되었습니다.'
      await refreshStates()
      await refreshTransitions()
      await refreshTaskTemplates()
    } catch (err) {
      const status = err?.response?.status
      if (status === 409) {
        errorMsg.value = '참조 중인 상태는 삭제할 수 없습니다.'
      } else if (status === 404) {
        errorMsg.value = '대상을 찾을 수 없습니다. 목록을 갱신합니다.'
        await refreshStates()
      } else {
        errorMsg.value = '삭제에 실패했습니다.'
      }
    }
  }
  confirmOpen.value = true
}

// --- 전이 정의 ---
const refreshTransitions = async () => {
  if (!selectedProcess.value) { transitions.value = []; return }
  try {
    transitions.value = await wfApi.getTransitions(selectedProcess.value.processDefId)
  } catch {
    transitions.value = []
  }
}

const handleAddTransition = () => {
  transitions.value.push({
    _tempId: genTempId(),
    _isNew: true,
    _dirty: false,
    fromStateDefId: states.value.length > 0 ? states.value[0].stateDefId : null,
    toStateDefId: states.value.length > 1 ? states.value[1].stateDefId : (states.value.length > 0 ? states.value[0].stateDefId : null),
    transitionCode: '',
    eventNm: '',
    sortOrder: transitions.value.length + 1
  })
}

const saveTransitionRows = async (createdBy) => {
  const pid = selectedProcess.value.processDefId
  for (const row of transitions.value) {
    try {
      if (row._isNew) {
        await wfApi.createTransition(pid, {
          fromStateDefId: row.fromStateDefId,
          toStateDefId: row.toStateDefId,
          transitionCode: row.transitionCode,
          eventNm: row.eventNm,
          sortOrder: row.sortOrder,
          createdBy
        })
      } else if (row._dirty) {
        await wfApi.updateTransition(row.transitionDefId, {
          fromStateDefId: row.fromStateDefId,
          toStateDefId: row.toStateDefId,
          transitionCode: row.transitionCode,
          eventNm: row.eventNm,
          sortOrder: row.sortOrder,
          createdBy
        })
      }
    } catch (err) {
      throw err
    }
  }
  await refreshTransitions()
}

const handleDeleteTransition = (trans) => {
  if (trans._isNew) {
    transitions.value = transitions.value.filter(t => t._tempId !== trans._tempId)
    return
  }
  confirmMessage.value = '전이를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.'
  pendingDeleteAction = async () => {
    try {
      await wfApi.deleteTransition(trans.transitionDefId)
      successMsg.value = '전이가 삭제되었습니다.'
      await refreshTransitions()
    } catch (err) {
      const status = err?.response?.status
      if (status === 409) {
        errorMsg.value = '참조 중인 전이는 삭제할 수 없습니다.'
      } else if (status === 404) {
        errorMsg.value = '대상을 찾을 수 없습니다. 목록을 갱신합니다.'
        await refreshTransitions()
      } else {
        errorMsg.value = '삭제에 실패했습니다.'
      }
    }
  }
  confirmOpen.value = true
}

// --- Task 템플릿 ---
const refreshTaskTemplates = async () => {
  if (!selectedProcess.value || states.value.length === 0) { taskTemplates.value = []; return }
  const promises = states.value
    .filter(s => s.stateDefId)
    .map(s => wfApi.getTaskTemplates(s.stateDefId).catch(() => []))
  const results = await Promise.all(promises)
  taskTemplates.value = results.flat()
}

const handleAddTask = () => {
  taskTemplates.value.push({
    _tempId: genTempId(),
    _isNew: true,
    _dirty: false,
    stateDefId: states.value.length > 0 ? states.value[0].stateDefId : null,
    taskNm: '',
    taskType: 'REVIEW',
    assigneeType: 'ROLE',
    assigneeValue: '',
    priority: 1,
    slaHours: null
  })
}

const saveTaskRows = async (createdBy) => {
  for (const row of taskTemplates.value) {
    try {
      if (row._isNew) {
        await wfApi.createTaskTemplate(row.stateDefId, {
          taskNm: row.taskNm,
          taskType: row.taskType,
          assigneeType: row.assigneeType,
          assigneeValue: row.assigneeValue,
          priority: row.priority,
          slaHours: row.slaHours,
          createdBy
        })
      } else if (row._dirty) {
        await wfApi.updateTaskTemplate(row.taskTemplateId, {
          taskNm: row.taskNm,
          taskType: row.taskType,
          assigneeType: row.assigneeType,
          assigneeValue: row.assigneeValue,
          priority: row.priority,
          slaHours: row.slaHours,
          createdBy
        })
      }
    } catch (err) {
      throw err
    }
  }
  await refreshTaskTemplates()
}

const handleDeleteTask = (task) => {
  if (task._isNew) {
    taskTemplates.value = taskTemplates.value.filter(t => t._tempId !== task._tempId)
    return
  }
  confirmMessage.value = 'Task 템플릿을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.'
  pendingDeleteAction = async () => {
    try {
      await wfApi.deleteTaskTemplate(task.taskTemplateId)
      successMsg.value = 'Task 템플릿이 삭제되었습니다.'
      await refreshTaskTemplates()
    } catch (err) {
      const status = err?.response?.status
      if (status === 409) {
        errorMsg.value = '참조 중인 Task 템플릿은 삭제할 수 없습니다.'
      } else if (status === 404) {
        errorMsg.value = '대상을 찾을 수 없습니다. 목록을 갱신합니다.'
        await refreshTaskTemplates()
      } else {
        errorMsg.value = '삭제에 실패했습니다.'
      }
    }
  }
  confirmOpen.value = true
}

// --- 프로세스 삭제 ---
const handleDeleteConfirm = async () => {
  confirmOpen.value = false
  clearMessages()
  if (pendingDeleteAction) {
    await pendingDeleteAction()
    pendingDeleteAction = null
    return
  }
  // 프로세스 삭제
  try {
    await wfApi.deleteProcessDef(selectedProcess.value.processDefId)
    successMsg.value = '프로세스가 삭제되었습니다.'
    handleCancel()
    await fetchProcessDefs()
  } catch (err) {
    const status = err?.response?.status
    if (status === 409) {
      errorMsg.value = '하위 정의가 존재하여 삭제할 수 없습니다.'
    } else if (status === 404) {
      errorMsg.value = '대상을 찾을 수 없습니다. 목록을 갱신합니다.'
      await fetchProcessDefs()
    } else {
      errorMsg.value = '삭제에 실패했습니다.'
    }
  }
}

// --- 초기 로드 ---
onMounted(async () => {
  try {
    entityTypes.value = await wfEntityTypeApi.getAll()
  } catch {
    errorMsg.value = '엔티티유형 목록 조회에 실패했습니다.'
  }
  await fetchProcessDefs()
})
</script>
