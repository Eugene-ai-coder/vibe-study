<template>
  <div class="min-h-screen bg-[#F8FAFC] flex items-center justify-center">
    <div class="bg-white rounded-lg shadow p-8 w-96">
      <h1 class="text-xl font-bold text-gray-800 mb-6 text-center">종량가입관리 시스템</h1>

      <div class="space-y-4">
        <input
          type="text"
          placeholder="아이디"
          v-model="userId"
          @keydown.enter="handleLogin"
          class="w-full h-8 px-3 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-blue-500"
        />
        <input
          type="password"
          placeholder="비밀번호"
          v-model="password"
          @keydown.enter="handleLogin"
          class="w-full h-8 px-3 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-blue-500"
        />

        <label class="flex items-center gap-2 text-sm text-gray-600 cursor-pointer">
          <input type="checkbox" v-model="rememberMe" />
          아이디 저장
        </label>

        <p v-if="errorMsg" class="text-sm text-red-600">{{ errorMsg }}</p>

        <button
          @click="handleLogin"
          :disabled="loading"
          class="w-full h-8 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded-lg transition-colors disabled:opacity-60"
        >
          {{ loading ? '로그인 중...' : '로그인' }}
        </button>

        <p class="text-center text-xs text-gray-400 cursor-default select-none">
          계정 찾기
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()

const userId = ref('')
const password = ref('')
const rememberMe = ref(false)
const errorMsg = ref('')
const loading = ref(false)

onMounted(() => {
  try {
    const saved = localStorage.getItem('savedUserId')
    if (saved) { userId.value = saved; rememberMe.value = true }
  } catch { /* localStorage 접근 불가 시 무시 */ }
})

const handleLogin = async () => {
  if (!userId.value.trim() || !password.value.trim()) {
    errorMsg.value = '아이디와 비밀번호를 입력해 주세요.'
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    await auth.login(userId.value, password.value)
    if (rememberMe.value) localStorage.setItem('savedUserId', userId.value)
    else localStorage.removeItem('savedUserId')
    router.push('/main')
  } catch (err) {
    const status = err?.response?.status
    errorMsg.value = status === 403
      ? '사용이 제한된 계정입니다.'
      : '아이디 또는 비밀번호가 일치하지 않습니다.'
  } finally {
    loading.value = false
  }
}
</script>
