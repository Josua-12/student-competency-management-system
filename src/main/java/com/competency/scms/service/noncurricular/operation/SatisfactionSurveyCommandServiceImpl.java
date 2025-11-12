package com.competency.scms.service.noncurricular.operation;


import com.competency.scms.domain.noncurricular.operation.*;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.program.ProgramSchedule;
import com.competency.scms.dto.noncurricular.operation.SatisfactionSurveyResponse;
import com.competency.scms.dto.noncurricular.operation.SatisfactionSurveySaveRequest;
import com.competency.scms.repository.noncurricular.operation.SatisfactionSurveyRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import com.competency.scms.repository.noncurricular.program.ProgramScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SatisfactionSurveyCommandServiceImpl implements SatisfactionSurveyCommandService {

    private final ProgramRepository programRepository;
    private final ProgramScheduleRepository scheduleRepository;
    private final SatisfactionSurveyRepository surveyRepository;
    private final ProgramScheduleRepository programScheduleRepository;

    @Override
    public SatisfactionSurveyResponse save(SatisfactionSurveySaveRequest req) {
        return upsert(req, req.getStatus()); // 보통 DRAFT
    }

    @Override
    public SatisfactionSurveyResponse publish(SatisfactionSurveySaveRequest req) {
        return upsert(req, SurveyStatus.PUBLISHED);
    }

    private SatisfactionSurveyResponse upsert(SatisfactionSurveySaveRequest req, SurveyStatus status) {
        Program program = programRepository.findById(req.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + req.getProgramId()));

        SatisfactionSurvey survey = SatisfactionSurvey.builder()
                .program(program)
                .title(req.getTitle())
                .openStart(req.getOpenStart())
                .openEnd(req.getOpenEnd())
                .anonymous(req.isAnonymous())
                .requiredToComplete(req.isRequiredToComplete())
                .status(status)
                .build();

        // 회차 매핑
        if (req.getScheduleIds() != null && !req.getScheduleIds().isEmpty()) {
            List<ProgramSchedule> schedules = scheduleRepository.findAllById(req.getScheduleIds());
            survey.setSchedules(schedules);
        }

        // 문항 매핑
        List<SurveyQuestion> qList = new ArrayList<>();
        req.getQuestions().forEach(q -> {
            SurveyQuestion sq = SurveyQuestion.builder()
                    .survey(survey)
                    .orderNo(q.getOrder())
                    .type(q.getType())
                    .title(q.getTitle())
                    .required(q.isRequired())
                    .scale(q.getType() == QuestionType.RATING ? (q.getScale() == null ? 5 : q.getScale()) : null)
                    .build();

            if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                List<SurveyOption> options = new ArrayList<>();
                q.getOptions().forEach(o -> {
                    options.add(SurveyOption.builder()
                            .question(sq)
                            .orderNo(o.getOrder())
                            .text(o.getText())
                            .build());
                });
                sq.setOptions(options);
            }
            qList.add(sq);
        });
        survey.setQuestions(qList);

        SatisfactionSurvey saved = surveyRepository.save(survey);
        return toResponse(saved);
    }

    private SatisfactionSurveyResponse toResponse(SatisfactionSurvey s) {
        return SatisfactionSurveyResponse.builder()
                .surveyId(s.getId())
                .programId(s.getProgram().getProgramId())
                .scheduleIds(s.getSchedules().stream().map(ProgramSchedule::getScheduleId).toList())
                .title(s.getTitle())
                .openStart(s.getOpenStart())
                .openEnd(s.getOpenEnd())
                .anonymous(s.isAnonymous())
                .requiredToComplete(s.isRequiredToComplete())
                .status(s.getStatus())
                .items(s.getQuestions().stream().map(q ->
                        SatisfactionSurveyResponse.Item.builder()
                                .questionId(q.getId())
                                .order(q.getOrderNo())
                                .type(q.getType().name())
                                .title(q.getTitle())
                                .required(q.isRequired())
                                .options(q.getOptions().stream().map(o ->
                                        SatisfactionSurveyResponse.Opt.builder()
                                                .optionId(o.getId())
                                                .order(o.getOrderNo())
                                                .text(o.getText())
                                                .build()
                                ).toList())
                                .build()
                ).toList())
                .build();
    }

    @Override
    public Page<ProgramSchedule> getSchedulesByProgram(Long programId) {
        Pageable pageable = PageRequest.of(
                0, 10,
                Sort.by("date").ascending().and(Sort.by("startTime").ascending())
        );

        // 인스턴스로 호출
        Page<ProgramSchedule> page =
                programScheduleRepository.findByProgram_ProgramId(programId, pageable);

        return page;
    }
}
