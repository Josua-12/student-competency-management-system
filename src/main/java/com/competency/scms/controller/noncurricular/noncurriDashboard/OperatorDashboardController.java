package com.competency.scms.controller.noncurricular.noncurriDashboard;


import com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorDashboardResponse;
import com.competency.scms.service.noncurricular.noncurriDashboard.OperatorDashboardService;
import lombok.RequiredArgsConstructor;
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
}

