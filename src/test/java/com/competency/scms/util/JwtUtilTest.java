package com.competency.scms.util;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tZ2VuZXJhdGlvbi0yNTYtYml0cw==");
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", 604800000L);
    }

    @Test
    void 액세스토큰_생성_성공() {
        // given
        Long userId = 1L;
        String email = "test@example.com";
        String role = "STUDENT";

        // when
        String token = jwtUtil.generateAccessToken(userId, email, role);

        // then
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void 리프레시토큰_생성_성공() {
        // given
        Long userId = 1L;
        String email = "test@example.com";
        String role = "STUDENT";

        // when
        String token = jwtUtil.generateRefreshToken(userId, email, role);

        // then
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void 토큰_검증_성공() {
        // given
        String token = jwtUtil.generateAccessToken(1L, "test@example.com", "STUDENT");

        // when
        boolean isValid = jwtUtil.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void 토큰_검증_실패_잘못된토큰() {
        // given
        String invalidToken = "invalid.token.here";

        // when
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void 토큰에서_이메일_추출() {
        // given
        String email = "test@example.com";
        String token = jwtUtil.generateAccessToken(1L, email, "STUDENT");

        // when
        String extractedEmail = jwtUtil.getEmailFromToken(token);

        // then
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    void 토큰에서_역할_추출() {
        // given
        String role = "STUDENT";
        String token = jwtUtil.generateAccessToken(1L, "test@example.com", role);

        // when
        String extractedRole = jwtUtil.getRoleFromToken(token);

        // then
        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    void 토큰에서_사용자ID_추출() {
        // given
        Long userId = 123L;
        String token = jwtUtil.generateAccessToken(userId, "test@example.com", "STUDENT");

        // when
        Long extractedUserId = jwtUtil.getUserIdFromToken(token);

        // then
        assertThat(extractedUserId).isEqualTo(userId);
    }
}
