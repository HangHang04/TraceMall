<script setup>
import { onErrorCaptured, ref } from 'vue'

const runtimeError = ref(null)

function reloadPage() {
  window.location.reload()
}

onErrorCaptured((error) => {
  console.error(error)
  runtimeError.value = error
  return false
})
</script>

<template>
  <main v-if="runtimeError" class="app-error-shell">
    <section class="app-error-card">
      <span class="app-error-tag">页面恢复</span>
      <h1>页面加载异常</h1>
      <p>系统捕获到了前端运行时错误，已阻止白屏扩散。可以先刷新页面，如果问题持续存在再继续排查。</p>
      <pre class="app-error-detail">{{ runtimeError?.message || String(runtimeError) }}</pre>
      <button class="app-error-button" type="button" @click="reloadPage">刷新页面</button>
    </section>
  </main>

  <RouterView v-else v-slot="{ Component, route }">
    <Transition name="page-slide" mode="out-in">
      <component :is="Component" :key="route.fullPath" />
    </Transition>
  </RouterView>
</template>

<style scoped>
.app-error-shell {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}

.app-error-card {
  width: min(560px, 100%);
  padding: 36px;
  border-radius: 32px;
  border: 1px solid rgba(88, 106, 95, 0.12);
  background: rgba(255, 255, 252, 0.94);
  box-shadow: 0 24px 50px rgba(33, 44, 40, 0.08);
}

.app-error-tag {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(107, 136, 85, 0.12);
  color: #5b7447;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.app-error-card h1 {
  margin-top: 16px;
  color: #223746;
  font-size: clamp(30px, 4vw, 42px);
  line-height: 1;
  letter-spacing: -0.05em;
}

.app-error-card p {
  margin-top: 14px;
  color: #61707a;
  line-height: 1.8;
}

.app-error-button {
  min-height: 52px;
  margin-top: 20px;
  padding: 0 20px;
  border: 0;
  border-radius: 18px;
  color: #fffef8;
  background: linear-gradient(180deg, #6f8e53, #597841);
  font: inherit;
  font-weight: 700;
  cursor: pointer;
}

.app-error-detail {
  margin-top: 14px;
  padding: 14px 16px;
  overflow: auto;
  border-radius: 18px;
  background: rgba(35, 55, 68, 0.06);
  color: #435561;
  font: 13px/1.6 'Consolas', 'SFMono-Regular', monospace;
  white-space: pre-wrap;
}
</style>
