package com.competency.SCMS.domain.competency;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "assessment_options")
@SQLDelete(sql = "UPDATE assessment_question_items SET deleted_at = CURRENT_TIMESTAMP " +
        "WHERE item_id = ?")
public class AssessmentOption extends CompetencyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qstn_id", nullable = false)
    private AssessmentQuestion question;

    @NotBlank
    @Column(name = "option_text", nullable = false, length = 200)
    private String optionText;

    @NotNull
    @Column(name = "score", nullable = false)
    private Integer score;

    @NotNull
    @Column(name = "disp_order", nullable = false)
    private int displayOrder;

    // ------ 편의 메서드 -------- //

    protected void setQuestion(AssessmentQuestion question) {
        this.question = question;
    }

    public void updateInfo(String optionText, Integer score, int displayOrder) {
        this.optionText = optionText;
        this.score = score;
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssessmentOption that)) return false;
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
