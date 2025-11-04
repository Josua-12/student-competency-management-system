package com.competency.SCMS.domain.noncurricular.Core;

import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.noncurricular.ProgramStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "programs",
        indexes = {
                @Index(name = "ix_programs_catg",          columnList = "catg_id"),
                @Index(name = "ix_programs_status",        columnList = "status"),
                @Index(name = "ix_programs_recruit_range", columnList = "recruit_start_at,recruit_end_at"),
                @Index(name = "ix_programs_prog_range",    columnList = "program_start_at,program_end_at"),
                @Index(name = "ix_programs_title",         columnList = "title")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_programs_code", columnNames = "prog_code")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Program extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prog_id")
    private Long id;

    @Column(name = "prog_code", length = 50, nullable = false)
    private String code;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 500)
    private String summary;

    @Lob
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "catg_id", nullable = false)
    private ProgramCategory category;

    @Column(name = "dept_id")
    private Long deptId; // 추후 Department 엔티티로 교체 가능

    @Column(name = "organizer_user_id", nullable = false)
    private Long organizerUserId; // 추후 User 엔티티로 교체 가능

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ProgramStatus status = ProgramStatus.DRAFT;

    @Column(name = "recruit_start_at")
    private LocalDateTime recruitStartAt;

    @Column(name = "recruit_end_at")
    private LocalDateTime recruitEndAt;

    @Column(name = "program_start_at")
    private LocalDateTime programStartAt;

    @Column(name = "program_end_at")
    private LocalDateTime programEndAt;

    private Integer capacity;

    @Column(name = "location_text", length = 200)
    private String locationText;

    @Column(name = "online_yn", nullable = false)
    private boolean online = false;

    @Column(nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProgramSchedule> schedules = new ArrayList<>();

    public void addSchedule(ProgramSchedule s) {
        s.setProgram(this);
        this.schedules.add(s);
    }
}

