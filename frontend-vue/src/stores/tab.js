import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useTabStore = defineStore('tab', () => {
  const tabs = ref([
    { path: '/main', label: '대시보드', componentName: 'MainPage', closable: false }
  ])

  const cachedNames = computed(() => [...new Set(tabs.value.map(t => t.componentName))])

  const pendingConfirm = ref(null)
  const skipNextConfirm = ref(false)

  function addTab({ path, label, componentName }) {
    const existing = tabs.value.find(t => t.path === path)
    if (existing) return true
    tabs.value.push({ path, label, componentName, closable: true })
    return false
  }

  function removeTab(path) {
    const idx = tabs.value.findIndex(t => t.path === path)
    if (idx === -1 || !tabs.value[idx].closable) return null
    tabs.value.splice(idx, 1)
    return tabs.value[Math.min(idx, tabs.value.length - 1)]?.path || '/main'
  }

  function hasTab(path) {
    return tabs.value.some(t => t.path === path)
  }

  function confirmNavigation(message) {
    return new Promise(resolve => {
      pendingConfirm.value = { message, resolve }
    })
  }

  function resolveConfirm(confirmed) {
    if (pendingConfirm.value) {
      pendingConfirm.value.resolve(confirmed)
      pendingConfirm.value = null
    }
  }

  return { tabs, cachedNames, pendingConfirm, skipNextConfirm, addTab, removeTab, hasTab, confirmNavigation, resolveConfirm }
})
