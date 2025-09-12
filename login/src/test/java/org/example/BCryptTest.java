package org.example;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String existingHash = "$2a$10$7JB720yubVSOfvVMe6/b/.b4OXhF.ktMfqH9z.jx.jjgs/9cDgCa2";
        
        // 验证现有哈希
        boolean matches = encoder.matches(rawPassword, existingHash);
        System.out.println("密码 '" + rawPassword + "' 与现有哈希匹配: " + matches);
        
        // 生成新的哈希
        String newHash = encoder.encode(rawPassword);
        System.out.println("新生成的哈希: " + newHash);
        
        // 验证新哈希
        boolean newMatches = encoder.matches(rawPassword, newHash);
        System.out.println("密码 '" + rawPassword + "' 与新哈希匹配: " + newMatches);
    }
}