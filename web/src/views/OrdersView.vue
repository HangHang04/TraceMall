<script setup>
import { computed, onMounted, ref } from 'vue'
import AppShell from '@/components/layout/AppShell.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import { listMyOrders, payOrder } from '@/api'
import { toOrderStatusLabel } from '@/utils/status'

const orders = ref([])
const loading = ref(false)

const payableOrders = computed(() => orders.value.filter((order) => order.status === 'PENDING_PAYMENT').length)

async function loadOrders() {
  loading.value = true
  try {
    orders.value = await listMyOrders()
  } finally {
    loading.value = false
  }
}

async function pay(orderNo) {
  await payOrder(orderNo)
  await loadOrders()
}

onMounted(loadOrders)
</script>

<template>
  <AppShell title="我的订单" subtitle="跟踪支付状态与订单流转结果">
    <section class="orders-hero panel">
      <div>
        <p class="orders-kicker">Orders Overview</p>
        <h2>当前共有 {{ orders.length }} 笔订单，待支付 {{ payableOrders }} 笔。</h2>
      </div>
      <div class="orders-hint">
        <span>支持演示支付</span>
        <strong>订单状态会在支付后刷新</strong>
      </div>
    </section>

    <section class="panel orders-panel">
      <div class="panel-head">
        <h3>订单列表</h3>
        <p>按订单号、支付状态和支付流水查看当前结果。</p>
      </div>
      <p v-if="loading">加载中...</p>
      <div class="orders-list" v-else>
        <article class="order-item" v-for="order in orders" :key="order.orderNo">
          <div class="order-main">
            <p class="order-no">{{ order.orderNo }}</p>
            <h4>{{ toOrderStatusLabel(order.status) }}</h4>
            <p class="order-meta">支付流水 {{ order.paymentRef || '-' }}</p>
          </div>
          <div class="order-side">
            <strong>¥{{ order.payableAmount }}</strong>
            <button class="btn btn-primary" v-if="order.status === 'PENDING_PAYMENT'" @click="pay(order.orderNo)">
              <span class="btn-icon"><NavIcon name="verify" /></span>
              <span>模拟支付</span>
            </button>
            <span class="order-static" v-else>无需操作</span>
          </div>
        </article>
      </div>
    </section>
  </AppShell>
</template>

<style scoped>
.orders-hero,
.orders-panel {
  padding: 24px;
}

.orders-hero {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: end;
  background:
    radial-gradient(circle at 0% 0%, rgba(255, 220, 186, 0.42), transparent 24%),
    linear-gradient(135deg, rgba(253, 248, 241, 0.96), rgba(240, 246, 237, 0.88));
}

.orders-kicker {
  color: var(--muted);
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.orders-hero h2 {
  margin-top: 10px;
  font-family: var(--font-display);
  font-size: clamp(2rem, 4vw, 3.4rem);
  line-height: 1;
}

.orders-hint {
  display: grid;
  gap: 6px;
  padding: 18px;
  border-radius: 22px;
  background: rgba(255, 251, 246, 0.78);
  border: 1px solid rgba(113, 129, 102, 0.12);
}

.orders-hint span,
.order-no,
.order-meta,
.order-static {
  color: var(--muted);
}

.orders-list {
  display: grid;
  gap: 12px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 18px 0;
  border-bottom: 1px dashed var(--line);
}

.order-item:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}

.order-main h4 {
  margin-top: 8px;
  font-family: var(--font-display);
  font-size: 1.9rem;
  line-height: 1;
}

.order-side {
  display: grid;
  justify-items: end;
  gap: 10px;
}

@media (max-width: 900px) {
  .orders-hero,
  .order-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .order-side {
    justify-items: start;
  }
}
</style>
