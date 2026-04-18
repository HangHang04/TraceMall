export const ORDER_STATUS_MAP = {
  CREATED: '已创建',
  PENDING_PAYMENT: '待支付',
  PAID: '已支付',
  SHIPPED: '已发货',
  COMPLETED: '已完成',
  DEPRECATED: '已废弃',
}

export const MERCHANT_STATUS_MAP = {
  ON_SALE: '在售',
  OFF_SALE: '下架',
  DISABLED: '禁用',
}

export const BATCH_STATUS_MAP = {
  IN_STOCK: '在库',
  SOLD_OUT: '售罄',
  EXPIRED: '已过期',
}

export const VERIFY_STATUS_MAP = {
  PASS: '通过',
  FAIL: '失败',
  SUSPECT: '可疑',
  HIGH_RISK: '高风险',
}

export const VERIFY_REASON_MAP = {
  'verify passed': '校验通过',
  'signature mismatch': '签名不一致',
  'anchor mismatch': '链上锚点不一致',
  'trace code not found': '未找到该溯源码',
  'traceId format invalid': '溯源码格式不合法',
  'frequent scan anomaly': '扫描频次或地理位置异常',
}

export function toOrderStatusLabel(status) {
  return ORDER_STATUS_MAP[status] || status || '-'
}

export function toMerchantStatusLabel(status) {
  return MERCHANT_STATUS_MAP[status] || status || '-'
}

export function toBatchStatusLabel(status) {
  return BATCH_STATUS_MAP[status] || status || '-'
}

export function toVerifyStatusLabel(status) {
  return VERIFY_STATUS_MAP[status] || status || '-'
}

export function toVerifyReasonLabel(reason) {
  return VERIFY_REASON_MAP[reason] || reason || '-'
}

export function toRoleLabel(role) {
  if (role === 'CONSUMER') return '用户'
  if (role === 'MERCHANT') return '商户'
  if (role === 'REGULATOR') return '监管方'
  return role || '-'
}
