package com.competency.scms.controller.noncurricular.noncurriDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/noncurricular/operator")
public class NoncurricularOperatorViewController {
    /**
     * 운영자 대시보드
     * GET /noncurricular/operator/dashboard
     */
    @GetMapping("/dashboard")
    public String operatorDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 운영자 대시보드");
        model.addAttribute("contentFragment",
                "noncurricular/noncurriDashboard/operator-dashboard :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 이후 운영자용 프로그램 등록/관리 화면도 여기로:
    // @GetMapping("/programs")
    // @GetMapping("/programs/new")
    // ...
}
