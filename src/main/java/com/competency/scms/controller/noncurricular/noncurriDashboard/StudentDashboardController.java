package com.competency.scms.controller.noncurricular.noncurriDashboard;

import com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentDashboardResponse;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.noncurricular.noncurriDashboard.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student")
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    @GetMapping("/dashboard")
    public StudentDashboardResponse getDashboard(
            @AuthenticationPrincipal CustomUserDetails user // 실제 Principal 타입에 맞게 수정
    ) {
        Long studentId = user.getUser().getId();; // or user.getUserId()
        return studentDashboardService.getDashboard(studentId);
    }
}

