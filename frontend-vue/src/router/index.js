import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../pages/LoginPage.vue'), meta: { public: true } },
  { path: '/', redirect: '/main' },
  { path: '/main', name: 'Main', component: () => import('../pages/MainPage.vue'), meta: { label: '대시보드' } },
  { path: '/users', name: 'Users', component: () => import('../pages/UserPage.vue'), meta: { label: '사용자관리' } },
  { path: '/subscriptions', name: 'Subscriptions', component: () => import('../pages/SubscriptionPage.vue'), meta: { label: '가입관리' } },
  { path: '/bill-std', name: 'BillStd', component: () => import('../pages/BillStdPage.vue'), meta: { label: '과금기준' } },
  { path: '/bill-std-field-config', name: 'BillStdFieldConfig', component: () => import('../pages/BillStdFieldConfigPage.vue'), meta: { label: '과금기준 필드설정' } },
  { path: '/subscription-main', name: 'SubscriptionMain', component: () => import('../pages/SubscriptionMainPage.vue'), meta: { label: '가입총괄' } },
  { path: '/study-logs', name: 'StudyLogs', component: () => import('../pages/StudyLogPage.vue'), meta: { label: '학습로그' } },
  { path: '/code', name: 'CommonCode', component: () => import('../pages/CommonCodePage.vue'), meta: { label: '공통코드' } },
  { path: '/qna', name: 'Qna', component: () => import('../pages/QnaPage.vue'), meta: { label: 'Q&A' } },
  { path: '/qna/new', name: 'QnaNew', component: () => import('../pages/QnaDetailPage.vue'), meta: { hidden: true } },
  { path: '/qna/:id', name: 'QnaDetail', component: () => import('../pages/QnaDetailPage.vue'), meta: { hidden: true } },
  { path: '/subs-bill-std', name: 'SubsBillStdList', component: () => import('../pages/SubsBillStdListPage.vue'), meta: { label: '가입별 과금기준' } },
  { path: '/special-subscription', name: 'SpecialSubscription', component: () => import('../pages/SpecialSubscriptionPage.vue'), meta: { label: '특수가입' } },
  { path: '/menu', name: 'Menu', component: () => import('../pages/MenuPage.vue'), meta: { label: '메뉴관리' } },
  { path: '/role', name: 'Role', component: () => import('../pages/RolePage.vue'), meta: { label: '역할관리' } },
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
