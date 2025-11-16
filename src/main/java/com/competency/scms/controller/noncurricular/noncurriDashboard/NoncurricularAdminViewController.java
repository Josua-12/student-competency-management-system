package com.competency.scms.controller.noncurricular.noncurriDashboard;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/noncurricular/admin")
public class NoncurricularAdminViewController {

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 관리자 대시보드");
        // 처음엔 operator-dashboard 화면 같이 써도 되고,
        // 나중에 admin 전용 화면 따로 만들면 content만 바꿔주면 됨.
        model.addAttribute("contentFragment",
                "noncurricular/noncurriDashboard/operator-dashboard :: content");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    // 관리자 전용 메뉴 (권한 관리, 통합 통계 등)도 여기로 확장
}
