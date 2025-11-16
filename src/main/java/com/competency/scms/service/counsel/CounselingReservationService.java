package com.competency.scms.service.counsel;

import com.competency.scms.domain.counseling.CounselingReservation;
import com.competency.scms.domain.counseling.CounselingSubField;
import com.competency.scms.domain.counseling.ReservationStatus;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.dto.counsel.CounselingApprovalDto;
import com.competency.scms.dto.counsel.CounselingReservationDto;
import com.competency.scms.exception.BusinessException;
import com.competency.scms.exception.ErrorCode;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.repository.counseling.CounselingSubFieldRepository;
import com.competency.scms.repository.counseling.CounselingReservationRepository;
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
    private final CounselingSubFieldRepository subFieldRepository;
    private final com.competency.scms.repository.counseling.CounselingSatisfactionRepository satisfactionRepository;
    private final com.competency.scms.repository.counseling.CounselingAttachmentRepository attachmentRepository;
    private final com.competency.scms.repository.counseling.CounselingRecordRepository recordRepository;
    private final FileStorageService fileStorageService;
    private final CounselingMapper mapper = new CounselingMapper();

    // CNSL-001: 상담 예약 등록
    @Transactional
    public Long createReservation(CounselingReservationDto.CreateRequest request, User currentUser, 
            org.springframework.web.multipart.MultipartFile resumeFile, 
            org.springframework.web.multipart.MultipartFile coverLetterFile) {
        Long reservationId = createReservation(request, currentUser);
        
        if (resumeFile != null && !resumeFile.isEmpty()) {
            saveAttachment(reservationId, resumeFile, com.competency.scms.domain.counseling.AttachmentType.RESUME);
        }
        if (coverLetterFile != null && !coverLetterFile.isEmpty()) {
            saveAttachment(reservationId, coverLetterFile, com.competency.scms.domain.counseling.AttachmentType.COVER_LETTER);
        }
        
        return reservationId;
    }
    
    @Transactional
    public Long createReservation(CounselingReservationDto.CreateRequest request, User currentUser) {
        if (currentUser == null) {
            currentUser = userRepository.findByUserNum(20213901)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        }
        
        User student;
        ReservationStatus initialStatus;
        User assignedCounselor = null;

        if (request.getStudentId() != null) { // 상담사/관리자가 학생을 위해 예약 생성하는 경우
            validateCounselorOrAdminRole(currentUser);
            student = findStudentById(request.getStudentId());
            initialStatus = ReservationStatus.CONFIRMED;
            assignedCounselor = currentUser.getRole() == UserRole.COUNSELOR ? currentUser : null; //상담사가 생성 시 본인 자동 배정
        } else { // 본인을 위해 예약 생성하는 경우
            student = currentUser;
            initialStatus = ReservationStatus.PENDING;
            
            // 학생이 상담사를 지정한 경우 (취업상담 등)
            if (request.getCounselorId() != null) {
                assignedCounselor = userRepository.findById(request.getCounselorId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            }
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
            reservation.setConfirmedDate(request.getReservationDate());
            reservation.setConfirmedStartTime(request.getStartTime());
            reservation.setConfirmedEndTime(request.getEndTime());
        }

        CounselingReservation saved = reservationRepository.save(reservation);
        return saved.getId();
    }

    // CNSL-002: 상담 예약 목록 조회 (학생)
    public Page<CounselingReservationDto.ListResponse> getMyReservations(User student, CounselingReservationDto.SearchCondition condition, Pageable pageable) {
        if (student == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        Page<CounselingReservation> reservations = reservationRepository.findByStudentOrderByCreatedAtDesc(student, pageable);
        return reservations.map(r -> {
            var response = mapper.toListResponse(r);
            response.setHasSatisfaction(satisfactionRepository.findByReservationId(r.getId()).isPresent());
            return response;
        });
    }

    // CNSL-003: 상담 예약 상세 조회
    public CounselingReservationDto.DetailResponse getReservationDetail(Long reservationId, User currentUser) {
        CounselingReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        validateAccessPermission(reservation, currentUser);
        Long recordId = recordRepository.findByReservationId(reservationId).map(r -> r.getId()).orElse(null);
        return mapper.toDetailResponse(reservation, recordId);
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
    public void approveReservation(CounselingApprovalDto.ApprovalRequest request, User currentUser) {
        CounselingReservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setConfirmedDate(request.getConfirmedDate());
        reservation.setConfirmedStartTime(request.getConfirmedStartTime());
        reservation.setConfirmedEndTime(request.getConfirmedEndTime());
        reservation.setConfirmedAt(LocalDateTime.now());
        reservation.setMemo(request.getMemo());
        
        if (currentUser.getRole() == UserRole.COUNSELOR) {
            reservation.setCounselor(currentUser);
        }

        // CONFIRMED 상태에서는 counselor 필수
        if (reservation.getStatus() == ReservationStatus.CONFIRMED && reservation.getCounselor() == null) {
            throw new BusinessException(ErrorCode.COUNSELOR_REQUIRED_FOR_CONFIRMED);
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
    
    // 상담 완료 처리
    @Transactional
    public void completeReservation(Long reservationId, User currentUser, org.springframework.web.multipart.MultipartFile[] files) {
        CounselingReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        if (!reservation.getCounselor().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setCompletedAt(LocalDateTime.now());
        
        if (files != null) {
            for (org.springframework.web.multipart.MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    saveAttachment(reservationId, file, com.competency.scms.domain.counseling.AttachmentType.DOCUMENT);
                }
            }
        }
    }

    // CNSL-011: 배정된 상담 일정 조회 (상담사)
    public Page<CounselingReservationDto.ListResponse> getAssignedReservations(User counselor, Pageable pageable) {
        Page<CounselingReservation> reservations = reservationRepository.findByCounselorAndStatusOrderByConfirmedDateTimeAsc(
                counselor, ReservationStatus.CONFIRMED, pageable);
        return reservations.map(mapper::toListResponse);
    }
    
    // 상담사 예약 승인 관리 - 대기중인 예약 조회
    public Page<CounselingReservationDto.ListResponse> getCounselorPendingReservations(User counselor, Pageable pageable) {
        Page<CounselingReservation> reservations = reservationRepository.findByCounselorAndStatusOrderByCreatedAtAsc(
                counselor, ReservationStatus.PENDING, pageable);
        return reservations.map(mapper::toListResponse);
    }

    private void validateCounselorOrAdminRole(User user) {
        if (user.getRole() != UserRole.COUNSELOR && user.getRole() != UserRole.ADMIN) {
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
        return subFieldRepository.findById(subFieldId)
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
    
    private void saveAttachment(Long reservationId, org.springframework.web.multipart.MultipartFile file, 
            com.competency.scms.domain.counseling.AttachmentType type) {
        try {
            String storedPath = fileStorageService.storeFile(file);
            CounselingReservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
            
            com.competency.scms.domain.counseling.CounselingAttachment attachment = 
                    com.competency.scms.domain.counseling.CounselingAttachment.builder()
                    .reservation(reservation)
                    .originalName(file.getOriginalFilename())
                    .storedPath(storedPath)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .attachmentType(type)
                    .uploadedAt(LocalDateTime.now())
                    .build();
            
            attachmentRepository.save(attachment);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
    
    public java.util.List<com.competency.scms.domain.counseling.CounselingAttachment> getAttachments(Long reservationId) {
        return attachmentRepository.findByReservationId(reservationId);
    }
    
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> downloadAttachment(Long attachmentId) {
        com.competency.scms.domain.counseling.CounselingAttachment attachment = 
            attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));
        
        try {
            java.nio.file.Path filePath = fileStorageService.loadFile(attachment.getStoredPath());
            org.springframework.core.io.Resource resource = 
                new org.springframework.core.io.UrlResource(filePath.toUri());
            
            String encodedFilename = java.net.URLEncoder.encode(attachment.getOriginalName(), "UTF-8")
                .replaceAll("\\+", "%20");
            
            return org.springframework.http.ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

}
