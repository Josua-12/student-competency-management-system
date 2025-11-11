package com.competency.scms.domain.noncurricular.operation;

import com.competency.scms.domain.BaseEntity;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.program.ProgramSchedule;
import com.competency.scms.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "program_attendance",
        uniqueConstraints = @UniqueConstraint(
                name="uq_attend_unique",
                columnNames = {"schd_id","user_id"}
        ),
        indexes = {
                @Index(name="ix_attend_prog", columnList="prog_id"),
                @Index(name="ix_attend_schd", columnList="schd_id"),
                @Index(name="ix_attend_student", columnList="user_id"),
                @Index(name="ix_attend_status", columnList="status")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgramAttendance extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attend_id")
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    private ProgramApplication programApplication;   // 신청 없이 현장등록이면 null 가능

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schd_id", nullable = false)
    private ProgramSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
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

