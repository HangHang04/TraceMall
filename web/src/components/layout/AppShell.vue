<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import AppSidebar from './AppSidebar.vue'

defineProps({
  title: String,
  subtitle: String,
})

const collapsed = ref(false)
const navPhase = ref('idle')
let phaseTimer = null

onMounted(() => {
  collapsed.value = localStorage.getItem('tracemall_sidebar_collapsed') === '1'
})

onBeforeUnmount(() => {
  if (phaseTimer) {
    clearTimeout(phaseTimer)
  }
})

watch(collapsed, (value) => {
  localStorage.setItem('tracemall_sidebar_collapsed', value ? '1' : '0')
})

function markPhaseIdle() {
  if (phaseTimer) {
    clearTimeout(phaseTimer)
  }
  phaseTimer = setTimeout(() => {
    navPhase.value = 'idle'
  }, 420)
}

function toggleSidebar() {
  if (collapsed.value) {
    collapsed.value = false
    navPhase.value = 'expanding'
    markPhaseIdle()
    return
  }

  navPhase.value = 'collapsing'
  requestAnimationFrame(() => {
    collapsed.value = true
  })
  markPhaseIdle()
}
</script>

<template>
  <div :class="['shell', { collapsed }]">
    <AppSidebar :collapsed="collapsed" :phase="navPhase" @toggle="toggleSidebar" />
    <main class="content">
      <header class="page-head">
        <h1>{{ title }}</h1>
        <p>{{ subtitle }}</p>
        <slot name="actions" />
      </header>
      <slot />
    </main>
  </div>
</template>
