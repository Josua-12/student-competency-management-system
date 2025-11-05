package com.competency.SCMS.domain.counseling;

import com.competency.SCMS.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "counseling_reservations")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CounselingReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    오류로 인한 수정 - JHE
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "student_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stu_id") // ERD: counseling_reservations.stu_id
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CounselingField counselingField;
    
    @Column(nullable = false)
    private LocalDateTime requestedDateTime;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;
//    오류로 인한 수정 - JHE
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "counselor_id")
//    @Column(nullable = true)
// (권장) 지연 로딩 + FK 컬럼 지정
    @ManyToOne(fetch = FetchType.LAZY, optional = true) // 상담사 배정 전일 수 있으니 optional=true
    @JoinColumn(name = "counselor_id")                  // FK 컬럼명 지정
    private User counselor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CounselingCategory category;
    
    private LocalDateTime confirmedDateTime;
    
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

    public enum ReservationStatus {
        PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED
    }
}