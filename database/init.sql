CREATE TABLE IF NOT EXISTS work_order (
  id INTEGER PRIMARY KEY,
  order_no TEXT,
  product_code TEXT,
  product_name TEXT,
  planned_qty TEXT,
  line_code TEXT,
  start_at TEXT,
  status TEXT
);

CREATE TABLE IF NOT EXISTS product_batch (
  id INTEGER PRIMARY KEY,
  batch_no TEXT UNIQUE,
  work_order_id TEXT,
  quantity TEXT,
  material_lot_no TEXT,
  produced_at TEXT,
  batch_status TEXT
);

CREATE TABLE IF NOT EXISTS quality_inspection (
  id INTEGER PRIMARY KEY,
  batch_id TEXT,
  inspector_id TEXT,
  inspection_type TEXT,
  standard_version TEXT,
  result_status TEXT,
  inspected_at TEXT
);

CREATE TABLE IF NOT EXISTS inspection_item_result (
  id INTEGER PRIMARY KEY,
  inspection_id TEXT,
  item_code TEXT,
  item_name TEXT,
  measured_value TEXT,
  limit_min TEXT,
  limit_max TEXT,
  item_status TEXT
);

CREATE TABLE IF NOT EXISTS defect_record (
  id INTEGER PRIMARY KEY,
  batch_id TEXT,
  defect_type TEXT,
  defect_qty TEXT,
  severity TEXT,
  root_cause TEXT,
  disposition_status TEXT,
  created_at TEXT
);

-- 批次质量放行记录：每个批次至多一条有效放行（重复/并发提交只能生效一次）
CREATE TABLE IF NOT EXISTS batch_release (
  id INTEGER PRIMARY KEY,
  batch_id INTEGER NOT NULL UNIQUE,
  batch_no TEXT,
  conclusion TEXT NOT NULL,              -- RELEASED / CONDITIONAL_RELEASED
  limit_note TEXT,                        -- 有条件放行时必填的限制说明
  released_by TEXT NOT NULL,              -- 放行人（质量经理工号）
  released_at TEXT NOT NULL,              -- 放行时间
  basis TEXT                              -- 放行依据快照（阻塞项/条件项/支持项 JSON）
);

CREATE TABLE IF NOT EXISTS audit_log (
  id INTEGER PRIMARY KEY,
  actor TEXT,
  action TEXT,
  target_type TEXT,
  target_id TEXT,
  detail TEXT,
  created_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_quality_inspection_batch ON quality_inspection(batch_id);
CREATE INDEX IF NOT EXISTS idx_defect_record_batch ON defect_record(batch_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_target ON audit_log(target_type, target_id);

-- 演示种子数据（与后端内存仓库保持一致）
INSERT INTO product_batch (id, batch_no, work_order_id, quantity, material_lot_no, produced_at, batch_status)
VALUES
  (1, 'B-2026-0001', '10', '120', 'MAT-A7', '2026-09-10T08:30:00', 'READY'),
  (2, 'B-2026-0002', '10', '200', 'MAT-A8', '2026-09-11T09:00:00', 'BLOCKED'),
  (3, 'B-2026-0003', '11', '80',  'MAT-B2', '2026-09-12T10:15:00', 'READY'),
  (4, 'B-2026-0004', '11', '60',  'MAT-B3', '2026-09-12T14:20:00', 'READY'),
  (5, 'B-2026-0005', '12', '300', 'MAT-C1', '2026-09-13T07:45:00', 'PENDING')
ON CONFLICT (id) DO NOTHING;

INSERT INTO defect_record (id, batch_id, defect_type, defect_qty, severity, root_cause, disposition_status, created_at)
VALUES
  (1, '1', 'SCRATCH',       '3',  'MINOR',    '包装划伤', 'CLOSED', '2026-09-10T11:00:00'),
  (2, '2', 'DIMENSION_OVER','12', 'CRITICAL', '模具偏移', 'OPEN',   '2026-09-11T13:30:00'),
  (3, '3', 'LABEL_MISS',    '2',  'MAJOR',    '标签漏贴', 'CLOSED', '2026-09-12T11:00:00'),
  (4, '4', 'BURR',          '5',  'MINOR',    '毛刺超标', 'OPEN',   '2026-09-12T16:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO quality_inspection (id, batch_id, inspector_id, inspection_type, standard_version, result_status, inspected_at)
VALUES
  (1, '1', 'U-1001', 'FIRST',  'STD-v3.2', 'PASS',             '2026-09-10T10:00:00'),
  (2, '1', 'U-1001', 'FINAL',  'STD-v3.2', 'CONDITIONAL_PASS', '2026-09-10T15:00:00'),
  (3, '2', 'U-1002', 'FINAL',  'STD-v3.2', 'FAIL',             '2026-09-11T16:30:00'),
  (4, '3', 'U-1003', 'FINAL',  'STD-v3.2', 'PASS',             '2026-09-12T15:00:00'),
  (5, '4', 'U-1004', 'PATROL', 'STD-v3.2', 'RECHECK',          '2026-09-12T17:00:00'),
  (6, '5', 'U-1005', 'FINAL',  'STD-v3.3', 'CONDITIONAL_PASS', '2026-09-13T09:30:00')
ON CONFLICT (id) DO NOTHING;
