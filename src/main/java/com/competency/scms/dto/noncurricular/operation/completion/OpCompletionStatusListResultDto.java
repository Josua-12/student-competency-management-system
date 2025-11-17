package com.competency.scms.dto.noncurricular.operation.completion;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OpCompletionStatusListResultDto {

    private List<OpCompletionStatusListItemDto> items;
    private long totalCount;
    private OpCompletionStatusSummaryDto summary;
}
