<script setup>
defineProps({
  label: String,
  value: [String, Number],
  trend: String,
  status: {
    type: String,
    default: 'normal',
  },
})
</script>

<template>
  <article class="metric-card">
    <div class="metric-top">
      <h4>{{ label }}</h4>
      <span class="chip">{{ status }}</span>
    </div>
    <p class="metric-value">{{ value }}</p>
    <p class="metric-trend">{{ trend }}</p>
  </article>
</template>