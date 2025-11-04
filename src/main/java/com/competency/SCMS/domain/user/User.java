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
    private Integer userId;

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
            this.locked =true;
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

}
