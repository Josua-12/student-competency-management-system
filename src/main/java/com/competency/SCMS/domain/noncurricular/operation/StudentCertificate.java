package com.competency.SCMS.domain.noncurricular.operation;

import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="student_certificates",
        uniqueConstraints=@UniqueConstraint(columnNames={"program_id","student_id"}))
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
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private Boolean issued = false;
    private String certificateNo;
    private String filePath;

}
