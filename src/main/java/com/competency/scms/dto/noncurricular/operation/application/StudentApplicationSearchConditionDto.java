package com.competency.scms.dto.noncurricular.operation.application;

import com.competency.scms.domain.noncurricular.operation.ApplicationStatus;
import com.competency.scms.domain.noncurricular.program.ProgramCategoryType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class StudentApplicationSearchConditionDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;    // 신청일 시작

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;      // 신청일 끝

    private String keyword;        // 프로그램명 검색

    // HTML에서 <select name="category"> 값이 CAREER / GLOBAL ... 이면 자동 매핑됨
    private ProgramCategoryType category;

    // HTML의 name="status" (PENDING, APPROVED, REJECTED, CANCELED)
    private ApplicationStatus status;

    // HTML에서 name="completion" : "COMPLETED", "NOT_COMPLETED" or null
    private String completion;
}

