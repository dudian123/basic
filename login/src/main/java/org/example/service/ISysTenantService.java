package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.domain.entity.SysTenant;

import java.util.List;

/**
 * 租户信息 服务层
 *
 * @author ruoyi
 */
public interface ISysTenantService extends IService<SysTenant> {

    /**
     * 查询租户列表
     *
     * @param tenant 租户信息
     * @return 租户集合
     */
    List<SysTenant> selectTenantList(SysTenant tenant);

    /**
     * 根据租户编号查询租户信息
     *
     * @param tenantId 租户编号
     * @return 租户信息
     */
    SysTenant selectTenantByTenantId(String tenantId);

    /**
     * 新增租户信息
     *
     * @param tenant 租户信息
     * @return 结果
     */
    boolean insertTenant(SysTenant tenant);

    /**
     * 修改租户信息
     *
     * @param tenant 租户信息
     * @return 结果
     */
    boolean updateTenant(SysTenant tenant);

    /**
     * 删除租户信息
     *
     * @param id 租户主键
     * @return 结果
     */
    boolean deleteTenantById(Long id);

}