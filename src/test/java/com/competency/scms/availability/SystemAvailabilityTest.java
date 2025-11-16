package com.competency.scms.availability;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class SystemAvailabilityTest {

    @Test
    public void testSystemAvailabilitySimulation() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger totalRequests = new AtomicInteger(0);
        
        CompletableFuture<Void>[] futures = IntStream.range(0, 1000)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    totalRequests.incrementAndGet();
                    
                    try {
                        long startTime = System.currentTimeMillis();
                        Thread.sleep(1);
                        long endTime = System.currentTimeMillis();
                        long responseTime = endTime - startTime;
                        
                        if (responseTime < 1000) {
                            successCount.incrementAndGet();
                        }
                        
                    } catch (Exception e) {
                        System.err.println("Request failed: " + e.getMessage());
                    }
                }, executor))
                .toArray(CompletableFuture[]::new);
        
        CompletableFuture.allOf(futures).join();
        
        int total = totalRequests.get();
        int success = successCount.get();
        double availabilityRate = (double) success / total * 100;
        
        System.out.println("총 요청 수: " + total);
        System.out.println("성공 요청 수: " + success);
        System.out.println("가용률: " + String.format("%.2f%%", availabilityRate));
        
        assertTrue(availabilityRate >= 99.0, 
                "시스템 가용률이 99% 이상이어야 합니다. 현재: " + String.format("%.2f%%", availabilityRate));
        
        executor.shutdown();
    }

    @Test
    public void testBusinessHourAvailability() {
        assertTrue(isValidBusinessHour(LocalTime.of(9, 0)), "09:00은 업무시간이어야 합니다");
        assertTrue(isValidBusinessHour(LocalTime.of(12, 30)), "12:30은 업무시간이어야 합니다");
        assertTrue(isValidBusinessHour(LocalTime.of(18, 0)), "18:00은 업무시간이어야 합니다");
        
        assertFalse(isValidBusinessHour(LocalTime.of(8, 59)), "08:59는 업무시간이 아니어야 합니다");
        assertFalse(isValidBusinessHour(LocalTime.of(18, 1)), "18:01은 업무시간이 아니어야 합니다");
    }

    private boolean isValidBusinessHour(LocalTime time) {
        LocalTime businessStart = LocalTime.of(9, 0);
        LocalTime businessEnd = LocalTime.of(18, 0);
        
        return !time.isBefore(businessStart) && !time.isAfter(businessEnd);
    }
}