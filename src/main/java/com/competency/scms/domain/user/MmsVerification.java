package com.competency.scms.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mms_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MmsVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long id;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "verification_code", nullable = false, length = 10)
    private String verificationCode;

    @Column(name = "receiver_email", length = 100)
    private String receiverEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private VerificationStatus status = VerificationStatus.PENDING;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "failure_count")
    @Builder.Default
    private Integer failureCount = 0;

    @Column(name = "purpose", length = 50)
    private String purpose;

    @Column(name = "sent_message", columnDefinition = "TEXT")
    private String sentMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 인증 상태 Enum (내부 클래스)
     */
    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        EXPIRED,
        BLOCKED
    }

    /**
     * 만료 여부 확인
     */
    public boolean isExpired() {
        return this.expiredAt != null && LocalDateTime.now().isAfter(this.expiredAt);
    }

    /**
     * 차단 여부 확인
     */
    public boolean isBlocked() {
        return this.status == VerificationStatus.BLOCKED;
    }

    /**
     * 인증 완료 처리
     */
    public void markAsVerified() {
        this.status = VerificationStatus.VERIFIED;
        this.verifiedAt = LocalDateTime.now();
    }

    /**
     * 만료 처리
     */
    public void markAsExpired() {
        this.status = VerificationStatus.EXPIRED;
    }

    /**
     * 차단 처리
     */
    public void block() {
        this.status = VerificationStatus.BLOCKED;
    }

    /**
     * 실패 횟수 증가
     */
    public void incrementFailureCount() {
        this.failureCount++;
        if (this.failureCount >= 5) {
            block();
        }
    }

    /**
     * 수신 메시지 설정
     */
    public void setSentMessage(String message) {
        this.sentMessage = message;
    }
}
