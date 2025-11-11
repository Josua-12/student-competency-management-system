package com.competency.scms.service.competency;

import com.competency.scms.domain.competency.AssessmentOption;
import com.competency.scms.domain.competency.AssessmentQuestion;
import com.competency.scms.domain.competency.Competency;
import com.competency.scms.dto.competency.*;
import com.competency.scms.repository.competency.AssessmentOptionRepository;
import com.competency.scms.repository.competency.AssessmentQuestionRepository;
import com.competency.scms.repository.competency.CompetencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetencyAdminService {

    private final CompetencyRepository competencyRepository;
    private final AssessmentQuestionRepository questionRepository;
    private final AssessmentOptionRepository optionRepository;

    /**
     * 1. 역량 조회 (R)
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
     * 2. 역량 생성 & 수정
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
     * 3. 역량 삭제
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

    /**
     * 4. 역량 상세 조회
     * 역량 1개의 상세 정보를 폼 형태로 조회
     */
    @Transactional(readOnly = true)
    public CompetencyFormDto getCompetencyDetails(Long competencyId) {
        Competency competency = competencyRepository.findById(competencyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역량입니다."));

        // Entity -> DTO 변환
        CompetencyFormDto dto = new CompetencyFormDto();
        dto.setId(competency.getId());

        // 부모 ID 설정
        if (competency.getParent() != null) {
            dto.setParentId(competency.getParent().getId());
        }

        // 나머지 필드 매핑
        dto.setName(competency.getName());
        dto.setCompCode(competency.getCompCode());
        dto.setDescription(competency.getDescription());
        dto.setDisplayOrder(competency.getDisplayOrder());
        dto.setActive(competency.isActive());
        dto.setAdviceHigh(competency.getAdviceHigh());
        dto.setAdviceLow(competency.getAdviceLow());

        return dto;
    }

    /**
     * 5. 문항 & 항목 저장/수정 (C/U)
     * QuestionFormDto를 받아서 문항과 항목(Options)을 한꺼번에 처리
     */
    @Transactional
    public Long saveOrUpdateQuestion(QuestionFormDto dto) {

        // 1. 부모 엔티티 조회
        Competency competency = competencyRepository.findById(dto.getCompetencyId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역량입니다."));

        AssessmentQuestion question;
        if (dto.getId() == null) {
            // 신규 문항 수정
            question = AssessmentQuestion.createQuestion(dto, competency);
        } else {
            // 기존 문항 수정
            question = questionRepository.findById(dto.getCompetencyId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문항입니다."));
            question.updateInfo(dto);
        }

        // 2. 문항 항목 처리
        question.getOptions().clear();

        if (dto.getOptions() != null) {
            for (OptionFormDto optionDto : dto.getOptions()) {
                AssessmentOption option = AssessmentOption.createOption(optionDto);
                question.addOption(option);
            }
        }

        //3. 문항 저장 (항목들도  Cascade로 함께 저장)
        AssessmentQuestion savedQuestion = questionRepository.save(question);
        return savedQuestion.getId();
    }

    /**
     * 6. 문항 1개 삭제
     */
    @Transactional
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new IllegalArgumentException("존재하지 않는 문항입니다. ID: " + questionId);
        }

        questionRepository.deleteById(questionId);
    }

    /**
     * 7. 문항 목록 조회
     * 특정 역량에 속한 문항 목록을 dto 리스트로 반환
     */
    @Transactional
    public List<QuestionListDto> getQuestionsByCompetencyId(Long competencyId) {

        //1. 역량 존재 확인
        if (!competencyRepository.existsById(competencyId)) {
            throw new IllegalArgumentException("존재하지 않는 역량입니다. ID: " + competencyId);
        }

        // 2. 리포지토리로 문항 목록을 조회
        List<AssessmentQuestion> questions = questionRepository.findByCompetencyId(competencyId);

        // 3. 리스트를 리스트dto로 변환
        return questions.stream()
                .map(QuestionListDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 8. 문항 상세 조회
     */
    @Transactional(readOnly = true)
    public QuestionFormDto getQuestionDetails(Long questionId) {
        // 1. 문항 엔티티 조회
        AssessmentQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문항입니다."));

        // 2. Entity -> DTO 변환
        QuestionFormDto dto = new QuestionFormDto();
        dto.setId(question.getId());
        dto.setCompetencyId(question.getCompetency().getId()); // 부모 역량 ID
        dto.setQuestionText(question.getQuestionText());
        dto.setQuestionCode(question.getQuestionCode());
        dto.setQuestionType(question.getQuestionType());
        dto.setDisplayOrder(question.getDisplayOrder());
        dto.setActive(question.isActive());

        // 3. (⭐️핵심) 하위 항목(Option) 목록을 List<OptionFormDto>로 변환
        List<OptionFormDto> optionDtos = question.getOptions().stream()
                .map(option -> {
                    // OptionFormDto도 수동 매핑
                    OptionFormDto optionDto = new OptionFormDto();
                    optionDto.setId(option.getId());
                    optionDto.setOptionText(option.getOptionText());
                    optionDto.setScore(option.getScore());
                    optionDto.setDisplayOrder(option.getDisplayOrder());
                    return optionDto;
                })
                // displayOrder 순서대로 정렬해서 반환하면 JS가 편함
                .sorted((o1, o2) -> Integer.compare(o1.getDisplayOrder(), o2.getDisplayOrder()))
                .collect(Collectors.toList());

        dto.setOptions(optionDtos);

        return dto;
    }
}