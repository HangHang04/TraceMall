<script setup>
import NavIcon from '@/components/ui/NavIcon.vue'

defineProps({
  events: {
    type: Array,
    default: () => [],
  },
})
</script>

<template>
  <section class="panel">
    <h3 class="panel-title">
      <span class="title-icon"><NavIcon name="verify" /></span>
      <span>溯源事件链</span>
    </h3>
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
