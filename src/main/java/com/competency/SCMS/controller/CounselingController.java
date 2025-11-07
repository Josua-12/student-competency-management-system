package com.competency.SCMS.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/counseling")
@RequiredArgsConstructor
public class CounselingController {

    // 학생 상담 메인 페이지
    @GetMapping("/student")
    public String studentCounselingMain() {
        return "counseling/student/main";
    }

    // 학생 상담 신청 현황
    @GetMapping("/student/status")
    public String studentCounselingStatus() {
        return "counseling/student/state";
    }

    // 상담사 메인 페이지
    @GetMapping("/counselor")
    public String counselorMain(Model model, Authentication auth) {
        return "counseling/counselor/counselor-main";
    }

    // 상담사 일정 관리
    @GetMapping("/counselor/schedule")
    public String counselorSchedule() {
        return "counseling/counselor/schedule-management";
    }

    // 상담사 예약 승인 관리
    @GetMapping("/counselor/reservations")
    public String counselorReservations() {
        return "counseling/counselor/reservation-management";
    }

    // 상담사 상담일지 관리
    @GetMapping("/counselor/records")
    public String counselorRecords() {
        return "counseling/counselor/record-management";
    }

    // 상담사 상담 이력 조회
    @GetMapping("/counselor/history")
    public String counselorHistory() {
        return "counseling/counselor/history-management";
    }

    // 상담사 만족도 조회
    @GetMapping("/counselor/satisfaction")
    public String counselorSatisfaction() {
        return "counseling/counselor/satisfaction-results";
    }

    // 관리자 메인 페이지
    @GetMapping("/admin")
    public String adminMain(Model model, Authentication auth) {
        return "counseling/admin/admin-main";
    }

    // 관리자 상담 승인 관리
    @GetMapping("/admin/approvals")
    public String adminApprovals() {
        return "counseling/admin/approval-management";
    }

    // 관리자 상담 통계
    @GetMapping("/admin/statistics")
    public String adminStatistics() {
        return "counseling/admin/statistics";
    }

    // 관리자 상담 기초 관리
    @GetMapping("/admin/settings")
    public String adminSettings() {
        return "counseling/admin/basic-settings";
    }
}