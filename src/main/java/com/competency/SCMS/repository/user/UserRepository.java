package com.competency.SCMS.repository.user;

import com.competency.SCMS.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 학번으로 사용자 조회
    Optional<User> findByStudentNum(Integer studentNum);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 휴대폰 번호로 사용자 조회
    Optional<User> findByPhone(String phone);

    // 비밀번호 재설정 토큰으로 조회
    Optional<User> findByPasswordResetToken(String token);

    // 휴대폰 번호 존재 여부
    boolean existsByPhone(String phone);

    // 이름과 학번으로 사용자 조회 (비밀번호 찾기)
    @Query("SELECT u FROM User u WHERE u.name = :name AND u.studentNum = :studentNum AND u.deletedAt IS NULL")
    Optional<User> findByNameAndStudentNum(
            @Param("name") String name,
            @Param("studentNum") Integer studentNum
    );

    // 이름, 학번, 생년월일로 사용자 조회
    @Query("SELECT u FROM User u WHERE u.name = :name AND u.studentNum = :studentNum " +
            "AND u.birthDate = :birthDate AND u.deletedAt IS NULL")
    Optional<User> findByNameAndStudentNumAndBirthDate(
            @Param("name") String name,
            @Param("studentNum") Integer studentNum,
            @Param("birthDate") LocalDate birthDate
    );

    // 이메일과 비밀번호로 사용자 조회 (로그인 시 사용)
    Optional<User> findByEmailAndPassword(String email, String password);
}
