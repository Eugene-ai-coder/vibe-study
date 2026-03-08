<template>
  <div class="flex flex-col min-h-screen">
    <!-- 헤더 -->
    <header class="h-14 bg-white border-b border-gray-200 flex items-center justify-between px-6 flex-shrink-0 sticky top-0 z-10">
      <span class="font-bold text-gray-800">종량가입관리 시스템</span>
      <div class="flex items-center gap-3">
        <span class="text-sm text-gray-600">
          <strong class="text-gray-800">{{ auth.user?.nickname }}</strong>님
        </span>
        <button
          @click="handleLogout"
          class="text-sm text-gray-500 hover:text-red-600 border border-gray-300 hover:border-red-400 px-3 h-8 rounded-lg transition-colors"
        >
          로그아웃
        </button>
      </div>
    </header>

    <!-- 바디 -->
    <div class="flex flex-1 overflow-hidden">
      <Sidebar />
      <main class="flex-1 overflow-auto bg-[#F8FAFC] p-6">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import Sidebar from '../main/Sidebar.vue'

const auth = useAuthStore()
const router = useRouter()

const handleLogout = async () => {
  await auth.logout()
  router.push('/login')
}
</script>
