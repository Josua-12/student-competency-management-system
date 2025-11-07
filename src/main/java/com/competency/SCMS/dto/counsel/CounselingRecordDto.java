package com.competency.SCMS.dto.counsel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CounselingRecordDto {
    
    // 상담일지 작성/수정용
    @Data
    public static class CreateRequest {
        @NotNull(message = "예약 ID는 필수입니다")
        private Long reservationId;
        @NotBlank(message = "상담 내용은 필수입니다")
        private String recordContent;
        private String counselorMemo;
        private Boolean isPublic;
    }
    
    @Data
    public static class UpdateRequest {
        @NotBlank(message = "상담 내용은 필수입니다")
        private String recordContent;
        private String counselorMemo;
        private Boolean isPublic;
    }
    
    // 상담일지 목록 조회용
    @Data
    public static class ListResponse {
        private Long id;
        private String studentName;
        private String studentId;
        private String categoryName;
        private LocalDateTime counselingDate;
        private Boolean isPublic;
        private LocalDateTime createdAt;
    }
    
    // 상담일지 상세 조회용
    @Data
    public static class DetailResponse {
        private Long id;
        private String studentName;
        private String studentId;
        private String categoryName;
        private String recordContent;
        private String counselorMemo;
        private Boolean isPublic;
        private LocalDateTime counselingDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    // 상담일지 검색 조건
    @Data
    public static class SearchCondition {
        private String studentName;
        private String studentId;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer page = 0;
        private Integer size = 10;
    }
}