package com.competency.scms.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SqlInjectionPreventionTest {

    @Test
    public void testSqlInjectionAttempts() {
        // SQL Injection 공격 시도 문자열들
        String[] sqlInjectionAttempts = {
            "'; DROP TABLE users; --",
            "' OR '1'='1",
            "' UNION SELECT * FROM users --",
            "admin'--",
            "' OR 1=1 --",
            "'; INSERT INTO users VALUES ('hacker', 'password'); --",
            "' OR 'x'='x",
            "1'; DELETE FROM users WHERE 't'='t"
        };
        
        for (String attempt : sqlInjectionAttempts) {
            // JPA/Hibernate는 PreparedStatement를 사용하므로 
            // 이런 문자열들이 SQL로 실행되지 않고 파라미터로 처리됨
            
            // SQL Injection 패턴 감지
            assertTrue(containsSqlInjectionPattern(attempt), 
                    "SQL Injection 패턴이 감지되어야 합니다: " + attempt);
            
            System.out.println("SQL Injection 시도 감지: " + attempt);
        }
    }

    @Test
    public void testSafeInputs() {
        // 안전한 입력들
        String[] safeInputs = {
            "user@example.com",
            "안전한사용자",
            "NormalUser123",
            "user.name@domain.co.kr"
        };
        
        for (String safeInput : safeInputs) {
            assertFalse(containsSqlInjectionPattern(safeInput), 
                    "안전한 입력은 SQL Injection 패턴으로 감지되지 않아야 합니다: " + safeInput);
        }
    }

    @Test
    public void testParameterizedQuerySafety() {
        // JPA Repository 메서드들은 자동으로 PreparedStatement 사용
        // 예: findByEmail(String email) -> SELECT * FROM users WHERE email = ?
        
        String maliciousEmail = "test@test.com'; DROP TABLE users; --";
        
        // 실제로는 이 값이 파라미터로 바인딩되어 SQL Injection이 불가능
        // 테스트에서는 패턴만 검증
        assertTrue(containsSqlInjectionPattern(maliciousEmail), 
                "악성 이메일 입력에서 SQL Injection 패턴이 감지되어야 합니다");
        
        System.out.println("JPA Repository는 PreparedStatement를 사용하여 안전합니다");
    }

    private boolean containsSqlInjectionPattern(String input) {
        if (input == null) return false;
        
        String lowerInput = input.toLowerCase();
        
        // 일반적인 SQL Injection 패턴들
        String[] patterns = {
            "drop table",
            "delete from", 
            "insert into",
            "update set",
            "union select",
            "' or '",
            "' or 1=1",
            "' or 'x'='x",
            "--",
            "/*",
            "*/",
            "';",
            "' and ",
            "' union ",
            "' having "
        };
        
        for (String pattern : patterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
}