<template>
  <MainLayout>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <SubscriptionSearchPopup
      v-if="popupOpen"
      @close="popupOpen = false"
      @select="handlePopupSelect"
    />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">대표가입 관리</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스</label>
            <select v-model="svcNm" class="h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option>전체</option>
              <option>IDC 전력</option>
              <option>IDC NW</option>
              <option>비즈넷</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색유형</label>
            <select v-model="searchType" class="h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option>가입ID</option>
              <option>가입명</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색어</label>
            <input v-model="keyword" @keydown.enter="handleSearch"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div class="self-end">
            <button @click="handleSearch" :disabled="isLoading"
              class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-60">조회</button>
          </div>
        </div>
      </div>

      <!-- 목록 -->
      <DataGrid
        :columns="columns"
        :data="displayItems"
        row-id-accessor="subsId"
        :selected-row-id="selected?.subsId"
        @row-click="handleRowClick"
        storage-key="subscriptionMainPage"
        title="대표가입 목록"
      />

      <!-- 입력 폼 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">대표가입 정보</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID</label>
            <input :value="formData.subsId" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">대표가입여부</label>
            <select v-model="formData.mainSubsYn" class="w-full h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option value="Y">Y</option>
              <option value="N">N</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">대표가입ID</label>
            <div class="flex gap-1">
              <input v-model="formData.mainSubsId"
                :readonly="formData.mainSubsYn === 'Y'"
                :class="['flex-1 h-8 px-2 border rounded text-sm focus:outline-none focus:border-blue-400',
                  formData.mainSubsYn === 'Y' ? 'bg-gray-50 text-gray-400 border-gray-200' : 'border-gray-300']" />
              <button v-if="formData.mainSubsYn === 'N'" @click="popupOpen = true"
                class="h-8 px-2 border border-gray-300 rounded text-xs text-gray-600 hover:bg-gray-50">검색</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar>
      <button @click="handleSaveClick" class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">저장</button>
    </FloatingActionBar>
  </MainLayout>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import { getSubscriptionMainList, saveSubscriptionMain } from '../api/subscriptionMainApi'
import MainLayout from '../components/common/MainLayout.vue'
import Toast from '../components/common/Toast.vue'
import DataGrid from '../components/common/DataGrid.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import SubscriptionSearchPopup from '../components/common/SubscriptionSearchPopup.vue'

const auth = useAuthStore()

const SVC_MAP = { 'IDC 전력': '서비스1', 'IDC NW': '서비스2', '비즈넷': '서비스3' }
const SVC_LABEL_MAP = Object.fromEntries(Object.entries(SVC_MAP).map(([k, v]) => [v, k]))

const EMPTY_FORM = { subsId: '', mainSubsYn: 'Y', mainSubsId: '', effStartDt: '', effEndDt: '' }

const columns = [
  { key: 'subsId', header: '가입ID', size: 120 },
  { key: 'subsNm', header: '가입명', size: 150 },
  { key: 'svcNm', header: '서비스명', size: 120 },
  { key: 'mainSubsYn', header: '대표가입여부', size: 100 },
  { key: 'mainSubsId', header: '대표가입ID', size: 120 },
]

const svcNm = ref('전체')
const searchType = ref('가입ID')
const keyword = ref('')
const items = ref([])
const selected = ref(null)
const formData = reactive({ ...EMPTY_FORM })
const popupOpen = ref(false)
const isLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const displayItems = computed(() =>
  items.value.map(item => ({ ...item, svcNm: SVC_LABEL_MAP[item.svcNm] || item.svcNm }))
)

const clearMessages = () => { errorMsg.value = ''; successMsg.value = '' }

const getSearchParams = () => ({
  svcNm: svcNm.value !== '전체' ? SVC_MAP[svcNm.value] : undefined,
  searchType: searchType.value || undefined,
  keyword: keyword.value.trim(),
})

const handleSearch = async () => {
  if (!keyword.value.trim()) { errorMsg.value = '조회조건을 입력해 주세요.'; return }
  if (keyword.value.trim().length < 2) { errorMsg.value = '조회조건은 2자 이상 입력해 주세요.'; return }
  clearMessages()
  isLoading.value = true
  try {
    items.value = await getSubscriptionMainList(getSearchParams())
    selected.value = null
    Object.assign(formData, EMPTY_FORM)
  } catch { errorMsg.value = '조회에 실패했습니다.' }
  finally { isLoading.value = false }
}

const handleRowClick = (item) => {
  selected.value = item
  Object.assign(formData, {
    subsId: item.subsId || '',
    mainSubsYn: item.mainSubsYn || 'Y',
    mainSubsId: item.mainSubsId || '',
    effStartDt: '',
    effEndDt: '',
  })
}

const handlePopupSelect = (subsId) => {
  formData.mainSubsId = subsId
}

const handleSaveClick = async () => {
  clearMessages()
  if (!selected.value) { errorMsg.value = '목록에서 가입을 선택해 주세요.'; return }
  try {
    await saveSubscriptionMain({
      subsId: formData.subsId,
      mainSubsYn: formData.mainSubsYn,
      mainSubsId: formData.mainSubsYn === 'Y' ? null : formData.mainSubsId,
      createdBy: auth.user?.userId ?? 'SYSTEM',
    })
    successMsg.value = '저장이 완료되었습니다.'
    const result = await getSubscriptionMainList(getSearchParams())
    items.value = result
    const updated = result.find(r => r.subsId === formData.subsId)
    if (updated) {
      selected.value = updated
      Object.assign(formData, {
        subsId: updated.subsId || '',
        mainSubsYn: updated.mainSubsYn || 'Y',
        mainSubsId: updated.mainSubsId || '',
        effStartDt: '',
        effEndDt: '',
      })
    }
  } catch (err) {
    errorMsg.value = err?.response?.data?.message || '저장에 실패했습니다.'
  }
}
</script>
