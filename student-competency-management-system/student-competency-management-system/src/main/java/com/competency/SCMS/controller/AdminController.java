package com.competency.SCMS.controller;

import com.competency.SCMS.repository.LoginHistoryRepository;
import com.competency.SCMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        long studentCount = userRepository.count();
        long todayLoginCount = loginHistoryRepository.countByLoginAtAfter(LocalDate.now().atStartOfDay());
        long monthNewStudentCount = userRepository.countByCreatedAtBetween(
                LocalDate.now().withDayOfMonth(1).atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()
        );
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("todayLoginCount", todayLoginCount);
        model.addAttribute("monthNewStudentCount", monthNewStudentCount);
        return "admin/dashboard";
    }
}
