<template>
  <div>
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
            <input v-model="formData.subsId" :readonly="isSubsLocked"
              :class="isSubsLocked ? 'w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400' : 'w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400'" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스코드 <span class="text-blue-400">*</span></label>
            <CommonCodeSelect ref="svcCdRef" common-code="svc_cd" v-model="formData.svcCd" @update:modelValue="handleSvcCdChange" :disabled="isSubsLocked" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">기본상품코드</label>
            <CommonCodeSelect common-code="basic_prod_cd" v-model="formData.basicProdCd" :disabled="isReadOnly" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">신청구분 <span class="text-blue-400">*</span></label>
            <select v-model="formData.reqTypeCd" :disabled="reqTypeLocked"
              :class="reqTypeLocked ? 'w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400' : 'w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 bg-white'">
              <option v-for="opt in reqTypeOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">과금기준ID</label>
            <input v-model="formData.billStdId" readonly class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">진행상태</label>
            <input :value="formData.stdRegStatCd ? statusLabel : ''" readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
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
              :ref="el => { if (el) dynamicSelectRefs[config.fieldCd] = el }"
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
import { getBillStdReq, searchBySubsId, createBillStdReq, saveBillStdReq, changeStatus, deleteBillStdReq } from '../api/billStdReqApi'
import { getEffectiveConfigs } from '../api/billStdFieldConfigApi'
import { useToast } from '../composables/useToast'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'
import CommonCodeSelect from '../components/common/CommonCodeSelect.vue'
import { useCommonCodeLabel } from '../composables/useCommonCodeLabel'

const auth = useAuthStore()
const route = useRoute()
const { getLabel } = useCommonCodeLabel(['std_reg_stat_cd'])

const EMPTY_FORM = {
  billStdReqId: '', subsId: '', svcCd: '', basicProdCd: '', reqTypeCd: 'NEW',
  billStdId: '', stdRegStatCd: '',
  fieldValues: {},
}

const formData = reactive({ ...EMPTY_FORM, fieldValues: {} })
const keyword = ref('')
const searchType = ref('billStdReqId')
const { showSuccess, showError } = useToast()
const isSearching = ref(false)
const fieldConfigs = ref([])
const confirmOpen = ref(false)
const confirmMessage = ref('')
const confirmText = ref('')
const confirmType = ref('primary')
const confirmAction = ref(null)
const subsSearched = ref(false)
const svcCdRef = ref(null)
const dynamicSelectRefs = reactive({})


const isNew = computed(() => !formData.billStdReqId)
const status = computed(() => formData.stdRegStatCd)

const isSubsLocked = computed(() => !!formData.billStdReqId || subsSearched.value)

const reqTypeLocked = computed(() => {
  if (formData.billStdReqId) return true
  if (subsSearched.value && !formData.billStdId) return true
  return false
})

const reqTypeOptions = computed(() => {
  if (formData.billStdReqId) {
    return [{ value: formData.reqTypeCd, label: { NEW: '신규', CHANGE: '변경', CANCEL: '해지' }[formData.reqTypeCd] || formData.reqTypeCd }]
  }
  if (subsSearched.value && formData.billStdId) {
    return [{ value: 'CHANGE', label: '변경' }, { value: 'CANCEL', label: '해지' }]
  }
  return [{ value: 'NEW', label: '신규' }]
})

const isReadOnly = computed(() => {
  if (isNew.value) return false
  return status.value !== 'DRAFT'
})

const buttons = computed(() => {
  const s = status.value
  return {
    save:      isNew.value || s === 'DRAFT',
    review:    s === 'DRAFT',
    apprvReq:  s === 'REVIEW' || s === 'REJECTED',
    approve:   s === 'APPRV_REQ',
    reject:    s === 'REVIEW' || s === 'APPRV_REQ',
    cancel:    s === 'DRAFT' || s === 'REVIEW' || s === 'APPRV_REQ' || s === 'REJECTED',
    delete:    s === 'DRAFT',
  }
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
  formData.basicProdCd = dto.basicProdCd ?? ''
  formData.reqTypeCd = dto.reqTypeCd ?? ''
  formData.billStdId = dto.billStdId ?? ''
  formData.stdRegStatCd = dto.stdRegStatCd ?? ''
  formData.fieldValues = dto.fieldValues ?? {}
}

const toRequestDto = () => ({
  subsId: formData.subsId,
  svcCd: formData.svcCd,
  basicProdCd: formData.basicProdCd,
  reqTypeCd: formData.reqTypeCd,
  billStdId: formData.billStdId || undefined,
  fieldValues: { ...formData.fieldValues },
  createdBy: auth.user?.userId ?? 'SYSTEM',
})

const handleNew = () => {
  Object.assign(formData, { ...EMPTY_FORM, fieldValues: {} })
  fieldConfigs.value = []
  subsSearched.value = false
}

const handleSearch = async () => {
  if (!keyword.value.trim()) { showError('검색어를 입력해 주세요.'); return }
  isSearching.value = true
  try {
    let found
    if (searchType.value === 'billStdReqId') {
      found = await getBillStdReq(keyword.value.trim())
    } else {
      found = await searchBySubsId(keyword.value.trim())
    }
    toFormData(found)
    await loadFieldConfigs(found.svcCd)
    subsSearched.value = searchType.value === 'subsId'
    if (found.billStdReqId) {
      showSuccess('조회가 완료되었습니다.')
    } else if (found.billStdId) {
      formData.reqTypeCd = 'CHANGE'
      showSuccess('진행중인 신청이 없습니다. 기존 과금기준을 기반으로 변경/해지 신청할 수 있습니다.')
    } else {
      formData.reqTypeCd = 'NEW'
      showSuccess('과금기준이 없습니다. 신규 신청할 수 있습니다.')
    }
  } catch (err) {
    const s = err?.response?.status
    if (s === 404) {
      showError(err?.response?.data?.message || '유효한 가입이 없습니다.')
    } else if (s === 409) {
      showError(err?.response?.data?.message || '진행중인 신청이 2건 이상 존재합니다.')
    } else {
      showError('서버와 연결할 수 없습니다.')
    }
    Object.assign(formData, { ...EMPTY_FORM, fieldValues: {} })
    fieldConfigs.value = []
    subsSearched.value = false
  } finally { isSearching.value = false }
}

const validateSelectValue = (value, selectRef, fieldNm) => {
  if (!value) return null
  const opts = selectRef?.options ?? []
  if (opts.length > 0 && !opts.some(o => o.commonDtlCode === value)) {
    return `${fieldNm}: 선택값 '${value}'이(가) 유효하지 않습니다.`
  }
  return null
}

const validateDynamicFields = () => {
  const dateRe = /^\d{8}$/
  const timeRe = /^\d{2}:\d{2}(:\d{2})?$/
  for (const config of fieldConfigs.value) {
    const val = formData.fieldValues[config.fieldCd]
    if (!val && val !== 0) continue
    const strVal = String(val)
    if (config.fieldType === 'NUMBER') {
      if (isNaN(Number(strVal)) || strVal.trim() === '') {
        return `${config.fieldNm}: 숫자 형식이 아닙니다.`
      }
    } else if (config.fieldType === 'DATE') {
      if (!dateRe.test(strVal)) {
        return `${config.fieldNm}: 날짜 형식(YYYYMMDD)이 아닙니다.`
      }
      const y = +strVal.slice(0, 4), m = +strVal.slice(4, 6), d = +strVal.slice(6, 8)
      const dt = new Date(y, m - 1, d)
      if (dt.getFullYear() !== y || dt.getMonth() !== m - 1 || dt.getDate() !== d) {
        return `${config.fieldNm}: 유효하지 않은 날짜입니다.`
      }
    } else if (config.fieldType === 'TIME') {
      if (!timeRe.test(strVal)) {
        return `${config.fieldNm}: 시간 형식(HH:MM 또는 HH:MM:SS)이 아닙니다.`
      }
    } else if (config.fieldType === 'SELECT') {
      const ref = dynamicSelectRefs[config.fieldCd]
      const err = validateSelectValue(strVal, ref, config.fieldNm)
      if (err) return err
    }
  }
  return null
}

const handleSave = () => {
  if (!formData.subsId) { showError('가입ID를 입력해 주세요.'); return }
  if (!formData.svcCd) { showError('서비스코드를 선택해 주세요.'); return }
  if (!formData.reqTypeCd) { showError('신청구분을 선택해 주세요.'); return }

  // 콤보 선택값 유효성 체크
  const svcErr = validateSelectValue(formData.svcCd, svcCdRef.value, '서비스코드')
  if (svcErr) { showError(svcErr); return }

  const reqOpts = reqTypeOptions.value.map(o => o.value)
  if (!reqOpts.includes(formData.reqTypeCd)) {
    showError('신청구분: 선택값이 유효하지 않습니다.'); return
  }

  // 동적 필드 포맷/선택값 유효성 체크
  const dynErr = validateDynamicFields()
  if (dynErr) { showError(dynErr); return }

  confirmMessage.value = '과금기준신청을 저장하시겠습니까?'
  confirmText.value = '저장'
  confirmType.value = 'primary'
  confirmAction.value = 'save'
  confirmOpen.value = true
}

const handleStatusChange = (newStatus) => {
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
  if (!formData.billStdReqId) { showError('조회 후 삭제할 수 있습니다.'); return }
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
        showSuccess('저장이 완료되었습니다.')
      } else {
        const saved = await saveBillStdReq(formData.billStdReqId, toRequestDto())
        toFormData(saved)
        await loadFieldConfigs(saved.svcCd)
        showSuccess('저장이 완료되었습니다.')
      }
    } else if (action === 'delete') {
      await deleteBillStdReq(formData.billStdReqId)
      Object.assign(formData, { ...EMPTY_FORM, fieldValues: {} })
      fieldConfigs.value = []
      showSuccess('삭제가 완료되었습니다.')
    } else {
      const result = await changeStatus(formData.billStdReqId, action, auth.user?.userId ?? 'SYSTEM')
      toFormData(result)
      showSuccess('상태가 변경되었습니다.')
    }
  } catch (err) {
    const s = err?.response?.status
    if (s === 400) {
      showError(err?.response?.data?.message || '입력값을 확인해 주세요.')
    } else {
      showError('처리에 실패했습니다.')
    }
  }
}

watch(() => route.query, (q) => {
  if (q.billStdReqId) {
    keyword.value = q.billStdReqId
    searchType.value = 'billStdReqId'
    handleSearch()
  } else if (q.subsId) {
    keyword.value = q.subsId
    searchType.value = 'subsId'
    handleSearch()
  }
}, { immediate: true })
</script>
