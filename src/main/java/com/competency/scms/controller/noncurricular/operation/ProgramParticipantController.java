package com.competency.scms.controller.noncurricular.operation;

import com.competency.scms.dto.noncurricular.operation.ApproveRejectRequestDto;
import com.competency.scms.dto.noncurricular.operation.NotifyRequestDto;
import com.competency.scms.dto.noncurricular.operation.ParticipantPageResponseDto;
import com.competency.scms.dto.noncurricular.operation.ParticipantSearchConditionDto;
import com.competency.scms.service.noncurricular.operation.ProgramParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/noncurricular-operator/programs/{programId}/participants")
public class ProgramParticipantController {
    private final ProgramParticipantService service;

    @GetMapping
    public ParticipantPageResponseDto list(
            @PathVariable Long programId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(Math.max(page,0), Math.max(size,1));
        ParticipantSearchConditionDto cond = ParticipantSearchConditionDto.builder()
                .status(status == null ? "" : status)
                .scheduleId(scheduleId)
                .q(keyword == null ? "" : keyword)
                .build();
        return service.search(programId, cond, pageable);
    }

    @PutMapping("/{applicationId}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(@PathVariable Long programId,
                        @PathVariable Long applicationId,
                        @RequestBody(required = false) ApproveRejectRequestDto body){
        service.approve(programId, applicationId, body==null?null:body.getReason());
    }

    @PutMapping("/{applicationId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable Long programId,
                       @PathVariable Long applicationId,
                       @RequestBody(required = false) ApproveRejectRequestDto body){
        service.reject(programId, applicationId, body==null?null:body.getReason());
    }

    @DeleteMapping("/{applicationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long programId,
                       @PathVariable Long applicationId,
                       @RequestBody(required = false) ApproveRejectRequestDto body){
        service.cancel(programId, applicationId, body==null?null:body.getReason());
    }

    @PostMapping("/notify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void notify(@PathVariable Long programId, @RequestBody NotifyRequestDto req){
        service.notifyToApplicants(programId, req);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@PathVariable Long programId,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) Long scheduleId,
                                         @RequestParam(name = "q", required = false) String keyword){
        ParticipantSearchConditionDto cond = ParticipantSearchConditionDto.builder()
                .status(status == null ? "" : status)
                .scheduleId(scheduleId)
                .q(keyword == null ? "" : keyword)
                .build();
        byte[] data = service.exportExcel(programId, cond);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("participants.csv").build());
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
