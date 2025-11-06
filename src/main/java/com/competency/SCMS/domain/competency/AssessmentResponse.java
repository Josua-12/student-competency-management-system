package com.competency.SCMS.domain.competency;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "assessment_responses")
@SQLDelete(sql = "UPDATE assessment_responses SET deleted_at = CURRENT_TIMESTAMP WHERE resp_id = ?")
public class AssessmentResponse extends CompetencyBaseEntity {

    /**
     * ID (PK) - 진단 답변 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resp_id")
    private Long id;

    /**
     * 1. 진단 결과 (N:1) - 이 답변이 속한 "결과지"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rslt_id", nullable = false)
    private AssessmentResult assessmentResult;

    /**
     * 2. 진단 문항 (N:1) - 이 답변이 응답한 "문항"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qstn_id", nullable = false)
    private AssessmentQuestion question;

    /**
     * 3. 문항 항목 (N:1) - 이 답변이 선택한 "보기"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id") // nullable = true (기본값)
    private AssessmentOption assessmentOption;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssessmentResponse that)) return false;
        if (this.id == null || that.id == null) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : super.hashCode();
    }

}