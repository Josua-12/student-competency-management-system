package com.competency.SCMS.domain.noncurricular.Performance;

import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.noncurricular.AlignmentStrength;
import com.competency.SCMS.domain.noncurricular.ProficiencyLevel;
import com.competency.SCMS.domain.noncurricular.Program;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

/**
 * 비교과 프로그램이 어떤 역량을 어느 강도로, 어느 목표수준까지 키우는지의 매핑
 */
@Entity
@Table(name = "program_competencies",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_program_competency",
                columnNames = {"prog_id","comp_id"}
        ),
        indexes = {
                @Index(name = "ix_prog_comp_program", columnList = "prog_id"),
                @Index(name = "ix_prog_comp_comp",    columnList = "comp_id")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Competency extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prog_comp_id")
    private Long id;

    /** 대상 프로그램 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    /** 매핑되는 역량 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comp_id", nullable = false)
    private Competency competency;

    /** 연계 강도 (필수/강/중/약) */
    @Enumerated(EnumType.STRING)
    @Column(name = "strength", length = 20, nullable = false)
    private AlignmentStrength strength = AlignmentStrength.MEDIUM;

    /** 가중치(0~100) — 통계/추천 로직에서 사용 */
    @Min(0) @Max(100)
    @Column(name = "weight_pct", nullable = false)
    private Integer weightPercent = 50;

    /** 목표 숙련도(선택) — 커리큘럼 설계 시 사용 */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_level", length = 20)
    private ProficiencyLevel targetLevel;  // null 가능
}

