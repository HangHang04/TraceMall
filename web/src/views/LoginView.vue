<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const error = ref('')

const form = reactive({
  username: 'consumer01',
  password: '123456',
})

async function submit() {
  loading.value = true
  error.value = ''
  try {
    const user = await auth.login(form)
    if (user.role === 'MERCHANT') {
      router.push('/merchant')
    } else if (user.role === 'REGULATOR') {
      router.push('/regulator')
    } else {
      router.push('/dashboard')
    }
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <section class="login-showcase">
      <div>
        <span class="hero-tag">TraceMall</span>
        <h1>可信果品流通的统一入口。</h1>
        <p>同一套前端覆盖消费者、商家和监管方，让交易、验真与监管共享同一条批次链路。</p>
      </div>

      <div class="login-showcase-grid">
        <div class="login-point">
          <strong>消费者</strong>
          <span>浏览商品、验真扫码、查看订单与批次来源。</span>
        </div>
        <div class="login-point">
          <strong>商家</strong>
          <span>录入商品、创建批次、生成二维码并补录事件。</span>
        </div>
        <div class="login-point">
          <strong>监管方</strong>
          <span>查看告警、审计日志和高风险批次线索。</span>
        </div>
      </div>
    </section>

    <section class="login-card">
      <div>
        <h2>登录系统</h2>
        <p class="panel-copy">使用现有测试账号进入对应角色界面。</p>
      </div>

      <div class="hint-box">
        consumer01 / merchant01 / regulator01
        <br />
        默认密码：123456
      </div>

      <label class="field-group">
        <span>用户名</span>
        <input v-model="form.username" />
      </label>

      <label class="field-group">
        <span>密码</span>
        <input type="password" v-model="form.password" @keyup.enter="submit" />
      </label>

      <button class="btn btn-primary btn-block" :disabled="loading" @click="submit">
        {{ loading ? '登录中...' : '进入系统' }}
      </button>
      <p class="error" v-if="error">{{ error }}</p>
    </section>
  </div>
</template>
