package org.example.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.EncryptUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.Map;

/**
 * RSA加密配置类
 * 用于管理RSA密钥对和提供加密解密服务
 *
 * @author Lion Li
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "rsa")
public class RsaConfig {

    /**
     * RSA公钥（用于前端加密）
     */
    private String publicKey;

    /**
     * RSA私钥（用于后端解密）
     */
    private String privateKey;

    /**
     * 是否启用RSA加密
     */
    private boolean enabled = false;

    @PostConstruct
    public void init() {
        // 如果配置文件中没有配置密钥，则自动生成
        if (enabled && (publicKey == null || privateKey == null)) {
            generateKeys();
        }
        
        if (enabled) {
            log.info("RSA加密已启用");
            log.info("RSA公钥: {}", publicKey);
            // 私钥不应该在日志中完整显示，只显示前几位
            log.info("RSA私钥: {}...", privateKey != null ? privateKey.substring(0, Math.min(20, privateKey.length())) : "null");
        } else {
            log.info("RSA加密未启用");
        }
    }

    /**
     * 生成RSA密钥对
     */
    private void generateKeys() {
        try {
            Map<String, String> keyMap = EncryptUtils.generateRsaKey();
            this.publicKey = keyMap.get(EncryptUtils.PUBLIC_KEY);
            this.privateKey = keyMap.get(EncryptUtils.PRIVATE_KEY);
            log.info("自动生成RSA密钥对成功");
        } catch (Exception e) {
            log.error("生成RSA密钥对失败", e);
        }
    }

    /**
     * 使用公钥加密数据
     *
     * @param data 待加密数据
     * @return 加密后的数据
     */
    public String encrypt(String data) {
        if (!enabled || publicKey == null) {
            return data;
        }
        try {
            return EncryptUtils.encryptByRsa(data, publicKey);
        } catch (Exception e) {
            log.error("RSA加密失败", e);
            return data;
        }
    }

    /**
     * 使用私钥解密数据
     *
     * @param encryptedData 待解密数据
     * @return 解密后的数据
     */
    public String decrypt(String encryptedData) {
        if (!enabled || privateKey == null) {
            return encryptedData;
        }
        try {
            return EncryptUtils.decryptByRsa(encryptedData, privateKey);
        } catch (Exception e) {
            log.error("RSA解密失败", e);
            return encryptedData;
        }
    }

    /**
     * 获取公钥（供前端使用）
     *
     * @return 公钥字符串
     */
    public String getPublicKeyForFrontend() {
        return enabled ? publicKey : null;
    }
}