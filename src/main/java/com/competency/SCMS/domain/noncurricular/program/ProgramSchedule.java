package com.competency.SCMS.domain.noncurricular.program;

import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.noncurricular.operation.ProgramAttendance;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "program_schedules",
        uniqueConstraints = @UniqueConstraint(name="uq_program_schedules", columnNames = {"prog_id","session_no"}),
        indexes = {
                @Index(name = "ix_schedules_prog",     columnList = "prog_id"),
                @Index(name = "ix_schedules_datetime", columnList = "start_at,end_at")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgramSchedule extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schd_id")
    private Long scheduleId;

    /** 소속 프로그램 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    /** 회차 번호 (1, 2, 3...) */
    @Column(name = "session_no", nullable = false)
    private Integer sessionNo;

    /** 일자 */
    private LocalDate date;

    /** 시작/종료 시간 */
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(name = "place_text", length = 200)
    private String placeText;

    @Column(name = "capacity_ovr")
    private Integer capacityOverride;

    /** 세부내용 */
    @Column(length = 255)
    private String remarks;

    @OneToMany(mappedBy="schedule", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ProgramAttendance> programAttendanceList = new ArrayList<>();
}

