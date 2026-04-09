<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppShell from '@/components/layout/AppShell.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import { listFruits, traceDetail, verifyTrace } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { buildDeviceFingerprint } from '@/utils/traceQr'
import { toVerifyReasonLabel, toVerifyStatusLabel } from '@/utils/status'

const HISTORY_KEY = 'tracemall_verify_history'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const cart = useCartStore()

const topNavItems = [
  { key: 'dashboard', label: '工作台', path: '/dashboard', icon: 'dashboard' },
  { key: 'scan', label: '扫码验真', path: '/trace/scan', icon: 'scan' },
  { key: 'orders', label: '订单中心', path: '/orders', icon: 'insights' },
  { key: 'cart', label: '采购车', path: '/cart', icon: 'cart' },
]

const quickWindows = [
  { label: '近 7 天', value: 7 },
  { label: '近 30 天', value: 30 },
  { label: '近 90 天', value: 90 },
]

const priceBands = [
  { key: 'premium', label: '高端价位', test: (price) => Number(price) >= 25 },
  { key: 'balanced', label: '平衡价位', test: (price) => Number(price) >= 15 && Number(price) < 25 },
  { key: 'daily', label: '日常采购', test: (price) => Number(price) < 15 },
]

const fruits = ref([])
const loading = ref(false)
const keyword = ref('')
const activeFilterTab = ref('supply')
const selectedCategories = ref([])
const selectedOrigins = ref([])
const selectedShops = ref([])
const selectedPriceBands = ref([])
const selectedWindow = ref(30)
const dateRange = ref(buildDateRange(30))
const verifyResult = ref(null)
const verifyError = ref('')
const verifyHistory = ref([])
const verifySection = ref(null)
const pageError = ref('')

const verifyInput = ref({
  traceId: 'TRACE-APPLE-001',
  signature: '',
  geo: '成都',
  ip: '10.0.2.8',
  deviceFingerprint: buildDeviceFingerprint(),
})

const safeFruits = computed(() => (Array.isArray(fruits.value) ? fruits.value : []))
const safeHistory = computed(() => (Array.isArray(verifyHistory.value) ? verifyHistory.value : []))
const safeSelectedCategories = computed(() =>
  Array.isArray(selectedCategories.value) ? selectedCategories.value : [],
)
const safeSelectedOrigins = computed(() => (Array.isArray(selectedOrigins.value) ? selectedOrigins.value : []))
const safeSelectedShops = computed(() => (Array.isArray(selectedShops.value) ? selectedShops.value : []))
const safeSelectedPriceBands = computed(() =>
  Array.isArray(selectedPriceBands.value) ? selectedPriceBands.value : [],
)

const categories = computed(() => [...new Set(safeFruits.value.map((item) => item.category).filter(Boolean))])
const origins = computed(() => [...new Set(safeFruits.value.map((item) => item.origin).filter(Boolean))])
const shops = computed(() => [...new Set(safeFruits.value.map((item) => item.shopName).filter(Boolean))])

const filteredProducts = computed(() =>
  safeFruits.value.filter((item) => {
    const query = keyword.value.trim().toLowerCase()
    const matchesQuery =
      !query ||
      [item.fruitName, item.origin, item.shopName, item.description]
        .filter(Boolean)
        .some((text) => String(text).toLowerCase().includes(query))

    const matchesCategory =
      !safeSelectedCategories.value.length || safeSelectedCategories.value.includes(item.category)

    const matchesOrigin = !safeSelectedOrigins.value.length || safeSelectedOrigins.value.includes(item.origin)
    const matchesShop = !safeSelectedShops.value.length || safeSelectedShops.value.includes(item.shopName)

    const matchesPrice =
      !safeSelectedPriceBands.value.length ||
      safeSelectedPriceBands.value.some((bandKey) =>
        priceBands.find((band) => band.key === bandKey)?.test(item.unitPrice),
      )

    return matchesQuery && matchesCategory && matchesOrigin && matchesShop && matchesPrice
  }),
)

const featuredProducts = computed(() => filteredProducts.value.slice(0, 6))
const averagePrice = computed(() => {
  if (!filteredProducts.value.length) return '0.00'
  const total = filteredProducts.value.reduce((sum, item) => sum + Number(item.unitPrice || 0), 0)
  return (total / filteredProducts.value.length).toFixed(2)
})

const activeFilterCount = computed(
  () =>
    safeSelectedCategories.value.length +
    safeSelectedOrigins.value.length +
    safeSelectedShops.value.length +
    safeSelectedPriceBands.value.length +
    (keyword.value.trim() ? 1 : 0),
)

const windowHistory = computed(() => {
  const start = dateRange.value.start ? new Date(`${dateRange.value.start}T00:00:00`) : null
  const end = dateRange.value.end ? new Date(`${dateRange.value.end}T23:59:59`) : null

  return safeHistory.value.filter((item) => {
    const timestamp = Number(item.timestamp || Date.parse(item.at) || 0)
    if (!timestamp) return true
    if (start && timestamp < start.getTime()) return false
    if (end && timestamp > end.getTime()) return false
    return true
  })
})

const resultSnapshot = computed(() => ({
  count: filteredProducts.value.length,
  originCount: new Set(filteredProducts.value.map((item) => item.origin)).size,
  shopCount: new Set(filteredProducts.value.map((item) => item.shopName)).size,
  averagePrice: averagePrice.value,
}))

const latestCandidate = computed(() => featuredProducts.value[0] || safeFruits.value[0] || null)
const latestHistory = computed(() => windowHistory.value[0] || safeHistory.value[0] || null)

const assistantMessages = computed(() => {
  const focusCategory = safeSelectedCategories.value[0] || latestCandidate.value?.category || '果品'
  const focusOrigin = safeSelectedOrigins.value[0] || latestCandidate.value?.origin || '多产地'
  const focusProduct = latestCandidate.value?.fruitName || '当前候选商品'
  const verifyStatus = verifyResult.value?.status
    ? toVerifyStatusLabel(verifyResult.value.status)
    : latestHistory.value?.status
      ? toVerifyStatusLabel(latestHistory.value.status)
      : '待验证'

  return [
    {
      role: 'assistant',
      title: '采购助理',
      text: `当前视图保留 ${resultSnapshot.value.count} 个候选商品，覆盖 ${resultSnapshot.value.originCount} 个产地，平均客单价约 ¥${resultSnapshot.value.averagePrice}。`,
    },
    {
      role: 'user',
      title: '当前意图',
      text: `优先关注 ${focusCategory}，来源锁定 ${focusOrigin}，并保留可快速验真的批次。`,
    },
    {
      role: 'assistant',
      title: '建议动作',
      text: `${focusProduct} 适合作为首个验真样本。当前风险状态为 ${verifyStatus}，可继续在下方录入溯源码获取更完整的批次判断。`,
    },
  ]
})

const verificationSummary = computed(() => {
  if (!verifyResult.value) {
    return [
      { label: '当前状态', value: '等待验真' },
      { label: '关注点', value: '建议先校验批次签名' },
      { label: '候选样本', value: latestCandidate.value?.fruitName || '暂无商品' },
      { label: '参考产地', value: latestCandidate.value?.origin || '未指定' },
    ]
  }

  return [
    { label: '验真状态', value: toVerifyStatusLabel(verifyResult.value.status) },
    { label: '风险说明', value: toVerifyReasonLabel(verifyResult.value.reason) },
    { label: '商品名称', value: verifyResult.value.batchSummary?.fruitName || '-' },
    { label: '来源商家', value: verifyResult.value.batchSummary?.shopName || '-' },
  ]
})

const avatarText = computed(() => {
  const source = auth.username || 'TraceMall'
  return source.slice(0, 2).toUpperCase()
})

function buildDateRange(days) {
  const end = new Date()
  const start = new Date(end)
  start.setDate(end.getDate() - days + 1)
  return {
    start: toDateInputValue(start),
    end: toDateInputValue(end),
  }
}

function toDateInputValue(date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

function readHistory() {
  try {
    const raw = localStorage.getItem(HISTORY_KEY)
    const parsed = raw ? JSON.parse(raw) : []
    verifyHistory.value = Array.isArray(parsed) ? parsed : []
  } catch {
    verifyHistory.value = []
  }
}

function saveHistory() {
  localStorage.setItem(HISTORY_KEY, JSON.stringify(verifyHistory.value.slice(0, 8)))
}

function toggleSelection(target, value) {
  const current = Array.isArray(target?.value) ? target.value : []
  target.value = current.includes(value)
    ? current.filter((item) => item !== value)
    : [...current, value]
}

function isSelected(target, value) {
  return Array.isArray(target?.value) ? target.value.includes(value) : false
}

function setQuickWindow(days) {
  selectedWindow.value = days
  dateRange.value = buildDateRange(days)
}

function syncCustomWindow() {
  const matched = quickWindows.find(
    (option) => JSON.stringify(buildDateRange(option.value)) === JSON.stringify(dateRange.value),
  )
  selectedWindow.value = matched?.value || 0
}

function resetFilters() {
  keyword.value = ''
  selectedCategories.value = []
  selectedOrigins.value = []
  selectedShops.value = []
  selectedPriceBands.value = []
}

async function loadFruits() {
  loading.value = true
  try {
    const data = await listFruits()
    fruits.value = Array.isArray(data) ? data : []
  } finally {
    loading.value = false
  }
}

async function seedSignature() {
  if (verifyInput.value.signature) return
  try {
    const trace = await traceDetail(verifyInput.value.traceId)
    verifyInput.value.signature = trace?.summary?.signature || ''
  } catch {}
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
  verifyInput.value.deviceFingerprint = buildDeviceFingerprint()

  try {
    verifyResult.value = await verifyTrace(verifyInput.value)
    const now = Date.now()
    verifyHistory.value = [
      {
        traceId: verifyInput.value.traceId,
        status: verifyResult.value?.status || 'UNKNOWN',
        reason: verifyResult.value?.reason || '',
        at: new Date(now).toLocaleString('zh-CN'),
        timestamp: now,
        fruitName: verifyResult.value?.batchSummary?.fruitName || '',
        shopName: verifyResult.value?.batchSummary?.shopName || '',
      },
      ...verifyHistory.value,
    ].slice(0, 8)
    saveHistory()
  } catch (error) {
    verifyError.value = error.message || '验真请求失败，请稍后重试。'
  }
}

function scrollToVerify() {
  verifySection.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function isActiveNav(path) {
  return route.path === path || route.path.startsWith(`${path}/`)
}

onMounted(async () => {
  readHistory()
  try {
    await loadFruits()
  } catch (error) {
    pageError.value = error.message || '商品数据加载失败，请稍后重试。'
  }
  await seedSignature()
})
</script>

<template>
  <AppShell layout="topbar" :show-page-head="false">
    <template #topbar>
      <header class="workspace-topbar">
        <button class="workspace-brand" @click="router.push('/dashboard')">
          <span class="workspace-brand-mark">T</span>
          <span class="workspace-brand-copy">
            <strong>TraceMall</strong>
            <small>可信果品采购平台</small>
          </span>
        </button>

        <nav class="workspace-nav">
          <button
            v-for="item in topNavItems"
            :key="item.key"
            :class="['workspace-nav-item', { active: isActiveNav(item.path) }]"
            @click="router.push(item.path)"
          >
            <span class="workspace-nav-icon"><NavIcon :name="item.icon" /></span>
            <span>{{ item.label }}</span>
          </button>
        </nav>

        <div class="workspace-tools">
          <label class="workspace-search">
            <span class="workspace-tool-icon"><NavIcon name="search" /></span>
            <input v-model="keyword" type="search" placeholder="搜索商品、产地或商家..." />
          </label>
          <button class="workspace-tool-button" type="button" @click="router.push('/trace/scan')">
            <NavIcon name="scan" />
          </button>
          <button class="workspace-tool-button" type="button" @click="router.push('/orders')">
            <NavIcon name="help" />
          </button>
          <button class="workspace-avatar" type="button" @click="router.push('/cart')">
            {{ avatarText }}
          </button>
        </div>
      </header>
    </template>

    <div class="consumer-dashboard">
      <section class="dashboard-hero">
        <div class="dashboard-hero-copy">
          <span class="dashboard-kicker">可信果品采购洞察</span>
          <h1>可信果品采购工作台</h1>
          <p class="dashboard-subtitle">
            把商品筛选、来源判断与批次验真整合进一个更克制的消费者入口，先完成选择，再做验证与下单。
          </p>

          <div class="hero-meta">
            <article class="hero-meta-card">
              <span>候选商品</span>
              <strong>{{ resultSnapshot.count }}</strong>
            </article>
            <article class="hero-meta-card">
              <span>覆盖产地</span>
              <strong>{{ resultSnapshot.originCount }}</strong>
            </article>
            <article class="hero-meta-card">
              <span>平均单价</span>
              <strong>¥{{ resultSnapshot.averagePrice }}</strong>
            </article>
          </div>
        </div>

        <div class="dashboard-hero-actions">
          <button class="btn btn-ghost dashboard-btn dashboard-btn-secondary" @click="router.push('/orders')">
            <span class="btn-icon"><NavIcon name="insights" /></span>
            <span>查看订单流转</span>
          </button>
          <button class="btn btn-primary dashboard-btn dashboard-btn-primary" @click="scrollToVerify">
            <span class="btn-icon"><NavIcon name="assistant" /></span>
            <span>开始批次验真</span>
          </button>
        </div>
      </section>

      <section class="dashboard-card timeline-card">
        <div class="card-head">
          <div>
            <p class="card-eyebrow">观察周期</p>
            <h2>数据观察周期</h2>
          </div>
          <span class="card-badge">{{ windowHistory.length }} 条验真记录</span>
        </div>

        <div class="timeline-controls">
          <div class="quick-pills">
            <button
              v-for="option in quickWindows"
              :key="option.value"
              :class="['pill-button', { selected: selectedWindow === option.value }]"
              type="button"
              @click="setQuickWindow(option.value)"
            >
              {{ option.label }}
            </button>
          </div>

          <div class="date-range">
            <label class="date-field">
              <span>开始日期</span>
              <div class="date-input-wrap">
                <span class="date-icon"><NavIcon name="calendar" /></span>
                <input v-model="dateRange.start" type="date" @change="syncCustomWindow" />
              </div>
            </label>
            <label class="date-field">
              <span>结束日期</span>
              <div class="date-input-wrap">
                <span class="date-icon"><NavIcon name="calendar" /></span>
                <input v-model="dateRange.end" type="date" @change="syncCustomWindow" />
              </div>
            </label>
          </div>
        </div>
      </section>

      <section class="dashboard-main-grid">
        <aside class="dashboard-card rail-card">
          <div class="card-head">
            <div>
              <p class="card-eyebrow">品类筛选</p>
              <h2>品类焦点</h2>
            </div>
            <span class="card-badge">{{ selectedCategories.length || categories.length }} 项</span>
          </div>

          <div class="pill-column">
            <button
              v-for="item in categories"
              :key="item"
              :class="['pill-button pill-button-block', { selected: isSelected(selectedCategories, item) }]"
              type="button"
              @click="toggleSelection(selectedCategories, item)"
            >
              {{ item }}
            </button>
          </div>

          <div class="rail-summary">
            <div>
              <span>采购车商品</span>
              <strong>{{ cart.count }}</strong>
            </div>
            <div>
              <span>当前商家</span>
              <strong>{{ resultSnapshot.shopCount }}</strong>
            </div>
          </div>
        </aside>

        <section class="dashboard-card conditions-card">
          <div class="card-head">
            <div>
              <p class="card-eyebrow">筛选工作区</p>
              <h2>采购条件</h2>
            </div>
            <div class="filter-summary">
              <span>{{ activeFilterCount }} 项筛选已应用</span>
              <button class="text-button" type="button" @click="resetFilters">清空</button>
            </div>
          </div>

          <div class="segment-switch">
            <button
              :class="['segment-button', { active: activeFilterTab === 'supply' }]"
              type="button"
              @click="activeFilterTab = 'supply'"
            >
              产地与价格
            </button>
            <button
              :class="['segment-button', { active: activeFilterTab === 'merchant' }]"
              type="button"
              @click="activeFilterTab = 'merchant'"
            >
              商家与渠道
            </button>
          </div>

          <div class="condition-groups" v-if="activeFilterTab === 'supply'">
            <div class="condition-group">
              <span class="condition-label">产地来源</span>
              <div class="pill-grid">
                <button
                  v-for="item in origins"
                  :key="item"
                  :class="['pill-button', { selected: isSelected(selectedOrigins, item) }]"
                  type="button"
                  @click="toggleSelection(selectedOrigins, item)"
                >
                  {{ item }}
                </button>
              </div>
            </div>

            <div class="condition-group">
              <span class="condition-label">价格带</span>
              <div class="pill-grid">
                <button
                  v-for="item in priceBands"
                  :key="item.key"
                  :class="['pill-button', { selected: isSelected(selectedPriceBands, item.key) }]"
                  type="button"
                  @click="toggleSelection(selectedPriceBands, item.key)"
                >
                  {{ item.label }}
                </button>
              </div>
            </div>
          </div>

          <div class="condition-groups" v-else>
            <div class="condition-group">
              <span class="condition-label">商家渠道</span>
              <div class="pill-grid">
                <button
                  v-for="item in shops"
                  :key="item"
                  :class="['pill-button', { selected: isSelected(selectedShops, item) }]"
                  type="button"
                  @click="toggleSelection(selectedShops, item)"
                >
                  {{ item }}
                </button>
              </div>
            </div>

            <div class="condition-group">
              <span class="condition-label">筛选说明</span>
              <div class="condition-note-list">
                <article class="condition-note">
                  <strong>优先显示来源完整的在售商品</strong>
                  <p>当前布局保留未来对接更多来源标签的空间，现阶段先依据商家与产地做轻量筛选。</p>
                </article>
                <article class="condition-note">
                  <strong>验真入口仍在当前页面保留</strong>
                  <p>用户完成筛选后，可以直接向下进入批次验真区，不需要切换成独立工作流。</p>
                </article>
              </div>
            </div>
          </div>
        </section>

        <aside class="dashboard-card assistant-card">
          <div class="assistant-head">
            <div>
              <p class="card-eyebrow">智能助理</p>
              <h2>采购助理</h2>
            </div>
            <span class="assistant-status"><i></i>在线</span>
          </div>

          <div class="assistant-feed">
            <article
              v-for="(message, index) in assistantMessages"
              :key="`${message.role}-${index}`"
              :class="['assistant-bubble', message.role]"
            >
              <span class="assistant-bubble-title">{{ message.title }}</span>
              <p>{{ message.text }}</p>
            </article>
          </div>

          <div class="assistant-footer">
            <button class="pill-button pill-button-soft" type="button" @click="activeFilterTab = 'supply'">
              调整供应筛选
            </button>
            <button class="pill-button pill-button-soft" type="button" @click="scrollToVerify">
              进入验真区
            </button>
          </div>
        </aside>
      </section>

      <section class="dashboard-bottom-grid">
        <section class="dashboard-card products-card">
          <div class="card-head">
            <div>
              <p class="card-eyebrow">精选商品</p>
              <h2>候选商品</h2>
            </div>
            <span class="card-badge">{{ filteredProducts.length }} 个结果</span>
          </div>

          <div class="empty-state products-empty" v-if="pageError">
            {{ pageError }}
          </div>

          <p class="products-copy" v-else>优先展示与当前筛选条件匹配的商品，保留详情与加购动作，方便继续沿用现有业务流程。</p>

          <div class="products-grid" v-if="!pageError && !loading && featuredProducts.length">
            <article class="product-card" v-for="item in featuredProducts" :key="item.id">
              <div class="product-card-top">
                <div class="product-chip-row">
                  <span class="small-chip">{{ item.category }}</span>
                  <span class="small-chip muted-chip">{{ item.shopName }}</span>
                </div>
                <button class="icon-button" type="button" @click="router.push(`/product/${item.id}`)">
                  <NavIcon name="chain" />
                </button>
              </div>

              <div class="product-card-body">
                <h3>{{ item.fruitName }}</h3>
                <p class="product-meta">{{ item.origin }} · {{ item.unit }}</p>
                <p class="product-desc">{{ item.description }}</p>
              </div>

              <div class="product-card-foot">
                <div>
                  <span class="price-label">参考单价</span>
                  <strong>¥{{ item.unitPrice }}</strong>
                </div>
                <div class="product-actions">
                  <button class="btn btn-ghost dashboard-btn dashboard-btn-secondary" @click="router.push(`/product/${item.id}`)">
                    查看详情
                  </button>
                  <button class="btn btn-primary dashboard-btn dashboard-btn-primary" @click="addToCart(item)">
                    加入采购车
                  </button>
                </div>
              </div>
            </article>
          </div>

          <div class="empty-state products-empty" v-else-if="!pageError && !loading">
            当前筛选条件下暂无商品，建议放宽产地或价格带后再试。
          </div>

          <div class="products-loading" v-else-if="!pageError">正在载入商品数据...</div>
        </section>

        <section ref="verifySection" class="dashboard-card verify-card">
          <div class="card-head">
            <div>
              <p class="card-eyebrow">批次验真</p>
              <h2>快速验真</h2>
            </div>
            <span class="card-badge">实时校验</span>
          </div>

          <div class="verify-form">
            <label class="verify-field">
              <span>溯源码</span>
              <input v-model="verifyInput.traceId" placeholder="TRACE-XXXX" />
            </label>
            <label class="verify-field">
              <span>签名摘要</span>
              <input v-model="verifyInput.signature" placeholder="请输入签名摘要" />
            </label>
            <label class="verify-field">
              <span>扫描位置</span>
              <input v-model="verifyInput.geo" placeholder="例如：成都" />
            </label>
            <button class="btn btn-primary dashboard-btn dashboard-btn-primary verify-submit" @click="doVerify">
              <span class="btn-icon"><NavIcon name="verify" /></span>
              <span>执行验真</span>
            </button>
          </div>

          <p class="error" v-if="verifyError">{{ verifyError }}</p>

          <div class="verify-summary-grid">
            <article class="verify-summary-card" v-for="item in verificationSummary" :key="item.label">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
            </article>
          </div>

          <div class="verify-history-block">
            <div class="verify-history-head">
              <span>观察周期内历史记录</span>
              <strong>{{ windowHistory.length }}</strong>
            </div>

            <div class="verify-history-list" v-if="windowHistory.length">
              <article class="verify-history-item" v-for="item in windowHistory.slice(0, 4)" :key="`${item.traceId}-${item.timestamp || item.at}`">
                <div>
                  <strong>{{ item.traceId }}</strong>
                  <p>{{ item.fruitName || '已验证批次' }}</p>
                </div>
                <div class="verify-history-meta">
                  <span>{{ toVerifyStatusLabel(item.status) }}</span>
                  <time>{{ item.at }}</time>
                </div>
              </article>
            </div>
            <div class="verify-history-empty" v-else>当前时间窗口内还没有验真记录。</div>
          </div>
        </section>
      </section>
    </div>
  </AppShell>
</template>

<style scoped>
.consumer-dashboard {
  --dashboard-card: rgba(255, 255, 252, 0.92);
  --dashboard-card-strong: #fcfcf8;
  --dashboard-line: rgba(77, 91, 86, 0.11);
  --dashboard-shadow: 0 18px 36px rgba(31, 45, 40, 0.06);
  --dashboard-shadow-soft: 0 10px 20px rgba(31, 45, 40, 0.04);
  --dashboard-title: #223746;
  --dashboard-text: #62707b;
  --dashboard-strong: #5f7f46;
  --dashboard-strong-deep: #526f3f;
  --dashboard-strong-muted: #f2f6eb;
  max-width: 1360px;
  margin: 0 auto;
  display: grid;
  gap: 24px;
}

.workspace-topbar {
  position: sticky;
  top: 0;
  z-index: 30;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 20px;
  padding: 18px 28px;
  border-bottom: 1px solid rgba(77, 91, 86, 0.1);
  background: rgba(248, 249, 244, 0.92);
  backdrop-filter: blur(12px);
}

.workspace-brand,
.workspace-nav-item,
.workspace-tool-button,
.workspace-avatar,
.icon-button,
.text-button {
  border: 0;
  background: transparent;
  cursor: pointer;
}

.workspace-brand {
  display: inline-flex;
  align-items: center;
  gap: 14px;
  padding: 0;
  text-align: left;
}

.workspace-brand-mark {
  width: 50px;
  height: 50px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: linear-gradient(180deg, #89a96d, #5e7d46);
  color: #fffef8;
  font-size: 24px;
  font-weight: 800;
  box-shadow: 0 14px 30px rgba(95, 127, 70, 0.22);
}

.workspace-brand-copy {
  display: grid;
  gap: 2px;
}

.workspace-brand-copy strong {
  color: var(--dashboard-title);
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.04em;
}

.workspace-brand-copy small {
  color: var(--dashboard-text);
  font-size: 13px;
  letter-spacing: 0.02em;
}

.workspace-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.workspace-nav-item {
  min-height: 54px;
  padding: 0 18px;
  border-radius: 18px;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: #62707c;
  font-size: 15px;
  font-weight: 600;
  transition:
    background-color 0.18s ease,
    color 0.18s ease,
    border-color 0.18s ease;
}

.workspace-nav-item:hover,
.workspace-tool-button:hover,
.icon-button:hover,
.text-button:hover {
  color: var(--dashboard-title);
}

.workspace-nav-item.active {
  color: var(--dashboard-strong-deep);
  background: rgba(235, 241, 226, 0.96);
  box-shadow: inset 0 0 0 1px rgba(95, 127, 70, 0.24);
}

.workspace-nav-icon,
.workspace-tool-icon,
.date-icon {
  width: 18px;
  height: 18px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.workspace-tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.workspace-search {
  min-width: 320px;
  padding: 0 16px;
  border-radius: 18px;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 252, 0.94);
  border: 1px solid rgba(77, 91, 86, 0.12);
  box-shadow: 0 8px 22px rgba(36, 50, 45, 0.04);
}

.workspace-search input {
  height: 56px;
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
}

.workspace-search input:focus {
  outline: none;
}

.workspace-tool-button,
.workspace-avatar,
.icon-button {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  color: #65747e;
  background: rgba(255, 255, 252, 0.88);
  border: 1px solid rgba(77, 91, 86, 0.1);
  box-shadow: var(--dashboard-shadow-soft);
  transition:
    transform 0.16s ease,
    box-shadow 0.16s ease,
    border-color 0.16s ease;
}

.workspace-tool-button:hover,
.workspace-avatar:hover,
.icon-button:hover {
  transform: translateY(-1px);
  border-color: rgba(95, 127, 70, 0.2);
}

.workspace-avatar {
  color: #fffef8;
  background: linear-gradient(180deg, #86a268, #5f7f46);
  border-color: transparent;
  font-size: 14px;
  font-weight: 700;
}

.dashboard-card {
  border-radius: 32px;
  border: 1px solid var(--dashboard-line);
  background: var(--dashboard-card);
  box-shadow: var(--dashboard-shadow);
}

.dashboard-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: start;
  gap: 24px;
  padding: 10px 4px 2px;
}

.dashboard-hero-copy {
  display: grid;
  gap: 18px;
}

.dashboard-kicker,
.card-eyebrow {
  color: var(--dashboard-strong);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.dashboard-hero h1 {
  max-width: 720px;
  color: var(--dashboard-title);
  font-size: clamp(48px, 5.4vw, 68px);
  line-height: 0.98;
  letter-spacing: -0.06em;
}

.dashboard-subtitle {
  max-width: 720px;
  color: var(--dashboard-text);
  font-size: 18px;
  line-height: 1.7;
}

.hero-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 180px));
  gap: 16px;
}

.hero-meta-card {
  padding: 18px 20px;
  border-radius: 24px;
  border: 1px solid rgba(77, 91, 86, 0.09);
  background: rgba(255, 255, 252, 0.68);
}

.hero-meta-card span,
.price-label,
.verify-summary-card span,
.verify-history-head span,
.verify-history-item p,
.condition-label,
.rail-summary span {
  color: var(--dashboard-text);
  font-size: 13px;
}

.hero-meta-card strong,
.price-label + strong,
.verify-summary-card strong,
.rail-summary strong {
  margin-top: 6px;
  display: block;
  color: var(--dashboard-title);
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.05em;
}

.dashboard-hero-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.dashboard-btn {
  min-height: 56px;
  padding: 0 22px;
  border-radius: 20px;
  font-size: 15px;
}

.dashboard-btn-primary {
  background: linear-gradient(180deg, #6f8e53, #597841);
  box-shadow: 0 16px 30px rgba(89, 120, 65, 0.2);
}

.dashboard-btn-secondary {
  color: var(--dashboard-strong-deep);
  background: rgba(237, 244, 228, 0.82);
  border: 1px solid rgba(95, 127, 70, 0.24);
}

.card-head,
.assistant-head,
.verify-history-head,
.product-card-top,
.product-card-foot,
.filter-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.card-head {
  padding: 28px 28px 0;
}

.card-head h2,
.assistant-head h2 {
  color: var(--dashboard-title);
  font-size: 34px;
  line-height: 1.05;
  letter-spacing: -0.05em;
}

.card-badge {
  min-height: 40px;
  padding: 0 14px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  color: var(--dashboard-strong-deep);
  background: var(--dashboard-strong-muted);
  border: 1px solid rgba(95, 127, 70, 0.18);
  font-size: 13px;
  font-weight: 700;
}

.timeline-card {
  padding-bottom: 28px;
}

.timeline-controls {
  padding: 22px 28px 0;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 24px;
  align-items: center;
}

.quick-pills,
.pill-grid,
.assistant-footer,
.product-chip-row,
.product-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.pill-button,
.segment-button {
  min-height: 46px;
  padding: 0 18px;
  border-radius: 999px;
  border: 1px solid rgba(95, 127, 70, 0.18);
  color: #5c6f5a;
  background: #f7f9f2;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition:
    background-color 0.16s ease,
    border-color 0.16s ease,
    color 0.16s ease,
    transform 0.16s ease,
    box-shadow 0.16s ease;
}

.pill-button:hover,
.segment-button:hover {
  transform: translateY(-1px);
  border-color: rgba(95, 127, 70, 0.32);
  color: var(--dashboard-strong-deep);
}

.pill-button.selected,
.segment-button.active {
  color: #fffef8;
  background: linear-gradient(180deg, #86a268, #5f7f46);
  border-color: transparent;
  box-shadow: 0 12px 24px rgba(95, 127, 70, 0.18);
}

.pill-button:focus-visible,
.segment-button:focus-visible,
.workspace-nav-item:focus-visible,
.workspace-tool-button:focus-visible,
.workspace-avatar:focus-visible,
.text-button:focus-visible,
.icon-button:focus-visible {
  outline: 3px solid rgba(115, 149, 86, 0.18);
  outline-offset: 2px;
}

.pill-button-block {
  width: 100%;
  justify-content: flex-start;
}

.pill-button-soft {
  min-height: 44px;
  background: rgba(247, 249, 242, 0.96);
}

.date-range {
  display: flex;
  gap: 16px;
}

.date-field {
  min-width: 200px;
  display: grid;
  gap: 8px;
}

.date-field > span,
.verify-field span {
  color: var(--dashboard-text);
  font-size: 13px;
  font-weight: 600;
}

.date-input-wrap {
  height: 52px;
  padding: 0 14px;
  border-radius: 18px;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: var(--dashboard-card-strong);
  border: 1px solid var(--dashboard-line);
}

.date-input-wrap input {
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
}

.date-input-wrap:focus-within,
.workspace-search:focus-within {
  border-color: rgba(95, 127, 70, 0.34);
  box-shadow: 0 0 0 4px rgba(115, 149, 86, 0.1);
}

.date-input-wrap input:focus {
  outline: none;
}

.dashboard-main-grid,
.dashboard-bottom-grid {
  display: grid;
  gap: 22px;
}

.dashboard-main-grid {
  grid-template-columns: 290px minmax(0, 1fr) 320px;
}

.dashboard-bottom-grid {
  grid-template-columns: minmax(0, 1.35fr) minmax(360px, 0.8fr);
  align-items: start;
}

.rail-card {
  padding: 0 0 28px;
}

.pill-column,
.condition-groups,
.condition-note-list,
.assistant-feed,
.verify-form,
.verify-summary-grid,
.verify-history-list {
  display: grid;
  gap: 14px;
}

.pill-column,
.condition-groups,
.assistant-feed,
.verify-form,
.verify-summary-grid,
.verify-history-block,
.products-card,
.verify-card {
  padding: 22px 28px 0;
}

.rail-summary {
  margin: 22px 28px 0;
  padding: 18px;
  border-radius: 24px;
  display: grid;
  gap: 16px;
  background: var(--dashboard-strong-muted);
  border: 1px solid rgba(95, 127, 70, 0.1);
}

.segment-switch {
  margin: 22px 28px 0;
  padding: 6px;
  border-radius: 22px;
  display: inline-grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px;
  background: #f3f5ee;
  border: 1px solid rgba(77, 91, 86, 0.08);
}

.condition-group {
  display: grid;
  gap: 12px;
}

.condition-note {
  padding: 18px;
  border-radius: 24px;
  border: 1px solid rgba(77, 91, 86, 0.08);
  background: rgba(255, 255, 252, 0.76);
}

.condition-note strong,
.product-card h3 {
  color: var(--dashboard-title);
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.03em;
}

.condition-note p,
.products-copy,
.product-desc,
.assistant-bubble p,
.verify-history-item time {
  margin-top: 8px;
  color: var(--dashboard-text);
  line-height: 1.7;
}

.text-button {
  padding: 0;
  color: var(--dashboard-strong-deep);
  font-size: 14px;
  font-weight: 700;
}

.assistant-card {
  padding: 28px;
  display: grid;
  gap: 20px;
}

.assistant-head {
  padding: 0;
}

.assistant-status {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--dashboard-text);
  font-size: 13px;
  font-weight: 600;
}

.assistant-status i {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #4fb86c;
}

.assistant-feed,
.assistant-footer {
  padding: 0;
}

.assistant-bubble {
  padding: 16px 18px;
  border-radius: 24px;
  max-width: 100%;
}

.assistant-bubble.assistant {
  background: #f3f5ee;
}

.assistant-bubble.user {
  margin-left: auto;
  background: rgba(235, 241, 226, 0.96);
}

.assistant-bubble-title {
  color: var(--dashboard-title);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.products-card,
.verify-card {
  padding-bottom: 28px;
}

.products-copy {
  padding: 18px 28px 0;
}

.products-grid {
  padding: 22px 28px 0;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.product-card {
  padding: 22px;
  border-radius: 28px;
  border: 1px solid rgba(77, 91, 86, 0.09);
  background: rgba(255, 255, 252, 0.9);
  box-shadow: var(--dashboard-shadow-soft);
}

.small-chip {
  min-height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  color: var(--dashboard-strong-deep);
  background: var(--dashboard-strong-muted);
  font-size: 12px;
  font-weight: 700;
}

.muted-chip {
  color: #5d6870;
  background: rgba(99, 112, 123, 0.08);
}

.product-card-body {
  margin-top: 18px;
}

.product-meta {
  margin-top: 8px;
  color: #57656f;
  font-size: 14px;
  font-weight: 600;
}

.product-card-foot {
  margin-top: 20px;
  align-items: flex-end;
}

.price-label + strong {
  font-size: 30px;
}

.verify-field {
  display: grid;
  gap: 8px;
}

.verify-submit {
  width: 100%;
  margin-top: 4px;
}

.verify-summary-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.verify-summary-card {
  padding: 18px;
  border-radius: 24px;
  border: 1px solid rgba(77, 91, 86, 0.09);
  background: rgba(255, 255, 252, 0.88);
}

.verify-history-block {
  padding-top: 20px;
}

.verify-history-list {
  padding: 16px 0 0;
}

.verify-history-item {
  padding: 16px 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  border-top: 1px solid rgba(77, 91, 86, 0.08);
}

.verify-history-item:first-child {
  border-top: 0;
  padding-top: 0;
}

.verify-history-meta {
  display: grid;
  gap: 4px;
  text-align: right;
}

.verify-history-meta span {
  color: var(--dashboard-strong-deep);
  font-size: 13px;
  font-weight: 700;
}

.verify-history-empty,
.products-loading,
.products-empty {
  margin: 22px 28px 0;
}

.products-empty,
.verify-history-empty {
  padding: 24px;
  border-radius: 24px;
  border: 1px dashed rgba(77, 91, 86, 0.16);
  color: var(--dashboard-text);
  text-align: center;
}

@media (max-width: 1320px) {
  .workspace-topbar {
    grid-template-columns: 1fr;
  }

  .workspace-nav {
    justify-content: flex-start;
    overflow-x: auto;
    padding-bottom: 4px;
  }

  .workspace-tools {
    justify-content: space-between;
  }

  .dashboard-main-grid,
  .dashboard-bottom-grid {
    grid-template-columns: 1fr;
  }

  .products-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 960px) {
  .workspace-topbar {
    padding: 16px 18px;
  }

  .workspace-tools {
    flex-wrap: wrap;
  }

  .workspace-search {
    min-width: 100%;
  }

  .dashboard-hero,
  .timeline-controls {
    grid-template-columns: 1fr;
  }

  .dashboard-hero-actions,
  .date-range {
    flex-direction: column;
    align-items: stretch;
  }

  .hero-meta,
  .products-grid,
  .verify-summary-grid {
    grid-template-columns: 1fr;
  }

  .product-card-foot,
  .workspace-tools {
    align-items: stretch;
  }

  .product-card-foot,
  .product-actions {
    flex-direction: column;
  }
}

@media (max-width: 640px) {
  .consumer-dashboard {
    gap: 18px;
  }

  .dashboard-hero h1 {
    font-size: 40px;
  }

  .dashboard-card {
    border-radius: 26px;
  }

  .card-head,
  .pill-column,
  .condition-groups,
  .assistant-card,
  .products-card,
  .verify-card,
  .timeline-controls,
  .products-copy {
    padding-left: 20px;
    padding-right: 20px;
  }

  .card-head h2,
  .assistant-head h2 {
    font-size: 28px;
  }

  .workspace-brand-copy strong {
    font-size: 24px;
  }
}
</style>
