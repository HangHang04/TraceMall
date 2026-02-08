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
    <section class="login-card">
      <h1>TraceMall</h1>
      <p>可溯源水果商城登录</p>
      <div class="hint-box">
        演示账号：`consumer01 / merchant01 / regulator01`<br />
        密码均为：`123456`
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
  </div>
</template>