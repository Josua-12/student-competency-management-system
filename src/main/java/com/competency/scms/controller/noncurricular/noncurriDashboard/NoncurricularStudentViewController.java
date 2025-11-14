package com.competency.scms.controller.noncurricular.noncurriDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/noncurricular")
public class NoncurricularStudentViewController {

    // 학생용 비교과 프로그램 관련 화면들
    // 대시보드는 NoncurricularEntryController에서 처리

    @GetMapping("/programs")
    public String studentProgramList(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 조회");
        model.addAttribute("content", "noncurricular/program/list_User :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    @GetMapping("/programs/detail")
    public String studentProgramDetail(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 상세");
        model.addAttribute("content", "noncurricular/program/detail_User :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }
}

