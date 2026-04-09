<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import AppSidebar from './AppSidebar.vue'

const props = defineProps({
  title: String,
  subtitle: String,
  eyebrow: {
    type: String,
    default: 'TraceMall',
  },
  layout: {
    type: String,
    default: 'sidebar',
  },
  showPageHead: {
    type: Boolean,
    default: true,
  },
  contentClass: {
    type: String,
    default: '',
  },
})

const collapsed = ref(false)
const navPhase = ref('idle')
let phaseTimer = null

onMounted(() => {
  if (props.layout !== 'sidebar') return
  collapsed.value = localStorage.getItem('tracemall_sidebar_collapsed') === '1'
})

onBeforeUnmount(() => {
  if (phaseTimer) {
    clearTimeout(phaseTimer)
  }
})

watch(collapsed, (value) => {
  if (props.layout !== 'sidebar') return
  localStorage.setItem('tracemall_sidebar_collapsed', value ? '1' : '0')
})

function markPhaseIdle() {
  if (phaseTimer) {
    clearTimeout(phaseTimer)
  }
  phaseTimer = setTimeout(() => {
    navPhase.value = 'idle'
  }, 320)
}

function toggleSidebar() {
  collapsed.value = !collapsed.value
  navPhase.value = collapsed.value ? 'collapsing' : 'expanding'
  markPhaseIdle()
}
</script>

<template>
  <div :class="['shell', `shell-${props.layout}`, { collapsed: props.layout === 'sidebar' && collapsed }]">
    <template v-if="props.layout === 'sidebar'">
      <AppSidebar :collapsed="collapsed" :phase="navPhase" @toggle="toggleSidebar" />
      <main :class="['content', props.contentClass]">
        <header class="page-head" v-if="props.showPageHead">
          <div class="page-head-copy">
            <div class="page-eyebrow">{{ eyebrow }}</div>
            <h1>{{ title }}</h1>
            <p v-if="subtitle">{{ subtitle }}</p>
          </div>
          <slot name="actions" />
        </header>
        <slot />
      </main>
    </template>

    <template v-else>
      <slot name="topbar" />
      <main :class="['content', 'content-topbar', props.contentClass]">
        <header class="page-head" v-if="props.showPageHead">
          <div class="page-head-copy">
            <div class="page-eyebrow">{{ eyebrow }}</div>
            <h1>{{ title }}</h1>
            <p v-if="subtitle">{{ subtitle }}</p>
          </div>
          <slot name="actions" />
        </header>
        <slot />
      </main>
    </template>
  </div>
</template>
