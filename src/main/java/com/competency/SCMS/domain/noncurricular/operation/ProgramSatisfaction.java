package com.competency.SCMS.domain.noncurricular.operation;

import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramSchedule;
import com.competency.SCMS.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@Entity
@Table(name = "program_satisfaction",
        uniqueConstraints = @UniqueConstraint(name="uq_satis_unique",
                columnNames={"prog_id","schd_id","student_id"}))
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
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="student_id", nullable=false)
    private User student;

    @Column(nullable=false) private Integer rating;   // 1~5
    @Column(length=500)     private String comment;
}


//        ),
//        indexes = {
//                @Index(name="ix_satis_prog", columnList="prog_id"),
//                @Index(name="ix_satis_student", columnList="student_id"),
//                @Index(name="ix_satis_rating", columnList="rating")
//        }


