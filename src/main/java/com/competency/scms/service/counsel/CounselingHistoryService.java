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
        List<ReservationStatus> activeStatuses = Arrays.asList(
                ReservationStatus.PENDING, 
                ReservationStatus.CONFIRMED, 
                ReservationStatus.COMPLETED
        );
        
        Page<CounselingReservation> allReservations = reservationRepository.findByCounselorAndStatusIn(
                counselor, activeStatuses, Pageable.unpaged());
        
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
        response.setCounselorName(reservation.getCounselor() != null ? reservation.getCounselor().getName() : null);
        response.setCounselingField(reservation.getCounselingField());
        response.setSubFieldName(reservation.getSubField().getSubfieldName());
        response.setCounselingDate(reservation.getReservationDate());
        response.setStatus(reservation.getStatus());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setCompletedAt(reservation.getCompletedAt());
        return response;
    }
}
