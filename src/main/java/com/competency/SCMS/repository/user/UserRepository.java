package com.competency.SCMS.repository.user;

import com.competency.SCMS.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserNum(Integer userNum);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByPasswordResetToken(String token);

    boolean existsByPhone(String phone);

    // 서비스에서 사용하는 파생 메서드(학번 + 이름)
    Optional<User> findByUserNumAndName(Integer userNum, String name);

    // 기존 @Query 버전 유지 (필요시)
    @Query("SELECT u FROM User u WHERE u.name = :name AND u.userNum = :userNum AND u.deletedAt IS NULL")
    Optional<User> findByNameAndUserNum(@Param("name") String name, @Param("userNum") Integer userNum);

    @Query("SELECT u FROM User u WHERE u.name = :name AND u.userNum = :userNum " +
            "AND u.birthDate = :birthDate AND u.deletedAt IS NULL")
    Optional<User> findByNameAndUserNumAndBirthDate(
            @Param("name") String name,
            @Param("userNum") Integer userNum,
            @Param("birthDate") LocalDate birthDate
    );

    Optional<User> findByEmailAndPassword(String email, String password);

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByDeletedAtIsNull();

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.deletedAt IS NULL")
    long countNewUsersInPeriod(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);
}
