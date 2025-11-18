package com.competency.scms.dto.noncurricular.operation.completion;


import com.competency.scms.domain.noncurricular.program.CompletionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class OpCompletionStatusListItemDto {
    private Long applicationId;

    private Long programId;
    private String programCode;      // prog_id (문자열 코드라면)
    private String programName;
    private String sessionName;      // 회차/일정명 (예: 1차, 2차)

    private String opDeptName;       // 운영부서명

    private String studentNo;
    private String studentName;
    private String departmentName;   // 학생 학과
    private String gradeName;        // 학년 (문자열로)

    private Integer attendanceRate;  // 출석률(%)

    private Boolean satisfactionSubmitted;
    private Double satisfactionScore;

    private CompletionStatus completionStatus;
    private LocalDate completionDate;

    private Boolean certificateAvailable; // 이수증 발급 여부

    private LocalDateTime lastModified;   // 최종 수정일시
}
