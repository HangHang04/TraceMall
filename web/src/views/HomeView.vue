<script setup>
const featuredLots = [
  { id: 'A01', name: '蒲江春见', detail: '48 小时内完成采后分拣、冷链入仓与批次建档。', tone: '柑橘类' },
  { id: 'B17', name: '烟台富士', detail: '果园温湿度、采收班组与质检结果同步留痕。', tone: '苹果类' },
  { id: 'C09', name: '攀枝花芒果', detail: '运输节点异常自动告警，验真记录可回溯。', tone: '热带类' },
]

const traceSteps = [
  { index: '01', title: '果园建档', text: '把产地、种植批次与农事记录绑定到每一批水果。' },
  { index: '02', title: '质检留证', text: '采收、分拣、检测、包装形成结构化事件链。' },
  { index: '03', title: '消费者验真', text: '扫码即可查看来源、风控提示与链上锚点状态。' },
]

const operatingSignals = [
  { label: '批次建档完整率', value: '98.6%' },
  { label: '冷链节点回传', value: '24 / 天' },
  { label: '异常扫描拦截', value: '13 次' },
]
</script>

<template>
  <main class="landing-page">
    <section class="hero-section">
      <header class="hero-nav">
        <div class="hero-brand">
          <span class="brand-mark">TM</span>
          <div>
            <p class="brand-name">TraceMall</p>
            <p class="brand-note">可追溯水果商城</p>
          </div>
        </div>
        <nav class="hero-actions">
          <RouterLink class="nav-link" to="/dashboard">进入商城</RouterLink>
          <RouterLink class="nav-link nav-link-strong" to="/login">登录系统</RouterLink>
        </nav>
      </header>

      <div class="hero-body">
        <div class="hero-copy">
          <p class="eyebrow">从果园到签收，每一次流转都有记录。</p>
          <h1>把交易、溯源与验真，放在同一张清晰的界面里。</h1>
          <p class="hero-text">
            面向消费者、商户与监管方的水果商城前端。既能完成下单，也能在扫码时看见来源、批次、异常扫描与链上锚点。
          </p>
          <div class="hero-cta">
            <RouterLink class="hero-button primary" to="/dashboard">查看商城工作台</RouterLink>
            <a class="hero-button secondary" href="#trace-story">浏览溯源流程</a>
          </div>
        </div>

        <div class="hero-visual" aria-hidden="true">
          <div class="visual-halo"></div>
          <div class="fruit-stage">
            <div class="stage-copy">
              <p>今日重点批次</p>
              <strong>TRACE-APPLE-001</strong>
              <span>温控正常 · 验真通过 · 来源清晰</span>
            </div>
            <div class="stage-orb orb-a"></div>
            <div class="stage-orb orb-b"></div>
            <div class="stage-orb orb-c"></div>
          </div>
          <div class="signal-strip">
            <div v-for="signal in operatingSignals" :key="signal.label" class="signal-item">
              <span>{{ signal.label }}</span>
              <strong>{{ signal.value }}</strong>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="feature-ribbon">
      <p>商城交易</p>
      <p>批次追溯</p>
      <p>二维码验真</p>
      <p>异常告警</p>
      <p>监管核查</p>
    </section>

    <section id="trace-story" class="story-section">
      <div class="section-heading">
        <p class="eyebrow">一张页面，三条链路</p>
        <h2>首页不是堆卡片，而是先把“可信购买”这件事讲明白。</h2>
      </div>
      <div class="story-grid">
        <article class="story-panel intro-panel">
          <p class="panel-kicker">购买之前</p>
          <h3>先看到商品是否值得信任</h3>
          <p>产地、批次、规格与价格放在同一层级，让用户先完成判断，再决定是否继续下单。</p>
        </article>
        <article class="story-panel warm-panel">
          <p class="panel-kicker">扫码之后</p>
          <h3>把验真结果变成可读信息</h3>
          <p>不只显示二维码是否有效，也提示风险等级、扫描环境与关键节点。</p>
        </article>
        <article class="story-panel quiet-panel">
          <p class="panel-kicker">监管视角</p>
          <h3>异常事件能够被追问</h3>
          <p>频次异常、地理异常与上链不一致，会直接转成告警与审计线索。</p>
        </article>
      </div>
    </section>

    <section class="lots-section">
      <div class="section-heading compact">
        <p class="eyebrow">示例批次</p>
        <h2>内容对口，围绕真实业务写界面。</h2>
      </div>
      <div class="lots-grid">
        <article v-for="lot in featuredLots" :key="lot.id" class="lot-item">
          <p class="lot-tone">{{ lot.tone }}</p>
          <h3>{{ lot.name }}</h3>
          <p>{{ lot.detail }}</p>
          <span class="lot-code">{{ lot.id }}</span>
        </article>
      </div>
    </section>

    <section class="process-section">
      <div class="process-copy">
        <p class="eyebrow">流程可视化</p>
        <h2>页面节奏跟着水果流转走，而不是跟着组件库走。</h2>
      </div>
      <div class="process-list">
        <article v-for="step in traceSteps" :key="step.index" class="process-item">
          <span class="process-index">{{ step.index }}</span>
          <div>
            <h3>{{ step.title }}</h3>
            <p>{{ step.text }}</p>
          </div>
        </article>
      </div>
    </section>

    <section class="final-section">
      <div>
        <p class="eyebrow">准备进入系统</p>
        <h2>先看温和的品牌首页，再进入商城、商户端或监管端。</h2>
      </div>
      <div class="final-actions">
        <RouterLink class="hero-button primary" to="/dashboard">打开商城</RouterLink>
        <RouterLink class="hero-button secondary" to="/login">登录后台</RouterLink>
      </div>
    </section>
  </main>
</template>

<style scoped>
.landing-page { min-height: 100vh; color: var(--text); }
.hero-section { position: relative; overflow: clip; min-height: 100vh; padding: 28px clamp(20px, 4vw, 52px) 40px; background: radial-gradient(circle at top left, rgba(255, 214, 175, 0.65), transparent 34%), radial-gradient(circle at 88% 16%, rgba(150, 190, 134, 0.22), transparent 26%), linear-gradient(135deg, #f8f1e7 0%, #f2ece1 34%, #e7efe5 100%); }
.hero-section::before { content: ''; position: absolute; inset: auto auto 0 -8%; width: 58vw; height: 58vw; border-radius: 50%; background: radial-gradient(circle, rgba(233, 158, 113, 0.18), transparent 62%); filter: blur(12px); pointer-events: none; }
.hero-nav, .hero-body, .feature-ribbon, .story-section, .lots-section, .process-section, .final-section { width: min(1200px, 100%); margin: 0 auto; }
.hero-nav { position: relative; z-index: 2; display: flex; align-items: center; justify-content: space-between; gap: 18px; }
.hero-brand { display: flex; align-items: center; gap: 14px; }
.brand-mark { width: 52px; height: 52px; border-radius: 18px; display: grid; place-items: center; background: rgba(91, 118, 82, 0.14); border: 1px solid rgba(91, 118, 82, 0.16); color: #3f513a; font-family: var(--font-display); font-size: 1.2rem; letter-spacing: 0.08em; }
.brand-name, .brand-note, .eyebrow, .hero-text, .signal-item span, .panel-kicker, .lot-tone, .lot-code { margin: 0; }
.brand-name { font-family: var(--font-display); font-size: 1.6rem; line-height: 1; }
.brand-note { margin-top: 4px; color: var(--muted); font-size: 0.92rem; }
.hero-actions { display: flex; gap: 12px; }
.nav-link, .hero-button { display: inline-flex; align-items: center; justify-content: center; min-height: 46px; padding: 0 18px; border-radius: 999px; text-decoration: none; transition: transform 240ms ease, background-color 240ms ease, color 240ms ease, box-shadow 240ms ease; }
.nav-link { color: #4c5d47; background: rgba(255, 250, 243, 0.7); border: 1px solid rgba(112, 132, 102, 0.18); }
.nav-link-strong, .hero-button.primary { color: #fffdf8; background: linear-gradient(135deg, #6a7f54, #526a45); box-shadow: 0 18px 34px rgba(85, 106, 72, 0.22); }
.hero-button.secondary { color: #42513b; background: rgba(255, 248, 240, 0.82); border: 1px solid rgba(112, 132, 102, 0.18); }
.nav-link:hover, .hero-button:hover { transform: translateY(-2px); }
.hero-body { position: relative; z-index: 1; display: grid; grid-template-columns: minmax(0, 1.05fr) minmax(320px, 0.95fr); align-items: center; gap: clamp(24px, 5vw, 80px); min-height: calc(100vh - 112px); }
.hero-copy { max-width: 640px; padding: 54px 0 32px; animation: fade-up 700ms cubic-bezier(0.2, 0.9, 0.2, 1) both; }
.eyebrow { font-size: 0.86rem; letter-spacing: 0.24em; text-transform: uppercase; color: #6f7f62; }
.hero-copy h1, .section-heading h2, .process-copy h2, .final-section h2 { font-family: var(--font-display); font-weight: 600; letter-spacing: -0.03em; }
.hero-copy h1 { margin-top: 18px; font-size: clamp(2.5rem, 5.1vw, 4.4rem); line-height: 0.98; max-width: 11ch; }
.hero-text { margin-top: 22px; max-width: 560px; color: #556452; font-size: 1.05rem; line-height: 1.8; }
.hero-cta { display: flex; flex-wrap: wrap; gap: 14px; margin-top: 28px; }
.hero-visual { position: relative; height: min(680px, 72vh); min-height: 480px; display: flex; align-items: center; justify-content: center; animation: fade-up 820ms cubic-bezier(0.2, 0.9, 0.2, 1) both 80ms; }
.visual-halo { position: absolute; inset: 12% 10% auto auto; width: 72%; aspect-ratio: 1; border-radius: 50%; background: radial-gradient(circle, rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0)); }
.fruit-stage { position: relative; width: min(480px, 100%); aspect-ratio: 0.9; border-radius: 40px; overflow: hidden; background: linear-gradient(180deg, rgba(255, 252, 247, 0.94), rgba(243, 234, 221, 0.76)), radial-gradient(circle at top, rgba(255, 255, 255, 0.8), transparent 46%); border: 1px solid rgba(122, 132, 107, 0.18); box-shadow: 0 28px 80px rgba(111, 101, 82, 0.16); }
.fruit-stage::before { content: ''; position: absolute; inset: auto 8% 5% 8%; height: 26%; border-radius: 50%; background: radial-gradient(circle, rgba(119, 94, 67, 0.22), transparent 70%); }
.stage-copy { position: absolute; left: 28px; top: 28px; display: grid; gap: 8px; z-index: 2; }
.stage-copy p, .stage-copy span { color: #6b7467; font-size: 0.92rem; }
.stage-copy strong { font-family: var(--font-display); font-size: clamp(2rem, 4vw, 3rem); font-weight: 600; color: #324531; }
.stage-orb { position: absolute; border-radius: 50%; filter: saturate(1.02); animation: float 5.6s ease-in-out infinite; }
.orb-a { width: 240px; height: 240px; left: 16%; bottom: 18%; background: radial-gradient(circle at 30% 30%, #ffd292 0, #ffba5c 28%, #de7e35 70%, #8b4b28 100%); }
.orb-b { width: 178px; height: 178px; right: 15%; bottom: 24%; background: radial-gradient(circle at 30% 30%, #ffe9a0 0, #f6c85d 24%, #b87425 68%, #74451d 100%); animation-delay: 1.2s; }
.orb-c { width: 128px; height: 128px; right: 28%; top: 24%; background: radial-gradient(circle at 30% 30%, #ffcc9f 0, #ff9d5f 28%, #ce6032 72%, #7d3824 100%); animation-delay: 2.1s; }
.signal-strip { position: absolute; left: 0; right: 0; bottom: 24px; display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 12px; padding: 0 20px; }
.signal-item { padding: 14px 16px; border-radius: 22px; background: rgba(255, 252, 246, 0.72); border: 1px solid rgba(131, 146, 120, 0.12); backdrop-filter: blur(10px); }
.signal-item strong { display: block; margin-top: 4px; font-size: 1.2rem; color: #39493a; }
.feature-ribbon { display: grid; grid-template-columns: repeat(5, minmax(0, 1fr)); gap: 12px; padding: 18px 20px 0; transform: translateY(-24px); }
.feature-ribbon p { padding: 16px 12px; text-align: center; border-top: 1px solid rgba(114, 130, 103, 0.12); color: #5f6f58; background: rgba(255, 250, 243, 0.52); }
.story-section, .lots-section, .process-section, .final-section { padding: 64px 20px 0; }
.section-heading { max-width: 760px; }
.section-heading h2, .process-copy h2, .final-section h2 { margin-top: 12px; font-size: clamp(2.2rem, 4vw, 3.6rem); line-height: 1; }
.section-heading.compact { max-width: 560px; }
.story-grid { display: grid; grid-template-columns: 1.1fr 0.9fr 0.9fr; gap: 18px; margin-top: 28px; }
.story-panel, .lot-item, .process-item, .final-section { border: 1px solid rgba(123, 135, 113, 0.14); }
.story-panel { min-height: 260px; padding: 28px; border-radius: 30px; }
.intro-panel { background: linear-gradient(160deg, rgba(251, 243, 231, 0.9), rgba(236, 245, 232, 0.84)); }
.warm-panel { background: linear-gradient(180deg, rgba(247, 230, 210, 0.7), rgba(252, 246, 240, 0.92)); }
.quiet-panel { background: linear-gradient(180deg, rgba(233, 240, 232, 0.86), rgba(247, 245, 238, 0.92)); }
.panel-kicker { font-size: 0.84rem; letter-spacing: 0.18em; text-transform: uppercase; color: #708168; }
.story-panel h3, .lot-item h3, .process-item h3 { margin-top: 18px; font-family: var(--font-display); font-size: 1.9rem; font-weight: 600; line-height: 1; }
.story-panel p:last-child, .lot-item p:nth-of-type(2), .process-item p { margin-top: 14px; color: #5b6858; line-height: 1.8; }
.lots-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 18px; margin-top: 26px; }
.lot-item { position: relative; min-height: 240px; padding: 26px; border-radius: 28px; overflow: hidden; background: radial-gradient(circle at top right, rgba(255, 245, 214, 0.66), transparent 34%), linear-gradient(180deg, rgba(253, 248, 241, 0.92), rgba(237, 244, 234, 0.9)); }
.lot-code { position: absolute; right: 24px; bottom: 22px; color: #708068; letter-spacing: 0.12em; }
.process-section { display: grid; grid-template-columns: minmax(0, 0.85fr) minmax(0, 1.15fr); gap: 30px; align-items: start; }
.process-copy { position: sticky; top: 24px; }
.process-list { display: grid; gap: 16px; }
.process-item { display: grid; grid-template-columns: 100px 1fr; gap: 18px; padding: 24px; border-radius: 26px; background: rgba(255, 251, 246, 0.72); transition: transform 240ms ease, box-shadow 240ms ease; }
.process-item:hover { transform: translateY(-2px); box-shadow: 0 18px 36px rgba(119, 109, 90, 0.1); }
.process-index { display: inline-flex; align-items: flex-start; justify-content: flex-start; font-family: var(--font-display); font-size: 3rem; color: #80907a; }
.final-section { margin: 72px auto 48px; padding: 34px; border-radius: 36px; display: flex; align-items: center; justify-content: space-between; gap: 18px; background: radial-gradient(circle at 0% 10%, rgba(255, 223, 188, 0.54), transparent 26%), linear-gradient(135deg, rgba(250, 244, 235, 0.96), rgba(233, 240, 231, 0.96)); }
.final-actions { display: flex; flex-wrap: wrap; gap: 12px; }
@keyframes fade-up { from { opacity: 0; transform: translateY(22px); } to { opacity: 1; transform: translateY(0); } }
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-12px); } }
@media (max-width: 980px) {
  .hero-body, .story-grid, .lots-grid, .process-section, .feature-ribbon, .signal-strip { grid-template-columns: 1fr; }
  .hero-section { min-height: auto; }
  .hero-body { min-height: auto; padding-top: 24px; }
  .hero-copy { padding-top: 34px; }
  .hero-visual { min-height: 540px; height: auto; }
  .feature-ribbon { transform: none; padding-top: 12px; }
  .process-copy { position: static; }
  .final-section { flex-direction: column; align-items: flex-start; }
}
@media (max-width: 640px) {
  .hero-section { padding-inline: 16px; }
  .hero-nav, .hero-actions, .hero-cta, .final-actions { flex-direction: column; align-items: stretch; }
  .hero-copy h1 { font-size: clamp(2.2rem, 12vw, 3.2rem); }
  .hero-visual { min-height: 420px; }
  .fruit-stage { border-radius: 30px; }
  .stage-copy { left: 20px; top: 20px; }
  .signal-strip { padding-inline: 12px; bottom: 12px; }
  .process-item { grid-template-columns: 1fr; }
}
@media (prefers-reduced-motion: reduce) {
  .hero-copy, .hero-visual, .stage-orb, .nav-link, .hero-button, .process-item { animation: none !important; transition: none !important; }
}
</style>
