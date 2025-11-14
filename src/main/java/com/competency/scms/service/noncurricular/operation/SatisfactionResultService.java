package com.competency.scms.service.noncurricular.operation;

import com.competency.scms.dto.noncurricular.operation.*;

public interface SatisfactionResultService {
    SatisfactionPageResponseDto search(SatisfactionSearchConditionDto cond);
    SatisfactionDetailDto getById(Long id);
}
