package com.competency.scms.domain.counseling;

import com.competency.scms.domain.user.User;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@SQLDelete(sql = "UPDATE counseling_records SET deleted_at = NOW() WHERE id = ?") //soft delete
@Where(clause = "deleted_at IS NULL") // 조회 시 자동으로 제외
@Entity
@Table(name = "counseling_records")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CounselingRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일지 아이디
    
    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private CounselingReservation reservation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_user_id", nullable = false)
    private User counselor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_user_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CounselingSubField subfield;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String recordContent; // 상담내용
    
    @Column(columnDefinition = "TEXT")
    private String counselorMemo; //메모

    @Column(nullable = false)
    @Builder.Default
    private boolean isPublic = false;   // 기본 설정 : 비공개

    public boolean isDeleted(){
        return deletedAt != null;       //soft 삭제 관리
    }
    
    @Column(nullable = false)
    private LocalDateTime counselingDate;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

}