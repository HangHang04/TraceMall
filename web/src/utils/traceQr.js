const TRACE_ID_PATTERN = /TRACE-[A-Z0-9-]{4,64}/i

export function buildTracePayload({ traceId = '', signature = '', batchNo = '' } = {}) {
  return JSON.stringify(
    {
      traceId,
      signature,
      batchNo,
      scene: 'trace-verify',
    },
    null,
    0,
  )
}

export function buildTraceVerifyUrl({ traceId = '', signature = '' } = {}) {
  const origin = typeof window !== 'undefined' ? window.location.origin : ''
  const url = new URL('/trace/scan', origin || 'http://localhost')
  if (traceId) {
    url.searchParams.set('traceId', traceId)
  }
  if (signature) {
    url.searchParams.set('signature', signature)
  }
  return origin ? url.toString() : `${url.pathname}${url.search}`
}

export function parseTraceScanContent(raw) {
  const text = String(raw || '').trim()
  const result = {
    rawText: text,
    traceId: '',
    signature: '',
    batchNo: '',
    source: 'unknown',
  }

  if (!text) {
    return result
  }

  try {
    const url = new URL(text)
    result.traceId = url.searchParams.get('traceId') || ''
    result.signature = url.searchParams.get('signature') || ''
    result.batchNo = url.searchParams.get('batchNo') || ''
    result.source = 'url'
    if (result.traceId) {
      return result
    }
  } catch {
    // Non-URL payloads fall through to the next parser.
  }

  try {
    const parsed = JSON.parse(text)
    result.traceId = parsed.traceId || parsed.code || ''
    result.signature = parsed.signature || parsed.sign || ''
    result.batchNo = parsed.batchNo || ''
    result.source = 'json'
    if (result.traceId) {
      return result
    }
  } catch {
    // Plain-text payloads fall through to regex extraction.
  }

  const matchedTraceId = text.match(TRACE_ID_PATTERN)?.[0] || ''
  if (matchedTraceId) {
    result.traceId = matchedTraceId.toUpperCase()
    result.source = 'text'
  }

  return result
}

export function buildDeviceFingerprint() {
  if (typeof navigator === 'undefined') {
    return 'web-ssr'
  }
  const lang = navigator.language || 'unknown-lang'
  const ua = navigator.userAgent || 'unknown-agent'
  return `web:${lang}:${ua.slice(0, 32)}`
}
