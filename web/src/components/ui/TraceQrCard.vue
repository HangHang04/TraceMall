<script setup>
import QRCode from 'qrcode'
import { onMounted, ref, watch } from 'vue'
import NavIcon from '@/components/ui/NavIcon.vue'

const props = defineProps({
  value: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    default: '批次二维码',
  },
  caption: {
    type: String,
    default: '',
  },
  traceId: {
    type: String,
    default: '',
  },
})

const canvasRef = ref(null)
const copied = ref(false)

async function renderQr() {
  if (!canvasRef.value || !props.value) return
  await QRCode.toCanvas(canvasRef.value, props.value, {
    width: 220,
    margin: 1,
    color: {
      dark: '#17352f',
      light: '#fffdf9',
    },
  })
}

async function copyValue() {
  await navigator.clipboard.writeText(props.value)
  copied.value = true
  setTimeout(() => {
    copied.value = false
  }, 1600)
}

function downloadQr() {
  if (!canvasRef.value) return
  const link = document.createElement('a')
  link.download = `${props.traceId || 'trace'}-qr.png`
  link.href = canvasRef.value.toDataURL('image/png')
  link.click()
}

onMounted(renderQr)
watch(() => props.value, renderQr)
</script>

<template>
  <section class="panel qr-card">
    <div class="qr-card-head">
      <div>
        <h3 class="panel-title">
          <span class="title-icon"><NavIcon name="verify" /></span>
          <span>{{ title }}</span>
        </h3>
        <p class="panel-copy">{{ caption }}</p>
      </div>
      <span class="chip" v-if="traceId">{{ traceId }}</span>
    </div>
    <div class="qr-card-body">
      <canvas ref="canvasRef" class="qr-canvas" />
      <div class="qr-card-meta">
        <p>扫码后可直接进入公共验真页，查看批次结果和完整事件链。</p>
        <code>{{ value }}</code>
      </div>
    </div>
    <div class="qr-actions">
      <button class="btn btn-primary" @click="downloadQr">
        <span class="btn-icon"><NavIcon name="orders" /></span>
        <span>下载二维码</span>
      </button>
      <button class="btn btn-ghost" @click="copyValue">
        <span class="btn-icon"><NavIcon name="verify" /></span>
        <span>{{ copied ? '已复制' : '复制链接' }}</span>
      </button>
    </div>
  </section>
</template>
