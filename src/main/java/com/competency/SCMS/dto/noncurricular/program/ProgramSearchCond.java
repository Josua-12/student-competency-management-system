package com.competency.SCMS.dto.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.operation.ApprovalStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class ProgramSearchCond {

    private String q;           // 프로그램명/코드 검색어
    private Long deptId;           // 부서 필터
    private String dept;        // 운영부서명 (예: 취업진로처)
    private String recruit;     // READY / OPEN / CLOSED
    private String approval;    // REQ / WAIT / DONE / REJ

    private String keyword;        // 제목 like

    private Long categoryId;       // 카테고리 필터
    private ApprovalStatus status; // DRAFT / PENDING / APPROVED / ONGOING / COMPLETED / REJECTED
    private LocalDate from;        // 시작일(운영시작일 >=)
    private LocalDate to;          // 종료일(운영종료일 <=)

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appStartFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appStartTo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate runStartFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate runStartTo;

    private Integer mileMin;    // 포인트 최소
    private Integer mileMax;    // 포인트 최대

    private ProgramSort sort = ProgramSort.CREATED_DESC; // 정렬
}
