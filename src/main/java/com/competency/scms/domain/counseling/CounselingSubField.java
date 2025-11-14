package com.competency.scms.domain.counseling;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "counseling_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CounselingSubField { // 관리자가 하위 상담 분야 관리
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CounselingField counselingField;    // 상위 상담 분야
    
    @Column(nullable = false)
    private String subfieldName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ConsultingType consultingType = ConsultingType.INTERVIEW;    // 면접상담 or 서면첨삭
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    public enum ConsultingType {
        INTERVIEW("면접상담"),
        WRITTEN_EDITING("서면첨삭");
        
        private final String displayName;
        
        ConsultingType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}