package com.competency.SCMS.domain.counseling;

import com.competency.SCMS.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "counseling_reservations",
        indexes = {
                @Index(name = "idx_counselor_date_time", columnList = "counselor_id,reservation_date,start_time"),
                @Index(name = "idx_student_status", columnList = "student_id,status"),
                @Index(name = "idx_reservation_date", columnList = "reservation_date")
        })
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CounselingReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stu_user_id") // ERD: counseling_reservations.stu_user_id
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CounselingField counselingField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_field_id")
    private CounselingSubField subField;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private LocalTime startTime;    //09:00, 10:00 등 (1시간 단위)

    @Column(nullable = false)
    private LocalTime endTime;    //10:00, 11:00 등 (1시간 단위)

    @Column(columnDefinition = "TEXT")
    private String requestContent;  // 상담 신청 내용
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY) // 상담사 배정 전일 수 있으니 optional=true
    @JoinColumn(name = "counselor_user_id")                  // FK 컬럼명 지정
    private User counselor;

    public Long getId() {
        return id;
    }

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(columnDefinition = "TEXT")
    private String cancelReason;

    @Column(columnDefinition = "TEXT")
    private String rejectReason;


    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;  // 승인 시각
    private LocalDateTime completedAt;  // 상담 완료 시각
    private LocalDateTime cancelledAt;  // 취소 시각
    private LocalDateTime rejectedAt;   // 거절 시각


    // 예약 시작 시간을 LocalDateTime으로 반환 (편의 메서드)
    public LocalDateTime getReservationDateTime() {
        return LocalDateTime.of(reservationDate, startTime);
    }

    // 예약 시간이 지났는지 확인
    public boolean isPast() {
        return getReservationDateTime().isBefore(LocalDateTime.now());
    }

    // 취소 가능한지 확인 (예: 예약 24시간 전까지만 취소 가능)
    public boolean isCancellable() {
        if (status != ReservationStatus.CONFIRMED) {
            return false;
        }
        LocalDateTime cancelDeadline = getReservationDateTime().minusHours(24);
        return LocalDateTime.now().isBefore(cancelDeadline);
    }
}