package org.example.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 加密工具类
 * 提供BCrypt密码加密、RSA非对称加密等功能
 *
 * @author Lion Li
 */
@Slf4j
public class EncryptUtils {

    /**
     * BCrypt密码编码器
     */
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * 公钥
     */
    public static final String PUBLIC_KEY = "publicKey";

    /**
     * 私钥
     */
    public static final String PRIVATE_KEY = "privateKey";

    // ==================== BCrypt 密码加密 ====================

    /**
     * BCrypt密码加密
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String rawPassword) {
        if (StrUtil.isBlank(rawPassword)) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return PASSWORD_ENCODER.encode(rawPassword);
    }

    /**
     * BCrypt密码验证
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 验证结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        log.info("EncryptUtils.matchesPassword 开始验证，原始密码: [{}], 哈希密码: [{}]", rawPassword, encodedPassword);
        
        if (StrUtil.isBlank(rawPassword) || StrUtil.isBlank(encodedPassword)) {
            log.warn("密码验证失败：密码或哈希为空，原始密码为空: {}, 哈希为空: {}", 
                    StrUtil.isBlank(rawPassword), StrUtil.isBlank(encodedPassword));
            return false;
        }
        
        try {
            boolean result = PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
            log.info("BCrypt验证结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("密码验证异常", e);
            return false;
        }
    }

    // ==================== Base64 编码 ====================

    /**
     * Base64加密
     *
     * @param data 待加密数据
     * @return 加密后字符串
     */
    public static String encryptByBase64(String data) {
        if (StrUtil.isBlank(data)) {
            return data;
        }
        return Base64.encode(data, StandardCharsets.UTF_8);
    }

    /**
     * Base64解密
     *
     * @param data 待解密数据
     * @return 解密后字符串
     */
    public static String decryptByBase64(String data) {
        if (StrUtil.isBlank(data)) {
            return data;
        }
        return Base64.decodeStr(data, StandardCharsets.UTF_8);
    }

    // ==================== RSA 非对称加密 ====================

    /**
     * 生成RSA密钥对
     *
     * @return 公私钥Map
     */
    public static Map<String, String> generateRsaKey() {
        Map<String, String> keyMap = new HashMap<>(2);
        RSA rsa = SecureUtil.rsa();
        keyMap.put(PRIVATE_KEY, rsa.getPrivateKeyBase64());
        keyMap.put(PUBLIC_KEY, rsa.getPublicKeyBase64());
        return keyMap;
    }

    /**
     * RSA公钥加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return 加密后字符串, 采用Base64编码
     */
    public static String encryptByRsa(String data, String publicKey) {
        if (StrUtil.isBlank(publicKey)) {
            throw new IllegalArgumentException("RSA需要传入公钥进行加密");
        }
        if (StrUtil.isBlank(data)) {
            return data;
        }
        RSA rsa = SecureUtil.rsa(null, publicKey);
        return rsa.encryptBase64(data, StandardCharsets.UTF_8, KeyType.PublicKey);
    }

    /**
     * RSA私钥解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     * @return 解密后字符串
     */
    public static String decryptByRsa(String data, String privateKey) {
        if (StrUtil.isBlank(privateKey)) {
            throw new IllegalArgumentException("RSA需要传入私钥进行解密");
        }
        if (StrUtil.isBlank(data)) {
            return data;
        }
        RSA rsa = SecureUtil.rsa(privateKey, null);
        return rsa.decryptStr(data, KeyType.PrivateKey);
    }

    // ==================== MD5 和 SHA256 哈希 ====================

    /**
     * MD5加密
     *
     * @param data 待加密数据
     * @return 加密后字符串, 采用Hex编码
     */
    public static String encryptByMd5(String data) {
        if (StrUtil.isBlank(data)) {
            return data;
        }
        return SecureUtil.md5(data);
    }

    /**
     * SHA256加密
     *
     * @param data 待加密数据
     * @return 加密后字符串, 采用Hex编码
     */
    public static String encryptBySha256(String data) {
        if (StrUtil.isBlank(data)) {
            return data;
        }
        return SecureUtil.sha256(data);
    }

    // ==================== 工具方法 ====================

    /**
     * 生成测试用的BCrypt密码
     * 仅用于开发测试环境
     *
     * @param password 原始密码
     */
    public static void printBCryptPassword(String password) {
        String encoded = encryptPassword(password);
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt加密: " + encoded);
        System.out.println("验证结果: " + matchesPassword(password, encoded));
    }

    /**
     * 生成测试用的RSA密钥对
     * 仅用于开发测试环境
     */
    public static void printRsaKeys() {
        Map<String, String> keyMap = generateRsaKey();
        System.out.println("RSA公钥: " + keyMap.get(PUBLIC_KEY));
        System.out.println("RSA私钥: " + keyMap.get(PRIVATE_KEY));
    }

    public static void main(String[] args) {
        printBCryptPassword("admin123");
    }
}