package com.competency.scms.domain.noncurricular.mileage;


import com.competency.scms.domain.BaseEntity;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mileage_records",
        indexes = {
                @Index(name = "ix_mileage_student", columnList = "user_id"),
                @Index(name = "ix_mileage_program", columnList = "prog_id"),
                @Index(name = "ix_mileage_type", columnList = "type")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MileageRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mileage_id")
    private Long mileageId;

    /** 학생 ID */
    @Column(name = "user_id", nullable = false)
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
}
