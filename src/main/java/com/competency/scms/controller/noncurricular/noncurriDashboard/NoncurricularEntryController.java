package com.competency.scms.controller.noncurricular.noncurriDashboard;

import com.competency.scms.domain.user.UserRole;
import com.competency.scms.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/noncurricular")
public class NoncurricularEntryController {

    @GetMapping("/dashboard")
    public String redirectDashboardByRole(Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserRole role = userDetails.getUser().getRole(); // STUDENT / OPERATOR / DEPARTMENT_ADMIN ...

        switch (role) {
            case STUDENT:
                return "redirect:/noncurricular/noncurriDashboard/student-dashboard";

            case OPERATOR:
                return "redirect:/noncurricular/noncurriDashboard/operator-dashboard";

            case ADMIN:
                return "redirect:/noncurricular/noncurriDashboard/operator-dashboard";

            default:
                return "redirect:/login";   // 혹시 모를 예외 처리
        }

    }
}

