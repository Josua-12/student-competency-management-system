package com.competency.scms.dto.noncurricular.operation.participant;

import lombok.Value;

@Value
public class ParticipantStatsDto {
    long total;
    long PENDING;
    long APPROVED;
    long REJECTED;
    long CANCELLED;
}
