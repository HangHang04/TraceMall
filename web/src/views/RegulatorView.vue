<script setup>
import { computed, onMounted, ref } from 'vue'
import AppShell from '@/components/layout/AppShell.vue'
import ChartPanel from '@/components/ui/ChartPanel.vue'
import MetricCard from '@/components/ui/MetricCard.vue'
import NavIcon from '@/components/ui/NavIcon.vue'
import RiskBadge from '@/components/ui/RiskBadge.vue'
import { listAuditLogs, listRiskAlerts } from '@/api'

const alertPage = ref({ records: [] })
const auditPage = ref({ records: [] })

const openAlerts = computed(() => alertPage.value.records.filter((item) => item.status === 'OPEN').length)
const highRiskCount = computed(() => alertPage.value.records.filter((item) => item.riskLevel === 'HIGH').length)

const alertOption = computed(() => {
  const riskLevels = ['HIGH', 'MODERATE', 'LOW']
  const data = riskLevels.map(
    (level) => alertPage.value.records.filter((item) => String(item.riskLevel).toUpperCase() === level).length,
  )

  return {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: riskLevels,
      axisLine: { lineStyle: { color: '#8da2c0' } },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: 'rgba(102, 126, 170, 0.18)' } },
    },
    grid: { left: 36, right: 12, top: 20, bottom: 24 },
    series: [
      {
        type: 'bar',
        data,
        itemStyle: {
          color: '#b34e45',
          borderRadius: [10, 10, 0, 0],
        },
      },
    ],
  }
})

const auditOption = computed(() => {
  const moduleCount = {}
  auditPage.value.records.forEach((item) => {
    moduleCount[item.module || 'UNKNOWN'] = (moduleCount[item.module || 'UNKNOWN'] || 0) + 1
  })

  return {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: Object.keys(moduleCount),
      axisLine: { lineStyle: { color: '#8da2c0' } },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: 'rgba(102, 126, 170, 0.18)' } },
    },
    grid: { left: 36, right: 12, top: 20, bottom: 24 },
    series: [
      {
        type: 'line',
        smooth: true,
        data: Object.values(moduleCount),
        itemStyle: { color: '#2f6a58' },
        areaStyle: { color: 'rgba(47, 106, 88, 0.12)' },
      },
    ],
  }
})

async function loadData() {
  alertPage.value = await listRiskAlerts(1, 20)
  auditPage.value = await listAuditLogs(1, 20)
}

onMounted(loadData)
</script>

<template>
  <AppShell title="监管控制台" subtitle="先看高风险告警，再追踪审计线索和异常分布。">
    <section class="workspace-hero">
      <div class="workspace-summary">
        <span class="page-eyebrow">监管控制台</span>
        <h2>把扫码异常转成可追踪、可排查的处置线索。</h2>
        <p>监管端聚焦风险批次、告警状态和审计留痕，首屏只展示最重要的异常情况。</p>
      </div>
    </section>

    <section class="metrics-grid">
      <MetricCard icon="regulator" label="告警总量" :value="alertPage.records.length" trend="当前拉取到的风险告警数量" status="监测" />
      <MetricCard icon="scan" label="高风险告警" :value="highRiskCount" trend="需要优先处置的批次数量" status="高优先级" />
      <MetricCard icon="verify" label="待处理告警" :value="openAlerts" trend="状态仍为 OPEN 的风险记录" status="处理中" />
      <MetricCard icon="orders" label="审计日志" :value="auditPage.records.length" trend="最近接口访问与操作留痕" status="审计" />
    </section>

    <div class="workspace-layout table-panel">
      <section class="panel">
        <div class="workspace-head">
          <div>
            <h3 class="panel-title">
              <span class="title-icon"><NavIcon name="scan" /></span>
              <span>高风险告警列表</span>
            </h3>
            <p class="panel-copy">优先核查高风险和待处理记录，缩短从异常扫码到监管处置的响应时间。</p>
          </div>
          <span class="chip">优先看 OPEN + HIGH</span>
        </div>
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>溯源码</th>
                <th>风险类型</th>
                <th>等级</th>
                <th>状态</th>
                <th>检测时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in alertPage.records" :key="item.id">
                <td>{{ item.id }}</td>
                <td><strong>{{ item.traceId }}</strong></td>
                <td>{{ item.riskType }}</td>
                <td><RiskBadge :level="item.riskLevel" /></td>
                <td>{{ item.status }}</td>
                <td>{{ item.detectedAt || '-' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <section class="panel">
        <h3 class="panel-title">
          <span class="title-icon"><NavIcon name="spark" /></span>
          <span>处置提示</span>
        </h3>
        <div class="workspace-stat-list">
          <div class="workspace-note">
            <span class="workspace-label">当前重点</span>
            <strong>优先核查高风险且仍未关闭的告警记录。</strong>
          </div>
          <div class="workspace-note">
            <span class="workspace-label">联动视角</span>
            <strong>结合审计日志确认异常请求来源，再回到批次链路核对签名和事件。</strong>
          </div>
          <div class="workspace-note">
            <span class="workspace-label">结果判断</span>
            <strong>若短时间内出现异地高频扫码，应重点关注二维码复用或伪造风险。</strong>
          </div>
        </div>
      </section>
    </div>

    <section class="panel table-panel">
      <h3 class="panel-title">
        <span class="title-icon"><NavIcon name="orders" /></span>
        <span>审计日志</span>
      </h3>
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>时间</th>
              <th>用户</th>
              <th>动作</th>
              <th>模块</th>
              <th>路径</th>
              <th>状态码</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in auditPage.records" :key="item.id">
              <td>{{ item.createdAt }}</td>
              <td>{{ item.username }}</td>
              <td>{{ item.action }}</td>
              <td>{{ item.module }}</td>
              <td>{{ item.requestPath }}</td>
              <td>{{ item.responseCode }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="chart-grid table-panel">
      <ChartPanel title="风险等级分布" subtitle="按 HIGH / MODERATE / LOW 观察异常结构" :option="alertOption" />
      <ChartPanel title="审计模块热度" subtitle="快速判断近期访问集中在哪些模块" :option="auditOption" />
    </section>
  </AppShell>
</template>
