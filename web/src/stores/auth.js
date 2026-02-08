import { defineStore } from 'pinia'
import { login as loginApi } from '@/api'

const TOKEN_KEY = 'tracemall_token'
const USER_KEY = 'tracemall_user'

function readUser() {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: readUser(),
  }),
  getters: {
    role: (state) => state.user?.role || '',
    username: (state) => state.user?.username || '',
  },
  actions: {
    async login(payload) {
      const data = await loginApi(payload)
      this.token = data.accessToken
      this.user = {
        userId: data.userId,
        username: data.username,
        role: data.role,
        shopId: data.shopId,
      }
      localStorage.setItem(TOKEN_KEY, this.token)
      localStorage.setItem(USER_KEY, JSON.stringify(this.user))
      return this.user
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
  },
})