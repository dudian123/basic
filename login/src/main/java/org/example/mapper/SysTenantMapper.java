package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.domain.entity.SysTenant;

/**
 * 租户信息 数据层
 *
 * @author ruoyi
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenant> {

}