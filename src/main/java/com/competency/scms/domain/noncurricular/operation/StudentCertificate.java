package com.competency.scms.domain.noncurricular.operation;

import com.competency.scms.domain.BaseEntity;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="student_certificates",
        uniqueConstraints=@UniqueConstraint(columnNames={"prog_id","user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCertificate extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private Boolean issued = false;
    private String certificateNo;
    private String filePath;

}
