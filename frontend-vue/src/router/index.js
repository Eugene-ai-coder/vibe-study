import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../pages/LoginPage.vue'), meta: { public: true } },
  { path: '/', redirect: '/main' },
  { path: '/main', name: 'Main', component: () => import('../pages/MainPage.vue') },
  { path: '/users', name: 'Users', component: () => import('../pages/UserPage.vue') },
  { path: '/subscriptions', name: 'Subscriptions', component: () => import('../pages/SubscriptionPage.vue') },
  { path: '/bill-std', name: 'BillStd', component: () => import('../pages/BillStdPage.vue') },
  { path: '/subscription-main', name: 'SubscriptionMain', component: () => import('../pages/SubscriptionMainPage.vue') },
  { path: '/study-logs', name: 'StudyLogs', component: () => import('../pages/StudyLogPage.vue') },
  { path: '/code', name: 'CommonCode', component: () => import('../pages/CommonCodePage.vue') },
  { path: '/qna', name: 'Qna', component: () => import('../pages/QnaPage.vue') },
  { path: '/qna/new', name: 'QnaNew', component: () => import('../pages/QnaDetailPage.vue') },
  { path: '/qna/:id', name: 'QnaDetail', component: () => import('../pages/QnaDetailPage.vue') },
  { path: '/special-subscription', name: 'SpecialSubscription', component: () => import('../pages/SpecialSubscriptionPage.vue') },
  { path: '/menu', name: 'Menu', component: () => import('../pages/MenuPage.vue') },
  { path: '/role', name: 'Role', component: () => import('../pages/RolePage.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  if (to.meta.public) return true

  const auth = useAuthStore()
  if (auth.loading) await auth.init()
  if (!auth.user) return '/login'
  return true
})

export default router
