package com.competency.scms.domain.noncurricular.operation;

import com.competency.scms.domain.BaseEntity;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.program.ProgramSchedule;
import com.competency.scms.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "program_satisfaction",
        uniqueConstraints = @UniqueConstraint(name="uq_satis_unique",
                columnNames={"prog_id","schd_id","user_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramSatisfaction extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="satisfaction_id")
    private Long satisfactionId;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="prog_id", nullable=false)
    private Program program;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="schd_id", nullable=false)
    private ProgramSchedule schedule;

    // 학생을 키만 들고갈지, User 연관으로 들고갈지는 프로젝트 정책에 따라
    // (지금 zip에는 User 연관으로 잡은 버전/키 보관 버전이 혼재)
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
    private User student;

    @Column(nullable=false) private Integer rating;   // 1~5

    @Column(columnDefinition = "text")
    private String feedback;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;
}


//        ),
//        indexes = {
//                @Index(name="ix_satis_prog", columnList="prog_id"),
//                @Index(name="ix_satis_student", columnList="user_id"),
//                @Index(name="ix_satis_rating", columnList="rating")
//        }


