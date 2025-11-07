package com.competency.SCMS.dto.counsel;

import com.competency.SCMS.domain.counseling.CounselingField;
import com.competency.SCMS.domain.counseling.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class CounselingApprovalDto {
    
    // 승인/배정 요청용
    @Data
    public static class ApprovalRequest {
        @NotNull(message = "상담사는 필수입니다")
        private Long counselorId;
        @NotNull(message = "확정 날짜는 필수입니다")
        private LocalDate confirmedDate;
        @NotNull(message = "시작 시간은 필수입니다")
        private LocalTime confirmedStartTime;
        @NotNull(message = "종료 시간은 필수입니다")
        private LocalTime confirmedEndTime;
        private String memo;
    }
    
    // 거부 요청용
    @Data
    public static class RejectRequest {
        @NotBlank(message = "거부 사유는 필수입니다")
        private String rejectReason;
        private LocalDateTime rejectedAt;
    }
    
    // 승인 대기 목록 조회용
    @Data
    public static class PendingListResponse {
        private Long id;
        private String studentName;
        private String studentId;
        private CounselingField counselingField;
        private String subFieldName;
        private LocalDate requestedDate;
        private LocalTime requestedStartTime;
        private String requestContent;
        private LocalDateTime createdAt;
    }
    
    // 승인 대기 검색 조건
    @Data
    public static class SearchCondition {
        private CounselingField counselingField;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer page = 0;
        private Integer size = 10;
    }
    
    // 배정된 상담 일정 조회용
    @Data
    public static class AssignedScheduleResponse {
        private Long id;
        private String studentName;
        private String studentId;
        private CounselingField counselingField;
        private String subFieldName;
        private LocalDate confirmedDate;
        private LocalTime confirmedStartTime;
        private LocalTime confirmedEndTime;
        private ReservationStatus status;
        private String requestContent;
    }
}