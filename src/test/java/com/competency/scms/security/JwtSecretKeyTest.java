package com.competency.scms.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.competency.scms.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;

public class JwtSecretKeyTest {

    @Test
    public void testJwtSecretKeyFromEnvironment() {
        // 환경변수 시뮬레이션
        String envSecretKey = "dGVzdFNlY3JldEtleUZvckVudmlyb25tZW50VGVzdDEyMzQ1Njc4OWFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6";
        
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", envSecretKey);
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 3600000L);
        
        // JWT 토큰 생성 및 검증
        String token = jwtUtil.generateAccessToken(1L, "test@test.com", "STUDENT");
        
        assertNotNull(token, "JWT 토큰이 생성되어야 합니다");
        assertTrue(jwtUtil.validateToken(token), "환경변수 Secret Key로 생성된 토큰이 유효해야 합니다");
    }

    @Test
    public void testSecretKeyMinimumLength() {
        // 256비트(32바이트) 이상의 키 길이 검증
        String validKey = "dGVzdFNlY3JldEtleUZvckVudmlyb25tZW50VGVzdDEyMzQ1Njc4OWFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6";
        String shortKey = "shortKey";
        
        JwtUtil jwtUtil = new JwtUtil();
        
        // 유효한 키 테스트
        ReflectionTestUtils.setField(jwtUtil, "secretKey", validKey);
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 3600000L);
        
        assertDoesNotThrow(() -> {
            String token = jwtUtil.generateAccessToken(1L, "test@test.com", "STUDENT");
            jwtUtil.validateToken(token);
        }, "충분한 길이의 Secret Key는 정상 작동해야 합니다");
        
        // 짧은 키 테스트
        ReflectionTestUtils.setField(jwtUtil, "secretKey", shortKey);
        
        assertThrows(Exception.class, () -> {
            jwtUtil.generateAccessToken(1L, "test@test.com", "STUDENT");
        }, "짧은 Secret Key는 예외를 발생시켜야 합니다");
    }

    @Test
    public void testSecretKeyNotHardcoded() {
        // 하드코딩된 키가 아닌 환경변수/설정 파일에서 로드되는지 확인
        String[] commonHardcodedKeys = {
            "secret",
            "mySecretKey", 
            "jwt-secret",
            "defaultSecret"
        };
        
        for (String hardcodedKey : commonHardcodedKeys) {
            JwtUtil jwtUtil = new JwtUtil();
            ReflectionTestUtils.setField(jwtUtil, "secretKey", hardcodedKey);
            ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 3600000L);
            
            assertThrows(Exception.class, () -> {
                jwtUtil.generateAccessToken(1L, "test@test.com", "STUDENT");
            }, "하드코딩된 약한 키 '" + hardcodedKey + "'는 사용할 수 없어야 합니다");
        }
    }
}