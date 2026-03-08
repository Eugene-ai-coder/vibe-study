<template>
  <MainLayout>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">공통코드 관리</h1>

      <!-- 조회영역 -->
      <div class="bg-white rounded-lg shadow-sm p-4 flex items-end gap-4">
        <div>
          <label class="block text-xs text-gray-500 mb-1">공통코드</label>
          <input v-model="searchCode" @keydown.enter="handleSearch"
            class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
        </div>
        <div>
          <label class="block text-xs text-gray-500 mb-1">공통코드명</label>
          <input v-model="searchCodeNm" @keydown.enter="handleSearch"
            class="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
        </div>
        <button @click="handleSearch" :disabled="isLoading"
          class="h-8 px-4 bg-blue-600 text-white text-sm rounded disabled:opacity-50">조회</button>
      </div>

      <!-- 마스터-디테일 목록 -->
      <div class="flex gap-4">
        <!-- 좌측: 공통코드 목록 -->
        <div class="w-1/3 space-y-2">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
            <table class="w-full text-sm border-collapse">
              <thead class="bg-gray-50 border-t border-gray-300 border-b border-gray-300">
                <tr>
                  <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">공통코드</th>
                  <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">공통코드명</th>
                </tr>
              </thead>
            </table>
            <div class="max-h-[40rem] overflow-y-auto">
              <table class="w-full text-sm border-collapse">
                <tbody>
                  <tr v-if="codes.length === 0">
                    <td colspan="2" class="px-3 py-4 text-center text-xs text-gray-400">데이터가 없습니다.</td>
                  </tr>
                  <tr v-for="code in codes" :key="code.commonCode"
                    @click="handleCodeRowClick(code)"
                    :class="['h-7 cursor-pointer border-b border-gray-200',
                      selectedCode?.commonCode === code.commonCode ? 'bg-blue-50 text-blue-900' : 'hover:bg-gray-50 text-gray-800']">
                    <td class="px-3 text-xs">{{ code.commonCode }}</td>
                    <td class="px-3 text-xs">{{ code.commonCodeNm }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="flex justify-end">
            <button @click="handleCodeNewClick" class="h-8 px-4 bg-blue-600 text-white text-sm rounded">등록</button>
          </div>
        </div>

        <!-- 우측: 상세코드 목록 -->
        <div class="w-2/3 space-y-2">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
            <table class="w-full text-sm border-collapse">
              <thead class="bg-gray-50 border-t border-gray-300 border-b border-gray-300">
                <tr>
                  <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">상세코드</th>
                  <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">상세코드명</th>
                  <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-16">정렬</th>
                </tr>
              </thead>
            </table>
            <div class="max-h-[40rem] overflow-y-auto">
              <table class="w-full text-sm border-collapse">
                <tbody>
                  <tr v-if="details.length === 0">
                    <td colspan="3" class="px-3 py-4 text-center text-xs text-gray-400">데이터가 없습니다.</td>
                  </tr>
                  <tr v-for="dtl in details" :key="dtl.commonDtlCode"
                    @click="handleDtlRowClick(dtl)"
                    :class="['h-7 cursor-pointer border-b border-gray-200',
                      selectedDtl?.commonDtlCode === dtl.commonDtlCode ? 'bg-blue-50 text-blue-900' : 'hover:bg-gray-50 text-gray-800']">
                    <td class="px-3 text-xs">{{ dtl.commonDtlCode }}</td>
                    <td class="px-3 text-xs">{{ dtl.commonDtlCodeNm }}</td>
                    <td class="px-3 text-xs text-center">{{ dtl.sortOrder }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="flex justify-end gap-2">
            <button @click="dtlMode = 'new'; Object.assign(dtlForm, EMPTY_DTL_FORM)"
              :disabled="!selectedCode"
              class="h-8 px-4 bg-blue-600 text-white text-sm rounded disabled:opacity-40 disabled:cursor-not-allowed">등록</button>
            <button @click="selectedDtl && (dtlMode = 'edit')"
              :disabled="!selectedDtl"
              class="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed">수정</button>
            <button @click="selectedDtl && (dtlConfirmOpen = true)"
              :disabled="!selectedDtl"
              class="h-8 px-4 border border-red-300 text-sm rounded text-red-600 hover:bg-red-50 disabled:opacity-40 disabled:cursor-not-allowed">삭제</button>
          </div>
        </div>
      </div>

      <!-- 공통코드 폼 -->
      <div v-if="codeMode !== 'view'" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h2 class="text-sm font-semibold text-gray-700 mb-3">공통코드 그룹 정보</h2>
        <div class="grid grid-cols-6 gap-x-4 gap-y-3">
          <label class="col-span-1 flex items-center text-xs text-gray-500">공통코드</label>
          <div class="col-span-1">
            <input v-model="codeForm.commonCode" :readonly="codeMode === 'edit'"
              :class="['w-full h-8 border rounded px-2 text-sm focus:outline-none focus:border-blue-400',
                codeMode === 'edit' ? 'border-gray-200 bg-gray-50 text-gray-500' : 'border-gray-300']" />
          </div>
          <label class="col-span-1 flex items-center text-xs text-gray-500">공통코드명</label>
          <div class="col-span-1">
            <input v-model="codeForm.commonCodeNm" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <label class="col-span-1 flex items-center text-xs text-gray-500">유효시작일</label>
          <div class="col-span-1">
            <input type="datetime-local" v-model="codeForm.effStartDt" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <label class="col-span-1 flex items-center text-xs text-gray-500">유효종료일</label>
          <div class="col-span-1">
            <input type="datetime-local" v-model="codeForm.effEndDt" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <label class="col-span-1 flex items-center text-xs text-gray-500">비고</label>
          <div class="col-span-3">
            <textarea v-model="codeForm.remark" rows="2" class="w-full border border-gray-300 rounded px-2 py-1 text-sm focus:outline-none focus:border-blue-400 resize-none" />
          </div>
        </div>
      </div>

      <!-- 상세코드 폼 -->
      <div v-if="dtlMode !== 'view'" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <h2 class="text-sm font-semibold text-gray-700 mb-3">공통상세코드 정보</h2>
        <div class="grid grid-cols-6 gap-x-4 gap-y-3">
          <label class="col-span-1 flex items-center text-xs text-gray-500">상세코드</label>
          <div class="col-span-1">
            <input v-model="dtlForm.commonDtlCode" :readonly="dtlMode === 'edit'"
              :class="['w-full h-8 border rounded px-2 text-sm focus:outline-none focus:border-blue-400',
                dtlMode === 'edit' ? 'border-gray-200 bg-gray-50 text-gray-500' : 'border-gray-300']" />
          </div>
          <label class="col-span-1 flex items-center text-xs text-gray-500">상세코드명</label>
          <div class="col-span-1">
            <input v-model="dtlForm.commonDtlCodeNm" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <label class="col-span-1 flex items-center text-xs text-gray-500">정렬순서</label>
          <div class="col-span-1">
            <input type="number" v-model.number="dtlForm.sortOrder" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <label class="col-span-1 flex items-center text-xs text-gray-500">유효시작일</label>
          <div class="col-span-1">
            <input type="datetime-local" v-model="dtlForm.effStartDt" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <label class="col-span-1 flex items-center text-xs text-gray-500">유효종료일</label>
          <div class="col-span-1">
            <input type="datetime-local" v-model="dtlForm.effEndDt" class="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <label class="col-span-1 flex items-center text-xs text-gray-500">비고</label>
          <div class="col-span-3">
            <textarea v-model="dtlForm.remark" rows="2" class="w-full border border-gray-300 rounded px-2 py-1 text-sm focus:outline-none focus:border-blue-400 resize-none" />
          </div>
        </div>
      </div>
    </div>

    <FloatingActionBar v-if="codeMode !== 'view' || dtlMode !== 'view'">
      <button @click="handleCancel"
        class="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 transition-colors">취소</button>
      <button @click="handleSave"
        class="h-8 px-6 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded transition-colors">저장</button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="dtlConfirmOpen"
      message="상세코드를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
      @confirm="handleDtlDeleteConfirm"
      @cancel="dtlConfirmOpen = false"
    />
  </MainLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { commonCodeApi } from '../api/commonCodeApi'
import MainLayout from '../components/common/MainLayout.vue'
import Toast from '../components/common/Toast.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'

const auth = useAuthStore()

const EMPTY_CODE_FORM = { commonCode: '', commonCodeNm: '', effStartDt: '', effEndDt: '', remark: '' }
const EMPTY_DTL_FORM = { commonDtlCode: '', commonDtlCodeNm: '', sortOrder: 0, effStartDt: '', effEndDt: '', remark: '' }

const searchCode = ref('')
const searchCodeNm = ref('')
const codes = ref([])
const details = ref([])
const selectedCode = ref(null)
const selectedDtl = ref(null)
const codeForm = reactive({ ...EMPTY_CODE_FORM })
const dtlForm = reactive({ ...EMPTY_DTL_FORM })
const codeMode = ref('view')
const dtlMode = ref('view')
const dtlConfirmOpen = ref(false)
const isLoading = ref(false)
const successMsg = ref('')
const errorMsg = ref('')

const clearMessages = () => { successMsg.value = ''; errorMsg.value = '' }

const getSearchParams = () => {
  const params = {}
  if (searchCode.value) params.commonCode = searchCode.value
  if (searchCodeNm.value) params.commonCodeNm = searchCodeNm.value
  return params
}

onMounted(async () => {
  try { codes.value = await commonCodeApi.getAll() } catch {}
})

const handleSearch = async () => {
  clearMessages()
  isLoading.value = true
  try {
    codes.value = await commonCodeApi.getAll(getSearchParams())
    selectedCode.value = null
    selectedDtl.value = null
    details.value = []
    Object.assign(codeForm, EMPTY_CODE_FORM)
    Object.assign(dtlForm, EMPTY_DTL_FORM)
    codeMode.value = 'view'
    dtlMode.value = 'view'
  } catch { errorMsg.value = '조회에 실패했습니다.' }
  finally { isLoading.value = false }
}

const handleCodeRowClick = async (code) => {
  selectedCode.value = code
  Object.assign(codeForm, { ...code })
  selectedDtl.value = null
  Object.assign(dtlForm, EMPTY_DTL_FORM)
  codeMode.value = 'edit'
  dtlMode.value = 'view'
  try { details.value = await commonCodeApi.getDetails(code.commonCode) }
  catch { errorMsg.value = '상세코드 조회에 실패했습니다.' }
}

const handleDtlRowClick = (dtl) => {
  selectedDtl.value = dtl
  Object.assign(dtlForm, { ...dtl })
  dtlMode.value = 'edit'
}

const handleCodeNewClick = () => {
  codeMode.value = 'new'
  Object.assign(codeForm, EMPTY_CODE_FORM)
}

const handleDtlDeleteConfirm = async () => {
  dtlConfirmOpen.value = false
  clearMessages()
  try {
    await commonCodeApi.deleteDetail(selectedCode.value.commonCode, selectedDtl.value.commonDtlCode)
    selectedDtl.value = null
    Object.assign(dtlForm, EMPTY_DTL_FORM)
    dtlMode.value = 'view'
    details.value = await commonCodeApi.getDetails(selectedCode.value.commonCode)
    successMsg.value = '상세코드가 삭제되었습니다.'
  } catch { errorMsg.value = '상세코드 삭제에 실패했습니다.' }
}

const handleSave = async () => {
  clearMessages()
  const createdBy = auth.user?.userId ?? 'SYSTEM'
  try {
    if (codeMode.value === 'new') {
      await commonCodeApi.create({ ...codeForm, createdBy })
      successMsg.value = '공통코드가 등록되었습니다.'
      codeMode.value = 'view'
      codes.value = await commonCodeApi.getAll(getSearchParams())
      Object.assign(codeForm, EMPTY_CODE_FORM)
    } else if (codeMode.value === 'edit') {
      await commonCodeApi.update(codeForm.commonCode, { ...codeForm, createdBy })
      successMsg.value = '공통코드가 수정되었습니다.'
      codes.value = await commonCodeApi.getAll(getSearchParams())
    }
  } catch (err) {
    errorMsg.value = err?.response?.data?.message || '저장에 실패했습니다.'
    return
  }

  if (dtlMode.value !== 'view' && selectedCode.value) {
    try {
      if (dtlMode.value === 'new') {
        await commonCodeApi.createDetail(selectedCode.value.commonCode, { ...dtlForm, createdBy })
        successMsg.value = '상세코드가 등록되었습니다.'
        dtlMode.value = 'view'
        Object.assign(dtlForm, EMPTY_DTL_FORM)
      } else if (dtlMode.value === 'edit') {
        await commonCodeApi.updateDetail(selectedCode.value.commonCode, dtlForm.commonDtlCode, { ...dtlForm, createdBy })
        successMsg.value = '상세코드가 수정되었습니다.'
      }
      details.value = await commonCodeApi.getDetails(selectedCode.value.commonCode)
    } catch (err) {
      errorMsg.value = err?.response?.data?.message || '상세코드 저장에 실패했습니다.'
    }
  }
}

const handleCancel = () => {
  codeMode.value = 'view'
  dtlMode.value = 'view'
  Object.assign(codeForm, selectedCode.value ? { ...selectedCode.value } : EMPTY_CODE_FORM)
  Object.assign(dtlForm, selectedDtl.value ? { ...selectedDtl.value } : EMPTY_DTL_FORM)
}
</script>
