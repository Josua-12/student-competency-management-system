package com.competency.scms.dto.noncurricular.program.student;

import com.competency.scms.domain.noncurricular.program.ProgramCategoryType;
import com.competency.scms.domain.noncurricular.program.ProgramStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ProgramStudentSearchRequest {

    // ?keyword=
    private String keyword;

    // ?category=CAREER …
    private ProgramCategoryType category;

    // ?status=OPEN …
    private ProgramStatus status;

    // ?dept=NCC / JOB / INTL …
    private String dept; // 부서코드(화면에서 셀렉트 박스 값)

    // ?from=2025-11-01
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;

    // ?to=2025-11-30
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;
}

