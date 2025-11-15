package com.competency.scms.controller;

import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.repository.counseling.CounselorRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import com.competency.scms.repository.user.LoginHistoryRepository;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.repository.competency.CompetencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final CompetencyRepository competencyRepository;
    private final CounselorRepository counselorRepository;
    private final ProgramRepository programRepository;

    // 최고 관리자 대시보드
    @GetMapping("/admin/dashboard")
    public String superAdminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("[AdminController] 최고 관리자 대시보드 요청");
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

            // 전체 시스템 통계
            long studentCount = userRepository.countByRole(UserRole.STUDENT);
            long counselorCount = userRepository.countByRole(UserRole.COUNSELOR);
            long adminCount = userRepository.count() - studentCount - counselorCount;

            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            long todayLoginCount = loginHistoryRepository.countByLoginAtAfter(todayStart);

            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            long monthNewStudentCount = userRepository.countByCreatedAtBetween(monthStart, LocalDateTime.now());

            model.addAttribute("studentCount", studentCount);
            model.addAttribute("counselorCount", counselorCount);
            model.addAttribute("adminCount", adminCount);
            model.addAttribute("todayLoginCount", todayLoginCount);
            model.addAttribute("monthNewStudentCount", monthNewStudentCount);

            // 전체 시스템 데이터 (최고 관리자는 모든 데이터 접근 가능)
            model.addAttribute("totalCompetencies", competencyRepository.count());
            model.addAttribute("totalPrograms", programRepository.count());
            model.addAttribute("activeCounselors", counselorRepository.countByIsActive(true));

            log.info("[AdminController] 최고 관리자 대시보드 데이터 로드 완료");
            return "admin/super-admin-main";
        } catch (Exception e) {
            log.error("[AdminController] 최고 관리자 대시보드 로드 실패", e);
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

    // 비교과 관리자 대시보드
    @GetMapping("/noncurricular/admin/dashboard")
    public String noncurricularAdminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("[AdminController] 비교과 관리자 대시보드 요청");
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

            // 비교과 프로그램 관련 통계만
            model.addAttribute("programList", programRepository.findAll());
            model.addAttribute("totalPrograms", programRepository.count());

            return "noncurricular/admin/noncurricular-admin-main";
        } catch (Exception e) {
            log.error("[AdminController] 비교과 관리자 대시보드 로드 실패", e);
            model.addAttribute("errorMessage", "대시보드를 불러올 수 없습니다.");
            return "error";
        }
    }
}
