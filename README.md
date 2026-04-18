# TraceMall

> 面向《可溯源的水果商城设计与实现》的重构版本。
> 当前分支进入 M1 闭环实现阶段（商城 + 溯源 + 验真）。

## 1. 项目目标

TraceMall 目标是构建一个“可交易 + 可追溯 + 可监管”的水果商城系统，解决传统链路中信息不透明、数据可篡改、消费者难以验证来源的问题。

相对普通毕设，本项目标准提升为：
- 不只做商城功能，而是做“交易链路 + 溯源链路 + 防伪链路”三链协同；
- 不只展示二维码，而是提供“校验规则 + 篡改检测 + 风险告警”；
- 不只完成 CRUD，而是形成“论文可论证”的指标体系与实验设计。

## 2. 研究问题映射（对应开题报告）

1. 水果供应链溯源需求分析
2. 区块链 + 二维码融合的数据模型设计
3. 二维码防伪与校验逻辑设计
4. 区块链 + 商城功能模块设计与实现
5. 系统测试与可行性验证

## 3. 技术路线（重构后的优选方案）

### 3.1 架构分层
- 展示层：用户端商城 + 管理后台
- 业务层：商品、订单、溯源、防伪、监管查询
- 数据层：MySQL（业务数据）+ Redis（缓存）
- 可信层：区块链存证（关键事件哈希上链，业务明细链下）

### 3.2 溯源模型（推荐）
- 链下存明细：种植、采收、分拣、质检、仓储、运输、签收
- 链上存哈希：每次关键事件生成摘要并锚定，保证不可篡改
- 二维码承载：traceId + 批次 + 校验字段（签名/摘要）

### 3.3 防伪校验核心
- 规则1：二维码格式合法性校验
- 规则2：签名/摘要一致性校验
- 规则3：上链锚点一致性校验
- 规则4：扫描频次与地理异常检测（风控）

## 4. 当前工程状态

## Monorepo 目录
- `web/`：Vue 3 前端
- `server/`：Spring Boot 后端
- `docs1/`：开发过程、项目介绍与使用说明
- `docs2/`：项目技术文档资料

## 前端基线
- Vue 3 + Vite
- Vue Router
- Pinia
- ESLint + Prettier

## 后端基础依赖
文件：`server/pom.xml`
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-security`
- `spring-boot-starter-data-redis`
- `spring-boot-starter-actuator`
- `mybatis-spring-boot-starter`（3.0.5）
- `mysql-connector-j`
- `flyway-core` + `flyway-mysql`
- `springdoc-openapi-starter-webmvc-ui`
- `lombok`
- `spring-boot-starter-test` + `spring-security-test`

## 5. 快速启动（当前基线）

### 前端
```bash
cd web
npm install
npm run dev
```

### 后端
```bash
cd server
./mvnw spring-boot:run
```

默认数据库连接（可通过环境变量覆盖）：
- Host: `localhost`
- Port: `3308`
- Database: `tracemall`
- Username: `root`
- Password: `123456`

## 6. 里程碑

- M0：工程基线与规范
- M1：最小可演示闭环（商品、订单、溯源、验真）
- M2：可信增强（锚定、告警、监管）
- M3：质量与论文支撑（性能、安全、实验）

## 7. 当前文档

- `docs1/TraceMall-开发过程与心路历程.md`
- `docs1/TraceMall-项目介绍与使用说明.md`
- `docs2/TraceMall-M1-项目全景技术文档-GPT5.2输入版.md`
