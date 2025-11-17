package com.competency.scms.controller.noncurricular.noncurriDashboard;

import com.competency.scms.domain.noncurricular.program.ProgramCategoryType;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationListDto;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationListResultDto;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationSearchConditionDto;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationSummaryDto;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.noncurricular.operation.ProgramApplicationService;
import com.competency.scms.service.noncurricular.operation.StudentApplicationQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/noncurricular/student")
public class NoncurricularStudentViewController {

    private ProgramApplicationService programApplicationService;
    private StudentApplicationQueryService studentApplicationQueryService;

    // 이 컨트롤러의 모든 요청에서 model에 "search"를 기본으로 깔아줌
    @ModelAttribute("search")
    public StudentApplicationSearchConditionDto initSearch() {
        return new StudentApplicationSearchConditionDto();
    }


    /**
     * 공통으로 프래그먼트 모델 설정
     * baseView: templates/ 이하의 경로 (확장자 .html 제외)
     * 예: "noncurricular/noncurriDashboard/student-dashboard"
     */
    private void setView(Model model, String baseView) {
        model.addAttribute("content", baseView);
    }

    /**
     * 학생 대시보드
     * GET /noncurricular/student/student-dashboard
     */
    @GetMapping("/student-dashboard")
    public String studentDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 학생 대시보드");
//        model.addAttribute("content", "noncurricular/noncurriDashboard/student-dashboard");
        setView(model, "noncurricular/noncurriDashboard/student-dashboard");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 프로그램 목록(학생)
     * GET /noncurricular/student/programs
     */
    @GetMapping("/program-list")
    public String studentProgramList(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 목록(학생)");
        setView(model, "noncurricular/program/list_User");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 신청/취소
     * GET /noncurricular/student/applications/history
     */
    @GetMapping("/applications/history")
    public String applications(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 신청/취소");
        setView(model, "noncurricular/operation/ResultApplicationHistory");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 신청 이력 조회
     * GET /noncurricular/student/applications/list
     */
    @GetMapping("/applications/list")
    public String applicationList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute("search") StudentApplicationSearchConditionDto search,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        // 1) 로그인한 학생 ID 가져오기
        Long studentId = userDetails.getUser().getId();      // 또는 getUserNum() 등 프로젝트 정의에 맞게

        // 2) 서비스 호출
        StudentApplicationListResultDto result =
                studentApplicationQueryService.getStudentApplications(studentId, search, pageable);

        // 3) 모델 바인딩
        model.addAttribute("search", search);
        model.addAttribute("summary", result.getSummary());
        model.addAttribute("applications", result.getApplications());
        model.addAttribute("page", result.getApplications());
        model.addAttribute("pageTitle", "비교과 프로그램 - 신청 이력 조회");

        setView(model, "noncurricular/operation/ResultApplicationHistory");

        return "noncurricular/application/application-list_student";
    }


    /**
     * 만족도 설문
     * GET /noncurricular/student/satisfaction
     */
    @GetMapping("/satisfaction")
    public String satisfaction(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 만족도 설문");
        
        // 더미 데이터 추가
        java.util.Map<String, Object> program = new java.util.HashMap<>();
        program.put("title", "취업 역량 강화를 위한 모의 면접 캐프");
        model.addAttribute("program", program);
        
        setView(model, "noncurricular/operation/ResponseSatisfactionSurvey");
        return "noncurricular/fix-screen/noncurricular-layout";
    }


    /**
     * 비교과 포인트 조회
     * GET /noncurricular/student/points
     * (일단 학생 대시보드 화면 재사용)
     */
    @GetMapping("/points")
    public String points(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 포인트 조회");
        setView(model, "noncurricular/mileage/student-points");
        return "noncurricular/fix-screen/noncurricular-layout";
    }


    /**
     * 결과보고서 등록
     * GET /noncurricular/student/reports
     */
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 결과보고서 등록");
        setView(model, "noncurricular/report/RegisterResultsReport");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 이수내역 조회
     * GET /noncurricular/student/completions
     */
    @GetMapping("/completions")
    public String completions(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 이수내역 조회");
        setView(model, "noncurricular/operation/ResultCompletionHistory");
        return "noncurricular/fix-screen/noncurricular-layout";
    }


}

