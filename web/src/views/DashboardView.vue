<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppShell from '@/components/layout/AppShell.vue'
import FilterBar from '@/components/ui/FilterBar.vue'
import MetricCard from '@/components/ui/MetricCard.vue'
import ChartPanel from '@/components/ui/ChartPanel.vue'
import VerifyResultPanel from '@/components/ui/VerifyResultPanel.vue'
import { listFruits, verifyTrace } from '@/api'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cart = useCartStore()
const fruits = ref([])
const filtered = ref([])
const loading = ref(false)
const verifyInput = ref({ traceId: '', signature: '', geo: '四川-成都', ip: '10.0.2.8', deviceFingerprint: 'web-demo' })
const verifyResult = ref(null)
const verifyError = ref('')

const totalProducts = computed(() => filtered.value.length)
const avgPrice = computed(() => {
  if (!filtered.value.length) return '0.00'
  const sum = filtered.value.reduce((acc, item) => acc + Number(item.unitPrice || 0), 0)
  return (sum / filtered.value.length).toFixed(2)
})

const categoryOption = computed(() => {
  const categoryCount = {}
  filtered.value.forEach((item) => {
    categoryCount[item.category] = (categoryCount[item.category] || 0) + 1
  })
  return {
    xAxis: { type: 'category', data: Object.keys(categoryCount) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: Object.values(categoryCount), itemStyle: { color: '#1f8f57' } }],
    tooltip: { trigger: 'axis' },
    grid: { left: 30, right: 12, top: 20, bottom: 24 },
  }
})

const priceOption = computed(() => ({
  xAxis: { type: 'category', data: filtered.value.slice(0, 8).map((it) => it.fruitName) },
  yAxis: { type: 'value' },
  series: [
    {
      type: 'line',
      smooth: true,
      data: filtered.value.slice(0, 8).map((it) => Number(it.unitPrice || 0)),
      itemStyle: { color: '#2dc76f' },
      areaStyle: { color: 'rgba(45,199,111,0.15)' },
    },
  ],
  tooltip: { trigger: 'axis' },
  grid: { left: 30, right: 12, top: 20, bottom: 24 },
}))

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
    const keywordOk = !filter.keyword || item.fruitName.includes(filter.keyword)
    const categoryOk = !filter.category || item.category === filter.category
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
  } catch (err) {
    verifyError.value = err.message
  }
}

onMounted(loadFruits)
</script>

<template>
  <AppShell title="商城仪表盘" subtitle="高保真风格 + 溯源验真闭环">
    <template #actions>
      <button class="btn btn-primary" @click="router.push('/cart')">购物车 ({{ cart.count }})</button>
    </template>

    <FilterBar @apply="applyFilter" />

    <section class="metrics-grid">
      <MetricCard label="在售商品" :value="totalProducts" trend="实时更新" status="normal" />
      <MetricCard label="平均单价" :value="`¥${avgPrice}`" trend="最近批次均价" status="stable" />
      <MetricCard label="购物车件数" :value="cart.count" trend="可直接下单" status="active" />
      <MetricCard label="系统状态" value="在线" trend="实时监控 active" status="healthy" />
    </section>

    <section class="chart-grid">
      <ChartPanel title="分类分布" subtitle="在售水果分类统计" :option="categoryOption" />
      <ChartPanel title="价格走势" subtitle="展示前 8 个商品价格" :option="priceOption" />
    </section>

    <section class="panel verify-panel">
      <h3>二维码验真</h3>
      <div class="verify-form">
        <input v-model="verifyInput.traceId" placeholder="TRACE-XXXX" />
        <input v-model="verifyInput.signature" placeholder="signature" />
        <input v-model="verifyInput.geo" placeholder="geo" />
        <button class="btn btn-primary" @click="doVerify">立即验真</button>
      </div>
      <p class="error" v-if="verifyError">{{ verifyError }}</p>
    </section>

    <VerifyResultPanel :result="verifyResult" />

    <section class="panel">
      <h3>水果列表</h3>
      <p v-if="loading">加载中...</p>
      <div class="fruit-grid" v-else>
        <article class="fruit-card" v-for="item in filtered" :key="item.id">
          <div>
            <h4>{{ item.fruitName }}</h4>
            <p>{{ item.category }} · {{ item.origin || '-' }}</p>
            <p class="price">¥{{ item.unitPrice }} / {{ item.unit }}</p>
          </div>
          <div class="card-actions">
            <button class="btn btn-ghost" @click="router.push(`/product/${item.id}`)">详情</button>
            <button class="btn btn-primary" @click="addToCart(item)">加入购物车</button>
          </div>
        </article>
      </div>
    </section>
  </AppShell>
</template>