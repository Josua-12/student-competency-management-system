package com.competency.scms.service.main;

import com.competency.scms.dto.dashboard.CompetencyChartDto;
import com.competency.scms.dto.dashboard.ConsultationHistoryDto;
import com.competency.scms.dto.dashboard.RecentProgramDto;
import com.competency.scms.dto.dashboard.DashboardResponseDto;
import com.competency.scms.domain.user.User;
import com.competency.scms.repository.counseling.CounselingReservationRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import com.competency.scms.repository.user.UserRepository;
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
    private final UserRepository userRepository;

    /**
     * 메인 대시보드 데이터 조회
     */
    public DashboardResponseDto getMainDashboardData(String identifier) {
        log.info("[MainDashboardService] 대시보드 데이터 조회 - identifier: {}", identifier);
        
        try {
            User user;
            
            // 이메일 형식인지 확인
            if (identifier.contains("@")) {
                user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            } else {
                // 이메일 형식이 아닌 경우 로그 찍기
                System.out.println("Identifier is not an email. Value: " + identifier);
                // 학번으로 조회
                try {
                    Integer userNum = Integer.parseInt(identifier);
                    user = userRepository.findByUserNum(userNum)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                } catch (NumberFormatException e) {
                    throw new RuntimeException("잘못된 사용자 식별자입니다.");
                }
            }
            
            log.info("사용자 조회 성공 - userNum: {}, name: {}", user.getUserNum(), user.getName());
            
            // 대시보드 데이터 구성
            CompetencyChartDto competencyChart = getLatestCompetencyChart();
            List<ConsultationHistoryDto> consultations = getRecentConsultations();
            List<RecentProgramDto> programs = getRecentPrograms();
            
            return DashboardResponseDto.builder()
                .userName(user.getName())
                .userNum(user.getUserNum().toString())
                .competencyChart(competencyChart)
                .recentConsultations(consultations)
                .recentPrograms(programs)
                .build();
                
        } catch (Exception e) {
            log.error("[MainDashboardService] 대시보드 데이터 조회 실패 - identifier: {}", identifier, e);
            throw new RuntimeException("대시보드 데이터를 조회할 수 없습니다.", e);
        }
    }

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
        try {
            var reservations = counselingReservationRepository.findAll(PageRequest.of(0, 3));
            
            return reservations.stream()
                .map(reservation -> ConsultationHistoryDto.of(
                    reservation.getId(),
                    reservation.getCounselor() != null ? reservation.getCounselor().getName() : "상담사 미지정",
                    reservation.getReservationDate().atStartOfDay(),
                    reservation.getStatus().name(),
                    reservation.getCounselingField().name()
                ))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("상담 내역 조회 실패", e);
            return List.of();
        }
    }
    
    // 최신 비교과 프로그램 3개 조회
    public List<RecentProgramDto> getRecentPrograms() {
        try {
            var programs = programRepository.findAll(PageRequest.of(0, 3));
            
            return programs.stream()
                .map(program -> RecentProgramDto.of(
                    program.getProgramId(),
                    program.getTitle(),
                    program.getCategory() != null ? program.getCategory().name() : "미분류",
                    program.getRecruitEndAt(),
                    program.getStatus() != null ? program.getStatus().name() : "DRAFT",
                    program.getCurrentParticipants() != null ? program.getCurrentParticipants() : 0,
                    program.getMaxParticipants()
                ))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("비교과 프로그램 조회 실패", e);
            return List.of();
        }
    }
}
