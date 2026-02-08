<script setup>
import * as echarts from 'echarts/core'
import { BarChart, LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { onMounted, onBeforeUnmount, ref, watch } from 'vue'

echarts.use([BarChart, LineChart, GridComponent, TooltipComponent, CanvasRenderer])

const props = defineProps({
  title: String,
  subtitle: String,
  option: Object,
})

const chartRef = ref(null)
let chart = null

function render() {
  if (!chartRef.value || !props.option) return
  if (!chart) {
    chart = echarts.init(chartRef.value)
  }
  chart.setOption(props.option)
}

onMounted(() => {
  render()
  window.addEventListener('resize', render)
})

watch(() => props.option, () => render(), { deep: true })

onBeforeUnmount(() => {
  window.removeEventListener('resize', render)
  if (chart) chart.dispose()
})
</script>

<template>
  <section class="panel chart-panel">
    <div class="panel-head">
      <h3>{{ title }}</h3>
      <p>{{ subtitle }}</p>
    </div>
    <div ref="chartRef" class="chart-canvas" />
  </section>
</template>
