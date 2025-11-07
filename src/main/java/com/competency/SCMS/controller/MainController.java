package com.competency.SCMS.controller;

import com.competency.SCMS.dto.dashboard.DashboardResponseDto;
import com.competency.SCMS.service.main.MainDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final MainDashboardService mainDashboardService;

    @GetMapping
    public String getMainDashboard(Model model, Authentication authentication) {
        log.info("[MainController] 메인 대시보드 페이지 요청 - userId: {}",
                authentication.getName());

        try {
            String userEmail = authentication.getName();
            DashboardResponseDto dashboard = mainDashboardService.getMainDashboardData(userEmail);

            model.addAttribute("userName", dashboard.getUserName());
            model.addAttribute("mileage", dashboard.getMileage());
            model.addAttribute("programCount", dashboard.getProgramCount());
            model.addAttribute("counselingCount", dashboard.getCounselingCount());
            model.addAttribute("competencyScore", dashboard.getCompetencyScore());
            model.addAttribute("recentPrograms", dashboard.getRecentPrograms());

            return "main/dashboard";
        } catch (Exception e) {
            log.error("[MainController] 메인 대시보드 로드 실패", e);
            model.addAttribute("errorMessage", "메인 대시보드를 불러올 수 없습니다.");
            return "error";
        }
    }

    @GetMapping("/api/data")
    @ResponseBody
    public DashboardResponseDto getMainDashboardData(Authentication authentication) {
        log.info("[MainController] 메인 대시보드 데이터 API 요청");

        try {
            String userEmail = authentication.getName();
            return mainDashboardService.getMainDashboardData(userEmail);
        } catch (Exception e) {
            log.error("[MainController] 메인 대시보드 데이터 조회 실패", e);
            throw new RuntimeException("메인 대시보드 데이터를 조회할 수 없습니다.", e);
        }
    }
}
