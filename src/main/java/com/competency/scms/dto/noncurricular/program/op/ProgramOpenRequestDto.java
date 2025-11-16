package com.competency.scms.dto.noncurricular.program.op;


import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgramOpenRequestDto {
    private Long programId;

    @NotNull
    private BasicInfoDto basic;

    @NotNull
    private OperationDto operation;

    @Singular
    private List<ScheduleDto> schedules;
}
