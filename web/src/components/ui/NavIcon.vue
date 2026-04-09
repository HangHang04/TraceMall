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

  <svg
    v-else-if="name === 'scan'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M7 4H5a1 1 0 0 0-1 1v2M17 4h2a1 1 0 0 1 1 1v2M7 20H5a1 1 0 0 1-1-1v-2M17 20h2a1 1 0 0 0 1-1v-2" />
    <path d="M8 12h8M8 9.2h5.2M8 14.8h6.4" />
  </svg>

  <svg
    v-else-if="name === 'camera'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M4 8.5a2 2 0 0 1 2-2h2.1l1.2-1.8h5.4l1.2 1.8H18a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2z" />
    <circle cx="12" cy="12.5" r="3.6" />
  </svg>

  <svg
    v-else-if="name === 'search'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <circle cx="11" cy="11" r="6.5" />
    <path d="m16 16 4.2 4.2" />
  </svg>

  <svg
    v-else-if="name === 'calendar'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <rect x="3.5" y="5.5" width="17" height="15" rx="2.4" />
    <path d="M8 3.8v3.4M16 3.8v3.4M3.5 9.5h17" />
  </svg>

  <svg
    v-else-if="name === 'bell'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M6.5 16.5h11l-1.3-1.7V11a4.7 4.7 0 1 0-9.4 0v3.8z" />
    <path d="M10 18.5a2.2 2.2 0 0 0 4 0" />
  </svg>

  <svg
    v-else-if="name === 'help'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <circle cx="12" cy="12" r="8" />
    <path d="M9.7 9.2a2.7 2.7 0 1 1 3.9 2.4c-.9.5-1.6 1.2-1.6 2.4" />
    <circle cx="12" cy="17.2" r=".8" fill="currentColor" stroke="none" />
  </svg>

  <svg
    v-else-if="name === 'dashboard'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <rect x="4" y="4" width="7" height="7" rx="1.8" />
    <rect x="13" y="4" width="7" height="11" rx="1.8" />
    <rect x="4" y="13" width="7" height="7" rx="1.8" />
    <rect x="13" y="17" width="7" height="3" rx="1.5" />
  </svg>

  <svg
    v-else-if="name === 'insights'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M4 18h16" />
    <path d="m5.5 14.5 4.2-4.2 3.1 3.1 5.7-6.2" />
    <path d="M15.6 7.2h2.9V10" />
  </svg>

  <svg
    v-else-if="name === 'assistant'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <rect x="5" y="6" width="14" height="11" rx="4" />
    <path d="M9 6V4.4M15 6V4.4M9.3 11.2h.01M14.7 11.2h.01M9 14.2c1 .7 2 .9 3 .9s2-.2 3-.9" />
  </svg>

  <svg
    v-else-if="name === 'spark'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="m12 3 1.9 4.8L19 10l-5.1 2.1L12 17l-1.9-4.9L5 10l5.1-2.2L12 3Z" />
    <path d="M5 18.5 6.2 21 8 22.2 6.2 23.4 5 26l-1.2-2.6L2 22.2 3.8 21z" transform="translate(0 -4)" />
  </svg>

  <svg
    v-else-if="name === 'chain'"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    aria-hidden="true"
  >
    <path d="M9 8.5 6.5 11a3 3 0 0 0 4.2 4.2l2-2M15 15.5l2.5-2.5A3 3 0 0 0 13.3 8.8l-2 2" />
  </svg>

  <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9" aria-hidden="true">
    <circle cx="12" cy="12" r="7" />
  </svg>
</template>
