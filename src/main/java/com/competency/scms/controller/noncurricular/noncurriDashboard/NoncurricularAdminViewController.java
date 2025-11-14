package com.competency.scms.controller.noncurricular.noncurriDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/noncurricular")
public class NoncurricularAdminViewController {

    // 운영자 대시보드: GET /admin/noncurricular/dashboard
    @GetMapping("/dashboard")
    public String operatorDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 운영자 대시보드");
        model.addAttribute("content",
                "noncurricular/noncurriDashboard/operator-dashboard :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 프로그램 조회(운영자): GET /admin/noncurricular/programs
    @GetMapping("/programs")
    public String operatorProgramList(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 조회(운영자)");
        model.addAttribute("content",
                "noncurricular/program/list_Op :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 과정 개설: GET /admin/noncurricular/programs/open
    @GetMapping("/programs/open")
    public String operatorProgramOpen(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 과정 개설");
        model.addAttribute("content",
                "noncurricular/program/open_Op :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 참가자 관리: GET /admin/noncurricular/participants
    @GetMapping("/participants")
    public String participantManage(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 참가자 관리");
        model.addAttribute("content",
                "noncurricular/operation/ParticipantMgt :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 만족도 설문 등록: GET /admin/noncurricular/satisfaction/register
    @GetMapping("/satisfaction/register")
    public String registerSatisfactionQuestion(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 만족도 설문 등록");
        model.addAttribute("content",
                "noncurricular/operation/RegisterSatisfactionQuestion :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 만족도 결과 조회: GET /admin/noncurricular/satisfaction/results
    @GetMapping("/satisfaction/results")
    public String resultSatisfaction(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 만족도 결과 조회");
        model.addAttribute("content",
                "noncurricular/operation/ResultSatisfaction :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 결과보고서 등록: GET /admin/noncurricular/reports/register
    @GetMapping("/reports/register")
    public String registerResultsReport(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 결과보고서 등록");
        model.addAttribute("content",
                "noncurricular/report/RegisterResultsReport :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

}

