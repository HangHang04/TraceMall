<script setup>
import { reactive } from 'vue'
import NavIcon from '@/components/ui/NavIcon.vue'

const emit = defineEmits(['apply'])

const form = reactive({
  keyword: '',
  category: '',
})

function apply() {
  emit('apply', { ...form })
}

function reset() {
  form.keyword = ''
  form.category = ''
  apply()
}
</script>

<template>
  <section class="panel filter-bar">
    <div class="field-group">
      <label>关键词</label>
      <input v-model="form.keyword" placeholder="输入水果名称" @keyup.enter="apply" />
    </div>
    <div class="field-group">
      <label>分类</label>
      <select v-model="form.category">
        <option value="">全部</option>
        <option value="苹果">苹果</option>
        <option value="柑橘">柑橘</option>
        <option value="葡萄">葡萄</option>
        <option value="浆果">浆果</option>
        <option value="梨">梨</option>
        <option value="热带水果">热带水果</option>
      </select>
    </div>
    <div class="filter-actions">
      <button class="btn btn-primary" @click="apply">
        <span class="btn-icon"><NavIcon name="filter" /></span>
        <span>应用筛选</span>
      </button>
      <button class="btn btn-ghost" @click="reset">
        <span class="btn-icon"><NavIcon name="refresh" /></span>
        <span>重置</span>
      </button>
    </div>
  </section>
</template>
