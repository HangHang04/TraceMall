<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppShell from '@/components/layout/AppShell.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import TraceQrCard from '@/components/ui/TraceQrCard.vue'
import TraceTimeline from '@/components/ui/TraceTimeline.vue'
import { getFruit, traceDetail } from '@/api'
import { buildTraceVerifyUrl } from '@/utils/traceQr'
import { toBatchStatusLabel } from '@/utils/status'

const DEMO_TRACE_BY_FRUIT_ID = {
  1: 'TRACE-APPLE-001',
  2: 'TRACE-ORANGE-001',
  3: 'TRACE-GRAPE-001',
  4: 'TRACE-KIWI-001',
  5: 'TRACE-BLUEBERRY-001',
  6: 'TRACE-PEAR-001',
  7: 'TRACE-WOGAN-001',
  8: 'TRACE-GREEN-GRAPE-001',
  9: 'TRACE-STRAWBERRY-001',
  10: 'TRACE-BANANA-001',
}

const route = useRoute()
const router = useRouter()

const fruit = ref(null)
const traceId = ref('')
const trace = ref(null)
const error = ref('')
const loading = ref(false)

const qrValue = computed(() => {
  const summary = trace.value?.summary
  if (!summary?.traceId || !summary?.signature) return ''
  return buildTraceVerifyUrl({
    traceId: summary.traceId,
    signature: summary.signature,
  })
})

async function loadFruit() {
  fruit.value = await getFruit(route.params.id)
  if (!traceId.value) {
    traceId.value = DEMO_TRACE_BY_FRUIT_ID[route.params.id] || ''
  }
}

async function loadTrace() {
  if (!traceId.value) return
  loading.value = true
  error.value = ''
  try {
    trace.value = await traceDetail(traceId.value)
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadFruit()
  await loadTrace()
})
</script>

<template>
  <AppShell title="商品与批次详情" subtitle="在商品信息旁直接查看批次来源、二维码和事件链。">
    <template #actions>
      <button class="btn btn-ghost" @click="router.push('/dashboard')">
        <span class="btn-icon"><NavIcon name="home" /></span>
        <span>返回首页</span>
      </button>
    </template>

    <section class="detail-hero" v-if="fruit">
      <div class="detail-hero-copy">
        <span class="hero-tag">商品详情</span>
        <h2>{{ fruit.fruitName }}</h2>
        <p>{{ fruit.description }}</p>
        <div class="detail-tags">
          <span class="chip">{{ fruit.category }}</span>
          <span class="chip chip-soft">{{ fruit.origin }}</span>
          <span class="chip chip-soft">{{ fruit.shopName }}</span>
        </div>
      </div>

      <div class="detail-price-card">
        <span class="detail-price-label">当前售价</span>
        <strong>¥{{ fruit.unitPrice }}</strong>
        <span class="muted">计量单位：{{ fruit.unit }}</span>
      </div>
    </section>

    <div class="detail-grid">
      <section class="panel">
        <div class="workspace-head">
          <div>
            <h3 class="panel-title">
              <span class="title-icon"><NavIcon name="scan" /></span>
              <span>查询批次信息</span>
            </h3>
            <p class="panel-copy">前十个种子商品会自动匹配一条示例批次，你也可以手动切换到其他溯源码。</p>
          </div>
        </div>

        <div class="inline-fields">
          <input v-model="traceId" placeholder="TRACE-XXXX" />
          <button class="btn btn-primary" @click="loadTrace">
            <span class="btn-icon"><NavIcon name="verify" /></span>
            <span>{{ loading ? '查询中' : '查询溯源' }}</span>
          </button>
        </div>

        <p class="error" v-if="error">{{ error }}</p>

        <div class="trace-summary rich" v-if="trace?.summary">
          <div class="trace-summary-card">
            <span>批次号</span>
            <strong>{{ trace.summary.batchNo }}</strong>
          </div>
          <div class="trace-summary-card">
            <span>商家</span>
            <strong>{{ trace.summary.shopName }}</strong>
          </div>
          <div class="trace-summary-card">
            <span>批次状态</span>
            <strong>{{ toBatchStatusLabel(trace.summary.batchStatus) }}</strong>
          </div>
          <div class="trace-summary-card">
            <span>采收日期</span>
            <strong>{{ trace.summary.harvestDate || '-' }}</strong>
          </div>
        </div>
      </section>

      <TraceQrCard
        v-if="qrValue"
        :value="qrValue"
        title="该批次二维码"
        caption="这个二维码对应公共验真链接，消费者扫码后可以直接查看结果和事件链。"
        :trace-id="trace?.summary?.traceId"
      />
    </div>

    <TraceTimeline :events="trace?.events || []" />
  </AppShell>
</template>
