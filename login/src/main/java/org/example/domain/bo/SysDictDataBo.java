package org.example.domain.bo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

/**
 * 字典数据业务对象 sys_dict_data
 *
 * @author example
 */
@Data
public class SysDictDataBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    private Long dictCode;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 字典排序
     */
    private Integer dictSort;

    /**
     * 字典标签
     */
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String dictLabel;

    /**
     * 字典键值
     */
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    private String dictValue;

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    @Size(max = 100, message = "样式属性长度不能超过100个字符")
    private String cssClass;

    /**
     * 表格回显样式
     */
    @Size(max = 100, message = "表格回显样式长度不能超过100个字符")
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    private String isDefault;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

}