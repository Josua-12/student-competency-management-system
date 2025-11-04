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

@Entity
@Table(name = "counseling_schedules")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CounselingSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id", nullable = false)
    private User counselor;
    
    @Column(nullable = false)
    private LocalDate scheduleDate;
    
    @Column(nullable = false)
    private Boolean slot0910 = false;
    
    @Column(nullable = false)
    private Boolean slot1011 = false;
    
    @Column(nullable = false)
    private Boolean slot1112 = false;
    
    @Column(nullable = false)
    private Boolean slot1213 = false;
    
    @Column(nullable = false)
    private Boolean slot1314 = false;
    
    @Column(nullable = false)
    private Boolean slot1415 = false;

    @Column(nullable = false)
    private Boolean slot1516 = false;

    @Column(nullable = false)
    private Boolean slot1617 = false;

    @Column(nullable = false)
    private Boolean slot1718 = false;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}