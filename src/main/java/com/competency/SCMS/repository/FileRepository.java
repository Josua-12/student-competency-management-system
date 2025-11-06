package com.competency.SCMS.repository;

import com.competency.SCMS.domain.noncurricular.ProgramFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<ProgramFile, Long> {
    List<ProgramFile> findAllByProgram_IdOrderByCreatedAtAsc(Long programId);
}
