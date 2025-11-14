package com.competency.scms.dto.noncurricular.program.op;

import com.competency.scms.domain.noncurricular.program.AttendanceType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDto {
    private Integer roundNo;
    @NotNull
    private LocalDate date;
    //    String date;        // yyyy-MM-dd

    private String timeRange;     // ex) "10:00 ~ 12:00"

    @NotNull
    private LocalTime startAt;

    @NotNull
    private LocalTime endAt;
//    String timeRange;   // "HH:mm ~ HH:mm"
    private String content;

    private String place;

    private Integer capacity; // null이면 전체정원

    private AttendanceType attendanceType;

    // Builder 커스터마이징: timeRange 자동 계산 기능
    public static class ScheduleDtoBuilder {
        public ScheduleDtoBuilder timeRange(LocalTime start, LocalTime end) {
            if (start == null && end == null) {
                this.timeRange = "-";
            } else {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
                String s = (start != null) ? start.format(fmt) : "";
                String e = (end != null) ? end.format(fmt) : "";
                this.timeRange = s + " ~ " + e;
            }
            return this;
        }
    }
}
