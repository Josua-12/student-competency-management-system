package com.competency.scms.controller.counsel;

import com.competency.scms.dto.counsel.CounselingReservationDto;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.counsel.CounselingReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/counseling/written-editing")
@RequiredArgsConstructor
public class WrittenEditingApiController {

    private final CounselingReservationService reservationService;

    @PostMapping
    public ResponseEntity<Long> submitWrittenEditing(
            @RequestParam String editingType,
            @RequestParam String companyName,
            @RequestParam String jobPosition,
            @RequestParam String recruitmentStage,
            @RequestParam String recruitmentType,
            @RequestParam String requestContent,
            @RequestParam(required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CounselingReservationDto.CreateRequest request = new CounselingReservationDto.CreateRequest();
        request.setCounselingField(com.competency.scms.domain.counseling.CounselingField.EMPLOYMENT);
        request.setSubFieldId(Long.parseLong(editingType));
        request.setReservationDate(LocalDate.now());
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(9, 40));
        String stageDisplay = getRecruitmentStageDisplay(recruitmentStage);
        String typeDisplay = getRecruitmentTypeDisplay(recruitmentType);
        request.setRequestContent(String.format("기업명: %s\n직무: %s\n채용준비단계: %s\n채용유형: %s\n요청내용: %s", 
            companyName, jobPosition, stageDisplay, typeDisplay, requestContent));
        Long reservationId = reservationService.createReservation(request, userDetails.getUser(), null, file);
        return ResponseEntity.ok(reservationId);
    }
    
    private String getRecruitmentStageDisplay(String stage) {
        switch (stage) {
            case "document": return "서류작성";
            case "submission": return "서류제출";
            case "interview": return "면접준비";
            default: return stage;
        }
    }
    
    private String getRecruitmentTypeDisplay(String type) {
        switch (type) {
            case "regular": return "정규직";
            case "contract": return "계약직";
            case "intern": return "인턴";
            case "parttime": return "파트타임";
            default: return type;
        }
    }
}
