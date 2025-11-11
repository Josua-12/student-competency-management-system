package com.competency.SCMS.repository.verification;

import com.competency.SCMS.domain.user.MmsVerification;
import com.competency.SCMS.domain.user.MmsVerification.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MmsVerificationRepository extends JpaRepository<MmsVerification, Long> {

    Optional<MmsVerification> findByPhoneAndStatus(String phone, VerificationStatus status);

    Optional<MmsVerification> findByPhoneAndVerificationCodeAndStatus(
            String phone,
            String verificationCode,
            VerificationStatus status
    );

    Optional<MmsVerification> findTopByPhoneOrderByCreatedAtDesc(String phone);
}
