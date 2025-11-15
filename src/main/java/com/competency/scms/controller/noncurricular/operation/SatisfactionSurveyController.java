package com.competency.scms.controller.noncurricular.operation;

import com.competency.scms.domain.noncurricular.operation.SurveyStatus;
import com.competency.scms.dto.noncurricular.operation.SatisfactionSurveyResponse;
import com.competency.scms.dto.noncurricular.operation.SatisfactionSurveySaveRequest;
import com.competency.scms.service.noncurricular.operation.SatisfactionSurveyCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noncurricular-operator/programs/{programId}/surveys/satisfaction")
public class SatisfactionSurveyController {

    private final SatisfactionSurveyCommandService commandService;

    @PostMapping
    public ResponseEntity<SatisfactionSurveyResponse> saveDraft(
            @PathVariable Long programId,
            @Valid @RequestBody SatisfactionSurveySaveRequest req
    ){
        // path의 programId를 강제 주입 (보안/정합)
        req.setProgramId(programId);
        if (req.getStatus() == null) req.setStatus(SurveyStatus.DRAFT);
        return ResponseEntity.ok(commandService.save(req));
    }

    @PostMapping("/publish")
    public ResponseEntity<SatisfactionSurveyResponse> publish(
            @PathVariable Long programId,
            @Valid @RequestBody SatisfactionSurveySaveRequest req
    ){
        req.setProgramId(programId);
        return ResponseEntity.ok(commandService.publish(req));
    }
}
