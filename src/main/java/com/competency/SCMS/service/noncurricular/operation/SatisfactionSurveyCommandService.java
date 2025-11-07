package com.competency.SCMS.service.noncurricular.operation;

import com.competency.SCMS.dto.noncurricular.operation.SatisfactionSurveyResponse;
import com.competency.SCMS.dto.noncurricular.operation.SatisfactionSurveySaveRequest;

public interface SatisfactionSurveyCommandService {
    SatisfactionSurveyResponse save(SatisfactionSurveySaveRequest req);        // DRAFT 저장/수정
    SatisfactionSurveyResponse publish(SatisfactionSurveySaveRequest req);     // 게시 저장
}
