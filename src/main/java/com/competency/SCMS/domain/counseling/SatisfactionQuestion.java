package com.competency.SCMS.domain.counseling;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "satisfaction_questions")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE satisfaction_questions SET deleted_at = NOW(), is_active = false WHERE id = ?")
public class SatisfactionQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;
    
    @Column(nullable = false)
    private Boolean isSystemDefault = false; // 시스템 기본 질문 (관리자가 실수로 핵심 질문을 삭제하는 것을 방지)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "counseling_field")
    private CounselingField counselingField;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CounselingSubField category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;
    
    @Column(nullable = false)
    private Integer displayOrder;
    
    @Column(nullable = false)
    private Boolean isRequired = true;
    
    @Column(nullable = false)
    private Boolean isActive = true; // 삭제 대신 비활성화 (기본:활성화)

    public boolean isDeleted(){
        return deletedAt != null;       //soft 삭제 관리
    }

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionOption> options = new ArrayList<>();

    // 필수 필드 생성자
    public SatisfactionQuestion(String questionText, QuestionType questionType, Integer displayOrder) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.displayOrder = displayOrder;
    }

    public enum QuestionType {
        RATING, TEXT, MULTIPLE_CHOICE //평점형, 텍스트형, 객관식
    }
}