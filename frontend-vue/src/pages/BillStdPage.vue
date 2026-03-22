<template>
  <div>
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

      <!-- 진행중 신청 안내 배너 -->
      <div v-if="pendingReq" class="bg-blue-50 border border-blue-200 rounded-lg px-4 py-2 text-sm text-blue-700">
        진행중인 과금기준신청이 있습니다 ({{ pendingReqStatusLabel }})
      </div>

      <!-- 과금기준 정보 -->
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
            <CommonCodeSelect common-code="svc_cd" v-model="formData.svcCd" disabled />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">기본상품코드</label>
            <CommonCodeSelect common-code="basic_prod_cd" v-model="formData.basicProdCd" disabled />
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

      <!-- 동적 필드 섹션 (읽기 전용) -->
      <div v-if="fieldConfigs.length > 0" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">서비스별 과금항목</h3>
        <div class="grid grid-cols-3 gap-x-4 gap-y-3">
          <div v-for="config in fieldConfigs" :key="config.fieldCd">
            <label class="block text-xs text-gray-500 mb-1">{{ config.fieldNm }}</label>
            <input v-if="config.fieldType === 'TEXT' || config.fieldType === 'NUMBER' || config.fieldType === 'DATE'"
              :value="formData.fieldValues[config.fieldCd]"
              readonly
              class="w-full h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
            <CommonCodeSelect v-else-if="config.fieldType === 'SELECT'"
              :common-code="config.commonCode"
              :modelValue="formData.fieldValues[config.fieldCd] || ''"
              disabled />
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar>
      <button v-if="formData.subsId" @click="goToReq"
        class="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">과금기준신청</button>
    </FloatingActionBar>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBillStdBySubsId, getBillStd } from '../api/billStdApi'
import { searchBySubsId as searchReqBySubsId } from '../api/billStdReqApi'
import { getEffectiveConfigs } from '../api/billStdFieldConfigApi'
import { getSubscription } from '../api/subscriptionApi'
import { useToast } from '../composables/useToast'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import CommonCodeSelect from '../components/common/CommonCodeSelect.vue'
import { useCommonCodeLabel } from '../composables/useCommonCodeLabel'

const route = useRoute()
const router = useRouter()
const { getLabel } = useCommonCodeLabel(['std_reg_stat_cd'])

const EMPTY_FORM = {
  billStdId: '', subsId: '', svcCd: '', basicProdCd: '', lastEffYn: '',
  effStartDt: '', effEndDt: '',
  billStdStatCd: '',
  fieldValues: {},
}

const formData = reactive({ ...EMPTY_FORM })
const keyword = ref('')
const searchType = ref('subsId')
const { showSuccess, showError } = useToast()
const isSearching = ref(false)
const fieldConfigs = ref([])
const subsAdminId = ref('')
const pendingReq = ref(null)

const pendingReqStatusLabel = ref('')


const loadFieldConfigs = async (svcCd) => {
  if (!svcCd) { fieldConfigs.value = []; return }
  try {
    fieldConfigs.value = await getEffectiveConfigs(svcCd)
  } catch { fieldConfigs.value = [] }
}

const toFormData = (dto) => {
  formData.billStdId = dto.billStdId ?? ''
  formData.subsId = dto.subsId ?? ''
  formData.svcCd = dto.svcCd ?? ''
  formData.basicProdCd = dto.basicProdCd ?? ''
  formData.lastEffYn = dto.lastEffYn ?? ''
  formData.effStartDt = dto.effStartDt ?? ''
  formData.effEndDt = dto.effEndDt ?? ''
  formData.billStdStatCd = dto.billStdStatCd ?? ''
  formData.fieldValues = dto.fieldValues ?? {}
}

const resetForm = () => {
  Object.assign(formData, { ...EMPTY_FORM, fieldValues: {} })
  fieldConfigs.value = []
  subsAdminId.value = ''
  pendingReq.value = null
  pendingReqStatusLabel.value = ''
}

const checkPendingReq = async (subsId) => {
  try {
    const req = await searchReqBySubsId(subsId)
    if (req.billStdReqId && ['DRAFT', 'REVIEW', 'APPRV_REQ'].includes(req.stdRegStatCd)) {
      pendingReq.value = req
      pendingReqStatusLabel.value = getLabel('std_reg_stat_cd', req.stdRegStatCd)
    } else {
      pendingReq.value = null
      pendingReqStatusLabel.value = ''
    }
  } catch {
    pendingReq.value = null
    pendingReqStatusLabel.value = ''
  }
}

const handleSearch = async () => {
  if (!keyword.value.trim()) { showError('검색어를 입력해 주세요.'); return }
  isSearching.value = true
  let shown = false
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
          Object.assign(formData, { ...EMPTY_FORM, subsId, fieldValues: {} })
          fieldConfigs.value = []
          showSuccess('과금기준이 없습니다. 과금기준신청에서 등록해 주세요.')
          shown = true
        } else {
          throw billErr
        }
      }
      await checkPendingReq(subsId)
    } else {
      const found = await getBillStd(keyword.value.trim())
      toFormData(found)
      await loadFieldConfigs(found.svcCd)
      try {
        const subs = await getSubscription(found.subsId)
        subsAdminId.value = subs.adminNickname || subs.adminId || ''
      } catch { subsAdminId.value = '' }
      await checkPendingReq(found.subsId)
    }
    if (!shown) showSuccess('조회가 완료되었습니다.')
  } catch (err) {
    showError(err?.response?.status === 404 ? '조회 결과가 없습니다.' : '서버와 연결할 수 없습니다.')
    resetForm()
  } finally { isSearching.value = false }
}

const goToReq = () => {
  router.push({ path: '/bill-std-req', query: { subsId: formData.subsId } })
}

watch(() => route.query.subsId, (subsId) => {
  if (subsId) {
    searchType.value = 'subsId'
    keyword.value = subsId
    handleSearch()
  }
}, { immediate: true })
</script>
