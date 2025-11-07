package com.competency.SCMS.domain.counseling;

import com.competency.SCMS.domain.user.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "counseling_satisfactions")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CounselingSatisfaction { // 상담별 만족도 설문 결과(통계 목적)
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private CounselingReservation reservation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id", nullable = false)
    private User counselor;
    
    @OneToMany(mappedBy = "satisfaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SatisfactionAnswer> answers = new ArrayList<>();
    
    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime submittedAt = LocalDateTime.now();

    // 필수 필드 생성자
    public CounselingSatisfaction(CounselingReservation reservation, User student, User counselor) {
        this.reservation = reservation;
        this.student = student;
        this.counselor = counselor;
    }

}