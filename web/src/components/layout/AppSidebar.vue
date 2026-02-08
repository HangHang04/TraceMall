<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const navItems = computed(() => {
  if (auth.role === 'MERCHANT') {
    return [{ key: 'merchant', label: '批次管理', path: '/merchant' }]
  }
  if (auth.role === 'REGULATOR') {
    return [{ key: 'regulator', label: '监管视图', path: '/regulator' }]
  }
  return [
    { key: 'dashboard', label: '商城主页', path: '/dashboard' },
    { key: 'cart', label: '购物车', path: '/cart' },
    { key: 'orders', label: '我的订单', path: '/orders' },
  ]
})

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <aside class="sidebar">
    <div class="brand">
      <div class="brand-logo">TM</div>
      <div>
        <div class="brand-title">TraceMall</div>
        <div class="brand-subtitle">可溯源水果商城</div>
      </div>
    </div>

    <nav class="menu">
      <button v-for="item in navItems" :key="item.key" class="menu-item" @click="router.push(item.path)">
        {{ item.label }}
      </button>
    </nav>

    <div class="sidebar-user">
      <div class="chip role">{{ auth.role || '-' }}</div>
      <div class="username">{{ auth.username || 'Guest' }}</div>
      <button class="btn btn-ghost" @click="logout">退出登录</button>
    </div>
  </aside>
</template>