package com.competency.scms.controller.noncurricular.noncurriDashboard;

import com.competency.scms.domain.user.UserRole;
import com.competency.scms.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/noncurricular")
public class NoncurricularEntryController {

    @GetMapping("/dashboard")
    public String redirectDashboardByRole(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            return "redirect:/auth/login";
        }

        UserRole role = customUserDetails.getUser().getRole();
        if (role == null) {
            return "redirect:/auth/login";
        }

        switch (role) {
            case STUDENT:
                return "redirect:/noncurricular/student/student-dashboard";

            case NONCURRICULAR_OPERATOR:
                return "redirect:/noncurricular/operator/dashboard";

            case NONCURRICULAR_ADMIN:
            case SUPER_ADMIN:
                return "redirect:/noncurricular/admin/dashboard";

            default:
                return "redirect:/auth/login";
        }
    }
}
