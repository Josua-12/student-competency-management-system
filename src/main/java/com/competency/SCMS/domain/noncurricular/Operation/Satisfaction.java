package com.competency.SCMS.domain.noncurricular.operation;

import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramSchedule;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "program_satisfaction",
        uniqueConstraints = @UniqueConstraint(
                name="uq_satis_unique",
                columnNames = {"prog_id","schd_id","student_id"}
        ),
        indexes = {
                @Index(name="ix_satis_prog", columnList="prog_id"),
                @Index(name="ix_satis_student", columnList="student_id"),
                @Index(name="ix_satis_rating", columnList="rating")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Satisfaction extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "satis_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schd_id")
    private ProgramSchedule schedule;   // 프로그램 단위면 null

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Integer rating;             // 1~5 (서비스/검증에서 범위 체크)

    @Column(length = 500)
    private String comment;
}

