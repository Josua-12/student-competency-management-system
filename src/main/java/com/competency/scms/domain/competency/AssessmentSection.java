package com.competency.scms.domain.competency;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"results"})
@Table(name = "assessment_sections")
@SQLDelete(sql = "UPDATE assessment_sections SET " +
        "is_active = false WHERE section_id = ?")
public class AssessmentSection {

    /**
     * ID (PK) - 진단 섹션 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long id;

    /**
     * 타이틀 (예: "2025년 1학기 정기 진단")
     */
    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 진단 시작일
     */
    @NotNull
    @Column(nullable = false)
    private LocalDateTime startDate;

    /**
     * 진단 종료일
     */
    @NotNull
    @Column(nullable = false)
    private LocalDateTime endDate;

    /**
     * 활성 여부
     */
    @NotNull
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;


    @OneToMany(mappedBy = "assessmentSection", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AssessmentResult> results = new ArrayList<>();


    // ------ 편의 메서드 -------- //

    /**
     * 정보 수정 (Setter 대용)
     */
    public void updateInfo(String title, String description, LocalDateTime startDate,
                           LocalDateTime endDate, boolean isActive) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssessmentSection that)) return false;
        if (this.id == null || that.id == null) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : super.hashCode();
    }
}
