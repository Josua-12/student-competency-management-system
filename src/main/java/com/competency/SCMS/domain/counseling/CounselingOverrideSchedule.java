package com.competency.SCMS.domain.counseling;

import com.competency.SCMS.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "counseling_override_schedules",
        indexes = {
                @Index(name = "idx_counselor_dates", columnList = "counselor_id,start_date,end_date"),
                @Index(name = "idx_approval_status", columnList = "approval_status")
        })
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CounselingOverrideSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id", nullable = false)
    private User counselor;

    @Column(nullable = false)
    private LocalDate startDate;   // 휴가 시작일 (예외 적용일)  하루짜리 휴가는 starDate ==endDate
    @Column(nullable = false)
    private LocalDate endDate;     // 휴가 종료일 (예외 종료일)

    // 시간대별 OFF 설정 (null = 기본스케줄 유지, false=근무, true=OFF)
    private Boolean slot0910 = null;
    private Boolean slot1011 = null;
    private Boolean slot1112 = null;
    private Boolean slot1213 = null;
    private Boolean slot1314 = null;
    private Boolean slot1415 = null;
    private Boolean slot1516 = null;
    private Boolean slot1617 = null;
    private Boolean slot1718 = null;

    @Enumerated(EnumType.STRING)
    private CounselingOffReason reason; // 병가, 반차, 출산휴가 등

    @Column(columnDefinition = "TEXT")
    private String detailReason;  // 기타 사유인 경우, 상세 내용

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    // 승인 관련 필드
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    @Column(columnDefinition = "TEXT")
    private String approvalComment;  // 승인/반려 사유

    private LocalDateTime approvedAt;

}