<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center">
    <div class="fixed inset-0 bg-black/30" @click="$emit('close')"></div>
    <div class="relative bg-white rounded-lg shadow-xl w-[480px] max-h-[80vh] flex flex-col">
      <div class="flex items-center justify-between p-4 border-b border-gray-200">
        <h3 class="text-sm font-semibold text-gray-700">사용자 검색</h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-lg">&times;</button>
      </div>
      <div class="p-4 space-y-3">
        <div class="flex gap-2">
          <input v-model="searchKeyword" @keydown.enter="handleSearch" placeholder="아이디 또는 닉네임"
            class="flex-1 h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          <button @click="handleSearch"
            class="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">검색</button>
        </div>
        <div class="max-h-[300px] overflow-y-auto border border-gray-200 rounded">
          <table class="w-full text-sm">
            <thead class="bg-gray-50 sticky top-0">
              <tr class="border-b border-gray-300">
                <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">아이디</th>
                <th class="px-3 py-1.5 text-left text-xs font-medium text-gray-500">닉네임</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="users.length === 0">
                <td colspan="2" class="px-3 py-4 text-center text-xs text-gray-400">데이터가 없습니다.</td>
              </tr>
              <tr v-for="user in users" :key="user.userId" @click="handleSelect(user)"
                class="h-7 cursor-pointer border-b border-gray-100 hover:bg-blue-50">
                <td class="px-3 text-xs">{{ user.userId }}</td>
                <td class="px-3 text-xs">{{ user.nickname }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getUsersPage } from '../../api/authApi'

defineProps({ visible: { type: Boolean, default: false } })
const emit = defineEmits(['select', 'close'])

const searchKeyword = ref('')
const users = ref([])

const handleSearch = async () => {
  try {
    const data = await getUsersPage({ keyword: searchKeyword.value.trim(), page: 0, size: 50 })
    users.value = data.content || []
  } catch {
    users.value = []
  }
}

const handleSelect = (user) => {
  emit('select', user)
  emit('close')
}
</script>
