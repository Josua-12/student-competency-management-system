package com.competency.scms.controller.noncurricular.noncurriDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/noncurricular/student")
public class NoncurricularStudentViewController {

    /**
     * 공통으로 프래그먼트 모델 설정
     * baseView: templates/ 이하의 경로 (확장자 .html 제외)
     * 예: "noncurricular/noncurriDashboard/student-dashboard"
     */
    private void setView(Model model, String baseView) {
        model.addAttribute("content", baseView);
//        model.addAttribute("contentStyles",   baseView);
//        model.addAttribute("contentFragment", baseView);
//        model.addAttribute("contentScript",   baseView);
    }

    /**
     * 학생 대시보드
     * GET /noncurricular/student/dashboard
     */
    @GetMapping("/dashboard")
    public String studentDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 학생 대시보드");
        setView(model, "noncurricular/noncurriDashboard/student-dashboard");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 프로그램 목록(학생)
     * GET /noncurricular/student/programs
     */
    @GetMapping("/programs")
    public String studentProgramList(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 목록(학생)");
        setView(model, "noncurricular/program/list_User");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 신청/취소
     * GET /noncurricular/student/applications
     */
//    @GetMapping("/applications")
//    public String applications(Model model) {
//        model.addAttribute("pageTitle", "비교과 프로그램 - 신청/취소");
//        setView(model, "noncurricular/program/open");
//        return "noncurricular/fix-screen/noncurricular-layout";
//    }

    /**
     * 신청 이력 조회
     * GET /noncurricular/student/applications/history
     */
    @GetMapping("/applications/history")
    public String applicationHistory(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 신청 이력");
        setView(model, "noncurricular/program/list_User");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 만족도 설문
     * GET /noncurricular/student/satisfaction
     */
    @GetMapping("/satisfaction")
    public String satisfaction(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 만족도 설문");
        setView(model, "noncurricular/operation/RegisterSatisfactionQuestion");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 결과보고서 등록
     * GET /noncurricular/student/reports
     */
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 결과보고서 등록");
        setView(model, "noncurricular/report/RegisterResultsReport");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 이수내역 조회
     * GET /noncurricular/student/completions
     */
    @GetMapping("/completions")
    public String completions(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 이수내역 조회");
        setView(model, "noncurricular/operation/ResultSatisfaction");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 비교과 포인트 조회
     * GET /noncurricular/student/points
     * (일단 학생 대시보드 화면 재사용)
     */
    @GetMapping("/points")
    public String points(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 포인트 조회");
        setView(model, "noncurricular/noncurriDashboard/student-dashboard");
        return "noncurricular/fix-screen/noncurricular-layout";
    }
}

