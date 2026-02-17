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

const totalProducts = computed(() => filtered.value.length)
const avgPrice = computed(() => {
  if (!filtered.value.length) return '0.00'
  const sum = filtered.value.reduce((acc, item) => acc + Number(item.unitPrice || 0), 0)
  return (sum / filtered.value.length).toFixed(2)
})

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
  <AppShell title="商城主页" subtitle="选购水果并快速完成溯源码验真">
    <template #actions>
      <button class="btn btn-primary" @click="router.push('/cart')">
        <span class="btn-icon"><NavIcon name="cart" /></span>
        <span>购物车 ({{ cart.count }})</span>
      </button>
    </template>

    <FilterBar @apply="applyFilter" />

    <section class="metrics-grid">
      <MetricCard icon="package" label="在售商品" :value="totalProducts" trend="按筛选条件实时更新" status="在线" />
      <MetricCard icon="price" label="平均单价" :value="`¥${avgPrice}`" trend="当前列表均价" status="参考" />
      <MetricCard icon="cart" label="购物车数量" :value="cart.count" trend="可直接提交订单" status="就绪" />
      <MetricCard icon="verify" label="最近验真" :value="verifyHistory.length" trend="本地保存 8 条记录" status="安全" />
    </section>

    <section class="panel verify-panel">
      <h3 class="panel-title">
        <span class="title-icon"><NavIcon name="verify" /></span>
        <span>二维码验真</span>
      </h3>
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

    <section class="panel" v-if="verifyHistory.length">
      <h3 class="panel-title">
        <span class="title-icon"><NavIcon name="orders" /></span>
        <span>最近验真记录</span>
      </h3>
      <div class="state-row" v-for="item in verifyHistory" :key="`${item.traceId}-${item.at}`">
        <span>{{ item.traceId }}</span>
        <span>{{ toVerifyStatusLabel(item.status) }}</span>
        <span>{{ item.at }}</span>
      </div>
    </section>

    <section class="panel">
      <h3 class="panel-title">
        <span class="title-icon"><NavIcon name="package" /></span>
        <span>水果列表</span>
      </h3>
      <p v-if="loading">加载中...</p>
      <div class="fruit-grid" v-else>
        <article class="fruit-card" v-for="item in filtered" :key="item.id">
          <div>
            <h4>{{ item.fruitName }}</h4>
            <p>{{ item.category }} · {{ item.origin || '-' }}</p>
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
  </AppShell>
</template>
