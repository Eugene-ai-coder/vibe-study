import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useTabStore } from '../stores/tab'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../pages/LoginPage.vue'), meta: { public: true } },
  { path: '/', redirect: '/main' },
  { path: '/main', name: 'Main', component: () => import('../pages/MainPage.vue'), meta: { label: '대시보드', componentName: 'MainPage' } },
  { path: '/users', name: 'Users', component: () => import('../pages/UserPage.vue'), meta: { label: '사용자관리', componentName: 'UserPage' } },
  { path: '/subscriptions', name: 'Subscriptions', component: () => import('../pages/SubscriptionPage.vue'), meta: { label: '가입관리', componentName: 'SubscriptionPage' } },
  { path: '/bill-std', name: 'BillStd', component: () => import('../pages/BillStdPage.vue'), meta: { label: '과금기준', componentName: 'BillStdPage' } },
  { path: '/bill-std-field-config', name: 'BillStdFieldConfig', component: () => import('../pages/BillStdFieldConfigPage.vue'), meta: { label: '과금기준 필드설정', componentName: 'BillStdFieldConfigPage' } },
  { path: '/subscription-main', name: 'SubscriptionMain', component: () => import('../pages/SubscriptionMainPage.vue'), meta: { label: '가입총괄', componentName: 'SubscriptionMainPage' } },
  { path: '/study-logs', name: 'StudyLogs', component: () => import('../pages/StudyLogPage.vue'), meta: { label: '학습로그', componentName: 'StudyLogPage' } },
  { path: '/code', name: 'CommonCode', component: () => import('../pages/CommonCodePage.vue'), meta: { label: '공통코드', componentName: 'CommonCodePage' } },
  { path: '/qna', name: 'Qna', component: () => import('../pages/QnaPage.vue'), meta: { label: 'Q&A', componentName: 'QnaPage' } },
  { path: '/qna/new', name: 'QnaNew', component: () => import('../pages/QnaDetailPage.vue'), meta: { label: 'Q&A 작성', componentName: 'QnaDetailPage', hidden: true } },
  { path: '/qna/:id', name: 'QnaDetail', component: () => import('../pages/QnaDetailPage.vue'), meta: { label: 'Q&A 상세', componentName: 'QnaDetailPage', hidden: true } },
  { path: '/subs-bill-std', name: 'SubsBillStdList', component: () => import('../pages/SubsBillStdListPage.vue'), meta: { label: '가입별 과금기준', componentName: 'SubsBillStdListPage' } },
  { path: '/special-subscription', name: 'SpecialSubscription', component: () => import('../pages/SpecialSubscriptionPage.vue'), meta: { label: '특수가입', componentName: 'SpecialSubscriptionPage' } },
  { path: '/menu', name: 'Menu', component: () => import('../pages/MenuPage.vue'), meta: { label: '메뉴관리', componentName: 'MenuPage' } },
  { path: '/role', name: 'Role', component: () => import('../pages/RolePage.vue'), meta: { label: '역할관리', componentName: 'RolePage' } },
  { path: '/todos', name: 'Todo', component: () => import('../pages/TodoPage.vue'), meta: { label: '내 할일', componentName: 'TodoPage' } },
  { path: '/wf/process-def', name: 'WfProcessDef', component: () => import('../pages/WfProcessDefPage.vue'), meta: { label: '워크플로우 정의', componentName: 'WfProcessDefPage' } },
  { path: '/wf/tasks', name: 'WfTask', component: () => import('../pages/WfTaskPage.vue'), meta: { label: '내 할일', componentName: 'WfTaskPage' } },
  { path: '/apprv-req', name: 'ApprvReq', component: () => import('../pages/ApprvReqPage.vue'), meta: { label: '결재관리', componentName: 'ApprvReqPage' } },
  { path: '/subs-mth-bill-qty', name: 'SubsMthBillQty', component: () => import('../pages/SubsMthBillQtyPage.vue'), meta: { label: '가입별 월별과금량', componentName: 'SubsMthBillQtyPage' } },
  { path: '/spcl-subs-mth-bill-qty', name: 'SpclSubsMthBillQty', component: () => import('../pages/SpclSubsMthBillQtyPage.vue'), meta: { label: '특수가입별 월별과금량', componentName: 'SpclSubsMthBillQtyPage' } },
  { path: '/spcl-subs-mth-bill-elem', name: 'SpclSubsMthBillElem', component: () => import('../pages/SpclSubsMthBillElemPage.vue'), meta: { label: '특수가입별 월별빌링요소', componentName: 'SpclSubsMthBillElemPage' } },
  { path: '/bill-std-req', name: 'BillStdReq', component: () => import('../pages/BillStdReqPage.vue'), meta: { label: '과금기준신청', componentName: 'BillStdReqPage' } },
  { path: '/subs-bill-std-req', name: 'SubsBillStdReqList', component: () => import('../pages/SubsBillStdReqListPage.vue'), meta: { label: '과금기준신청목록', componentName: 'SubsBillStdReqListPage' } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to, from) => {
  if (to.meta.public) return true

  const auth = useAuthStore()
  if (auth.loading) await auth.init()
  if (!auth.user) return '/login'

  const tabStore = useTabStore()
  if (
    tabStore.hasTab(to.path) &&
    from.path !== to.path &&
    Object.keys(to.query).length > 0 &&
    !tabStore.skipNextConfirm
  ) {
    const label = to.meta.label || to.path
    const confirmed = await tabStore.confirmNavigation(
      `${label} 탭이 이미 열려있습니다. 이동하시겠습니까?`
    )
    if (!confirmed) return false
  }
  tabStore.skipNextConfirm = false
  return true
})

router.afterEach((to) => {
  if (to.meta.public) return

  const tabStore = useTabStore()
  const label = to.meta.label || to.path
  const componentName = to.meta.componentName
  if (componentName) {
    tabStore.addTab({ path: to.path, label, componentName })
  }
})

export default router
