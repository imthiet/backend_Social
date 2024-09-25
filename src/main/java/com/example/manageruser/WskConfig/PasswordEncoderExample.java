package com.example.manageruser.WskConfig;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderExample {
    public static void main(String[] args) {
        String rawPassword = "mothai"; // Thay thế bằng mật khẩu gốc của bạn
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Encoded password: " + encodedPassword);
    }
}
