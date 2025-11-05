package com.competency.SCMS.domain.noncurricular.operation;

import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramSchedule;
import com.competency.SCMS.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "program_attendance",
        uniqueConstraints = @UniqueConstraint(
                name="uq_attend_unique",
                columnNames = {"schd_id","student_id"}
        ),
        indexes = {
                @Index(name="ix_attend_prog", columnList="prog_id"),
                @Index(name="ix_attend_schd", columnList="schd_id"),
                @Index(name="ix_attend_student", columnList="student_id"),
                @Index(name="ix_attend_status", columnList="status")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attend_id")
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    private Application application;   // 신청 없이 현장등록이면 null 가능

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schd_id", nullable = false)
    private ProgramSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private AttendanceStatus status = AttendanceStatus.ABSENT;

    @Column(name = "attended_at")
    private LocalDateTime attendedAt;

    @Column(name = "recorded_by")
    private Long recordedByUserId;  // 운영자 ID

    @Column(length = 255)
    private String remarks;
}

