package com.competency.SCMS.repository;

import com.competency.SCMS.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // 이름 또는 학번 검색
    Page<User> findByNameContainsOrStudentNumContains(String name, String studentNum, Pageable pageable);

    // 활성/비활성 계정 조회
    Page<User> findByLocked(boolean locked, Pageable pageable);

    // 학생 목록 전체 조회
    Page<User> findAll(Pageable pageable);

    Page<User> findByNameContainsOrStudentNumContainsAndLocked(String search, String search1, Boolean locked, Pageable pageable);
}
