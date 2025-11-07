package com.competency.SCMS.repository.verification;

import com.competency.SCMS.domain.user.PhoneVerification;
import com.competency.SCMS.domain.user.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneVerificationRepo extends JpaRepository<PhoneVerification, Long> {

    // 휴대폰 번호와 상태로 조회
    Optional<PhoneVerification> findByPhoneAndStatus(String phone, VerificationStatus status);

    // 휴대폰 번호, 인증코드, 상태로 조회
    Optional<PhoneVerification> findByPhoneAndVerificationCodeAndStatus(
            String phone,
            String verificationCode,
            VerificationStatus status
    );

    // 최근 인증 요청 조회
    Optional<PhoneVerification> findTopByPhoneOrderByCreatedAtDesc(String phone);

    // 만료된 인증 조회
    List<PhoneVerification> findByStatusAndExpiredAtBefore(
            VerificationStatus status,
            LocalDateTime expiresAt
    );

    // 만료된 데이터 삭제
    void deleteByExpiredAtBefore(LocalDateTime expiresAt);

    // 사용자 ID로 최근 미인증 요청 조회
    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.user.id = :userId AND pv.isVerified = false " +
            "AND pv.expiredAt > :now " +
            "ORDER BY pv.createdAt DESC LIMIT 1")
    Optional<PhoneVerification> findLatestUnverifiedByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );

    // 휴대폰 번호와 인증코드로 미인증 요청 조회 (만료되지 않은 것만)
    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.phone = :phone AND pv.verificationCode = :code " +
            "AND pv.isVerified = false AND pv.expiredAt > :now")
    Optional<PhoneVerification> findByPhoneAndCodeAndNotExpired(
            @Param("phone") String phone,
            @Param("code") String code,
            @Param("now") LocalDateTime now
    );

    // 메일 수신자로 검색 (메일 감지할 때 사용)
    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.receiverEmail = :email AND pv.isVerified = false " +
            "AND pv.expiredAt > :now " +
            "ORDER BY pv.createdAt DESC LIMIT 1")
    Optional<PhoneVerification> findLatestUnverifiedByEmailAndNotExpired(
            @Param("email") String email,
            @Param("now") LocalDateTime now
    );
}
