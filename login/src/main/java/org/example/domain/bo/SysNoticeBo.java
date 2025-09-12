package org.example.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.domain.BaseEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 通知公告业务对象 sys_notice
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeBo extends BaseEntity {

    /**
     * 公告ID
     */
    private Long noticeId;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过{max}个字符")
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    @NotBlank(message = "公告类型不能为空")
    private String noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 公告状态（0正常 1关闭）
     */
    private String status;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 备注
     */
    private String remark;
}