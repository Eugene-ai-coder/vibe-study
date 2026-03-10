<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
    <div class="bg-white rounded-lg shadow-xl w-[500px] flex flex-col max-h-[70vh]">
      <div class="flex items-center justify-between px-4 py-3 border-b border-gray-200">
        <h2 class="text-sm font-semibold text-gray-800">가입 검색</h2>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-lg leading-none">&times;</button>
      </div>

      <div class="flex gap-2 p-3 border-b border-gray-100">
        <input v-model="keyword" @keydown.enter="handleSearch" placeholder="가입ID 입력"
          class="flex-1 h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
        <button @click="handleSearch" :disabled="isSearching"
          class="h-8 px-3 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-50">조회</button>
      </div>

      <div class="overflow-auto flex-1">
        <table class="w-full text-sm">
          <thead class="bg-gray-50 sticky top-0">
            <tr>
              <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">가입ID</th>
              <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">가입명</th>
              <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">서비스</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="results.length === 0">
              <td colspan="3" class="px-3 py-4 text-center text-xs text-gray-400">조회 결과가 없습니다.</td>
            </tr>
            <tr v-for="item in results" :key="item.subsId"
              @click="handleSelect(item.subsId)"
              class="h-7 cursor-pointer hover:bg-blue-50 border-b border-gray-100">
              <td class="px-3 text-xs text-gray-800">{{ item.subsId }}</td>
              <td class="px-3 text-xs text-gray-600">{{ item.subsNm }}</td>
              <td class="px-3 text-xs text-gray-600">{{ getLabel('svc_cd', item.svcCd) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { searchSubscriptions } from '../../api/subscriptionApi'
import { useCommonCodeLabel } from '../../composables/useCommonCodeLabel'

const emit = defineEmits(['close', 'select'])
const { getLabel } = useCommonCodeLabel(['svc_cd'])

const keyword = ref('')
const results = ref([])
const isSearching = ref(false)

const handleSearch = async () => {
  if (!keyword.value.trim()) return
  isSearching.value = true
  try {
    const page = await searchSubscriptions({ type: 'SUBS_ID', keyword: keyword.value.trim() })
    results.value = page.content || []
  } catch {
    results.value = []
  } finally {
    isSearching.value = false
  }
}

const handleSelect = (subsId) => {
  emit('select', subsId)
  emit('close')
}
</script>
