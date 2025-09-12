import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String hashedPassword = "$2a$10$mRMiC7P7fRQQVkrOoxfcfOEaYLT9JO/SR/eW6FK2pClKxFkmSrxui";
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("哈希密码: " + hashedPassword);
        System.out.println("验证结果: " + encoder.matches(rawPassword, hashedPassword));
        
        // 生成新的哈希
        String newHash = encoder.encode(rawPassword);
        System.out.println("新生成的哈希: " + newHash);
        System.out.println("新哈希验证: " + encoder.matches(rawPassword, newHash));
    }
}