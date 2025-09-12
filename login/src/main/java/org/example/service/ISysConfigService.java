package org.example.service;

/**
 * 系统配置服务接口
 *
 * @author Lion Li
 */
public interface ISysConfigService {

    /**
     * 根据配置键查询配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String selectConfigByKey(String configKey);

    /**
     * 查询注册功能是否开启
     *
     * @return 是否开启注册功能
     */
    boolean selectRegisterEnabled();

    /**
     * 设置配置值
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 是否设置成功
     */
    boolean setConfig(String configKey, String configValue);

    /**
     * 初始化系统配置
     */
    void initSystemConfig();

    /**
     * 查询参数配置列表
     *
     * @return 参数配置列表
     */
    java.util.List<org.example.domain.vo.SysConfigVo> selectConfigList();
}