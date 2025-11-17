package com.competency.scms.service.noncurricular.operation;

import com.competency.scms.dto.noncurricular.mypage.*;
import com.competency.scms.dto.noncurricular.operation.completion.StudentCompletionListResultDto;
import com.competency.scms.dto.noncurricular.operation.completion.StudentCompletionSearchConditionDto;
import org.springframework.data.domain.Pageable;

public interface StudentCompletionService {

    /**
     * 학생의 비교과 프로그램 이수내역 조회
     */
    StudentCompletionListResultDto getStudentCompletions(Long studentId,
                                                         StudentCompletionSearchConditionDto condition,
                                                         Pageable pageable);
}

