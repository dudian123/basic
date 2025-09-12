-- ----------------------------
-- User Information Table
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT 'Tenant ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT 'Department ID',
  `user_name` varchar(30) NOT NULL COMMENT 'Username',
  `nick_name` varchar(30) NOT NULL COMMENT 'Nickname',
  `user_type` varchar(10) DEFAULT 'sys_user' COMMENT 'User Type',
  `email` varchar(50) DEFAULT '' COMMENT 'Email',
  `phonenumber` varchar(11) DEFAULT '' COMMENT 'Phone Number',
  `sex` char(1) DEFAULT '0' COMMENT 'Gender 0-Male 1-Female 2-Unknown',
  `avatar` varchar(100) DEFAULT '' COMMENT 'Avatar',
  `password` varchar(100) DEFAULT '' COMMENT 'Password',
  `status` char(1) DEFAULT '0' COMMENT 'Status 0-Normal 1-Disabled',
  `del_flag` char(1) DEFAULT '0' COMMENT 'Delete Flag 0-Exist 2-Deleted',
  `login_ip` varchar(128) DEFAULT '' COMMENT 'Last Login IP',
  `login_date` datetime DEFAULT NULL COMMENT 'Last Login Date',
  `create_by` varchar(64) DEFAULT '' COMMENT 'Creator',
  `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) DEFAULT '' COMMENT 'Updater',
  `update_time` datetime DEFAULT NULL COMMENT 'Update Time',
  `remark` varchar(500) DEFAULT NULL COMMENT 'Remark',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `tenant_id` (`tenant_id`,`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='User Information Table';

-- ----------------------------
-- Role Information Table
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Role ID',
  `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT 'Tenant ID',
  `role_name` varchar(30) NOT NULL COMMENT 'Role Name',
  `role_key` varchar(100) NOT NULL COMMENT 'Role Key',
  `role_sort` int(4) NOT NULL COMMENT 'Sort Order',
  `data_scope` char(1) DEFAULT '1' COMMENT 'Data Scope',
  `menu_check_strictly` tinyint(1) DEFAULT 1 COMMENT 'Menu Check Strictly',
  `dept_check_strictly` tinyint(1) DEFAULT 1 COMMENT 'Dept Check Strictly',
  `status` char(1) NOT NULL COMMENT 'Status 0-Normal 1-Disabled',
  `del_flag` char(1) DEFAULT '0' COMMENT 'Delete Flag',
  `create_by` varchar(64) DEFAULT '' COMMENT 'Creator',
  `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) DEFAULT '' COMMENT 'Updater',
  `update_time` datetime DEFAULT NULL COMMENT 'Update Time',
  `remark` varchar(500) DEFAULT NULL COMMENT 'Remark',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `tenant_id` (`tenant_id`,`role_key`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='Role Information Table';

-- ----------------------------
-- Menu Permission Table
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Menu ID',
  `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT 'Tenant ID',
  `menu_name` varchar(50) NOT NULL COMMENT 'Menu Name',
  `parent_id` bigint(20) DEFAULT 0 COMMENT 'Parent Menu ID',
  `order_num` int(4) DEFAULT 0 COMMENT 'Order Number',
  `path` varchar(200) DEFAULT '' COMMENT 'Route Path',
  `component` varchar(255) DEFAULT NULL COMMENT 'Component Path',
  `query_param` varchar(255) DEFAULT NULL COMMENT 'Query Parameters',
  `is_frame` int(1) DEFAULT 1 COMMENT 'Is External Link',
  `is_cache` int(1) DEFAULT 0 COMMENT 'Is Cache',
  `menu_type` char(1) DEFAULT '' COMMENT 'Menu Type M-Directory C-Menu F-Button',
  `visible` char(1) DEFAULT 0 COMMENT 'Visible Status',
  `status` char(1) DEFAULT 0 COMMENT 'Menu Status',
  `perms` varchar(100) DEFAULT NULL COMMENT 'Permission String',
  `icon` varchar(100) DEFAULT '#' COMMENT 'Menu Icon',
  `create_by` varchar(64) DEFAULT '' COMMENT 'Creator',
  `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) DEFAULT '' COMMENT 'Updater',
  `update_time` datetime DEFAULT NULL COMMENT 'Update Time',
  `remark` varchar(500) DEFAULT '' COMMENT 'Remark',
  PRIMARY KEY (`menu_id`),
  KEY `idx_sys_menu_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2000 DEFAULT CHARSET=utf8mb4 COMMENT='Menu Permission Table';

-- ----------------------------
-- User Role Association Table
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT 'User ID',
  `role_id` bigint(20) NOT NULL COMMENT 'Role ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User Role Association Table';

-- ----------------------------
-- Role Menu Association Table
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT 'Role ID',
  `menu_id` bigint(20) NOT NULL COMMENT 'Menu ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role Menu Association Table';

-- ----------------------------
-- Department Table
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Department ID',
  `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT 'Tenant ID',
  `parent_id` bigint(20) DEFAULT 0 COMMENT 'Parent Department ID',
  `ancestors` varchar(50) DEFAULT '' COMMENT 'Ancestors',
  `dept_name` varchar(30) DEFAULT '' COMMENT 'Department Name',
  `order_num` int(4) DEFAULT 0 COMMENT 'Order Number',
  `leader` varchar(20) DEFAULT NULL COMMENT 'Leader',
  `phone` varchar(11) DEFAULT NULL COMMENT 'Phone',
  `email` varchar(50) DEFAULT NULL COMMENT 'Email',
  `status` char(1) DEFAULT '0' COMMENT 'Status',
  `del_flag` char(1) DEFAULT '0' COMMENT 'Delete Flag',
  `create_by` varchar(64) DEFAULT '' COMMENT 'Creator',
  `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) DEFAULT '' COMMENT 'Updater',
  `update_time` datetime DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`dept_id`),
  KEY `idx_sys_dept_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COMMENT='Department Table';

-- ----------------------------
-- Initialize Department Data
-- ----------------------------
INSERT INTO `sys_dept` VALUES(100, '000000', 0, '0', 'Head Office', 0, 'Administrator', '15888888888', 'admin@example.com', '0', '0', 'admin', now(), '', null);
INSERT INTO `sys_dept` VALUES(101, '000000', 100, '0,100', 'R&D Department', 1, 'R&D Manager', '15888888888', 'dev@example.com', '0', '0', 'admin', now(), '', null);
INSERT INTO `sys_dept` VALUES(102, '000000', 100, '0,100', 'Marketing Department', 2, 'Marketing Manager', '15888888888', 'market@example.com', '0', '0', 'admin', now(), '', null);

-- ----------------------------
-- Initialize Role Data
-- ----------------------------
INSERT INTO `sys_role` VALUES(1, '000000', 'Super Administrator', 'admin', 1, '1', 1, 1, '0', '0', 'admin', now(), '', null, 'Super Administrator');
INSERT INTO `sys_role` VALUES(2, '000000', 'Common User', 'common', 2, '2', 1, 1, '0', '0', 'admin', now(), '', null, 'Common User');

-- ----------------------------
-- Initialize User Data
-- ----------------------------
INSERT INTO `sys_user` VALUES(1, '000000', 100, 'admin', 'Administrator', 'sys_user', 'admin@example.com', '15888888888', '1', '', '$2a$10$7JB720yubVSOfvVMe6/b/.b4OXhF.ktMfqH9z.jx.jjgs/9cDgCa2', '0', '0', '127.0.0.1', now(), 'admin', now(), '', null, 'Administrator');
INSERT INTO `sys_user` VALUES(2, '000000', 101, 'user', 'Common User', 'sys_user', 'user@example.com', '15666666666', '1', '', '$2a$10$7JB720yubVSOfvVMe6/b/.b4OXhF.ktMfqH9z.jx.jjgs/9cDgCa2', '0', '0', '127.0.0.1', now(), 'admin', now(), '', null, 'Common User');

-- ----------------------------
-- Initialize User Role Association Data
-- ----------------------------
INSERT INTO `sys_user_role` VALUES(1, 1);
INSERT INTO `sys_user_role` VALUES(2, 2);

-- ----------------------------
-- Initialize Menu Data
-- ----------------------------
INSERT INTO `sys_menu` VALUES(1, '000000', 'System Management', 0, 1, 'system', null, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', now(), '', null, 'System Management Directory');
INSERT INTO `sys_menu` VALUES(100, '000000', 'User Management', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', now(), '', null, 'User Management Menu');
INSERT INTO `sys_menu` VALUES(101, '000000', 'Role Management', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', now(), '', null, 'Role Management Menu');
INSERT INTO `sys_menu` VALUES(102, '000000', 'Menu Management', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', now(), '', null, 'Menu Management Menu');
INSERT INTO `sys_menu` VALUES(103, '000000', 'Department Management', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', now(), '', null, 'Department Management Menu');
INSERT INTO `sys_menu` VALUES(104, '000000', 'Post Management', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', now(), '', null, 'Post Management Menu');
INSERT INTO `sys_menu` VALUES(105, '000000', 'Dictionary Management', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', now(), '', null, 'Dictionary Management Menu');
INSERT INTO `sys_menu` VALUES(106, '000000', 'Parameter Settings', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', now(), '', null, 'Parameter Settings Menu');

-- User Management Buttons
INSERT INTO `sys_menu` VALUES(1001, '000000', 'User Query', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1002, '000000', 'User Add', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1003, '000000', 'User Edit', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1004, '000000', 'User Delete', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', now(), '', null, '');

-- Role Management Buttons
INSERT INTO `sys_menu` VALUES(1005, '000000', 'Role Query', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1006, '000000', 'Role Add', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1007, '000000', 'Role Edit', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1008, '000000', 'Role Delete', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', now(), '', null, '');

-- Menu Management Buttons
INSERT INTO `sys_menu` VALUES(1009, '000000', 'Menu Query', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1010, '000000', 'Menu Add', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1011, '000000', 'Menu Edit', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1012, '000000', 'Menu Delete', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', now(), '', null, '');

-- Dictionary Management Buttons
INSERT INTO `sys_menu` VALUES(1013, '000000', 'Dictionary Query', 105, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1014, '000000', 'Dictionary Add', 105, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1015, '000000', 'Dictionary Edit', 105, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1016, '000000', 'Dictionary Delete', 105, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', now(), '', null, '');

-- Parameter Settings Buttons
INSERT INTO `sys_menu` VALUES(1017, '000000', 'Parameter Query', 106, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1018, '000000', 'Parameter Add', 106, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1019, '000000', 'Parameter Edit', 106, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', now(), '', null, '');
INSERT INTO `sys_menu` VALUES(1020, '000000', 'Parameter Delete', 106, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', now(), '', null, '');

-- ----------------------------
-- Initialize Role Menu Association Data
-- ----------------------------
-- Super Administrator has all permissions
INSERT INTO `sys_role_menu` VALUES(1, 1);
INSERT INTO `sys_role_menu` VALUES(1, 100);
INSERT INTO `sys_role_menu` VALUES(1, 101);
INSERT INTO `sys_role_menu` VALUES(1, 102);
INSERT INTO `sys_role_menu` VALUES(1, 103);
INSERT INTO `sys_role_menu` VALUES(1, 104);
INSERT INTO `sys_role_menu` VALUES(1, 105);
INSERT INTO `sys_role_menu` VALUES(1, 106);
INSERT INTO `sys_role_menu` VALUES(1, 1001);
INSERT INTO `sys_role_menu` VALUES(1, 1002);
INSERT INTO `sys_role_menu` VALUES(1, 1003);
INSERT INTO `sys_role_menu` VALUES(1, 1004);
INSERT INTO `sys_role_menu` VALUES(1, 1005);
INSERT INTO `sys_role_menu` VALUES(1, 1006);
INSERT INTO `sys_role_menu` VALUES(1, 1007);
INSERT INTO `sys_role_menu` VALUES(1, 1008);
INSERT INTO `sys_role_menu` VALUES(1, 1009);
INSERT INTO `sys_role_menu` VALUES(1, 1010);
INSERT INTO `sys_role_menu` VALUES(1, 1011);
INSERT INTO `sys_role_menu` VALUES(1, 1012);
INSERT INTO `sys_role_menu` VALUES(1, 1013);
INSERT INTO `sys_role_menu` VALUES(1, 1014);
INSERT INTO `sys_role_menu` VALUES(1, 1015);
INSERT INTO `sys_role_menu` VALUES(1, 1016);
INSERT INTO `sys_role_menu` VALUES(1, 1017);
INSERT INTO `sys_role_menu` VALUES(1, 1018);
INSERT INTO `sys_role_menu` VALUES(1, 1019);
INSERT INTO `sys_role_menu` VALUES(1, 1020);

-- Common User only has query permissions
INSERT INTO `sys_role_menu` VALUES(2, 1);
INSERT INTO `sys_role_menu` VALUES(2, 100);
INSERT INTO `sys_role_menu` VALUES(2, 101);
INSERT INTO `sys_role_menu` VALUES(2, 102);
INSERT INTO `sys_role_menu` VALUES(2, 105);
INSERT INTO `sys_role_menu` VALUES(2, 1001);
INSERT INTO `sys_role_menu` VALUES(2, 1005);
INSERT INTO `sys_role_menu` VALUES(2, 1009);
INSERT INTO `sys_role_menu` VALUES(2, 1013);
INSERT INTO `sys_role_menu` VALUES(2, 1017);