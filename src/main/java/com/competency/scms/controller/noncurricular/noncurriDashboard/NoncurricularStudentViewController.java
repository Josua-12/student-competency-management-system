package com.competency.scms.controller.noncurricular.noncurriDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/noncurricular/student")
public class NoncurricularStudentViewController {


    /**
     * 학생 대시보드
     * GET /noncurricular/student/dashboard
     */
    @GetMapping("/dashboard")
    public String studentDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 학생 대시보드");
        model.addAttribute("contentFragment",
                "noncurricular/noncurriDashboard/student-dashboard :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 프로그램 목록(학생)
     * GET /noncurricular/student/programs
     */
    @GetMapping("/programs")
    public String studentProgramList(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 목록(학생)");
        model.addAttribute("contentFragment",
                "noncurricular/program/list_User :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 나중에 상세, 신청현황, 포인트 조회 등도 여기에 계속 추가
    // @GetMapping("/programs/{id}")
    // @GetMapping("/applications")
    // ...
}


