package com.competency.scms.controller.noncurricular.noncurriDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/noncurricular")
public class NoncurricularDashboardViewController {

    // 학생용 비교과 대시보드
    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 학생 대시보드");
        model.addAttribute("content", "noncurricular/student/student-dashboard :: content");
        return "layouts/noncurricular-layout";
    }

    // 비교과 운영자 대시보드
    @GetMapping("/operator/dashboard")
    public String operatorDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 운영자 대시보드");
        model.addAttribute("content", "noncurricular/operator/operator-dashboard :: content");
        return "layouts/noncurricular-layout";
    }


}
