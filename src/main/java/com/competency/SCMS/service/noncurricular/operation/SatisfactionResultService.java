package com.competency.SCMS.service.noncurricular.operation;

import com.competency.SCMS.dto.noncurricular.operation.*;

public interface SatisfactionResultService {
    SatisfactionPageResponseDto search(SatisfactionSearchConditionDto cond);
    SatisfactionDetailDto getById(Long id);
}
