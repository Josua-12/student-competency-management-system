package com.competency.SCMS.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "phone_verification")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long verificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "verification_code", nullable = false, length = 10)
    private String verificationCode;

    @Column(name = "receiver_email", nullable = false, length = 255)
    private String receiverEmail;

    @Column(name = "sent_message", columnDefinition = "TEXT")
    private String sentMessage;

    @Column(name = "message_received_at")
    private LocalDateTime messageReceivedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private VerificationStatus status = VerificationStatus.PENDING;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "failure_count", nullable = false)
    @Builder.Default
    private Integer failureCount = 0;

    @Column(name = "purpose", length = 50)
    private String purpose;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiredAt);
    }

    public boolean isValid() {
        return !this.isExpired() && !this.isVerified;
    }

    public void incrementFailureCount() {
        this.failureCount++;
        if (this.failureCount >= 5) {
            this.status = VerificationStatus.BLOCKED;
        }
    }

    public boolean isBlocked() {
        return this.status == VerificationStatus.BLOCKED;
    }

    public void markAsVerified() {
        this.status = VerificationStatus.VERIFIED;
        this.isVerified = true;
        this.verifiedAt = LocalDateTime.now();
        this.messageReceivedAt = LocalDateTime.now();
    }

    public void markAsExpired() {
        this.status = VerificationStatus.EXPIRED;
    }
}
