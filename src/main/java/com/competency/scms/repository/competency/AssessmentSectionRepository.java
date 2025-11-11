package com.competency.SCMS.repository.competency;

import com.competency.SCMS.domain.competency.AssessmentSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AssessmentSectionRepository extends JpaRepository<AssessmentSection, Long> {

    // 현재 날짜가 시작일과 종료일 사이에 있고, 활성화된 진단만 조회
    List<AssessmentSection> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
            LocalDateTime startDate, LocalDateTime endDate

    );

    // 활성화 되어있고, 진단 시작일이 지금보다 이전인 모든 진단 섹션을 시작일 기준 내림차순 조회
    List<AssessmentSection> findByIsActiveTrueAndStartDateBeforeOrderByStartDateDesc(LocalDateTime currentDate);


    // 활성화된 모든 진단 목록 (관리자용)
    List<AssessmentSection> findByisActiveTrueOrderByStartDateDesc();

}
