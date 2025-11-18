package com.competency.scms.controller.counsel;

import com.competency.scms.dto.counsel.CounselingAdminDto;
import com.competency.scms.dto.counsel.CounselingApprovalDto;
import com.competency.scms.dto.counsel.CounselingReservationDto;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.counsel.CounselingAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counseling/admin")
@RequiredArgsConstructor
public class CounselingAdminApiController {

    private final CounselingAdminService adminService;

    // 대시보드 통계 데이터 조회
    @GetMapping("/dashboard")
    public ResponseEntity<CounselingAdminDto.DashboardResponse> getDashboardData() {
        CounselingAdminDto.DashboardResponse dashboard = adminService.getDashboardData();
        return ResponseEntity.ok(dashboard);
    }

    // 승인 대기 목록 조회
    @GetMapping("/approvals")
    public ResponseEntity<Page<CounselingReservationDto.AdminListResponse>> getPendingApprovals(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {
        Page<CounselingReservationDto.AdminListResponse> approvals = 
                adminService.getReservationsForApproval(status, field, counselorId, startDate, endDate, pageable);
        return ResponseEntity.ok(approvals);
    }

    // 상담 예약 승인
    @PostMapping("/approvals/{reservationId}/approve")
    public ResponseEntity<Void> approveReservation(
            @PathVariable Long reservationId,
            @RequestBody @Valid CounselingApprovalDto.AdminApprovalRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        adminService.approveReservation(reservationId, request, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    // 상담 예약 거부
    @PostMapping("/approvals/{reservationId}/reject")
    public ResponseEntity<Void> rejectReservation(
            @PathVariable Long reservationId,
            @RequestBody @Valid CounselingApprovalDto.RejectRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        adminService.rejectReservation(reservationId, request, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    // 상담사 목록 조회 (필드별)
    @GetMapping("/counselors")
    public ResponseEntity<java.util.List<CounselingAdminDto.CounselorOption>> getCounselorsByField(
            @RequestParam(required = false) String field) {
        java.util.List<CounselingAdminDto.CounselorOption> counselors = 
                adminService.getCounselorsByField(field);
        return ResponseEntity.ok(counselors);
    }
}