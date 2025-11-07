package com.competency.SCMS.domain.counseling;

import com.competency.SCMS.domain.user.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "counseling_base_schedules",  // 상담사별 요일당 1개의 스케줄
        uniqueConstraints = @UniqueConstraint(columnNames = {"counselor_id", "day_of_week"}))
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CounselingBaseSchedule { // 기본 근무 시간표
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)  // 상담사가 요일별로 총 7개의 스케줄
    @JoinColumn(name = "counselor_id", nullable = false)
    private User counselor;
    
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;
    
    @Column(nullable = false)
    private Boolean slot0910 = true;
    
    @Column(nullable = false)
    private Boolean slot1011 = true;
    
    @Column(nullable = false)
    private Boolean slot1112 = true;
    
    @Column(nullable = false)
    private Boolean slot1213 = false;   //점심시간
    
    @Column(nullable = false)
    private Boolean slot1314 = true;    
    
    @Column(nullable = false)
    private Boolean slot1415 = true;

    @Column(nullable = false)
    private Boolean slot1516 = true;

    @Column(nullable = false)
    private Boolean slot1617 = true;

    @Column(nullable = false)
    private Boolean slot1718 = true;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 특정 시간대의 가용성을 조회하는 헬퍼 메서드
    public Boolean getSlotAvailability(int startHour) {
        return switch(startHour) {
            case 9 -> slot0910;
            case 10 -> slot1011;
            case 11 -> slot1112;
            case 12 -> slot1213;
            case 13 -> slot1314;
            case 14 -> slot1415;
            case 15 -> slot1516;
            case 16 -> slot1617;
            case 17 -> slot1718;
            default -> false;
        };
    }
}