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
    private Long id;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ========== 비밀번호 재설정 관련 필드 추가 ==========
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

    public void increaseFailCount() {
        this.failCnt++;
        if (this.failCnt >= 5) {
            this.locked = true;
        }
    }

    public void resetFailCount() {
        this.failCnt = 0;
    }

    public void unlock() {
        this.locked = false;
        this.failCnt = 0;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateProfile(String phone, String department, Integer grade) {
        this.phone = phone;
        this.department = department;
        this.grade = grade;
    }

    public String getUsername() {
        return String.valueOf(this.studentNum);
    }

    // ========== 비밀번호 재설정 관련 메서드 ==========
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
}
