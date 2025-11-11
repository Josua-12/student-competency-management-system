package com.competency.scms.dto.counsel;

import com.competency.scms.domain.counseling.ReservationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CounselingScheduleDto {
    
    // 상담 일정 등록/수정용
    @Data
    public static class ScheduleRequest {
        @NotNull(message = "일정 날짜는 필수입니다")
        private LocalDate scheduleDate;
        @NotEmpty(message = "시간대는 필수입니다")
        @Valid
        private List<TimeSlot> timeSlots;
        
        @Data
        public static class TimeSlot {
            @NotNull(message = "시작 시간은 필수입니다")
            private LocalTime startTime;
            @NotNull(message = "종료 시간은 필수입니다")
            private LocalTime endTime;
            @NotNull(message = "가용 여부는 필수입니다")
            private Boolean isAvailable;
        }
    }
    
    // 상담 시간대 관리용
    @Data
    public static class TimeSlotResponse {
        private Long id;
        private LocalDate scheduleDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Boolean isAvailable;
        private Boolean isReserved;
    }
    
    // 상담사 일일 예약 현황 조회용
    @Data
    public static class DailyScheduleResponse {
        private LocalDate scheduleDate;
        private String counselorName;
        private List<ReservationSlot> slots;
        
        @Data
        public static class ReservationSlot {
            private LocalTime startTime;
            private LocalTime endTime;
            private Long reservationId;
            private String studentName;
            private String studentId;
            private ReservationStatus status;
            private String counselingFieldName;
        }
    }
    
    // 상담사 주간 일정 조회용
    @Data
    public static class WeeklyScheduleResponse {
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private List<DaySchedule> days;
        
        @Data
        public static class DaySchedule {
            private LocalDate date;
            private String dayOfWeek;
            private List<TimeSlotStatus> timeSlots;
            
            @Data
            public static class TimeSlotStatus {
                private LocalTime startTime;
                private LocalTime endTime;
                private String status; // "AVAILABLE", "RESERVED", "OFF", "UNAVAILABLE"
                private String studentName;
                private String offReason;
            }
        }
    }
}