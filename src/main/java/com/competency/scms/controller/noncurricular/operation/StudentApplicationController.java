package com.competency.scms.controller.noncurricular.operation;

import com.competency.scms.domain.noncurricular.program.ProgramCategoryType;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationListResultDto;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationSearchConditionDto;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.noncurricular.operation.StudentApplicationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/noncurricular/student-app")
public class StudentApplicationController {

    private final StudentApplicationQueryService studentApplicationQueryService;

    /**
     * 비교과 프로그램 신청내역 조회 (학생)
     * GET /noncurricular/student/applications
     */
    @GetMapping("/applications")
    public String applicationList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  StudentApplicationSearchConditionDto search,
                                  @PageableDefault(size = 10, sort = "appliedAt") Pageable pageable,
                                  Model model) {

        Long studentId = userDetails.getUser().getId();

        StudentApplicationListResultDto result =
                studentApplicationQueryService.getStudentApplications(studentId, search, pageable);

        model.addAttribute("search", search);
        // 분류 드롭다운용 (HTML에서 th:value="${cat.name()}", th:text="${cat.label}" 로 쓰면 됨)
        model.addAttribute("categories", ProgramCategoryType.values());
        model.addAttribute("summary", result.getSummary());
        model.addAttribute("applications", result.getApplications());
        model.addAttribute("page", pageable);

        // 하은님이 만든 HTML 경로
        return "noncurricular/application/application-list_student";
    }
}

