<script setup>
import { onMounted, ref, watch } from 'vue'
import AppSidebar from './AppSidebar.vue'

defineProps({
  title: String,
  subtitle: String,
})

const collapsed = ref(false)

onMounted(() => {
  collapsed.value = localStorage.getItem('tracemall_sidebar_collapsed') === '1'
})

watch(collapsed, (value) => {
  localStorage.setItem('tracemall_sidebar_collapsed', value ? '1' : '0')
})

function toggleSidebar() {
  collapsed.value = !collapsed.value
}
</script>

<template>
  <div :class="['shell', { collapsed }]">
    <AppSidebar :collapsed="collapsed" @toggle="toggleSidebar" />
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
