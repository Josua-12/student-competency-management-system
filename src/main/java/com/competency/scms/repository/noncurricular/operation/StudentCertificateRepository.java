package com.competency.scms.repository.noncurricular.operation;

import com.competency.scms.domain.noncurricular.operation.StudentCertificate;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentCertificateRepository extends JpaRepository<StudentCertificate, Long> {
    boolean existsByProgram_ProgramIdAndStudent_Id(Long programId, Long studentId);
    Optional<StudentCertificate> findByProgram_ProgramIdAndStudent_Id(Long programId, Long studentId);
}

