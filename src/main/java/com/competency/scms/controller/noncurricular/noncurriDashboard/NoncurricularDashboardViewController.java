package com.competency.scms.controller.noncurricular.noncurriDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/noncurricular/dashboard")
public class NoncurricularDashboardViewController {

    @GetMapping("/personal")
    public String personalDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 학생 대시보드");
        model.addAttribute("content",
                "noncurricular/noncurriDashboard/student-dashboard :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    @GetMapping("/operator")
    public String operatorDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 운영자 대시보드");
        model.addAttribute("content",
                "noncurricular/noncurriDashboard/operator-dashboard :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    @GetMapping("/department")
    public String departmentDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 부서관리자 대시보드");
        model.addAttribute("content",
                "noncurricular/noncurriDashboard/department-dashboard :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }
}
