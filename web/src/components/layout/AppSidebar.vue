<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavIcon from '@/components/ui/NavIcon.vue'
import { useAuthStore } from '@/stores/auth'
import { toRoleLabel } from '@/utils/status'

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
  phase: {
    type: String,
    default: 'idle',
  },
})

const emit = defineEmits(['toggle'])

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const navItems = computed(() => {
  if (auth.role === 'MERCHANT') {
    return [{ key: 'merchant', icon: 'merchant', label: '商家工作台', path: '/merchant' }]
  }
  if (auth.role === 'REGULATOR') {
    return [{ key: 'regulator', icon: 'regulator', label: '监管控制台', path: '/regulator' }]
  }
  return [
    { key: 'dashboard', icon: 'home', label: '可信消费首页', path: '/dashboard' },
    { key: 'scan', icon: 'scan', label: '扫码验真', path: '/trace/scan' },
    { key: 'cart', icon: 'cart', label: '购物车', path: '/cart' },
    { key: 'orders', icon: 'orders', label: '订单', path: '/orders' },
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
  <aside
    :class="[
      'sidebar',
      {
        collapsed: props.collapsed,
        'phase-collapsing': props.phase === 'collapsing',
        'phase-expanding': props.phase === 'expanding',
      },
    ]"
  >
    <div class="sidebar-top">
      <button class="brand" @click="emit('toggle')" :title="props.collapsed ? '展开导航栏' : '收起导航栏'">
        <div class="brand-logo">T</div>
        <div class="brand-text">
          <div class="brand-title">TraceMall</div>
          <div class="brand-subtitle">可信水果流通</div>
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
      <button class="btn btn-ghost logout-btn" @click="logout">
        <span class="btn-icon"><NavIcon name="logout" /></span>
        <span class="btn-text">{{ props.collapsed ? '退出' : '退出登录' }}</span>
      </button>
    </div>
  </aside>
</template>
