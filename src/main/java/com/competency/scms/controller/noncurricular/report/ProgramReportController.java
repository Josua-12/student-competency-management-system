package com.competency.scms.controller.noncurricular.report;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.noncurricular.report.*;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.noncurricular.report.ProgramReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OP-007 결과보고서 등록
 * HTML의 API 설정:
 *   form:   /ops/reports/form
 *   stats:  /ops/reports/stats
 *   save:   /ops/reports
 *   submit: /ops/reports/submit
 *   upload: /ops/reports/{reportId}/files
 *   export: /ops/reports/export
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/noncurricular-operator/reports")
public class ProgramReportController {

    private final ProgramReportService reportService;
    private final UserRepository userRepository;

    /** 운영자 탭 - 폼 + 기존 데이터 조회 */
    @GetMapping("/form")
    public OperatorReportFormResponseDto loadForm(
            @RequestParam("progId") Long programId,
            @RequestParam(value = "schdId", required = false) Long scheduleId
    ) {
        User current = getCurrentUser();
        return reportService.loadOperatorForm(programId, scheduleId, current);
    }

    /** 운영자 탭 - KPI / 통계 조회 */
    @GetMapping("/stats")
    public OperatorReportStatsResponseDto loadStats(
            @RequestParam("progId") Long programId,
            @RequestParam(value = "schdId", required = false) Long scheduleId
    ) {
        return reportService.loadStats(programId, scheduleId);
    }

    /** 운영자 탭 - 임시저장 */
    @PostMapping
    public OperatorReportResponseDto save(@RequestBody OperatorReportSaveRequestDto req) {
        User current = getCurrentUser();
        return reportService.saveOperatorReport(req, current);
    }

    /** 운영자 탭 - 제출(승인요청) */
    @PostMapping("/submit")
    public OperatorReportResponseDto submit(@RequestBody OperatorReportSaveRequestDto req) {
        User current = getCurrentUser();
        return reportService.submitOperatorReport(req, current);
    }

    /** 첨부파일 업로드(증빙/사진) - 간단 stub */
    @PostMapping("/{reportId}/files")
    public ResponseEntity<Void> uploadFiles(
            @PathVariable Long reportId,
            @RequestPart(value = "docs", required = false) List<MultipartFile> docs,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        // TODO: ProgramReport.attachmentPath 또는 File 엔티티와 연동해서 저장
        return ResponseEntity.ok().build();
    }

    /** 엑셀 다운로드 - stub */
    @GetMapping("/export")
    public ResponseEntity<Void> export(
            @RequestParam("progId") Long programId,
            @RequestParam(value = "schdId", required = false) Long scheduleId
    ) {
        // TODO: 엑셀 생성 후 ResponseEntity<Resource>로 교체
        return ResponseEntity.ok().build();
    }

    // ===== 학생 탭 =====

    @GetMapping("/student/targets")
    public List<StudentReportListItemDto> studentTargets() {
        User current = getCurrentUser();
        return reportService.findStudentTargets(current.getId(), null);
    }

    @PostMapping("/student")
    public StudentReportResponseDto saveStudent(@RequestBody StudentReportSaveRequestDto req) {
        User current = getCurrentUser();
        return reportService.saveStudentReport(req, current);
    }

    @PostMapping("/student/submit")
    public StudentReportResponseDto submitStudent(@RequestBody StudentReportSaveRequestDto req) {
        User current = getCurrentUser();
        return reportService.submitStudentReport(req, current);
    }

    // ===== 공통: 현재 로그인 사용자 조회 =====

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

