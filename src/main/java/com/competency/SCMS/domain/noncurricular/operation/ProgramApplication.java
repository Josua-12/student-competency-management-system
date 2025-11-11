package com.competency.SCMS.domain.noncurricular.operation;

import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramSchedule;
import com.competency.SCMS.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "program_applications",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_app_unique",
                columnNames = {"prog_id","schd_id","user_id"}
        ),
        indexes = {
                @Index(name="ix_app_prog", columnList="prog_id"),
                @Index(name="ix_app_schd", columnList="schd_id"),
                @Index(name="ix_app_student", columnList="user_id"),
                @Index(name="ix_app_status", columnList="status")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgramApplication extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_id")
    private Long applicationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    // 회차별 신청인 경우 선택
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schd_id")
    private ProgramSchedule schedule;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
<<<<<<< HEAD
    @JoinColumn(name = "stu_user_id")
=======
    @JoinColumn(name = "user_id")
>>>>>>> 8f74f2b01d284f4d9ef2011a6bb2e3773c784cd7
    private User student;

    public Long getId() {
        return applicationId;
    }


    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "reason_text", length = 255)
    private String reasonText;

    @Column(name = "consent_yn", nullable = false)
    private boolean consentYn = false;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime cancelledAt;

    @Column(nullable=false)
    private boolean fromWaitlist = false;

}

