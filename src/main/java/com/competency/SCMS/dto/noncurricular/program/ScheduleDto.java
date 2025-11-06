package com.competency.SCMS.dto.noncurricular.program;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class ScheduleDto {
    Integer roundNo;
    String date;        // yyyy-MM-dd
    String timeRange;   // "HH:mm ~ HH:mm"
    String content;
}
