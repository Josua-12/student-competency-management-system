package com.competency.scms.dto.noncurricular.program;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class ProgramListRowDto {
    private Long id;
    private String code;
    private String title;
    private String dept;
    private String recruit;      // READY/OPEN/CLOSED
    private String appPeriod;   // 신청기간
    private String runPeriod;   // 운영기간
//    private LocalDate appStart;
//    private LocalDate appEnd;
//    private LocalDate runStart;
//    private LocalDate runEnd;
    private Integer capacity;
    private Integer applied;
    private Integer mileage;
    private String approval;     // REQ/WAIT/DONE/REJ
    private String status;       // DRAFT/.../REJECTED
    private String writer;
    private LocalDate created;
    private String deptName;
    private String categoryName;
    private String thumbnailUrl;
}
