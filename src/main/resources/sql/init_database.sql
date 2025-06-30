-- 健康管理系统数据库初始化脚本
-- 数据库：health_management_system
-- 作者：AI健康管理系统开发团队
-- 创建日期：2024

-- 如果数据库已存在则删除并重新创建（谨慎使用）
-- DROP DATABASE IF EXISTS health_management_system;
-- CREATE DATABASE health_management_system WITH ENCODING 'UTF8';

-- 使用数据库
-- \c health_management_system;

-- 1. 创建users表
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    uname VARCHAR(100),
    tel VARCHAR(20),
    sex VARCHAR(10),
    bir DATE,
    idcard VARCHAR(18),
    address VARCHAR(255),
    dep VARCHAR(50),
    lev VARCHAR(50),
    avatar VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'NORMAL_USER' CHECK (role IN ('NORMAL_USER', 'ADMIN', 'SUPER_ADMIN')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. 创建check_items表
CREATE TABLE IF NOT EXISTS check_items (
    item_id SERIAL PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL UNIQUE,
    item_name VARCHAR(100) NOT NULL,
    reference_val VARCHAR(255),
    unit VARCHAR(50),
    created_by INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 3. 创建check_groups表
CREATE TABLE IF NOT EXISTS check_groups (
    group_id SERIAL PRIMARY KEY,
    group_code VARCHAR(50) NOT NULL UNIQUE,
    group_name VARCHAR(100) NOT NULL,
    description TEXT,
    created_by INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 4. 创建group_check_item关联表
CREATE TABLE IF NOT EXISTS group_check_item (
    group_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    PRIMARY KEY (group_id, item_id),
    FOREIGN KEY (group_id) REFERENCES check_groups(group_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES check_items(item_id) ON DELETE CASCADE
);

-- 5. 创建appointments表
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME,
    examination_method VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT '待确认' CHECK (status IN ('待确认', '已确认', '已完成', '已取消')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES check_groups(group_id) ON DELETE CASCADE
);

-- 6. 创建examination_results表
CREATE TABLE IF NOT EXISTS examination_results (
    result_id SERIAL PRIMARY KEY,
    appointment_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    measured_value VARCHAR(255) NOT NULL,
    result_notes TEXT,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES check_groups(group_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES check_items(item_id) ON DELETE CASCADE
);

-- 7. 创建medical_history表
CREATE TABLE IF NOT EXISTS medical_history (
    history_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    diagnosis VARCHAR(255) NOT NULL,
    doctor_name VARCHAR(100),
    diagnosis_date DATE NOT NULL,
    treatment TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_check_items_code ON check_items(item_code);
CREATE INDEX IF NOT EXISTS idx_check_items_active ON check_items(is_active);
CREATE INDEX IF NOT EXISTS idx_check_groups_code ON check_groups(group_code);
CREATE INDEX IF NOT EXISTS idx_check_groups_active ON check_groups(is_active);
CREATE INDEX IF NOT EXISTS idx_appointments_user ON appointments(user_id);
CREATE INDEX IF NOT EXISTS idx_appointments_date ON appointments(appointment_date);
CREATE INDEX IF NOT EXISTS idx_appointments_status ON appointments(status);
CREATE INDEX IF NOT EXISTS idx_examination_results_user ON examination_results(user_id);
CREATE INDEX IF NOT EXISTS idx_examination_results_appointment ON examination_results(appointment_id);
CREATE INDEX IF NOT EXISTS idx_examination_results_group ON examination_results(group_id);
CREATE INDEX IF NOT EXISTS idx_medical_history_user ON medical_history(user_id);
CREATE INDEX IF NOT EXISTS idx_medical_history_date ON medical_history(diagnosis_date);

-- 插入初始数据（可选）
-- 1. 插入系统管理员用户
-- 用户名：MengChen
-- 密码：Test123
INSERT INTO users (username, password, email, uname, role) 
VALUES ('MengChen', '$2a$10$/yONHiDLT.R9eHCOIw89kej40HheY8JnauaxqLsJ7C7oDnSxkbrBi', 'admin@healthsys.com', '系统管理员', 'SUPER_ADMIN')
ON CONFLICT (username) DO NOTHING;

-- 2. 插入示例检查项
INSERT INTO check_items (item_code, item_name, reference_val, unit, is_active) VALUES
('BLD001', '血红蛋白', '120-160 g/L', 'g/L', TRUE),
('BLD002', '白细胞计数', '4.0-10.0 ×10^9/L', '×10^9/L', TRUE),
('BLD003', '红细胞计数', '4.0-5.5 ×10^12/L', '×10^12/L', TRUE),
('BLD004', '血小板计数', '100-300 ×10^9/L', '×10^9/L', TRUE),
('GLU001', '空腹血糖', '3.9-6.1 mmol/L', 'mmol/L', TRUE),
('CHO001', '总胆固醇', '<5.2 mmol/L', 'mmol/L', TRUE),
('CHO002', '甘油三酯', '<1.7 mmol/L', 'mmol/L', TRUE),
('LIV001', 'ALT谷丙转氨酶', '0-40 U/L', 'U/L', TRUE),
('LIV002', 'AST谷草转氨酶', '0-40 U/L', 'U/L', TRUE),
('KID001', '尿素氮', '2.9-8.2 mmol/L', 'mmol/L', TRUE)
ON CONFLICT (item_code) DO NOTHING;

-- 3. 插入示例检查组
INSERT INTO check_groups (group_code, group_name, description, is_active) VALUES
('GRP001', '血常规检查', '包含血红蛋白、白细胞、红细胞、血小板等基础血液检查项目', TRUE),
('GRP002', '肝功能检查', '包含ALT、AST等肝功能相关检查项目', TRUE),
('GRP003', '血糖血脂检查', '包含血糖、胆固醇、甘油三酯等代谢相关检查项目', TRUE),
('GRP004', '基础体检套餐', '包含血常规、肝功能、血糖血脂等基础检查项目', TRUE)
ON CONFLICT (group_code) DO NOTHING;

-- 4. 建立检查组与检查项的关联关系
-- 血常规检查组
INSERT INTO group_check_item (group_id, item_id) 
SELECT g.group_id, i.item_id 
FROM check_groups g, check_items i 
WHERE g.group_code = 'GRP001' AND i.item_code IN ('BLD001', 'BLD002', 'BLD003', 'BLD004')
ON CONFLICT DO NOTHING;

-- 肝功能检查组
INSERT INTO group_check_item (group_id, item_id) 
SELECT g.group_id, i.item_id 
FROM check_groups g, check_items i 
WHERE g.group_code = 'GRP002' AND i.item_code IN ('LIV001', 'LIV002')
ON CONFLICT DO NOTHING;

-- 血糖血脂检查组
INSERT INTO group_check_item (group_id, item_id) 
SELECT g.group_id, i.item_id 
FROM check_groups g, check_items i 
WHERE g.group_code = 'GRP003' AND i.item_code IN ('GLU001', 'CHO001', 'CHO002')
ON CONFLICT DO NOTHING;

-- 基础体检套餐
INSERT INTO group_check_item (group_id, item_id) 
SELECT g.group_id, i.item_id 
FROM check_groups g, check_items i 
WHERE g.group_code = 'GRP004' AND i.item_code IN ('BLD001', 'BLD002', 'BLD003', 'BLD004', 'GLU001', 'CHO001', 'CHO002', 'LIV001', 'LIV002', 'KID001')
ON CONFLICT DO NOTHING;

-- 数据库初始化完成
-- 可以使用以下命令在终端执行此脚本：
-- psql -U postgres -d health_management_system -f init_database.sql 

-- 添加测试数据（用于演示和调试）
-- 5. 插入测试用户
INSERT INTO users (username, password, email, uname, tel, sex, role) VALUES
('testuser1', '$2a$10$/yONHiDLT.R9eHCOIw89kej40HheY8JnauaxqLsJ7C7oDnSxkbrBi', 'user1@test.com', '张三', '13800138001', '男', 'NORMAL_USER'),
('testuser2', '$2a$10$/yONHiDLT.R9eHCOIw89kej40HheY8JnauaxqLsJ7C7oDnSxkbrBi', 'user2@test.com', '李四', '13800138002', '女', 'NORMAL_USER'),
('testadmin', '$2a$10$/yONHiDLT.R9eHCOIw89kej40HheY8JnauaxqLsJ7C7oDnSxkbrBi', 'admin@test.com', '管理员', '13800138888', '男', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- 6. 插入测试预约
INSERT INTO appointments (user_id, group_id, appointment_date, appointment_time, examination_method, status) 
SELECT u.user_id, g.group_id, '2024-01-15', '09:00:00', '医院体检', '已完成'
FROM users u, check_groups g 
WHERE u.username = 'testuser1' AND g.group_code = 'GRP001'
ON CONFLICT DO NOTHING;

INSERT INTO appointments (user_id, group_id, appointment_date, appointment_time, examination_method, status) 
SELECT u.user_id, g.group_id, '2024-01-20', '10:30:00', '医院体检', '已完成'
FROM users u, check_groups g 
WHERE u.username = 'testuser1' AND g.group_code = 'GRP003'
ON CONFLICT DO NOTHING;

INSERT INTO appointments (user_id, group_id, appointment_date, appointment_time, examination_method, status) 
SELECT u.user_id, g.group_id, '2024-02-01', '14:00:00', '上门体检', '已完成'
FROM users u, check_groups g 
WHERE u.username = 'testuser2' AND g.group_code = 'GRP004'
ON CONFLICT DO NOTHING;

INSERT INTO appointments (user_id, group_id, appointment_date, appointment_time, examination_method, status) 
SELECT u.user_id, g.group_id, '2024-02-10', '09:30:00', '医院体检', '已确认'
FROM users u, check_groups g 
WHERE u.username = 'testuser1' AND g.group_code = 'GRP002'
ON CONFLICT DO NOTHING;

-- 7. 插入测试体检结果
-- 为testuser1的血常规检查（预约1）插入结果
INSERT INTO examination_results (appointment_id, user_id, group_id, item_id, measured_value, result_notes, recorded_at)
SELECT a.appointment_id, a.user_id, a.group_id, i.item_id, 
       CASE 
         WHEN i.item_code = 'BLD001' THEN '135'
         WHEN i.item_code = 'BLD002' THEN '6.8'
         WHEN i.item_code = 'BLD003' THEN '4.5'
         WHEN i.item_code = 'BLD004' THEN '250'
       END as measured_value,
       CASE 
         WHEN i.item_code = 'BLD001' THEN '正常范围'
         WHEN i.item_code = 'BLD002' THEN '正常'
         WHEN i.item_code = 'BLD003' THEN '正常'
         WHEN i.item_code = 'BLD004' THEN '正常'
       END as result_notes,
       '2024-01-15 10:30:00'
FROM appointments a
JOIN users u ON a.user_id = u.user_id
JOIN check_groups g ON a.group_id = g.group_id
JOIN group_check_item gci ON g.group_id = gci.group_id
JOIN check_items i ON gci.item_id = i.item_id
WHERE u.username = 'testuser1' 
  AND g.group_code = 'GRP001'
  AND a.appointment_date = '2024-01-15'
ON CONFLICT DO NOTHING;

-- 为testuser1的血糖血脂检查（预约2）插入结果
INSERT INTO examination_results (appointment_id, user_id, group_id, item_id, measured_value, result_notes, recorded_at)
SELECT a.appointment_id, a.user_id, a.group_id, i.item_id, 
       CASE 
         WHEN i.item_code = 'GLU001' THEN '5.2'
         WHEN i.item_code = 'CHO001' THEN '4.8'
         WHEN i.item_code = 'CHO002' THEN '1.2'
       END as measured_value,
       CASE 
         WHEN i.item_code = 'GLU001' THEN '血糖正常'
         WHEN i.item_code = 'CHO001' THEN '胆固醇正常'
         WHEN i.item_code = 'CHO002' THEN '甘油三酯正常'
       END as result_notes,
       '2024-01-20 11:15:00'
FROM appointments a
JOIN users u ON a.user_id = u.user_id
JOIN check_groups g ON a.group_id = g.group_id
JOIN group_check_item gci ON g.group_id = gci.group_id
JOIN check_items i ON gci.item_id = i.item_id
WHERE u.username = 'testuser1' 
  AND g.group_code = 'GRP003'
  AND a.appointment_date = '2024-01-20'
ON CONFLICT DO NOTHING;

-- 为testuser2的基础体检套餐（预约3）插入部分结果
INSERT INTO examination_results (appointment_id, user_id, group_id, item_id, measured_value, result_notes, recorded_at)
SELECT a.appointment_id, a.user_id, a.group_id, i.item_id, 
       CASE 
         WHEN i.item_code = 'BLD001' THEN '128'
         WHEN i.item_code = 'BLD002' THEN '5.5'
         WHEN i.item_code = 'GLU001' THEN '4.8'
         WHEN i.item_code = 'CHO001' THEN '5.8'
         WHEN i.item_code = 'LIV001' THEN '32'
         WHEN i.item_code = 'LIV002' THEN '28'
       END as measured_value,
       CASE 
         WHEN i.item_code = 'BLD001' THEN '略低，建议补充营养'
         WHEN i.item_code = 'BLD002' THEN '正常'
         WHEN i.item_code = 'GLU001' THEN '血糖正常'
         WHEN i.item_code = 'CHO001' THEN '略高，注意饮食'
         WHEN i.item_code = 'LIV001' THEN '肝功能正常'
         WHEN i.item_code = 'LIV002' THEN '肝功能正常'
       END as result_notes,
       '2024-02-01 15:30:00'
FROM appointments a
JOIN users u ON a.user_id = u.user_id
JOIN check_groups g ON a.group_id = g.group_id
JOIN group_check_item gci ON g.group_id = gci.group_id
JOIN check_items i ON gci.item_id = i.item_id
WHERE u.username = 'testuser2' 
  AND g.group_code = 'GRP004'
  AND a.appointment_date = '2024-02-01'
  AND i.item_code IN ('BLD001', 'BLD002', 'GLU001', 'CHO001', 'LIV001', 'LIV002')
ON CONFLICT DO NOTHING;

-- 8. 插入测试病史记录
INSERT INTO medical_history (user_id, diagnosis, doctor_name, diagnosis_date, treatment, notes)
SELECT u.user_id, '高血压', '张医生', '2023-06-15', '服用降压药，定期监测血压', '需要长期管理，注意饮食和运动'
FROM users u WHERE u.username = 'testuser1'
ON CONFLICT DO NOTHING;

INSERT INTO medical_history (user_id, diagnosis, doctor_name, diagnosis_date, treatment, notes)
SELECT u.user_id, '轻度贫血', '李医生', '2023-12-10', '补充铁剂，改善饮食结构', '建议多吃含铁丰富的食物'
FROM users u WHERE u.username = 'testuser2'
ON CONFLICT DO NOTHING;

-- 测试数据插入完成
-- 现在系统中有：
-- - 3个测试用户（testuser1, testuser2, testadmin）+ 1个超级管理员（MengChen）
-- - 10个检查项目
-- - 4个检查组
-- - 4个预约记录（3个已完成，1个已确认）
-- - 多条体检结果记录
-- - 2条病史记录 