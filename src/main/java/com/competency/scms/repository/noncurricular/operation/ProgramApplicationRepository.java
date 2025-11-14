package com.competency.scms.repository.noncurricular.operation;


import com.competency.scms.domain.noncurricular.operation.ApprovalStatus;
import com.competency.scms.domain.noncurricular.operation.ProgramApplication;
import com.competency.scms.domain.noncurricular.operation.ApplicationStatus;
import com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorApprovalRequestDto;
import com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentLatestApplicationDto;
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

    int countByProgram_ProgramIdAndStatus(Long programId, ApprovalStatus status);

    // 학생 대시보드 요약
    long countByStudentId(Long studentId);

    long countByStudentIdAndStatusIn(Long studentId, List<ApplicationStatus> statuses);

    long countByStudent_IdAndStatus(Long studentId, ApplicationStatus status);

    // 학생 최근 신청 3건
    @Query("""
        select new com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentLatestApplicationDto(
            p.programId,
            p.title,
            concat(
                function('date_format', p.programStartAt, '%Y-%m-%d'),
                ' ~ ',
                function('date_format', p.programEndAt, '%Y-%m-%d')
            ),
            app.status
        )
        from ProgramApplication app
        join app.program p
        where app.student.id = :studentId
        order by app.appliedAt desc
        """)
    List<StudentLatestApplicationDto> findLatestApplications(Long studentId, Pageable pageable);

    // 운영자 승인 요청 목록 (최근 n개)
    @Query("""
        select new com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorApprovalRequestDto(
            p.programId,
            p.title,
            function('date', app.appliedAt),
            app.status
        )
        from ProgramApplication app
        join app.program p
        where app.status = com.competency.scms.domain.noncurricular.operation.ApplicationStatus.PENDING
        order by app.appliedAt desc
        """)
    List<OperatorApprovalRequestDto> findPendingApprovalRequests(Pageable pageable);
}

