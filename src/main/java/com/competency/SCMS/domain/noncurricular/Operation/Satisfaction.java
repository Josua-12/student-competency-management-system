package com.competency.SCMS.domain.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramSchedule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "program_satisfaction")
@Getter @Setter
public class Satisfaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "schedule_id")
    private ProgramSchedule schedule;

    @Column(name = "student_id", nullable = false)
    private Long studentId; // 실명/학번 등은 별도 조회 or join. 여기선 익명화 가정.

    @Column(nullable = false)
    private Integer rating; // 1~5

    @Column(columnDefinition = "text")
    private String feedback;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;
}
