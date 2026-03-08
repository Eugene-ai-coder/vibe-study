import { defineStore } from 'pinia'
import { ref } from 'vue'
import { menuApi } from '../api/menuApi'

export const useMenuStore = defineStore('menu', () => {
  const menuTree = ref([])

  async function fetchMyMenu() {
    try {
      menuTree.value = await menuApi.getMyTree()
    } catch {
      menuTree.value = []
    }
  }

  function clear() {
    menuTree.value = []
  }

  return { menuTree, fetchMyMenu, clear }
})
