package com.competency.scms.controller.counsel;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingApprovalDto;
import com.competency.scms.dto.counsel.CounselingReservationDto;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.counsel.CounselingReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/counseling/reservations")
@RequiredArgsConstructor
public class CounselingReservationApiController {

    private final CounselingReservationService reservationService;

    // CNSL-001: 상담 예약 등록
    @PostMapping
    public ResponseEntity<Long> createReservation(
            @Valid @RequestBody CounselingReservationDto.CreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long reservationId = reservationService.createReservation(request, userDetails.getUser());
        return ResponseEntity.ok(reservationId);
    }
    
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Long> createReservationWithFiles(
            @RequestParam Long counselorId,
            @RequestParam Long subfieldId,
            @RequestParam String reservationDate,
            @RequestParam String reservationTime,
            @RequestParam String content,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile resumeFile,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile coverLetterFile,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CounselingReservationDto.CreateRequest request = new CounselingReservationDto.CreateRequest();
        request.setCounselingField(com.competency.scms.domain.counseling.CounselingField.EMPLOYMENT);
        request.setSubFieldId(subfieldId);
        request.setCounselorId(counselorId);
        request.setReservationDate(java.time.LocalDate.parse(reservationDate));
        request.setStartTime(java.time.LocalTime.parse(reservationTime));
        request.setEndTime(java.time.LocalTime.parse(reservationTime).plusMinutes(40));
        request.setRequestContent(content);
        Long reservationId = reservationService.createReservation(request, userDetails.getUser(), resumeFile, coverLetterFile);
        return ResponseEntity.ok(reservationId);
    }
    
    @GetMapping("/{reservationId}/attachments")
    public ResponseEntity<java.util.List<com.competency.scms.dto.counsel.CounselingAttachmentDto>> getAttachments(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        java.util.List<com.competency.scms.domain.counseling.CounselingAttachment> attachments = 
            reservationService.getAttachments(reservationId);
        java.util.List<com.competency.scms.dto.counsel.CounselingAttachmentDto> dtos = 
            attachments.stream().map(att -> {
                com.competency.scms.dto.counsel.CounselingAttachmentDto dto = 
                    new com.competency.scms.dto.counsel.CounselingAttachmentDto();
                dto.setId(att.getId());
                dto.setOriginalName(att.getOriginalName());
                dto.setStoredPath(att.getStoredPath());
                dto.setFileSize(att.getFileSize());
                dto.setAttachmentType(att.getAttachmentType());
                return dto;
            }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<org.springframework.core.io.Resource> downloadAttachment(
            @PathVariable Long attachmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return reservationService.downloadAttachment(attachmentId);
    }

    // CNSL-002: 상담 예약 목록 조회
    @GetMapping
    public ResponseEntity<Page<CounselingReservationDto.ListResponse>> getMyReservations(
            @ModelAttribute CounselingReservationDto.SearchCondition condition,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<CounselingReservationDto.ListResponse> reservations = 
                reservationService.getMyReservations(userDetails.getUser(), condition, pageable);
        return ResponseEntity.ok(reservations);
    }

    // CNSL-003: 상담 예약 상세 조회
    @GetMapping("/{reservationId}")
    public ResponseEntity<CounselingReservationDto.DetailResponse> getReservationDetail(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CounselingReservationDto.DetailResponse detail = 
                reservationService.getReservationDetail(reservationId, userDetails.getUser());
        return ResponseEntity.ok(detail);
    }

    // CNSL-004: 상담 예약 취소
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long reservationId,
            @Valid @RequestBody CounselingReservationDto.CancelRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        reservationService.cancelReservation(reservationId, request, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    // CNSL-008, CNSL-009: 상담 승인
    @PostMapping("/{reservationId}/approve")
    public ResponseEntity<Void> approveReservation(
            @PathVariable Long reservationId,
            @RequestBody @Valid CounselingApprovalDto.ApprovalRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!reservationId.equals(request.getReservationId())) {
            throw new IllegalArgumentException("URL의 reservationId와 요청 본문의 reservationId가 일치하지 않습니다");
        }
        reservationService.approveReservation(request, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    // CNSL-010: 상담 거부
    @PostMapping("/{reservationId}/reject")
    public ResponseEntity<Void> rejectReservation(
            @PathVariable Long reservationId,
            @RequestParam String rejectReason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        reservationService.rejectReservation(reservationId, rejectReason, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    // CNSL-011: 배정된 상담 일정 조회
    @GetMapping("/assigned")
    public ResponseEntity<Page<CounselingReservationDto.ListResponse>> getAssignedReservations(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<CounselingReservationDto.ListResponse> reservations = 
                reservationService.getAssignedReservations(userDetails.getUser(), pageable);
        return ResponseEntity.ok(reservations);
    }
    
    // 상담사 예약 승인 관리 - 대기중인 예약 조회
    @GetMapping("/counselor")
    public ResponseEntity<Page<CounselingReservationDto.ListResponse>> getCounselorReservations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 상담사의 모든 예약을 조회하고 필터링은 프론트엔드에서 처리
        Page<CounselingReservationDto.ListResponse> reservations = 
                reservationService.getCounselorAllReservations(userDetails.getUser(), pageable);
        return ResponseEntity.ok(reservations);
    }
    
    // 상담 완료 처리
    @PostMapping(value = "/{reservationId}/complete", consumes = "multipart/form-data")
    public ResponseEntity<Void> completeReservation(
            @PathVariable Long reservationId,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile[] files,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        reservationService.completeReservation(reservationId, userDetails.getUser(), files);
        return ResponseEntity.ok().build();
    }
}
