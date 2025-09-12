package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;
import java.util.Map;

/**
 * 系统配置 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysConfigMapper {

    /**
     * 根据配置键查询配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    @Select("SELECT config_value FROM sys_config WHERE config_key = #{configKey}")
    String selectConfigByKey(@Param("configKey") String configKey);

    /**
     * 查询所有配置
     *
     * @return 配置列表
     */
    @Select("SELECT config_key, config_value FROM sys_config")
    List<Map<String, String>> selectAllConfig();

    /**
     * 查询参数配置列表
     *
     * @return 参数配置列表
     */
    @Select("SELECT config_id, tenant_id, config_name, config_key, config_value, config_type, create_time, remark FROM sys_config ORDER BY config_id")
    List<Map<String, Object>> selectConfigList();

    /**
     * 插入配置
     *
     * @param configKey   配置键
     * @param configValue 配置值
     * @param configName  配置名称
     * @param remark      备注
     * @return 影响行数
     */
    @Insert("INSERT INTO sys_config (config_key, config_value, config_name, config_type, create_by, create_time, remark) " +
            "VALUES (#{configKey}, #{configValue}, #{configName}, 'N', 1, NOW(), #{remark}) " +
            "ON DUPLICATE KEY UPDATE config_value = #{configValue}, update_time = NOW()")
    int insertConfig(@Param("configKey") String configKey, 
                    @Param("configValue") String configValue,
                    @Param("configName") String configName,
                    @Param("remark") String remark);

    /**
     * 更新配置
     *
     * @param configKey   配置键
     * @param configValue 配置值
     * @return 影响行数
     */
    @Update("UPDATE sys_config SET config_value = #{configValue}, update_time = NOW() WHERE config_key = #{configKey}")
    int updateConfig(@Param("configKey") String configKey, @Param("configValue") String configValue);

    /**
     * 删除配置
     *
     * @param configKey 配置键
     * @return 影响行数
     */
    @Delete("DELETE FROM sys_config WHERE config_key = #{configKey}")
    int deleteConfig(@Param("configKey") String configKey);

    /**
     * 检查配置是否存在
     *
     * @param configKey 配置键
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM sys_config WHERE config_key = #{configKey}")
    int checkConfigExists(@Param("configKey") String configKey);
}