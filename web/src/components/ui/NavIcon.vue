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

  <svg
    v-else-if="name === 'filter'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M4 5h16l-6.5 7.3V19l-3-1.8v-4.9z" />
  </svg>

  <svg
    v-else-if="name === 'refresh'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M20 6v5h-5" />
    <path d="M4 18v-5h5" />
    <path d="M6.8 9.2A7 7 0 0 1 18.4 7L20 11" />
    <path d="M17.2 14.8A7 7 0 0 1 5.6 17L4 13" />
  </svg>

  <svg
    v-else-if="name === 'verify'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <rect x="4" y="4" width="16" height="16" rx="3" />
    <path d="m8.7 12 2.2 2.2 4.4-4.6" />
  </svg>

  <svg
    v-else-if="name === 'plus'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M12 5v14M5 12h14" />
  </svg>

  <svg
    v-else-if="name === 'price'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M12 4v16" />
    <path d="M16.5 7.5c0-1.7-2-2.8-4.5-2.8S7.5 5.8 7.5 7.5 9.5 10.3 12 10.3s4.5 1.1 4.5 2.8-2 2.8-4.5 2.8-4.5-1.1-4.5-2.8" />
  </svg>

  <svg
    v-else-if="name === 'package'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="m12 3 8 4.2v9.6L12 21l-8-4.2V7.2L12 3Z" />
    <path d="m4.4 7.2 7.6 4 7.6-4" />
    <path d="M12 11.2V21" />
  </svg>

  <svg
    v-else-if="name === 'status'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <circle cx="12" cy="12" r="8" />
    <path d="M12 8v4l2.6 2.6" />
  </svg>

  <svg
    v-else-if="name === 'logout'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M10 4H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h4" />
    <path d="M14 16l4-4-4-4" />
    <path d="M9 12h9" />
  </svg>

  <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9" aria-hidden="true">
    <circle cx="12" cy="12" r="7" />
  </svg>
</template>
