package com.competency.scms.dto.noncurricular.operation.pending;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProgramBatchActionResultDto {
    private int successCount;
    private int failCount;
}
