-- System configuration table initialization script
-- Create system configuration table
CREATE TABLE IF NOT EXISTS `sys_config` (
    `config_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Config ID',
    `config_name` varchar(100) DEFAULT '' COMMENT 'Config Name',
    `config_key` varchar(100) DEFAULT '' COMMENT 'Config Key',
    `config_value` varchar(500) DEFAULT '' COMMENT 'Config Value',
    `config_type` char(1) DEFAULT 'N' COMMENT 'System Built-in (Y=Yes, N=No)',
    `create_by` bigint(20) DEFAULT NULL COMMENT 'Creator',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_by` bigint(20) DEFAULT NULL COMMENT 'Updater',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `remark` varchar(500) DEFAULT NULL COMMENT 'Remark',
    PRIMARY KEY (`config_id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System Config Table';

-- Insert system configuration initial data
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`) VALUES
(1, 'Register Function Switch', 'sys.account.registerUser', 'true', 'Y', 1, NOW(), 'Whether to enable user registration function (true=enable, false=disable)'),
(2, 'User Management - Initial Password', 'sys.user.initPassword', '123456', 'Y', 1, NOW(), 'Initial password 123456'),
(3, 'Main Frame - Default Skin Style Name', 'sys.index.skinName', 'skin-blue', 'Y', 1, NOW(), 'Blue skin-blue, Green skin-green, Purple skin-purple, Red skin-red, Yellow skin-yellow'),
(4, 'Captcha Function Switch', 'sys.user.captchaEnabled', 'true', 'Y', 1, NOW(), 'Whether to enable captcha function (true=enable, false=disable)')
ON DUPLICATE KEY UPDATE
    `config_name` = VALUES(`config_name`),
    `config_value` = VALUES(`config_value`),
    `config_type` = VALUES(`config_type`),
    `update_time` = NOW(),
    `remark` = VALUES(`remark`);

-- 创建用户类型字典数据（如果不存在字典表，可以忽略此部分）
-- 这里只是示例，实际项目中可能需要根据具体的字典表结构调整

-- 查询系统配置
-- SELECT * FROM sys_config WHERE config_key = 'sys.account.registerUser';

-- 更新注册功能开关
-- UPDATE sys_config SET config_value = 'false' WHERE config_key = 'sys.account.registerUser';

-- 查询所有系统配置
-- SELECT config_key, config_value, config_name, remark FROM sys_config ORDER BY config_id;