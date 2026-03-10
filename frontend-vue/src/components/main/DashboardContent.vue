<template>
  <div class="space-y-6">
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />
    <h1 class="text-xl font-bold text-gray-800">대시보드</h1>

    <!-- 현황 카드 -->
    <div class="grid grid-cols-4 gap-4">
      <div v-for="card in CARDS" :key="card.label" class="bg-white rounded-lg shadow-sm p-5">
        <p class="text-xs text-gray-400 mb-1">{{ card.label }}</p>
        <p :class="['text-3xl font-bold', card.color]">{{ card.value }}</p>
      </div>
    </div>

    <!-- 차트 (간단한 바 차트) -->
    <div class="bg-white rounded-lg shadow-sm p-5">
      <h2 class="text-sm font-semibold text-gray-700 mb-4">월별 신규 가입</h2>
      <div class="flex items-end gap-2 h-[200px]">
        <div
          v-for="item in chartData"
          :key="item.month"
          class="flex-1 flex flex-col items-center justify-end"
        >
          <span class="text-xs text-gray-500 mb-1">{{ item.count }}</span>
          <div
            class="w-full bg-[#2563EB] rounded-t"
            :style="{ height: barHeight(item.count) + 'px' }"
          />
          <span class="text-xs text-gray-400 mt-1">{{ item.month.slice(5) }}월</span>
        </div>
      </div>
    </div>

    <!-- TODO 과금기준 -->
    <div class="bg-white rounded-lg shadow-sm p-5">
      <h2 class="text-sm font-semibold text-gray-700 mb-3">
        과금기준 TODO <span class="text-[#2563EB] font-bold">{{ todoItems.length }}</span>건
      </h2>
      <p v-if="todoItems.length === 0" class="text-sm text-gray-400">처리할 TODO가 없습니다.</p>
      <table v-else class="w-full text-sm">
        <thead>
          <tr class="border-b border-gray-100">
            <th class="py-1 text-left text-xs text-gray-500 font-medium">가입ID</th>
            <th class="py-1 text-left text-xs text-gray-500 font-medium">등록진행상태</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in todoItems" :key="item.billStdId"
            class="border-b border-gray-50 hover:bg-blue-50 cursor-pointer"
            @click="router.push(`/bill-std?subsId=${item.subsId}`)">
            <td class="py-1.5 text-[#2563EB]">{{ item.subsId }}</td>
            <td class="py-1.5">{{ getLabel('std_reg_stat_cd', item.stdRegStatCd) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 미납자 리스트 -->
    <div class="bg-white rounded-lg shadow-sm p-5">
      <h2 class="text-sm font-semibold text-gray-700 mb-3">
        미납 가입 <span class="text-[#2563EB] font-bold">{{ pending.length }}</span>건
      </h2>
      <p v-if="pending.length === 0" class="text-sm text-gray-400">미납 가입이 없습니다.</p>
      <table v-else class="w-full text-sm">
        <thead>
          <tr class="border-b border-gray-100">
            <th v-for="h in ['가입ID', '가입명', '서비스명']" :key="h" class="py-1 text-left text-xs text-gray-500 font-medium">{{ h }}</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="item in pending"
            :key="item.subsId"
            class="border-b border-gray-50 hover:bg-blue-50 cursor-pointer"
            @click="router.push(`/subscriptions?subsId=${item.subsId}`)"
          >
            <td class="py-1.5 text-[#2563EB]">{{ item.subsId }}</td>
            <td class="py-1.5">{{ item.subsNm }}</td>
            <td class="py-1.5">{{ getLabel('svc_cd', item.svcCd) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { searchSubscriptions } from '../../api/subscriptionApi'
import { getTodoList } from '../../api/billStdApi'
import { useCommonCodeLabel } from '../../composables/useCommonCodeLabel'
import Toast from '../common/Toast.vue'

const router = useRouter()
const { getLabel } = useCommonCodeLabel(['svc_cd', 'std_reg_stat_cd'])
const items = ref([])
const todoItems = ref([])
const errorMsg = ref('')

onMounted(async () => {
  try {
    const page = await searchSubscriptions({})
    items.value = page.content || []
  } catch {
    errorMsg.value = '대시보드 데이터를 불러오지 못했습니다.'
  }
  try {
    todoItems.value = await getTodoList()
  } catch {}
})

const total = computed(() => items.value.length)
const active = computed(() => items.value.filter(i => i.subsStatusCd === 'ACTIVE').length)
const suspended = computed(() => items.value.filter(i => i.subsStatusCd === 'SUSPENDED').length)
const terminated = computed(() => items.value.filter(i => i.subsStatusCd === 'TERMINATED').length)
const pending = computed(() => items.value.filter(i => i.subsStatusCd === 'PENDING'))

const CARDS = computed(() => [
  { label: '전체 가입자', value: total.value, color: 'text-gray-800' },
  { label: '활성', value: active.value, color: 'text-green-600' },
  { label: '정지', value: suspended.value, color: 'text-yellow-600' },
  { label: '탈퇴', value: terminated.value, color: 'text-red-500' },
])

function getRecentMonths(data) {
  const counts = {}
  const now = new Date()
  for (let i = 5; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
    const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
    counts[key] = 0
  }
  data.forEach(item => {
    if (!item.subsDt) return
    const key = item.subsDt.slice(0, 7)
    if (key in counts) counts[key]++
  })
  return Object.entries(counts).map(([month, count]) => ({ month, count }))
}

const chartData = computed(() => getRecentMonths(items.value))

const maxCount = computed(() => Math.max(...chartData.value.map(d => d.count), 1))

const barHeight = (count) => Math.max(4, (count / maxCount.value) * 160)
</script>
