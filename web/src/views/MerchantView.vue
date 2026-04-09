<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import AppShell from '@/components/layout/AppShell.vue'
import ChartPanel from '@/components/ui/ChartPanel.vue'
import MetricCard from '@/components/ui/MetricCard.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import TraceQrCard from '@/components/ui/TraceQrCard.vue'
import { createBatch, createBatchEvent, createMerchantFruit, listMerchantFruits } from '@/api'
import { buildTraceVerifyUrl } from '@/utils/traceQr'
import { toMerchantStatusLabel } from '@/utils/status'

const fruits = ref([])
const loading = ref(false)
const message = ref('')
const latestBatch = ref(null)

const fruitForm = reactive({
  fruitName: '测试苹果',
  category: '苹果',
  origin: '四川',
  unit: 'kg',
  unitPrice: 12.5,
  description: '脆甜、适合即食，支持批次验真。',
})

const batchForm = reactive({
  fruitId: null,
  batchNo: `BATCH-WEB-${new Date().getFullYear()}-001`,
  traceId: '',
  harvestDate: '2026-03-13',
  expireDate: '2026-04-13',
  quantity: 100,
})

const eventForm = reactive({
  batchId: 1,
  eventType: 'QUALITY_CHECK',
  eventTime: '2026-03-13T12:00',
  location: '成都质检中心',
  payloadJson: '{"result":"PASS","temperature":"4C"}',
})

const onSaleCount = computed(
  () => fruits.value.filter((item) => String(item.status || '').toUpperCase() === 'ON_SALE').length,
)
const averagePrice = computed(() => {
  if (!fruits.value.length) return '0.00'
  const total = fruits.value.reduce((sum, item) => sum + Number(item.unitPrice || 0), 0)
  return (total / fruits.value.length).toFixed(2)
})
const traceQrValue = computed(() => {
  if (!latestBatch.value?.traceId || !latestBatch.value?.signature) return ''
  return buildTraceVerifyUrl({
    traceId: latestBatch.value.traceId,
    signature: latestBatch.value.signature,
  })
})

const categoryOption = computed(() => {
  const countMap = {}
  fruits.value.forEach((item) => {
    const key = item.category || '未分类'
    countMap[key] = (countMap[key] || 0) + 1
  })
  return {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: Object.keys(countMap),
      axisLine: { lineStyle: { color: '#8da2c0' } },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: 'rgba(102, 126, 170, 0.18)' } },
    },
    grid: { left: 36, right: 12, top: 20, bottom: 24 },
    series: [
      {
        type: 'bar',
        data: Object.values(countMap),
        itemStyle: {
          color: '#3f8a6c',
          borderRadius: [10, 10, 0, 0],
        },
      },
    ],
  }
})

const priceOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: fruits.value.slice(0, 8).map((item) => item.fruitName),
    axisLine: { lineStyle: { color: '#8da2c0' } },
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: 'rgba(102, 126, 170, 0.18)' } },
  },
  grid: { left: 36, right: 12, top: 20, bottom: 24 },
  series: [
    {
      type: 'line',
      smooth: true,
      data: fruits.value.slice(0, 8).map((item) => Number(item.unitPrice || 0)),
      itemStyle: { color: '#c96d3a' },
      lineStyle: { width: 3 },
      areaStyle: { color: 'rgba(201, 109, 58, 0.12)' },
    },
  ],
}))

function generateTraceId() {
  return `TRACE-${Date.now().toString(36).toUpperCase()}`
}

function normalizeDateTime(value) {
  if (!value) return value
  return value.length === 16 ? `${value}:00` : value
}

async function loadFruits() {
  loading.value = true
  try {
    fruits.value = await listMerchantFruits()
    if (!batchForm.fruitId && fruits.value.length) {
      batchForm.fruitId = fruits.value[0].id
    }
  } finally {
    loading.value = false
  }
}

async function addFruit() {
  await createMerchantFruit(fruitForm)
  message.value = '商品已创建并刷新列表。'
  await loadFruits()
}

async function addBatch() {
  const payload = {
    ...batchForm,
    traceId: batchForm.traceId || generateTraceId(),
  }
  const data = await createBatch(payload)
  batchForm.traceId = payload.traceId
  latestBatch.value = data
  eventForm.batchId = data.batchId || eventForm.batchId
  message.value = `批次创建成功：${data.traceId}`
}

async function addEvent() {
  const data = await createBatchEvent({
    ...eventForm,
    eventTime: normalizeDateTime(eventForm.eventTime),
  })
  message.value = `事件写入成功：${data.anchorId}`
}

onMounted(loadFruits)
</script>

<template>
  <AppShell title="商家工作台" subtitle="围绕上架、批次生成和事件补录组织日常操作。">
    <section class="workspace-hero">
      <div class="workspace-summary">
        <span class="page-eyebrow">商家工作台</span>
        <h2>先把批次链路补完整，再把二维码发出去。</h2>
        <p>商家端的重点不是展示，而是高效完成商品录入、批次创建、事件补录和二维码分发。</p>
      </div>

      <div class="workspace-checklist">
        <div class="workspace-check">
          <span class="workspace-bullet" />
          <span>先录入商品，保证分类、产地和价格信息完整。</span>
        </div>
        <div class="workspace-check">
          <span class="workspace-bullet" />
          <span>创建批次时生成溯源码与签名，作为后续扫码入口。</span>
        </div>
        <div class="workspace-check">
          <span class="workspace-bullet" />
          <span>补录质检、仓储和运输等关键节点，保证链路可信。</span>
        </div>
      </div>
    </section>

    <section class="metrics-grid">
      <MetricCard icon="package" label="商品总数" :value="fruits.length" trend="当前商家名下商品数量" status="库存" />
      <MetricCard icon="merchant" label="在售商品" :value="onSaleCount" trend="当前可面向消费者销售的商品" status="在售" />
      <MetricCard icon="price" label="平均单价" :value="`¥${averagePrice}`" trend="按现有商品计算" status="价格" />
      <MetricCard icon="verify" label="最近批次" :value="latestBatch?.traceId || '--'" trend="创建批次后自动刷新" status="溯源" />
    </section>

    <div class="workspace-layout table-panel">
      <div class="section-stack">
        <section class="panel form-card">
          <div class="workspace-head">
            <div>
              <h3 class="panel-title">
                <span class="title-icon"><NavIcon name="plus" /></span>
                <span>录入商品</span>
              </h3>
              <p class="panel-copy">优先保证商品名称、产地、规格和价格完整，后续批次会直接复用这些信息。</p>
            </div>
          </div>
          <div class="form-stack">
            <input v-model="fruitForm.fruitName" placeholder="水果名称" />
            <input v-model="fruitForm.category" placeholder="分类" />
            <input v-model="fruitForm.origin" placeholder="产地" />
            <div class="inline-fields">
              <input v-model="fruitForm.unit" placeholder="单位，例如 kg" />
              <input v-model.number="fruitForm.unitPrice" type="number" placeholder="单价" />
            </div>
            <textarea v-model="fruitForm.description" rows="4" placeholder="商品描述" />
            <button class="btn btn-primary" @click="addFruit">
              <span class="btn-icon"><NavIcon name="plus" /></span>
              <span>创建商品</span>
            </button>
          </div>
        </section>

        <section class="panel form-card">
          <div class="workspace-head">
            <div>
              <h3 class="panel-title">
                <span class="title-icon"><NavIcon name="package" /></span>
                <span>创建批次</span>
              </h3>
              <p class="panel-copy">创建批次后会得到可分发的溯源码和二维码，消费者扫码后可进入公共验真页。</p>
            </div>
          </div>
          <div class="form-stack">
            <select v-model.number="batchForm.fruitId">
              <option :value="null">选择商品</option>
              <option v-for="item in fruits" :key="item.id" :value="item.id">{{ item.fruitName }} #{{ item.id }}</option>
            </select>
            <input v-model="batchForm.batchNo" placeholder="批次号" />
            <input v-model="batchForm.traceId" placeholder="溯源码，可留空自动生成" />
            <div class="inline-fields">
              <input v-model="batchForm.harvestDate" type="date" />
              <input v-model="batchForm.expireDate" type="date" />
            </div>
            <input v-model.number="batchForm.quantity" type="number" placeholder="数量" />
            <button class="btn btn-primary" @click="addBatch">
              <span class="btn-icon"><NavIcon name="verify" /></span>
              <span>生成批次</span>
            </button>
          </div>
        </section>
      </div>

      <div class="section-stack">
        <section class="panel form-card">
          <div class="workspace-head">
            <div>
              <h3 class="panel-title">
                <span class="title-icon"><NavIcon name="chain" /></span>
                <span>补录事件</span>
              </h3>
              <p class="panel-copy">将质检、温控、仓储和运输等关键动作写入链路，方便后续扫码核对。</p>
            </div>
          </div>
          <div class="form-stack">
            <input v-model.number="eventForm.batchId" type="number" placeholder="batchId" />
            <input v-model="eventForm.eventType" placeholder="事件类型，例如 QUALITY_CHECK" />
            <input v-model="eventForm.eventTime" type="datetime-local" />
            <input v-model="eventForm.location" placeholder="发生地点" />
            <textarea v-model="eventForm.payloadJson" rows="4" placeholder="JSON 载荷" />
            <button class="btn btn-primary" @click="addEvent">
              <span class="btn-icon"><NavIcon name="chain" /></span>
              <span>写入事件</span>
            </button>
          </div>
        </section>

        <section class="panel">
          <h3 class="panel-title">
            <span class="title-icon"><NavIcon name="spark" /></span>
            <span>当前节奏</span>
          </h3>
          <div class="workspace-stat-list">
            <div class="workspace-note">
              <span class="workspace-label">今日重点</span>
              <strong>确保在售商品都绑定了可扫码批次。</strong>
            </div>
            <div class="workspace-note">
              <span class="workspace-label">建议顺序</span>
              <strong>商品录入 → 批次创建 → 事件补录 → 下载二维码。</strong>
            </div>
            <div class="workspace-note">
              <span class="workspace-label">反馈</span>
              <strong>{{ message || '完成操作后，这里会显示最近一次写入结果。' }}</strong>
            </div>
          </div>
        </section>
      </div>
    </div>

    <section class="chart-grid table-panel">
      <ChartPanel title="商品分类分布" subtitle="看当前在售结构是否均衡" :option="categoryOption" />
      <ChartPanel title="价格带走势" subtitle="观察主力商品的价格区间" :option="priceOption" />
    </section>

    <TraceQrCard
      v-if="traceQrValue"
      :value="traceQrValue"
      title="最近生成的批次二维码"
      caption="下载后可直接贴到包装或宣传物料中，消费者扫码会进入公共验真页。"
      :trace-id="latestBatch?.traceId"
    />

    <section class="panel table-panel">
      <h3 class="panel-title">
        <span class="title-icon"><NavIcon name="merchant" /></span>
        <span>商品清单</span>
      </h3>
      <p v-if="loading">加载中...</p>
      <div class="table-wrap" v-else>
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>名称</th>
              <th>分类</th>
              <th>产地</th>
              <th>价格</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in fruits" :key="item.id">
              <td>{{ item.id }}</td>
              <td><strong>{{ item.fruitName }}</strong></td>
              <td>{{ item.category }}</td>
              <td>{{ item.origin }}</td>
              <td>¥{{ item.unitPrice }}</td>
              <td>{{ toMerchantStatusLabel(item.status) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>
