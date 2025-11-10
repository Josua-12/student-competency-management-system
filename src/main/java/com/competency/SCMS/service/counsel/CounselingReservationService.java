package com.competency.SCMS.service.counsel;

import com.competency.SCMS.domain.counseling.CounselingReservation;
import com.competency.SCMS.domain.counseling.CounselingSubField;
import com.competency.SCMS.domain.counseling.ReservationStatus;
import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.domain.user.UserRole;
import com.competency.SCMS.dto.counsel.CounselingReservationDto;
import com.competency.SCMS.exception.BusinessException;
import com.competency.SCMS.exception.ErrorCode;
import com.competency.SCMS.repository.user.UserRepository;
import com.competency.SCMS.repository.counseling.CounselingCategoryRepository;
import com.competency.SCMS.repository.counseling.CounselingReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingReservationService {

    private final CounselingReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CounselingCategoryRepository categoryRepository;

    // CNSL-001: 상담 예약 등록
    @Transactional
    public Long createReservation(CounselingReservationDto.CreateRequest request, User currentUser) {
        User student;
        ReservationStatus initialStatus;
        User assignedCounselor = null;

        if (request.getStudentId() != null) { // 상담사/관리자가 학생을 위해 예약 생성하는 경우
            validateCounselorOrAdminRole(currentUser);
            student = findStudentById(request.getStudentId());
            initialStatus = ReservationStatus.CONFIRMED;
            assignedCounselor = currentUser.getRole() == UserRole.COUNSELOR ? currentUser : null; //상담사가 생성 시 본인 자동 배정
        } else { // 학생이 본인을 위해 예약 생성하는 경우
            validateStudentRole(currentUser);
            student = currentUser;
            initialStatus = ReservationStatus.PENDING;
        }

        CounselingSubField subField = findSubFieldById(request.getSubFieldId());
        
        CounselingReservation reservation = new CounselingReservation();
        reservation.setStudent(student);
        reservation.setCounselingField(request.getCounselingField());
        reservation.setSubField(subField);
        reservation.setReservationDate(request.getReservationDate());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setRequestContent(request.getRequestContent());
        reservation.setStatus(initialStatus);
        reservation.setCounselor(assignedCounselor);
        
        if (initialStatus == ReservationStatus.CONFIRMED) {
            reservation.setConfirmedAt(LocalDateTime.now());
        }

        CounselingReservation saved = reservationRepository.save(reservation);
        return saved.getId();
    }

    // CNSL-002: 상담 예약 목록 조회 (학생)
    public Page<CounselingReservationDto.ListResponse> getMyReservations(User student, CounselingReservationDto.SearchCondition condition, Pageable pageable) {
        Page<CounselingReservation> reservations = reservationRepository.findByStudentOrderByCreatedAtDesc(student, pageable);
        return reservations.map(this::toListResponse);
    }

    // CNSL-003: 상담 예약 상세 조회
    public CounselingReservationDto.DetailResponse getReservationDetail(Long reservationId, User currentUser) {
        CounselingReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        validateAccessPermission(reservation, currentUser);
        return toDetailResponse(reservation);
    }

    // CNSL-004: 상담 예약 취소
    @Transactional
    public void cancelReservation(Long reservationId, CounselingReservationDto.CancelRequest request, User currentUser) {
        CounselingReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        if (!reservation.getStudent().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        if (reservation.getStatus() != ReservationStatus.PENDING && reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelReason(request.getCancelReason());
        reservation.setCancelledAt(LocalDateTime.now());
    }

    // CNSL-008, CNSL-009: 상담 승인 (관리자/상담사)
    @Transactional
    public void approveReservation(Long reservationId, LocalDateTime confirmedDateTime, String memo, User currentUser) {
        CounselingReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setConfirmedAt(LocalDateTime.now());
        reservation.setMemo(memo);
        
        if (currentUser.getRole() == UserRole.COUNSELOR) {
            reservation.setCounselor(currentUser);
        }
    }

    // CNSL-010: 상담 거부
    @Transactional
    public void rejectReservation(Long reservationId, String rejectReason, User currentUser) {
        CounselingReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        
        reservation.setStatus(ReservationStatus.REJECTED);
        reservation.setRejectReason(rejectReason);
        reservation.setRejectedAt(LocalDateTime.now());
    }

    // CNSL-011: 배정된 상담 일정 조회 (상담사)
    public Page<CounselingReservationDto.ListResponse> getAssignedReservations(User counselor, Pageable pageable) {
        Page<CounselingReservation> reservations = reservationRepository.findByCounselorAndStatusOrderByConfirmedDateTimeAsc(
                counselor, ReservationStatus.CONFIRMED, pageable);
        return reservations.map(this::toListResponse);
    }

    private void validateCounselorOrAdminRole(User user) {
        if (user.getRole() != UserRole.COUNSELOR && user.getRole() != UserRole.ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateStudentRole(User user) {
        if (user.getRole() != UserRole.STUDENT) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private User findStudentById(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        if (student.getRole() != UserRole.STUDENT) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        
        return student;
    }

    private CounselingSubField findSubFieldById(Long subFieldId) {
        return categoryRepository.findById(subFieldId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));
    }

    private void validateAccessPermission(CounselingReservation reservation, User currentUser) {
        boolean isStudent = currentUser.getId().equals(reservation.getStudent().getId());
        boolean isCounselor = reservation.getCounselor() != null && currentUser.getId().equals(reservation.getCounselor().getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        
        if (!isStudent && !isCounselor && !isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private CounselingReservationDto.ListResponse toListResponse(CounselingReservation reservation) {
        CounselingReservationDto.ListResponse response = new CounselingReservationDto.ListResponse();
        response.setId(reservation.getId());
        response.setStudentName(reservation.getStudent().getName());
        response.setCounselingField(reservation.getCounselingField());
        response.setSubFieldName(reservation.getSubField().getCategoryName());
        response.setReservationDate(reservation.getReservationDate());
        response.setStartTime(reservation.getStartTime());
        response.setEndTime(reservation.getEndTime());
        response.setStatus(reservation.getStatus());
        response.setCounselorName(reservation.getCounselor() != null ? reservation.getCounselor().getName() : null);
        return response;
    }

    private CounselingReservationDto.DetailResponse toDetailResponse(CounselingReservation reservation) {
        CounselingReservationDto.DetailResponse response = new CounselingReservationDto.DetailResponse();
        response.setId(reservation.getId());
        response.setStudentName(reservation.getStudent().getName());
        response.setCounselingField(reservation.getCounselingField());
        response.setSubFieldName(reservation.getSubField().getCategoryName());
        response.setReservationDate(reservation.getReservationDate());
        response.setStartTime(reservation.getStartTime());
        response.setEndTime(reservation.getEndTime());
        response.setRequestContent(reservation.getRequestContent());
        response.setStatus(reservation.getStatus());
        response.setCounselorName(reservation.getCounselor() != null ? reservation.getCounselor().getName() : null);
        response.setMemo(reservation.getMemo());
        response.setRejectReason(reservation.getRejectReason());
        response.setCancelReason(reservation.getCancelReason());
        return response;
    }
}
