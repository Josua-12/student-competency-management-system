package com.competency.scms.service.noncurricular.noncurriDashboard;

import com.competency.scms.domain.noncurricular.operation.ApplicationStatus;
import com.competency.scms.dto.dashboard.*;
import com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentCompetencyDto;
import com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentDashboardResponse;
import com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentRecommendationDto;
import com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentSummaryDto;
import com.competency.scms.repository.competency.AssessmentResultRepository;
import com.competency.scms.repository.competency.AssessmentScoreRepository;
import com.competency.scms.repository.noncurricular.mileage.MileageRecordRepository;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentDashboardServiceImpl implements StudentDashboardService {

    private final ProgramApplicationRepository applicationRepository;
    private final MileageRecordRepository mileageRecordRepository;
    private final AssessmentResultRepository assessmentResultRepository;
    private final ProgramRepository programRepository;

    @Override
    public StudentDashboardResponse getDashboard(Long studentId) {

        // 요약
        long totalApplied = applicationRepository.countByStudentId(studentId);
        long active = applicationRepository.countByStudentIdAndStatusIn(
                studentId,
                List.of(ApplicationStatus.APPROVED, ApplicationStatus.PENDING)
        );
        long completed = applicationRepository.countByStudentIdAndCompletedTrue(studentId);
        long mileage = mileageRecordRepository.sumPointsByStudent(studentId);

        var summary = StudentSummaryDto.builder()
                .totalApplied(totalApplied)
                .active(active)
                .completed(completed)
                .mileage(mileage)
                .build();

        // 최근 신청 3건
        var latestApps = applicationRepository.findLatestApplications(studentId, PageRequest.of(0, 3));

        // 추천 프로그램 (간단: 모집중 상위 3개)
        var recommendPrograms = programRepository.findRecommendablePrograms(PageRequest.of(0, 3));
        var recommendations = recommendPrograms.stream()
                .map(p -> StudentRecommendationDto.builder()
                        .programId(p.getProgramId())
                        .title(p.getTitle())
                        .category(p.getCategory().getLabel())
                        .periodText(/* 기간 문자열 조합 */ "")
                        .reason("관심/역량 기반 추천") // TODO: 실제 추천 로직에 맞게 수정
                        .build())
                .toList();

        // 역량 진단
        StudentCompetencyDto competencyDto = null;
        var latestResultList = assessmentResultRepository.findLatestResult(studentId, PageRequest.of(0, 1));
        if (!latestResultList.isEmpty()) {
            var latestResult = latestResultList.get(0);
            var scores = assessmentResultRepository.findScoresByResultId(latestResult.getId());
            competencyDto = StudentCompetencyDto.builder()
                    .lastAssessmentDate(
                            latestResult.getSubmittedAt() != null
                                    ? latestResult.getSubmittedAt().toLocalDate().toString()
                                    : null
                    )
                    .scores(scores)
                    .build();
        }

        return StudentDashboardResponse.builder()
                .lastUpdated(LocalDateTime.now())
                .summary(summary)
                .latestApplications(latestApps)
                .recommendations(recommendations)
                .competency(competencyDto)
                .build();
    }
}
