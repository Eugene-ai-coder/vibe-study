import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getMe, login as apiLogin, logout as apiLogout } from '../api/authApi'
import { useMenuStore } from './menu'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const loading = ref(true)

  async function init() {
    try {
      user.value = await getMe()
      if (user.value) {
        const menuStore = useMenuStore()
        await menuStore.fetchMyMenu()
      }
    } catch {
      user.value = null
    } finally {
      loading.value = false
    }
  }

  async function login(userId, password) {
    const userInfo = await apiLogin(userId, password)
    user.value = userInfo
    const menuStore = useMenuStore()
    await menuStore.fetchMyMenu()
    return userInfo
  }

  async function logout() {
    try { await apiLogout() } catch {}
    user.value = null
    const menuStore = useMenuStore()
    menuStore.clear()
  }

  return { user, loading, init, login, logout }
})
