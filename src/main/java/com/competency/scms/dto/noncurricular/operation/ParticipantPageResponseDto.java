package com.competency.scms.dto.noncurricular.operation;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
public class ParticipantPageResponseDto {
    List<ParticipantListItemDto> content;
    long total;
    ParticipantStatsDto stats;

    @Builder
    public ParticipantPageResponseDto(
            List<ParticipantListItemDto> content,
            long total,
            ParticipantStatsDto stats
    ) {
        this.content = content;
        this.total = total;
        this.stats = stats;
    }
}

