//package com.competency.SCMS.service;
//
//import com.competency.SCMS.domain.user.User;
//import com.competency.SCMS.dto.response.MainUserInfoDTO;
//import com.competency.SCMS.repository.UserRepository;
//import com.competency.SCMS.repository.noncurricular.linkCompetency.CompetencyRepo;
//import com.competency.SCMS.repository.noncurricular.program.ProgramRepo;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@Transactional(readOnly = true)
//public class MainDashboardService {
//
//    private final UserRepository userRepository;
//    private final ProgramRepo programRepo;
//    private final ProgramApplicaitonRepo applicationRepo;
//    private final ConsultationRepo cousultationRepo;
//    private final CompetencyRepo competencyRepo;
//
//    public MainDashboardDTO getMainDashboardData(String userEmail) {
//        log.info("[MainDashboardService] 메인 대시보드 데이터 조회 : userEmail = {}", userEmail);
//
//        try {
//            // 사용자 정보 조회
//            User user = userRepository.findByEmail(userEmail)
//                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//
//            log.debug("[MainDashboardService] 사용자 조회 완료: userId={}", user.getId());
//
//            MainUserInfoDTO userInfo = convertUserToUserInfoDTO(user);
//
//            // 핵심역량 조회 (그래프용)
//            List<CompetencyScoreDTO> competencies = getCompetencyScores(user.getId());
//            log.debug("[MainDashboardService] 역량 데이터: {} 개", competencies.size());
//
//            // 상담이력 조회 (최근 3개)
//            List<ConsultationDTO> consultations = getRecentConsultations(user.getId());
//            log.debug("[MainDashboardService] 상담이력: {} 개", consultations.size());
//
//            // 최신 비교과 프로그램 (3개)
//            List<ProgramDTO> recentPrograms = getRecentPrograms(3);
//            log.debug("[MainDashboardService] 최신 프로그램: {} 개", recentPrograms.size());
//
//            // DTO 조립
//            MainDashboardDTO dashboard = MainDashboardDTO.builder()
//                    .userInfo(userInfo)
//                    .competencies(competencies)
//                    .consultations(consultations)
//                    .recentPrograms(recentPrograms)
//                    .build();
//
//            log.info("[MainDashboardService] 메인 대시보드 데이터 조회 완료");
//            return dashboard;
//
//        } catch (Exception e) {
//            log.error("[MainDashboardService] 메인 대시보드 데이터 조회 실패", e);
//            throw new RuntimeException("메인 대시보드 데이터를 조회할 수 없습니다.", e);
//        }
//    }
//
//    private MainUserInfoDTO convertUserToUserInfoDTO(User user) {
//        log.debug("[MainDashboardService] 사용자 정보 변환: userId={}", user.getId());
//
//        // 비교과 프로그램 신청 수
//        long programCount = applicationRepo.countByUserIdAndCompletionStatusIsNotNull(user.getId());
//
//        // 상담 완료 수
//        long consultationCount = consultationRepo.countByUserIdAndCompletionStatusIsTrue(user.getId());
//
//        // 마일리지 (사용자 엔티티에서 직접 조회 또는 별도 테이블)
//        Integer mileage = user.getMileage() != null ? user.getMileage() : 0;
//
//        return MainUserInfoDTO.builder()
//                .userId(user.getId())
//                .userName(user.getName())
//                .userEmail(user.getEmail())
//                .mileage(mileage)
//                .programCount((int) programCount)
//                .consultationCount((int) consultationCount)
//                .profileImageUrl(user.getProfileImageUrl())
//                .build();
//    }
//
//    private List<CompetencyScoreDTO> getCompetencyScores(Long userId) {
//        log.debug("[MainDashboardService] 역량 점수 조회: userId={}", userId);
//
//        // 실제 구현은 CompetencyScoreRepository에서 조회
//        // SELECT * FROM competency_scores WHERE user_id = ?
//
//        // 임시 목데이터
//        return List.of(
//                CompetencyScoreDTO.builder()
//                        .competencyId(1L)
//                        .competencyName("창의성")
//                        .score(85.0)
//                        .color("#667eea")
//                        .build(),
//                CompetencyScoreDTO.builder()
//                        .competencyId(2L)
//                        .competencyName("의사소통")
//                        .score(92.0)
//                        .color("#764ba2")
//                        .build(),
//                CompetencyScoreDTO.builder()
//                        .competencyId(3L)
//                        .competencyName("리더십")
//                        .score(78.0)
//                        .color("#f093fb")
//                        .build(),
//                CompetencyScoreDTO.builder()
//                        .competencyId(4L)
//                        .competencyName("문제해결")
//                        .score(88.0)
//                        .color("#4facfe")
//                        .build()
//        );
//    }
//
//    private List<ConsultationDTO> getRecentConsultations(Long userId) {
//        log.debug("[MainDashboardService] 상담이력 조회: userId={}", userId);
//
//        // 실제 구현:
//        // return consultationRepo.findByUserIdAndCompletionStatusIsTrueOrderByConsultationDateDesc(userId)
//        //        .stream().limit(3)
//        //        .map(this::convertConsultationToDTO)
//        //        .collect(Collectors.toList());
//
//        // 임시 목데이터
//        return List.of(
//                ConsultationDTO.builder()
//                        .consultationId(1L)
//                        .counselorName("김상담사")
//                        .consultationDate(LocalDateTime.now().minusDays(5))
//                        .topic("진로상담")
//                        .notes("개발 분야 진로 지도")
//                        .build(),
//                ConsultationDTO.builder()
//                        .consultationId(2L)
//                        .counselorName("이상담사")
//                        .consultationDate(LocalDateTime.now().minusDays(10))
//                        .topic("학사상담")
//                        .notes("학점 관리 및 선수과목 안내")
//                        .build()
//        );
//    }
//
//    private List<ProgramDTO> getRecentPrograms(int limit) {
//        log.debug("[MainDashboardService] 최신 프로그램 조회: limit={}", limit);
//
//        // DB에서 최신 프로그램 조회
//        List<Program> programs = programRepo.findLatestPrograms(limit);
//
//        log.debug("[MainDashboardService] 최신 프로그램 조회 완료: {} 개", programs.size());
//
//        // DTO로 변환
//        return programs.stream()
//                .map(this::convertProgramToDTO)
//                .collect(Collectors.toList());
//    }
//
//    private ProgramDTO convertProgramToDTO(Program program) {
//        long participationCount = applicationRepo.countByProgram(program);
//
//        LocalDateTime now = LocalDateTime.now();
//        boolean recruitmentOpen = program.getRecruitStartAt() != null
//                && program.getRecruitEndAt() != null
//                && !now.isBefore(program.getRecruitStartAt())
//                && !now.isAfter(program.getRecruitEndAt());
//
//        Double participationRate = program.getCapacity() != null && program.getCapacity() > 0
//                ? (participationCount * 100.0 / program.getCapacity())
//                : 0.0;
//
//        return ProgramDTO.builder()
//                .id(program.getId())
//                .title(program.getTitle())
//                .summary(program.getSummary())
//                .description(program.getDescription())
//                .imageUrl(program.getImageUrl())
//                .categoryId(program.getCategory().getId())
//                .categoryName(program.getCategory().getName())
//                .categoryColor(program.getCategory().getColor())
//                .status(program.getStatus().getDisplayName())
//                .recruitStartAt(program.getRecruitStartAt())
//                .recruitEndAt(program.getRecruitEndAt())
//                .recruitmentOpen(recruitmentOpen)
//                .programStartAt(program.getProgramStartAt())
//                .programEndAt(program.getProgramEndAt())
//                .capacity(program.getCapacity())
//                .currentParticipants((int) participationCount)
//                .participationRate(participationRate)
//                .build();
//    }
//}
