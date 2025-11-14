package com.competency.scms.domain.counseling;

import com.competency.scms.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import org.hibernate.annotations.SQLDelete;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "counselors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE counselors SET deleted_at = NOW(), is_active = false WHERE user_id = ?")
public class Counselor {
    
    @Id
    @Column(name = "user_id")
    private Long counselorId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CounselingField counselingField;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "counselor_subfields",
        joinColumns = @JoinColumn(name = "counselor_id"),
        inverseJoinColumns = @JoinColumn(name = "subfield_id")
    )
    private List<CounselingSubField> specializations = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String specialization;  // 기존 호환성 유지

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;    // 상담 예약 가능 여부 제어

    public boolean isDeleted(){
        return deletedAt != null;       //상담원 soft 삭제 관리
    }

    
    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

}