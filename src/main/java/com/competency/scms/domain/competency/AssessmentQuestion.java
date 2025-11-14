package com.competency.scms.domain.competency;

import com.competency.scms.dto.competency.QuestionFormDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"competency", "options", "responses"})
@Table(name = "assessment_questions")
@SQLDelete(sql = "UPDATE assessment_questions SET deleted_at = CURRENT_TIMESTAMP, " +
        "is_active = false WHERE qstn_id = ?")
public class AssessmentQuestion extends CompetencyBaseEntity {

    /**
     * ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qstn_id")
    private Long id;

    /**
     * 문항 코드
     */
    @NotBlank
    @Column(name = "qstn_code", nullable = false, unique = true, updatable = false, length = 50)
    private String questionCode;

    /**
     * 문항 내용
     */
    @NotBlank
    @Column(name = "qstn_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    /**
     * 문항 유형
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "qstn_type", nullable = false, length = 30)
    private QuestionType questionType;

    /**
     * 정렬 순서
     */
    @NotNull(message = "순서는 필수 항목입니다.")
    @Column(name = "disp_order", nullable = false)
    private int displayOrder;

    /**
     * 활성 여부
     */
    @NotNull
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /**
     * 문항이 속한 역량
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comp_id", nullable = false)
    private Competency competency;

    /**
     * 문항이 가지는 문항 항목 목록
     */
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AssessmentOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "question") // cascade 없음 (문항 삭제되도 답변은 남아야 함)
    @Builder.Default
    private List<AssessmentResponse> responses = new ArrayList<>();

    // ------ 편의 메서드 -------- //

    /**
     * 관계 설정용 (역량 설정)
     */
    protected void setCompetency(Competency competency) {
        this.competency = competency;
    }

    public void addOption(AssessmentOption option) {
        this.options.add(option);
        option.setQuestion(this);
    }

    /**
     * 생성용 편의 메서드
     * @param dto
     * @param competency
     * @return
     */
    public static AssessmentQuestion createQuestion(QuestionFormDto dto, Competency competency) {
        AssessmentQuestion question = AssessmentQuestion.builder()
                .questionCode(dto.getQuestionCode())
                .questionText(dto.getQuestionText())
                .questionType(dto.getQuestionType())
                .displayOrder(dto.getDisplayOrder())
                .isActive(dto.isActive())
                .build();

        competency.addQuestion(question);
        return question;
    }

    public void updateInfo(QuestionFormDto dto) {
        this.questionText = dto.getQuestionText();
        this.displayOrder = dto.getDisplayOrder();
        this.isActive = dto.isActive();
        this.questionType = dto.getQuestionType();
    }

    /**
     * 동일성 비교 (id 기준)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssessmentQuestion that)) return false;
        if (this.id == null || that.id == null) return false;
        return id.equals(that.id);
    }

    /**
     * 해시코드 (id 기준)
     */
    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : super.hashCode();
    }
}
