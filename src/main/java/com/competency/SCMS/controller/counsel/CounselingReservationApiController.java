package com.competency.SCMS.controller.counsel;

import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.dto.counsel.CounselingReservationDto;
import com.competency.SCMS.service.counsel.CounselingReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counseling/reservations")
@RequiredArgsConstructor
public class CounselingReservationApiController {

    private final CounselingReservationService reservationService;

    @PostMapping
    public ResponseEntity<Long> createReservation(
            @Valid @RequestBody CounselingReservationDto.CreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        Long reservationId = reservationService.createReservation(request, currentUser);
        return ResponseEntity.ok(reservationId);
    }
}
