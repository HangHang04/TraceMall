<script setup>
import { reactive } from 'vue'

const emit = defineEmits(['apply'])

const form = reactive({
  keyword: '',
  category: '',
})

function apply() {
  emit('apply', { ...form })
}
</script>

<template>
  <section class="panel filter-bar">
    <div class="field-group">
      <label>关键字</label>
      <input v-model="form.keyword" placeholder="输入水果名称" />
    </div>
    <div class="field-group">
      <label>分类</label>
      <select v-model="form.category">
        <option value="">全部</option>
        <option value="苹果">苹果</option>
        <option value="柑橘">柑橘</option>
        <option value="葡萄">葡萄</option>
        <option value="浆果">浆果</option>
      </select>
    </div>
    <button class="btn btn-primary" @click="apply">应用筛选</button>
  </section>
</template>