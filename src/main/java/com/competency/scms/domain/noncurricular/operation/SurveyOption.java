package com.competency.scms.domain.noncurricular.operation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "survey_option")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SurveyOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private SurveyQuestion question;

    @Column(name = "order_no")
    private Integer orderNo;

    @Column(length = 300)
    private String text;
}
