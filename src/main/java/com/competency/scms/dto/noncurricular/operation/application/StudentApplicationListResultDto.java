package com.competency.scms.dto.noncurricular.operation.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class StudentApplicationListResultDto {

    // 상단 요약 카드 영역
    private StudentApplicationSummaryDto summary;

    // 신청 내역 페이지 (테이블 + 페이징)
    private Page<StudentApplicationListItemDto> applications;
}


