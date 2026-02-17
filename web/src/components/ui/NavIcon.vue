<script setup>
defineProps({
  name: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <svg v-if="name === 'home'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9" aria-hidden="true">
    <path d="M3 10.5 12 3l9 7.5" />
    <path d="M5.5 9.8V20h13V9.8" />
    <path d="M10 20v-5.5h4V20" />
  </svg>

  <svg v-else-if="name === 'cart'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9" aria-hidden="true">
    <circle cx="9" cy="19" r="1.6" />
    <circle cx="17" cy="19" r="1.6" />
    <path d="M3 5h2.5l2 9.2a1.2 1.2 0 0 0 1.2.9h8.8a1.2 1.2 0 0 0 1.2-1l1.2-6.6H7" />
  </svg>

  <svg
    v-else-if="name === 'orders'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <rect x="4" y="3.5" width="16" height="17" rx="2.2" />
    <path d="M8 8.5h8M8 12h8M8 15.5h5" />
  </svg>

  <svg
    v-else-if="name === 'merchant'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M4 20V10M10 20V4M16 20v-7M20 20v-4" />
    <path d="M3 20h18" />
  </svg>

  <svg
    v-else-if="name === 'regulator'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="m12 3 7 3.2v5.7c0 4.5-2.8 7.9-7 9.9-4.2-2-7-5.4-7-9.9V6.2L12 3Z" />
    <path d="m9.4 12.2 1.8 1.8 3.6-3.8" />
  </svg>
</template>
