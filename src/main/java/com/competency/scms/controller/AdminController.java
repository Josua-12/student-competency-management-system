package com.competency.scms.controller;

import com.competency.scms.domain.user.User;
import com.competency.scms.repository.counseling.CounselorRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import com.competency.scms.repository.user.LoginHistoryRepository;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.repository.competency.CompetencyRepository;
import com.competency.scms.repository.counsel.CounselRepository;
import com.competency.scms.repository.program.ProgramRepository;
import com.competency.scms.entity.User;
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

    // 각 담당 영역 Repository
    private final CompetencyRepository competencyRepository;
    private final CounselorRepository counselorRepository;
    private final ProgramRepository programRepository;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("[AdminController] 관리자 대시보드 요청");
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

            // 공통 데이터(전체 학생 수, 오늘 로그인 수, 이번 달 신규 학생 수)
            long studentCount = userRepository.count();
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            long todayLoginCount = loginHistoryRepository.countByLoginAtAfter(todayStart);
            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            long monthNewStudentCount = userRepository.countByCreatedAtBetween(monthStart, LocalDateTime.now());

            model.addAttribute("studentCount", studentCount);
            model.addAttribute("todayLoginCount", todayLoginCount);
            model.addAttribute("monthNewStudentCount", monthNewStudentCount);

            // 담당자 역할 확인
            String role = user.getRole();

            if ("ADMIN".equals(role)) {
                // 역량진단 담당: 본인이 등록한 역량진단 결과만 조회
                model.addAttribute("competencyList", competencyRepository.findAllByAdminId(user.getId()));
            } else if ("COUNSELOR".equals(role)) {
                // 상담 담당: 본인이 등록한 상담 정보만 조회
                model.addAttribute("counselList", counselorRepository.findAllByCounselorId(user.getId()));
            } else if ("OPERATOR".equals(role)) {
                // 비교과프로그램 담당: 본인이 담당하는 프로그램만 조회
                model.addAttribute("programList", programRepository.findAllByOperatorId(user.getId()));
            }

            log.info("[AdminController] 대시보드 데이터 - 학생수 {}, 오늘 로그인 {}, 이달 신규 {}, 역할 {}", studentCount, todayLoginCount, monthNewStudentCount, role);
            return "admin/dashboard";
        } catch (Exception e) {
            log.error("[AdminController] 관리자 대시보드 로드 실패", e);
            model.addAttribute("errorMessage", "관리자 대시보드를 불러올 수 없습니다.");
            return "error";
        }
    }
}
