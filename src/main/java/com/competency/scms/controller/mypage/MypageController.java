package com.competency.scms.controller.mypage;

import com.competency.scms.dto.noncurricular.mypage.ApplicationStatusDto;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.competency.mypage.AssessmentHistoryService;
import com.competency.scms.service.noncurricular.mypage.ApplicationStatusService;
import com.competency.scms.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageController {

    private final UserInfoService userInfoService;
    private final AssessmentHistoryService assessmentHistoryService;
    private final ApplicationStatusService applicationStatusService;


    @GetMapping
    public String mypageDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("마이페이지 대시보드 접근 - userId: {}", userDetails.getId());

        var userInfo = userInfoService.getUserInfo(userDetails.getId());
        model.addAttribute("userName", userInfo.name());
        model.addAttribute("userEmail", userInfo.email());
        model.addAttribute("userNum", userInfo.userNum());
        model.addAttribute("department", userInfo.department());

        return "mypage/mypageDashBoard";
    }

    @GetMapping("/info")
    public String changeInfo(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("개인정보 수정 페이지 접근 - userId: {}", userDetails.getId());

        var userInfo = userInfoService.getUserInfo(userDetails.getId());
        model.addAttribute("userName", userInfo.name());
        model.addAttribute("userEmail", userInfo.email());
        model.addAttribute("userNum", userInfo.userNum());
        model.addAttribute("department", userInfo.department());
        model.addAttribute("phone", userInfo.phone());

        return "mypage/changeinfo";
    }

    @GetMapping("/password")
    public String changePassword() {
        log.info("비밀번호 변경 페이지 접근");
        return "mypage/changePW";
    }

    @GetMapping("/assessment-history")
    public String assessmentHistory(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("진단 이력 페이지 접근 - userId: {}", userDetails.getId());

        var assessmentHistory = assessmentHistoryService.getUserAssessmentHistory(userDetails.getId());
        model.addAttribute("assessmentHistory", assessmentHistory);
        model.addAttribute("assessmentCount", assessmentHistory.size());

        return "mypage/assessmentHistory";
    }

    @GetMapping("/application-status")
    public String applicationStatus(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("신청 현황 페이지 접근 - userId: {}", userDetails.getId());

        var applicationStatus = applicationStatusService.getUserApplicationStatus(userDetails.getId());
        model.addAttribute("applicationStatus", applicationStatus);
        model.addAttribute("applicationCount", applicationStatus.size());

        return "mypage/applicationstatus";
    }
}


