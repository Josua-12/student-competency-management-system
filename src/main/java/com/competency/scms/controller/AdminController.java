package com.competency.scms.controller;

import com.competency.scms.domain.user.User;
import com.competency.scms.repository.counseling.CounselorRepository;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.repository.competency.CompetencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserRepository userRepository;
    private final CompetencyRepository competencyRepository;
    private final CounselorRepository counselorRepository;



    // 역량 관리자 대시보드
    @GetMapping("/competency/admin/dashboard")
    public String competencyAdminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("[AdminController] 역량 관리자 대시보드 요청");
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

            // 역량진단 관련 통계만
            model.addAttribute("competencyList", competencyRepository.findAll());
            model.addAttribute("totalAssessments", competencyRepository.count());

            return "competency/admin/competency-admin-main";
        } catch (Exception e) {
            log.error("[AdminController] 역량 관리자 대시보드 로드 실패", e);
            model.addAttribute("errorMessage", "대시보드를 불러올 수 없습니다.");
            return "error";
        }
    }

    // 상담 관리자 대시보드
    @GetMapping("/counseling/admin/dashboard")
    public String counselingAdminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("[AdminController] 상담 관리자 대시보드 요청");
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

            // 상담 관련 통계만
            model.addAttribute("counselorList", counselorRepository.findAll());
            model.addAttribute("activeCounselors", counselorRepository.countByIsActive(true));

            return "counseling/admin/counseling-admin-main";
        } catch (Exception e) {
            log.error("[AdminController] 상담 관리자 대시보드 로드 실패", e);
            model.addAttribute("errorMessage", "대시보드를 불러올 수 없습니다.");
            return "error";
        }
    }

    // 사용자 목록 페이지
    @GetMapping("/admin/users")
    public String userList(Model model) {
        log.info("[AdminController] 사용자 목록 요청");
        try {
            model.addAttribute("users", userRepository.findAll());
            return "admin/user-list";
        } catch (Exception e) {
            log.error("[AdminController] 사용자 목록 로드 실패", e);
            model.addAttribute("errorMessage", "사용자 목록을 불러올 수 없습니다.");
            return "error";
        }
    }



}
