# API 分组说明

## 认证
- `POST /api/auth/login`

## 商城（消费者）
- `GET /api/fruits`
- `GET /api/fruits/{fruitId}`
- `POST /api/orders`
- `POST /api/orders/{orderNo}/pay`
- `GET /api/orders/mine`

## 商家
- `GET /api/merchant/fruits`
- `POST /api/merchant/fruits`
- `POST /api/merchant/batches`
- `POST /api/merchant/batch-events`

## 溯源与验真
- `GET /api/trace/{traceId}`
- `POST /api/trace/verify`

## 监管
- `GET /api/regulator/alerts`
- `GET /api/regulator/audits`

## 统一返回体
`ApiResponse<T> { code, message, data, traceId, timestamp }`