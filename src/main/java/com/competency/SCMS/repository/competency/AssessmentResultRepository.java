package com.competency.SCMS.repository.competency;

import com.competency.SCMS.domain.competency.AssessmentResult;
import com.competency.SCMS.domain.competency.AssessmentSection;
import com.competency.SCMS.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {

    // 특정 학생이 응시한 모든 진단 결과 목록 조회
    List<AssessmentResult> findByUser(User user);

    // 특정 학생이 특정 진단 섹션에 응시한 결과를 조회
    Optional<AssessmentResult> findByUserAndAssessmentSection(User user, AssessmentSection section);

    // 특정 진단 회차에 응시한 모든 결과 조회
    List<AssessmentResult> findByAssessmentSection(AssessmentSection section);

    // 특정 진단 회차에 응시한 특정 학과 학생의 모든 결과 조회
    List<AssessmentResult> findByAssessmentSectionAndUserDepartment(
            AssessmentSection section, String departmentName
    );

}
