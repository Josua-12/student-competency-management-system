package com.competency.scms.performance;

import com.competency.scms.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtPerformanceTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "dGVzdFNlY3JldEtleUZvclBlcmZvcm1hbmNlVGVzdDEyMzQ1Njc4OWFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6");
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", 604800000L);
    }

    @Test
    public void testJwtValidationPerformance() {
        // JWT 토큰 생성
        String token = jwtUtil.generateAccessToken(1L, "test@test.com", "STUDENT");
        
        // 100ms 이내 검증 테스트
        long startTime = System.currentTimeMillis();
        boolean isValid = jwtUtil.validateToken(token);
        long endTime = System.currentTimeMillis();
        
        long duration = endTime - startTime;
        System.out.println("JWT 검증 시간: " + duration + "ms");
        
        assertTrue(isValid, "JWT 토큰이 유효해야 합니다");
        assertTrue(duration < 100, "JWT 검증은 100ms 이내에 완료되어야 합니다. 실제: " + duration + "ms");
    }

    @Test
    public void testConcurrentJwtValidation() throws Exception {
        String token = jwtUtil.generateAccessToken(1L, "test@test.com", "STUDENT");
        ExecutorService executor = Executors.newFixedThreadPool(100);
        
        long startTime = System.currentTimeMillis();
        
        // 100개 동시 요청
        CompletableFuture<Boolean>[] futures = IntStream.range(0, 100)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    long threadStart = System.currentTimeMillis();
                    boolean result = jwtUtil.validateToken(token);
                    long threadEnd = System.currentTimeMillis();
                    System.out.println("Thread " + i + " 검증 시간: " + (threadEnd - threadStart) + "ms");
                    return result;
                }, executor))
                .toArray(CompletableFuture[]::new);
        
        CompletableFuture.allOf(futures).join();
        long endTime = System.currentTimeMillis();
        
        long totalDuration = endTime - startTime;
        System.out.println("100개 동시 JWT 검증 총 시간: " + totalDuration + "ms");
        
        // 모든 검증이 성공했는지 확인
        for (CompletableFuture<Boolean> future : futures) {
            assertTrue(future.get(), "모든 JWT 검증이 성공해야 합니다");
        }
        
        executor.shutdown();
    }
}