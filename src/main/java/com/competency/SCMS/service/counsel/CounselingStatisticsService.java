package com.competency.SCMS.service.counsel;

import com.competency.SCMS.domain.counseling.CounselingField;
import com.competency.SCMS.domain.counseling.ReservationStatus;
import com.competency.SCMS.dto.counsel.CounselingStatisticsDto;
import com.competency.SCMS.repository.counseling.CounselingReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingStatisticsService {

    private final CounselingReservationRepository reservationRepository;

    // CNSL-018: 상담 유형별 통계
    public CounselingStatisticsDto.TypeStatistics getTypeStatistics() {
        List<Object[]> results = reservationRepository.countByStatusGroupByCounselingField(ReservationStatus.COMPLETED);
        
        Map<CounselingField, Long> statistics = new HashMap<>();
        for (Object[] result : results) {
            CounselingField field = (CounselingField) result[0];
            Long count = (Long) result[1];
            statistics.put(field, count);
        }
        
        CounselingStatisticsDto.TypeStatistics response = new CounselingStatisticsDto.TypeStatistics();
        response.setCareerCount(statistics.getOrDefault(CounselingField.CAREER, 0L));
        response.setAcademicCount(statistics.getOrDefault(CounselingField.ACADEMIC, 0L));
        response.setPsychologicalCount(statistics.getOrDefault(CounselingField.PSYCHOLOGICAL, 0L));
        response.setEmploymentCount(statistics.getOrDefault(CounselingField.EMPLOYMENT, 0L));
        
        return response;
    }

    // CNSL-019: 전체 상담 현황/이력 통계
    public CounselingStatisticsDto.OverallStatistics getOverallStatistics() {
        List<Object[]> results = reservationRepository.countGroupByStatus();
        
        Map<ReservationStatus, Long> statistics = new HashMap<>();
        for (Object[] result : results) {
            ReservationStatus status = (ReservationStatus) result[0];
            Long count = (Long) result[1];
            statistics.put(status, count);
        }
        
        CounselingStatisticsDto.OverallStatistics response = new CounselingStatisticsDto.OverallStatistics();
        response.setTotalCount(statistics.values().stream().mapToLong(Long::longValue).sum());
        response.setPendingCount(statistics.getOrDefault(ReservationStatus.PENDING, 0L));
        response.setConfirmedCount(statistics.getOrDefault(ReservationStatus.CONFIRMED, 0L));
        response.setCompletedCount(statistics.getOrDefault(ReservationStatus.COMPLETED, 0L));
        response.setCancelledCount(statistics.getOrDefault(ReservationStatus.CANCELLED, 0L));
        response.setRejectedCount(statistics.getOrDefault(ReservationStatus.REJECTED, 0L));
        
        return response;
    }

    // CNSL-020: 상담만족도 결과 조회 (상담사별)
    public List<CounselingStatisticsDto.SatisfactionResult> getSatisfactionResults() {
        // 구현 필요: 상담사별 만족도 평균 계산
        return List.of();
    }

    // CNSL-021: 상담원별 현황
    public List<CounselingStatisticsDto.CounselorStatistics> getCounselorStatistics() {
        // 구현 필요: 상담사별 상담 현황 통계
        return List.of();
    }
}
