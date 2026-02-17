<script setup>
import NavIcon from '@/components/ui/NavIcon.vue'

defineProps({
  label: String,
  value: [String, Number],
  trend: String,
  icon: {
    type: String,
    default: '',
  },
  status: {
    type: String,
    default: 'normal',
  },
})
</script>

<template>
  <article class="metric-card">
    <div class="metric-top">
      <h4 class="metric-label">
        <span class="metric-icon" v-if="icon"><NavIcon :name="icon" /></span>
        <span>{{ label }}</span>
      </h4>
      <span class="chip">{{ status }}</span>
    </div>
    <p class="metric-value">{{ value }}</p>
    <p class="metric-trend">{{ trend }}</p>
  </article>
</template>
