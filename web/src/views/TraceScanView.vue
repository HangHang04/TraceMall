<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavIcon from '@/components/ui/NavIcon.vue'
import TraceScannerPanel from '@/components/ui/TraceScannerPanel.vue'
import TraceTimeline from '@/components/ui/TraceTimeline.vue'
import VerifyResultPanel from '@/components/ui/VerifyResultPanel.vue'
import { traceDetail, verifyTrace } from '@/api'
import { buildDeviceFingerprint } from '@/utils/traceQr'

const route = useRoute()
const router = useRouter()

const verifyInput = ref({
  traceId: '',
  signature: '',
  geo: '四川-成都',
  ip: '10.0.2.8',
  deviceFingerprint: buildDeviceFingerprint(),
})
const verifyResult = ref(null)
const trace = ref(null)
const error = ref('')
const loading = ref(false)

async function loadTrace(traceId) {
  if (!traceId) return
  try {
    trace.value = await traceDetail(traceId)
  } catch {}
}

async function submitVerify() {
  if (!verifyInput.value.traceId) return
  loading.value = true
  error.value = ''
  verifyInput.value.deviceFingerprint = buildDeviceFingerprint()
  try {
    verifyResult.value = await verifyTrace(verifyInput.value)
    await loadTrace(verifyInput.value.traceId)
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

async function handleScanDecoded(parsed) {
  verifyInput.value.traceId = parsed.traceId || ''
  verifyInput.value.signature = parsed.signature || ''
  await submitVerify()
}

async function syncFromRoute() {
  verifyInput.value.traceId = String(route.query.traceId || '')
  verifyInput.value.signature = String(route.query.signature || '')
  if (verifyInput.value.traceId && verifyInput.value.signature) {
    await submitVerify()
    return
  }
  if (verifyInput.value.traceId) {
    await loadTrace(verifyInput.value.traceId)
  }
}

watch(
  () => route.fullPath,
  async () => {
    await syncFromRoute()
  },
)

onMounted(syncFromRoute)
</script>

<template>
  <div class="scan-page">
    <header class="scan-page-head">
      <div class="scan-shell">
        <span class="hero-tag">公众验真入口</span>
        <h1>扫码后，先看结论，再看链路。</h1>
        <p>
          这个页面直接承接包装二维码。用户无需登录，就可以完成验真、查看风险说明，并核对批次事件链。
        </p>
        <div class="hero-actions">
          <button class="btn btn-primary" @click="submitVerify" :disabled="loading || !verifyInput.traceId">
            <span class="btn-icon"><NavIcon name="verify" /></span>
            <span>{{ loading ? '校验中' : '立即校验' }}</span>
          </button>
          <button class="btn btn-ghost" @click="router.push('/login')">
            <span class="btn-icon"><NavIcon name="home" /></span>
            <span>进入系统</span>
          </button>
        </div>
      </div>
    </header>

    <main class="scan-page-main">
      <div class="scan-layout">
        <section class="panel">
          <div class="scan-form-head">
            <div>
              <h3 class="panel-title">
                <span class="title-icon"><NavIcon name="scan" /></span>
                <span>扫码或手动输入</span>
              </h3>
              <p class="panel-copy">如果二维码中已经携带溯源码和签名摘要，扫码后会自动完成回填和校验。</p>
            </div>
            <span class="chip">无需登录</span>
          </div>

          <div class="workspace-panels">
            <label class="field-group">
              <span>溯源码</span>
              <input v-model="verifyInput.traceId" placeholder="TRACE-XXXX" />
            </label>
            <label class="field-group">
              <span>签名</span>
              <input v-model="verifyInput.signature" placeholder="请输入签名摘要" />
            </label>
            <label class="field-group">
              <span>扫码位置</span>
              <input v-model="verifyInput.geo" placeholder="例如：四川-成都" />
            </label>
            <button class="btn btn-primary" @click="submitVerify" :disabled="loading">
              <span class="btn-icon"><NavIcon name="verify" /></span>
              <span>{{ loading ? '校验中' : '立即验真' }}</span>
            </button>
          </div>

          <p class="error" v-if="error">{{ error }}</p>
        </section>

        <VerifyResultPanel :result="verifyResult" />
      </div>

      <TraceScannerPanel @decoded="handleScanDecoded" />
      <TraceTimeline v-if="trace?.events?.length" :events="trace.events" />
      <div class="empty-state" v-else-if="verifyInput.traceId && !trace?.events?.length">
        当前批次暂无可展示的事件链。
      </div>
    </main>
  </div>
</template>
