package com.competency.SCMS.dto.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.AttendanceType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;


@Value @Builder
public class ScheduleDto {
    private Integer roundNo;
    @NotNull
    private LocalDate date;
    //    String date;        // yyyy-MM-dd
    @NotNull
    private LocalTime startAt;

    @NotNull
    private LocalTime endAt;
//    String timeRange;   // "HH:mm ~ HH:mm"
    private String content;

    private String place;

    private Integer capacity; // null이면 전체정원

    private AttendanceType attendanceType;
}
