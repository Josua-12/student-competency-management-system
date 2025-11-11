package com.competency.scms.controller.counsel;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingReservationDto;
import com.competency.scms.service.counsel.CounselingReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/counseling/reservations")
@RequiredArgsConstructor
public class CounselingReservationApiController {

    private final CounselingReservationService reservationService;

    // CNSL-001: 상담 예약 등록
    @PostMapping
    public ResponseEntity<Long> createReservation(
            @Valid @RequestBody CounselingReservationDto.CreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        Long reservationId = reservationService.createReservation(request, currentUser);
        return ResponseEntity.ok(reservationId);
    }

    // CNSL-002: 상담 예약 목록 조회
    @GetMapping
    public ResponseEntity<Page<CounselingReservationDto.ListResponse>> getMyReservations(
            @ModelAttribute CounselingReservationDto.SearchCondition condition,
            Pageable pageable,
            @AuthenticationPrincipal User currentUser) {
        
        Page<CounselingReservationDto.ListResponse> reservations = 
                reservationService.getMyReservations(currentUser, condition, pageable);
        return ResponseEntity.ok(reservations);
    }

    // CNSL-003: 상담 예약 상세 조회
    @GetMapping("/{reservationId}")
    public ResponseEntity<CounselingReservationDto.DetailResponse> getReservationDetail(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal User currentUser) {
        
        CounselingReservationDto.DetailResponse detail = 
                reservationService.getReservationDetail(reservationId, currentUser);
        return ResponseEntity.ok(detail);
    }

    // CNSL-004: 상담 예약 취소
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long reservationId,
            @Valid @RequestBody CounselingReservationDto.CancelRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        reservationService.cancelReservation(reservationId, request, currentUser);
        return ResponseEntity.ok().build();
    }

    // CNSL-008, CNSL-009: 상담 승인
    @PostMapping("/{reservationId}/approve")
    public ResponseEntity<Void> approveReservation(
            @PathVariable Long reservationId,
            @RequestParam LocalDateTime confirmedDateTime,
            @RequestParam(required = false) String memo,
            @AuthenticationPrincipal User currentUser) {
        
        reservationService.approveReservation(reservationId, confirmedDateTime, memo, currentUser);
        return ResponseEntity.ok().build();
    }

    // CNSL-010: 상담 거부
    @PostMapping("/{reservationId}/reject")
    public ResponseEntity<Void> rejectReservation(
            @PathVariable Long reservationId,
            @RequestParam String rejectReason,
            @AuthenticationPrincipal User currentUser) {
        
        reservationService.rejectReservation(reservationId, rejectReason, currentUser);
        return ResponseEntity.ok().build();
    }

    // CNSL-011: 배정된 상담 일정 조회
    @GetMapping("/assigned")
    public ResponseEntity<Page<CounselingReservationDto.ListResponse>> getAssignedReservations(
            Pageable pageable,
            @AuthenticationPrincipal User currentUser) {
        
        Page<CounselingReservationDto.ListResponse> reservations = 
                reservationService.getAssignedReservations(currentUser, pageable);
        return ResponseEntity.ok(reservations);
    }
}
