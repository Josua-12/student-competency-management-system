package com.competency.SCMS.repository.competency;

import com.competency.SCMS.domain.competency.Competency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetencyRepository extends JpaRepository<Competency, Long> {

    // 1. 관리자 페이지 TUI-Tree의 최상위 노드를 찾기 위해 필요
    //(parent 필드가 null인 역량들을 찾음 - 최상위 역량 찾기)
    List<Competency> findByParentIsNull();

    // 2. 특정 부모 ID 하위의 역량들 찾기
    List<Competency> findByParentId(Long parentId);
}
