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
    public String mainRedirect() {
        return "redirect:/main/dashboard";
    }

    @GetMapping("/main/dashboard")
    public String dashboard() {
        return "main/dashboard";
    }
}