package com.competency.SCMS.dto.noncurricular.program;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProgramListRow {
    private Long id;
    private String code;
    private String title;
    private String dept;
    private String recruit;      // READY/OPEN/CLOSED
    private LocalDate appStart;
    private LocalDate appEnd;
    private LocalDate runStart;
    private LocalDate runEnd;
    private Integer capacity;
    private Integer applied;
    private Integer mileage;
    private String approval;     // REQ/WAIT/DONE/REJ
    private String status;       // DRAFT/.../REJECTED
    private String writer;
    private LocalDate created;
}
