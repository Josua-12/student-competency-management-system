package com.competency.SCMS.domain.noncurricular.Closure;


import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.noncurricular.MileageReason;
import com.competency.SCMS.domain.noncurricular.MileageType;
import com.competency.SCMS.domain.noncurricular.Core.Program;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mileage_records",
        indexes = {
                @Index(name = "ix_mileage_student", columnList = "student_id"),
                @Index(name = "ix_mileage_program", columnList = "prog_id"),
                @Index(name = "ix_mileage_type", columnList = "type")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MileageRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mileage_id")
    private Long id;

    /** 학생 ID */
    @Column(name = "student_id", nullable = false)
    private Long studentId;

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
}
