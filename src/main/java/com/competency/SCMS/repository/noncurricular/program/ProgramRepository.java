package com.competency.SCMS.repository.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramStatus;
import com.competency.SCMS.dto.noncurricular.program.ProgramListRow;
import com.competency.SCMS.dto.noncurricular.program.ProgramSearchCond;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, Long>, JpaSpecificationExecutor<Program>, ProgramRepositoryCustom {

    // 목록/검색
    Page<Program> findByStatus(ProgramStatus status, Pageable pageable);
    Page<Program> findByCategory_CatgId(Long catgId, Pageable pageable);
    Page<Program> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // 기간 교집합(운영중 판별 등에 활용)
    @Query("""
       select p from Program p
       where p.startDate <= :to and p.endDate >= :from
    """)
    Page<Program> findActiveInPeriod(LocalDateTime from, LocalDateTime to, Pageable pageable);

    // 권한별: 내가 개설한 프로그램
    Page<Program> findByOwner_UserIdOrderByProgramIdDesc(Long ownerId, Pageable pageable);

    // 상태 전환 보조
    long countByStatus(ProgramStatus status);

    // 상세 조회 시 즉시 로딩 최적화(필요 시)
    @EntityGraph(attributePaths = {"category"})
    Optional<Program> findWithCategoryByProgramId(Long programId);

    @Query("select p from Program p where p.deleted=false and (:catgId is null or p.category.id=:catgId) " +
            "and (:kw is null or (lower(p.title) like lower(concat('%',:kw,'%')) or lower(p.summary) like lower(concat('%',:kw,'%'))))")
    Page<Program> search(@Param("catgId") Long catgId, @Param("kw") String keyword, Pageable pageable);

    @Query("select p from Program p where p.deleted=false and " +
            "((:from is null or p.recruitStartAt <= :to) and (:to is null or p.recruitEndAt >= :from))")
    Page<Program> findRecruitingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);


}

interface ProgramRepositoryCustom {
    Page<ProgramListRow> searchForOperatorList(ProgramSearchCond cond, Pageable pageable);
}
