package com.competency.scms.controller.mypage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageController {

    @GetMapping
    public String mypageDashboard(Model model) {
        log.info("마이페이지 대시보드 접근");

        // 개발용 더미 데이터
        model.addAttribute("userName", "홍길동");
        model.addAttribute("userEmail", "student@test.com");
        model.addAttribute("userNum", "20241234");
        model.addAttribute("department", "컴퓨터공학과");

        return "mypage/mypageDashBoard";
    }

    @GetMapping("/info")
    public String changeInfo(Model model) {
        log.info("개인정보 수정 페이지 접근");

        // 개발용 더미 데이터
        model.addAttribute("userName", "홍길동");
        model.addAttribute("userEmail", "student@test.com");
        model.addAttribute("userNum", "20241234");
        model.addAttribute("department", "컴퓨터공학과");
        model.addAttribute("phone", "010-1234-5678");

        return "mypage/changeinfo";
    }

    @GetMapping("/password")
    public String changePassword() {
        log.info("비밀번호 변경 페이지 접근");
        return "mypage/changePW";
    }

    @GetMapping("/assessment-history")
    public String assessmentHistory(Model model) {
        log.info("진단 이력 페이지 접근");

        // 개발용 더미 데이터
        model.addAttribute("assessmentCount", 3);

        return "mypage/assessmentHistory";
    }

    @GetMapping("/application-status")
    public String applicationStatus(Model model) {
        log.info("신청 현황 페이지 접근");

        // 개발용 더미 데이터
        model.addAttribute("applicationCount", 2);

        return "mypage/applicationstatus";
    }
}
