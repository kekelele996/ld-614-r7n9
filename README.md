# 制造业质量追溯 API 服务

面向小型制造工厂的批次质量追溯后端服务，覆盖工单、批次、检验项、不良记录和追溯查询。

## 快速启动

```bash
cp .env.example .env && docker compose up -d
```

## 访问地址或 CLI 示例

后端健康检查：<http://localhost:21114/health>

批次放行（仅质量经理，通过 `X-Role` / `X-User` 请求头标识）：

```bash
# 提交放行结论：RELEASED 或 CONDITIONAL_RELEASED（有条件放行必须带 restrictionNote）
curl -X POST http://localhost:21114/api/batches/BATCH-OK-001/release \
  -H 'Content-Type: application/json' \
  -H 'X-Role: QUALITY_MANAGER' -H 'X-User: manager-li' \
  -d '{"decision":"RELEASED"}'

# 查询批次放行记录
curl http://localhost:21114/api/batches/BATCH-OK-001/release

# 批次全链路追溯树（含放行结论、限制说明及对应依据）
curl http://localhost:21114/api/trace/BATCH-OK-001
```

### 批次质量放行规则

- 存在未关闭的重大（MAJOR）或严重（CRITICAL）不良，或最近检验为不合格（FAIL）/待复检（RECHECK）时拒绝放行，响应 `422` 并返回全部阻塞项 `blockers`。
- 一般（MINOR）不良已关闭且最近检验为让步接收（CONDITIONAL_PASS）时，仅允许有条件放行（CONDITIONAL_RELEASED），且必须填写限制说明 `restrictionNote`。
- 放行成功记录放行人与放行时间；同一批次重复或并发提交只有一笔生效，其余返回 `409 RELEASE_ALREADY_EXISTS`。
- 放行失败时批次、不良与放行记录全部保持原样；追溯查询 `GET /api/trace/{batchNo}` 展示放行结论、限制说明及对应依据。


## 本地开发方式


- 后端：进入 `backend` 后按技术栈运行开发命令，接口统一挂在 `/api`。


## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | - |
| 后端 | Spring Boot 3 + Java 17 + MyBatis-Plus |
| 数据库 | PostgreSQL 15 |
| 部署 | Docker Compose |

## 项目目录结构

```text

backend/src/routes, controllers, services, models, repositories, middlewares, constants, constructors, utils, types, config
```

## 环境变量说明

- `COMPOSE_PROJECT_NAME`: Compose 项目名，默认 `quality-trace`

- `BACKEND_PORT`: 后端端口，默认 `21114`
- `DB_PORT`: 数据库宿主机端口
- `DB_USER/DB_PASSWORD/DB_NAME`: 本地数据库凭据

## Docker 部署说明

- 根 Compose 文件不写 `version`，顶层 `name: quality-trace`。
- 容器名均使用 `${COMPOSE_PROJECT_NAME:-quality-trace}` 前缀。
- 数据库使用命名卷，避免绑定中文路径。
- 常见问题：端口占用时修改 `.env` 中端口后重启；需要重置数据时执行 `docker compose down -v`。

## 枚举/常量出现位置清单

- WorkOrderStatus: constants/WorkOrderStatus、types/WorkOrderStatus、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- InspectionResultStatus: constants/InspectionResultStatus、types/InspectionResultStatus、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- DefectSeverity: constants/DefectSeverity、types/DefectSeverity、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- ReleaseDecision（RELEASED / CONDITIONAL_RELEASED）: constants/ReleaseDecision、validators/BatchReleaseValidator、services/BatchReleaseService、constructors/BatchReleaseDtoFactory、utils/Formatters、logTemplates、errorMessages。
- DispositionStatus（OPEN / DISPOSED / CLOSED）: constants/DispositionStatus、services/BatchReleaseService、utils/Formatters、repositories/DefectRecordRepository（种子数据）。
- BatchStatus（CREATED / IN_INSPECTION / ON_HOLD / RELEASED / CONDITIONAL_RELEASED）: constants/BatchStatus、services/BatchReleaseService、repositories/ProductBatchRepository、utils/Formatters。
- UserRole（INSPECTOR / LINE_SUPERVISOR / QUALITY_MANAGER / AUDITOR）: constants/UserRole、middlewares/RbacMiddleware、controllers/BatchReleaseController。

## 为什么会牵一发动全身

实体字段、枚举、日志模板、错误消息、构造器、筛选器和展示组件被刻意拆散到多个目录；修改一个状态值通常需要同步类型、构造器、服务、控制器、store、页面、README 与数据库种子。

## License

MIT
