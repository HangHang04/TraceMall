<script setup>
import { computed, onMounted, ref } from 'vue'
import AppShell from '@/components/layout/AppShell.vue'
import RiskBadge from '@/components/ui/RiskBadge.vue'
import { listAuditLogs, listRiskAlerts } from '@/api'

const alertPage = ref({ records: [] })
const auditPage = ref({ records: [] })

const highRiskCount = computed(
  () => alertPage.value.records.filter((item) => String(item.riskLevel || '').toUpperCase().includes('HIGH')).length,
)

async function loadData() {
  alertPage.value = await listRiskAlerts(1, 20)
  auditPage.value = await listAuditLogs(1, 20)
}

onMounted(loadData)
</script>

<template>
  <AppShell title="监管视图" subtitle="优先查看风险告警，再核对审计日志">
    <section class="regulator-hero panel">
      <div>
        <p class="regulator-kicker">Regulatory Console</p>
        <h2>当前共收到 {{ alertPage.records.length }} 条告警，其中高风险 {{ highRiskCount }} 条。</h2>
      </div>
      <div class="regulator-summary">
        <div><span>风险告警</span><strong>{{ alertPage.records.length }}</strong></div>
        <div><span>审计日志</span><strong>{{ auditPage.records.length }}</strong></div>
        <div><span>高风险</span><strong>{{ highRiskCount }}</strong></div>
      </div>
    </section>

    <section class="regulator-grid">
      <section class="panel regulator-panel">
        <div class="panel-head">
          <h3>风险告警</h3>
          <p>按溯源码、风险类型和等级追查异常扫描。</p>
        </div>
        <div class="alert-list">
          <article class="alert-item" v-for="item in alertPage.records" :key="item.id">
            <div>
              <p class="alert-trace">{{ item.traceId }}</p>
              <h4>{{ item.riskType }}</h4>
              <p class="alert-meta">状态 {{ item.status }}</p>
            </div>
            <RiskBadge :level="item.riskLevel" />
          </article>
        </div>
      </section>

      <section class="panel regulator-panel">
        <div class="panel-head">
          <h3>审计日志</h3>
          <p>追踪接口路径、用户动作和响应码。</p>
        </div>
        <div class="audit-list">
          <article class="audit-item" v-for="item in auditPage.records" :key="item.id">
            <div>
              <p class="audit-head">{{ item.username }} · {{ item.action }}</p>
              <p class="audit-meta">{{ item.module }} · {{ item.requestPath }}</p>
            </div>
            <div class="audit-side">
              <strong>{{ item.responseCode }}</strong>
              <span>{{ item.createdAt }}</span>
            </div>
          </article>
        </div>
      </section>
    </section>
  </AppShell>
</template>

<style scoped>
.regulator-hero,
.regulator-panel {
  padding: 24px;
}

.regulator-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(280px, 0.95fr);
  gap: 20px;
  background:
    radial-gradient(circle at 0% 0%, rgba(255, 225, 193, 0.4), transparent 22%),
    linear-gradient(135deg, rgba(253, 248, 241, 0.96), rgba(239, 244, 237, 0.88));
}

.regulator-kicker {
  color: var(--muted);
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.regulator-hero h2 {
  margin-top: 10px;
  font-family: var(--font-display);
  font-size: clamp(2rem, 4vw, 3.4rem);
  line-height: 1;
}

.regulator-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.regulator-summary div {
  padding: 18px;
  border-radius: 22px;
  background: rgba(255, 251, 246, 0.78);
  border: 1px solid rgba(113, 129, 102, 0.12);
}

.regulator-summary span,
.alert-meta,
.audit-meta,
.audit-side span {
  color: var(--muted);
}

.regulator-summary strong {
  display: block;
  margin-top: 6px;
  font-size: 1.2rem;
}

.regulator-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.alert-list,
.audit-list {
  display: grid;
  gap: 12px;
}

.alert-item,
.audit-item {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 18px 0;
  border-bottom: 1px dashed var(--line);
}

.alert-item:last-child,
.audit-item:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}

.alert-trace,
.audit-head {
  font-weight: 700;
}

.alert-item h4 {
  margin-top: 8px;
  font-family: var(--font-display);
  font-size: 1.7rem;
  line-height: 1;
}

.audit-side {
  display: grid;
  justify-items: end;
}

@media (max-width: 1100px) {
  .regulator-hero,
  .regulator-grid,
  .regulator-summary {
    grid-template-columns: 1fr;
  }

  .audit-side {
    justify-items: start;
  }
}
</style>
