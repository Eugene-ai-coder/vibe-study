<template>
  <div>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">가입별 과금기준 관리</h1>

      <!-- 조회영역 -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-3">
        <div class="flex items-center gap-2">
          <div>
            <label class="block text-xs text-gray-500 mb-1">검색유형</label>
            <select v-model="searchType" class="h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option value="subsId">가입ID</option>
              <option value="billStdId">과금기준ID</option>
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
        </div>
      </div>

      <!-- 입력 폼 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">과금기준 정보</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">과금기준ID</label>
            <input v-model="formData.billStdId" readonly class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">가입ID</label>
            <input v-model="formData.subsId" readonly class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">관리자</label>
            <input :value="subsAdminId" readonly class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스</label>
            <CommonCodeSelect common-code="svc_cd" v-model="formData.svcCd" @update:modelValue="handleSvcCdChange" :disabled="!!formData.subsId" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">적용시작일</label>
            <input v-model="formData.effStartDt" readonly class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">적용종료일</label>
            <input v-model="formData.effEndDt" readonly class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">최종적용여부</label>
            <input v-model="formData.lastEffYn" readonly class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">과금기준상태</label>
            <CommonCodeSelect common-code="bill_std_stat_cd" v-model="formData.billStdStatCd" disabled />
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
            <!-- TEXT/NUMBER -->
            <input v-if="config.fieldType === 'TEXT' || config.fieldType === 'NUMBER'"
              v-model="formData.fieldValues[config.fieldCd]"
              :type="config.fieldType === 'NUMBER' ? 'number' : 'text'"
              class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
            <!-- SELECT (공통코드 연동) -->
            <CommonCodeSelect v-else-if="config.fieldType === 'SELECT'"
              :common-code="config.commonCode"
              :modelValue="formData.fieldValues[config.fieldCd] || ''"
              @update:modelValue="formData.fieldValues[config.fieldCd] = $event" />
            <!-- DATE -->
            <input v-else-if="config.fieldType === 'DATE'"
              v-model="formData.fieldValues[config.fieldCd]" type="text"
              placeholder="YYYYMMDD"
              class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar>
      <button @click="handleDeleteClick" class="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 rounded text-sm transition-colors">삭제</button>
      <button @click="handleChange" class="h-8 px-6 border border-blue-600 text-blue-600 hover:bg-blue-50 text-sm rounded transition-colors">변경</button>
      <button @click="handleSave" class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">저장</button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="confirmOpen"
      message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
      confirm-text="삭제"
      confirm-type="danger"
      @confirm="executeDelete"
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
import { ref, reactive, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getBillStdBySubsId, getBillStd, createBillStd, updateBillStd, deleteBillStd } from '../api/billStdApi'
import { getEffectiveConfigs } from '../api/billStdFieldConfigApi'
import { getSubscription } from '../api/subscriptionApi'
import Toast from '../components/common/Toast.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'
import CommonCodeSelect from '../components/common/CommonCodeSelect.vue'

const auth = useAuthStore()
const route = useRoute()

const EMPTY_FORM = {
  billStdId: '', subsId: '', svcCd: '', lastEffYn: 'Y',
  effStartDt: '', effEndDt: '',
  billStdStatCd: '',
  fieldValues: {},
}

const formData = reactive({ ...EMPTY_FORM })
const keyword = ref('')
const searchType = ref('subsId')
const errorMsg = ref('')
const successMsg = ref('')
const confirmOpen = ref(false)
const saveConfirmOpen = ref(false)
const saveConfirmMessage = ref('')
const saveConfirmAction = ref(null)
const isSearching = ref(false)
const fieldConfigs = ref([])
const subsAdminId = ref('')

const clearMessages = () => { errorMsg.value = ''; successMsg.value = '' }

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
  formData.billStdId = dto.billStdId ?? ''
  formData.subsId = dto.subsId ?? ''
  formData.svcCd = dto.svcCd ?? ''
  formData.lastEffYn = dto.lastEffYn ?? 'Y'
  formData.effStartDt = dto.effStartDt ?? ''
  formData.effEndDt = dto.effEndDt ?? ''
  formData.billStdStatCd = dto.billStdStatCd ?? ''
  formData.fieldValues = dto.fieldValues ?? {}
}

const toRequestDto = () => ({
  subsId: formData.subsId,
  svcCd: formData.svcCd,
  lastEffYn: formData.lastEffYn,
  effStartDt: formData.effStartDt,
  effEndDt: formData.effEndDt,
  billStdStatCd: formData.billStdStatCd,
  fieldValues: { ...formData.fieldValues },
  createdBy: auth.user?.userId ?? 'SYSTEM',
})

const handleSearch = async () => {
  clearMessages()
  if (!keyword.value.trim()) { errorMsg.value = '검색어를 입력해 주세요.'; return }
  isSearching.value = true
  try {
    if (searchType.value === 'subsId') {
      const subsId = keyword.value.trim()
      const subs = await getSubscription(subsId)
      subsAdminId.value = subs.adminNickname || subs.adminId || ''
      try {
        const found = await getBillStdBySubsId(subsId)
        toFormData(found)
        await loadFieldConfigs(found.svcCd)
      } catch (billErr) {
        if (billErr?.response?.status === 404) {
          const now = new Date()
          const pad = (n) => String(n).padStart(2, '0')
          const nowIso = `${now.getFullYear()}-${pad(now.getMonth()+1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
          Object.assign(formData, {
            ...EMPTY_FORM,
            subsId: subs.subsId,
            svcCd: subs.svcCd || '',
            effStartDt: nowIso,
            effEndDt: '9999-12-31T23:59:59',
            lastEffYn: 'Y',
            billStdStatCd: 'ACTIVE',
            fieldValues: {},
          })
          await loadFieldConfigs(subs.svcCd)
        } else {
          throw billErr
        }
      }
    } else {
      const found = await getBillStd(keyword.value.trim())
      toFormData(found)
      await loadFieldConfigs(found.svcCd)
      try {
        const subs = await getSubscription(found.subsId)
        subsAdminId.value = subs.adminNickname || subs.adminId || ''
      } catch { subsAdminId.value = '' }
    }
    successMsg.value = '조회가 완료되었습니다.'
  } catch (err) {
    errorMsg.value = err?.response?.status === 404 ? '조회 결과가 없습니다.' : '서버와 연결할 수 없습니다.'
    Object.assign(formData, { ...EMPTY_FORM, fieldValues: {} })
    fieldConfigs.value = []
    subsAdminId.value = ''
  } finally { isSearching.value = false }
}

const handleSave = () => {
  clearMessages()
  saveConfirmMessage.value = '과금기준을 저장하시겠습니까?'
  saveConfirmAction.value = 'save'
  saveConfirmOpen.value = true
}

const handleChange = () => {
  if (!formData.billStdId) { errorMsg.value = '조회 후 변경할 수 있습니다.'; return }
  clearMessages()
  saveConfirmMessage.value = '과금기준을 변경하시겠습니까?'
  saveConfirmAction.value = 'change'
  saveConfirmOpen.value = true
}

const handleSaveConfirm = async () => {
  saveConfirmOpen.value = false
  try {
    if (saveConfirmAction.value === 'save') {
      const created = await createBillStd(toRequestDto())
      toFormData(created)
      await loadFieldConfigs(created.svcCd)
      successMsg.value = '저장이 완료되었습니다.'
    } else {
      const updated = await updateBillStd(formData.billStdId, toRequestDto())
      toFormData(updated)
      await loadFieldConfigs(updated.svcCd)
      successMsg.value = '변경이 완료되었습니다.'
    }
  } catch (err) {
    errorMsg.value = err?.response?.data?.message || (saveConfirmAction.value === 'save' ? '저장에 실패했습니다.' : '변경에 실패했습니다.')
  }
}

const handleDeleteClick = () => {
  if (!formData.billStdId) { errorMsg.value = '조회 후 삭제할 수 있습니다.'; return }
  clearMessages()
  confirmOpen.value = true
}

const executeDelete = async () => {
  confirmOpen.value = false
  try {
    await deleteBillStd(formData.billStdId)
    Object.assign(formData, EMPTY_FORM)
    fieldConfigs.value = []
    subsAdminId.value = ''
    successMsg.value = '삭제가 완료되었습니다.'
  } catch (err) {
    errorMsg.value = err?.response?.status === 409 ? '다른 이력이 존재하여 삭제할 수 없습니다.' : '삭제에 실패했습니다.'
  }
}

watch(() => route.query.subsId, (subsId) => {
  if (subsId) {
    searchType.value = 'subsId'
    keyword.value = subsId
    handleSearch()
  }
}, { immediate: true })
</script>
