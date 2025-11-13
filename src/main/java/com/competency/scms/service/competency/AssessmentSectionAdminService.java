package com.competency.scms.service.competency;

import com.competency.scms.domain.competency.AssessmentSection;
import com.competency.scms.dto.competency.AssessmentSectionFormDto;
import com.competency.scms.repository.competency.AssessmentSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssessmentSectionAdminService {

    private final AssessmentSectionRepository sectionRepository;

    // 1. 목록 조회 (관리자)
    public List<AssessmentSectionFormDto> getAllSections() {
        return sectionRepository.findAll().stream()
                .map(this::entityToDto)
                .toList();
    }

    // 2. 단건 조회 (수정 폼 채우기)
    public AssessmentSectionFormDto getSectionDetails(Long id) {
        AssessmentSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 섹션입니다."));
        return entityToDto(section);
    }

    // 3. 생성 및 수정
    public Long saveOrUpdateSection(AssessmentSectionFormDto dto) {
        AssessmentSection section;
        if (dto.getId() == null) {
            // 생성
            section = AssessmentSection.builder()
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .isActive(dto.isActive())
                    .build();
        } else {
            // 수정
            section = sectionRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 섹션입니다."));
            // 엔티티의 updateInfo 메서드 사용
            section.updateInfo(
                    dto.getTitle(),
                    dto.getDescription(),
                    dto.getStartDate(),
                    dto.getEndDate(),
                    dto.isActive()
            );
        }
        AssessmentSection savedSection = sectionRepository.save(section);
        return savedSection.getId();
    }

    // 4. 삭제
    public void deleteSection(Long id) {
        if (!sectionRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 섹션입니다.");
        }
        sectionRepository.deleteById(id);
    }

    // -- DTO 변환 헬퍼 --
    private AssessmentSectionFormDto entityToDto(AssessmentSection section) {
        AssessmentSectionFormDto dto = new AssessmentSectionFormDto();
        dto.setId(section.getId());
        dto.setTitle(section.getTitle());
        dto.setDescription(section.getDescription());
        dto.setStartDate(section.getStartDate());
        dto.setEndDate(section.getEndDate());
        dto.setActive(section.isActive());
        return dto;
    }
}
