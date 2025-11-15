package com.competency.scms.controller.noncurricular.noncurriDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/noncurricular/admin")
public class NoncurricularAdminViewController {

    // 비교과 관리자 대시보드
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 관리자 대시보드");
        model.addAttribute("content", "noncurricular/admin/admin-dashboard :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 프로그램 관리
    @GetMapping("/programs")
    public String adminProgramList(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 관리");
        model.addAttribute("content", "noncurricular/admin/program-list :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 과정 개설 승인
    @GetMapping("/programs/approval")
    public String programApproval(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 과정 개설 승인");
        model.addAttribute("content", "noncurricular/admin/program-approval :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 참가자 현황 조회
    @GetMapping("/participants")
    public String participantStatus(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 참가자 현황");
        model.addAttribute("content", "noncurricular/admin/participant-status :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 만족도 설문 관리
    @GetMapping("/satisfaction/manage")
    public String manageSatisfactionSurvey(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 만족도 설문 관리");
        model.addAttribute("content", "noncurricular/admin/satisfaction-manage :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 만족도 결과 조회
    @GetMapping("/satisfaction/results")
    public String satisfactionResults(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 만족도 결과 조회");
        model.addAttribute("content", "noncurricular/admin/satisfaction-results :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 결과보고서 관리
    @GetMapping("/reports")
    public String manageReports(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 결과보고서 관리");
        model.addAttribute("content", "noncurricular/admin/report-manage :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }
}

