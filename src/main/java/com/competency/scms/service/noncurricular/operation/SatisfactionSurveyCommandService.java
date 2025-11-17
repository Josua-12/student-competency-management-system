package com.competency.scms.service.noncurricular.operation;

import com.competency.scms.domain.noncurricular.program.ProgramSchedule;
import com.competency.scms.dto.noncurricular.operation.satisfaction.SatisfactionSurveyResponse;
import com.competency.scms.dto.noncurricular.operation.satisfaction.SatisfactionSurveySaveRequest;
import org.springframework.data.domain.Page;

public interface SatisfactionSurveyCommandService {
    SatisfactionSurveyResponse save(SatisfactionSurveySaveRequest req);        // DRAFT 저장/수정
    SatisfactionSurveyResponse publish(SatisfactionSurveySaveRequest req);     // 게시 저장
    Page<ProgramSchedule> getSchedulesByProgram(Long programId);
}
