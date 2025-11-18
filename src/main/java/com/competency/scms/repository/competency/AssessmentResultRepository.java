package com.competency.scms.repository.competency;

import com.competency.scms.domain.Department;
import com.competency.scms.domain.competency.AssessmentResult;
import com.competency.scms.domain.competency.AssessmentResultStatus;
import com.competency.scms.domain.competency.AssessmentSection;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.competency.CompetencyAverageDto;
import com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentCompetencyScoreDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {

    // 특정 학생이 응시한 모든 진단 결과 목록 조회
    List<AssessmentResult> findByUser(User user);

    // 특정 학생이 응시한 모든 진단 결과 목록 조회 (ID)
    List<AssessmentResult> findByUserId(Long userId);

    // 특정 학생이 특정 진단 섹션에 응시한 결과를 조회
    Optional<AssessmentResult> findByUserAndAssessmentSection(User user, AssessmentSection section);

    // 특정 진단 회차에 응시한 모든 결과 조회
    List<AssessmentResult> findByAssessmentSection(AssessmentSection section);

    // 특정 진단 회차에 응시한 특정 학과 학생의 모든 결과 조회
    List<AssessmentResult> findByAssessmentSectionAndUser_Department_Name(
            AssessmentSection section, String departmentName
    );

    Optional<AssessmentResult> findFirstByUserIdAndStatusOrderBySubmittedAtDesc(Long userId, AssessmentResultStatus status);

    /**
     * (N+1 문제 해결용)
     * 특정 사용자의 '완료된' 모든 진단 결과를
     * 연관된 AssessmentSection과 함께 Fetch Join하여 조회합니다.
     * 완료된 순서 내림차순 정렬
     */
    @Query("SELECT r FROM AssessmentResult r " +
            "JOIN FETCH r.assessmentSection s " +
            "WHERE r.user.id = :userId AND r.status = 'COMPLETED' " +
            "ORDER BY r.submittedAt DESC")
    List<AssessmentResult> findCompletedWithSectionByUserId(@Param("userId") Long userId);

    /**
     * 특정 섹션, 특정 유저의 '특정 상태'인 Result를 조회
     * (주로 Draft 상태를 찾기 위해 사용)
     */
    Optional<AssessmentResult> findByAssessmentSectionIdAndUserIdAndStatus(
            Long sectionId, Long userId, AssessmentResultStatus status
    );

    // 비교과 대시보드관련 필요에 의한 추가 - 2025.11.14 11:36 JHE
    // 학생 최신 진단 1건
    @Query("""
        select ar
        from AssessmentResult ar
        where ar.user.id = :studentId
        order by ar.submittedAt desc
        """)
    List<AssessmentResult> findLatestResult(Long studentId, Pageable pageable);

    @Query("""
        select new com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentCompetencyScoreDto(
            'competency', 0.0
        )
        from AssessmentResult ar
        where ar.id = :id
        """)
    List<StudentCompetencyScoreDto> findScoresByResultId(Long id);


    // 통계 조회 메서드

    /**
     * 특정 학과의 평균 점수 계산 (viewResult에서 사용)
     * - 특정 학과(department) 소속 사용자들이
     * - 특정 핵심 역량들(parentIds)에 대해 완료한 응답의 평균 점수를 'CompetencyAverageDto'로 반환
     */
    @Query("SELECT new com.competency.scms.dto.competency.CompetencyAverageDto(c.parent.id, AVG(opt.score)) " +
            "FROM AssessmentResponse res " +
            "JOIN res.assessmentOption opt " +
            "JOIN res.question q " +
            "JOIN q.competency c " +
            "JOIN res.assessmentResult ar " +
            "JOIN ar.user u " +
            "WHERE ar.status = 'COMPLETED' " +
            "  AND u.department = :department " + // 학과 필터
            "  AND c.parent.id IN :parentIds " +  // 핵심 역량 ID 리스트 필터
            "  AND ar.assessmentSection.id = :assessmentSectionId " +   // 진단 세션 ID 필터
            "GROUP BY c.parent.id")
    List<CompetencyAverageDto> findDepartmentAverages(
            @Param("department") Department department,
            @Param("parentIds") List<Long> parentIds,
            @Param("assessmentSectionId") Long assessmentSectionId
    );

    /**
     * [통계] 학교 전체 평균 점수 계산 (viewResult에서 사용)
     */
    @Query("SELECT new com.competency.scms.dto.competency.CompetencyAverageDto(c.parent.id, AVG(opt.score)) " +
            "FROM AssessmentResponse res " +
            "JOIN res.assessmentOption opt " +
            "JOIN res.question q " +
            "JOIN q.competency c " +
            "JOIN res.assessmentResult ar " +
            "WHERE ar.status = 'COMPLETED' " +
            "  AND c.parent.id IN :parentIds " +
            "  AND ar.assessmentSection.id = :assessmentSectionId " +
            "GROUP BY c.parent.id")
    List<CompetencyAverageDto> findUniversityAverages(
            @Param("parentIds") List<Long> parentIds,
            @Param("assessmentSectionId") Long assessmentSectionId
    );

    Long user(User user);
}
