package com.competency.SCMS.repository;

import com.competency.SCMS.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /** 부서 코드로 조회 */
    Optional<Department> findByCode(String code);

    /** 부서명으로 조회 */
    Optional<Department> findByName(String name);
}
