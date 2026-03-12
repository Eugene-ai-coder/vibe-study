<template>
  <div class="h-9 bg-gray-100 border-b border-gray-200 flex items-end px-2 gap-0.5 overflow-x-auto flex-shrink-0">
    <div
      v-for="tab in tabStore.tabs"
      :key="tab.path"
      @click="handleTabClick(tab)"
      :class="[
        'group flex items-center gap-1 px-3 h-8 text-xs rounded-t-lg cursor-pointer border border-b-0 transition-colors select-none',
        route.path === tab.path
          ? 'bg-white text-blue-600 border-gray-200 font-medium'
          : 'bg-gray-50 text-gray-500 border-transparent hover:bg-gray-200/60 hover:text-gray-700'
      ]"
    >
      <span class="whitespace-nowrap">{{ tab.label }}</span>
      <button
        v-if="tab.closable"
        @click.stop="handleTabClose(tab)"
        class="w-4 h-4 flex items-center justify-center rounded hover:bg-gray-300/60 text-gray-400 hover:text-gray-600 opacity-0 group-hover:opacity-100 transition-opacity"
      >
        &times;
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useTabStore } from '../../stores/tab'

const router = useRouter()
const route = useRoute()
const tabStore = useTabStore()

const handleTabClick = (tab) => {
  if (route.path === tab.path) return
  tabStore.skipNextConfirm = true
  router.push(tab.path)
}

const handleTabClose = (tab) => {
  const nextPath = tabStore.removeTab(tab.path)
  if (nextPath && route.path === tab.path) {
    tabStore.skipNextConfirm = true
    router.push(nextPath)
  }
}
</script>
