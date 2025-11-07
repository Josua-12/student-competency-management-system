package com.competency.SCMS.repository.noncurricular.linkCompetency;

import com.competency.SCMS.domain.noncurricular.linkCompetency.LinkCompetency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LinkCompetencyRepository extends JpaRepository<LinkCompetency, Long> {
    /**
     * 특정 프로그램에 연계된 '활성화된' 역량 목록 조회
     * Program.programId + Competency.isActive = true 기준
     */
    List<LinkCompetency> findAllByProgram_ProgramIdAndCompetency_IsActiveTrueOrderByCompetency_NameAsc(Long programId);

    /**
     * 명시적 JPQL 버전 (보다 안전하게)
     */
    @Query("""
        select lc
          from LinkCompetency lc
          join fetch lc.competency c
         where lc.program.programId = :programId
           and c.isActive = true
         order by c.name asc
    """)
    List<LinkCompetency> findAllActiveByProgram(@Param("programId") Long programId);

//    List<LinkCompetency> findAllByProgram_IdAndCompetency_ActiveTrueOrderByCompetency_NameAsc(Long programId);
//    boolean existsByCode(String code);
//    List<LinkCompetency> findAllByProgram_programId(Long programId);
}



