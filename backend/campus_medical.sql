/*
Navicat MySQL Data Transfer

Source Server         : Mysql57
Source Server Version : 80031
Source Host           : localhost:3306
Source Database       : campus_medical

Target Server Type    : MYSQL
Target Server Version : 80031
File Encoding         : 65001

Date: 2026-06-10 20:22:26
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for medical_appointment
-- ----------------------------
DROP TABLE IF EXISTS `medical_appointment`;
CREATE TABLE `medical_appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `appointment_no` varchar(30) NOT NULL COMMENT '预约编号',
  `user_id` bigint NOT NULL COMMENT '学生用户ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `schedule_id` bigint NOT NULL COMMENT '排班ID',
  `dept_id` bigint NOT NULL COMMENT '科室ID',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `time_slot` varchar(20) NOT NULL COMMENT '预约时段',
  `appointment_type` varchar(10) NOT NULL COMMENT '预约类型(普通/特殊)',
  `status` varchar(20) NOT NULL COMMENT '状态(待确认/已确认/已完成/已取消/待审批)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `appointment_no` (`appointment_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_appointment_date` (`appointment_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预约表';

-- ----------------------------
-- Records of medical_appointment
-- ----------------------------
INSERT INTO `medical_appointment` VALUES ('4', 'AP202606100001', '2', '1', '1', '1', '2026-06-10', '上午08:30-11:30', '普通', '已完成', '2026-06-10 16:43:19', '2026-06-10 16:43:19');
INSERT INTO `medical_appointment` VALUES ('5', 'AP202606100002', '2', '1', '11', '1', '2026-06-15', '上午08:30-11:30', '普通', '待确认', '2026-06-10 16:45:10', '2026-06-10 16:45:10');
INSERT INTO `medical_appointment` VALUES ('6', 'AP202606100003', '2', '1', '12', '1', '2026-06-15', '下午14:30-17:30', '普通', '已取消', '2026-06-10 16:45:19', '2026-06-10 16:45:19');
INSERT INTO `medical_appointment` VALUES ('7', 'AP202606100004', '2', '1', '1', '1', '2026-06-10', '上午08:30-11:30', '普通', '已完成', '2026-06-10 16:47:13', '2026-06-10 16:47:13');
INSERT INTO `medical_appointment` VALUES ('8', 'AP202606100005', '2', '1', '1', '1', '2026-06-10', '上午08:30-11:30', '普通', '已完成', '2026-06-10 16:56:19', '2026-06-10 16:56:19');
INSERT INTO `medical_appointment` VALUES ('9', 'AP202606100006', '2', '1', '2', '1', '2026-06-10', '下午14:30-17:30', '普通', '已完成', '2026-06-10 16:57:36', '2026-06-10 16:57:36');
INSERT INTO `medical_appointment` VALUES ('10', 'AP202606100007', '2', '1', '2', '1', '2026-06-10', '下午14:30-17:30', '普通', '已完成', '2026-06-10 17:19:56', '2026-06-10 17:19:56');
INSERT INTO `medical_appointment` VALUES ('11', 'AP202606100008', '12', '1', '1', '1', '2026-06-10', '上午08:30-11:30', '普通', '已完成', '2026-06-10 17:48:45', '2026-06-10 17:48:45');
INSERT INTO `medical_appointment` VALUES ('12', 'AP202606100009', '12', '1', '1', '1', '2026-06-10', '上午08:30-11:30', '普通', '已取消', '2026-06-10 17:56:44', '2026-06-10 17:56:44');

-- ----------------------------
-- Table structure for medical_approval
-- ----------------------------
DROP TABLE IF EXISTS `medical_approval`;
CREATE TABLE `medical_approval` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审批ID',
  `approval_no` varchar(30) NOT NULL COMMENT '审批编号',
  `approval_type` varchar(20) NOT NULL COMMENT '审批类型(特殊挂号/退号/退费)',
  `business_id` bigint NOT NULL COMMENT '关联业务ID',
  `applicant_id` bigint NOT NULL COMMENT '申请人ID',
  `reason` text COMMENT '申请原因',
  `status` varchar(20) NOT NULL COMMENT '状态(待审核/已通过/已驳回)',
  `auditor_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `audit_content` text COMMENT '审核意见',
  `patient_name` varchar(50) DEFAULT NULL COMMENT '患者姓名（冗余字段）',
  `appointment_no` varchar(30) DEFAULT NULL COMMENT '预约编号（冗余字段）',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退费金额',
  `patient_no` varchar(20) DEFAULT NULL COMMENT '患者学号（冗余字段）',
  `doctor_name` varchar(50) DEFAULT NULL COMMENT '医生姓名（冗余字段）',
  `time_slot` varchar(20) DEFAULT NULL COMMENT '时段（冗余字段）',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话（冗余字段）',
  `dept_name` varchar(50) DEFAULT NULL COMMENT '科室名称（冗余字段）',
  `visit_date` varchar(20) DEFAULT NULL COMMENT '就诊日期（冗余字段）',
  `appointment_id` bigint DEFAULT NULL COMMENT '预约ID（关联业务）',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `approval_no` (`approval_no`),
  KEY `idx_approval_type` (`approval_type`),
  KEY `idx_business_id` (`business_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审批表';

-- ----------------------------
-- Records of medical_approval
-- ----------------------------
INSERT INTO `medical_approval` VALUES ('1', 'RF202606108924', '退费', '1', '2', '????', '待审核', null, null, '测试学生', null, '18.50', 'student', null, null, null, null, null, null, null, '2026-06-10 19:22:38', '2026-06-10 19:22:38');
INSERT INTO `medical_approval` VALUES ('2', 'RF202606106960', '退费', '3', '12', '没钱\n', '待审核', null, null, 'a', null, '185.00', '2026', null, null, null, null, null, null, null, '2026-06-10 19:25:06', '2026-06-10 19:25:06');

-- ----------------------------
-- Table structure for medical_check
-- ----------------------------
DROP TABLE IF EXISTS `medical_check`;
CREATE TABLE `medical_check` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '盘点ID',
  `check_no` varchar(30) NOT NULL COMMENT '盘点单号',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `status` varchar(20) NOT NULL COMMENT '状态(进行中/已完成/已核销)',
  `total_diff_amount` decimal(10,2) DEFAULT '0.00' COMMENT '差异总金额',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `check_no` (`check_no`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='药品盘点表';

-- ----------------------------
-- Records of medical_check
-- ----------------------------

-- ----------------------------
-- Table structure for medical_check_item
-- ----------------------------
DROP TABLE IF EXISTS `medical_check_item`;
CREATE TABLE `medical_check_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `check_id` bigint NOT NULL COMMENT '盘点ID',
  `medicine_id` bigint NOT NULL COMMENT '药品ID',
  `medicine_name` varchar(50) NOT NULL COMMENT '药品名称',
  `spec` varchar(50) NOT NULL COMMENT '药品规格',
  `book_quantity` int NOT NULL COMMENT '账面数量',
  `actual_quantity` int NOT NULL COMMENT '实际数量',
  `diff_quantity` int NOT NULL COMMENT '差异数量',
  `diff_amount` decimal(10,2) NOT NULL COMMENT '差异金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_check_id` (`check_id`),
  KEY `idx_medicine_id` (`medicine_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='药品盘点明细表';

-- ----------------------------
-- Records of medical_check_item
-- ----------------------------

-- ----------------------------
-- Table structure for medical_department
-- ----------------------------
DROP TABLE IF EXISTS `medical_department`;
CREATE TABLE `medical_department` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '科室ID',
  `dept_name` varchar(50) NOT NULL COMMENT '科室名称',
  `dept_code` varchar(50) NOT NULL COMMENT '科室编码',
  `description` varchar(255) DEFAULT NULL COMMENT '科室简介',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dept_code` (`dept_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='科室表';

-- ----------------------------
-- Records of medical_department
-- ----------------------------
INSERT INTO `medical_department` VALUES ('1', '内科', 'internal', '内科诊疗', '1', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `medical_department` VALUES ('2', '外科', 'surgical', '外科诊疗', '2', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `medical_department` VALUES ('3', '口腔科', 'dental', '口腔科诊疗', '3', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `medical_department` VALUES ('4', '眼科', 'ophthalmology', '眼科诊疗', '4', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `medical_department` VALUES ('5', '急诊科', 'emergency', '急诊诊疗', '5', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');

-- ----------------------------
-- Table structure for medical_doctor
-- ----------------------------
DROP TABLE IF EXISTS `medical_doctor`;
CREATE TABLE `medical_doctor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '医生ID',
  `user_id` bigint NOT NULL COMMENT '关联系统用户ID',
  `dept_id` bigint NOT NULL COMMENT '所属科室ID',
  `title` varchar(50) DEFAULT NULL COMMENT '职称',
  `specialty` varchar(255) DEFAULT NULL COMMENT '专业特长',
  `introduction` text COMMENT '医生简介',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='医生表';

-- ----------------------------
-- Records of medical_doctor
-- ----------------------------
INSERT INTO `medical_doctor` VALUES ('1', '3', '1', '主治医师', '内科常见病诊疗', '校医院内科主治医师', '2026-06-10 16:32:22', '2026-06-10 16:32:22');

-- ----------------------------
-- Table structure for medical_health_profile
-- ----------------------------
DROP TABLE IF EXISTS `medical_health_profile`;
CREATE TABLE `medical_health_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `blood_type` varchar(10) DEFAULT NULL COMMENT '血型',
  `allergy` text COMMENT '过敏史',
  `past_history` text COMMENT '既往病史',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='个人健康档案表';

-- ----------------------------
-- Records of medical_health_profile
-- ----------------------------
INSERT INTO `medical_health_profile` VALUES ('1', '2', 'A型', '青霉素过敏', '2025年曾患急性胃炎', '2026-06-10 16:32:22', '2026-06-10 16:32:22');

-- ----------------------------
-- Table structure for medical_instock
-- ----------------------------
DROP TABLE IF EXISTS `medical_instock`;
CREATE TABLE `medical_instock` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '入库ID',
  `instock_no` varchar(30) NOT NULL COMMENT '入库单号',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `instock_no` (`instock_no`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='药品入库表';

-- ----------------------------
-- Records of medical_instock
-- ----------------------------

-- ----------------------------
-- Table structure for medical_instock_item
-- ----------------------------
DROP TABLE IF EXISTS `medical_instock_item`;
CREATE TABLE `medical_instock_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `instock_id` bigint NOT NULL COMMENT '入库ID',
  `medicine_id` bigint NOT NULL COMMENT '药品ID',
  `medicine_name` varchar(50) NOT NULL COMMENT '药品名称',
  `spec` varchar(50) NOT NULL COMMENT '药品规格',
  `quantity` int NOT NULL COMMENT '数量',
  `unit` varchar(10) NOT NULL COMMENT '单位',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `batch_no` varchar(30) DEFAULT NULL COMMENT '批号',
  `expire_date` date DEFAULT NULL COMMENT '有效期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_instock_id` (`instock_id`),
  KEY `idx_medicine_id` (`medicine_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='药品入库明细表';

-- ----------------------------
-- Records of medical_instock_item
-- ----------------------------

-- ----------------------------
-- Table structure for medical_invoice
-- ----------------------------
DROP TABLE IF EXISTS `medical_invoice`;
CREATE TABLE `medical_invoice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '发票ID',
  `invoice_no` varchar(30) NOT NULL COMMENT '发票编号',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '发票金额',
  `invoice_url` varchar(255) DEFAULT NULL COMMENT '发票PDF地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `invoice_no` (`invoice_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='发票表';

-- ----------------------------
-- Records of medical_invoice
-- ----------------------------
INSERT INTO `medical_invoice` VALUES ('1', 'INV202606102325', '3', '12', '185.00', '/invoices/INV202606102325.pdf', '2026-06-10 17:54:32');
INSERT INTO `medical_invoice` VALUES ('2', 'INV202606108917', '1', '2', '18.50', '/invoices/INV202606108917.pdf', '2026-06-10 19:22:38');

-- ----------------------------
-- Table structure for medical_medicine
-- ----------------------------
DROP TABLE IF EXISTS `medical_medicine`;
CREATE TABLE `medical_medicine` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '药品ID',
  `medicine_name` varchar(50) NOT NULL COMMENT '药品名称',
  `spec` varchar(50) NOT NULL COMMENT '药品规格',
  `unit` varchar(10) NOT NULL COMMENT '单位',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `min_stock` int NOT NULL DEFAULT '10' COMMENT '最低库存阈值',
  `category` varchar(20) DEFAULT NULL COMMENT '药品分类',
  `manufacturer` varchar(50) DEFAULT NULL COMMENT '生产厂家',
  `batch_no` varchar(30) DEFAULT NULL COMMENT '批号',
  `expire_date` date DEFAULT NULL COMMENT '有效期',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-禁用,1-正常)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_medicine_name` (`medicine_name`),
  KEY `idx_category` (`category`),
  KEY `idx_expire_date` (`expire_date`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='药品表';

-- ----------------------------
-- Records of medical_medicine
-- ----------------------------
INSERT INTO `medical_medicine` VALUES ('1', '阿莫西林胶囊', '0.5g*24粒', '盒', '35.00', '200', '20', '抗生素', null, null, null, '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_medicine` VALUES ('2', '感冒清热颗粒', '12g*10袋', '盒', '25.75', '150', '15', '中成药', null, null, null, '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_medicine` VALUES ('3', '布洛芬缓释胶囊', '0.3g*20粒', '盒', '30.00', '176', '20', '解热镇痛', null, null, null, '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_medicine` VALUES ('4', '甲硝唑片', '0.2g*21片', '盒', '18.50', '109', '10', '抗生素', null, null, null, '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_medicine` VALUES ('5', '云南白药气雾剂', '85g', '瓶', '42.00', '80', '10', '外用药', null, null, null, '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');

-- ----------------------------
-- Table structure for medical_order
-- ----------------------------
DROP TABLE IF EXISTS `medical_order`;
CREATE TABLE `medical_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(30) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `prescription_id` bigint DEFAULT NULL COMMENT '关联处方ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `pay_type` varchar(20) DEFAULT NULL COMMENT '支付方式(微信/支付宝/校园卡)',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `status` varchar(20) NOT NULL COMMENT '状态(待支付/已支付/已取消/已退费)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- ----------------------------
-- Records of medical_order
-- ----------------------------
INSERT INTO `medical_order` VALUES ('1', 'ORD202606100001', '2', '3', '18.50', 'wechat', '2026-06-10 19:22:39', '已支付', '2026-06-10 17:23:18', '2026-06-10 17:23:18');
INSERT INTO `medical_order` VALUES ('2', 'ORD202606100002', '2', '4', '120.00', null, null, '待支付', '2026-06-10 17:24:26', '2026-06-10 17:24:26');
INSERT INTO `medical_order` VALUES ('3', 'ORD202606100003', '12', '5', '185.00', 'wechat', '2026-06-10 17:54:32', '已支付', '2026-06-10 17:51:19', '2026-06-10 17:51:19');

-- ----------------------------
-- Table structure for medical_order_item
-- ----------------------------
DROP TABLE IF EXISTS `medical_order_item`;
CREATE TABLE `medical_order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `item_name` varchar(100) NOT NULL COMMENT '收费项目名称',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `dept_id` bigint DEFAULT NULL COMMENT '科室ID',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '总价（与amount保持一致）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';

-- ----------------------------
-- Records of medical_order_item
-- ----------------------------

-- ----------------------------
-- Table structure for medical_physical_exam
-- ----------------------------
DROP TABLE IF EXISTS `medical_physical_exam`;
CREATE TABLE `medical_physical_exam` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `exam_date` date NOT NULL COMMENT '体检日期',
  `exam_type` varchar(20) DEFAULT NULL COMMENT '体检类型',
  `report_url` varchar(255) DEFAULT NULL COMMENT '体检报告URL',
  `conclusion` text COMMENT '体检结论',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='体检记录表';

-- ----------------------------
-- Records of medical_physical_exam
-- ----------------------------

-- ----------------------------
-- Table structure for medical_prescription
-- ----------------------------
DROP TABLE IF EXISTS `medical_prescription`;
CREATE TABLE `medical_prescription` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '处方ID',
  `prescription_no` varchar(30) NOT NULL COMMENT '处方编号',
  `record_id` bigint NOT NULL COMMENT '关联病历ID',
  `user_id` bigint NOT NULL COMMENT '患者用户ID',
  `doctor_id` bigint NOT NULL COMMENT '开具医生ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `status` varchar(20) NOT NULL COMMENT '状态(草稿/已提交/待配药/配药中/已完成/已取消)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `prescription_no` (`prescription_no`),
  KEY `idx_record_id` (`record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='处方表';

-- ----------------------------
-- Records of medical_prescription
-- ----------------------------
INSERT INTO `medical_prescription` VALUES ('1', 'RX202606100001', '3', '2', '1', '25.75', '草稿', '2026-06-10 16:56:51', '2026-06-10 16:56:51');
INSERT INTO `medical_prescription` VALUES ('2', 'RX202606100002', '4', '2', '1', '154.50', '草稿', '2026-06-10 17:00:00', '2026-06-10 17:00:00');
INSERT INTO `medical_prescription` VALUES ('3', 'RX202606100003', '2', '2', '1', '18.50', '待配药', '2026-06-10 17:22:08', '2026-06-10 17:22:08');
INSERT INTO `medical_prescription` VALUES ('4', 'RX202606100004', '5', '2', '1', '120.00', '已提交', '2026-06-10 17:24:12', '2026-06-10 17:24:12');
INSERT INTO `medical_prescription` VALUES ('5', 'RX202606100005', '6', '12', '1', '185.00', '已完成', '2026-06-10 17:51:13', '2026-06-10 17:51:13');

-- ----------------------------
-- Table structure for medical_prescription_item
-- ----------------------------
DROP TABLE IF EXISTS `medical_prescription_item`;
CREATE TABLE `medical_prescription_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `prescription_id` bigint NOT NULL COMMENT '处方ID',
  `medicine_id` bigint NOT NULL COMMENT '药品ID',
  `medicine_name` varchar(50) NOT NULL COMMENT '药品名称',
  `spec` varchar(50) NOT NULL COMMENT '药品规格',
  `quantity` int NOT NULL COMMENT '数量',
  `unit` varchar(10) NOT NULL COMMENT '单位',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `usage_method` varchar(100) DEFAULT NULL COMMENT '用法用量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '总价（与amount保持一致）',
  PRIMARY KEY (`id`),
  KEY `idx_prescription_id` (`prescription_id`),
  KEY `idx_medicine_id` (`medicine_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='处方明细表';

-- ----------------------------
-- Records of medical_prescription_item
-- ----------------------------
INSERT INTO `medical_prescription_item` VALUES ('1', '1', '1', 'test', '0.5g*24粒', '1', '盒', '25.75', '25.75', 'oral，tid', '2026-06-10 16:56:51', '25.75');
INSERT INTO `medical_prescription_item` VALUES ('2', '2', '2', '感冒清热颗粒', '12g*10袋', '6', '盒', '25.75', '154.50', '冲剂，一日3次，一日3次', '2026-06-10 17:00:00', '154.50');
INSERT INTO `medical_prescription_item` VALUES ('3', '3', '4', '甲硝唑片', '0.2g*21片', '1', '盒', '18.50', '18.50', '怡，一日3次', '2026-06-10 17:22:08', '18.50');
INSERT INTO `medical_prescription_item` VALUES ('4', '4', '3', '布洛芬缓释胶囊', '0.3g*20粒', '4', '盒', '30.00', '120.00', '口服，一日3次', '2026-06-10 17:24:12', '120.00');
INSERT INTO `medical_prescription_item` VALUES ('5', '5', '4', '甲硝唑片', '0.2g*21片', '10', '盒', '18.50', '185.00', '口服，一日3次', '2026-06-10 17:51:13', '185.00');

-- ----------------------------
-- Table structure for medical_queue
-- ----------------------------
DROP TABLE IF EXISTS `medical_queue`;
CREATE TABLE `medical_queue` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '队列ID',
  `queue_no` varchar(10) NOT NULL COMMENT '排队号',
  `appointment_id` bigint NOT NULL COMMENT '关联预约ID',
  `user_id` bigint NOT NULL COMMENT '学生用户ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `dept_id` bigint NOT NULL COMMENT '科室ID',
  `queue_date` date NOT NULL COMMENT '排队日期',
  `symptom` varchar(255) DEFAULT NULL COMMENT '主要症状',
  `status` varchar(20) NOT NULL COMMENT '状态(候诊中/就诊中/已完成/已过号)',
  `wait_time` int DEFAULT NULL COMMENT '等待时长(分钟)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `appointment_id` (`appointment_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_queue_date` (`queue_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='候诊队列表';

-- ----------------------------
-- Records of medical_queue
-- ----------------------------
INSERT INTO `medical_queue` VALUES ('1', '001', '4', '2', '1', '1', '2026-06-10', '??', '已完成', null, '2026-06-10 16:43:19', '2026-06-10 16:43:19');
INSERT INTO `medical_queue` VALUES ('2', '001', '5', '2', '1', '1', '2026-06-15', '待诊', '候诊中', null, '2026-06-10 16:45:10', '2026-06-10 16:45:10');
INSERT INTO `medical_queue` VALUES ('3', '002', '6', '2', '1', '1', '2026-06-15', '待诊', '已取消', null, '2026-06-10 16:45:19', '2026-06-10 16:45:19');
INSERT INTO `medical_queue` VALUES ('4', '002', '7', '2', '1', '1', '2026-06-10', '咳嗽 流鼻涕', '已完成', null, '2026-06-10 16:47:13', '2026-06-10 16:47:13');
INSERT INTO `medical_queue` VALUES ('5', '003', '8', '2', '1', '1', '2026-06-10', '??', '候诊中', null, '2026-06-10 16:56:19', '2026-06-10 16:56:19');
INSERT INTO `medical_queue` VALUES ('6', '004', '9', '2', '1', '1', '2026-06-10', '待诊', '已完成', null, '2026-06-10 16:57:36', '2026-06-10 16:57:36');
INSERT INTO `medical_queue` VALUES ('7', '005', '10', '2', '1', '1', '2026-06-10', '头疼', '已完成', null, '2026-06-10 17:19:56', '2026-06-10 17:19:56');
INSERT INTO `medical_queue` VALUES ('8', '006', '11', '12', '1', '1', '2026-06-10', '心脏时不时刺痛', '已完成', null, '2026-06-10 17:48:45', '2026-06-10 17:48:45');
INSERT INTO `medical_queue` VALUES ('9', '007', '12', '12', '1', '1', '2026-06-10', '待诊', '已取消', null, '2026-06-10 17:56:44', '2026-06-10 17:56:44');

-- ----------------------------
-- Table structure for medical_record
-- ----------------------------
DROP TABLE IF EXISTS `medical_record`;
CREATE TABLE `medical_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '病历ID',
  `record_no` varchar(30) NOT NULL COMMENT '病历编号',
  `user_id` bigint NOT NULL COMMENT '患者用户ID',
  `doctor_id` bigint NOT NULL COMMENT '接诊医生ID',
  `dept_id` bigint NOT NULL COMMENT '科室ID',
  `appointment_id` bigint DEFAULT NULL COMMENT '关联预约ID',
  `chief_complaint` text NOT NULL COMMENT '主诉',
  `present_illness` text COMMENT '现病史',
  `past_history` text COMMENT '既往史',
  `physical_examination` text COMMENT '体格检查',
  `diagnosis` varchar(255) NOT NULL COMMENT '诊断结果',
  `advice` text COMMENT '医嘱',
  `college` varchar(100) DEFAULT NULL COMMENT '学院（冗余字段）',
  `dept_name` varchar(50) DEFAULT NULL COMMENT '科室名称（冗余字段）',
  `patient_id` bigint DEFAULT NULL COMMENT '患者ID（冗余字段，与userId保持一致）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `record_no` (`record_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='电子病历表';

-- ----------------------------
-- Records of medical_record
-- ----------------------------
INSERT INTO `medical_record` VALUES ('1', 'MR202606100001', '2', '1', '1', '4', '咳嗽 流鼻涕 ；两天', '吹风', '无', '正常', '感冒', '多休息', null, null, null, '2026-06-10 16:44:39', '2026-06-10 16:44:39');
INSERT INTO `medical_record` VALUES ('2', 'MR202606100002', '2', '1', '1', '7', '咳嗽，流鼻涕', '吹风', '无', '正常', '感冒', '注意休息', null, null, null, '2026-06-10 16:48:21', '2026-06-10 16:48:21');
INSERT INTO `medical_record` VALUES ('3', 'MR202606100003', '2', '1', '1', '8', '??', '1?', '?', '??', '??', '??', null, null, null, '2026-06-10 16:56:19', '2026-06-10 16:56:19');
INSERT INTO `medical_record` VALUES ('4', 'MR202606100004', '2', '1', '1', '9', 'cough', '1d', 'none', 'normal', 'cold', 'rest', null, null, null, '2026-06-10 16:59:59', '2026-06-10 16:59:59');
INSERT INTO `medical_record` VALUES ('5', 'MR202606100005', '2', '1', '1', '10', '人特别', '他个人', '', '给他人', '他个人', '', null, null, null, '2026-06-10 17:24:12', '2026-06-10 17:24:12');
INSERT INTO `medical_record` VALUES ('6', 'MR202606100006', '12', '1', '1', '11', '心脏不舒服', '长期熬夜', '', '心电图', '心率过快', '', null, null, null, '2026-06-10 17:51:13', '2026-06-10 17:51:13');

-- ----------------------------
-- Table structure for medical_schedule
-- ----------------------------
DROP TABLE IF EXISTS `medical_schedule`;
CREATE TABLE `medical_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `dept_id` bigint NOT NULL COMMENT '科室ID',
  `schedule_date` date NOT NULL COMMENT '排班日期',
  `time_slot` varchar(20) NOT NULL COMMENT '出诊时段(08:00-12:00/14:00-17:30)',
  `max_num` int NOT NULL COMMENT '最大号源数',
  `remain_num` int NOT NULL COMMENT '剩余号源数',
  `schedule_type` varchar(10) NOT NULL COMMENT '号源类型(普通/专家/急诊)',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-取消,1-正常)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_doctor_date_slot` (`doctor_id`,`schedule_date`,`time_slot`),
  KEY `idx_schedule_date` (`schedule_date`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='医生排班表';

-- ----------------------------
-- Records of medical_schedule
-- ----------------------------
INSERT INTO `medical_schedule` VALUES ('1', '1', '1', '2026-06-10', '上午08:30-11:30', '20', '16', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('2', '1', '1', '2026-06-10', '下午14:30-17:30', '20', '18', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('3', '1', '1', '2026-06-11', '上午08:30-11:30', '12', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 17:51:45');
INSERT INTO `medical_schedule` VALUES ('4', '1', '1', '2026-06-11', '下午14:30-17:30', '20', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('5', '1', '1', '2026-06-12', '上午08:30-11:30', '20', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('6', '1', '1', '2026-06-12', '下午14:30-17:30', '20', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('7', '1', '1', '2026-06-13', '上午08:30-11:30', '20', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('8', '1', '1', '2026-06-13', '下午14:30-17:30', '20', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('9', '1', '1', '2026-06-14', '上午08:30-11:30', '20', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('10', '1', '1', '2026-06-14', '下午14:30-17:30', '20', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('11', '1', '1', '2026-06-15', '上午08:30-11:30', '20', '19', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('12', '1', '1', '2026-06-15', '下午14:30-17:30', '20', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('13', '1', '1', '2026-06-16', '上午08:30-11:30', '20', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `medical_schedule` VALUES ('14', '1', '1', '2026-06-16', '下午14:30-17:30', '20', '20', '普通', '1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');

-- ----------------------------
-- Table structure for medical_sick_leave
-- ----------------------------
DROP TABLE IF EXISTS `medical_sick_leave`;
CREATE TABLE `medical_sick_leave` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '病假申请ID',
  `leave_no` varchar(30) NOT NULL COMMENT '病假申请编号',
  `user_id` bigint NOT NULL COMMENT '学生用户ID',
  `doctor_id` bigint NOT NULL COMMENT '开具医生ID',
  `sick_note_id` bigint NOT NULL COMMENT '关联病假条ID',
  `diagnosis` varchar(255) NOT NULL COMMENT '诊断结果',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `leave_days` int NOT NULL COMMENT '请假天数',
  `diagnosis_proof_url` varchar(255) DEFAULT NULL COMMENT '诊断证明URL',
  `status` int NOT NULL COMMENT '状态(待审核/已通过/已驳回/已撤回)',
  `auditor_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `audit_content` text COMMENT '审核意见',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `leave_no` (`leave_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='电子病假申请表';

-- ----------------------------
-- Records of medical_sick_leave
-- ----------------------------
INSERT INTO `medical_sick_leave` VALUES ('1', 'SL202606105406', '12', '0', '0', '心率过快', '2026-06-10', '2026-06-11', '2', null, '2', '6', '无', null, '2026-06-10 17:55:45', '2026-06-10 17:56:08');

-- ----------------------------
-- Table structure for medical_sick_note
-- ----------------------------
DROP TABLE IF EXISTS `medical_sick_note`;
CREATE TABLE `medical_sick_note` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '病假条ID',
  `sick_note_no` varchar(30) NOT NULL COMMENT '病假条编号',
  `record_id` bigint NOT NULL COMMENT '关联病历ID',
  `user_id` bigint NOT NULL COMMENT '患者用户ID',
  `doctor_id` bigint NOT NULL COMMENT '开具医生ID',
  `diagnosis` varchar(255) NOT NULL COMMENT '诊断结果',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `leave_days` int NOT NULL COMMENT '请假天数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sick_note_no` (`sick_note_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_doctor_id` (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='电子病假条表';

-- ----------------------------
-- Records of medical_sick_note
-- ----------------------------

-- ----------------------------
-- Table structure for medical_template
-- ----------------------------
DROP TABLE IF EXISTS `medical_template`;
CREATE TABLE `medical_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(50) NOT NULL COMMENT '模板名称',
  `template_content` text NOT NULL COMMENT '模板内容',
  `template_type` varchar(20) DEFAULT NULL COMMENT '模板类型(发热/腹痛/外伤等)',
  `doctor_id` bigint DEFAULT NULL COMMENT '创建医生ID(0表示系统通用模板)',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_template_type` (`template_type`),
  KEY `idx_doctor_id` (`doctor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='病历模板表';

-- ----------------------------
-- Records of medical_template
-- ----------------------------
INSERT INTO `medical_template` VALUES ('1', '发热', '发热伴咳嗽、咳痰X天', 'common', '0', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `medical_template` VALUES ('2', '腹痛', '上腹部疼痛X小时，伴恶心、呕吐', 'common', '0', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `medical_template` VALUES ('3', '外伤', '外伤致X部位疼痛、肿胀X小时', 'common', '0', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `medical_template` VALUES ('4', '感冒', '鼻塞、流涕、咽痛X天，伴全身酸痛', 'common', '0', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');

-- ----------------------------
-- Table structure for medical_vaccine
-- ----------------------------
DROP TABLE IF EXISTS `medical_vaccine`;
CREATE TABLE `medical_vaccine` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `vaccine_name` varchar(50) NOT NULL COMMENT '疫苗名称',
  `vaccine_date` date NOT NULL COMMENT '接种日期',
  `dose` varchar(20) DEFAULT NULL COMMENT '剂次',
  `manufacturer` varchar(50) DEFAULT NULL COMMENT '生产厂家',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='疫苗接种记录表';

-- ----------------------------
-- Records of medical_vaccine
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` varchar(255) NOT NULL COMMENT '配置值',
  `config_name` varchar(100) DEFAULT NULL COMMENT '配置名称',
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('1', 'max_appointment_num', '20', '每日最大预约号源', '每个医生每日最大可预约号源数量', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_config` VALUES ('2', 'login_timeout', '1800', '登录超时时间', '用户登录会话超时时间(秒)', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_config` VALUES ('3', 'file_max_size', '10', '文件上传最大大小', '上传文件最大大小(MB)', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_config` VALUES ('4', 'refund_deadline', '24', '退号截止时间', '就诊前多少小时可申请退号(小时)', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_config` VALUES ('5', 'warn_threshold', '10', '健康预警阈值', '单日同类疾病超过此数量触发预警', '2026-06-04 08:40:09', '2026-06-04 08:40:09');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型编码',
  `dict_label` varchar(100) NOT NULL COMMENT '字典标签',
  `dict_value` varchar(100) NOT NULL COMMENT '字典值',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-禁用,1-启用)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注描述',
  `category` varchar(100) DEFAULT NULL COMMENT '所属分类',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据字典数据表';

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES ('1', 'gender', '男', '1', '1', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('2', 'gender', '女', '2', '2', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('3', 'gender', '未知', '0', '3', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('4', 'user_role', '学生', 'student', '1', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('5', 'user_role', '医生', 'doctor', '2', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('6', 'user_role', '药师', 'pharmacist', '3', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('7', 'user_role', '财务人员', 'finance', '4', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('8', 'user_role', '审批管理员', 'approver', '5', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('9', 'user_role', '校领导', 'leader', '6', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('10', 'user_role', '系统管理员', 'admin', '7', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('11', 'sick_type', '感冒', 'cold', '1', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('12', 'sick_type', '发烧', 'fever', '2', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('13', 'sick_type', '肠胃炎', 'gastroenteritis', '3', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('14', 'sick_type', '外伤', 'injury', '4', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('15', 'appointment_status', '待确认', 'pending', '1', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('16', 'appointment_status', '已确认', 'confirmed', '2', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('17', 'appointment_status', '已完成', 'completed', '3', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('18', 'appointment_status', '已取消', 'cancelled', '4', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('19', 'appointment_status', '待审批', 'approving', '5', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('20', 'order_status', '待支付', 'unpaid', '1', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('21', 'order_status', '已支付', 'paid', '2', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('22', 'order_status', '已取消', 'cancelled', '3', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('23', 'order_status', '已退费', 'refunded', '4', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('24', 'leave_status', '待审核', 'pending', '1', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('25', 'leave_status', '已通过', 'approved', '2', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('26', 'leave_status', '已驳回', 'rejected', '3', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('27', 'leave_status', '已撤回', 'withdrawn', '4', '1', NULL, NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('28', 'drug_category', '抗生素类', 'KSS', '1', '1', '抗生素类药品', NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('29', 'drug_category', '感冒用药', 'GM', '2', '1', '感冒类药品', NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('30', 'drug_category', '止痛药', 'ZT', '3', '1', '止痛类药品', NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('31', 'drug_category', '外用药', 'YW', '4', '1', '外用类药品', NULL, '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('32', 'disease', '急性上呼吸道感染', 'NK001', '1', '1', '常见感冒症状', '内科疾病', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('33', 'disease', '软组织损伤', 'WK001', '2', '1', '扭伤、拉伤等', '外科疾病', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('34', 'disease', '牙龈炎', 'KQ001', '3', '1', '牙龈发炎', '口腔疾病', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_data` VALUES ('35', 'disease', '结膜炎', 'YK001', '4', '1', '眼部发炎', '眼科疾病', '2026-06-04 08:40:09', '2026-06-04 08:40:09');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型编码',
  `dict_name` varchar(100) NOT NULL COMMENT '字典类型名称',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES ('1', 'gender', '性别', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_type` VALUES ('2', 'user_role', '用户角色', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_type` VALUES ('3', 'sick_type', '疾病类型', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_type` VALUES ('4', 'appointment_status', '预约状态', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_type` VALUES ('5', 'order_status', '订单状态', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_type` VALUES ('6', 'leave_status', '病假状态', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_type` VALUES ('7', 'drug_category', '药品分类', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_dict_type` VALUES ('8', 'disease', '病种管理', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `login_ip` varchar(50) DEFAULT NULL COMMENT '登录IP',
  `login_location` varchar(255) DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(50) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(50) DEFAULT NULL COMMENT '操作系统',
  `status` tinyint DEFAULT NULL COMMENT '登录状态(0-成功,1-失败)',
  `msg` varchar(255) DEFAULT NULL COMMENT '提示信息',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统登录日志表';

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `menu_path` varchar(255) DEFAULT NULL COMMENT '菜单路径',
  `menu_icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `menu_type` tinyint DEFAULT NULL COMMENT '菜单类型(0-目录,1-菜单,2-按钮)',
  `permission` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '0', '首页', '/home', 'HomeFilled', '1', NULL, '1', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('100', '0', '学生模块', NULL, 'User', '0', NULL, '10', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('101', '100', '预约挂号', '/student/register', 'Calendar', '1', NULL, '11', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('102', '100', '在线缴费', '/student/pay', 'Money', '1', NULL, '12', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('103', '100', '诊疗记录', '/student/record', 'Document', '1', NULL, '13', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('104', '100', '病假申请', '/student/sickLeave', 'Tickets', '1', NULL, '14', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('105', '100', '健康档案', '/student/healthRecord', 'Notebook', '1', NULL, '15', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('200', '0', '医生模块', NULL, 'FirstAidKit', '0', NULL, '20', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('201', '200', '候诊队列', '/doctor/queue', 'User', '1', NULL, '21', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('202', '200', '病历书写', '/doctor/medicalRecord', 'Notebook', '1', NULL, '22', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('203', '200', '处方开具', '/doctor/prescription', 'Reading', '1', NULL, '23', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('204', '200', '个人排班', '/doctor/schedule', 'Clock', '1', NULL, '24', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('205', '200', '病假条开具', '/doctor/sickLeave', 'Tickets', '1', NULL, '25', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('300', '0', '药师模块', NULL, 'Box', '0', NULL, '30', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('301', '300', '处方配药', '/pharmacist/prescription', 'List', '1', NULL, '31', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('302', '300', '药品入库', '/pharmacist/drugIn', 'Box', '1', NULL, '32', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('303', '300', '库存管理', '/pharmacist/stock', 'Warehouse', '1', NULL, '33', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('304', '300', '药品盘点', '/pharmacist/check', 'Checked', '1', NULL, '34', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('400', '0', '审批模块', NULL, 'CircleCheck', '0', NULL, '40', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('401', '400', '病假审批', '/approver/sickLeave', 'Tickets', '1', NULL, '41', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('402', '400', '特殊挂号审批', '/approver/specialRegister', 'CircleCheck', '1', NULL, '42', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('403', '400', '退号审批', '/approver/cancelRegister', 'CloseBold', '1', NULL, '43', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('500', '0', '财务模块', NULL, 'Money', '0', NULL, '50', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('501', '500', '收费订单管理', '/finance/order', 'Money', '1', NULL, '51', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('502', '500', '财务对账', '/finance/reconciliation', 'DataAnalysis', '1', NULL, '52', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('503', '500', '未缴费催缴', '/finance/reminder', 'Bell', '1', NULL, '53', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('600', '0', '校领导模块', NULL, 'TrendCharts', '0', NULL, '60', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('601', '600', '就诊量统计', '/leader/visitStats', 'TrendCharts', '1', NULL, '61', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('602', '600', '疾病谱分析', '/leader/diseaseStats', 'DataAnalysis', '1', NULL, '62', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('603', '600', '药品消耗统计', '/leader/drugStats', 'Box', '1', NULL, '63', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('604', '600', '病假数据统计', '/leader/sickLeaveStats', 'Tickets', '1', NULL, '64', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('700', '0', '系统管理', NULL, 'Setting', '0', NULL, '70', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('701', '700', '用户管理', '/admin/user', 'UserFilled', '1', NULL, '71', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('702', '700', '角色权限管理', '/admin/role', 'Lock', '1', NULL, '72', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('703', '700', '医生排班管理', '/admin/schedule', 'Calendar', '1', NULL, '73', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('704', '700', '基础数据管理', '/admin/dictionary', 'Collection', '1', NULL, '74', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('705', '700', '系统日志', '/admin/log', 'DocumentCopy', '1', NULL, '75', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_menu` VALUES ('706', '700', '系统配置', '/admin/backup', 'Refresh', '1', NULL, '76', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `title` varchar(100) DEFAULT NULL COMMENT '操作模块',
  `business_type` tinyint DEFAULT NULL COMMENT '业务类型(0-新增,1-修改,2-删除,3-查询,4-导出,5-导入)',
  `method` varchar(255) DEFAULT NULL COMMENT '请求方法',
  `request_url` varchar(255) DEFAULT NULL COMMENT '请求URL',
  `request_param` text COMMENT '请求参数',
  `response_result` text COMMENT '响应结果',
  `oper_user_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `oper_user_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `oper_ip` varchar(50) DEFAULT NULL COMMENT '操作IP',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `status` tinyint DEFAULT NULL COMMENT '操作状态(0-成功,1-失败)',
  `error_msg` text COMMENT '错误信息',
  PRIMARY KEY (`id`),
  KEY `idx_oper_user_id` (`oper_user_id`),
  KEY `idx_oper_time` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统操作日志表';

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '学生', 'student', '普通学生用户', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_role` VALUES ('2', '医生', 'doctor', '校医/医生用户', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_role` VALUES ('3', '药师', 'pharmacist', '药房管理员', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_role` VALUES ('4', '财务人员', 'finance', '财务管理人员', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_role` VALUES ('5', '审批管理员', 'approver', '审批管理人员', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_role` VALUES ('6', '校领导', 'leader', '校领导/管理人员', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_role` VALUES ('7', '系统管理员', 'admin', '系统超级管理员', '1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1', '1', '1', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('2', '1', '100', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('3', '1', '101', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('4', '1', '102', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('5', '1', '103', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('6', '1', '104', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('7', '1', '105', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('8', '2', '1', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('9', '2', '200', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('10', '2', '201', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('11', '2', '202', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('12', '2', '203', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('13', '2', '204', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('14', '2', '205', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('15', '3', '1', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('16', '3', '300', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('17', '3', '301', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('18', '3', '302', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('19', '3', '303', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('20', '3', '304', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('21', '4', '1', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('22', '4', '500', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('23', '4', '501', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('24', '4', '502', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('25', '4', '503', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('26', '5', '1', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('27', '5', '400', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('28', '5', '401', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('29', '5', '402', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('30', '5', '403', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('31', '6', '1', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('32', '6', '600', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('33', '6', '601', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('34', '6', '602', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('35', '6', '603', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('36', '6', '604', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('37', '7', '1', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('38', '7', '700', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('39', '7', '701', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('40', '7', '702', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('41', '7', '703', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('42', '7', '704', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('43', '7', '705', '2026-06-04 08:40:09');
INSERT INTO `sys_role_menu` VALUES ('44', '7', '706', '2026-06-04 08:40:09');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名(学号/工号)',
  `password` varchar(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `department` varchar(100) DEFAULT NULL COMMENT '所属部门/学院',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级(学生专用)',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint DEFAULT '1' COMMENT '状态(0-禁用,1-启用)',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'admin', '$2a$10$zhY/hTswjTPVbcJr2f4hKe7xMEF38UfK.wfj0aDSCSIJUjfXd3hSm', '系统管理员', '7', '信息中心', '信息中心', '13800138003', 'admin@campus.edu', null, '1', '2026-06-10 20:12:38', '127.0.0.1', '2026-06-04 08:40:09', '2026-06-04 08:40:09');
INSERT INTO `sys_user` VALUES ('2', 'student', '$2a$10$sxNuJklPK7GNaRANkDthVOr9L3vVoQvrzwSkgUffIzV6ARajpFd5u', '测试学生', '1', '计算机学院', '计算机202班', '13800138001', null, null, '1', '2026-06-10 19:22:39', '127.0.0.1', '2026-06-05 00:33:16', '2026-06-05 00:33:16');
INSERT INTO `sys_user` VALUES ('3', 'doctor', '$2a$10$MSWjz2/8Ogz9ctdYukocquMNODOQUtZ8CGIzaXFLQEpOjHrNXTBVu', '王医生', '2', '内科', '内科', '13800138002', null, null, '1', '2026-06-10 17:55:09', '127.0.0.1', '2026-06-05 00:33:16', '2026-06-05 00:33:16');
INSERT INTO `sys_user` VALUES ('4', 'pharmacist', '$2a$10$sNWZ66zasUErV8fEn2B0aeb5O5RGZF5QjA0H7spN1Uv8dLcWH.RFu', '李药师', '3', '药房', '药房', '13800138004', null, null, '1', '2026-06-10 19:50:48', '127.0.0.1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `sys_user` VALUES ('5', 'finance', '$2a$10$BC7d9JndWLxd15dDsMSkyO5xjXbecVk4h.7VCyqIKkWdoIf.4ncL2', '张财务', '4', '财务科', '财务科', '13800138005', null, null, '1', '2026-06-10 20:06:49', '127.0.0.1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `sys_user` VALUES ('6', 'approver', '$2a$10$rMiFFo32JMUIXCrycroI5u5qtfMrs4EJvInOfWPP5Di768qmZowwG', '刘审批', '5', '学生处', '学生处', '13800138006', null, null, '1', '2026-06-10 19:50:41', '127.0.0.1', '2026-06-10 16:32:22', '2026-06-10 16:32:22');
INSERT INTO `sys_user` VALUES ('7', 'leader', '$2a$10$gaP9fekPNoKDsuoDxD3v6uj3pwUPVymuFtBP6t9is5e71OPYX6Yby', '陈校长', '6', '校办', '校办', '13800138007', 'fgd@qq.com', null, '1', '2026-06-10 20:07:16', '127.0.0.1', '2026-06-10 16:32:23', '2026-06-10 17:26:50');
INSERT INTO `sys_user` VALUES ('9', '2023', 'f66321ec3fee006a9f6fa10d2aa88929', 'zz', '1', 'finival', null, '13623576256', 'fsdfs@qq.com', null, '1', '2026-06-10 17:37:12', '127.0.0.1', '2026-06-10 17:26:23', '2026-06-10 17:29:08');
INSERT INTO `sys_user` VALUES ('10', 's1', '$2a$10$YdXrit3et9pkpMgt9y9TleIjg1Jy6k.JiS9YS462Gyxgnou600Xoi', 'ab', '1', 'dhgs', null, '1453545465', 'fsdfs@qq.com', null, '1', '2026-06-10 19:15:13', '127.0.0.1', '2026-06-10 17:29:38', '2026-06-10 17:39:47');
INSERT INTO `sys_user` VALUES ('11', 'teststu01', '$2a$10$v0FrbpqW9fWSk4KsUjgSoOZmvgHwHPWnFpHsmwzURW/ji5JO94dAe', '????', '1', '????', null, '13900000001', 'test01@test.com', null, '1', '2026-06-10 17:39:15', '127.0.0.1', '2026-06-10 17:39:15', '2026-06-10 17:39:15');
INSERT INTO `sys_user` VALUES ('12', '2026', '$2a$10$QPRGz8YXS8HoMQp51gCImeUxHBwZL8I9dZn6y4zptsoDqsZE3FGjC', 'a', '1', 'financal', null, '1433545456', 'fsdfs@qq.com', null, '1', '2026-06-10 17:56:28', '127.0.0.1', '2026-06-10 17:46:19', '2026-06-10 17:46:19');
INSERT INTO `sys_user` VALUES ('13', '1', '$2a$10$zs4OwLrt9c/5NJFsYQmdRuD6sPUWBDci1CDVqBRDqauHv335w/INm', '徐医生', '2', '内科', null, '1563453453', '322@qq.com', null, '1', '2026-06-10 20:11:33', '127.0.0.1', '2026-06-10 20:09:54', '2026-06-10 20:09:54');
