package com.competency.scms.repository.competency;

import com.competency.scms.domain.competency.Competency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetencyRepository extends JpaRepository<Competency, Long> {

    // 1. 관리자 페이지 TUI-Tree의 최상위 노드를 찾기 위해 필요
    //(parent 필드가 null인 역량들을 찾음 - 최상위 역량 찾기)
    List<Competency> findByParentIsNull();

    // 2. 특정 부모 ID 하위의 역량들 찾기
    List<Competency> findByParentId(Long parentId);

    /**
     * 3. '진단 페이지' 로드를 위한 N+1 해결 쿼리
     *  1) 모든 활성화된 '핵심역량'(parent=null) 조회
     *  2) 그 '자식'들도 Fetch Join (1차)
     *  3) 하위역량의 '문항'들도 Fetch Join (2차)
     *  4) 문항의 '보기'들도 Fetch Join (3차)
     */
//    @Query("SELECT DISTINCT c FROM Competency c " +
//            "LEFT JOIN FETCH c.children sc " +  // sc: sub-competency (하위역량)
//            "LEFT JOIN FETCH sc.questions sq " +    // sq: sub-question (하위역량의 문항)
//            "LEFT JOIN FETCH sq.options so " +  // so: sub-option (하위역량 문항의 보기)
//            "WHERE c.parent IS NULL AND c.isActive = true " +
//            "AND (sc IS NULL OR sc.isActive = true) " +
//            "AND (sq IS NULL OR sq.isActive = true) " +
//            "ORDER BY c.displayOrder, sc.displayOrder, sq.displayOrder, so.displayOrder")
//    List<Competency> findActiveRootCompetenciesForAssessment();

    // "부모가 없고(Root) + 활성화된(Active) 역량을 순서대로 찾아줘"
    List<Competency> findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * 4. 역량 코드로 존재 여부 확인
     */
    boolean existsByCompCode(String compCode);
  
    // 담당자 조회 메서드
    List<Competency> findAllByAdminId(Long adminId); // CompetencyRepository

}
