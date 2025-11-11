package com.competency.scms.domain.counseling;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "satisfaction_answers")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SatisfactionAnswer { //하나의 설문에 있는 설문문항들 중 문항 하나에 대해 작성한 답변 1개

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "satisfaction_id", nullable = false)
    private CounselingSatisfaction satisfaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private SatisfactionQuestion question;

    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "답변은 2000자를 초과할 수 없습니다")
    private String answerText; // TEXT 타입 답변
    
    @Min(value = 1, message = "평점은 1 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5 이하여야 합니다")
    private Integer ratingValue; // RATING 타입 답변 (1-5점 등)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption; // MULTIPLE_CHOICE 타입 답변


}
