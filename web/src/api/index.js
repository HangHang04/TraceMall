import http from '@/lib/http'

export async function login(payload) {
  const { data } = await http.post('/api/auth/login', payload)
  return data.data
}

export async function listFruits() {
  const { data } = await http.get('/api/fruits')
  return data.data || []
}

export async function getFruit(id) {
  const { data } = await http.get(`/api/fruits/${id}`)
  return data.data
}

export async function createOrder(payload) {
  const { data } = await http.post('/api/orders', payload)
  return data.data
}

export async function payOrder(orderNo, payload = {}) {
  const { data } = await http.post(`/api/orders/${orderNo}/pay`, payload)
  return data.data
}

export async function listMyOrders() {
  const { data } = await http.get('/api/orders/mine')
  return data.data || []
}

export async function listMerchantFruits() {
  const { data } = await http.get('/api/merchant/fruits')
  return data.data || []
}

export async function createMerchantFruit(payload) {
  const { data } = await http.post('/api/merchant/fruits', payload)
  return data.data
}

export async function createBatch(payload) {
  const { data } = await http.post('/api/merchant/batches', payload)
  return data.data
}

export async function createBatchEvent(payload) {
  const { data } = await http.post('/api/merchant/batch-events', payload)
  return data.data
}

export async function traceDetail(traceId) {
  const { data } = await http.get(`/api/trace/${traceId}`)
  return data.data
}

export async function verifyTrace(payload) {
  const { data } = await http.post('/api/trace/verify', payload)
  return data.data
}

export async function listRiskAlerts(page = 1, size = 10) {
  const { data } = await http.get('/api/regulator/alerts', { params: { page, size } })
  return data.data
}

export async function listAuditLogs(page = 1, size = 10) {
  const { data } = await http.get('/api/regulator/audits', { params: { page, size } })
  return data.data
}