package com.competency.scms.dto.noncurricular.operation.completion;

import com.competency.scms.domain.noncurricular.program.CompletionStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class OpBulkCompletionUpdateRequestDto {
    private List<Long> applicationIds;

    private CompletionStatus completionStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate completionDate;

    private String remark;
}
