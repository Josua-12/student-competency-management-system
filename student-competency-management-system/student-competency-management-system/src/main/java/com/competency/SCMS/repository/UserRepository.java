package com.competency.SCMS.repository;

import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.domain.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);   // 이메일로 사용자 찾기
    Optional<User> findByStudentNum(Integer studentNum);    // 학번으로 사용자 찾기

    boolean existByEmail(String email); // 해당 이메일 존재 확인
    boolean existByStudentNum(Integer studentNum);  // 해당 학번 존재 확인

    List<User> findByRole(UserRole role);   // 특정 역할을 가진 모든 사용자를 리스트로 반환
    List<User> findByDepartment(String department); //
    List<User> findByGrade(Integer grade);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);
}
