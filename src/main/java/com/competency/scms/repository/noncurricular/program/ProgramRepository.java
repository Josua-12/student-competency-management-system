package com.competency.scms.repository.noncurricular.program;

import com.competency.scms.domain.noncurricular.operation.ApprovalStatus;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.program.ProgramCategoryType;
import com.competency.scms.domain.noncurricular.program.ProgramStatus;
import com.competency.scms.dto.noncurricular.mileage.ProgramSearchRowDto;
import com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorCategoryStatDto;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, Long>, JpaSpecificationExecutor<Program>,
        ProgramRepositoryCustom {

    // 목록/검색
    Page<Program> findByStatus(ProgramStatus status, Pageable pageable);
//    Page<Program> findByCategory_CatgId(Long catgId, Pageable pageable);
    Page<Program> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Program> findByCategory(ProgramCategoryType category, Pageable pageable);
    // 여러 카테고리 한 번에
    Page<Program> findByCategoryIn(Collection<ProgramCategoryType> categories, Pageable pageable);
    // 기간 교집합(운영중 판별 등에 활용)
    /**
     * 운영기간(programStartAt~programEndAt) 겹치는 프로그램 조회
     */
    @Query("""
        select p
        from Program p
        where p.deleted = false
          and (:from is null or p.programEndAt   is null or p.programEndAt   >= :from)
          and (:to   is null or p.programStartAt is null or p.programStartAt <= :to)
    """)
    Page<Program> findActiveInPeriod(@Param("from") LocalDateTime from,
                                     @Param("to")   LocalDateTime to,
                                     Pageable pageable);

    /**
     * 모집기간(recruitStartAt~recruitEndAt) 겹치는 프로그램 조회
     */
    @Query("""
        select p
        from Program p
        where p.deleted = false
          and (:from is null or p.recruitEndAt   is null or p.recruitEndAt   >= :from)
          and (:to   is null or p.recruitStartAt is null or p.recruitStartAt <= :to)
    """)
    Page<Program> findRecruitingInPeriod(@Param("from") LocalDateTime from,
                                         @Param("to")   LocalDateTime to,
                                         Pageable pageable);

    // 권한별: 내가 개설한 프로그램
    Page<Program> findByOwner_IdOrderByProgramIdDesc(Long ownerId, Pageable pageable);

    // 상태 전환 보조
    long countByStatus(ProgramStatus status);

    // 상세 조회 시 즉시 로딩 최적화(필요 시)
    @EntityGraph(attributePaths = {"category"})
    Optional<Program> findWithCategoryByProgramId(Long programId);

    @Query(
            value = "select p from Program p " +
                    "where (:catg is null or p.category = :catg) " +
                    "  and (:kw is null " +
                    "       or lower(p.title) like :kw " +
                    "       or lower(p.description) like :kw)",
            countQuery = "select count(p) from Program p " +
                    "where (:catg is null or p.category = :catg) " +
                    "  and (:kw is null " +
                    "       or lower(p.title) like :kw " +
                    "       or lower(p.description) like :kw)"
    )
    Page<Program> search(@Param("catg") ProgramCategoryType catg,
                         @Param("kw") String kw,
                         Pageable pageable);
    @Query("select p from Program p where p.deleted=false and " +
            "((:from is null or p.recruitStartAt <= :to) and (:to is null or p.recruitEndAt >= :from))")
    Page<Program> findRecruitingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

    @Query("""
      select new com.competency.scms.dto.noncurricular.mileage.ProgramSearchRowDto(
         p.programId,
         p.title,
         p.department.name,
         concat(function('date_format', p.programStartAt, '%Y-%m-%d'),
                ' ~ ',
                function('date_format', p.programEndAt, '%Y-%m-%d')),
         p.status
      )
        from Program p
       where (:status is null or p.status = :status)
         and ( :q is null
            or lower(p.title) like concat('%', lower(:q), '%')
            or lower(p.description) like concat('%', lower(:q), '%')
       )
       order by p.programStartAt desc, p.programId desc
    """)
    Page<ProgramSearchRowDto> searchForMileage(
            @Param("q") String keyword,
            @Param("status") ProgramStatus status,
            Pageable pageable);
//    // 부서/카테고리만 즉시 로딩(프로젝션용)
//    @EntityGraph(attributePaths = {"department", "category"})
//    Optional<Program> findWithDeptAndCategoryById(Long id);
//
//    // 소유/권한 체크용 (exists)
//    boolean existsByProgramIdAndCreatedBy(Long id, Long operatorId);

    // 기본 상세 조회 (부서까지)
    @EntityGraph(attributePaths = {"department"})
    Optional<Program> findByProgramId(Long programId);

    // 권한 체크용 예시 (createdBy 사용 시)
    boolean existsByProgramIdAndCreatedBy(Long programId, Long userId);

    // 권한 체크용 예시 (owner 연관관계 사용 시)
    boolean existsByProgramIdAndOwner_Id(Long programId, Long ownerId);

    //학생용
    @Query("""
           select p
           from Program p
           where p.deleted = false
             and (:approvalStatus is null or p.approvalStatus = :approvalStatus)
             and (:keyword is null or :keyword = '' or p.title like concat('%', :keyword, '%'))
             and (:category is null or p.category = :category)
             and (:status is null or p.status = :status)
             and (:deptCode is null or :deptCode = '' or p.department.code = :deptCode)
             and (:from is null or p.recruitStartAt >= :from)
             and (:to   is null or p.recruitEndAt   <= :to)
           """)
    Page<Program> searchStudentPrograms(
            @Param("keyword") String keyword,
            @Param("category") ProgramCategoryType category,
            @Param("status") ProgramStatus status,
            @Param("deptCode") String deptCode,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            Pageable pageable
    );

    // 최근 n개월 월별 개설 수 + 참여자 수 (참여자 수는 join으로 집계)
    @Query("""
        select new com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorMonthlyProgramStatDto(
            concat(function('date_format', p.programStartAt, '%Y-%m')),
            count(distinct p.programId),
            count(distinct app.applicationId)
        )
        from Program p
        left join p.programApplications app
        where p.programStartAt >= :fromDate
        group by function('date_format', p.programStartAt, '%Y-%m')
        order by function('date_format', p.programStartAt, '%Y-%m')
        """)
    List<com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorMonthlyProgramStatDto> findMonthlyProgramStats(LocalDate fromDate);

    // 카테고리별 프로그램 수 (top5)
    @Query("""
        select new com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorCategoryStatDto(
            p.category,
            count(p),
            coalesce(sum(p.currentParticipants), 0)
        )
        from Program p
        group by p.category
        order by count(p) desc
        """)
    List<OperatorCategoryStatDto> findTopCategoryStats(org.springframework.data.domain.Pageable pageable);

    // 학생 추천용(간단 버전): 앞으로 진행 예정 + 모집중 프로그램 중 일부
    @Query("""
        select p
        from Program p
        where p.recruitStartAt <= current_date
          and p.recruitEndAt >= current_date
          and p.deleted = false
        order by p.recruitEndAt asc
        """)
    List<Program> findRecommendablePrograms(org.springframework.data.domain.Pageable pageable);

}
