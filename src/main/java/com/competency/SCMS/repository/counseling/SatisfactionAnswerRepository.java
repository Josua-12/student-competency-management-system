package com.competency.SCMS.repository.counseling;

import com.competency.SCMS.domain.counseling.CounselingSatisfaction;
import com.competency.SCMS.domain.counseling.SatisfactionAnswer;
import com.competency.SCMS.domain.counseling.SatisfactionQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SatisfactionAnswerRepository extends JpaRepository<SatisfactionAnswer, Long> {

    // 만족도별 답변 조회
    Page<SatisfactionAnswer> findBySatisfactionOrderByQuestionDisplayOrderAsc(CounselingSatisfaction satisfaction, Pageable pageable);

    // 질문별 답변 통계 (평점 평균)
    @Query("SELECT AVG(sa.ratingValue) FROM SatisfactionAnswer sa WHERE sa.question = :question AND sa.ratingValue IS NOT NULL")
    Double getAverageRatingByQuestion(@Param("question") SatisfactionQuestion question);

    // 질문별 답변 개수
    @Query("SELECT COUNT(sa) FROM SatisfactionAnswer sa WHERE sa.question = :question")
    Long countByQuestion(@Param("question") SatisfactionQuestion question);
}
