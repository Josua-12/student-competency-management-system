package com.competency.SCMS.domain.noncurricular.Core;

import com.competency.SCMS.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    @Column(name = "session_no", nullable = false)
    private Integer sessionNo;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "place_text", length = 200)
    private String placeText;

    @Column(name = "capacity_ovr")
    private Integer capacityOverride;

    @Column(length = 255)
    private String remarks;
}

