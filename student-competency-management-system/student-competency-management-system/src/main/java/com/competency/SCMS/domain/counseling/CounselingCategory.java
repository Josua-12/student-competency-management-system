package com.competency.SCMS.domain.counseling;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "counseling_categories")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CounselingCategory { // 관리자가 하위 상담 분야 관리
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "상담 분야는 필수입니다")
    private CounselingField counselingField;    // 상위 상담 분야
    
    @Column(nullable = false)
    @NotBlank(message = "카테고리명은 필수입니다")
    @Size(max = 100, message = "카테고리명은 100자를 초과할 수 없습니다")
    private String categoryName;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String description;
    
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 필수 필드 생성자
    public CounselingCategory(CounselingField counselingField, String categoryName) {
        this.counselingField = counselingField;
        this.categoryName = categoryName;
    }
}