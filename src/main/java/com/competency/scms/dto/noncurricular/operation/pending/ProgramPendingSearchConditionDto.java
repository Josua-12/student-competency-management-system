package com.competency.scms.dto.noncurricular.operation.pending;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ProgramPendingSearchConditionDto {

    /** 프로그램명 (LIKE 검색) */
    private String programTitle;

    /** 운영부서 ID */
    private Long departmentId;

    /** 카테고리 ID */
    private Long categoryId;

    /** 승인 상태 (기본값 PENDING) - ProgramStatus 사용 */
    private String approvalStatus; // "PENDING", "APPROVED", "REJECTED" 등

    /** 승인요청일 From ~ To -> 여기서는 updatedAt 기준으로 사용 가정 */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime requestDateFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime requestDateTo;

    /** 모집기간 From ~ To -> appStart/appEnd 기준 */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appStartFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appEndTo;
}

