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
        // 인증되지 않은 사용자는 로그인 페이지로
        if (auth == null) {
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
            case "ROLE_NONCURRICULAR_ADMIN" -> "redirect:/noncurricular-admin/dashboard";
            case "ROLE_NONCURRICULAR_OPERATOR" -> "redirect:/noncurricular-operator/dashboard";
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
    public String dashboard() {
        log.info("학생 대시보드 페이지 접근");
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

    @GetMapping("/noncurricular-operator/dashboard")
    public String operatorDashboard() {
        log.info("비교과 운영자 대시보드 페이지 접근");
        return "noncurricular/noncurriDashboard/operator-dashboard";
    }

    @GetMapping("/noncurricular-admin/dashboard")
    public String noncurricularAdminDashboard() {
        log.info("비교과 관리자 대시보드 페이지 접근");
        return "noncurricular/noncurriDashboard/student-dashboard";
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
}
