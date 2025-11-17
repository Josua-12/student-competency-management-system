package com.competency.scms.controller.noncurricular.noncurriDashboard;

import com.competency.scms.dto.noncurricular.operation.pending.ProgramPendingListResultDto;
import com.competency.scms.dto.noncurricular.operation.pending.ProgramPendingSearchConditionDto;
import com.competency.scms.service.noncurricular.operation.ProgramApprovalService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/noncurricular")
public class NoncurricularOperatorViewController {

    private ProgramApprovalService programApprovalService;
    /**
     * 공통 프래그먼트 설정
     * baseView: templates/ 이하 경로 (확장자 .html 제외)
     * 예) "noncurricular/noncurriDashboard/operator-dashboard"
     */
    private void setView(Model model, String baseView) {
        model.addAttribute("content", baseView);
    }

    /* =========================
     *  대시보드 (운영자 / 부서관리자 공용)
     * ========================= */

    /**
     * 운영자 / 부서관리자 대시보드
     * GET /noncurricular/operator/dashboard
     * GET /noncurricular/admin/dashboard
     */
    @GetMapping({"/operator/dashboard", "/admin/dashboard"})
    public String operatorDashboard(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 운영/부서 대시보드");
        // templates/noncurricular/noncurriDashboard/operator-dashboard.html
        setView(model, "noncurricular/noncurriDashboard/operator-dashboard");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /* =========================
     *  목록 (프로그램 조회)
     * ========================= */

    /**
     * 프로그램 목록 (운영자 / 부서관리자 공용)
     * GET /noncurricular/operator/programs
     * GET /noncurricular/admin/programs
     */
    @GetMapping({"/operator/programs", "/admin/programs"})
    public String operatorProgramList(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 목록(운영/부서)");
        // templates/noncurricular/program/list_Op.html
        setView(model, "noncurricular/program/list_Op");
        return "noncurricular/fix-screen/noncurricular-layout";
    }


    /* =========================
     *  참여 관리
     * ========================= */

    /**
     * 참가자 관리
     * GET /noncurricular/admin/participants
     */
    @GetMapping("/admin/participants")
    public String manageParticipants(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 참가자 관리");
        // templates/noncurricular/operation/ParticipantMgt.html
        setView(model, "noncurricular/operation/ParticipantMgt");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 만족도 결과 조회
     * GET /noncurricular/admin/satisfaction/results
     */
    @GetMapping("/admin/satisfaction/results")
    public String satisfactionResults(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 만족도 결과 조회");
        // templates/noncurricular/operation/ResultSatisfaction.html
        setView(model, "noncurricular/operation/ResultSatisfaction");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 이수현황 관리
     * GET /noncurricular/admin/completion/status
     * TODO: 대응하는 템플릿 생성 필요 (예: noncurricular/operation/CompletionStatus.html)
     */
    @GetMapping("/admin/completion/status")
    public String completionStatus(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 이수현황 관리");
        setView(model, "noncurricular/operation/CompletionStatus");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 결과보고서 등록
     * GET /noncurricular/admin/reports/register
     * TODO: 대응 템플릿 생성 필요
     */
    @GetMapping("/admin/reports/register")
    public String registerResultReport(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 결과보고서 등록");
        setView(model, "noncurricular/operation/RegisterResultReport");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /* =========================
     *  비교과 포인트 관리
     * ========================= */

    /**
     * 비교과 포인트 조회
     * GET /noncurricular/admin/points
     */
    @GetMapping("/admin/points")
    public String managePoints(Model model) {
        model.addAttribute("pageTitle", "비교과 포인트 조회 (운영자)");
        setView(model, "noncurricular/mileage/operator-points");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 비교과 포인트 등록
     * GET /noncurricular/admin/points/register
     */
    @GetMapping("/admin/points/register")
    public String registerPoints(Model model) {
        model.addAttribute("pageTitle", "비교과 포인트 등록");
        setView(model, "noncurricular/mileage/operator-points-register");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /* =========================
     *  관리
     * ========================= */

    /**
     * 프로그램 일정/분류/재원 관리
     * GET /noncurricular/admin/config/program
     * TODO: 대응 템플릿 생성 필요
     */
    @GetMapping("/admin/config/program")
    public String programConfig(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 프로그램 일정/분류/재원 관리");
        setView(model, "noncurricular/operation/ProgramConfig");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 승인대기 목록 화면
     * GET /noncurricular/admin/programs/pending
     */
    @GetMapping("/pending")
    public String pendingList(@ModelAttribute("searchCondition") ProgramPendingSearchConditionDto condition,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size,
                              Model model) {

        // 기본 정렬: 승인요청일(=updatedAt) 최신순
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        // 기본 상태값: PENDING
        if (condition.getApprovalStatus() == null || condition.getApprovalStatus().isBlank()) {
            condition.setApprovalStatus("PENDING");
        }

        ProgramPendingListResultDto result = programApprovalService.getPendingPrograms(condition, pageable);

        model.addAttribute("result", result);
        model.addAttribute("pageTitle", "비교과 프로그램 승인대기 목록");

        // 아까 만든 HTML 템플릿
        return "noncurricular/admin/pending-list";
    }

    /**
     * 결과보고서 관리
     * GET /noncurricular/admin/reports/manage
     * TODO: 대응 템플릿 생성 필요
     */
    @GetMapping("/admin/reports/manage")
    public String manageReports(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 결과보고서 관리");
        setView(model, "noncurricular/operation/ReportMgt");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /* =========================
     *  알림
     * ========================= */

    /**
     * 신청 승인 이메일 템플릿
     * GET /noncurricular/admin/notifications/approve-email
     * TODO: 대응 템플릿 생성 필요
     */
    @GetMapping("/admin/notifications/approve-email")
    public String approveEmailTemplate(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 신청 승인 이메일");
        setView(model, "noncurricular/operation/NotificationApproveEmail");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    /**
     * 신청 취소 이메일 템플릿
     * GET /noncurricular/admin/notifications/cancel-email
     * TODO: 대응 템플릿 생성 필요
     */
    @GetMapping("/admin/notifications/cancel-email")
    public String cancelEmailTemplate(Model model) {
        model.addAttribute("pageTitle", "비교과 프로그램 - 신청 취소 이메일");
        setView(model, "noncurricular/operation/NotificationCancelEmail");
        return "noncurricular/fix-screen/noncurricular-layout";
    }
}

