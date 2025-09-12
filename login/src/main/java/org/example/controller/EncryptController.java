package org.example.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.RsaConfig;
import org.example.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 加密相关控制器
 * 提供RSA公钥获取、加密解密测试等接口
 *
 * @author Lion Li
 */
@Slf4j
@RestController
@RequestMapping("/encrypt")
@RequiredArgsConstructor
public class EncryptController {

    private final RsaConfig rsaConfig;

    /**
     * 获取RSA公钥
     * 供前端加密使用
     *
     * @return RSA公钥
     */
    // 公开接口，无需权限验证
    @GetMapping("/rsa/publicKey")
    public R<Map<String, Object>> getRsaPublicKey() {
        Map<String, Object> result = new HashMap<>();
        result.put("enabled", rsaConfig.isEnabled());
        result.put("publicKey", rsaConfig.getPublicKeyForFrontend());
        
        log.info("获取RSA公钥，启用状态: {}", rsaConfig.isEnabled());
        return R.ok(result);
    }

    /**
     * RSA解密测试接口
     * 用于测试前端加密后的数据是否能正确解密
     *
     * @param encryptedData 加密后的数据
     * @return 解密结果
     */
    @SaCheckPermission("system:test:encrypt")
    @PostMapping("/rsa/decrypt")
    public R<Map<String, Object>> testRsaDecrypt(@RequestBody Map<String, String> request) {
        String encryptedData = request.get("encryptedData");
        
        if (encryptedData == null || encryptedData.trim().isEmpty()) {
            return R.fail("加密数据不能为空");
        }
        
        try {
            String decryptedData = rsaConfig.decrypt(encryptedData);
            
            Map<String, Object> result = new HashMap<>();
            result.put("encryptedData", encryptedData);
            result.put("decryptedData", decryptedData);
            result.put("success", true);
            
            log.info("RSA解密测试成功，原始长度: {}, 解密后长度: {}", 
                    encryptedData.length(), decryptedData.length());
            
            return R.ok(result);
        } catch (Exception e) {
            log.error("RSA解密测试失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("encryptedData", encryptedData);
            result.put("error", e.getMessage());
            result.put("success", false);
            
            return R.fail("解密失败: " + e.getMessage(), result);
        }
    }

    /**
     * RSA加密测试接口
     * 用于测试后端加密功能
     *
     * @param request 包含待加密数据的请求
     * @return 加密结果
     */
    @SaCheckPermission("system:test:encrypt")
    @PostMapping("/rsa/encrypt")
    public R<Map<String, Object>> testRsaEncrypt(@RequestBody Map<String, String> request) {
        String plainData = request.get("plainData");
        
        if (plainData == null || plainData.trim().isEmpty()) {
            return R.fail("待加密数据不能为空");
        }
        
        try {
            String encryptedData = rsaConfig.encrypt(plainData);
            
            Map<String, Object> result = new HashMap<>();
            result.put("plainData", plainData);
            result.put("encryptedData", encryptedData);
            result.put("success", true);
            
            log.info("RSA加密测试成功，原始长度: {}, 加密后长度: {}", 
                    plainData.length(), encryptedData.length());
            
            return R.ok(result);
        } catch (Exception e) {
            log.error("RSA加密测试失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("plainData", plainData);
            result.put("error", e.getMessage());
            result.put("success", false);
            
            return R.fail("加密失败: " + e.getMessage(), result);
        }
    }

    /**
     * BCrypt密码测试接口
     * 用于测试BCrypt密码加密和验证功能
     *
     * @param request 包含密码的请求
     * @return 测试结果
     */
    @SaCheckPermission("system:test:encrypt")
    @PostMapping("/test/bcrypt")
    public R<Map<String, Object>> testBCrypt(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        String hash = request.get("hash");
        
        if (password == null || password.trim().isEmpty()) {
            return R.fail("密码不能为空");
        }
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            if (hash != null && !hash.trim().isEmpty()) {
                // 验证密码
                boolean matches = org.example.utils.EncryptUtils.matchesPassword(password, hash);
                result.put("password", password);
                result.put("hash", hash);
                result.put("matches", matches);
            } else {
                // 生成哈希
                String generatedHash = org.example.utils.EncryptUtils.encryptPassword(password);
                result.put("password", password);
                result.put("generatedHash", generatedHash);
                
                // 验证生成的哈希
                boolean matches = org.example.utils.EncryptUtils.matchesPassword(password, generatedHash);
                result.put("verificationTest", matches);
            }
            
            return R.ok(result);
        } catch (Exception e) {
            log.error("BCrypt测试失败", e);
            return R.fail("BCrypt测试失败: " + e.getMessage());
        }
    }
}