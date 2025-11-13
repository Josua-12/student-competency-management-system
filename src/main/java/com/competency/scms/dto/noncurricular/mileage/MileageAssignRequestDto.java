package com.competency.scms.dto.noncurricular.mileage;

import com.competency.scms.domain.noncurricular.program.Program;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MileageAssignRequestDto {
    Program programId;
    Long scheduleId;
    @Singular
    List<MileageAssignItemDto> items;
}
