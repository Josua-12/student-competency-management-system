package com.competency.scms.service.counsel;

import com.competency.scms.domain.counseling.CounselingReservation;
import com.competency.scms.domain.counseling.ReservationStatus;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.dto.counsel.CounselingHistoryDto;
import com.competency.scms.exception.BusinessException;
import com.competency.scms.exception.ErrorCode;
import com.competency.scms.repository.counseling.CounselingReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingHistoryService {

    private final CounselingReservationRepository reservationRepository;
    private final com.competency.scms.repository.counseling.CounselingRecordRepository recordRepository;
    private final com.competency.scms.repository.counseling.CounselingSatisfactionRepository satisfactionRepository;
    private final com.competency.scms.repository.counseling.SatisfactionAnswerRepository satisfactionAnswerRepository;

    // CNSL-015: 전체 상담 이력 조회
    public Page<CounselingHistoryDto.HistoryResponse> getAllHistory(CounselingHistoryDto.SearchCondition condition, 
                                                                      User currentUser, Pageable pageable) {
        if (currentUser.getRole() == UserRole.COUNSELOR) {
            return getCounselorHistory(currentUser, pageable);
        } else if (currentUser.getRole() == UserRole.COUNSELING_ADMIN) {
            return getAdminHistory(condition, pageable);
        } else {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    // CNSL-016: 상담사별 상담 이력 조회
    public Page<CounselingHistoryDto.HistoryResponse> getCounselorHistory(User counselor, Pageable pageable) {
        Page<CounselingReservation> reservations = reservationRepository.findByCounselorOrderByCreatedAtDesc(counselor, pageable);
        return reservations.map(this::toHistoryResponse);
    }

    // CNSL-017: 상담사 본인 담당 상담 현황
    public CounselingHistoryDto.StatusResponse getCounselorStatus(User counselor) {
        List<ReservationStatus> allStatuses = Arrays.asList(
                ReservationStatus.PENDING, 
                ReservationStatus.CONFIRMED, 
                ReservationStatus.COMPLETED,
                ReservationStatus.CANCELLED,
                ReservationStatus.REJECTED
        );
        
        Page<CounselingReservation> allReservations = reservationRepository.findByCounselorAndStatusIn(
                counselor, allStatuses, Pageable.unpaged());
        
        long totalCount = allReservations.getTotalElements();
        long completedCount = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED)
                .count();
        long pendingCount = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .count();
        long cancelledCount = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .count();
        
        CounselingHistoryDto.StatusResponse response = new CounselingHistoryDto.StatusResponse();
        response.setCounselorName(counselor.getName());
        response.setTotalCount(totalCount);
        response.setCompletedCount(completedCount);
        response.setPendingCount(pendingCount);
        response.setCancelledCount(cancelledCount);
        
        // 평균 만족도 계산
        Double avgSatisfaction = calculateAverageSatisfaction(counselor);
        response.setAvgSatisfaction(avgSatisfaction);
        
        return response;
    }

    private Page<CounselingHistoryDto.HistoryResponse> getAdminHistory(CounselingHistoryDto.SearchCondition condition, 
                                                                         Pageable pageable) {
        Page<CounselingReservation> reservations = reservationRepository.findAll(pageable);
        return reservations.map(this::toHistoryResponse);
    }

    private CounselingHistoryDto.HistoryResponse toHistoryResponse(CounselingReservation reservation) {
        CounselingHistoryDto.HistoryResponse response = new CounselingHistoryDto.HistoryResponse();
        response.setId(reservation.getId());
        response.setStudentName(reservation.getStudent().getName());
        response.setStudentId(reservation.getStudent().getUserNum().toString());
        response.setStudentNumber(reservation.getStudent().getUserNum().toString());
        response.setDepartment(reservation.getStudent().getDepartment() != null ? 
                reservation.getStudent().getDepartment().getName() : "-");
        response.setCounselorName(reservation.getCounselor() != null ? reservation.getCounselor().getName() : null);
        response.setCounselingField(reservation.getCounselingField());
        response.setSubFieldName(reservation.getSubField().getSubfieldName());
        response.setCounselingDate(reservation.getReservationDate());
        response.setStatus(reservation.getStatus());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setCompletedAt(reservation.getCompletedAt());
        response.setHasRecord(recordRepository.findByReservationId(reservation.getId()).isPresent());
        
        // 만족도 정보 추가
        boolean hasSatisfaction = satisfactionRepository.findByReservationId(reservation.getId()).isPresent();
        response.setHasSatisfaction(hasSatisfaction);
        response.setSatisfaction(null); // 평균 만족도는 별도 계산 필요
        
        return response;
    }
    
    // 상담사의 평균 만족도 계산
    private Double calculateAverageSatisfaction(User counselor) {
        // 해당 상담사의 완료된 상담 중 만족도가 제출된 것들
        List<com.competency.scms.domain.counseling.CounselingSatisfaction> satisfactions = 
            satisfactionRepository.findByCounselor(counselor);
        
        if (satisfactions.isEmpty()) {
            return 0.0;
        }
        
        double totalAvg = 0.0;
        int validSatisfactionCount = 0;
        
        for (com.competency.scms.domain.counseling.CounselingSatisfaction satisfaction : satisfactions) {
            // 각 만족도 설문에서 RATING 타입 질문들의 평균 계산
            List<com.competency.scms.domain.counseling.SatisfactionAnswer> ratingAnswers = 
                satisfaction.getAnswers().stream()
                    .filter(answer -> answer.getQuestion().getQuestionType() == 
                        com.competency.scms.domain.counseling.SatisfactionQuestion.QuestionType.RATING)
                    .filter(answer -> answer.getRatingValue() != null)
                    .toList();
            
            if (!ratingAnswers.isEmpty()) {
                double satisfactionAvg = ratingAnswers.stream()
                    .mapToInt(com.competency.scms.domain.counseling.SatisfactionAnswer::getRatingValue)
                    .average()
                    .orElse(0.0);
                
                totalAvg += satisfactionAvg;
                validSatisfactionCount++;
            }
        }
        
        return validSatisfactionCount > 0 ? Math.round((totalAvg / validSatisfactionCount) * 10.0) / 10.0 : 0.0;
    }
}
