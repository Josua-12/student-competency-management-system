package com.competency.scms.repository.noncurricular.mileage;

import com.competency.scms.domain.noncurricular.mileage.MileageRecord;
import com.competency.scms.domain.noncurricular.mileage.MileageType;
import com.competency.scms.domain.user.User;
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
         where mr.program.programId = :progId
         order by mr.createdAt asc
    """)
    List<MileageRecord> findHistory(@Param("progId") Long programId,
                                    @Param("schdId") Long scheduleId);

    // 학생/프로그램 기준 누적 포인트
    @Query("""
        select coalesce(sum(mr.points), 0)
          from MileageRecord mr
         where mr.program.programId = :progId
           and mr.student = :studentId
    """)
    Integer sumPointsByProgramAndStudent(@Param("progId") Long programId,
                                         @Param("studentId") User student);

    /** 단일 값 필드(Long student) 기준 조회 */
    List<MileageRecord> findByStudentOrderByCreatedAtDesc(User student);
    Page<MileageRecord> findByStudentOrderByMileageIdDesc(User student, Pageable pageable);
    List<MileageRecord> findByStudentAndType(User student, MileageType type);

    /** 합계 (JPQL) — 정수 SUM은 Long으로 받는 게 안전 */
    @Query("""
        select coalesce(sum(m.points), 0)
          from MileageRecord m
         where m.student = :student
    """)
    Long sumPointsByStudent(@Param("student") User student);

    /** 프로그램 기준 조회 (Program 연관의 PK는 programId) */
    List<MileageRecord> findAllByProgram_ProgramId(Long programId);
}

