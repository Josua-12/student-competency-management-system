package com.competency.SCMS.repository.noncurricular.mileage;

import com.competency.SCMS.domain.noncurricular.mileage.MileageRecord;
import com.competency.SCMS.domain.noncurricular.mileage.MileageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface MileageRecordRepository extends JpaRepository<MileageRecord, Long>, JpaSpecificationExecutor<MileageRecord> {

    List<MileageRecord> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    @Query("select coalesce(sum(m.points),0) from MileageRecord m where m.studentId=:studentId")
    Integer getTotalPoints(@Param("studentId") Long studentId);

    List<MileageRecord> findAllByProgramId(Long programId);

    Page<MileageRecord> findByStudent_UserIdOrderByMileageIdDesc(Long studentId, Pageable pageable);
    List<MileageRecord> findByStudent_UserIdAndType(Long studentId, MileageType type);
    Integer sumPointsByStudent_UserId(Long studentId); // 구현 필요 시 @Query로 합계 처리

}
