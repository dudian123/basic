package org.example.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.ISysConfigService;
import org.example.domain.vo.SysConfigVo;
import org.example.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统配置管理控制器
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/config")
public class SysConfigController {

    private final ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @GetMapping("/list")
    public R<List<SysConfigVo>> list() {
        try {
            List<SysConfigVo> list = configService.selectConfigList();
            return R.ok(list);
        } catch (Exception e) {
            log.error("获取参数配置列表失败: {}", e.getMessage(), e);
            return R.fail("获取参数配置列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据配置键查询配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    // 公开接口，无需权限验证
    @GetMapping("/getConfigByKey")
    public R<String> getConfigByKey(@RequestParam String configKey) {
        try {
            String configValue = configService.selectConfigByKey(configKey);
            return R.ok(configValue);
        } catch (Exception e) {
            log.error("查询配置失败: {}", e.getMessage(), e);
            return R.fail("查询配置失败: " + e.getMessage());
        }
    }

    /**
     * 查询注册功能是否开启
     *
     * @return 是否开启注册功能
     */
    // 公开接口，无需权限验证
    @GetMapping("/registerEnabled")
    public R<Boolean> getRegisterEnabled() {
        try {
            boolean enabled = configService.selectRegisterEnabled();
            return R.ok(enabled);
        } catch (Exception e) {
            log.error("查询注册开关失败: {}", e.getMessage(), e);
            return R.fail("查询注册开关失败: " + e.getMessage());
        }
    }

    /**
     * 设置配置值
     *
     * @param configKey   配置键
     * @param configValue 配置值
     * @return 操作结果
     */
    @SaCheckPermission("system:config:edit")
    @PostMapping("/setConfig")
    public R<Void> setConfig(@RequestParam String configKey, @RequestParam String configValue) {
        try {
            boolean success = configService.setConfig(configKey, configValue);
            if (success) {
                return R.ok("配置设置成功");
            } else {
                return R.fail("配置设置失败");
            }
        } catch (Exception e) {
            log.error("设置配置失败: {}", e.getMessage(), e);
            return R.fail("设置配置失败: " + e.getMessage());
        }
    }

    /**
     * 设置注册功能开关
     *
     * @param enabled 是否开启注册功能
     * @return 操作结果
     */
    @SaCheckPermission("system:config:edit")
    @PostMapping("/setRegisterEnabled")
    public R<Void> setRegisterEnabled(@RequestParam boolean enabled) {
        try {
            String configValue = enabled ? "true" : "false";
            boolean success = configService.setConfig("sys.account.registerUser", configValue);
            if (success) {
                return R.ok("注册开关设置成功");
            } else {
                return R.fail("注册开关设置失败");
            }
        } catch (Exception e) {
            log.error("设置注册开关失败: {}", e.getMessage(), e);
            return R.fail("设置注册开关失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有系统配置
     *
     * @return 系统配置列表
     */
    @GetMapping("/getAllConfigs")
    public R<Map<String, String>> getAllConfigs() {
        try {
            Map<String, String> configs = new HashMap<>();
            
            // 获取主要配置项
            configs.put("sys.account.registerUser", configService.selectConfigByKey("sys.account.registerUser"));
            configs.put("sys.user.initPassword", configService.selectConfigByKey("sys.user.initPassword"));
            configs.put("sys.index.skinName", configService.selectConfigByKey("sys.index.skinName"));
            configs.put("sys.user.captchaEnabled", configService.selectConfigByKey("sys.user.captchaEnabled"));
            
            return R.ok(configs);
        } catch (Exception e) {
            log.error("获取所有配置失败: {}", e.getMessage(), e);
            return R.fail("获取所有配置失败: " + e.getMessage());
        }
    }

    /**
     * 重新初始化系统配置
     *
     * @return 操作结果
     */
    @PostMapping("/initConfig")
    public R<Void> initConfig() {
        try {
            configService.initSystemConfig();
            return R.ok("系统配置初始化成功");
        } catch (Exception e) {
            log.error("初始化系统配置失败: {}", e.getMessage(), e);
            return R.fail("初始化系统配置失败: " + e.getMessage());
        }
    }
}