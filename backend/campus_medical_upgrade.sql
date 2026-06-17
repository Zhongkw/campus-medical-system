/*
  校园医务系统 - 增量升级脚本（保留现有数据，不删库）
  适用：已有 campus_medical 数据库，仅需补齐本次改造新增的数据

  使用方式：
  1. Navicat 连接 MySQL，选中 campus_medical 库
  2. 新建查询，粘贴本文件全部内容
  3. 点击运行

  说明：
  - 不会 DROP 任何表，不会删除 sys_user / medical_* 业务数据
  - 若某步已执行过（如字段已存在），对应语句可能报错，可忽略继续执行后续语句
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `campus_medical`;

-- ============================================================
-- 第一步：sys_dict_data 表新增字段（基础数据管理需要）
-- 若 remark / category 已存在，会报 Duplicate column，忽略即可
-- ============================================================
ALTER TABLE `sys_dict_data`
  ADD COLUMN `remark` varchar(255) DEFAULT NULL COMMENT '备注描述' AFTER `status`;

ALTER TABLE `sys_dict_data`
  ADD COLUMN `category` varchar(100) DEFAULT NULL COMMENT '所属分类' AFTER `remark`;

-- ============================================================
-- 第二步：新增字典类型（药品分类、病种管理）
-- ============================================================
INSERT INTO `sys_dict_type` (`dict_type`, `dict_name`, `status`)
SELECT 'drug_category', '药品分类', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'drug_category');

INSERT INTO `sys_dict_type` (`dict_type`, `dict_name`, `status`)
SELECT 'disease', '病种管理', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'disease');

-- ============================================================
-- 第三步：新增药品分类字典数据（药师入库-分类编号下拉用）
-- ============================================================
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`, `remark`, `category`)
SELECT 'drug_category', '抗生素类', 'KSS', 1, 1, '抗生素类药品', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'drug_category' AND `dict_value` = 'KSS');

INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`, `remark`, `category`)
SELECT 'drug_category', '感冒用药', 'GM', 2, 1, '感冒类药品', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'drug_category' AND `dict_value` = 'GM');

INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`, `remark`, `category`)
SELECT 'drug_category', '止痛药', 'ZT', 3, 1, '止痛类药品', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'drug_category' AND `dict_value` = 'ZT');

INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`, `remark`, `category`)
SELECT 'drug_category', '外用药', 'YW', 4, 1, '外用类药品', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'drug_category' AND `dict_value` = 'YW');

-- ============================================================
-- 第四步：新增病种字典数据（基础数据管理-病种管理用）
-- ============================================================
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`, `remark`, `category`)
SELECT 'disease', '急性上呼吸道感染', 'NK001', 1, 1, '常见感冒症状', '内科疾病' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'disease' AND `dict_value` = 'NK001');

INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`, `remark`, `category`)
SELECT 'disease', '软组织损伤', 'WK001', 2, 1, '扭伤、拉伤等', '外科疾病' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'disease' AND `dict_value` = 'WK001');

INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`, `remark`, `category`)
SELECT 'disease', '牙龈炎', 'KQ001', 3, 1, '牙龈发炎', '口腔疾病' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'disease' AND `dict_value` = 'KQ001');

INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`, `remark`, `category`)
SELECT 'disease', '结膜炎', 'YK001', 4, 1, '眼部发炎', '眼科疾病' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'disease' AND `dict_value` = 'YK001');

-- ============================================================
-- 第五步：初始化系统菜单 sys_menu（角色权限管理、动态侧边栏用）
-- 使用 INSERT IGNORE：已有相同 id 则跳过，不会覆盖
-- ============================================================
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_path`, `menu_icon`, `menu_type`, `permission`, `sort`, `status`, `create_time`, `update_time`) VALUES
(1, 0, '首页', '/home', 'HomeFilled', 1, NULL, 1, 1, NOW(), NOW()),
(100, 0, '学生模块', NULL, 'User', 0, NULL, 10, 1, NOW(), NOW()),
(101, 100, '预约挂号', '/student/register', 'Calendar', 1, NULL, 11, 1, NOW(), NOW()),
(102, 100, '在线缴费', '/student/pay', 'Money', 1, NULL, 12, 1, NOW(), NOW()),
(103, 100, '诊疗记录', '/student/record', 'Document', 1, NULL, 13, 1, NOW(), NOW()),
(104, 100, '病假申请', '/student/sickLeave', 'Tickets', 1, NULL, 14, 1, NOW(), NOW()),
(105, 100, '健康档案', '/student/healthRecord', 'Notebook', 1, NULL, 15, 1, NOW(), NOW()),
(200, 0, '医生模块', NULL, 'FirstAidKit', 0, NULL, 20, 1, NOW(), NOW()),
(201, 200, '候诊队列', '/doctor/queue', 'User', 1, NULL, 21, 1, NOW(), NOW()),
(202, 200, '病历书写', '/doctor/medicalRecord', 'Notebook', 1, NULL, 22, 1, NOW(), NOW()),
(203, 200, '处方开具', '/doctor/prescription', 'Reading', 1, NULL, 23, 1, NOW(), NOW()),
(204, 200, '个人排班', '/doctor/schedule', 'Clock', 1, NULL, 24, 1, NOW(), NOW()),
(205, 200, '病假条开具', '/doctor/sickLeave', 'Tickets', 1, NULL, 25, 1, NOW(), NOW()),
(300, 0, '药师模块', NULL, 'Box', 0, NULL, 30, 1, NOW(), NOW()),
(301, 300, '处方配药', '/pharmacist/prescription', 'List', 1, NULL, 31, 1, NOW(), NOW()),
(302, 300, '药品入库', '/pharmacist/drugIn', 'Box', 1, NULL, 32, 1, NOW(), NOW()),
(303, 300, '库存管理', '/pharmacist/stock', 'Warehouse', 1, NULL, 33, 1, NOW(), NOW()),
(400, 0, '审批模块', NULL, 'CircleCheck', 0, NULL, 40, 1, NOW(), NOW()),
(401, 400, '病假审批', '/approver/sickLeave', 'Tickets', 1, NULL, 41, 1, NOW(), NOW()),
(402, 400, '特殊挂号审批', '/approver/specialRegister', 'CircleCheck', 1, NULL, 42, 1, NOW(), NOW()),
(403, 400, '退号审批', '/approver/cancelRegister', 'CloseBold', 1, NULL, 43, 1, NOW(), NOW()),
(500, 0, '财务模块', NULL, 'Money', 0, NULL, 50, 1, NOW(), NOW()),
(501, 500, '收费订单管理', '/finance/order', 'Money', 1, NULL, 51, 1, NOW(), NOW()),
(502, 500, '财务对账', '/finance/reconciliation', 'DataAnalysis', 1, NULL, 52, 1, NOW(), NOW()),
(503, 500, '未缴费催缴', '/finance/reminder', 'Bell', 1, NULL, 53, 1, NOW(), NOW()),
(600, 0, '校领导模块', NULL, 'TrendCharts', 0, NULL, 60, 1, NOW(), NOW()),
(601, 600, '就诊量统计', '/leader/visitStats', 'TrendCharts', 1, NULL, 61, 1, NOW(), NOW()),
(602, 600, '疾病谱分析', '/leader/diseaseStats', 'DataAnalysis', 1, NULL, 62, 1, NOW(), NOW()),
(603, 600, '药品消耗统计', '/leader/drugStats', 'Box', 1, NULL, 63, 1, NOW(), NOW()),
(604, 600, '病假数据统计', '/leader/sickLeaveStats', 'Tickets', 1, NULL, 64, 1, NOW(), NOW()),
(700, 0, '系统管理', NULL, 'Setting', 0, NULL, 70, 1, NOW(), NOW()),
(701, 700, '用户管理', '/admin/user', 'UserFilled', 1, NULL, 71, 1, NOW(), NOW()),
(702, 700, '角色权限管理', '/admin/role', 'Lock', 1, NULL, 72, 1, NOW(), NOW()),
(703, 700, '医生排班管理', '/admin/schedule', 'Calendar', 1, NULL, 73, 1, NOW(), NOW()),
(704, 700, '基础数据管理', '/admin/dictionary', 'Collection', 1, NULL, 74, 1, NOW(), NOW()),
(705, 700, '系统日志', '/admin/log', 'DocumentCopy', 1, NULL, 75, 1, NOW(), NOW()),
(706, 700, '系统配置', '/admin/backup', 'Refresh', 1, NULL, 76, 1, NOW(), NOW());

-- ============================================================
-- 第六步：初始化角色菜单关联 sys_role_menu
-- role_id 对应：1学生 2医生 3药师 4财务 5审批 6校领导 7管理员
-- ============================================================
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`, `create_time`) VALUES
(1, 1, NOW()), (1, 100, NOW()), (1, 101, NOW()), (1, 102, NOW()), (1, 103, NOW()), (1, 104, NOW()), (1, 105, NOW()),
(2, 1, NOW()), (2, 200, NOW()), (2, 201, NOW()), (2, 202, NOW()), (2, 203, NOW()), (2, 204, NOW()), (2, 205, NOW()),
(3, 1, NOW()), (3, 300, NOW()), (3, 301, NOW()), (3, 302, NOW()), (3, 303, NOW()),
(4, 1, NOW()), (4, 500, NOW()), (4, 501, NOW()), (4, 502, NOW()), (4, 503, NOW()),
(5, 1, NOW()), (5, 400, NOW()), (5, 401, NOW()), (5, 402, NOW()), (5, 403, NOW()),
(6, 1, NOW()), (6, 600, NOW()), (6, 601, NOW()), (6, 602, NOW()), (6, 603, NOW()), (6, 604, NOW()),
(7, 1, NOW()), (7, 700, NOW()), (7, 701, NOW()), (7, 702, NOW()), (7, 703, NOW()), (7, 704, NOW()), (7, 705, NOW()), (7, 706, NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 第七步：执行后验证（可选，运行完看结果）
-- ============================================================
SELECT 'sys_menu' AS tbl, COUNT(*) AS cnt FROM sys_menu
UNION ALL
SELECT 'sys_role_menu', COUNT(*) FROM sys_role_menu
UNION ALL
SELECT 'drug_category', COUNT(*) FROM sys_dict_data WHERE dict_type = 'drug_category'
UNION ALL
SELECT 'disease', COUNT(*) FROM sys_dict_data WHERE dict_type = 'disease';

/*
  期望结果：
  sys_menu        = 38
  sys_role_menu   = 44
  drug_category   = 4
  disease         = 4

  执行完毕后请重启后端，各角色重新登录测试。
*/

-- ============================================================
-- 第八步（新增功能）：药品作用说明 + 病历病种属性
-- 若字段已存在会报错，忽略即可
-- ============================================================
ALTER TABLE `medical_medicine`
  ADD COLUMN `description` varchar(500) DEFAULT NULL COMMENT '药品作用说明' AFTER `category`;

ALTER TABLE `medical_record`
  ADD COLUMN `disease_code` varchar(50) DEFAULT NULL COMMENT '病种编码' AFTER `diagnosis`;

ALTER TABLE `medical_record`
  ADD COLUMN `disease_name` varchar(100) DEFAULT NULL COMMENT '病种名称' AFTER `disease_code`;

-- ============================================================
-- 第九步：现有药品归类到标准分类编码并补充作用说明
-- ============================================================
UPDATE `medical_medicine` SET `category`='KSS', `description`='用于敏感菌所致的呼吸道、泌尿生殖道等感染' WHERE `medicine_name`='阿莫西林胶囊';
UPDATE `medical_medicine` SET `category`='KSS', `description`='抗厌氧菌感染，用于口腔、腹腔及妇科感染' WHERE `medicine_name`='甲硝唑片';
UPDATE `medical_medicine` SET `category`='GM', `description`='疏风散寒、解表清热，用于风寒感冒、头痛发热' WHERE `medicine_name`='感冒清热颗粒';
UPDATE `medical_medicine` SET `category`='ZT', `description`='解热镇痛，用于缓解轻至中度疼痛及发热' WHERE `medicine_name`='布洛芬缓释胶囊';
UPDATE `medical_medicine` SET `category`='YW', `description`='活血散瘀、消肿止痛，用于跌打损伤、肌肉酸痛' WHERE `medicine_name`='云南白药气雾剂';

-- ============================================================
-- 第十步：移除已废弃的「药品盘点」菜单（药师模块精简）
-- ============================================================
DELETE FROM `sys_role_menu` WHERE `menu_id` = 304;
DELETE FROM `sys_menu` WHERE `id` = 304;

-- 回填历史入库明细的分类编号（batch_no 字段存分类编码）
UPDATE `medical_instock_item` ii
INNER JOIN `medical_medicine` m ON ii.medicine_id = m.id
SET ii.batch_no = m.batch_no
WHERE (ii.batch_no IS NULL OR ii.batch_no = '') AND m.batch_no IS NOT NULL AND m.batch_no != '';

-- ============================================================
-- 第十一步：精简菜单 + 订单关联病历字段
-- ============================================================
ALTER TABLE `medical_order`
  ADD COLUMN `record_id` bigint DEFAULT NULL COMMENT '关联病历ID' AFTER `prescription_id`;

DELETE FROM `sys_role_menu` WHERE `menu_id` IN (205, 402, 705, 706);
DELETE FROM `sys_menu` WHERE `id` IN (205, 402, 705, 706);
