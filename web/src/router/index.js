import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: () => import('@/views/HomeView.vue') },
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { auth: true, roles: ['CONSUMER'] },
    },
    {
      path: '/product/:id',
      name: 'product-detail',
      component: () => import('@/views/ProductDetailView.vue'),
      meta: { auth: true, roles: ['CONSUMER'] },
    },
    {
      path: '/cart',
      name: 'cart',
      component: () => import('@/views/CartView.vue'),
      meta: { auth: true, roles: ['CONSUMER'] },
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('@/views/OrdersView.vue'),
      meta: { auth: true, roles: ['CONSUMER'] },
    },
    {
      path: '/merchant',
      name: 'merchant',
      component: () => import('@/views/MerchantView.vue'),
      meta: { auth: true, roles: ['MERCHANT'] },
    },
    {
      path: '/regulator',
      name: 'regulator',
      component: () => import('@/views/RegulatorView.vue'),
      meta: { auth: true, roles: ['REGULATOR'] },
    },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.auth) {
    return true
  }
  if (!auth.token) {
    return '/login'
  }
  const roles = to.meta.roles || []
  if (roles.length > 0 && !roles.includes(auth.role)) {
    if (auth.role === 'MERCHANT') {
      return '/merchant'
    }
    if (auth.role === 'REGULATOR') {
      return '/regulator'
    }
    return '/dashboard'
  }
  return true
})

export default router
