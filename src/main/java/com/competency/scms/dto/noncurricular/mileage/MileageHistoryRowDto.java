package com.competency.scms.dto.noncurricular.mileage;

import com.competency.scms.domain.noncurricular.mileage.MileageType;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class MileageHistoryRowDto {
    LocalDateTime createdAt;
    String studentNo;
    String name;
    MileageType type;
    Integer points;
    String description;
    String createdByName;
}

