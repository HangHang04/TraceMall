# 验真规则说明（M1）

## 规则 1：二维码格式合法性
- 检查 `traceId` 是否符合 `TRACE-[A-Z0-9-]{4,64}`
- 非法则返回 `FAIL` 并记录高风险告警

## 规则 2：签名一致性
- 使用 `trace.sign.key` + `traceId` + `batchNo` 计算签名
- 与二维码签名和库内签名一致才通过
- 不一致返回 `FAIL`，触发 `SIGNATURE_MISMATCH`

## 规则 3：锚点一致性
- 从 `QUALITY_CHECK` 事件取 `anchorId` + `eventHash`
- 调用 `ChainAnchorGateway.verify(anchorId, eventHash)`
- 不匹配返回 `SUSPECT` 并记录 `ANCHOR_MISMATCH`

## 规则 4：扫描异常检测
- 统计近 5 分钟扫描次数与地理离散度
- 高频 + 异地判定为 `HIGH_RISK`
- 触发 `FREQUENT_SCAN` 告警