package com.competency.scms.service.noncurricular.operation;

import com.competency.scms.dto.noncurricular.operation.completion.StudentCompletionListItemDto;
import com.competency.scms.dto.noncurricular.operation.completion.StudentCompletionSearchConditionDto;
import com.competency.scms.dto.noncurricular.operation.completion.StudentCompletionSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentCompletionQueryService {

    /**
     * 학생 이수내역 페이지 조회
     */
    Page<StudentCompletionListItemDto> getStudentCompletionPage(
            Long studentId,
            StudentCompletionSearchConditionDto condition,
            Pageable pageable
    );

    /**
     * 학생 이수 요약 정보 조회
     */
    StudentCompletionSummaryDto getStudentCompletionSummary(
            Long studentId,
            StudentCompletionSearchConditionDto condition
    );
}

