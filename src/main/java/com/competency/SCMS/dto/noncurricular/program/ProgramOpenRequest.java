package com.competency.SCMS.dto.noncurricular.program;


import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgramOpenRequest {
    private Long programId;

    @NotNull
    private BasicInfoDto basic;

    @NotNull
    private OperationDto operation;

    @Singular
    private List<ScheduleDto> schedules;
}
