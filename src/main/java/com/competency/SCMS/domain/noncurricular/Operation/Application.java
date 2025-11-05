package com.competency.SCMS.domain.noncurricular.operation;

import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramSchedule;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "program_applications",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_app_unique",
                columnNames = {"prog_id","schd_id","student_id"}
        ),
        indexes = {
                @Index(name="ix_app_prog", columnList="prog_id"),
                @Index(name="ix_app_schd", columnList="schd_id"),
                @Index(name="ix_app_student", columnList="student_id"),
                @Index(name="ix_app_status", columnList="status")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Application extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schd_id")
    private ProgramSchedule schedule;   // nullable

    @Column(name = "student_id", nullable = false)
    private Long studentId;             // 추후 User 엔티티로 교체 가능

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "reason_text", length = 255)
    private String reasonText;

    @Column(name = "consent_yn", nullable = false)
    private boolean consentYn = false;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();
}

