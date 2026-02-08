# Commit Policy

## 分阶段提交规则

1. 每个阶段完成后立即提交一次。
2. 每次提交仅包含当前阶段相关文件，避免跨阶段混改。
3. 每次提交前执行最小校验：
   - `web`: `npm run build`
   - `server`: `./mvnw test`
   - Flyway 迁移脚本语法检查
4. 每次提交后立即推送：`git push origin main`。

## Commit Message 模板

使用 Conventional Commits：
- `chore(repo): ...`
- `chore(env): ...`
- `feat(db): ...`
- `feat(server): ...`
- `feat(trace): ...`
- `feat(web): ...`
- `test: ...`
- `docs: ...`

## 提交说明建议

提交描述中建议补充：
- 阶段目标
- 关键变更点
- 验证命令与结果
- 已知风险/后续事项
