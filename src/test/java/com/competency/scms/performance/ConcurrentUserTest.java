package com.competency.scms.performance;

import com.competency.scms.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrentUserTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "dGVzdFNlY3JldEtleUZvclBlcmZvcm1hbmNlVGVzdDEyMzQ1Njc4OWFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6");
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", 604800000L);
    }

    @Test
    public void testConcurrentUserAccess() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        AtomicInteger successCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // 100명 동시 사용자 시뮬레이션
        CompletableFuture<Void>[] futures = IntStream.range(0, 100)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        // 토큰 생성 (로그인 시뮬레이션)
                        String token = jwtUtil.generateAccessToken((long) i, "user" + i + "@test.com", "STUDENT");
                        
                        // 토큰 검증 (API 호출 시뮬레이션)
                        boolean isValid = jwtUtil.validateToken(token);
                        
                        if (isValid) {
                            successCount.incrementAndGet();
                        }
                        
                        // 추가 작업 시뮬레이션 (사용자 정보 조회 등)
                        Thread.sleep(10);
                        
                    } catch (Exception e) {
                        System.err.println("User " + i + " failed: " + e.getMessage());
                    }
                }, executor))
                .toArray(CompletableFuture[]::new);
        
        CompletableFuture.allOf(futures).join();
        long endTime = System.currentTimeMillis();
        
        long totalDuration = endTime - startTime;
        System.out.println("100명 동시 접속 처리 시간: " + totalDuration + "ms");
        System.out.println("성공한 사용자 수: " + successCount.get());
        
        // 모든 사용자가 성공적으로 처리되어야 함
        assertEquals(100, successCount.get(), "모든 사용자가 성공적으로 처리되어야 합니다");
        
        // 합리적인 시간 내에 처리되어야 함 (5초 이내)
        assertTrue(totalDuration < 5000, "100명 동시 접속은 5초 이내에 처리되어야 합니다. 실제: " + totalDuration + "ms");
        
        executor.shutdown();
    }
}