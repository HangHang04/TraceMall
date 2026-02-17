<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import AppShell from '@/components/layout/AppShell.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import TraceTimeline from '@/components/ui/TraceTimeline.vue'
import { getFruit, traceDetail } from '@/api'
import { toBatchStatusLabel } from '@/utils/status'

const route = useRoute()
const fruit = ref(null)
const traceId = ref('TRACE-APPLE-001')
const trace = ref(null)
const error = ref('')

async function loadFruit() {
  fruit.value = await getFruit(route.params.id)
}

async function loadTrace() {
  error.value = ''
  try {
    trace.value = await traceDetail(traceId.value)
  } catch (err) {
    error.value = err.message
  }
}

onMounted(async () => {
  await loadFruit()
  await loadTrace()
})
</script>

<template>
  <AppShell title="商品详情" subtitle="查看商品信息与完整溯源链路">
    <section class="panel" v-if="fruit">
      <h3 class="panel-title">
        <span class="title-icon"><NavIcon name="package" /></span>
        <span>{{ fruit.fruitName }}</span>
      </h3>
      <p>{{ fruit.category }} · {{ fruit.origin }}</p>
      <p class="price">¥{{ fruit.unitPrice }} / {{ fruit.unit }}</p>
      <p>{{ fruit.description }}</p>
    </section>

    <section class="panel">
      <h3 class="panel-title">
        <span class="title-icon"><NavIcon name="verify" /></span>
        <span>溯源查询</span>
      </h3>
      <div class="verify-form">
        <input v-model="traceId" placeholder="TRACE-XXXX" />
        <button class="btn btn-primary" @click="loadTrace">
          <span class="btn-icon"><NavIcon name="verify" /></span>
          <span>查询</span>
        </button>
      </div>
      <p class="error" v-if="error">{{ error }}</p>
      <div v-if="trace?.summary" class="trace-summary">
        <p><strong>批次：</strong>{{ trace.summary.batchNo }}</p>
        <p><strong>店铺：</strong>{{ trace.summary.shopName }}</p>
        <p><strong>状态：</strong>{{ toBatchStatusLabel(trace.summary.batchStatus) }}</p>
      </div>
    </section>

    <TraceTimeline :events="trace?.events || []" />
  </AppShell>
</template>
