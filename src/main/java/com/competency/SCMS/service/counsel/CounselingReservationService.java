package com.competency.SCMS.service.counsel;

import com.competency.SCMS.domain.counseling.CounselingReservation;
import com.competency.SCMS.domain.counseling.CounselingSubField;
import com.competency.SCMS.domain.counseling.ReservationStatus;
import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.domain.user.UserRole;
import com.competency.SCMS.dto.counsel.CounselingReservationDto;
import com.competency.SCMS.exception.BusinessException;
import com.competency.SCMS.exception.ErrorCode;
import com.competency.SCMS.repository.UserRepository;
import com.competency.SCMS.repository.counseling.CounselingCategoryRepository;
import com.competency.SCMS.repository.counseling.CounselingReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingReservationService {

    private final CounselingReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CounselingCategoryRepository categoryRepository;

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
}
