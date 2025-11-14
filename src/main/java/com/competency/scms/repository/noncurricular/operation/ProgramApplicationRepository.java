package com.competency.scms.repository.noncurricular.operation;


import com.competency.scms.domain.noncurricular.operation.ProgramApplication;
import com.competency.scms.domain.noncurricular.operation.ApplicationStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

public interface ProgramApplicationRepository
        extends JpaRepository<ProgramApplication, Long>, JpaSpecificationExecutor<ProgramApplication> {

    @Query(
            value = """
        select a
        from ProgramApplication a
        where a.student.id = :studentId
          and (:status is null or a.status = :status)
        order by a.createdAt desc
      """,
            countQuery = """
        select count(a)
        from ProgramApplication a
        where a.student.id = :studentId
          and (:status is null or a.status = :status)
      """

    )
    Page<ProgramApplication> findMyApplications(@Param("studentId") Long studentId,
                                                @Param("status") ApplicationStatus status,
                                                Pageable pageable);
    @Query("""
        select count(pa) from ProgramApplication pa
        where pa.program.id = :programId
    """)
    long countAllByProgramId(Long programId);

    @Query("""
        select count(pa) from ProgramApplication pa
        where pa.program.id = :programId and pa.status = :status
    """)
    long countByProgramIdAndStatus(Long programId, ApplicationStatus status);

    // 중복 신청 방지용
    boolean existsByProgram_ProgramIdAndStudent_Id(Long programId, Long studentId);

    Optional<ProgramApplication> findByProgram_ProgramIdAndStudent_Id(Long programId, Long studentId);

    // 상태별 목록
    Page<ProgramApplication> findByProgram_ProgramIdAndStatus(Long programId, ApplicationStatus status, Pageable pageable);

    // 회차 지정 신청 목록
    Page<ProgramApplication> findByProgram_ProgramIdAndSchedule_ScheduleId(Long programId, Long scheduleId, Pageable pageable);

    // 학생의 신청 이력
    Page<ProgramApplication> findByStudent_IdOrderByApplicationIdDesc(Long studentId, Pageable pageable);

    long countByProgram_ProgramIdAndStatus(Long programId, ApplicationStatus status);

    @Query("""
      select pa
        from ProgramApplication pa
        join fetch pa.student s
        join fetch pa.program p
        left join fetch pa.schedule sch
       where p.programId = :progId
         and (:schdId is null or sch.scheduleId = :schdId)
    """)
    List<ProgramApplication> findEligibleForMileage(
            @Param("progId") Long progId,
            @Param("schdId") Long schdId);
}

