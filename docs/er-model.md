# TraceMall ER 模型说明（M1）

## 核心实体
- `tm_user`：系统用户
- `tm_role` / `tm_user_role`：角色与授权映射
- `tm_shop`：商家店铺
- `tm_fruit`：水果商品
- `tm_batch`：批次
- `tm_batch_event`：批次事件链
- `tm_trace_code`：溯源码
- `tm_order` / `tm_order_item`：订单与订单项
- `tm_inventory`：库存
- `tm_trace_scan_log`：扫码日志
- `tm_risk_alert`：风险告警
- `tm_chain_anchor`：链上锚定记录（Mock）
- `tm_audit_log`：审计日志

## 关键关系
1. 用户-角色：多对多（`tm_user_role`）
2. 商家-商品：一对多（`tm_shop` -> `tm_fruit`）
3. 商品-批次：一对多（`tm_fruit` -> `tm_batch`）
4. 批次-事件：一对多（`tm_batch` -> `tm_batch_event`）
5. 批次-溯源码：一对一（`tm_batch` -> `tm_trace_code`）
6. 订单-订单项：一对多（`tm_order` -> `tm_order_item`）
7. 扫码日志-风险告警：按 `trace_id` 聚合分析

## 命名和约束
- 业务唯一：`trace_id`、`batch_no`、`order_no`
- 所有表包含：`created_at`, `updated_at`, `is_deleted`
- 金额统一：`DECIMAL(10,2)`
- 数量统一：`DECIMAL(10,3)`