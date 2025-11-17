package com.competency.scms.controller.noncurricular.mileage;

import com.competency.scms.domain.user.User;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.noncurricular.mileage.StudentMileageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student")
public class StudentMileageController {

    private final StudentMileageService studentMileageService;

    @GetMapping("/mileage/history")
    public Map<String, Object> getHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User student = userDetails.getUser();
        return studentMileageService.getStudentMileageHistory(student);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User student = userDetails.getUser();
        return studentMileageService.getStudentMileageHistory(student);
    }
}
