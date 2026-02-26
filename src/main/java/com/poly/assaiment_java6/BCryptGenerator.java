package com.poly.assaiment_java6;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";

        // Tạo chuỗi Hash BCrypt từ mật khẩu thô
        String encodedPassword = encoder.encode(rawPassword);

        // In ra console để COPY
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("BCrypt Hash for password '" + rawPassword + "':");
        System.out.println(encodedPassword);
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
    }
}

