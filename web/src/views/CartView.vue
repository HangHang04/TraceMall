<script setup>
import { useRouter } from 'vue-router'
import AppShell from '@/components/layout/AppShell.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import { useCartStore } from '@/stores/cart'
import { createOrder } from '@/api'

const router = useRouter()
const cart = useCartStore()

async function checkout() {
  if (!cart.items.length) return

  const payload = {
    items: cart.items.map((item) => ({
      batchId: item.batchId,
      quantity: Number(item.quantity),
    })),
    shippingAddress: '测试收货地址',
    remark: 'web checkout',
  }

  await createOrder(payload)
  cart.clear()
  router.push('/orders')
}
</script>

<template>
  <AppShell title="购物车" subtitle="确认购买数量并提交订单。">
    <section class="panel">
      <h3 class="panel-title">
        <span class="title-icon"><NavIcon name="cart" /></span>
        <span>待提交商品</span>
      </h3>

      <div class="empty-state" v-if="!cart.items.length">购物车还是空的，可以先回到首页挑选商品。</div>

      <div v-else class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>批次 ID</th>
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
              <td><strong>{{ item.fruitName }}</strong></td>
              <td>{{ item.quantity }}</td>
              <td>¥{{ item.unitPrice }}</td>
              <td>¥{{ (Number(item.quantity) * Number(item.unitPrice)).toFixed(2) }}</td>
              <td>
                <button class="btn btn-ghost" @click="cart.remove(item.batchId)">
                  <span class="btn-icon"><NavIcon name="refresh" /></span>
                  <span>移除</span>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="cart-foot" v-if="cart.items.length">
        <strong>总计：¥{{ cart.total.toFixed(2) }}</strong>
        <button class="btn btn-primary" @click="checkout">
          <span class="btn-icon"><NavIcon name="orders" /></span>
          <span>提交订单</span>
        </button>
      </div>
    </section>
  </AppShell>
</template>
