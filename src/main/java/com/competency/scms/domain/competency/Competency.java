package com.competency.scms.domain.competency;

import com.competency.scms.dto.competency.CompetencyFormDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
//toString 호출 시 무한루프 방지
@ToString(exclude = {"parent", "children", "questions"})
@Table(name = "competencies")
@SQLDelete(sql = "UPDATE competencies SET deleted_at = CURRENT_TIMESTAMP, " +
        "is_active = false WHERE comp_id = ?")
public class Competency extends CompetencyBaseEntity {

    /**
     * ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comp_id")
    private Long id;

    /**
     * 상위 역량(자기참조)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Competency parent;

    /**
     * 하위 역량 목록(자기참조)
     * 부모가 삭제될 때 자식도 함께 삭제되도록 설정
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Competency> children = new ArrayList<>();

    /**
     * 역량명
     */
    @NotBlank(message = "역량명은 필수 항목입니다.")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 문제순서 (표시 순서)
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
     * 약점 조언
     */
    @Column(name = "advice_low", columnDefinition = "TEXT")
    private String adviceLow;

    /**
     * 강점 설명
     */
    @Column(name = "advice_high", columnDefinition = "TEXT")
    private String adviceHigh;

    /**
     * 비교과 프로그램 추천에서 사용할 코드
     */
    @NotBlank
    @Column(name = "comp_code", nullable = false, unique = true,updatable = false, length = 50)
    private String compCode;

    /**
     * 진단 문항 목록
     * 부모가 삭제될 때 자식도 삭제
     */
    @Builder.Default
    @OneToMany(mappedBy = "competency", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentQuestion> questions = new ArrayList<>();

    // ------ 편의 메서드 -------- //

    /**
     * 하위 역량 추가 메서드에서 사용할 메서드
     */
    protected void setParent(Competency parent) {
        this.parent = parent;
    }

    /**
     * 하위 역량 추가
     */
    public void addChildCompetency(Competency child) {
        child.setParentCompetency(this);
    }

    /**
     * 상위 역량 설정
     */
    public void setParentCompetency(Competency parent) {
        // 1. 기존 부모가 있다면, 기존 부모의 자식 목록에서 this 제거
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }

        // 2. 'this'의 부모를 새 부모로 설정
        this.setParent(parent);

        // 3. 새 부모가 null이 아니라면, 새 부모의 자식 목록에 'this'를 추가
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    /**
     * 생성용 편의 메서드
     */
    public static Competency createCompetency(CompetencyFormDto dto, Competency parent) {
        Competency competency = Competency.builder()
                .name(dto.getName())
                .compCode(dto.getCompCode())
                .description(dto.getDescription())
                .displayOrder(dto.getDisplayOrder())
                .isActive(dto.isActive())
                .adviceHigh(dto.getAdviceHigh())
                .adviceLow(dto.getAdviceLow())
                .build();

        // (선택) 부모 설정
        if (parent != null) {
            competency.setParentCompetency(parent);
        }

        return competency;
    }

    /**
     * 역량 정보 수정 메서드
     */
    public void updateInfo(CompetencyFormDto dto) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.displayOrder = dto.getDisplayOrder();
        this.isActive = dto.isActive();
        this.adviceLow = dto.getAdviceLow();
        this.adviceHigh = dto.getAdviceHigh();
    }

    /**
     * 진단 문항 추가 (양방향)
     * @param question 문항 객체
     */
    public void addQuestion(AssessmentQuestion question) {
        this.questions.add(question);
        question.setCompetency(this);
    }


    /**
     * 동일성 비교를 위한 오버라이드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // id가 null이 아니고, 같은 클래스인지 확인
        if (!(o instanceof Competency competency)) return false;

        // id가 아직 없는(transient) 객체는 메모리 주소로 비교,
        // id가 있는 객체는 id로 비교
        if (this.id == null || competency.id == null) {
            return false;
        }
        return id.equals(competency.id);
    }

    @Override
    public int hashCode() {
        // id가 있으면 id를, 없으면 기본 hashCode를 반환
        return (id != null) ? id.hashCode() : super.hashCode();
    }

}
