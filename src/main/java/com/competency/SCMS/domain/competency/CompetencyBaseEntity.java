package com.competency.SCMS.domain.competency;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at is NULL")
public abstract class CompetencyBaseEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    /**
     * 삭제 메서드 (SQLDelete를 사용하기 때문에 없어도 되지만 편의상 남겨둠)
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 삭제 복구 메서드
     */
    public void restore() {
        this.deletedAt = null;
    }

    /**
     * 삭제 여부 확인 메서드
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
