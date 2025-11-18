package com.competency.scms.service.counsel;

import com.competency.scms.domain.counseling.*;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingAdminDto;
import com.competency.scms.dto.counsel.CounselingApprovalDto;
import com.competency.scms.dto.counsel.CounselingReservationDto;
import com.competency.scms.repository.counseling.CounselingReservationRepository;
import com.competency.scms.repository.counseling.CounselorRepository;
import com.competency.scms.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingAdminService {

    private final CounselingReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CounselorRepository counselorRepository;

    public CounselingAdminDto.DashboardResponse getDashboardData() {
        // 전체 상담 예약 수
        long totalReservations = reservationRepository.count();
        
        // 승인 대기 수
        long pendingApprovals = reservationRepository.countByStatus(ReservationStatus.PENDING);
        
        // 활성 상담사 수 (COUNSELOR 역할을 가진 활성 사용자)
        long activeCounselors = userRepository.countByRoleAndDeletedAtIsNull(com.competency.scms.domain.user.UserRole.COUNSELOR);
        
        // 평균 만족도 (임시로 4.2 설정, 실제로는 만족도 테이블에서 계산)
        double avgSatisfaction = 4.2;

        return CounselingAdminDto.DashboardResponse.builder()
                .totalReservations(totalReservations)
                .pendingApprovals(pendingApprovals)
                .activeCounselors(activeCounselors)
                .avgSatisfaction(avgSatisfaction)
                .build();
    }

    public Page<CounselingReservationDto.AdminListResponse> getReservationsForApproval(
            String status, String field, Long counselorId, String startDate, String endDate, Pageable pageable) {
        
        return reservationRepository.findAll((root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            
            // 상태 필터
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), ReservationStatus.valueOf(status)));
            }
            
            // 상담 유형 필터
            if (field != null && !field.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("counselingField"), CounselingField.valueOf(field)));
            }
            
            // 상담사 필터
            if (counselorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("counselor").get("id"), counselorId));
            }
            
            // 기간 필터
            if (startDate != null && !startDate.isEmpty()) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"), 
                    java.time.LocalDate.parse(startDate).atStartOfDay()
                ));
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"), 
                    java.time.LocalDate.parse(endDate).atTime(23, 59, 59)
                ));
            }
            
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable).map(this::convertToAdminListResponse);
    }

    @Transactional
    public void approveReservation(Long reservationId, CounselingApprovalDto.AdminApprovalRequest request, User admin) {
        CounselingReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        
        // 상담사 설정
        User counselor = userRepository.findById(request.getCounselorId())
                .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));
        
        reservation.assignCounselor(counselor);
        reservation.approve();
        
        // 확정 일시 설정
        if (request.getConfirmedDate() != null && request.getConfirmedStartTime() != null) {
            LocalDateTime confirmedDateTime = LocalDateTime.of(request.getConfirmedDate(), request.getConfirmedStartTime());
            reservation.setConfirmedDateTime(confirmedDateTime);
        }
        
        reservationRepository.save(reservation);
    }

    @Transactional
    public void rejectReservation(Long reservationId, CounselingApprovalDto.RejectRequest request, User admin) {
        CounselingReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        
        reservation.reject(request.getRejectReason());
        reservationRepository.save(reservation);
    }

    public List<CounselingAdminDto.CounselorOption> getCounselorsByField(String field) {
        List<com.competency.scms.domain.counseling.Counselor> counselors = 
            counselorRepository.findByIsActiveTrueAndDeletedAtIsNull();
        
        return counselors.stream()
                .map(counselor -> {
                    String fieldDisplay = counselor.getCounselingField().getDisplayName();
                    String subfieldDisplay = counselor.getSpecializations().isEmpty() ? 
                        "전체" : 
                        counselor.getSpecializations().stream()
                            .map(sf -> sf.getSubfieldName())
                            .collect(Collectors.joining(", "));
                    
                    return CounselingAdminDto.CounselorOption.builder()
                            .id(counselor.getCounselorId())
                            .name(counselor.getUser().getName())
                            .field(fieldDisplay + " - " + subfieldDisplay)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private CounselingReservationDto.AdminListResponse convertToAdminListResponse(CounselingReservation reservation) {
        return CounselingReservationDto.AdminListResponse.builder()
                .id(reservation.getId())
                .studentName(reservation.getStudent().getName())
                .studentId(reservation.getStudent().getUserNum().toString())
                .counselingType(reservation.getCounselingField().getDisplayName())
                .subFieldName(reservation.getSubField() != null ? reservation.getSubField().getSubfieldName() : "")
                .requestedDateTime(reservation.getReservationDate().atTime(reservation.getStartTime()))
                .confirmedDateTime(reservation.getConfirmedDateTime())
                .createdAt(reservation.getCreatedAt())
                .counselorName(reservation.getCounselor() != null ? reservation.getCounselor().getName() : null)
                .status(reservation.getStatus())
                .requestContent(reservation.getRequestContent())
                .rejectReason(reservation.getRejectReason())
                .build();
    }
}