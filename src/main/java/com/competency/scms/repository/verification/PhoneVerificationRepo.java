package com.competency.scms.repository.verification;

import com.competency.scms.domain.user.PhoneVerification;
import com.competency.scms.domain.user.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneVerificationRepo extends JpaRepository<PhoneVerification, Long> {

    Optional<PhoneVerification> findByPhoneAndStatus(String phone, VerificationStatus status);

    Optional<PhoneVerification> findByPhoneAndVerificationCodeAndStatus(
            String phone,
            String verificationCode,
            VerificationStatus status
    );

    Optional<PhoneVerification> findTopByPhoneOrderByCreatedAtDesc(String phone);

    List<PhoneVerification> findByStatusAndExpiredAtBefore(
            VerificationStatus status,
            LocalDateTime expiresAt
    );

    void deleteByExpiredAtBefore(LocalDateTime expiresAt);

    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.user.id = :userId AND pv.isVerified = false " +
            "AND pv.expiredAt > :now " +
            "ORDER BY pv.createdAt DESC LIMIT 1")
    Optional<PhoneVerification> findLatestUnverifiedByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.phone = :phone AND pv.verificationCode = :code " +
            "AND pv.isVerified = false AND pv.expiredAt > :now")
    Optional<PhoneVerification> findByPhoneAndCodeAndNotExpired(
            @Param("phone") String phone,
            @Param("code") String code,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.receiverEmail = :email AND pv.isVerified = false " +
            "AND pv.expiredAt > :now " +
            "ORDER BY pv.createdAt DESC LIMIT 1")
    Optional<PhoneVerification> findLatestUnverifiedByEmailAndNotExpired(
            @Param("email") String email,
            @Param("now") LocalDateTime now
    );
}
