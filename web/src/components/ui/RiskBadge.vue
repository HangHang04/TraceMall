<script setup>
defineProps({
  level: {
    type: String,
    default: 'LOW',
  },
})
</script>

<template>
  <span :class="['risk-badge', level.toLowerCase()]">{{ level }}</span>
</template>