package com.competency.scms.dto.noncurricular.operation;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SatisfactionSearchConditionDto {
    private Long programId;
    private Long scheduleId;
    private String dept;          // 부서 코드 또는 명칭
    private String category;      // 카테고리 코드
    private String status;        // ProgramStatus 이름 문자열(예: "COMPLETED")
    private LocalDate submittedFrom; // 날짜만 받음
    private LocalDate submittedTo;
    private Integer ratingMin;    // 1~5
    private Integer ratingMax;    // 1~5
    private String keyword;       // feedback like 검색
    private Integer page;         // 0-base
    private Integer size;         // 페이지 크기
}
