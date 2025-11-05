package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.StudentCertificate;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentCertificateRepository extends JpaRepository<StudentCertificate, Long> {
    boolean existsByProgram_ProgramIdAndStudent_UserId(Long programId, Long studentId);
    Optional<StudentCertificate> findByProgram_ProgramIdAndStudent_UserId(Long programId, Long studentId);
}

