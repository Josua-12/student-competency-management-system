package com.competency.scms.dto.noncurricular.mileage;

import com.competency.scms.domain.noncurricular.mileage.MileageType;
import com.competency.scms.domain.user.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MileageAssignItemDto {
    User student;
    String name;

    Long programId;
    Long scheduleId; // nullable

    MileageType type; // EARN/USE/ADJUST
    Integer points;
    String description;
}

