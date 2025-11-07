package com.competency.SCMS.dto.counsel;

import com.competency.SCMS.domain.counseling.CounselingField;
import com.competency.SCMS.domain.counseling.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CounselingReservationDto {
    
    // 예약 등록용
    @Data
    public static class CreateRequest {
        private Long studentId; // 상담사가 예약 시 필수, 학생이 예약 시 null (인증된 학생 정보 사용)
        @NotNull(message = "상담 분야는 필수입니다")
        private CounselingField counselingField;
        @NotNull(message = "세부 분야는 필수입니다")
        private Long subFieldId;
        @NotNull(message = "예약 날짜는 필수입니다")
        private LocalDate reservationDate;
        @NotNull(message = "시작 시간은 필수입니다")
        private LocalTime startTime;
        @NotNull(message = "종료 시간은 필수입니다")
        private LocalTime endTime;
        @NotBlank(message = "상담 내용은 필수입니다")
        private String requestContent;
    }
    
    // 예약 목록 조회용
    @Data
    public static class ListResponse {
        private Long id;
        private String studentName;
        private CounselingField counselingField;
        private String subFieldName;
        private LocalDate reservationDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private ReservationStatus status;
        private String counselorName;
    }
    
    // 예약 목록 검색 조건
    @Data
    public static class SearchCondition {
        private ReservationStatus status;
        private CounselingField counselingField;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer page = 0;
        private Integer size = 10;
    }
    
    // 특정 시간대 예약 가능 상담사 조회용
    @Data
    public static class AvailableCounselorsRequest {
        @NotNull(message = "날짜는 필수입니다")
        private LocalDate date;
        @NotNull(message = "시작 시간은 필수입니다")
        private LocalTime startTime;
        @NotNull(message = "종료 시간은 필수입니다")
        private LocalTime endTime;
        private CounselingField counselingField;
    }
    
    @Data
    public static class AvailableCounselorsResponse {
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private List<CounselorInfo> availableCounselors;
        
        @Data
        public static class CounselorInfo {
            private Long counselorId;
            private String counselorName;
            private CounselingField specialization;
            private String email;
        }
    }
    
    // 월별 예약 가능 시간대 조회용 (캘린더용)
    @Data
    public static class MonthlyAvailabilityResponse {
        private Integer year;
        private Integer month;
        private List<DayAvailability> days;
        
        @Data
        public static class DayAvailability {
            private LocalDate date;
            private List<TimeSlotAvailability> timeSlots;
            
            @Data
            public static class TimeSlotAvailability {
                private LocalTime startTime;
                private LocalTime endTime;
                private Integer availableCounselorCount;
                private Boolean hasAvailability;
            }
        }
    }
    
    // 예약 상세 조회용
    @Data
    public static class DetailResponse {
        private Long id;
        private String studentName;
        private CounselingField counselingField;
        private String subFieldName;
        private LocalDate reservationDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String requestContent;
        private ReservationStatus status;
        private String counselorName;
        private String memo;
        private String rejectReason;
        private String cancelReason;
    }
    
    // 예약 취소용
    @Data
    public static class CancelRequest {
        @NotBlank(message = "취소 사유는 필수입니다")
        private String cancelReason;
    }

    // 예약 거절용
    @Data
    public static class RejectRequest {
        @NotBlank(message = "거절 사유는 필수입니다")
        private String rejectReason;
    }
}
