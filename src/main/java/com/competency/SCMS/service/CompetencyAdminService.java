package com.competency.SCMS.service;

import com.competency.SCMS.domain.competency.Competency;
import com.competency.SCMS.dto.competency.CompetencyFormDto;
import com.competency.SCMS.dto.competency.CompetencyTreeDto;
import com.competency.SCMS.repository.competency.CompetencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetencyAdminService {

    private final CompetencyRepository competencyRepository;

    /**
     * 1. 조회 (R)
     * 역량 계층 구조 (TUI-Tree) 조회
     */
    @Transactional(readOnly = true)
    public List<CompetencyTreeDto> getCompetencyTree() {
        // 부모가 null값인 역량 찾기 (핵심역량)
        List<Competency> rootCompetencies = competencyRepository.findByParentIsNull();
        return rootCompetencies.stream()
                .map(this::convertToTreeDto)
                .collect(Collectors.toList());
    }

    /**
     * 엔티티 -> DTO 변환
     */
    private CompetencyTreeDto convertToTreeDto(Competency competency) {
        List<CompetencyTreeDto> childDtos = competency.getChildren().stream()
                .map(this::convertToTreeDto)
                .collect(Collectors.toList());

        return CompetencyTreeDto.builder()
                .id(competency.getId())
                .text(competency.getName() + " (" + competency.getCompCode() + ")")
                .children(childDtos)
                .opened(true)   // 기본으로 열린 상태 -TUI-Tree
                .build();
    }

    /**
     * 2. 생성 & 수정
     * CompetencyFormDto를 받아서 저장 또는 수정
     */
    @Transactional
    public Long saveOrUpdateCompetency(CompetencyFormDto dto) {

        // 1. 부모 엔티티 찾기 (하위 역량 추가 시)
        Competency parent = null;
        if (dto.getParentId() != null) {
            parent = competencyRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상위 역량입니다."));
        }

        // 2. 생성, 수정
        Competency competency;
        if (dto.getId() == null) {
            // 신규 생성
            competency = Competency.createCompetency(dto, parent);
        } else {
            competency = competencyRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역량 ID 입니다."));

            // 수정
            competency.updateInfo(dto);

            // 수정 시 부모 관계도 변경되었는지 확인하고 설정
            if (competency.getParent() != parent) {
                competency.setParentCompetency(parent);
            }
        }

        // 3. 저장
        Competency savedCompetency = competencyRepository.save(competency);
        return savedCompetency.getId();
    }

    /**
     * 3. 삭제
     * 소프트 삭제
     * 자식들도 함께 처리
     */
    @Transactional
    public void deleteCompetency(Long competencyId) {
        // 해당 역량ID가 있는지 확인
        if (!competencyRepository.existsById(competencyId)) {
            throw new IllegalArgumentException("존재하지 않는 역량입니다. ID: " + competencyId);
        }

        // 실제로는 @SQLDelete에 정의된 업데이트 쿼리 실행
        competencyRepository.deleteById(competencyId);

    }
}
