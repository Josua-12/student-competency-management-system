package com.competency.scms.repository.competency;

import com.competency.scms.domain.competency.AssessmentResult;
import com.competency.scms.domain.competency.AssessmentResultStatus;
import com.competency.scms.domain.competency.AssessmentSection;
import com.competency.scms.domain.user.User;
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
}
