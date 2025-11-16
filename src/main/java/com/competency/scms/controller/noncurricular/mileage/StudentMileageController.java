package com.competency.scms.controller.noncurricular.mileage;

import com.competency.scms.domain.user.User;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.noncurricular.mileage.StudentMileageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/mileage")
public class StudentMileageController {

    private final StudentMileageService studentMileageService;
    private final UserRepository userRepository;

    @GetMapping("/history")
    public Map<String, Object> getHistory() {
        User student = getCurrentUser();
        return studentMileageService.getStudentMileageHistory(student);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("사용자 인증 정보를 찾을 수 없습니다.");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userRepository.findByUserNum(userDetails.getUser().getUserNum())
                    .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));
        }
        throw new IllegalStateException("올바르지 않은 인증 정보입니다.");
    }
}
