<script setup>
defineProps({
  events: {
    type: Array,
    default: () => [],
  },
})
</script>

<template>
  <section class="panel">
    <h3>溯源事件链</h3>
    <ul class="timeline">
      <li v-for="(event, index) in events" :key="index">
        <div class="timeline-dot" />
        <div>
          <p class="timeline-title">{{ event.eventType }}</p>
          <p class="timeline-meta">{{ event.location }} · {{ event.eventTime }}</p>
        </div>
      </li>
    </ul>
  </section>
</template>
