package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.SysUser;
import org.example.domain.entity.SysUserRole;
import org.example.domain.PageQuery;
import org.example.domain.TableDataInfo;
import org.example.mapper.SysUserMapper;
import org.example.mapper.SysUserRoleMapper;
import org.example.mapper.SysRoleMapper;
import org.example.service.ISysUserService;
import org.example.utils.EncryptUtils;
import org.example.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.example.domain.vo.SysUserVo;

/**
 * 用户 业务层处理
 *
 * @author Lion Li
 */
@Slf4j
@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            return null;
        }
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userMapper.selectUserById(userId);
    }

    /**
     * 通过用户ID查询用户VO
     *
     * @param userId 用户ID
     * @return 用户VO对象信息
     */
    @Override
    public org.example.domain.vo.SysUserVo selectUserVoById(Long userId) {
        SysUser user = selectUserById(userId);
        if (user == null) {
            return null;
        }
        
        org.example.domain.vo.SysUserVo userVo = new org.example.domain.vo.SysUserVo();
        // 复制基本属性
        userVo.setUserId(user.getUserId());
        userVo.setUserName(user.getUserName());
        userVo.setNickName(user.getNickName());
        userVo.setEmail(user.getEmail());
        userVo.setPhonenumber(user.getPhonenumber());
        userVo.setSex(user.getSex());
        userVo.setAvatar(user.getAvatar());
        userVo.setStatus(user.getStatus());
        userVo.setCreateTime(user.getCreateTime());
        userVo.setRemark(user.getRemark());
        userVo.setDeptId(user.getDeptId());
        
        // 查询角色信息
        try {
            List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
            userVo.setRoleIds(roleIds);
            userVo.setRoleGroup(selectUserRoleGroup(userId));
        } catch (Exception e) {
            log.warn("查询用户角色信息失败: {}", e.getMessage());
        }
        
        return userVo;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(String userName) {
        if (StringUtils.isBlank(userName)) {
            return false;
        }
        int count = userMapper.countByUserName(userName);
        return count == 0;
    }

    /**
     * 验证用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 验证结果
     */
    @Override
    public boolean validateUserPassword(String userName, String password) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            log.warn("用户名或密码为空");
            return false;
        }

        SysUser user = selectUserByUserName(userName);
        if (user == null) {
            log.warn("用户不存在: {}", userName);
            return false;
        }

        // 检查用户状态
        if (!"0".equals(user.getStatus())) {
            log.warn("用户已被禁用: {}", userName);
            return false;
        }

        // 使用BCrypt验证密码
        boolean isValid = EncryptUtils.matchesPassword(password, user.getPassword());
        if (isValid) {
            log.info("用户 {} 密码验证成功", userName);
        } else {
            log.warn("用户 {} 密码验证失败", userName);
        }
        return isValid;
    }

    /**
     * 验证用户密码并返回用户信息
     *
     * @param userName 用户名
     * @param password 密码
     * @return 用户信息，验证失败返回null
     */
    @Override
    public SysUser validateUserPasswordAndGetUser(String userName, String password) {
        log.info("开始验证用户密码，用户名: {}", userName);
        
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            log.warn("用户名或密码为空，用户名: {}, 密码长度: {}", userName, password != null ? password.length() : 0);
            return null;
        }

        log.info("查询用户信息，用户名: {}", userName);
        SysUser user = selectUserByUserName(userName);
        if (user == null) {
            log.warn("用户不存在: {}", userName);
            return null;
        }
        
        log.info("找到用户，用户ID: {}, 状态: {}, 密码哈希: {}", user.getUserId(), user.getStatus(), user.getPassword());

        // 检查用户状态
        if (!"0".equals(user.getStatus())) {
            log.warn("用户已被禁用: {}, 状态: {}", userName, user.getStatus());
            return null;
        }

        // 使用BCrypt验证密码
        log.info("开始BCrypt密码验证，原始密码: {}, 哈希密码: {}", password, user.getPassword());
        boolean isValid = EncryptUtils.matchesPassword(password, user.getPassword());
        log.info("BCrypt密码验证结果: {}", isValid);
        
        if (isValid) {
            log.info("用户 {} 密码验证成功", userName);
            return user;
        } else {
            log.warn("用户 {} 密码验证失败", userName);
            return null;
        }
    }

    /**
     * 注册用户
     *
     * @param user 用户信息
     * @return 注册结果
     */
    @Override
    public boolean registerUser(SysUser user) {
        if (user == null || StringUtils.isBlank(user.getUserName()) || StringUtils.isBlank(user.getPassword())) {
            log.warn("用户信息不完整");
            return false;
        }

        // 检查用户名是否已存在
        if (!checkUserNameUnique(user.getUserName())) {
            log.warn("用户名已存在: {}", user.getUserName());
            return false;
        }

        // 加密密码
        String encryptedPassword = EncryptUtils.encryptPassword(user.getPassword());
        user.setPassword(encryptedPassword);

        // 设置默认值
        if (StringUtils.isBlank(user.getStatus())) {
            user.setStatus("0"); // 正常状态
        }
        if (StringUtils.isBlank(user.getDelFlag())) {
            user.setDelFlag("0"); // 未删除
        }
        if (user.getCreateTime() == null) {
            user.setCreateTime(java.time.LocalDateTime.now());
        }

        try {
            int result = userMapper.insertUser(user);
            if (result > 0) {
                log.info("用户 {} 注册成功", user.getUserName());
                return true;
            } else {
                log.warn("用户 {} 注册失败", user.getUserName());
                return false;
            }
        } catch (Exception e) {
            log.error("用户注册异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserAuth(Long userId, Long[] roleIds) {
        insertUserRole(userId, roleIds, true);
    }

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(Long userId) {
        try {
            List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
            if (roleIds == null || roleIds.isEmpty()) {
                return "";
            }
            
            StringBuilder roleNames = new StringBuilder();
            for (Long roleId : roleIds) {
                // 这里应该查询角色名称，暂时使用角色ID
                if (roleNames.length() > 0) {
                    roleNames.append(",");
                }
                roleNames.append("角色").append(roleId);
            }
            return roleNames.toString();
        } catch (Exception e) {
            log.error("查询用户角色组失败: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     * @param clear   清除已存在的关联数据
     */
    private void insertUserRole(Long userId, Long[] roleIds, boolean clear) {
        if (roleIds != null && roleIds.length > 0) {
            List<Long> roleList = new ArrayList<>();
            for (Long roleId : roleIds) {
                roleList.add(roleId);
            }
            
            if (clear) {
                // 删除用户与角色关联
                try {
                    userRoleMapper.deleteByUserId(userId);
                    log.info("清除用户{}的角色关联", userId);
                } catch (Exception e) {
                    log.warn("清除用户角色关联失败: {}", e.getMessage());
                }
            }
            
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<>();
            for (Long roleId : roleList) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            
            try {
                for (SysUserRole userRole : list) {
                    userRoleMapper.insert(userRole);
                }
                log.info("用户{}角色分配成功，分配角色数量: {}", userId, list.size());
            } catch (Exception e) {
                log.error("用户角色分配失败: {}", e.getMessage(), e);
                throw new RuntimeException("用户角色分配失败: " + e.getMessage());
            }
        }
    }

    /**
     * 查询用户列表
     *
     * @return 用户信息集合
     */
    @Override
    public List<SysUserVo> selectUserList() {
        log.info("开始查询用户列表");
        List<SysUser> users = userMapper.selectUserList();
        log.info("从数据库查询到用户数量: {}", users != null ? users.size() : 0);
        
        if (users == null || users.isEmpty()) {
            log.warn("数据库中没有查询到用户数据");
            return new ArrayList<>();
        }
        
        List<SysUserVo> result = users.stream()
                .map(SysUserVo::fromSysUser)
                .collect(Collectors.toList());
        log.info("转换后的用户VO数量: {}", result.size());
        return result;
    }

    /**
     * 分页查询用户列表
     *
     * @param pageQuery 分页查询参数
     * @return 分页结果
     */
    @Override
    public TableDataInfo<SysUserVo> selectUserList(PageQuery pageQuery) {
        log.info("开始分页查询用户列表，分页参数: {}", pageQuery);

        // 使用 MyBatis-Plus 分页插件进行物理分页
        Page<SysUser> page = pageQuery.build();
        IPage<SysUser> userPage = userMapper.selectPage(page, null);

        List<SysUser> records = userPage.getRecords();
        log.info("分页查询到记录数: {}，总记录数: {}", records != null ? records.size() : 0, userPage.getTotal());

        // 转换为 VO 对象
        List<SysUserVo> userVoList = new ArrayList<>();
        if (records != null) {
            for (SysUser user : records) {
                userVoList.add(SysUserVo.fromSysUser(user));
            }
        }

        // 构建返回结果（复用分页的 total、pages 等信息）
        return TableDataInfo.build(userPage, userVoList);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param roleId 角色ID
     * @param userName 用户名
     * @param phonenumber 手机号
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUserVo> selectAllocatedList(Long roleId, String userName, String phonenumber) {
        log.info("查询已分配角色{}的用户列表，用户名: {}, 手机号: {}", roleId, userName, phonenumber);
        try {
            List<SysUser> users = userMapper.selectAllocatedUserList(roleId, userName, phonenumber);
            log.info("查询到已分配用户数量: {}", users != null ? users.size() : 0);
            
            if (users == null || users.isEmpty()) {
                return new ArrayList<>();
            }
            
            return users.stream()
                    .map(SysUserVo::fromSysUser)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询已分配用户列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param roleId 角色ID
     * @param userName 用户名
     * @param phonenumber 手机号
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUserVo> selectUnallocatedList(Long roleId, String userName, String phonenumber) {
        log.info("查询未分配角色{}的用户列表，用户名: {}, 手机号: {}", roleId, userName, phonenumber);
        try {
            List<SysUser> users = userMapper.selectUnallocatedUserList(roleId, userName, phonenumber);
            log.info("查询到未分配用户数量: {}", users != null ? users.size() : 0);
            
            if (users == null || users.isEmpty()) {
                return new ArrayList<>();
            }
            
            return users.stream()
                    .map(SysUserVo::fromSysUser)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询未分配用户列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param roleId 角色ID
     * @param userName 用户名
     * @param phonenumber 手机号
     * @param pageQuery 分页查询参数
     * @return 用户信息分页集合信息
     */
    @Override
    public TableDataInfo<SysUserVo> selectAllocatedList(Long roleId, String userName, String phonenumber, PageQuery pageQuery) {
        log.info("开始分页查询已分配角色{}的用户列表，分页参数: {}", roleId, pageQuery);

        // 使用 MyBatis-Plus 分页插件进行物理分页
        Page<SysUser> page = pageQuery.build();
        
        IPage<SysUser> userPage = userMapper.selectAllocatedUserPage(page, roleId, userName, phonenumber);
        
        List<SysUser> records = userPage.getRecords();
        log.info("分页查询到已分配用户记录数: {}，总记录数: {}", records != null ? records.size() : 0, userPage.getTotal());

        // 转换为 VO 对象
        List<SysUserVo> userVoList = new ArrayList<>();
        if (records != null) {
            for (SysUser user : records) {
                userVoList.add(SysUserVo.fromSysUser(user));
            }
        }

        // 构建返回结果（复用分页的 total、pages 等信息）
        return TableDataInfo.build(userPage, userVoList);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param roleId 角色ID
     * @param userName 用户名
     * @param phonenumber 手机号
     * @param pageQuery 分页查询参数
     * @return 用户信息分页集合信息
     */
    @Override
    public TableDataInfo<SysUserVo> selectUnallocatedList(Long roleId, String userName, String phonenumber, PageQuery pageQuery) {
        log.info("开始分页查询未分配角色{}的用户列表，分页参数: {}", roleId, pageQuery);

        // 使用 MyBatis-Plus 分页插件进行物理分页
        Page<SysUser> page = pageQuery.build();
        
        IPage<SysUser> userPage = userMapper.selectUnallocatedUserPage(page, roleId, userName, phonenumber);
        
        List<SysUser> records = userPage.getRecords();
        log.info("分页查询到未分配用户记录数: {}，总记录数: {}", records != null ? records.size() : 0, userPage.getTotal());

        // 转换为 VO 对象
        List<SysUserVo> userVoList = new ArrayList<>();
        if (records != null) {
            for (SysUser user : records) {
                userVoList.add(SysUserVo.fromSysUser(user));
            }
        }

        // 构建返回结果（复用分页的 total、pages 等信息）
        return TableDataInfo.build(userPage, userVoList);
    }

    @Override
    public boolean updateUser(SysUser user) {
        if (user == null || user.getUserId() == null) {
            log.warn("更新用户失败，用户或用户ID为空");
            return false;
        }
        try {
            // 如果密码不为空，进行加密
            if (StringUtils.isNotBlank(user.getPassword())) {
                String encryptedPassword = EncryptUtils.encryptPassword(user.getPassword());
                user.setPassword(encryptedPassword);
            }
            // 设置更新时间
            user.setUpdateTime(java.time.LocalDateTime.now());
            int rows = userMapper.updateUser(user);
            if (rows > 0) {
                log.info("用户 {} 更新成功", user.getUserName());
                return true;
            } else {
                log.warn("用户 {} 更新失败，未影响任何行", user.getUserName());
                return false;
            }
        } catch (Exception e) {
            log.error("更新用户异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        if (userIds == null || userIds.length == 0) {
            log.warn("删除用户失败，用户ID数组为空");
            return 0;
        }
        // 先清理用户与角色的关联关系
        for (Long userId : userIds) {
            try {
                userRoleMapper.deleteByUserId(userId);
                log.info("已清理用户{}的角色关联", userId);
            } catch (Exception e) {
                log.warn("清理用户{}角色关联失败: {}", userId, e.getMessage());
            }
        }
        // 逻辑删除用户
        int rows = userMapper.deleteUserByIds(userIds);
        log.info("已逻辑删除用户数量: {}", rows);
        return rows;
    }
}