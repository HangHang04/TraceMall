<script setup>
import NavIcon from '@/components/ui/NavIcon.vue'

defineProps({
  events: {
    type: Array,
    default: () => [],
  },
})

function formatPayload(payload) {
  if (!payload) return '无附加数据'
  if (typeof payload !== 'string') {
    return JSON.stringify(payload)
  }
  return payload
}
</script>

<template>
  <section class="panel">
    <div class="timeline-head">
      <div>
        <h3 class="panel-title">
          <span class="title-icon"><NavIcon name="chain" /></span>
          <span>溯源事件链</span>
        </h3>
        <p class="panel-copy">采收、质检、仓储和运输等关键事件按时间排序展示，便于快速核对批次链路。</p>
      </div>
      <span class="chip">事件 {{ events.length }}</span>
    </div>

    <ul class="timeline" v-if="events.length">
      <li v-for="(event, index) in events" :key="event.id || `${event.eventType}-${index}`">
        <div class="timeline-dot" />
        <div class="timeline-card">
          <div class="timeline-card-head">
            <p class="timeline-title">{{ event.eventType }}</p>
            <span class="chip" v-if="event.anchorId">{{ event.anchorId }}</span>
          </div>
          <p class="timeline-meta">{{ event.location || '位置未填写' }} · {{ event.eventTime || '时间未记录' }}</p>
          <p class="timeline-payload">{{ formatPayload(event.payloadJson) }}</p>
        </div>
      </li>
    </ul>

    <p v-else class="panel-copy">当前批次还没有可展示的溯源事件。</p>
  </section>
</template>
