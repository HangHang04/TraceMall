<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppShell from '@/components/layout/AppShell.vue'
import FilterBar from '@/components/ui/FilterBar.vue'
import MetricCard from '@/components/ui/MetricCard.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import VerifyResultPanel from '@/components/ui/VerifyResultPanel.vue'
import { listFruits, verifyTrace } from '@/api'
import { useCartStore } from '@/stores/cart'
import { toVerifyStatusLabel } from '@/utils/status'

const HISTORY_KEY = 'tracemall_verify_history'

const router = useRouter()
const cart = useCartStore()
const fruits = ref([])
const filtered = ref([])
const loading = ref(false)
const verifyInput = ref({
  traceId: 'TRACE-APPLE-001',
  signature: 'demo-signature',
  geo: '四川-成都',
  ip: '10.0.2.8',
  deviceFingerprint: 'web-demo',
})
const verifyResult = ref(null)
const verifyError = ref('')
const verifyHistory = ref([])
const quickTags = ['当季优选', '48 小时冷链', '支持扫码验真']

const totalProducts = computed(() => filtered.value.length)
const avgPrice = computed(() => {
  if (!filtered.value.length) return '0.00'
  const sum = filtered.value.reduce((acc, item) => acc + Number(item.unitPrice || 0), 0)
  return (sum / filtered.value.length).toFixed(2)
})
const featuredFruit = computed(() => filtered.value[0] || fruits.value[0] || null)
const curatedFruits = computed(() => filtered.value.slice(0, 6))
const activeTraceId = computed(() => verifyResult.value?.traceId || verifyInput.value.traceId)
const verifySummary = computed(() => [
  { label: '最近验真', value: `${verifyHistory.value.length} 条` },
  { label: '购物车', value: `${cart.count} 件` },
  { label: '均价', value: `¥${avgPrice.value}` },
])

function readHistory() {
  try {
    const raw = localStorage.getItem(HISTORY_KEY)
    verifyHistory.value = raw ? JSON.parse(raw) : []
  } catch {
    verifyHistory.value = []
  }
}

function saveHistory() {
  localStorage.setItem(HISTORY_KEY, JSON.stringify(verifyHistory.value.slice(0, 8)))
}

async function loadFruits() {
  loading.value = true
  try {
    fruits.value = await listFruits()
    filtered.value = fruits.value
  } finally {
    loading.value = false
  }
}

function applyFilter(filter) {
  filtered.value = fruits.value.filter((item) => {
    const keyword = String(filter.keyword || '')
      .trim()
      .toLowerCase()
    const keywordOk = !keyword || String(item.fruitName || '').toLowerCase().includes(keyword)
    const categoryOk = !filter.category || String(item.category || '').includes(filter.category)
    return keywordOk && categoryOk
  })
}

function addToCart(item) {
  cart.add({
    batchId: item.id,
    fruitName: item.fruitName,
    unitPrice: item.unitPrice,
    quantity: 1,
  })
}

async function doVerify() {
  verifyError.value = ''
  try {
    verifyResult.value = await verifyTrace(verifyInput.value)
    verifyHistory.value = [
      {
        traceId: verifyInput.value.traceId,
        status: verifyResult.value?.status || 'UNKNOWN',
        at: new Date().toLocaleString(),
      },
      ...verifyHistory.value,
    ].slice(0, 8)
    saveHistory()
  } catch (err) {
    verifyError.value = err.message
  }
}

onMounted(async () => {
  readHistory()
  await loadFruits()
})
</script>

<template>
  <AppShell title="商城工作台" subtitle="先看重点批次，再完成选购与扫码验真">
    <template #actions>
      <button class="btn btn-primary" @click="router.push('/cart')">
        <span class="btn-icon"><NavIcon name="cart" /></span>
        <span>购物车 ({{ cart.count }})</span>
      </button>
    </template>

    <section class="dashboard-hero panel">
      <div class="hero-main">
        <p class="hero-kicker">重点批次</p>
        <h2>{{ featuredFruit?.fruitName || '今日水果批次' }}</h2>
        <p class="hero-description">
          {{ featuredFruit?.origin || '产地待补充' }} · {{ featuredFruit?.category || '新鲜水果' }} · 当前可直接加入购物车并发起验真。
        </p>
        <div class="hero-tags">
          <span v-for="tag in quickTags" :key="tag" class="hero-tag">{{ tag }}</span>
        </div>
        <div class="hero-actions">
          <button v-if="featuredFruit" class="btn btn-primary" @click="addToCart(featuredFruit)">
            <span class="btn-icon"><NavIcon name="plus" /></span>
            <span>加入购物车</span>
          </button>
          <button v-if="featuredFruit" class="btn btn-ghost" @click="router.push(`/product/${featuredFruit.id}`)">
            <span class="btn-icon"><NavIcon name="orders" /></span>
            <span>查看详情</span>
          </button>
        </div>
      </div>

      <div class="hero-side">
        <div class="hero-trace">
          <span>验真编号</span>
          <strong>{{ activeTraceId }}</strong>
          <p>输入溯源码、签名和位置后，可直接返回风控与链路结果。</p>
        </div>
        <div class="hero-summary">
          <div v-for="item in verifySummary" :key="item.label">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </div>
    </section>

    <FilterBar @apply="applyFilter" />

    <section class="dashboard-grid">
      <div class="dashboard-main">
        <section class="metrics-grid">
          <MetricCard icon="package" label="在售商品" :value="totalProducts" trend="按筛选条件实时更新" status="在线" />
          <MetricCard icon="price" label="平均单价" :value="`¥${avgPrice}`" trend="当前列表均价" status="参考" />
          <MetricCard icon="cart" label="购物车数量" :value="cart.count" trend="可直接提交订单" status="就绪" />
          <MetricCard icon="verify" label="最近验真" :value="verifyHistory.length" trend="本地保存 8 条记录" status="安全" />
        </section>

        <section class="panel dashboard-products">
          <div class="dashboard-section-head">
            <div>
              <p class="section-kicker">选购区域</p>
              <h3 class="panel-title-text">优先展示当前筛选结果</h3>
            </div>
            <span class="section-note">{{ totalProducts }} 个商品</span>
          </div>
          <p v-if="loading">加载中...</p>
          <div class="fruit-grid refined" v-else>
            <article class="fruit-card refined" v-for="item in curatedFruits" :key="item.id">
              <div class="fruit-card-top">
                <span class="fruit-badge">{{ item.category }}</span>
                <span class="fruit-origin">{{ item.origin || '产地待补充' }}</span>
              </div>
              <div>
                <h4>{{ item.fruitName }}</h4>
                <p class="fruit-meta">批次 {{ item.id }} · {{ item.unit || '件' }}装</p>
                <p class="price">¥{{ item.unitPrice }} / {{ item.unit }}</p>
              </div>
              <div class="card-actions">
                <button class="btn btn-ghost" @click="router.push(`/product/${item.id}`)">
                  <span class="btn-icon"><NavIcon name="orders" /></span>
                  <span>详情</span>
                </button>
                <button class="btn btn-primary" @click="addToCart(item)">
                  <span class="btn-icon"><NavIcon name="plus" /></span>
                  <span>加入购物车</span>
                </button>
              </div>
            </article>
          </div>
        </section>
      </div>

      <aside class="dashboard-side">
        <section class="panel verify-panel refined">
          <div class="dashboard-section-head">
            <div>
              <p class="section-kicker">扫码入口</p>
              <h3 class="panel-title-text">二维码验真</h3>
            </div>
          </div>
          <div class="verify-form">
            <input v-model="verifyInput.traceId" placeholder="TRACE-XXXX" />
            <input v-model="verifyInput.signature" placeholder="签名 signature" />
            <input v-model="verifyInput.geo" placeholder="地理位置，如 四川-成都" />
            <button class="btn btn-primary" @click="doVerify">
              <span class="btn-icon"><NavIcon name="verify" /></span>
              <span>立即验真</span>
            </button>
          </div>
          <p class="error" v-if="verifyError">{{ verifyError }}</p>
        </section>

        <VerifyResultPanel :result="verifyResult" />

        <section class="panel history-panel" v-if="verifyHistory.length">
          <div class="dashboard-section-head">
            <div>
              <p class="section-kicker">最近记录</p>
              <h3 class="panel-title-text">验真历史</h3>
            </div>
          </div>
          <div class="history-list">
            <div class="history-item" v-for="item in verifyHistory" :key="`${item.traceId}-${item.at}`">
              <strong>{{ item.traceId }}</strong>
              <span>{{ toVerifyStatusLabel(item.status) }}</span>
              <small>{{ item.at }}</small>
            </div>
          </div>
        </section>
      </aside>
    </section>
  </AppShell>
</template>

<style scoped>
.dashboard-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(280px, 0.85fr);
  gap: 24px;
  padding: 28px;
  background:
    radial-gradient(circle at top left, rgba(255, 223, 183, 0.56), transparent 28%),
    linear-gradient(135deg, rgba(253, 248, 241, 0.96), rgba(240, 246, 237, 0.9));
}

.hero-kicker,
.section-kicker {
  color: var(--muted);
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.hero-main h2 {
  margin-top: 10px;
  font-family: var(--font-display);
  font-size: clamp(2.3rem, 4vw, 4rem);
  font-weight: 600;
  line-height: 0.95;
}

.hero-description {
  margin-top: 14px;
  max-width: 44rem;
  color: #53614f;
  line-height: 1.8;
}

.hero-tags,
.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.hero-tag {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 252, 247, 0.9);
  border: 1px solid rgba(113, 129, 102, 0.14);
  color: #4c5b49;
  font-size: 13px;
}

.hero-side {
  display: grid;
  gap: 14px;
}

.hero-trace,
.hero-summary {
  padding: 20px;
  border-radius: 24px;
  background: rgba(255, 251, 246, 0.78);
  border: 1px solid rgba(113, 129, 102, 0.12);
}

.hero-trace span,
.hero-summary span {
  color: var(--muted);
  font-size: 13px;
}

.hero-trace strong {
  display: block;
  margin-top: 8px;
  font-family: var(--font-display);
  font-size: 2.2rem;
  font-weight: 600;
  line-height: 1;
}

.hero-trace p {
  margin-top: 10px;
  color: #576553;
  line-height: 1.7;
}

.hero-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.hero-summary strong {
  display: block;
  margin-top: 6px;
  font-size: 1.2rem;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.25fr) minmax(320px, 0.75fr);
  gap: 16px;
}

.dashboard-main,
.dashboard-side {
  display: grid;
  gap: 16px;
  align-content: start;
}

.dashboard-products,
.verify-panel.refined,
.history-panel {
  padding: 22px;
}

.dashboard-section-head {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.panel-title-text {
  margin-top: 8px;
  font-family: var(--font-display);
  font-size: 2rem;
  font-weight: 600;
  line-height: 1;
}

.section-note {
  color: var(--muted);
  font-size: 13px;
}

.fruit-grid.refined {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.fruit-card.refined {
  padding: 18px;
  gap: 18px;
  background: linear-gradient(180deg, rgba(255, 252, 247, 0.92), rgba(246, 249, 244, 0.82));
}

.fruit-card-top {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  color: var(--muted);
  font-size: 13px;
}

.fruit-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(142, 170, 127, 0.12);
  color: #4e6546;
}

.fruit-meta {
  margin-top: 6px;
  color: var(--muted);
  font-size: 13px;
}

.history-list {
  display: grid;
  gap: 10px;
}

.history-item {
  display: grid;
  gap: 4px;
  padding: 14px 0;
  border-bottom: 1px dashed var(--line);
}

.history-item:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}

.history-item span,
.history-item small {
  color: var(--muted);
}

@media (max-width: 1100px) {
  .dashboard-hero,
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 767px) {
  .hero-summary,
  .fruit-grid.refined {
    grid-template-columns: 1fr;
  }

  .dashboard-hero,
  .dashboard-products,
  .verify-panel.refined,
  .history-panel {
    padding: 18px;
  }

  .dashboard-section-head {
    flex-direction: column;
  }
}
</style>
