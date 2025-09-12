package org.example.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 参数配置视图对象 sys_config
 *
 * @author Lion Li
 */
@Data
public class SysConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 参数主键
     */
    private Long configId;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    private String configType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String remark;
}