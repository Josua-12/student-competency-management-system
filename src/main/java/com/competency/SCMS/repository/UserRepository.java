package com.competency.SCMS.repository;

import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.domain.user.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
//public interface UserRepository extends JpaRepository<User, Integer> {
//
//    // 이름, 학번
//    Page<User> findByNameContainsOrStudentNumContains(String name, String studentNum, Pageable pageable);
//    // 활성화 여부
//    Page<User> findByLocked(boolean locked, Pageable pageable);
//    // 전체 조회
//    Page<User> findAll(Pageable pageable);
//    // 이름이나 학번, 활성화 조회
//    Page<User> findByNameContainsOrStudentNumContainsAndLocked(String search, String search1, Boolean locked, Pageable pageable);
//
//    // 이메일 조회
//    Optional<User> findByEmail(String email);
//    // 학번 조회
//    Optional<User> findByStudentNum(Integer studentNum);
//    // 이메일 존재 확인
//    boolean existByEmail(String email);
//    // 학번 존재 확인
//    boolean existByStudentNum(Integer studentNum);
//    // 역할 조회
//    List<User> findByRole(UserRole role);
//    // 학과 조회
//    List<User> findByDepartment(String department);
//    // 학년 조회
//    List<User> findByGrade(Integer grade);
//    // 존재하는 이메일 조회
//    Optional<User> findByEmailAndDeletedAtIsNull(String email);
//}
