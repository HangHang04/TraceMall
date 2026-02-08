<script setup>
import { onMounted, reactive, ref } from 'vue'
import AppShell from '@/components/layout/AppShell.vue'
import { createBatch, createBatchEvent, createMerchantFruit, listMerchantFruits } from '@/api'

const fruits = ref([])
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
  fruitId: 1,
  batchNo: 'BATCH-WEB-001',
  traceId: '',
  harvestDate: '2026-02-08',
  expireDate: '2026-03-08',
  quantity: 100,
})

const eventForm = reactive({
  batchId: 1,
  eventType: 'QUALITY_CHECK',
  eventTime: '2026-02-08T12:00:00',
  location: '成都质检中心',
  payloadJson: '{"result":"PASS"}',
})

async function loadFruits() {
  fruits.value = await listMerchantFruits()
}

async function addFruit() {
  await createMerchantFruit(fruitForm)
  message.value = '水果新增成功'
  await loadFruits()
}

async function addBatch() {
  const data = await createBatch(batchForm)
  message.value = `批次创建成功：${data.traceId}`
}

async function addEvent() {
  const data = await createBatchEvent(eventForm)
  message.value = `事件创建成功：${data.anchorId}`
}

onMounted(loadFruits)
</script>

<template>
  <AppShell title="商家批次管理" subtitle="商品上架、批次创建、事件上链(Mock)">
    <section class="panel form-grid-3">
      <div>
        <h3>新增水果</h3>
        <input v-model="fruitForm.fruitName" placeholder="fruitName" />
        <input v-model="fruitForm.category" placeholder="category" />
        <input v-model="fruitForm.origin" placeholder="origin" />
        <input v-model="fruitForm.unit" placeholder="unit" />
        <input v-model.number="fruitForm.unitPrice" type="number" placeholder="unitPrice" />
        <textarea v-model="fruitForm.description" rows="3" />
        <button class="btn btn-primary" @click="addFruit">提交</button>
      </div>

      <div>
        <h3>创建批次</h3>
        <input v-model.number="batchForm.fruitId" type="number" placeholder="fruitId" />
        <input v-model="batchForm.batchNo" placeholder="batchNo" />
        <input v-model="batchForm.traceId" placeholder="traceId(optional)" />
        <input v-model="batchForm.harvestDate" type="date" />
        <input v-model="batchForm.expireDate" type="date" />
        <input v-model.number="batchForm.quantity" type="number" placeholder="quantity" />
        <button class="btn btn-primary" @click="addBatch">提交</button>
      </div>

      <div>
        <h3>新增溯源事件</h3>
        <input v-model.number="eventForm.batchId" type="number" placeholder="batchId" />
        <input v-model="eventForm.eventType" placeholder="eventType" />
        <input v-model="eventForm.eventTime" type="datetime-local" />
        <input v-model="eventForm.location" placeholder="location" />
        <textarea v-model="eventForm.payloadJson" rows="3" />
        <button class="btn btn-primary" @click="addEvent">提交</button>
      </div>
    </section>

    <section class="panel">
      <h3>商家水果列表</h3>
      <p class="success" v-if="message">{{ message }}</p>
      <div class="table-wrap">
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
              <td>{{ item.status }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>