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
        if (auth == null) {
            return "redirect:/auth/login";
        }

        // 사용자 역할에 따른 대시보드 리다이렉트
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_STUDENT");

        return switch (role) {
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            case "ROLE_COUNSELOR" -> "redirect:/counselor/dashboard";
            case "ROLE_OPERATOR" -> "redirect:/operator/dashboard";
            default -> "redirect:/main/dashboard"; // 학생용
        };
    }

    @GetMapping("/user/dashboard")
    public String dashboard() {
        log.info("학생 대시보드 페이지 접근");
        return "main/dashboard";
    }

    @GetMapping("/counselor/dashboard")
    public String counselorDashboard() {
        log.info("상담사 대시보드 페이지 접근");
        return "counseling/counselor/counselor-main";
    }

    @GetMapping("/operator/dashboard")
    public String operatorDashboard() {
        log.info("운영자 대시보드 페이지 접근");
        return "noncurricular/operator/operator-main";
    }

    @GetMapping("/auth/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/login")
    public String loginRedirect() {
        return "redirect:/auth/login";
    }
}
