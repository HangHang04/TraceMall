# Release Notes - v0.1-m1-closed-loop

## 范围
- 完成 M1 闭环：商品、批次、订单、模拟支付、溯源查询、验真

## 关键能力
- JWT + RBAC（三角色）
- Flyway 数据迁移与演示数据
- 存证网关抽象 + Mock 实现
- 风险告警与审计日志
- 高保真前端仪表盘和业务页面

## 验证
- `server`: `./mvnw test` 通过
- `web`: `npm run build` 通过

## 已知限制
- 区块链为 Mock，未接入真实 Fabric/EVM
- 前端图表 chunk 较大（ECharts 体积影响）