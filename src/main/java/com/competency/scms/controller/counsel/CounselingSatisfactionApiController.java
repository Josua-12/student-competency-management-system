package com.competency.scms.controller.counsel;

import com.competency.scms.dto.counsel.CounselingSatisfactionDto;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.counsel.CounselingSatisfactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counseling/satisfaction")
@RequiredArgsConstructor
public class CounselingSatisfactionApiController {

    private final CounselingSatisfactionService satisfactionService;

    // CNSL-005: 상담만족도 제출
    @PostMapping
    public ResponseEntity<Long> submitSatisfaction(
            @Valid @RequestBody CounselingSatisfactionDto.SubmitRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long satisfactionId = satisfactionService.submitSatisfaction(request, userDetails.getUser());
        return ResponseEntity.ok(satisfactionId);
    }

    // 만족도 설문 조회
    @GetMapping("/survey/{reservationId}")
    public ResponseEntity<CounselingSatisfactionDto.SurveyResponse> getSurvey(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CounselingSatisfactionDto.SurveyResponse survey = satisfactionService.getSurvey(reservationId, userDetails.getUser());
        return ResponseEntity.ok(survey);
    }

    // 제출된 만족도 조회
    @GetMapping("/result/{reservationId}")
    public ResponseEntity<CounselingSatisfactionDto.ResultResponse> getResult(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CounselingSatisfactionDto.ResultResponse result = satisfactionService.getResult(reservationId, userDetails.getUser());
        return ResponseEntity.ok(result);
    }

    // 만족도 수정
    @PutMapping("/{satisfactionId}")
    public ResponseEntity<Void> updateSatisfaction(
            @PathVariable Long satisfactionId,
            @Valid @RequestBody CounselingSatisfactionDto.SubmitRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        satisfactionService.updateSatisfaction(satisfactionId, request, userDetails.getUser());
        return ResponseEntity.ok().build();
    }
    
    // 상담사 만족도 요약
    @GetMapping("/counselor/summary")
    public ResponseEntity<CounselingSatisfactionDto.SummaryResponse> getCounselorSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CounselingSatisfactionDto.SummaryResponse summary = satisfactionService.getCounselorSummary(userDetails.getUser());
        return ResponseEntity.ok(summary);
    }
    
    // 상담사 만족도 분포
    @GetMapping("/counselor/distribution")
    public ResponseEntity<CounselingSatisfactionDto.DistributionResponse> getCounselorDistribution(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CounselingSatisfactionDto.DistributionResponse distribution = satisfactionService.getCounselorDistribution(userDetails.getUser());
        return ResponseEntity.ok(distribution);
    }
    
    // 상담사 월별 만족도 추이
    @GetMapping("/counselor/monthly")
    public ResponseEntity<CounselingSatisfactionDto.MonthlyResponse> getCounselorMonthly(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CounselingSatisfactionDto.MonthlyResponse monthly = satisfactionService.getCounselorMonthly(userDetails.getUser());
        return ResponseEntity.ok(monthly);
    }
}
