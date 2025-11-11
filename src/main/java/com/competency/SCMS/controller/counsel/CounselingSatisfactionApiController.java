package com.competency.SCMS.controller.counsel;

import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.dto.counsel.CounselingSatisfactionDto;
import com.competency.SCMS.service.counsel.CounselingSatisfactionService;
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
            @AuthenticationPrincipal User currentUser) {
        
        Long satisfactionId = satisfactionService.submitSatisfaction(request, currentUser);
        return ResponseEntity.ok(satisfactionId);
    }

    // 만족도 설문 조회
    @GetMapping("/survey/{reservationId}")
    public ResponseEntity<CounselingSatisfactionDto.SurveyResponse> getSurvey(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal User currentUser) {
        
        CounselingSatisfactionDto.SurveyResponse survey = satisfactionService.getSurvey(reservationId, currentUser);
        return ResponseEntity.ok(survey);
    }
}
