package com.competency.scms.domain.competency;

import com.competency.scms.dto.competency.OptionFormDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "assessment_options")
@SQLDelete(sql = "UPDATE assessment_options SET deleted_at = CURRENT_TIMESTAMP " +
        "WHERE option_id = ?")
@SQLRestriction("deleted_at is NULL")
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

    /**
     * 생성 용 편의 메서드
     * @param dto
     * @return
     */
    public static AssessmentOption createOption(OptionFormDto dto) {
        return AssessmentOption.builder()
                .optionText(dto.getOptionText())
                .score(dto.getScore())
                .displayOrder(dto.getDisplayOrder())
                .build();
    }

    public void updateInfo(OptionFormDto dto) {
        this.optionText = dto.getOptionText();
        this.score = dto.getScore();
        this.displayOrder = dto.getDisplayOrder();
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
