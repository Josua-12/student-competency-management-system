package com.competency.SCMS.dto.counsel;

import com.competency.SCMS.domain.counseling.CounselingField;
import com.competency.SCMS.domain.counseling.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class CounselingReservationDto {
    
    // 예약 등록용
    @Data
    public static class CreateRequest {
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
    
    // 가능한 상담 시간대 조회용 (학생용)
    @Data
    public static class AvailableSlotResponse {
        private LocalDate date;
        private List<TimeSlot> availableSlots;
        
        @Data
        public static class TimeSlot {
            private LocalTime startTime;
            private LocalTime endTime;
            private String counselorName;
            private Long counselorId;
        }
    }
    
    // 예약 상세 조회용
    @Data
    public static class DetailResponse {
        private Long id;
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
}