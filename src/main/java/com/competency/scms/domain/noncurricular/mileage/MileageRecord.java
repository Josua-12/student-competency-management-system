package com.competency.scms.domain.noncurricular.mileage;


import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mileage_record",
        indexes = {
                @Index(name = "ix_mileage_student", columnList = "student_id"),
                @Index(name = "ix_mileage_program", columnList = "prog_id"),
                @Index(name = "ix_mileage_type", columnList = "type")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MileageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mileage_id")
    private Long mileageId;

    // 학생
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;


    /** 관련 프로그램 (없을 수도 있음) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prog_id")
    private Program program;

    /** 거래유형 (적립/사용/조정) */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private MileageType type;

    /** 발생 사유 */
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", length = 30)
    private MileageReason reason;

    /** 마일리지 점수 (+/-) */
    @Column(name = "points", nullable = false)
    private Integer points;

    /** 비고 / 상세 사유 */
    @Column(name = "remarks", length = 255)
    private String remarks;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by_user_id")
    private User createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
