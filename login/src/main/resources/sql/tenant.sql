-- ----------------------------
-- 租户表结构
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '租户id',
  `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号',
  `contact_user_name` varchar(20) DEFAULT '' COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT '' COMMENT '联系电话',
  `company_name` varchar(50) DEFAULT '' COMMENT '企业名称',
  `license_number` varchar(30) DEFAULT '' COMMENT '统一社会信用代码',
  `address` varchar(200) DEFAULT '' COMMENT '地址',
  `intro` varchar(200) DEFAULT '' COMMENT '企业简介',
  `domain` varchar(200) DEFAULT '' COMMENT '域名',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `package_id` bigint DEFAULT NULL COMMENT '租户套餐编号',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `account_count` int DEFAULT -1 COMMENT '用户数量（-1不限制）',
  `status` char(1) DEFAULT '0' COMMENT '租户状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='租户表';

-- ----------------------------
-- 初始化租户数据
-- ----------------------------
INSERT INTO `sys_tenant` VALUES (1, '000000', '管理组', '15888888888', 'XXX有限公司', NULL, NULL, NULL, NULL, NULL, NULL, NULL, -1, '0', '0', 'admin', sysdate(), '', NULL);

-- ----------------------------
-- 为现有业务表添加租户字段
-- ----------------------------

-- 用户表添加租户字段
ALTER TABLE `sys_user` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `user_id`;
ALTER TABLE `sys_user` ADD INDEX `idx_sys_user_tenant_id` (`tenant_id`);

-- 角色表添加租户字段
ALTER TABLE `sys_role` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `role_id`;
ALTER TABLE `sys_role` ADD INDEX `idx_sys_role_tenant_id` (`tenant_id`);

-- 菜单表添加租户字段
ALTER TABLE `sys_menu` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `menu_id`;
ALTER TABLE `sys_menu` ADD INDEX `idx_sys_menu_tenant_id` (`tenant_id`);

-- 部门表添加租户字段
ALTER TABLE `sys_dept` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `dept_id`;
ALTER TABLE `sys_dept` ADD INDEX `idx_sys_dept_tenant_id` (`tenant_id`);

-- 岗位表添加租户字段
ALTER TABLE `sys_post` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `post_id`;
ALTER TABLE `sys_post` ADD INDEX `idx_sys_post_tenant_id` (`tenant_id`);

-- 用户角色关联表添加租户字段
ALTER TABLE `sys_user_role` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `role_id`;
ALTER TABLE `sys_user_role` ADD INDEX `idx_sys_user_role_tenant_id` (`tenant_id`);

-- 角色菜单关联表添加租户字段
ALTER TABLE `sys_role_menu` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `menu_id`;
ALTER TABLE `sys_role_menu` ADD INDEX `idx_sys_role_menu_tenant_id` (`tenant_id`);

-- 角色部门关联表添加租户字段
ALTER TABLE `sys_role_dept` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `dept_id`;
ALTER TABLE `sys_role_dept` ADD INDEX `idx_sys_role_dept_tenant_id` (`tenant_id`);

-- 用户岗位关联表添加租户字段
ALTER TABLE `sys_user_post` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `post_id`;
ALTER TABLE `sys_user_post` ADD INDEX `idx_sys_user_post_tenant_id` (`tenant_id`);

-- 操作日志表添加租户字段
ALTER TABLE `sys_oper_log` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `oper_id`;
ALTER TABLE `sys_oper_log` ADD INDEX `idx_sys_oper_log_tenant_id` (`tenant_id`);

-- 登录日志表添加租户字段
ALTER TABLE `sys_logininfor` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `info_id`;
ALTER TABLE `sys_logininfor` ADD INDEX `idx_sys_logininfor_tenant_id` (`tenant_id`);

-- 通知公告表添加租户字段
ALTER TABLE `sys_notice` ADD COLUMN `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户编号' AFTER `notice_id`;
ALTER TABLE `sys_notice` ADD INDEX `idx_sys_notice_tenant_id` (`tenant_id`);

-- 更新现有数据的租户ID为默认值
UPDATE `sys_user` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_role` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_menu` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_dept` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_post` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_user_role` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_role_menu` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_role_dept` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_user_post` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_oper_log` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_logininfor` SET `tenant_id` = '000000' WHERE `tenant_id` = '';
UPDATE `sys_notice` SET `tenant_id` = '000000' WHERE `tenant_id` = '';