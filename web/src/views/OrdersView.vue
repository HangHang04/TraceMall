<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/layout/AppShell.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import { listMyOrders, payOrder } from '@/api'
import { toOrderStatusLabel } from '@/utils/status'

const orders = ref([])
const loading = ref(false)

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
  <AppShell title="我的订单" subtitle="模拟支付并查看订单状态流转">
    <section class="panel">
      <h3 class="panel-title">
        <span class="title-icon"><NavIcon name="orders" /></span>
        <span>订单列表</span>
      </h3>
      <p v-if="loading">加载中...</p>
      <div class="table-wrap" v-else>
        <table class="table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>状态</th>
              <th>金额</th>
              <th>支付号</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.orderNo">
              <td>{{ order.orderNo }}</td>
              <td>{{ toOrderStatusLabel(order.status) }}</td>
              <td>¥{{ order.payableAmount }}</td>
              <td>{{ order.paymentRef || '-' }}</td>
              <td>
                <button class="btn btn-primary" v-if="order.status === 'PENDING_PAYMENT'" @click="pay(order.orderNo)">
                  <span class="btn-icon"><NavIcon name="verify" /></span>
                  <span>模拟支付</span>
                </button>
                <span v-else>-</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>
