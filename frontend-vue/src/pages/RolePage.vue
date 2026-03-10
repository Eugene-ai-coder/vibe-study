<template>
  <MainLayout>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">권한관리</h1>

      <div class="flex gap-4">
        <!-- 좌측: 역할 목록 -->
        <div class="w-2/5 space-y-2">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-3">
            <span class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2 block">역할 목록</span>
            <div class="border border-gray-200 rounded overflow-hidden">
              <div v-if="roles.length === 0" class="px-3 py-4 text-center text-xs text-gray-400">
                역할 데이터가 없습니다.
              </div>
              <div
                v-for="role in roles"
                :key="role.id.commonDtlCode"
                :class="[
                  'flex items-center px-3 cursor-pointer transition-colors',
                  selectedRoleCd === role.id.commonDtlCode
                    ? 'bg-blue-50 text-blue-900'
                    : 'hover:bg-gray-50 text-gray-800'
                ]"
                style="height: 36px;"
                @click="handleRoleSelect(role.id.commonDtlCode)"
              >
                <span class="text-sm font-medium">{{ role.id.commonDtlCode }}</span>
                <span class="text-xs text-gray-400 ml-2">{{ role.commonDtlCodeNm }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 우측: 사용자 할당 -->
        <div class="w-3/5">
          <div v-if="selectedRoleCd" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4 space-y-3">
            <div class="flex items-center justify-between">
              <span class="text-xs font-semibold text-gray-500 uppercase tracking-wide">
                {{ selectedRoleCd }} 역할 — 할당된 사용자
              </span>
              <button @click="showAddPopup = true"
                class="h-7 px-3 bg-blue-600 text-white text-xs rounded hover:bg-blue-700">사용자 추가</button>
            </div>
            <DataGrid
              :columns="assignedColumns"
              :data="assignedUsers"
              row-id-accessor="userId"
              :selected-row-id="selectedUserId"
              storage-key="roleAssignedUsers"
              title="할당된 사용자"
              empty-message="할당된 사용자가 없습니다."
              @rowClick="row => selectedUserId = row.userId"
            />
          </div>

          <div v-else class="bg-white rounded-lg shadow-sm border border-gray-200 p-4 flex items-center justify-center h-48">
            <p class="text-sm text-gray-400">좌측에서 역할을 선택해주세요.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 하단 액션바 -->
    <FloatingActionBar v-if="selectedRoleCd && selectedUserId">
      <button @click="confirmOpen = true"
        class="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 text-sm rounded transition-colors">선택 사용자 제거</button>
    </FloatingActionBar>

    <!-- 사용자 추가 팝업 -->
    <div v-if="showAddPopup" class="fixed inset-0 bg-black/30 flex items-center justify-center z-50" @click.self="showAddPopup = false">
      <div class="bg-white rounded-lg shadow-xl border border-gray-200 w-[480px] max-h-[70vh] flex flex-col">
        <div class="flex items-center justify-between px-4 py-3 border-b border-gray-200">
          <span class="text-sm font-semibold text-gray-800">사용자 추가</span>
          <button @click="showAddPopup = false" class="text-gray-400 hover:text-gray-600 text-lg">&times;</button>
        </div>
        <div class="p-4 overflow-y-auto flex-1">
          <div v-if="availableUsers.length === 0" class="text-center text-sm text-gray-400 py-8">
            추가할 수 있는 사용자가 없습니다.
          </div>
          <div v-else class="space-y-1">
            <label
              v-for="user in availableUsers"
              :key="user.userId"
              class="flex items-center gap-2 px-2 py-1.5 rounded hover:bg-gray-50 cursor-pointer"
            >
              <input type="checkbox" :value="user.userId" v-model="selectedAddUserIds"
                class="rounded border-gray-300 text-blue-600 focus:ring-blue-500" />
              <span class="text-sm text-gray-800">{{ user.userId }}</span>
              <span class="text-xs text-gray-400">{{ user.nickname }}</span>
            </label>
          </div>
        </div>
        <div class="flex justify-end gap-2 px-4 py-3 border-t border-gray-200">
          <button @click="showAddPopup = false"
            class="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50">취소</button>
          <button @click="handleAddUsers" :disabled="selectedAddUserIds.length === 0"
            class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-40">추가</button>
        </div>
      </div>
    </div>

    <!-- 제거 확인 -->
    <ConfirmDialog
      v-if="confirmOpen"
      message="선택한 사용자의 역할을 제거하시겠습니까?"
      confirm-text="제거"
      confirm-type="danger"
      @confirm="handleRemoveUser"
      @cancel="confirmOpen = false"
    />

    <ConfirmDialog
      v-if="addConfirmOpen"
      :message="`${selectedAddUserIds.length}명의 사용자를 추가하시겠습니까?`"
      confirm-text="추가"
      confirm-type="primary"
      @confirm="handleAddUsersConfirm"
      @cancel="addConfirmOpen = false"
    />
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { roleApi } from '../api/roleApi'
import MainLayout from '../components/common/MainLayout.vue'
import Toast from '../components/common/Toast.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'
import DataGrid from '../components/common/DataGrid.vue'

const roles = ref([])
const selectedRoleCd = ref(null)
const assignedUsers = ref([])
const availableUsers = ref([])
const selectedUserId = ref(null)
const selectedAddUserIds = ref([])
const showAddPopup = ref(false)
const confirmOpen = ref(false)
const addConfirmOpen = ref(false)
const successMsg = ref('')
const errorMsg = ref('')

const assignedColumns = [
  { accessorKey: 'userId', header: '사용자ID', width: 140 },
  { accessorKey: 'nickname', header: '닉네임', width: 140 },
  { accessorKey: 'accountStatusLabel', header: '계정상태', width: 100 },
]

const clearMessages = () => { successMsg.value = ''; errorMsg.value = '' }

const fetchRoles = async () => {
  try {
    roles.value = await roleApi.getRoles()
  } catch {
    errorMsg.value = '역할 목록 조회에 실패했습니다.'
  }
}

const fetchAssignedUsers = async (roleCd) => {
  try {
    const users = await roleApi.getUsersByRole(roleCd)
    assignedUsers.value = users.map(u => ({
      ...u,
      accountStatusLabel: u.accountStatus === 1 ? '활성' : '비활성'
    }))
  } catch {
    errorMsg.value = '사용자 목록 조회에 실패했습니다.'
  }
}

const fetchAvailableUsers = async (roleCd) => {
  try {
    availableUsers.value = await roleApi.getAvailableUsers(roleCd)
  } catch {
    errorMsg.value = '미할당 사용자 조회에 실패했습니다.'
  }
}

onMounted(async () => {
  await fetchRoles()
})

const handleRoleSelect = async (roleCd) => {
  clearMessages()
  selectedRoleCd.value = roleCd
  selectedUserId.value = null
  await fetchAssignedUsers(roleCd)
}

const handleAddUsers = () => {
  clearMessages()
  addConfirmOpen.value = true
}

const handleAddUsersConfirm = async () => {
  addConfirmOpen.value = false
  try {
    const currentIds = assignedUsers.value.map(u => u.userId)
    const newIds = [...currentIds, ...selectedAddUserIds.value]
    await roleApi.saveRoleUsers(selectedRoleCd.value, newIds)
    successMsg.value = '사용자가 추가되었습니다.'
    showAddPopup.value = false
    selectedAddUserIds.value = []
    await fetchAssignedUsers(selectedRoleCd.value)
  } catch {
    errorMsg.value = '사용자 추가에 실패했습니다.'
  }
}

const handleRemoveUser = async () => {
  confirmOpen.value = false
  clearMessages()
  try {
    const remainingIds = assignedUsers.value
      .filter(u => u.userId !== selectedUserId.value)
      .map(u => u.userId)
    await roleApi.saveRoleUsers(selectedRoleCd.value, remainingIds)
    successMsg.value = '사용자가 제거되었습니다.'
    selectedUserId.value = null
    await fetchAssignedUsers(selectedRoleCd.value)
  } catch {
    errorMsg.value = '사용자 제거에 실패했습니다.'
  }
}

// 팝업 열 때 미할당 사용자 조회
import { watch } from 'vue'
watch(showAddPopup, async (val) => {
  if (val && selectedRoleCd.value) {
    selectedAddUserIds.value = []
    await fetchAvailableUsers(selectedRoleCd.value)
  }
})
</script>
