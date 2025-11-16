package com.competency.scms.controller.noncurricular.noncurriDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/noncurricular")
public class NoncurricularStudentViewController {

    // 학생 대시보드는 NoncurricularEntryController에서 처리

    // 프로그램 조회(학생): GET /user/noncurricular/programs
    @GetMapping("/programs")
    public String studentProgramList(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 조회(학생)");
        model.addAttribute("content",
                "noncurricular/program/list_User :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // (선택) 프로그램 상세(학생): GET /user/noncurricular/programs/detail
    @GetMapping("/programs/detail")
    public String studentProgramDetail(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 상세(학생)");
        model.addAttribute("content",
                "noncurricular/program/detail_User :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // TODO: 신청/참여/포인트 조회 화면도 템플릿 만들면 여기 추가
}

