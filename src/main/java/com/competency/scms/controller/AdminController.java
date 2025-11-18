package com.competency.scms.controller;

import com.competency.scms.domain.user.User;

import com.competency.scms.repository.counseling.CounselorRepository;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.repository.competency.CompetencyRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserRepository userRepository;
    private final CompetencyRepository competencyRepository;
    private final CounselorRepository counselorRepository;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        log.info("[AdminController] 관리자 대시보드 요청");
        try {
            model.addAttribute("totalUsers", userRepository.count());
            model.addAttribute("activeUsers", userRepository.count());
            model.addAttribute("lockedUsers", 0L);
            model.addAttribute("competencyCount", competencyRepository.count());
            model.addAttribute("counselorCount", counselorRepository.count());
            model.addAttribute("notices", java.util.List.of());
            model.addAttribute("todos", java.util.List.of());
            return "admin/dashboard";
        } catch (Exception e) {
            log.error("[AdminController] 관리자 대시보드 로드 실패", e);
            model.addAttribute("errorMessage", "대시보드를 불러올 수 없습니다.");
            return "error";
        }
    }



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

    @GetMapping("/admin/user/{id}")
    public String userDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        log.info("[AdminController] 사용자 상세 정보 요청: {}", id);
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
            model.addAttribute("user", user);

            CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
            model.addAttribute("_csrf", csrfToken);

            return "admin/user-detail";
        } catch (Exception e) {
            log.error("[AdminController] 사용자 상세 정보 로드 실패", e);
            model.addAttribute("errorMessage", "사용자 정보를 불러올 수 없습니다.");
            return "error";
        }
    }

    @PostMapping("/admin/user/{id}/edit")
    public String updateUserStatus(@PathVariable Long id,
                                   @RequestParam("locked") String locked,
                                   Model model) {
        // 1. User 찾기
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // 2. locked 값 변환 및 저장
        user.setLocked(Boolean.parseBoolean(locked)); // "true"→true, "false"→false
        userRepository.save(user);

        // 3. 리다이렉트 또는 결과 페이지로 이동
        return "redirect:/admin/users";
    }
}
