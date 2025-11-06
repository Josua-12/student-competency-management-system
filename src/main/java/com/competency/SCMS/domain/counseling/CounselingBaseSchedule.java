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
@Table(name = "counseling_schedules")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CounselingBaseSchedule { // 기본 근무 시간표
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
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
}