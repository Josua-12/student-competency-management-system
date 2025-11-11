package com.competency.SCMS.domain.counseling;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_options")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE question_options SET deleted_at = NOW(), is_active = false WHERE id = ?")
public class QuestionOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @NotNull(message = "질문 정보는 필수입니다")
    private SatisfactionQuestion question;
    
    @Column(nullable = false)
    @NotBlank(message = "옵션 텍스트는 필수입니다")
    @Size(max = 200, message = "옵션 텍스트는 200자를 초과할 수 없습니다")
    private String optionText;
    
    @Column(nullable = false)
    @NotNull(message = "옵션 값은 필수입니다")
    private Integer optionValue;
    
    @Column(nullable = false)
    @NotNull(message = "표시 순서는 필수입니다")
    @Min(value = 1, message = "표시 순서는 1 이상이어야 합니다")
    private Integer displayOrder;
    
    @Column(nullable = false)
    private Boolean isActive = true;

    public boolean isDeleted(){
        return deletedAt != null;       //soft 삭제 관리
    }
    
    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

}