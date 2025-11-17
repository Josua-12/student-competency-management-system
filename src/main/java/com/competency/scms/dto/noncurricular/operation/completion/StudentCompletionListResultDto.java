package com.competency.scms.dto.noncurricular.operation.completion;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class StudentCompletionListResultDto {

    private final Page<StudentCompletionListItemDto> page;
    private final StudentCompletionSummaryDto summary;
}

