<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import AppShell from '@/components/layout/AppShell.vue'
import ChartPanel from '@/components/ui/ChartPanel.vue'
import MetricCard from '@/components/ui/MetricCard.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import { createBatch, createBatchEvent, createMerchantFruit, listMerchantFruits } from '@/api'
import { toMerchantStatusLabel } from '@/utils/status'

const fruits = ref([])
const loading = ref(false)
const message = ref('')

const fruitForm = reactive({
  fruitName: '测试苹果',
  category: '苹果',
  origin: '四川',
  unit: 'kg',
  unitPrice: 12.5,
  description: '商家新增水果',
})

const batchForm = reactive({
  fruitId: null,
  batchNo: 'BATCH-WEB-001',
  traceId: '',
  harvestDate: '2026-02-08',
  expireDate: '2026-03-08',
  quantity: 100,
})

const eventForm = reactive({
  batchId: 1,
  eventType: 'QUALITY_CHECK',
  eventTime: '2026-02-08T12:00',
  location: '成都质检中心',
  payloadJson: '{"result":"PASS"}',
})

const onSaleCount = computed(
  () => fruits.value.filter((item) => String(item.status || '').toUpperCase() === 'ON_SALE').length,
)

const avgPrice = computed(() => {
  if (!fruits.value.length) return '0.00'
  const sum = fruits.value.reduce((acc, item) => acc + Number(item.unitPrice || 0), 0)
  return (sum / fruits.value.length).toFixed(2)
})

const categoryOption = computed(() => {
  const countMap = {}
  fruits.value.forEach((item) => {
    const category = item.category || '未分类'
    countMap[category] = (countMap[category] || 0) + 1
  })

  return {
    xAxis: { type: 'category', data: Object.keys(countMap) },
    yAxis: { type: 'value' },
    tooltip: { trigger: 'axis' },
    series: [{ type: 'bar', data: Object.values(countMap), itemStyle: { color: '#6b835b' } }],
    grid: { left: 36, right: 12, top: 20, bottom: 24 },
  }
})

const priceOption = computed(() => ({
  xAxis: { type: 'category', data: fruits.value.slice(0, 8).map((item) => item.fruitName) },
  yAxis: { type: 'value' },
  tooltip: { trigger: 'axis' },
  series: [
    {
      type: 'line',
      smooth: true,
      data: fruits.value.slice(0, 8).map((item) => Number(item.unitPrice || 0)),
      itemStyle: { color: '#a06b3f' },
      areaStyle: { color: 'rgba(160, 107, 63, 0.14)' },
    },
  ],
  grid: { left: 36, right: 12, top: 20, bottom: 24 },
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
  message.value = '水果新增成功'
  await loadFruits()
}

async function addBatch() {
  const payload = {
    ...batchForm,
    traceId: batchForm.traceId || generateTraceId(),
  }
  const data = await createBatch(payload)
  batchForm.traceId = payload.traceId
  eventForm.batchId = data.id || eventForm.batchId
  message.value = `批次创建成功：${data.traceId}`
}

async function addEvent() {
  const data = await createBatchEvent({
    ...eventForm,
    eventTime: normalizeDateTime(eventForm.eventTime),
  })
  message.value = `事件存证成功：${data.anchorId}`
}

onMounted(loadFruits)
</script>

<template>
  <AppShell title="商户工作台" subtitle="录入商品、创建批次并维护溯源事件">
    <section class="merchant-hero panel">
      <div>
        <p class="merchant-kicker">Merchant Operations</p>
        <h2>围绕商品、批次和事件三条主线完成日常录入。</h2>
      </div>
      <div class="merchant-summary">
        <div><span>商品数</span><strong>{{ fruits.length }}</strong></div>
        <div><span>在售中</span><strong>{{ onSaleCount }}</strong></div>
        <div><span>均价</span><strong>¥{{ avgPrice }}</strong></div>
      </div>
    </section>

    <section class="metrics-grid">
      <MetricCard icon="package" label="商品总数" :value="fruits.length" trend="当前商家名下商品" status="统计" />
      <MetricCard icon="merchant" label="在售商品" :value="onSaleCount" trend="可直接售卖" status="在售" />
      <MetricCard icon="price" label="平均单价" :value="`¥${avgPrice}`" trend="按所有商品计算" status="参考" />
      <MetricCard icon="status" label="管理状态" value="在线" trend="批次录入与存证可用" status="稳定" />
    </section>

    <section class="chart-grid">
      <ChartPanel title="商品分类分布" subtitle="查看当前上架结构" :option="categoryOption" />
      <ChartPanel title="价格趋势（前 8 项）" subtitle="快速观察价格带" :option="priceOption" />
    </section>

    <section class="merchant-form-grid">
      <section class="panel form-panel">
        <div class="panel-head">
          <h3>新增水果</h3>
          <p>先录入商品基础资料，再进入批次环节。</p>
        </div>
        <input v-model="fruitForm.fruitName" placeholder="水果名称" />
        <input v-model="fruitForm.category" placeholder="分类" />
        <input v-model="fruitForm.origin" placeholder="产地" />
        <input v-model="fruitForm.unit" placeholder="单位，如 kg" />
        <input v-model.number="fruitForm.unitPrice" type="number" placeholder="单价" />
        <textarea v-model="fruitForm.description" rows="3" placeholder="商品描述" />
        <button class="btn btn-primary" @click="addFruit">
          <span class="btn-icon"><NavIcon name="plus" /></span>
          <span>提交</span>
        </button>
      </section>

      <section class="panel form-panel">
        <div class="panel-head">
          <h3>创建批次</h3>
          <p>绑定 fruitId、批次号和溯源码。</p>
        </div>
        <input v-model.number="batchForm.fruitId" type="number" placeholder="fruitId" />
        <input v-model="batchForm.batchNo" placeholder="批次号" />
        <input v-model="batchForm.traceId" placeholder="溯源码（留空自动生成）" />
        <input v-model="batchForm.harvestDate" type="date" />
        <input v-model="batchForm.expireDate" type="date" />
        <input v-model.number="batchForm.quantity" type="number" placeholder="数量" />
        <button class="btn btn-primary" @click="addBatch">
          <span class="btn-icon"><NavIcon name="plus" /></span>
          <span>提交</span>
        </button>
      </section>

      <section class="panel form-panel">
        <div class="panel-head">
          <h3>新增溯源事件</h3>
          <p>用于质检、仓储、运输等关键节点存证。</p>
        </div>
        <input v-model.number="eventForm.batchId" type="number" placeholder="batchId" />
        <input v-model="eventForm.eventType" placeholder="事件类型，如 QUALITY_CHECK" />
        <input v-model="eventForm.eventTime" type="datetime-local" />
        <input v-model="eventForm.location" placeholder="地点" />
        <textarea v-model="eventForm.payloadJson" rows="3" placeholder="JSON 载荷" />
        <button class="btn btn-primary" @click="addEvent">
          <span class="btn-icon"><NavIcon name="plus" /></span>
          <span>提交</span>
        </button>
      </section>
    </section>

    <section class="panel merchant-table-panel">
      <div class="panel-head">
        <h3>商家水果列表</h3>
        <p>查看当前商品状态与价格信息。</p>
      </div>
      <p class="success" v-if="message">{{ message }}</p>
      <p v-if="loading">加载中...</p>
      <div class="table-wrap" v-else>
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>名称</th>
              <th>分类</th>
              <th>价格</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in fruits" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.fruitName }}</td>
              <td>{{ item.category }}</td>
              <td>¥{{ item.unitPrice }}</td>
              <td>{{ toMerchantStatusLabel(item.status) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>

<style scoped>
.merchant-hero,
.merchant-table-panel {
  padding: 24px;
}

.merchant-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(280px, 0.9fr);
  gap: 20px;
  background:
    radial-gradient(circle at 0% 0%, rgba(255, 225, 193, 0.42), transparent 24%),
    linear-gradient(135deg, rgba(253, 248, 241, 0.96), rgba(240, 246, 237, 0.88));
}

.merchant-kicker {
  color: var(--muted);
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.merchant-hero h2 {
  margin-top: 10px;
  font-family: var(--font-display);
  font-size: clamp(2rem, 4vw, 3.6rem);
  line-height: 1;
}

.merchant-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.merchant-summary div {
  padding: 18px;
  border-radius: 22px;
  background: rgba(255, 251, 246, 0.78);
  border: 1px solid rgba(113, 129, 102, 0.12);
}

.merchant-summary span {
  color: var(--muted);
  font-size: 13px;
}

.merchant-summary strong {
  display: block;
  margin-top: 6px;
  font-size: 1.2rem;
}

.merchant-form-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.form-panel {
  display: grid;
  gap: 10px;
  padding: 22px;
}

@media (max-width: 1100px) {
  .merchant-hero,
  .merchant-form-grid,
  .merchant-summary {
    grid-template-columns: 1fr;
  }
}
</style>
