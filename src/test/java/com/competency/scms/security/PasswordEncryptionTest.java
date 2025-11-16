package com.competency.scms.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordEncryptionTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    public void testBCryptPasswordEncryption() {
        String rawPassword = "testPassword123!";
        
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"), 
                "BCrypt 해시 형식이어야 합니다");
        
        assertNotEquals(rawPassword, encodedPassword, "암호화된 비밀번호는 원본과 달라야 합니다");
        
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword), 
                "원본 비밀번호와 암호화된 비밀번호가 일치해야 합니다");
        
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword), 
                "잘못된 비밀번호는 일치하지 않아야 합니다");
    }

    @Test
    public void testBCryptSaltRandomness() {
        String password = "samePassword";
        
        String encoded1 = passwordEncoder.encode(password);
        String encoded2 = passwordEncoder.encode(password);
        
        assertNotEquals(encoded1, encoded2, "Salt로 인해 같은 비밀번호도 다르게 암호화되어야 합니다");
        
        assertTrue(passwordEncoder.matches(password, encoded1));
        assertTrue(passwordEncoder.matches(password, encoded2));
    }
}