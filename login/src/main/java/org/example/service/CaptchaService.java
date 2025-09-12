package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务
 *
 * @author Lion Li
 */
@Slf4j
@Service
public class CaptchaService {

    /**
     * 验证码缓存
     */
    private final Map<String, String> captchaCache = new ConcurrentHashMap<>();

    /**
     * 验证码字符
     */
    private static final String CAPTCHA_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    /**
     * 验证码长度
     */
    private static final int CAPTCHA_LENGTH = 4;

    /**
     * 验证码过期时间（毫秒）
     */
    private static final long CAPTCHA_EXPIRE_TIME = 2 * 60 * 1000; // 2分钟

    /**
     * 生成验证码
     *
     * @param uuid 唯一标识
     * @return 验证码信息
     */
    public Map<String, Object> generateCaptcha(String uuid) {
        // 生成验证码文本
        String captchaText = generateCaptchaText();
        
        // 存储验证码（实际项目中应该存储到Redis等缓存中）
        captchaCache.put(uuid, captchaText + ":" + (System.currentTimeMillis() + CAPTCHA_EXPIRE_TIME));
        
        // 生成验证码图片（这里简化处理，实际项目中需要生成真实的图片）
        BufferedImage image = generateCaptchaImage(captchaText);
        
        Map<String, Object> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("img", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="); // 简化的base64图片
        
        return result;
    }

    /**
     * 验证验证码
     *
     * @param uuid 唯一标识
     * @param code 验证码
     * @return 是否验证成功
     */
    public boolean verifyCaptcha(String uuid, String code) {
        if (uuid == null || code == null) {
            return false;
        }
        
        String cachedData = captchaCache.get(uuid);
        if (cachedData == null) {
            return false;
        }
        
        String[] parts = cachedData.split(":");
        if (parts.length != 2) {
            return false;
        }
        
        String cachedCode = parts[0];
        long expireTime = Long.parseLong(parts[1]);
        
        // 检查是否过期
        if (System.currentTimeMillis() > expireTime) {
            captchaCache.remove(uuid);
            return false;
        }
        
        // 验证码验证成功后删除
        captchaCache.remove(uuid);
        
        return cachedCode.equalsIgnoreCase(code);
    }

    /**
     * 生成验证码文本
     *
     * @return 验证码文本
     */
    private String generateCaptchaText() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成验证码图片
     *
     * @param text 验证码文本
     * @return 验证码图片
     */
    private BufferedImage generateCaptchaImage(String text) {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        
        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.BLACK);
        
        // 绘制验证码文本
        g.drawString(text, 20, 25);
        
        g.dispose();
        return image;
    }

    /**
     * 清理过期的验证码
     */
    public void cleanExpiredCaptcha() {
        long currentTime = System.currentTimeMillis();
        captchaCache.entrySet().removeIf(entry -> {
            String[] parts = entry.getValue().split(":");
            if (parts.length == 2) {
                long expireTime = Long.parseLong(parts[1]);
                return currentTime > expireTime;
            }
            return true;
        });
    }
}