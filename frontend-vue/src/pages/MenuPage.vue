<template>
  <MainLayout>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">메뉴관리</h1>

      <div class="flex gap-4">
        <!-- 좌측: 메뉴 트리뷰 -->
        <div class="w-2/5 space-y-2">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-3">
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-semibold text-gray-500 uppercase tracking-wide">메뉴 트리</span>
              <button @click="handleAddRoot"
                class="h-7 px-3 bg-blue-600 text-white text-xs rounded hover:bg-blue-700">최상위 메뉴 추가</button>
            </div>
            <div class="max-h-[40rem] overflow-y-auto border border-gray-200 rounded">
              <div v-if="treeData.length === 0" class="px-3 py-4 text-center text-xs text-gray-400">
                메뉴 데이터가 없습니다.
              </div>
              <tree-node
                v-for="(node, idx) in treeData"
                :key="node.menuId"
                :node="node"
                :depth="0"
                :selected-id="selectedMenuId"
                :is-first="idx === 0"
                :is-last="idx === treeData.length - 1"
                @select="handleNodeSelect"
                @add-child="handleAddChild"
                @move-up="handleMoveUp"
                @move-down="handleMoveDown"
              />
            </div>
          </div>

          <!-- 미연결 화면 목록 -->
          <div v-if="unlinkedPages.length > 0" class="bg-white rounded-lg shadow-sm border border-gray-200 p-3 mt-2">
            <span class="text-xs font-semibold text-gray-500 uppercase tracking-wide">미연결 화면 ({{ unlinkedPages.length }})</span>
            <div class="mt-2 space-y-1">
              <div v-for="page in unlinkedPages" :key="page.path"
                @click="handleUnlinkedPageClick(page)"
                class="flex items-center justify-between px-2 py-1.5 text-sm rounded cursor-pointer hover:bg-blue-50 text-gray-600 hover:text-blue-600 transition-colors">
                <span>{{ page.label }}</span>
                <span class="text-xs text-gray-400">{{ page.path }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 우측: 편집 폼 -->
        <div class="w-3/5">
          <div v-if="mode !== 'view'" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4 space-y-4">
            <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide">
              {{ mode === 'new' ? '메뉴 등록' : '메뉴 수정' }}
            </h3>
            <div class="grid grid-cols-3 gap-x-4 gap-y-3">
              <div>
                <label class="block text-xs text-gray-500 mb-1">메뉴ID</label>
                <input :value="form.menuId || '(자동생성)'" readonly
                  class="w-full h-8 border border-gray-200 rounded px-2 text-sm bg-gray-50 text-gray-400" />
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">메뉴명 <span class="text-blue-400">*</span></label>
                <input v-model="form.menuNm"
                  class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">메뉴 URL</label>
                <input v-model="form.menuUrl" placeholder="/example"
                  class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">상위메뉴</label>
                <input :value="parentMenuNm" readonly
                  class="w-full h-8 border border-gray-200 rounded px-2 text-sm bg-gray-50 text-gray-400" />
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">사용여부</label>
                <select v-model="form.useYn"
                  class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400">
                  <option value="Y">사용</option>
                  <option value="N">미사용</option>
                </select>
              </div>
            </div>

            <!-- 역할 할당 -->
            <div>
              <label class="block text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2">역할 할당</label>
              <div class="flex flex-wrap gap-3">
                <label v-for="role in availableRoles" :key="role" class="flex items-center gap-1.5 text-sm text-gray-700">
                  <input type="checkbox" :value="role" v-model="form.roleCds"
                    class="rounded border-gray-300 text-blue-600 focus:ring-blue-500" />
                  {{ role }}
                </label>
                <span v-if="availableRoles.length === 0" class="text-xs text-gray-400">역할 코드가 없습니다.</span>
              </div>
            </div>
          </div>

          <div v-else class="bg-white rounded-lg shadow-sm border border-gray-200 p-4 flex items-center justify-center h-48">
            <p class="text-sm text-gray-400">좌측 트리에서 메뉴를 선택하거나, 메뉴를 추가해주세요.</p>
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar v-if="mode !== 'view'">
      <button v-if="mode === 'edit'" @click="confirmOpen = true"
        class="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 text-sm rounded transition-colors">삭제</button>
      <button @click="handleCancel"
        class="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 transition-colors">취소</button>
      <button @click="handleSave"
        class="h-8 px-6 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded transition-colors">저장</button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="confirmOpen"
      message="메뉴를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
      confirm-text="삭제"
      confirm-type="danger"
      @confirm="handleDeleteConfirm"
      @cancel="confirmOpen = false"
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { menuApi } from '../api/menuApi'
import { commonCodeApi } from '../api/commonCodeApi'
import { useMenuStore } from '../stores/menu'
import MainLayout from '../components/common/MainLayout.vue'
import Toast from '../components/common/Toast.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'
import TreeNode from '../components/menu/TreeNode.vue'

const router = useRouter()
const menuStore = useMenuStore()

// 라우터에서 메뉴 연결 대상 화면 자동 추출 (meta.label이 있는 라우트만)
const ALL_PAGES = router.getRoutes()
  .filter(r => r.meta?.label && !r.meta?.hidden)
  .map(r => ({ path: r.path, label: r.meta.label }))

const EMPTY_FORM = {
  menuId: '', menuNm: '', menuUrl: '', parentMenuId: null,
  sortOrder: 0, useYn: 'Y', menuLevel: 1, roleCds: []
}

const treeData = ref([])
const mode = ref('view')
const form = reactive({ ...EMPTY_FORM })
const selectedMenuId = ref(null)
const confirmOpen = ref(false)
const saveConfirmOpen = ref(false)
const saveConfirmMessage = ref('')
const successMsg = ref('')
const errorMsg = ref('')
const availableRoles = ref([])

const clearMessages = () => { successMsg.value = ''; errorMsg.value = '' }

// 트리에서 모든 menuUrl 수집
function collectMenuUrls(nodes) {
  const urls = []
  for (const node of nodes) {
    if (node.menuUrl) urls.push(node.menuUrl)
    if (node.children?.length) urls.push(...collectMenuUrls(node.children))
  }
  return urls
}

const unlinkedPages = computed(() => {
  const usedUrls = new Set(collectMenuUrls(treeData.value))
  return ALL_PAGES.filter(p => !usedUrls.has(p.path))
})

const parentMenuNm = computed(() => {
  if (!form.parentMenuId) return '(최상위)'
  const found = findNodeById(treeData.value, form.parentMenuId)
  return found ? found.menuNm : form.parentMenuId
})

function findNodeById(nodes, id) {
  for (const node of nodes) {
    if (node.menuId === id) return node
    if (node.children && node.children.length > 0) {
      const found = findNodeById(node.children, id)
      if (found) return found
    }
  }
  return null
}

const fetchTree = async () => {
  try {
    treeData.value = await menuApi.getTree()
  } catch {
    errorMsg.value = '메뉴 트리 조회에 실패했습니다.'
  }
}

const fetchRoles = async () => {
  try {
    const details = await commonCodeApi.getDetails('ROLE')
    availableRoles.value = details.map(d => d.commonDtlCode)
  } catch {
    availableRoles.value = ['ADMIN', 'USER']
  }
}

onMounted(async () => {
  await Promise.all([fetchTree(), fetchRoles()])
})

const handleNodeSelect = async (node) => {
  clearMessages()
  selectedMenuId.value = node.menuId
  try {
    const detail = await menuApi.get(node.menuId)
    Object.assign(form, {
      menuId: detail.menuId,
      menuNm: detail.menuNm,
      menuUrl: detail.menuUrl || '',
      parentMenuId: detail.parentMenuId,
      sortOrder: detail.sortOrder,
      useYn: detail.useYn,
      menuLevel: detail.menuLevel,
      roleCds: detail.roleCds || []
    })
    mode.value = 'edit'
  } catch {
    errorMsg.value = '메뉴 정보 조회에 실패했습니다.'
  }
}

const handleAddRoot = () => {
  clearMessages()
  selectedMenuId.value = null
  Object.assign(form, { ...EMPTY_FORM, parentMenuId: null, menuLevel: 1 })
  mode.value = 'new'
}

const handleAddChild = (parentNode) => {
  clearMessages()
  selectedMenuId.value = null
  Object.assign(form, {
    ...EMPTY_FORM,
    parentMenuId: parentNode.menuId,
    menuLevel: parentNode.menuLevel + 1
  })
  mode.value = 'new'
}

const handleSave = () => {
  clearMessages()
  if (!form.menuNm.trim()) {
    errorMsg.value = '메뉴명은 필수입니다.'
    return
  }
  saveConfirmMessage.value = mode.value === 'new' ? '메뉴를 등록하시겠습니까?' : '메뉴를 수정하시겠습니까?'
  saveConfirmOpen.value = true
}

const handleSaveConfirm = async () => {
  saveConfirmOpen.value = false
  try {
    const payload = {
      menuNm: form.menuNm,
      menuUrl: form.menuUrl || null,
      parentMenuId: form.parentMenuId || null,
      useYn: form.useYn,
      menuLevel: form.menuLevel,
      roleCds: form.roleCds
    }
    if (mode.value === 'new') {
      await menuApi.create(payload)
      successMsg.value = '메뉴가 등록되었습니다.'
    } else {
      await menuApi.update(form.menuId, payload)
      successMsg.value = '메뉴가 수정되었습니다.'
    }
    mode.value = 'view'
    selectedMenuId.value = null
    Object.assign(form, EMPTY_FORM)
    await fetchTree()
    await menuStore.fetchMyMenu()
  } catch (err) {
    const status = err?.response?.status
    if (status === 400) {
      errorMsg.value = err?.response?.data?.message || '입력값을 확인해 주세요.'
    } else {
      errorMsg.value = '저장에 실패했습니다.'
    }
  }
}

const handleDeleteConfirm = async () => {
  confirmOpen.value = false
  clearMessages()
  try {
    await menuApi.delete(form.menuId)
    successMsg.value = '메뉴가 삭제되었습니다.'
    mode.value = 'view'
    selectedMenuId.value = null
    Object.assign(form, EMPTY_FORM)
    await fetchTree()
    await menuStore.fetchMyMenu()
  } catch (err) {
    const status = err?.response?.status
    if (status === 409) {
      errorMsg.value = '하위 메뉴가 존재하여 삭제할 수 없습니다.'
    } else {
      errorMsg.value = '삭제에 실패했습니다.'
    }
  }
}

const handleMoveUp = async (node) => {
  clearMessages()
  try {
    await menuApi.moveUp(node.menuId)
    await fetchTree()
    await menuStore.fetchMyMenu()
    successMsg.value = '메뉴 순서가 변경되었습니다.'
  } catch {
    errorMsg.value = '이동에 실패했습니다.'
  }
}

const handleMoveDown = async (node) => {
  clearMessages()
  try {
    await menuApi.moveDown(node.menuId)
    await fetchTree()
    await menuStore.fetchMyMenu()
    successMsg.value = '메뉴 순서가 변경되었습니다.'
  } catch {
    errorMsg.value = '이동에 실패했습니다.'
  }
}

const handleUnlinkedPageClick = (page) => {
  clearMessages()
  selectedMenuId.value = null
  Object.assign(form, { ...EMPTY_FORM, menuNm: page.label, menuUrl: page.path, parentMenuId: null, menuLevel: 1 })
  mode.value = 'new'
}

const handleCancel = () => {
  mode.value = 'view'
  selectedMenuId.value = null
  Object.assign(form, EMPTY_FORM)
}
</script>
