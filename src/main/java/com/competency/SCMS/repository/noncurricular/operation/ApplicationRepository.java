package com.competency.SCMS.repository.noncurricular.operation;


import com.competency.SCMS.domain.noncurricular.operation.Application;
import com.competency.SCMS.domain.noncurricular.operation.ApplicationStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.util.*;

public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    Optional<Application> findByProgramIdAndScheduleIdAndStudentId(Long programId, Long scheduleId, Long studentId);
    boolean existsByProgramIdAndScheduleIdAndStudentId(Long programId, Long scheduleId, Long studentId);

    Page<Application> findByProgramIdAndStatus(Long programId, ApplicationStatus status, Pageable pageable);

    @Query("select a from ProgramApplication a " +
            "where a.studentId=:studentId and (:status is null or a.status=:status) " +
            "order by a.createdAt desc")
    Page<Application> findMyApplications(@Param("studentId") Long studentId,
                                                @Param("status") ApplicationStatus status,
                                                Pageable pageable);
}

