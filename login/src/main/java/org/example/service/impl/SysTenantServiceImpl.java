package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.domain.entity.SysTenant;
import org.example.utils.TenantHelper;
import org.example.mapper.SysTenantMapper;
import org.example.service.ISysTenantService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 租户信息 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements ISysTenantService {

    @Override
    public List<SysTenant> selectTenantList(SysTenant tenant) {
        // 忽略租户过滤，查询所有租户
        return TenantHelper.ignore(() -> {
            LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(tenant.getTenantId())) {
                wrapper.like(SysTenant::getTenantId, tenant.getTenantId());
            }
            if (StringUtils.hasText(tenant.getCompanyName())) {
                wrapper.like(SysTenant::getCompanyName, tenant.getCompanyName());
            }
            if (StringUtils.hasText(tenant.getStatus())) {
                wrapper.eq(SysTenant::getStatus, tenant.getStatus());
            }
            return baseMapper.selectList(wrapper);
        });
    }

    @Override
    public SysTenant selectTenantByTenantId(String tenantId) {
        // 忽略租户过滤，根据租户编号查询
        return TenantHelper.ignore(() -> {
            LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysTenant::getTenantId, tenantId);
            return baseMapper.selectOne(wrapper);
        });
    }

    @Override
    public boolean insertTenant(SysTenant tenant) {
        // 忽略租户过滤，新增租户
        return TenantHelper.ignore(() -> {
            return baseMapper.insert(tenant) > 0;
        });
    }

    @Override
    public boolean updateTenant(SysTenant tenant) {
        // 忽略租户过滤，修改租户
        return TenantHelper.ignore(() -> {
            return baseMapper.updateById(tenant) > 0;
        });
    }

    @Override
    public boolean deleteTenantById(Long id) {
        // 忽略租户过滤，删除租户
        return TenantHelper.ignore(() -> {
            return baseMapper.deleteById(id) > 0;
        });
    }

}