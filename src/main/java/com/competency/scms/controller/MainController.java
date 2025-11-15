package com.competency.scms.controller;

import com.competency.scms.service.main.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final DashboardService mainDashboardService;

    @GetMapping({"/", "/main"})
    public String mainRedirect(Authentication auth) {
        log.info("메인 리다이렉트 - 인증 상태: {}", auth != null ? auth.getName() : "null");
        if (auth != null) {
            log.info("인증된 사용자 권한: {}", auth.getAuthorities());
        }
        
        // 인증되지 않은 사용자는 로그인 페이지로
        if (auth == null) {
            log.info("비인증 사용자 - 로그인 페이지로 리다이렉트");
            return "redirect:/auth/login";
        }

        // 인증된 사용자는 역할에 따른 대시보드로 리다이렉트
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_STUDENT");

        return switch (role) {
            case "ROLE_SUPER_ADMIN" -> "redirect:/super-admin/dashboard";
            case "ROLE_COUNSELING_ADMIN" -> "redirect:/counseling-admin/dashboard";
            case "ROLE_NONCURRICULAR_ADMIN" -> "redirect:/noncurricular/admin/dashboard";
            case "ROLE_NONCURRICULAR_OPERATOR" -> "redirect:/noncurricular/operator/dashboard";
            case "ROLE_COMPETENCY_ADMIN" -> "redirect:/competency-admin/dashboard";
            case "ROLE_COUNSELOR" -> "redirect:/counselor/dashboard";
            default -> "redirect:/student/dashboard"; // 학생용
        };
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect(Authentication auth) {
        return mainRedirect(auth);
    }

    @GetMapping("/student/dashboard")
    public String dashboard(Authentication auth, org.springframework.ui.Model model) {
        log.info("학생 대시보드 페이지 접근");
        try {
            String userNum = auth.getName();
            log.info("대시보드 조회할 사용자 학번: {}", userNum);

            var dashboardData = mainDashboardService.getMainDashboardData(userNum);
            model.addAttribute("dashboardData", dashboardData);
            
            // JavaScript에서 사용할 수 있도록 JSON 문자열로 전달
            model.addAttribute("dashboardDataJson", 
                new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dashboardData));
        } catch (Exception e) {
            log.error("대시보드 데이터 로드 실패", e);
            model.addAttribute("errorMessage", "대시보드 데이터를 불러올 수 없습니다.");
        }
        return "main/dashboard";
    }

    @GetMapping("/counselor/dashboard")
    public String counselorDashboard() {
        log.info("상담사 대시보드 페이지 접근");
        return "counseling/counselor/counselor-main";
    }

    @GetMapping("/counseling-admin/dashboard")
    public String counselingAdminDashboard() {
        log.info("상담 관리자 대시보드 페이지 접근");
        return "counseling/admin/admin-main";
    }





    @GetMapping("/competency-admin/dashboard")
    public String competencyAdminDashboard() {
        log.info("역량 관리자 대시보드 페이지 접근");
        return "competency/admin-section";
    }

    @GetMapping("/super-admin/dashboard")
    public String superAdminDashboard() {
        log.info("최고 관리자 대시보드 페이지 접근");
        return "admin/dashboard";
    }

    @GetMapping("/auth/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/login")
    public String loginRedirect() {
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/find-password")
    public String findPasswordPage() {
        return "auth/find-password";
    }

    @GetMapping("/auth/verify-email")
    public String verifyEmailPage() {
        return "auth/verify-email";
    }

    @GetMapping("/auth/reset-password")
    public String resetPasswordPage() {
        return "auth/reset-password";
    }

    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) {
        log.info("=== 로그아웃 요청 시작 ===");
        log.info("요청 URL: {}", request.getRequestURL());
        log.info("요청 IP: {}", request.getRemoteAddr());
        
        // 기존 쿠키 확인
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName()) || "refreshToken".equals(cookie.getName())) {
                    log.info("기존 쿠키 발견: {} = {}", cookie.getName(), 
                        cookie.getValue().length() > 10 ? cookie.getValue().substring(0, 10) + "..." : cookie.getValue());
                }
            }
        }

        // 쿠키 삭제
        jakarta.servlet.http.Cookie accessTokenCookie = new jakarta.servlet.http.Cookie("accessToken", "");
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        response.addCookie(accessTokenCookie);
        log.info("accessToken 쿠키 삭제 설정 완료");
        
        jakarta.servlet.http.Cookie refreshTokenCookie = new jakarta.servlet.http.Cookie("refreshToken", "");
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        response.addCookie(refreshTokenCookie);
        log.info("refreshToken 쿠키 삭제 설정 완료");

        // localStorage 삭제를 위한 헤더
        response.setHeader("Clear-Site-Data", "\"cookies\", \"storage\"");
        log.info("Clear-Site-Data 헤더 설정 완료");
        
        log.info("=== 로그아웃 완료 - /auth/login으로 리다이렉트 ===");
        return "redirect:/auth/login";
    }
}
