package com.competency.SCMS.repository.noncurricular.operation;


import com.competency.SCMS.domain.noncurricular.operation.Application;
import com.competency.SCMS.domain.noncurricular.operation.ApplicationStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.util.*;

public interface ApplicationRepository
        extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    @Query(
            value = """
        select a
        from Application a
        where a.studentId = :studentId
          and (:status is null or a.status = :status)
        order by a.createdAt desc
      """,
            countQuery = """
        select count(a)
        from Application a
        where a.studentId = :studentId
          and (:status is null or a.status = :status)
      """
    )
    Page<Application> findMyApplications(@Param("studentId") Long studentId,
                                         @Param("status") ApplicationStatus status,
                                         Pageable pageable);
}

