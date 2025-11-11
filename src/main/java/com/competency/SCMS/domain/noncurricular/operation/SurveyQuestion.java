package com.competency.SCMS.domain.noncurricular.operation;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey_question")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SurveyQuestion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private SatisfactionSurvey survey;

    @Column(name = "order_no")
    private Integer orderNo;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Column(length = 500)
    private String title;

    private boolean required;

    // rating 전용 옵션: scale(1~N)
    private Integer scale;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNo ASC, id ASC")
    @Builder.Default
    private List<SurveyOption> options = new ArrayList<>();
}
