package com.competency.SCMS.domain.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramSchedule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey_satisfaction")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SatisfactionSurvey {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id")
    private Program program;

    // 전체회차 선택 시 이 테이블은 비울 수 있음
    @ManyToMany
    @JoinTable(name = "survey_satisfaction_schedule",
            joinColumns = @JoinColumn(name = "survey_id"),
            inverseJoinColumns = @JoinColumn(name = "schd_id"))
    @Builder.Default
    private List<ProgramSchedule> schedules = new ArrayList<>();

    private String title;

    private LocalDateTime openStart;
    private LocalDateTime openEnd;

    private boolean anonymous;
    private boolean requiredToComplete;

    @Enumerated(EnumType.STRING)
    private SurveyStatus status;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNo ASC, id ASC")
    @Builder.Default
    private List<SurveyQuestion> questions = new ArrayList<>();
}
