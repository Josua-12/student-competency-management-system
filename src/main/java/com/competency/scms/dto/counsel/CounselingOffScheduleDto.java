package com.competency.scms.dto.counsel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CounselingOffScheduleDto {
    
    // 휴무 신청용
    @Data
    public static class CreateRequest {
        @NotNull(message = "시작일은 필수입니다")
        private LocalDate startDate;
        @NotNull(message = "종료일은 필수입니다")
        private LocalDate endDate;
        @NotBlank(message = "휴무 사유는 필수입니다")
        private String offReason;
        private String detailReason;
    }
    
    // 휴무 목록 조회용
    @Data
    public static class ListResponse {
        private Long id;
        private LocalDate startDate;
        private LocalDate endDate;
        private String offReason;
        private String status; // "PENDING", "APPROVED", "REJECTED"
        private LocalDateTime createdAt;
    }
    
    // 휴무 상세 조회용
    @Data
    public static class DetailResponse {
        private Long id;
        private LocalDate startDate;
        private LocalDate endDate;
        private String offReason;
        private String detailReason;
        private String status;
        private String rejectReason;
        private LocalDateTime createdAt;
        private LocalDateTime approvedAt;
    }
}
