//package com.competency.SCMS.controller;
//
//
//import com.competency.SCMS.repository.user.LoginHistoryRepository;
//import com.competency.SCMS.repository.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//public class AdminController {
//
//    private final UserRepository userRepository;
//    private final LoginHistoryRepository loginHistoryRepository;
//
//    @GetMapping("/admin/dashboard")
//    public String dashboard(Model model) {
//        log.info("[AdminController] 관리자 대시보드 요청");
//
//        try {
//            // 1. 전체 학생 수
//            long studentCount = userRepository.count();
//
//            // 2. 오늘 로그인한 사용자 수
//            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
//            LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
//            long todayLoginCount = loginHistoryRepository.countByLoginAtAfter(todayStart);
//
//            // 3. 이번 달 신규 학생 수
//            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
//            LocalDateTime monthEnd = LocalDate.now().plusDays(1).atStartOfDay();
//            long monthNewStudentCount = userRepository.countByCreatedAtBetween(monthStart, monthEnd);
//
//            model.addAttribute("studentCount", studentCount);
//            model.addAttribute("todayLoginCount", todayLoginCount);
//            model.addAttribute("monthNewStudentCount", monthNewStudentCount);
//
//            log.info("[AdminController] 대시보드 데이터 - 전체: {}, 오늘 로그인: {}, 이달 신규: {}",
//                    studentCount, todayLoginCount, monthNewStudentCount);
//
//            return "admin/dashboard";
//        } catch (Exception e) {
//            log.error("[AdminController] 관리자 대시보드 로드 실패", e);
//            model.addAttribute("errorMessage", "관리자 대시보드를 불러올 수 없습니다.");
//            return "error";
//        }
//    }
//}
