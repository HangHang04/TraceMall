<script setup>
import { computed } from 'vue'
import RiskBadge from './RiskBadge.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import { toVerifyReasonLabel, toVerifyStatusLabel } from '@/utils/status'

const props = defineProps({
  result: {
    type: Object,
    default: null,
  },
})

const panelState = computed(() => {
  const status = String(props.result?.status || '').toLowerCase()
  if (!status) return 'state-pass'
  if (status === 'fail') return 'state-fail'
  if (status === 'suspect' || status === 'high_risk') return `state-${status}`
  return 'state-pass'
})
</script>

<template>
  <section :class="['panel', 'verify-result-panel', panelState]" v-if="result">
    <div class="verify-result-head">
      <div>
        <h3 class="panel-title">
          <span class="title-icon"><NavIcon name="verify" /></span>
          <span>验真结果</span>
        </h3>
        <p class="panel-copy">系统已完成二维码格式、签名、链上锚点和近期扫码风险的综合校验。</p>
      </div>
      <RiskBadge :level="result.riskLevel || 'LOW'" />
    </div>

    <div class="verify-grid">
      <article class="verify-item">
        <span class="verify-item-label">校验状态</span>
        <strong>{{ toVerifyStatusLabel(result.status) }}</strong>
      </article>
      <article class="verify-item">
        <span class="verify-item-label">原因说明</span>
        <strong>{{ toVerifyReasonLabel(result.reason) }}</strong>
      </article>
      <article class="verify-item">
        <span class="verify-item-label">链上锚点</span>
        <strong>{{ result.anchorMatched ? '匹配成功' : '未匹配' }}</strong>
      </article>
      <article class="verify-item">
        <span class="verify-item-label">风险等级</span>
        <strong>{{ result.riskLevel || 'LOW' }}</strong>
      </article>
    </div>

    <div class="verify-summary-grid" v-if="result.batchSummary">
      <div class="verify-summary-card">
        <span class="verify-item-label">溯源码</span>
        <strong>{{ result.batchSummary.traceId }}</strong>
      </div>
      <div class="verify-summary-card">
        <span class="verify-item-label">批次号</span>
        <strong>{{ result.batchSummary.batchNo }}</strong>
      </div>
      <div class="verify-summary-card">
        <span class="verify-item-label">商品</span>
        <strong>{{ result.batchSummary.fruitName }}</strong>
      </div>
      <div class="verify-summary-card">
        <span class="verify-item-label">来源商家</span>
        <strong>{{ result.batchSummary.shopName }}</strong>
      </div>
    </div>
  </section>
</template>
