package com.competency.scms.controller.noncurricular.noncurriDashboard;


import com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorDashboardResponse;
import com.competency.scms.service.noncurricular.noncurriDashboard.OperatorDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operator")
public class OperatorDashboardController {

    private final OperatorDashboardService operatorDashboardService;

    @GetMapping("/dashboard")
    public OperatorDashboardResponse getDashboard() {
        return operatorDashboardService.getDashboard();
    }

    @GetMapping("/noncurricular/programs")
    public String programList(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 조회");
        model.addAttribute("content", "noncurricular/program-list :: content");
        return "layouts/noncurricular-layout";
    }
}

