package com.competency.SCMS.repository.noncurricular.program;
import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long>, JpaSpecificationExecutor<Program> {

    Optional<Program> findByCode(String code);

    @EntityGraph(attributePaths = {"category"})
    Page<Program> findByStatusAndDeletedFalse(ProgramStatus status, Pageable pageable);

    @Query("select p from Program p where p.deleted=false and (:catgId is null or p.category.id=:catgId) " +
            "and (:kw is null or (lower(p.title) like lower(concat('%',:kw,'%')) or lower(p.summary) like lower(concat('%',:kw,'%'))))")
    Page<Program> search(@Param("catgId") Long catgId, @Param("kw") String keyword, Pageable pageable);

    @Query("select p from Program p where p.deleted=false and " +
            "((:from is null or p.recruitStartAt <= :to) and (:to is null or p.recruitEndAt >= :from))")
    Page<Program> findRecruitingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);
}
