<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import NavIcon from '@/components/ui/NavIcon.vue'
import { toRoleLabel } from '@/utils/status'

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['toggle'])

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const navItems = computed(() => {
  if (auth.role === 'MERCHANT') {
    return [{ key: 'merchant', icon: 'merchant', label: '商家看板', path: '/merchant' }]
  }
  if (auth.role === 'REGULATOR') {
    return [{ key: 'regulator', icon: 'regulator', label: '监管视图', path: '/regulator' }]
  }
  return [
    { key: 'dashboard', icon: 'home', label: '商城主页', path: '/dashboard' },
    { key: 'cart', icon: 'cart', label: '购物车', path: '/cart' },
    { key: 'orders', icon: 'orders', label: '我的订单', path: '/orders' },
  ]
})

function isActive(path) {
  return route.path === path || route.path.startsWith(`${path}/`)
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <aside :class="['sidebar', { collapsed: props.collapsed }]">
    <div class="sidebar-top">
      <button class="brand" @click="emit('toggle')" :title="props.collapsed ? '展开导航栏' : '收起导航栏'">
        <div class="brand-logo">T</div>
        <div class="brand-text">
          <div class="brand-title">Trace Mall</div>
          <div class="brand-subtitle">可溯源水果商城</div>
        </div>
      </button>
    </div>

    <nav class="menu">
      <button
        v-for="item in navItems"
        :key="item.key"
        :class="['menu-item', { active: isActive(item.path) }]"
        :title="item.label"
        @click="router.push(item.path)"
      >
        <span class="menu-icon"><NavIcon :name="item.icon" /></span>
        <span class="menu-label">{{ item.label }}</span>
      </button>
    </nav>

    <div :class="['sidebar-user', { compact: props.collapsed }]">
      <div class="chip role">{{ toRoleLabel(auth.role) }}</div>
      <div class="username">{{ auth.username || '访客' }}</div>
      <button class="btn btn-ghost logout-btn" @click="logout">{{ props.collapsed ? '↩' : '退出登录' }}</button>
    </div>
  </aside>
</template>
