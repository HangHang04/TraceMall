<script setup>
import { useRouter } from 'vue-router'
import AppShell from '@/components/layout/AppShell.vue'
import { useCartStore } from '@/stores/cart'
import { createOrder } from '@/api'

const router = useRouter()
const cart = useCartStore()

async function checkout() {
  if (!cart.items.length) return
  const payload = {
    items: cart.items.map((item) => ({ batchId: item.batchId, quantity: Number(item.quantity) })),
    shippingAddress: '默认演示地址',
    remark: 'web checkout',
  }
  await createOrder(payload)
  cart.clear()
  router.push('/orders')
}
</script>

<template>
  <AppShell title="购物车" subtitle="确认订单并提交支付流程">
    <section class="panel">
      <h3>购物车商品</h3>
      <div v-if="!cart.items.length">暂无商品</div>
      <div v-else class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>批次ID</th>
              <th>名称</th>
              <th>数量</th>
              <th>单价</th>
              <th>小计</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in cart.items" :key="item.batchId">
              <td>{{ item.batchId }}</td>
              <td>{{ item.fruitName }}</td>
              <td>{{ item.quantity }}</td>
              <td>¥{{ item.unitPrice }}</td>
              <td>¥{{ (Number(item.quantity) * Number(item.unitPrice)).toFixed(2) }}</td>
              <td><button class="btn btn-ghost" @click="cart.remove(item.batchId)">移除</button></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="cart-foot">
        <strong>总计：¥{{ cart.total.toFixed(2) }}</strong>
        <button class="btn btn-primary" @click="checkout">提交订单</button>
      </div>
    </section>
  </AppShell>
</template>