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

const demoAccounts = [
  { label: '用户', value: 'consumer01' },
  { label: '商户', value: 'merchant01' },
  { label: '监管方', value: 'regulator01' },
]

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

function fillAccount(username) {
    form.username = username
    form.password = '123456'
}
</script>

<template>
  <div class="login-page refined-login">
    <section class="login-stage">
      <div class="login-copy">
        <p class="login-kicker">TraceMall Access</p>
        <h1>登录后进入统一风格的商城、商户端或监管视图。</h1>
        <p class="login-description">
          当前演示环境提供三种角色入口。系统会根据身份自动跳转到对应工作台，并保留追溯与验真能力。
        </p>
        <div class="account-pills">
          <button v-for="account in demoAccounts" :key="account.value" class="account-pill" @click="fillAccount(account.value)">
            <span>{{ account.label }}</span>
            <strong>{{ account.value }}</strong>
          </button>
        </div>
      </div>

      <section class="login-card refined">
        <div class="login-card-head">
          <p class="login-kicker">账户登录</p>
          <h2>进入系统</h2>
          <p>默认密码均为 `123456`。</p>
        </div>
        <label>用户名</label>
        <input v-model="form.username" />
        <label>密码</label>
        <input type="password" v-model="form.password" />
        <button class="btn btn-primary" :disabled="loading" @click="submit">
          {{ loading ? '登录中...' : '登录' }}
        </button>
        <p class="error" v-if="error">{{ error }}</p>
      </section>
    </section>
  </div>
</template>

<style scoped>
.refined-login {
  background:
    radial-gradient(circle at 10% 10%, rgba(255, 221, 188, 0.5), transparent 28%),
    radial-gradient(circle at 100% 0%, rgba(174, 196, 160, 0.28), transparent 24%),
    linear-gradient(135deg, #f8f1e7, #eef3e9);
}

.login-stage {
  width: min(1180px, 100%);
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(340px, 0.8fr);
  gap: 28px;
  align-items: center;
}

.login-copy {
  padding: 30px 0;
}

.login-kicker {
  color: var(--muted);
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.login-copy h1,
.login-card-head h2 {
  margin-top: 12px;
  font-family: var(--font-display);
  line-height: 0.98;
}

.login-copy h1 {
  font-size: clamp(3rem, 6vw, 5.4rem);
  max-width: 9ch;
}

.login-description {
  max-width: 34rem;
  margin-top: 18px;
  color: #556253;
  line-height: 1.85;
}

.account-pills {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 26px;
}

.account-pill {
  border: 1px solid rgba(110, 126, 100, 0.15);
  border-radius: 24px;
  background: rgba(255, 251, 246, 0.75);
  padding: 16px;
  text-align: left;
  cursor: pointer;
  color: inherit;
}

.account-pill span,
.login-card-head p {
  color: var(--muted);
}

.account-pill strong {
  display: block;
  margin-top: 6px;
}

.login-card.refined {
  width: 100%;
  padding: 30px;
  background: rgba(255, 252, 248, 0.88);
}

.login-card-head {
  margin-bottom: 8px;
}

.login-card-head h2 {
  font-size: 2.4rem;
}

@media (max-width: 900px) {
  .login-stage,
  .account-pills {
    grid-template-columns: 1fr;
  }
}
</style>
