<script setup>
import AppSidebar from './AppSidebar.vue'

defineProps({
  title: String,
  subtitle: String,
})
</script>

<template>
  <div class="shell">
    <AppSidebar />
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