package com.competency.scms.service.main;

import com.competency.scms.dto.dashboard.CompetencyChartDto;
import com.competency.scms.dto.dashboard.ConsultationHistoryDto;
import com.competency.scms.dto.dashboard.RecentProgramDto;
import com.competency.scms.repository.counseling.CounselingReservationRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final CounselingReservationRepository counselingReservationRepository;
    private final ProgramRepository programRepository;
    private final com.competency.scms.repository.competency.CompetencyRepository competencyRepository;

    // 핵심역량 최신 검사 결과 조회
    public CompetencyChartDto getLatestCompetencyChart() {
        var competencies = competencyRepository.findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAsc();
        
        if (competencies.isEmpty()) {
            return CompetencyChartDto.of(
                List.of("자기관리", "의사소통", "글로벌", "대인관계", "사고력", "기술활용"),
                List.of(4.2, 3.8, 4.0, 3.5, 4.1, 3.9)
            );
        }
        
        List<String> labels = competencies.stream()
            .map(comp -> comp.getName().replace("역량", "").replace(" ", ""))
            .collect(Collectors.toList());
        
        // 임시 점수 (실제로는 AssessmentResult에서 가져와야 함)
        List<Double> scores = competencies.stream()
            .map(comp -> 3.5 + Math.random() * 1.5) // 3.5~5.0 사이 랜덤 점수
            .collect(Collectors.toList());
        
        return CompetencyChartDto.of(labels, scores);
    }
    
    // 상담 내역 조회 (최근 3건)
    public List<ConsultationHistoryDto> getRecentConsultations() {
        var reservations = counselingReservationRepository.findAll(PageRequest.of(0, 3));
        
        return reservations.stream()
            .map(reservation -> ConsultationHistoryDto.of(
                reservation.getId(),
                reservation.getCounselor().getName(),
                reservation.getReservationDate().atStartOfDay(),
                reservation.getStatus().name(),
                reservation.getCounselingField().name()
            ))
            .collect(Collectors.toList());
    }
    
    // 최신 비교과 프로그램 3개 조회
    public List<RecentProgramDto> getRecentPrograms() {
        var programs = programRepository.findAll(PageRequest.of(0, 3));
        
        return programs.stream()
            .map(program -> RecentProgramDto.of(
                program.getProgramId(),
                program.getTitle(),
                program.getCategory().name(),
                program.getRecruitEndAt(),
                program.getStatus().name(),
                program.getCurrentParticipants() != null ? program.getCurrentParticipants() : 0,
                program.getMaxParticipants()
            ))
            .collect(Collectors.toList());
    }
}