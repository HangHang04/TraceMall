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
    shippingAddress: '默认演示地址',
    remark: 'web checkout',
  }

  await createOrder(payload)
  cart.clear()
  router.push('/orders')
}
</script>

<template>
  <AppShell title="购物车" subtitle="确认当前选择，再进入订单支付流程">
    <section class="cart-layout" v-if="cart.items.length">
      <section class="panel cart-list-panel">
        <div class="panel-head">
          <h3>已选商品</h3>
          <p>当前购物车包含 {{ cart.items.length }} 个条目。</p>
        </div>
        <div class="cart-list">
          <article class="cart-item" v-for="item in cart.items" :key="item.batchId">
            <div>
              <p class="cart-item-kicker">批次 {{ item.batchId }}</p>
              <h4>{{ item.fruitName }}</h4>
              <p class="cart-item-meta">数量 {{ item.quantity }} · 单价 ¥{{ item.unitPrice }}</p>
            </div>
            <div class="cart-item-side">
              <strong>¥{{ (Number(item.quantity) * Number(item.unitPrice)).toFixed(2) }}</strong>
              <button class="btn btn-ghost" @click="cart.remove(item.batchId)">
                <span class="btn-icon"><NavIcon name="refresh" /></span>
                <span>移除</span>
              </button>
            </div>
          </article>
        </div>
      </section>

      <aside class="panel cart-summary-panel">
        <div class="panel-head">
          <h3>订单摘要</h3>
          <p>确认金额后直接提交演示订单。</p>
        </div>
        <div class="summary-row">
          <span>商品数</span>
          <strong>{{ cart.count }}</strong>
        </div>
        <div class="summary-row">
          <span>配送方式</span>
          <strong>冷链配送</strong>
        </div>
        <div class="summary-row total">
          <span>总计</span>
          <strong>¥{{ cart.total.toFixed(2) }}</strong>
        </div>
        <button class="btn btn-primary checkout-btn" @click="checkout">
          <span class="btn-icon"><NavIcon name="orders" /></span>
          <span>提交订单</span>
        </button>
      </aside>
    </section>

    <section class="panel empty-panel" v-else>
      <div class="panel-head">
        <h3>购物车为空</h3>
        <p>返回商城继续挑选支持验真的水果商品。</p>
      </div>
      <button class="btn btn-primary" @click="router.push('/dashboard')">返回商城</button>
    </section>
  </AppShell>
</template>

<style scoped>
.cart-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(280px, 0.8fr);
  gap: 16px;
}

.cart-list-panel,
.cart-summary-panel,
.empty-panel {
  padding: 24px;
}

.cart-list {
  display: grid;
  gap: 12px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 0;
  border-bottom: 1px dashed var(--line);
}

.cart-item:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}

.cart-item-kicker,
.cart-item-meta,
.summary-row span {
  color: var(--muted);
}

.cart-item h4 {
  margin-top: 8px;
  font-family: var(--font-display);
  font-size: 1.8rem;
  line-height: 1;
}

.cart-item-side {
  display: grid;
  justify-items: end;
  gap: 10px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px dashed var(--line);
}

.summary-row.total {
  margin-top: 6px;
  font-size: 1.08rem;
}

.checkout-btn {
  width: 100%;
  margin-top: 18px;
}

@media (max-width: 900px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }
}
</style>
