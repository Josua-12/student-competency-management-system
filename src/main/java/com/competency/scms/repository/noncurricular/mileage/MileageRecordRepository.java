package com.competency.scms.repository.noncurricular.mileage;

import com.competency.scms.domain.noncurricular.mileage.MileageRecord;
import com.competency.scms.domain.noncurricular.mileage.MileageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface MileageRecordRepository
        extends JpaRepository<MileageRecord, Long>, JpaSpecificationExecutor<MileageRecord> {

    // 특정 프로그램(및 선택 회차)의 이력 조회
    @Query("""
        select mr
          from MileageRecord mr
         where mr.program.progId = :progId
           and (:schdId is null or mr.schedule.schdId = :schdId)
         order by mr.createdAt asc
    """)
    List<MileageRecord> findHistory(@Param("progId") Long programId,
                                    @Param("schdId") Long scheduleId);

    // 학생/프로그램 기준 누적 포인트
    @Query("""
        select coalesce(sum(mr.pointsSigned), 0)
          from MileageRecord mr
         where mr.program.progId = :progId
           and mr.student.userId = :studentId
    """)
    Integer sumPointsByProgramAndStudent(@Param("progId") Long programId,
                                         @Param("studentId") Long studentId);

    /** 단일 값 필드(Long student) 기준 조회 */
    List<MileageRecord> findByStudentOrderByCreatedAtDesc(Long studentId);
    Page<MileageRecord> findByStudentOrderByMileageIdDesc(Long studentId, Pageable pageable);
    List<MileageRecord> findByStudentAndType(Long studentId, MileageType type);

    /** 합계 (JPQL) — 정수 SUM은 Long으로 받는 게 안전 */
    @Query("""
        select coalesce(sum(m.points), 0)
          from MileageRecord m
         where m.student = :studentId
    """)
    Long sumPointsByStudent(@Param("studentId") Long studentId);

    /** 프로그램 기준 조회 (Program 연관의 PK는 programId) */
    List<MileageRecord> findAllByProgram_ProgramId(Long programId);
}

