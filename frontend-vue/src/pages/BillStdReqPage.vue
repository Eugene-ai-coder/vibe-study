<template>
  <div>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">과금기준신청</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색유형</label>
            <select v-model="searchType" class="h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option value="billStdReqId">신청ID</option>
              <option value="subsId">가입ID</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색어</label>
            <input v-model="keyword" @keydown.enter="handleSearch"
              class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div class="self-end">
            <button @click="handleSearch" :disabled="isSearching"
              class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-60">조회</button>
          </div>
          <div class="self-end">
            <button @click="handleNew"
              class="h-8 px-4 border border-gray-300 text-gray-600 text-sm rounded hover:bg-gray-50">신규</button>
          </div>
        </div>
      </div>

      <!-- 입력 폼: 기본정보 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">기본 정보</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">신청ID</label>
            <input v-model="formData.billStdReqId" readonly class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID <span class="text-blue-400">*</span></label>
            <input v-model="formData.subsId" :readonly="!!formData.billStdReqId"
              :class="formData.billStdReqId ? 'w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400' : 'w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400'" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스코드 <span class="text-blue-400">*</span></label>
            <CommonCodeSelect common-code="svc_cd" v-model="formData.svcCd" @update:modelValue="handleSvcCdChange" :disabled="!!formData.billStdReqId" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">신청구분 <span class="text-blue-400">*</span></label>
            <CommonCodeSelect common-code="bill_std_req_type_cd" v-model="formData.reqTypeCd" :disabled="!!formData.billStdReqId" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">과금기준ID</label>
            <input v-model="formData.billStdId" readonly class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">진행상태</label>
            <div class="h-8 flex items-center">
              <span v-if="formData.stdRegStatCd" :class="statusBadgeClass" class="px-2 py-0.5 rounded text-xs font-medium">{{ statusLabel }}</span>
              <span v-else class="text-sm text-gray-400">-</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 동적 필드 섹션 -->
      <div v-if="fieldConfigs.length > 0" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">서비스별 과금항목</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div v-for="config in fieldConfigs" :key="config.fieldCd">
            <label class="block text-xs text-gray-500 mb-1">
              {{ config.fieldNm }}
              <span v-if="config.requiredYn === 'Y'" class="text-blue-400">*</span>
            </label>
            <input v-if="config.fieldType === 'TEXT' || config.fieldType === 'NUMBER'"
              v-model="formData.fieldValues[config.fieldCd]"
              :type="config.fieldType === 'NUMBER' ? 'number' : 'text'"
              :readonly="isReadOnly"
              :class="isReadOnly ? 'w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400' : 'w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400'" />
            <CommonCodeSelect v-else-if="config.fieldType === 'SELECT'"
              :common-code="config.commonCode"
              :modelValue="formData.fieldValues[config.fieldCd] || ''"
              @update:modelValue="formData.fieldValues[config.fieldCd] = $event"
              :disabled="isReadOnly" />
            <input v-else-if="config.fieldType === 'DATE'"
              v-model="formData.fieldValues[config.fieldCd]" type="text"
              placeholder="YYYYMMDD"
              :readonly="isReadOnly"
              :class="isReadOnly ? 'w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400' : 'w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400'" />
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar>
      <button v-if="buttons.delete" @click="handleDeleteClick"
        class="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 rounded text-sm transition-colors">삭제</button>
      <button v-if="buttons.cancel" @click="handleStatusChange('CANCELLED')"
        class="h-8 px-4 border border-gray-300 text-gray-600 hover:bg-gray-50 text-sm rounded transition-colors">취소</button>
      <button v-if="buttons.reject" @click="handleStatusChange('REJECTED')"
        class="h-8 px-4 border border-gray-300 text-gray-600 hover:bg-gray-50 text-sm rounded transition-colors">반려</button>
      <button v-if="buttons.review" @click="handleStatusChange('REVIEW')"
        class="h-8 px-4 border border-blue-600 text-blue-600 hover:bg-blue-50 text-sm rounded transition-colors">검토요청</button>
      <button v-if="buttons.apprvReq" @click="handleStatusChange('APPRV_REQ')"
        class="h-8 px-4 border border-blue-600 text-blue-600 hover:bg-blue-50 text-sm rounded transition-colors">결재요청</button>
      <button v-if="buttons.approve" @click="handleStatusChange('APPROVED')"
        class="h-8 px-4 border border-blue-600 text-blue-600 hover:bg-blue-50 text-sm rounded transition-colors">승인</button>
      <button v-if="buttons.save" @click="handleSave"
        class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">저장</button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="confirmOpen"
      :message="confirmMessage"
      :confirm-text="confirmText"
      :confirm-type="confirmType"
      @confirm="executeConfirmAction"
      @cancel="confirmOpen = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getBillStdReq, createBillStdReq, saveBillStdReq, changeStatus, deleteBillStdReq } from '../api/billStdReqApi'
import { getEffectiveConfigs } from '../api/billStdFieldConfigApi'
import Toast from '../components/common/Toast.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'
import CommonCodeSelect from '../components/common/CommonCodeSelect.vue'
import { useCommonCodeLabel } from '../composables/useCommonCodeLabel'

const auth = useAuthStore()
const route = useRoute()
const { getLabel } = useCommonCodeLabel(['std_reg_stat_cd'])

const EMPTY_FORM = {
  billStdReqId: '', subsId: '', svcCd: '', reqTypeCd: 'NEW',
  billStdId: '', stdRegStatCd: '',
  fieldValues: {},
}

const formData = reactive({ ...EMPTY_FORM, fieldValues: {} })
const keyword = ref('')
const searchType = ref('billStdReqId')
const errorMsg = ref('')
const successMsg = ref('')
const isSearching = ref(false)
const fieldConfigs = ref([])
const confirmOpen = ref(false)
const confirmMessage = ref('')
const confirmText = ref('')
const confirmType = ref('primary')
const confirmAction = ref(null)

const clearMessages = () => { errorMsg.value = ''; successMsg.value = '' }

const isNew = computed(() => !formData.billStdReqId)
const status = computed(() => formData.stdRegStatCd)

const isReadOnly = computed(() => {
  if (isNew.value) return false
  return status.value !== 'DRAFT'
})

const buttons = computed(() => {
  const s = status.value
  return {
    save:      isNew.value || s === 'DRAFT',
    review:    s === 'DRAFT',
    apprvReq:  s === 'REVIEW',
    approve:   s === 'APPRV_REQ',
    reject:    s === 'REVIEW' || s === 'APPRV_REQ',
    cancel:    s === 'DRAFT' || s === 'REVIEW' || s === 'APPRV_REQ',
    delete:    s === 'DRAFT',
  }
})

const statusBadgeClass = computed(() => {
  const colors = {
    DRAFT: 'bg-gray-100 text-gray-700',
    REVIEW: 'bg-yellow-100 text-yellow-700',
    APPRV_REQ: 'bg-blue-100 text-blue-700',
    APPROVED: 'bg-green-100 text-green-700',
    REJECTED: 'bg-red-100 text-red-700',
    CANCELLED: 'bg-gray-100 text-gray-500',
  }
  return colors[formData.stdRegStatCd] || 'bg-gray-100 text-gray-700'
})

const statusLabel = computed(() => getLabel('std_reg_stat_cd', formData.stdRegStatCd))

const loadFieldConfigs = async (svcCd) => {
  if (!svcCd) { fieldConfigs.value = []; return }
  try {
    fieldConfigs.value = await getEffectiveConfigs(svcCd)
  } catch { fieldConfigs.value = [] }
}

const handleSvcCdChange = (newSvcCd) => {
  loadFieldConfigs(newSvcCd)
}

const toFormData = (dto) => {
  formData.billStdReqId = dto.billStdReqId ?? ''
  formData.subsId = dto.subsId ?? ''
  formData.svcCd = dto.svcCd ?? ''
  formData.reqTypeCd = dto.reqTypeCd ?? ''
  formData.billStdId = dto.billStdId ?? ''
  formData.stdRegStatCd = dto.stdRegStatCd ?? ''
  formData.fieldValues = dto.fieldValues ?? {}
}

const toRequestDto = () => ({
  subsId: formData.subsId,
  svcCd: formData.svcCd,
  reqTypeCd: formData.reqTypeCd,
  billStdId: formData.billStdId || undefined,
  fieldValues: { ...formData.fieldValues },
  createdBy: auth.user?.userId ?? 'SYSTEM',
})

const handleNew = () => {
  clearMessages()
  Object.assign(formData, { ...EMPTY_FORM, fieldValues: {} })
  fieldConfigs.value = []
}

const handleSearch = async () => {
  clearMessages()
  if (!keyword.value.trim()) { errorMsg.value = '검색어를 입력해 주세요.'; return }
  isSearching.value = true
  try {
    if (searchType.value === 'billStdReqId') {
      const found = await getBillStdReq(keyword.value.trim())
      toFormData(found)
      await loadFieldConfigs(found.svcCd)
    } else {
      errorMsg.value = '가입ID로 검색하려면 목록 화면을 이용해 주세요.'
      return
    }
    successMsg.value = '조회가 완료되었습니다.'
  } catch (err) {
    const s = err?.response?.status
    if (s === 404) {
      errorMsg.value = '조회 결과가 없습니다.'
    } else {
      errorMsg.value = '서버와 연결할 수 없습니다.'
    }
    Object.assign(formData, { ...EMPTY_FORM, fieldValues: {} })
    fieldConfigs.value = []
  } finally { isSearching.value = false }
}

const handleSave = () => {
  clearMessages()
  if (!formData.subsId) { errorMsg.value = '가입ID를 입력해 주세요.'; return }
  if (!formData.svcCd) { errorMsg.value = '서비스코드를 선택해 주세요.'; return }
  if (!formData.reqTypeCd) { errorMsg.value = '신청구분을 선택해 주세요.'; return }
  confirmMessage.value = '과금기준신청을 저장하시겠습니까?'
  confirmText.value = '저장'
  confirmType.value = 'primary'
  confirmAction.value = 'save'
  confirmOpen.value = true
}

const handleStatusChange = (newStatus) => {
  clearMessages()
  const labels = {
    REVIEW: '검토요청', APPRV_REQ: '결재요청', APPROVED: '승인',
    REJECTED: '반려', CANCELLED: '취소'
  }
  confirmMessage.value = `${labels[newStatus]}하시겠습니까?`
  confirmText.value = labels[newStatus]
  confirmType.value = newStatus === 'REJECTED' || newStatus === 'CANCELLED' ? 'danger' : 'primary'
  confirmAction.value = newStatus
  confirmOpen.value = true
}

const handleDeleteClick = () => {
  if (!formData.billStdReqId) { errorMsg.value = '조회 후 삭제할 수 있습니다.'; return }
  clearMessages()
  confirmMessage.value = '삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.'
  confirmText.value = '삭제'
  confirmType.value = 'danger'
  confirmAction.value = 'delete'
  confirmOpen.value = true
}

const executeConfirmAction = async () => {
  confirmOpen.value = false
  const action = confirmAction.value
  try {
    if (action === 'save') {
      if (isNew.value) {
        const created = await createBillStdReq(toRequestDto())
        toFormData(created)
        await loadFieldConfigs(created.svcCd)
        successMsg.value = '저장이 완료되었습니다.'
      } else {
        const saved = await saveBillStdReq(formData.billStdReqId, toRequestDto())
        toFormData(saved)
        await loadFieldConfigs(saved.svcCd)
        successMsg.value = '저장이 완료되었습니다.'
      }
    } else if (action === 'delete') {
      await deleteBillStdReq(formData.billStdReqId)
      Object.assign(formData, { ...EMPTY_FORM, fieldValues: {} })
      fieldConfigs.value = []
      successMsg.value = '삭제가 완료되었습니다.'
    } else {
      const result = await changeStatus(formData.billStdReqId, action, auth.user?.userId ?? 'SYSTEM')
      toFormData(result)
      successMsg.value = '상태가 변경되었습니다.'
    }
  } catch (err) {
    const s = err?.response?.status
    if (s === 400) {
      errorMsg.value = err?.response?.data?.message || '입력값을 확인해 주세요.'
    } else {
      errorMsg.value = '처리에 실패했습니다.'
    }
  }
}

watch(() => route.query.billStdReqId, (id) => {
  if (id) {
    keyword.value = id
    searchType.value = 'billStdReqId'
    handleSearch()
  }
}, { immediate: true })
</script>
