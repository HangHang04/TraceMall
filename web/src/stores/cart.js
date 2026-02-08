import { defineStore } from 'pinia'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
  }),
  getters: {
    count: (state) => state.items.length,
    total: (state) =>
      state.items.reduce((sum, item) => {
        return sum + Number(item.quantity || 0) * Number(item.unitPrice || 0)
      }, 0),
  },
  actions: {
    add(item) {
      const found = this.items.find((it) => it.batchId === item.batchId)
      if (found) {
        found.quantity = Number(found.quantity) + Number(item.quantity || 1)
      } else {
        this.items.push({ ...item, quantity: Number(item.quantity || 1) })
      }
    },
    remove(batchId) {
      this.items = this.items.filter((it) => it.batchId !== batchId)
    },
    clear() {
      this.items = []
    },
  },
})