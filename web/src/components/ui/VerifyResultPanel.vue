<script setup>
import RiskBadge from './RiskBadge.vue'

defineProps({
  result: {
    type: Object,
    default: null,
  },
})
</script>

<template>
  <section class="panel" v-if="result">
    <h3>验真结果</h3>
    <div class="verify-grid">
      <p><strong>状态：</strong>{{ result.status }}</p>
      <p><strong>原因：</strong>{{ result.reason }}</p>
      <p>
        <strong>风险：</strong>
        <RiskBadge :level="result.riskLevel || 'LOW'" />
      </p>
      <p><strong>锚点：</strong>{{ result.anchorMatched ? '匹配' : '未匹配' }}</p>
    </div>
  </section>
</template>