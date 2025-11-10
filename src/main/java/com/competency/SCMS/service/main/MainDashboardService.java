package com.competency.SCMS.service.main;

import com.competency.SCMS.domain.competency.Competency;
import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.dto.competency.CompetencyScoreDto;
import com.competency.SCMS.dto.dashboard.DashboardResponseDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramBasicDto;
import com.competency.SCMS.repository.competency.CompetencyRepository;
import com.competency.SCMS.repository.counseling.CounselorRepository;
import com.competency.SCMS.repository.noncurricular.program.ProgramRepository;
import com.competency.SCMS.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainDashboardService {

    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final CounselorRepository counselorRepository;
    private final CompetencyRepository competencyRepository;

    public DashboardResponseDto getMainDashboardData(String userNum) {
        log.info("[MainDashboardService] 대시보드 데이터 조회 - userNum: {}", userNum);

        try {
            // 1. 사용자 정보 조회
            Integer userNumber = Integer.parseInt(userNum);
            User user = userRepository.findByUserNum(userNumber)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            String userName = user.getName();
            // ✓ 수정: getMileage() → 마일리지 필드 없음 (필요시 추가하거나 0으로 설정)
            Integer mileage = 0;  // User에 mileage 필드 없음

            // 2. 프로그램 정보 조회
            Integer programCount = (int) programRepository.count();
            List<ProgramBasicDto> recentPrograms = Collections.emptyList();

            // 3. 상담 정보 조회
            long counselingCount = counselorRepository.count();

            // 4. 역량 정보 조회
            List<CompetencyScoreDto> competencyScore = competencyRepository.findAll()
                    .stream()
                    .map(this::convertToCompetencyScoreDto)
                    .collect(Collectors.toList());

            return DashboardResponseDto.builder()
                    .userName(userName)
                    .userEmail(user.getEmail())
                    .mileage(mileage)
                    .programCount(programCount)
                    .counselingCount((int) counselingCount)
                    .competencyScore(competencyScore)
                    .recentPrograms(recentPrograms)
                    .build();

        } catch (Exception e) {
            log.error("[MainDashboardService] 대시보드 데이터 조회 실패 - userNum: {}", userNum, e);
            throw new RuntimeException("대시보드 데이터를 조회할 수 없습니다.", e);
        }
    }

    private ProgramBasicDto convertToProgramBasicDto(Program program) {
        return ProgramBasicDto.builder()
                .id(program.getProgramId())  // ✓ 수정: getId() → getProgramId()
                .title(program.getTitle())
                .status(program.getStatus() != null ? program.getStatus().name() : "UNKNOWN")
                .mileage(program.getMileage())
                .location(program.getLocation())
                .build();
    }

    private CompetencyScoreDto convertToCompetencyScoreDto(Competency competency) {
        return CompetencyScoreDto.builder()
                .competencyName(competency.getName())  // ✓ 수정: getCompetencyName() → getName()
                .score(0.0)  // ✓ 수정: getScore() → Competency에 score 필드 없음
                .build();
    }
}
