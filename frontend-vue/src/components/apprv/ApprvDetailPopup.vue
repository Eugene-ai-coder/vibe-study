<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
    <div class="bg-white rounded-lg shadow-xl w-[720px] flex flex-col max-h-[90vh]">
      <!-- 팝업 헤더 -->
      <div class="flex items-center justify-between px-5 py-2 border-b border-gray-200">
        <span class="text-xs text-gray-400">결재내용 상세</span>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-lg leading-none">&times;</button>
      </div>

      <div class="overflow-auto flex-1">
        <div v-if="loading" class="text-center text-sm text-gray-400 py-16">불러오는 중...</div>
        <template v-else-if="apprvReq">
          <!-- ═══ 결재서류 본문 ═══ -->
          <div class="px-8 py-6 space-y-5">

            <!-- ── 문서 제목 ── -->
            <div class="text-center">
              <h1 class="text-xl font-bold text-gray-800 tracking-widest">과금기준 결재요청서</h1>
              <div class="mt-1 h-[2px] bg-gray-800 mx-auto w-48"></div>
            </div>

            <!-- ── 결재선 (우측 정렬) ── -->
            <div class="flex justify-end">
              <table class="border-collapse text-xs text-center">
                <thead>
                  <tr>
                    <th class="border border-gray-400 bg-gray-100 px-4 py-1 font-semibold text-gray-600 w-[80px]">기안</th>
                    <th class="border border-gray-400 bg-gray-100 px-4 py-1 font-semibold text-gray-600 w-[80px]">승인</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td class="border border-gray-400 h-[52px] align-middle text-sm font-medium text-gray-700">
                      {{ apprvReq.createdBy }}
                    </td>
                    <td class="border border-gray-400 h-[52px] align-middle">
                      <template v-if="apprvReq.approverId">
                        <span class="text-sm font-medium text-blue-700">{{ apprvReq.approverId }}</span>
                      </template>
                      <template v-else>
                        <span class="text-gray-400 text-[10px]">(미결)</span>
                      </template>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- ── 문서 정보 ── -->
            <table class="w-full text-sm border-collapse border-t-2 border-gray-800">
              <colgroup>
                <col class="w-[110px]" />
                <col />
                <col class="w-[110px]" />
                <col />
              </colgroup>
              <tbody>
                <tr>
                  <th class="doc-th">결재요청ID</th>
                  <td class="doc-td font-mono">{{ apprvReq.apprvReqId }}</td>
                  <th class="doc-th">요청일시</th>
                  <td class="doc-td">{{ apprvReq.createdDt }}</td>
                </tr>
                <tr>
                  <th class="doc-th">기안자</th>
                  <td class="doc-td">{{ apprvReq.createdBy }}</td>
                  <th class="doc-th">결재자</th>
                  <td class="doc-td">{{ apprvReq.approverId || '-' }}</td>
                </tr>
              </tbody>
            </table>

            <!-- ── 과금기준 스냅샷 ── -->
            <template v-if="snapshot">
              <div>
                <div class="flex items-center gap-2 mb-2">
                  <div class="w-1 h-4 bg-blue-600 rounded-full"></div>
                  <h3 class="text-sm font-semibold text-gray-700">과금기준신청 내역</h3>
                </div>
                <table class="w-full text-sm border-collapse border-t-2 border-gray-800">
                  <colgroup>
                    <col class="w-[110px]" />
                    <col />
                    <col class="w-[110px]" />
                    <col />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th class="doc-th">과금기준ID</th>
                      <td class="doc-td font-mono">{{ snapshot.billStdId || '-' }}</td>
                      <th class="doc-th">가입ID</th>
                      <td class="doc-td font-mono">{{ snapshot.subsId }}</td>
                    </tr>
                    <tr>
                      <th class="doc-th">서비스코드</th>
                      <td class="doc-td">{{ getLabel('svc_cd', snapshot.svcCd) }}</td>
                      <th class="doc-th">신청유형</th>
                      <td class="doc-td">
                        <span :class="getReqTypeBadgeClass(snapshot.reqTypeCd)">{{ REQ_TYPE_LABELS[snapshot.reqTypeCd] || snapshot.reqTypeCd }}</span>
                      </td>
                    </tr>
                    <tr>
                      <th class="doc-th">기본상품</th>
                      <td class="doc-td">{{ snapshot.basicProdCd ? getLabel('basic_prod_cd', snapshot.basicProdCd) : '-' }}</td>
                      <th class="doc-th">등록상태</th>
                      <td class="doc-td">{{ snapshot.stdRegStatCd || '-' }}</td>
                    </tr>
                    <tr>
                      <th class="doc-th">최초신청일</th>
                      <td class="doc-td">{{ snapshot.firstReqDt || '-' }}</td>
                      <th class="doc-th">유효시작일</th>
                      <td class="doc-td">{{ snapshot.effStartDt || '-' }}</td>
                    </tr>
                    <tr>
                      <th class="doc-th">유효종료일</th>
                      <td class="doc-td">{{ snapshot.effEndDt || '-' }}</td>
                      <td colspan="2" class="doc-td"></td>
                    </tr>
                    <!-- 동적 필드 -->
                    <template v-if="snapshot.fieldValues && Object.keys(snapshot.fieldValues).length > 0">
                      <tr v-for="(pair, idx) in dynamicFieldPairs" :key="'dyn-' + idx">
                        <th class="doc-th">{{ pair[0].label }}</th>
                        <td class="doc-td">{{ pair[0].value }}</td>
                        <th v-if="pair[1]" class="doc-th">{{ pair[1].label }}</th>
                        <td v-if="pair[1]" class="doc-td">{{ pair[1].value }}</td>
                        <td v-else colspan="2" class="doc-td"></td>
                      </tr>
                    </template>
                  </tbody>
                </table>
              </div>
            </template>

            <!-- ── 특기사항 (읽기 전용) ── -->
            <div v-if="apprvReq.apprvRemarks">
              <div class="flex items-center gap-2 mb-2">
                <div class="w-1 h-4 bg-blue-600 rounded-full"></div>
                <h3 class="text-sm font-semibold text-gray-700">특기사항</h3>
              </div>
              <div class="w-full border border-gray-300 rounded px-3 py-2 text-sm text-gray-800 bg-gray-50 whitespace-pre-wrap min-h-[40px]">{{ apprvReq.apprvRemarks }}</div>
            </div>

          </div>
        </template>
        <div v-else class="text-center text-sm text-gray-400 py-16">결재요청 정보를 찾을 수 없습니다.</div>
      </div>

      <!-- 하단 버튼 -->
      <div class="px-5 py-3 border-t border-gray-200 flex justify-end bg-gray-50 rounded-b-lg">
        <button @click="$emit('close')"
          class="h-8 px-4 border border-gray-300 text-gray-600 rounded text-sm hover:bg-gray-100">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getApprvReq } from '../../api/apprvReqApi'
import { useCommonCodeLabel } from '../../composables/useCommonCodeLabel'
import { useDynamicFieldConfigs, REQ_TYPE_LABELS, getReqTypeBadgeClass } from '../../composables/useDynamicFieldConfigs'

const props = defineProps({
  apprvReqId: { type: String, required: true },
})

defineEmits(['close'])

const { getLabel } = useCommonCodeLabel(['svc_cd', 'basic_prod_cd'])
const { fieldConfigs, dynCodeMap, loadFieldConfigs, buildFieldPairs } = useDynamicFieldConfigs()
const apprvReq = ref(null)
const loading = ref(true)

const snapshot = computed(() => {
  if (!apprvReq.value?.apprvReqContent) return null
  try {
    return JSON.parse(apprvReq.value.apprvReqContent)
  } catch {
    return null
  }
})

const dynamicFieldPairs = computed(() => buildFieldPairs(snapshot.value?.fieldValues))

onMounted(async () => {
  try {
    apprvReq.value = await getApprvReq(props.apprvReqId)
    const snap = snapshot.value
    if (snap?.svcCd) {
      await loadFieldConfigs(snap.svcCd)
    }
  } catch {
    apprvReq.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.doc-th {
  @apply bg-gray-50 border-b border-r border-gray-300 px-3 py-2 text-left text-xs font-semibold text-gray-600 whitespace-nowrap;
}
.doc-td {
  @apply border-b border-r border-gray-300 px-3 py-2 text-gray-800;
}
.doc-td:last-child,
.doc-th:last-child {
  @apply border-r-0;
}
</style>
