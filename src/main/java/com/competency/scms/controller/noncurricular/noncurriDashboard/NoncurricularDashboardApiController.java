package com.competency.scms.controller.noncurricular.noncurriDashboard;

import com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorDashboardResponse;
import com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentDashboardResponse;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.noncurricular.noncurriDashboard.OperatorDashboardService;
import com.competency.scms.service.noncurricular.noncurriDashboard.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noncurricular")
public class NoncurricularDashboardApiController {

    private final StudentDashboardService studentDashboardService;
    private final OperatorDashboardService operatorDashboardService;
    // private final AdminDashboardService adminDashboardService;

    /**
     * 학생 대시보드 API
     * GET /api/noncurricular/student/dashboard
     */
    @GetMapping("/student/dashboard")
    public StudentDashboardResponse getStudentDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // ✅ 로그인한 사용자에서 ID 꺼내기
        Long userId = userDetails.getUser().getId(); // ← 실제 필드명에 맞게 getId() / getUserId() / getUserNum() 중 하나로 수정

        // ✅ 서비스가 요구하는 형태대로 userId 전달
        return studentDashboardService.getDashboard(userId);
    }

    /**
     * 운영자 대시보드 API
     * GET /api/noncurricular/operator/dashboard
     */
    @GetMapping("/operator/dashboard")
    public OperatorDashboardResponse getOperatorDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();  // 운영자도 로그인 유저 기준 데이터면 이렇게
        return operatorDashboardService.getDashboard(userId);
    }

    // 관리자 대시보드도 필요하면 같은 패턴으로 추가
    /*
    @GetMapping("/admin/dashboard")
    public AdminDashboardResponse getAdminDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return adminDashboardService.getDashboard(userId);
    }
    */
}
