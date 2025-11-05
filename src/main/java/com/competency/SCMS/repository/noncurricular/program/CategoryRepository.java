package com.competency.SCMS.repository.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.ProgramCategory;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface CategoryRepository extends JpaRepository<ProgramCategory, Long> {
    Optional<ProgramCategory> findByCode(String code);
    List<ProgramCategory> findAllByUseYnTrueOrderBySortOrderAsc();
    boolean existsByCode(String code);
}

