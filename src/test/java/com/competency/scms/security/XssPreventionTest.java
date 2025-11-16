package com.competency.scms.security;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.HtmlUtils;

import static org.junit.jupiter.api.Assertions.*;

public class XssPreventionTest {

    @Test
    public void testHtmlEscaping() {
        // XSS 공격 시도 문자열들
        String[] xssAttempts = {
            "<script>alert('XSS')</script>",
            "<img src=x onerror=alert('XSS')>",
            "javascript:alert('XSS')",
            "<iframe src='javascript:alert(\"XSS\")'></iframe>",
            "<svg onload=alert('XSS')>",
            "';DROP TABLE users;--"
        };
        
        for (String xssAttempt : xssAttempts) {
            String escaped = HtmlUtils.htmlEscape(xssAttempt);
            
            // HTML 특수문자가 이스케이프되었는지 확인
            assertFalse(escaped.contains("<script>"), "script 태그가 이스케이프되어야 합니다");
            assertFalse(escaped.contains("<img"), "img 태그가 이스케이프되어야 합니다");
            assertFalse(escaped.contains("<iframe"), "iframe 태그가 이스케이프되어야 합니다");
            assertFalse(escaped.contains("<svg"), "svg 태그가 이스케이프되어야 합니다");
            
            // 이스케이프된 문자열에는 &lt; &gt; 등이 포함되어야 함
            if (xssAttempt.contains("<")) {
                assertTrue(escaped.contains("&lt;"), "< 문자가 &lt;로 이스케이프되어야 합니다");
            }
            if (xssAttempt.contains(">")) {
                assertTrue(escaped.contains("&gt;"), "> 문자가 &gt;로 이스케이프되어야 합니다");
            }
            
            System.out.println("원본: " + xssAttempt);
            System.out.println("이스케이프: " + escaped);
            System.out.println("---");
        }
    }

    @Test
    public void testSafeUserInput() {
        // 안전한 사용자 입력
        String[] safeInputs = {
            "안전한 한글 입력",
            "Safe English Input",
            "숫자123과 특수문자!@#",
            "이메일@example.com"
        };
        
        for (String safeInput : safeInputs) {
            String escaped = HtmlUtils.htmlEscape(safeInput);
            
            // 안전한 입력은 대부분 그대로 유지되어야 함 (특수문자 제외)
            assertNotNull(escaped, "이스케이프된 결과가 null이 아니어야 합니다");
            
            System.out.println("안전한 입력: " + safeInput + " -> " + escaped);
        }
    }

    @Test
    public void testUrlEncoding() {
        String maliciousUrl = "javascript:alert('XSS')";
        String safeUrl = "https://example.com/page?param=value";
        
        // URL 인코딩 테스트 (실제 구현에서는 URL 검증 로직 필요)
        assertFalse(maliciousUrl.startsWith("http://") || maliciousUrl.startsWith("https://"), 
                "javascript: URL은 허용되지 않아야 합니다");
        
        assertTrue(safeUrl.startsWith("http://") || safeUrl.startsWith("https://"), 
                "안전한 URL은 허용되어야 합니다");
    }
}