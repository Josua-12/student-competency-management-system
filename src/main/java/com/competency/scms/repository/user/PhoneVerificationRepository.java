package com.competency.scms.repository.user;

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
public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {

    // 기본 조회 (phone 기준)
    Optional<PhoneVerification> findByPhoneAndStatus(String phone, VerificationStatus status);

    List<PhoneVerification> findByPhoneAndStatusNot(String phone, VerificationStatus status);

    Optional<PhoneVerification> findByPhoneAndVerificationCode(String phone, String verificationCode);

    Optional<PhoneVerification> findByPhoneAndVerificationCodeAndStatus(
            String phone, String verificationCode, VerificationStatus status
    );

    Optional<PhoneVerification> findTopByPhoneOrderByCreatedAtDesc(String phone);

    // 최신 1건(권장: createdAt 기준)
    Optional<PhoneVerification> findTopByPhoneAndVerificationCodeAndStatusOrderByCreatedAtDesc(
            String phone, String verificationCode, VerificationStatus status
    );

    Optional<PhoneVerification> findTopByPhoneAndStatusOrderByCreatedAtDesc(
            String phone, VerificationStatus status
    );

    // 만료 관련
    List<PhoneVerification> findByStatusAndExpiredAtBefore(VerificationStatus status, LocalDateTime expiresAt);

    void deleteByExpiredAtBefore(LocalDateTime expiresAt);

    // JPQL 보조 쿼리
    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.user.id = :userId AND pv.isVerified = false AND pv.expiredAt > :now " +
            "ORDER BY pv.createdAt DESC")
    Optional<PhoneVerification> findLatestUnverifiedByUserId(
            @Param("userId") Long userId, @Param("now") LocalDateTime now
    );

    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.phone = :phone AND pv.verificationCode = :code AND pv.expiredAt > :now")
    Optional<PhoneVerification> findByPhoneAndCodeAndNotExpired(
            @Param("phone") String phone, @Param("code") String code, @Param("now") LocalDateTime now
    );

    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.receiverEmail = :email AND pv.isVerified = false " +
            "AND pv.expiredAt > :now ORDER BY pv.createdAt DESC")
    Optional<PhoneVerification> findLatestUnverifiedByEmailAndNotExpired(
            @Param("email") String email, @Param("now") LocalDateTime now
    );

    // 서비스가 '...OrderByIdDesc'로 호출 중이면 아래 별칭 메서드 2개로 호환 가능(정렬은 createdAt로 고정)
    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.phone = :phone AND pv.verificationCode = :code AND pv.status = :status " +
            "ORDER BY pv.createdAt DESC")
    Optional<PhoneVerification> findTopByPhoneAndVerificationCodeAndStatusOrderByIdDesc(
            @Param("phone") String phone, @Param("code") String code, @Param("status") VerificationStatus status
    );

    @Query("SELECT pv FROM PhoneVerification pv " +
            "WHERE pv.phone = :phone AND pv.status = :status " +
            "ORDER BY pv.createdAt DESC")
    Optional<PhoneVerification> findTopByPhoneAndStatusOrderByIdDesc(
            @Param("phone") String phone, @Param("status") VerificationStatus status
    );
}
