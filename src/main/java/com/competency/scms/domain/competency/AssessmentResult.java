package com.competency.SCMS.domain.competency;

import com.competency.SCMS.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"assessmentSection", "user", "responses"})
@Table(name = "assessment_results")
@SQLDelete(sql = "UPDATE assessment_results SET deleted_at = CURRENT_TIMESTAMP WHERE rslt_id = ?")
public class AssessmentResult extends CompetencyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rslt_id")
    private Long rsltId;

    /**
     * 이 진단을 수행한 유저 (N:1)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK 컬럼명은 "user_id"
    private User user;

    public Long getId() {
        return rsltId;
    }

    /**
     * 이 진단을 포함하는 진단섹션
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false) // 3. FK 컬럼
    private AssessmentSection assessmentSection;

    /**
     * 제출 여부 상태 필드
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull
    @Builder.Default
    private AssessmentResultStatus status = AssessmentResultStatus.DRAFT;

    /**
     * 최종 제출 일시 (임시저장 상태일 때는 null)
     */
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt; // 5. nullable = true (기본값)

    /**
     * 진단 답변 목록
     */
    @OneToMany(mappedBy = "assessmentResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AssessmentResponse> responses = new ArrayList<>();

    // 편의 메서드 추가

    /**
     * 진단을 '제출 완료' 상태로 변경합니다.
     */
    public void completeSubmission() {
        // 이미 제출 완료된 것은 아닌지 확인
        if (this.status == AssessmentResultStatus.DRAFT) {
            this.status = AssessmentResultStatus.COMPLETED;
            this.submittedAt = LocalDateTime.now(); // 제출 시각 기록
        }
    }

    /**
     * 임시저장 상태인지 확인
     */
    public boolean isDraft() {
        return this.status == AssessmentResultStatus.DRAFT;
    }
}
