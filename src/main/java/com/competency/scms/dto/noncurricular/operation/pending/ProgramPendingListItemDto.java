package com.competency.scms.dto.noncurricular.operation.pending;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProgramPendingListItemDto {

    private Long progId;
    private String title;
    private String categoryName;
    private String departmentName;

    /** 승인 상태 */
    private String status; // ProgramStatus.name()

    /** 승인요청일(여기서는 updatedAt 기준) */
    private LocalDateTime requestDate;

    /** 모집 기간 */
    private LocalDateTime appStart;
    private LocalDateTime appEnd;

    /** 정원 / 현재 신청 인원 */
    private Integer maxPart;
    private Integer curPart;
}

