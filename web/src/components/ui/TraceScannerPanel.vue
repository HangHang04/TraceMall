<script setup>
import { computed, onBeforeUnmount, ref } from 'vue'
import { Html5Qrcode, Html5QrcodeSupportedFormats } from 'html5-qrcode'
import NavIcon from '@/components/ui/NavIcon.vue'
import { parseTraceScanContent } from '@/utils/traceQr'

const emit = defineEmits(['decoded'])

const fileInput = ref(null)
const scannerId = `trace-scanner-${Math.random().toString(36).slice(2, 9)}`
const scanning = ref(false)
const cameraSupported = ref(typeof navigator !== 'undefined' && !!navigator.mediaDevices)
const imageLoading = ref(false)
const lastText = ref('')
const scannerError = ref('')
const activeMode = ref('camera')

let html5Qr = null

const scannerHint = computed(() => {
  if (!cameraSupported.value) {
    return '当前浏览器无法直接调用摄像头，可改用上传二维码图片识别。'
  }
  if (scanning.value) {
    return '摄像头已开启，请将二维码放置在取景框中央。'
  }
  return '支持摄像头扫码，也支持上传二维码截图。'
})

async function ensureScanner() {
  if (!html5Qr) {
    html5Qr = new Html5Qrcode(scannerId, {
      formatsToSupport: [Html5QrcodeSupportedFormats.QR_CODE],
      verbose: false,
    })
  }
  return html5Qr
}

function emitDecoded(text) {
  lastText.value = text
  scannerError.value = ''
  emit('decoded', parseTraceScanContent(text))
}

async function startCameraScan() {
  if (!cameraSupported.value || scanning.value) return
  scannerError.value = ''
  activeMode.value = 'camera'

  try {
    const instance = await ensureScanner()
    const cameras = await Html5Qrcode.getCameras()
    const cameraId = cameras[0]?.id
    if (!cameraId) {
      throw new Error('未检测到可用摄像头')
    }
    await instance.start(
      cameraId,
      { fps: 10, qrbox: { width: 220, height: 220 } },
      (decodedText) => emitDecoded(decodedText),
      () => {},
    )
    scanning.value = true
  } catch (error) {
    scannerError.value = error?.message || '摄像头启动失败'
  }
}

async function stopCameraScan() {
  if (!html5Qr || !scanning.value) return
  try {
    await html5Qr.stop()
    await html5Qr.clear()
  } catch {
    // Ignore teardown failures from partially initialized scanners.
  }
  scanning.value = false
}

async function handleFileChange(event) {
  const file = event.target.files?.[0]
  if (!file) return
  activeMode.value = 'image'
  imageLoading.value = true
  scannerError.value = ''

  try {
    const instance = await ensureScanner()
    const text = await instance.scanFile(file, true)
    emitDecoded(text)
  } catch (error) {
    scannerError.value = error?.message || '图片识别失败'
  } finally {
    imageLoading.value = false
    event.target.value = ''
  }
}

function openFilePicker() {
  fileInput.value?.click()
}

onBeforeUnmount(() => {
  stopCameraScan()
})
</script>

<template>
  <section class="panel scanner-panel">
    <div class="scanner-head">
      <div>
        <h3 class="panel-title">
          <span class="title-icon"><NavIcon name="scan" /></span>
          <span>扫码入口</span>
        </h3>
        <p class="panel-copy">{{ scannerHint }}</p>
      </div>
      <div class="scanner-actions">
        <button class="btn btn-primary" @click="startCameraScan" :disabled="scanning || !cameraSupported">
          <span class="btn-icon"><NavIcon name="camera" /></span>
          <span>{{ scanning ? '扫码中' : '启动扫码' }}</span>
        </button>
        <button class="btn btn-ghost" @click="stopCameraScan" :disabled="!scanning">
          <span class="btn-icon"><NavIcon name="refresh" /></span>
          <span>停止</span>
        </button>
        <button class="btn btn-ghost" @click="openFilePicker" :disabled="imageLoading">
          <span class="btn-icon"><NavIcon name="package" /></span>
          <span>{{ imageLoading ? '识别中' : '上传图片' }}</span>
        </button>
      </div>
    </div>

    <div class="scanner-stage">
      <div :id="scannerId" class="scanner-camera" :class="{ active: scanning }" />
      <div class="scanner-sidecard">
        <div class="scanner-sidecard-title">识别说明</div>
        <p>推荐内容是带有 <code>溯源码</code> 和 <code>签名摘要</code> 的验真链接，扫码后会直接触发校验。</p>
        <p>兼容纯 <code>TRACE-XXXX</code> 文本、JSON 溯源包，以及系统生成的扫码链接。</p>
        <div class="scanner-mode">
          <span class="chip">{{ activeMode === 'camera' ? '摄像头模式' : '图片识别模式' }}</span>
        </div>
      </div>
    </div>

    <input ref="fileInput" type="file" accept="image/*" class="hidden-input" @change="handleFileChange" />
    <p class="error" v-if="scannerError">{{ scannerError }}</p>
    <div class="scanner-last" v-if="lastText">
      <span class="scanner-last-label">最近一次扫码内容</span>
      <code>{{ lastText }}</code>
    </div>
  </section>
</template>
