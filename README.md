# 制造业质量追溯 API 服务

面向小型制造工厂的批次质量追溯后端服务，覆盖工单、批次、检验项、不良记录、**批次质量放行**和追溯查询。

## 快速启动

```bash
cp .env.example .env && docker compose up -d
```

健康检查：<http://localhost:21114/health>

## 批次质量放行（新增功能）

质量经理按批次提交放行结论，系统在同一事务性流程内完成阻塞判定、结论校验、记录落库、状态更新与审计。

### 放行规则

| 情形 | 结果 |
|---|---|
| 存在**未关闭的重大（CRITICAL）或严重（MAJOR）不良** | 拒绝放行，返回**全部阻塞项** |
| 最近检验为**不合格（FAIL）或待复检（RECHECK）** | 拒绝放行，返回全部阻塞项 |
| 最近检验为**让步接收（CONDITIONAL_PASS）**，或存在未关闭一般不良（MINOR） | **只允许有条件放行（CONDITIONAL_RELEASED）**，且必须填写限制说明 |
| 一般不良已关闭且最近检验让步接收 | 只允许有条件放行（已关闭不良作为支持性依据） |
| 最近检验合格（PASS）且无未关闭不良 | 允许正常放行（RELEASED），限制说明必须为空 |
| 非质量经理提交 | 403 RBAC 拒绝 |
| 重复或并发提交同一批次 | 只有第一次生效（20 并发压测：1 成功 / 19 冲突），后续返回 409 与既有放行信息 |

失败（拒绝/冲突/校验失败）时，批次、不良记录、放行记录均保持原样，仅追加审计/追溯事件日志。成功后记录放行人（`releasedBy`）、放行时间（`releasedAt`），批次状态更新为 `RELEASED` 或 `CONDITIONAL_RELEASED`。

### 接口

- `POST /api/batch-releases`：质量经理提交放行结论（Header 可带 `X-Actor-Role`）
- `GET /api/batch-releases/by-batch/{batchId}`：查询批次的生效放行记录
- `GET /api/trace/{batchNo}`：批次全链路追溯树，放行区块展示**放行结论、限制说明、放行人和时间及对应依据（BLOCKER/CONDITION/SUPPORT）**

提交示例（有条件放行）：

```bash
curl -X POST http://localhost:21114/api/batch-releases \
  -H 'Content-Type: application/json' \
  -d '{
    "batchNo":"B-2026-0001",
    "conclusion":"CONDITIONAL_RELEASED",
    "limitNote":"仅限华东客户，30天内完成复判",
    "managerId":"M-1",
    "managerRole":"QUALITY_MANAGER"
  }'
```

被拒绝时响应体 `blockers` 数组返回全部阻塞项（不良/检验事实，含实体 ID 与结论）。

### 内置演示批次（种子数据）

| 批次 | 不良 | 最近检验 | 预期 |
|---|---|---|---|
| B-2026-0001 | MINOR 已关闭 | 让步接收 | 仅有条件放行 |
| B-2026-0002 | CRITICAL 未关闭 | 不合格 | 拒绝（2 个阻塞项） |
| B-2026-0003 | MAJOR 已关闭 | 合格 | 正常放行 |
| B-2026-0004 | MINOR 未关闭 | 待复检 | 拒绝（待复检阻塞） |
| B-2026-0005 | 无 | 让步接收 | 仅有条件放行 |

## 访问地址或 CLI 示例

- 后端健康检查：<http://localhost:21114/health>
- 批次列表：`GET http://localhost:21114/api/product-batch`
- 追溯查询：`GET http://localhost:21114/api/trace/B-2026-0001`

## 本地开发方式

- 后端：进入 `backend` 后执行 `mvn spring-boot:run`，接口统一挂在 `/api`，默认端口 8080。
- 打包：`mvn -DskipTests package`，产物为 `target/quality-trace-0.1.0.jar`。
- 当前数据访问为内存仓库（与原始骨架一致），`database/init.sql` 维护等价 PostgreSQL 表结构与种子数据（含 `batch_release` 表及 `batch_id UNIQUE` 约束）。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | - |
| 后端 | Spring Boot 3 + Java 17 + MyBatis-Plus |
| 数据库 | PostgreSQL 15 |
| 部署 | Docker Compose |

## 项目目录结构

```text
backend/src/main/java/com/generated/qualityTrace/
├── routes/               # BatchReleaseRoutes、TraceRoutes 等按实体分文件
├── controllers/          # BatchReleaseController、QualityTraceController ...
├── services/             # BatchReleaseService（规则核心）、QualityTraceQueryService ...
├── models/               # ProductBatch、DefectRecord、QualityInspection、BatchRelease、AuditLog
├── repositories/         # 并发安全内存仓库（BatchReleaseRepository 原子插入保证一次生效）
├── middlewares/          # Auth/Rbac/AuditLog/ErrorHandler/RateLimit
├── constants/            # 枚举、错误码、错误消息、日志模板
├── constructors/         # DTO 工厂 + ReleaseBasisAssembler/Parser（依据组装/解析）
├── validators/           # BatchReleaseValidator
├── exceptions/           # 放行领域异常（含全部阻塞项回传）
├── utils/                # Formatters（日期/风险等级/状态文案/审计标识）
├── types/                # ReleaseDecisionPayload 等记录类型
└── config/
```

## 环境变量说明

- `COMPOSE_PROJECT_NAME`: Compose 项目名，默认 `quality-trace`
- `BACKEND_PORT`: 后端端口，默认 `21114`
- `DB_PORT`: 数据库宿主机端口
- `DB_USER/DB_PASSWORD/DB_NAME`: 本地数据库凭据
- `JWT_SECRET`: JWT 密钥

## Docker 部署说明

- 根 Compose 文件不写 `version`，顶层 `name: quality-trace`。
- 容器名均使用 `${COMPOSE_PROJECT_NAME:-quality-trace}` 前缀。
- 数据库使用命名卷，避免绑定中文路径。
- 常见问题：端口占用时修改 `.env` 中端口后重启；需要重置数据时执行 `docker compose down -v`。

## 枚举/常量出现位置清单

- WorkOrderStatus: constants/WorkOrderStatus、types、constructors、logTemplates、errorMessages、筛选器、控制器均有引用。
- InspectionResultStatus（PASS/FAIL/CONDITIONAL_PASS/RECHECK）:
  constants/InspectionResultStatus（判定方法）、models/QualityInspection、types、QualityInspectionDtoFactory、
  BatchReleaseDtoFactory、ReleaseBasisAssembler、repositories、BatchReleaseService、QualityTraceQueryService、
  validators/BatchReleaseValidator、LogTemplates、ErrorMessages、utils/Formatters。
- DefectSeverity（MINOR/MAJOR/CRITICAL）:
  constants/DefectSeverity（`isMajorOrCritical`）、models/DefectRecord、types、DefectRecordDtoFactory、
  BatchReleaseDtoFactory、ReleaseBasisAssembler、repositories、BatchReleaseService、validators、LogTemplates、ErrorMessages、Formatters。
- ReleaseConclusion（RELEASED/CONDITIONAL_RELEASED）:
  constants/ReleaseConclusion、models/BatchRelease、types/ReleaseDecisionPayload、BatchReleaseDtoFactory、
  BatchReleaseValidator、LogTemplates、ErrorMessages、Formatters、BatchReleaseService、BatchReleaseController、BatchReleaseRepository。
- BatchStatus（PENDING/READY/BLOCKED/RELEASED/CONDITIONAL_RELEASED）:
  constants/BatchStatus、models/ProductBatch、ProductBatchDtoFactory、BatchReleaseDtoFactory、
  ProductBatchRepository、BatchReleaseService、Formatters。
- DefectDispositionStatus（OPEN/CLOSED）与 UserRole（含 QUALITY_MANAGER）: constants、models、middlewares/RbacMiddleware、validators、services。

## 为什么会牵一发动全身

实体字段、枚举、日志模板、错误消息、构造器、校验器、依据组装器、筛选器和展示组件被刻意拆散到多个目录；
修改一个放行状态值或一条阻塞规则，通常需要同步 `constants/`（枚举/错误码/错误消息/日志模板）、`validators/`、
`constructors/`（DTO 工厂与依据组装/解析成对维护）、`services/`、`controllers/`、`repositories/`、`database/init.sql`、
README 与种子数据。放行依据快照在 `BatchReleaseService` 序列化、`ReleaseBasisParser` 反序列化，字段变更必须同步两处。

## License

MIT
