<template>
  <div>
    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">가입 관리</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색유형</label>
            <select v-model="searchType" class="h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 bg-white">
              <option value="SUBS_ID">가입ID</option>
              <option value="SUBS_NM">가입명</option>
              <option value="SVC_CD">서비스</option>
              <option value="BASIC_PROD_CD">기본상품코드</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색어</label>
            <input v-model="keyword" @keydown.enter="onSearch"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div class="self-end">
            <button @click="onSearch" :disabled="isSearching"
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
        :selected-row-id="selectedSubs?.subsId"
        @row-click="onRowSelect"
        storage-key="subscriptionPage"
        title="가입 목록"
      />

      <!-- 입력 폼 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">가입 정보</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID <span class="text-blue-400">*</span></label>
            <input v-model="formData.subsId" :readonly="isRowSelected"
              :class="['w-full h-8 px-2 border rounded text-sm focus:outline-none focus:border-blue-400', isRowSelected ? 'bg-gray-50 text-gray-400 border-gray-200' : 'border-gray-300']" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입명</label>
            <input v-model="formData.subsNm" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스</label>
            <CommonCodeSelect common-code="svc_cd" v-model="formData.svcCd" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">기본상품코드</label>
            <CommonCodeSelect common-code="basic_prod_cd" v-model="formData.basicProdCd" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">상태</label>
            <CommonCodeSelect common-code="subs_status_cd" v-model="formData.subsStatusCd" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입일시</label>
            <input type="datetime-local" v-model="formData.subsDt" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">관리자</label>
            <div class="flex gap-2">
              <input :value="adminNickname" readonly
                class="flex-1 h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
              <button @click="showUserPopup = true"
                class="h-8 px-3 border border-gray-300 rounded text-sm text-gray-600 hover:bg-gray-50">검색</button>
            </div>
          </div>
        </div>
      </div>

    </div>

    <UserSearchPopup
      :visible="showUserPopup"
      @select="onAdminSelect"
      @close="showUserPopup = false"
    />

    <FloatingActionBar>
      <button @click="onRegister" class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors">등록</button>
      <button @click="onUpdate" class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors">변경</button>
      <button @click="onDeleteClick" class="h-8 px-4 border border-red-300 text-sm rounded text-red-600 hover:bg-red-50 transition-colors">삭제</button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="confirmOpen"
      message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
      confirm-text="삭제"
      confirm-type="danger"
      @confirm="onDelete"
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { searchSubscriptions, createSubscription, updateSubscription, deleteSubscription } from '../api/subscriptionApi'
import { useToast } from '../composables/useToast'
import DataGrid from '../components/common/DataGrid.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'
import CommonCodeSelect from '../components/common/CommonCodeSelect.vue'
import UserSearchPopup from '../components/common/UserSearchPopup.vue'
import { useCommonCodeLabel } from '../composables/useCommonCodeLabel'

const auth = useAuthStore()
const route = useRoute()

const { getLabel } = useCommonCodeLabel(['subs_status_cd', 'svc_cd', 'basic_prod_cd'])

const EMPTY_FORM = { subsId: '', subsNm: '', svcCd: '', basicProdCd: '', subsStatusCd: '', subsDt: '', chgDt: '', adminId: '' }
const showUserPopup = ref(false)

const columns = computed(() => [
  { key: 'subsId', header: '가입ID', size: 120 },
  { key: 'subsNm', header: '가입명', size: 150 },
  { key: 'svcCd', header: '서비스', size: 120,
    cell: { props: ['value'], setup(props) { return () => getLabel('svc_cd', props.value) } } },
  { key: 'basicProdCd', header: '기본상품코드', size: 120,
    cell: { props: ['value'], setup(props) { return () => getLabel('basic_prod_cd', props.value) } } },
  { key: 'subsStatusCd', header: '상태', size: 80,
    cell: { props: ['value'], setup(props) { return () => getLabel('subs_status_cd', props.value) } } },
  { key: 'subsDt', header: '가입일시', size: 160 },
])

const items = ref([])
const selectedSubs = ref(null)
const formData = reactive({ ...EMPTY_FORM })
const keyword = ref('')
const searchType = ref('SUBS_ID')
const { showSuccess, showError } = useToast()
const confirmOpen = ref(false)
const saveConfirmOpen = ref(false)
const saveConfirmMessage = ref('')
const saveConfirmAction = ref(null)
const isSearching = ref(false)

const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 10

const isRowSelected = ref(false)
const adminNickname = ref('')

const toFormData = (dto) => ({
  subsId: dto.subsId || '',
  subsNm: dto.subsNm || '',
  svcCd: dto.svcCd || '',
  basicProdCd: dto.basicProdCd || '',
  subsStatusCd: dto.subsStatusCd || '',
  subsDt: dto.subsDt ? dto.subsDt.slice(0, 16) : '',
  chgDt: dto.chgDt ? dto.chgDt.slice(0, 16) : '',
  adminId: dto.adminId || '',
})

const toRequestDto = () => ({
  subsId: formData.subsId || null,
  subsNm: formData.subsNm || null,
  svcCd: formData.svcCd || null,
  basicProdCd: formData.basicProdCd || null,
  subsStatusCd: formData.subsStatusCd || null,
  subsDt: formData.subsDt || null,
  chgDt: formData.chgDt || null,
  adminId: formData.adminId || null,
  createdBy: auth.user?.userId ?? 'SYSTEM',
})


const fetchList = async (pageNum = 0) => {
  const data = await searchSubscriptions({ type: searchType.value, keyword: keyword.value.trim(), page: pageNum, size: pageSize })
  items.value = data.content
  page.value = data.number
  totalPages.value = data.totalPages
  totalElements.value = data.totalElements
}

const handlePageChange = (newPage) => fetchList(newPage)

watch(() => route.query.entityId || route.query.subsId, async (subsId) => {
  if (subsId) {
    keyword.value = subsId
    searchType.value = 'SUBS_ID'
    try {
      await fetchList()
    } catch { showError('조회에 실패했습니다.') }
  }
}, { immediate: true })

const onAdminSelect = (user) => {
  formData.adminId = user.userId
  adminNickname.value = user.nickname || ''
}

const onSearch = async () => {
  isSearching.value = true
  try {
    await fetchList(0)
    selectedSubs.value = null
    Object.assign(formData, EMPTY_FORM)
    adminNickname.value = ''
    isRowSelected.value = false
  } catch { showError('조회에 실패했습니다.') }
  finally { isSearching.value = false }
}

const onRowSelect = (item) => {
  selectedSubs.value = item
  Object.assign(formData, toFormData(item))
  adminNickname.value = item.adminNickname || ''
  isRowSelected.value = true
}

const onRegister = () => {
  if (!formData.subsId.trim()) { showError('가입ID는 필수값입니다.'); return }
  saveConfirmMessage.value = '가입을 등록하시겠습니까?'
  saveConfirmAction.value = 'register'
  saveConfirmOpen.value = true
}

const onUpdate = () => {
  if (!selectedSubs.value) { showError('가입을 선택해 주세요.'); return }
  saveConfirmMessage.value = '가입 정보를 변경하시겠습니까?'
  saveConfirmAction.value = 'update'
  saveConfirmOpen.value = true
}

const handleSaveConfirm = async () => {
  saveConfirmOpen.value = false
  try {
    if (saveConfirmAction.value === 'register') {
      const created = await createSubscription(toRequestDto())
      selectedSubs.value = created
      Object.assign(formData, toFormData(created))
      adminNickname.value = created.adminNickname || ''
      isRowSelected.value = true
      showSuccess('등록이 완료되었습니다.')
    } else {
      const updated = await updateSubscription(selectedSubs.value.subsId, toRequestDto())
      selectedSubs.value = updated
      Object.assign(formData, toFormData(updated))
      adminNickname.value = updated.adminNickname || ''
      showSuccess('변경이 완료되었습니다.')
    }
    await fetchList()
  } catch (err) {
    if (saveConfirmAction.value === 'register') {
      showError(err?.response?.data?.message || '등록에 실패했습니다.')
    } else {
      showError('변경에 실패했습니다.')
    }
  }
}

const onDeleteClick = () => {
  if (!selectedSubs.value) { showError('가입을 선택해 주세요.'); return }
  confirmOpen.value = true
}

const onDelete = async () => {
  confirmOpen.value = false
  try {
    await deleteSubscription(selectedSubs.value.subsId)
    selectedSubs.value = null
    Object.assign(formData, EMPTY_FORM)
    adminNickname.value = ''
    isRowSelected.value = false
    showSuccess('삭제가 완료되었습니다.')
    await fetchList()
  } catch (err) {
    showError(err?.response?.status === 409
      ? '과금기준이 존재하는 가입은 삭제할 수 없습니다.'
      : '삭제에 실패했습니다.')
  }
}

</script>
