package com.competency.SCMS.repository.noncurricular.mileage;

import com.competency.SCMS.domain.noncurricular.mileage.MileageRecord;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface MileageRecordRepo extends JpaRepository<MileageRecord, Long>, JpaSpecificationExecutor<MileageRecord> {

    List<MileageRecord> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    @Query("select coalesce(sum(m.points),0) from MileageRecord m where m.studentId=:studentId")
    Integer getTotalPoints(@Param("studentId") Long studentId);

    List<MileageRecord> findAllByProgramId(Long programId);
}
