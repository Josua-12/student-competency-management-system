package com.competency.SCMS.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "student_num", nullable = false)
    private Integer studentNum;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "phone", length = 100)
    private String phone;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "grade")
    private Integer grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    @Builder.Default
    private UserRole role = UserRole.STUDENT;

    @Column(name = "locked", nullable = false)
    @Builder.Default
    private Boolean locked = false;

    @Column(name = "fail_cnt", nullable = false)
    @Builder.Default
    private Integer failCnt = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ========== 비밀번호 재설정 관련 필드 ==========
    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;

    @Column(name = "password_reset_token_expired_at")
    private LocalDateTime passwordResetTokenExpiredAt;

    @Column(name = "last_verification_phone_number", length = 20)
    private String lastVerificationPhoneNumber;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ========== 계정 잠금 관련 메서드 ==========
    public void addFailAttempt() {
        this.failCnt++;
        if (this.failCnt >= 5) {
            this.locked = true;
            this.lockedUntil = LocalDateTime.now().plusMinutes(30);
        }
    }

    public Long getId() {
        return userId;
    }

    public boolean isAccountLocked() {
        if (!this.locked) {
            return false;
        }

        // 잠금 시간이 지났으면 자동 해제
        if (this.lockedUntil != null && this.lockedUntil.isBefore(LocalDateTime.now())) {
            this.locked = false;
            this.failCnt = 0;
            this.lockedUntil = null;
            return false;
        }

        return true;
    }


    public void resetFailAttempt() {
        this.failCnt = 0;
        this.locked = false;
        this.lockedUntil = null;
    }

    public void unlock() {
        this.locked = false;
        this.failCnt = 0;
        this.lockedUntil = null;
    }

    // ========== 비밀번호 관련 메서드 ==========
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void setPasswordResetToken(String token, int expiryMinutes) {
        this.passwordResetToken = token;
        this.passwordResetTokenExpiredAt = LocalDateTime.now().plusMinutes(expiryMinutes);
    }

    public boolean isPasswordResetTokenExpired() {
        return this.passwordResetTokenExpiredAt != null
                && LocalDateTime.now().isAfter(this.passwordResetTokenExpiredAt);
    }

    public void clearPasswordResetToken() {
        this.passwordResetToken = null;
        this.passwordResetTokenExpiredAt = null;
    }

    // ========== 프로필 관련 메서드 ==========
    public void updateProfile(String phone, String department, Integer grade) {
        this.phone = phone;
        this.department = department;
        this.grade = grade;
    }
}
