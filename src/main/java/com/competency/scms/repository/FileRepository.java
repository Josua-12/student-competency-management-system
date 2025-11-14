package com.competency.scms.repository;

import com.competency.scms.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByProgram_programIdOrderByCreatedAtAsc(Long programId);
    List<File> findAllByProgram_programId(Long programId);
}
