package com.competency.scms.controller;

import com.competency.scms.dto.dashboard.DashboardResponseDto;
import com.competency.scms.service.main.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final DashboardService mainDashboardService;

    @GetMapping({"/", "/main"})
    public String getMainDashboard(Model model, Authentication authentication) {
        
        log.info("[MainController] 메인 대시보드 페이지 요청 - userId: {}",
                authentication.getName());

        try {
            String userNum = authentication.getName();
            DashboardResponseDto dashboard = mainDashboardService.getMainDashboardData(userNum);

            model.addAttribute("userInfo", dashboard);
            model.addAttribute("competencies", dashboard.getCompetencyScore());
            model.addAttribute("consultations", java.util.Collections.emptyList());
            model.addAttribute("recentPrograms", dashboard.getRecentPrograms());

            return "main/dashboard";
        } catch (Exception e) {
            log.error("[MainController] 메인 대시보드 로드 실패", e);
            model.addAttribute("errorMessage", "메인 대시보드를 불러올 수 없습니다.");
            return "error";
        }
    }
}