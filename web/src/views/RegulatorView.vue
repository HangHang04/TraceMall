<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/layout/AppShell.vue'
import RiskBadge from '@/components/ui/RiskBadge.vue'
import { listAuditLogs, listRiskAlerts } from '@/api'

const alertPage = ref({ records: [] })
const auditPage = ref({ records: [] })

async function loadData() {
  alertPage.value = await listRiskAlerts(1, 20)
  auditPage.value = await listAuditLogs(1, 20)
}

onMounted(loadData)
</script>

<template>
  <AppShell title="监管视图" subtitle="风险告警与审计日志基础面板">
    <section class="panel">
      <h3>风险告警</h3>
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>TraceId</th>
              <th>风险类型</th>
              <th>等级</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in alertPage.records" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.traceId }}</td>
              <td>{{ item.riskType }}</td>
              <td><RiskBadge :level="item.riskLevel" /></td>
              <td>{{ item.status }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="panel">
      <h3>审计日志</h3>
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
  </AppShell>
</template>
