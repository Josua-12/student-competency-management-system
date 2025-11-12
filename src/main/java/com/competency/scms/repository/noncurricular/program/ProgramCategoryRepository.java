package com.competency.scms.repository.noncurricular.program;

import com.competency.scms.domain.noncurricular.program.ProgramCategory;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface ProgramCategoryRepository extends JpaRepository<ProgramCategory, Long> {
    Optional<ProgramCategory> findByCode(String code);
    List<ProgramCategory> findAllByUseYnTrueOrderBySortOrderAsc();
    boolean existsByCode(String code);
    boolean existsByName(String name);
}

