# 演示脚本（答辩流程）

1. 启动环境
- `docker compose up -d`
- `cd server && ./mvnw spring-boot:run`
- `cd web && npm run dev`

2. 登录演示
- 消费者：`consumer01 / 123456`
- 商家：`merchant01 / 123456`
- 监管员：`regulator01 / 123456`

3. 商家流程
- 新增水果
- 创建批次（返回 `traceId`）
- 提交批次事件（返回 `anchorId`）

4. 消费者流程
- 浏览商品 -> 加入购物车 -> 提交订单 -> 模拟支付
- 输入 `traceId + signature` 执行验真

5. 监管流程
- 查看风险告警
- 查看审计日志

6. 篡改演示
- 修改签名后再次验真，展示 `FAIL` 与告警生成