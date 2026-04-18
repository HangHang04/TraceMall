<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppShell from '@/components/layout/AppShell.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import TraceQrCard from '@/components/ui/TraceQrCard.vue'
import TraceTimeline from '@/components/ui/TraceTimeline.vue'
import { getFruit, traceDetail } from '@/api'
import { useCartStore } from '@/stores/cart'
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
const cart = useCartStore()
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

const summaryRows = computed(() => [
  { label: '分类', value: fruit.value?.category || '-' },
  { label: '产地', value: fruit.value?.origin || '-' },
  { label: '规格', value: fruit.value?.unit || '-' },
  { label: '价格', value: fruit.value ? `¥${fruit.value.unitPrice} / ${fruit.value.unit}` : '-' },
])

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

function addCurrentFruit() {
  if (!fruit.value) return
  cart.add({
    batchId: fruit.value.id,
    fruitName: fruit.value.fruitName,
    unitPrice: fruit.value.unitPrice,
    quantity: 1,
  })
}

onMounted(async () => {
  await loadFruit()
  await loadTrace()
})
</script>

<template>
  <AppShell title="商品详情" subtitle="查看商品信息、批次状态与完整溯源链路">
    <section class="detail-hero panel" v-if="fruit">
      <div class="detail-copy">
        <p class="detail-kicker">Selected Fruit</p>
        <h2>{{ fruit.fruitName }}</h2>
        <p class="detail-description">
          {{ fruit.description || '当前商品支持下单、验真与溯源查询。' }}
        </p>
        <div class="detail-actions">
          <button class="btn btn-primary" @click="addCurrentFruit">
            <span class="btn-icon"><NavIcon name="plus" /></span>
            <span>加入购物车</span>
          </button>
          <button class="btn btn-ghost" @click="router.push('/cart')">
            <span class="btn-icon"><NavIcon name="cart" /></span>
            <span>查看购物车</span>
          </button>
        </div>
      </div>
      <div class="detail-meta">
        <div v-for="row in summaryRows" :key="row.label" class="meta-item">
          <span>{{ row.label }}</span>
          <strong>{{ row.value }}</strong>
        </div>
      </div>
    </section>

    <section class="detail-grid">
      <section class="panel trace-panel">
        <div class="panel-head">
          <h3>溯源查询</h3>
          <p>输入溯源码后查看批次摘要、二维码与事件链。</p>
        </div>
        <div class="verify-form">
          <input v-model="traceId" placeholder="TRACE-XXXX" />
          <button class="btn btn-primary" @click="loadTrace">
            <span class="btn-icon"><NavIcon name="verify" /></span>
            <span>{{ loading ? '查询中' : '查询' }}</span>
          </button>
        </div>
        <p class="error" v-if="error">{{ error }}</p>
        <div v-if="trace?.summary" class="trace-summary detail-summary">
          <p><strong>批次：</strong>{{ trace.summary.batchNo }}</p>
          <p><strong>店铺：</strong>{{ trace.summary.shopName }}</p>
          <p><strong>状态：</strong>{{ toBatchStatusLabel(trace.summary.batchStatus) }}</p>
        </div>
      </section>

      <div class="detail-side">
        <TraceQrCard
          v-if="qrValue"
          :value="qrValue"
          title="该批次二维码"
          caption="二维码会直接指向公开验真页，消费者扫码后可查看结果与事件链。"
          :trace-id="trace?.summary?.traceId"
        />
        <TraceTimeline :events="trace?.events || []" />
      </div>
    </section>
  </AppShell>
</template>

<style scoped>
.detail-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(280px, 0.9fr);
  gap: 24px;
  padding: 28px;
  background:
    radial-gradient(circle at 0% 0%, rgba(255, 219, 188, 0.4), transparent 24%),
    linear-gradient(135deg, rgba(253, 248, 241, 0.96), rgba(240, 246, 237, 0.88));
}

.detail-kicker {
  color: var(--muted);
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.detail-copy h2 {
  margin-top: 10px;
  font-family: var(--font-display);
  font-size: clamp(2.4rem, 4vw, 4.2rem);
  line-height: 0.95;
}

.detail-description {
  max-width: 42rem;
  margin-top: 14px;
  color: #556452;
  line-height: 1.8;
}

.detail-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.detail-meta {
  display: grid;
  gap: 12px;
}

.meta-item {
  padding: 18px;
  border-radius: 22px;
  background: rgba(255, 251, 246, 0.78);
  border: 1px solid rgba(113, 129, 102, 0.12);
}

.meta-item span {
  color: var(--muted);
  font-size: 13px;
}

.meta-item strong {
  display: block;
  margin-top: 6px;
  font-size: 1.1rem;
}

.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 0.92fr);
  gap: 16px;
}

.detail-side {
  display: grid;
  gap: 16px;
}

.detail-summary {
  margin-top: 18px;
}

@media (max-width: 900px) {
  .detail-hero,
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
