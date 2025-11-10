package com.competency.SCMS.service.competency;

import com.competency.SCMS.domain.competency.*;
import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.dto.competency.*;
import com.competency.SCMS.exception.UserNotFoundException;
import com.competency.SCMS.repository.competency.*;
import com.competency.SCMS.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssessmentService {

    private final AssessmentSectionRepository assessmentSectionRepository;
    private final AssessmentResultRepository assessmentResultRepository;
    private final AssessmentResponseRepository assessmentResponseRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final AssessmentOptionRepository assessmentOptionRepository;
    private final CompetencyRepository competencyRepository;
    private final UserRepository userRepository;

    /**
     * 1. 진단목록 탭을 위한 데이터 조회
     */
    public List<AssessmentSectionListDto> getAssessmentSectionsForUser(Long userId) {
        // 1. '진단 시작일'이 지난 활성화된 모든 진단 섹션을 가져옴 (최신순)
        List<AssessmentSection> activeSections =
                assessmentSectionRepository.findByIsActiveTrueAndStartDateBeforeOrderByStartDateDesc(LocalDateTime.now());

        // 2. 해당 유저의 모든 진단 결과(DRAFT, COMPLETED)를 가져옴
        List<AssessmentResult> userResults =
                assessmentResultRepository.findByUserId(userId);

        // 3. 섹션 ID를 Key로 하는 Map으로 반환
        Map<Long, AssessmentResult> resultMap = userResults.stream()
                .collect(Collectors.toMap(
                        result -> result.getAssessmentSection().getId(),
                        result -> result
                ));

        LocalDateTime now = LocalDateTime.now();

        // 4. 각 섹션을 DTO로 변환하며 '상태'를 결정
        return activeSections.stream()
                .map(section -> {
                    AssessmentResult result = resultMap.get(section.getId());
                    String status;
                    Long resultId = null;

                    if (result != null && result.getStatus() == AssessmentResultStatus.COMPLETED) {
                        // 1. 이미 완료한 경우
                        status = "COMPLETED";
                        resultId = result.getId();
                    } else if (now.isAfter(section.getEndDate())) {
                        // 2. 기간이 만료된 경우
                        status = "EXPIRED";
                    } else {
                        // 3. 그 외 (DRAFT 상태, 시작 전 혹은 진행 중)
                        // StartDate-EndDate 사이
                        status = "AVAILABLE";
                    }

                    return new AssessmentSectionListDto(
                            section.getId(),
                            section.getTitle(),
                            section.getStartDate(),
                            section.getEndDate(),
                            status,
                            resultId
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 2. '진단 결과' 탭을 위한 데이터 조회
     */
    public List<CompletedResultDto> getCompletedResultsForUser(Long userId) {
        // 1. (N+1 방지) Fetch Join을 사용하여 '완료'된 결과와 '섹션' 정보를 한 번에 조회
        List<AssessmentResult> completedResults =
                assessmentResultRepository.findCompletedWithSectionByUserId(userId);

        // 2. DTO로 변환
        return completedResults.stream()
                .map(result -> new CompletedResultDto(
                        result.getId(),
                        result.getAssessmentSection().getTitle(),
                        result.getSubmittedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 3. 진단 시작 또는 이어하기
     * - sectionId와 userId로 DRAFT 상태의 result를 찾기
     * - 없으면 새로 생성하고 반환
     * - 있으면 찾은 DRAFT result를 반환
     */
    @Transactional
    public AssessmentResult startOrResumeAssessment(Long sectionId, Long userId) {

        // 1. DRAFT 상태의 기존 결과가 있는지 찾습니다.
        Optional<AssessmentResult> draftResult = assessmentResultRepository
                .findByAssessmentSectionIdAndUserIdAndStatus(sectionId, userId, AssessmentResultStatus.DRAFT);

        // 2. [이어하기] DRAFT가 있으면 그대로 반환
        if (draftResult.isPresent()) {
            return draftResult.get();
        }

        // 3. [신규 시작] DRAFT가 없으면 새로 생성

        // 3-1. 부모 엔티티(User, AssessmentSection) 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        AssessmentSection section = assessmentSectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("진단 섹션을 찾을 수 없습니다."));

        // 3-2. 새 AssessmentResult(DRAFT) 생성
        AssessmentResult newResult = AssessmentResult.builder()
                .user(user)
                .assessmentSection(section)
                .status(AssessmentResultStatus.DRAFT)
                .build();

        // 3-3. 저장 후 반환
        return assessmentResultRepository.save(newResult);
    }

    /**
     * 4. 진단 페이지에 필요한 데이터 조회
     *
     * @param resultId 현재 진단 ID
     * @param userId 현재 사용자 ID
     * @return 뷰에 렌더링할 AssessmentPageDto
     */
    @Transactional(readOnly = true)
    public AssessmentPageDto getAssessmentPageData(Long resultId, Long userId) {

        // 1. Result를 조회해 이 Result가 현재 로그인한 유저의 것인지 확인
        AssessmentResult result = assessmentResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 진단입니다."));

        if (!result.getUser().getId().equals(userId)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }

        // 2. 이미 'COMPLETED'된 진단인지 확인
        if (!result.isDraft()) {
            throw new IllegalStateException("이미 완료된 진단입니다.");
        }

        // 3. DB에서 모든 역량/문항/보기 데이터를 Fetch Join으로 한 번에 조회
        List<Competency> rootComps = competencyRepository.findActiveRootCompetenciesForAssessment();

        // 4. 이전에 임시저장한 응답(Response)이 있는지 조회
        List<AssessmentResponse> responses = assessmentResponseRepository.findByAssessmentResultId(resultId);

        // 5. 응답을 (QuestionId, OptionId) 맵으로 변환 (나중에 DTO에 담기)
        Map<Long, Long> savedResponseMap = responses.stream()
                .collect(Collectors.toMap(
                        resp -> resp.getQuestion().getId(), // Key: 문항 ID
                        resp -> resp.getAssessmentOption().getId()  // Value: 선택한 보기 ID
                ));

        // 6. [데이터 변환] Entity -> DTO
        AssessmentPageDto pageDto = new AssessmentPageDto();
        pageDto.setResultId(resultId);
        pageDto.setAssessmentTitle(result.getAssessmentSection().getTitle());
        pageDto.setSavedResponses(savedResponseMap); // 5번 맵 설정

        pageDto.setRootCompetencies(
                rootComps.stream()
                        .map(this::mapRootCompetencyToDto)
                        .collect(Collectors.toList())
        );

        return pageDto;
    }

    // --- DTO 변환 헬퍼 메서드들 ---

    /**
     * 핵심역량 Entity -> DTO 변환
     */
    private RootCompetencyDto mapRootCompetencyToDto(Competency root) {
        RootCompetencyDto dto = new RootCompetencyDto();
        dto.setId(root.getId());
        dto.setName(root.getName());
        dto.setDescription(root.getDescription());

        // 1. 하위 역량이 있는지 확인
        if (root.getChildren() != null && !root.getChildren().isEmpty()) {
            dto.setSubCompetencies(
                    root.getChildren().stream()
                            .filter(Competency::isActive)
                            .sorted(Comparator.comparingInt(Competency::getDisplayOrder))
                            .map(this::mapSubCompetencyToDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    /**
     * 하위역량 Entity -> DTO 변환
     */
    private SubCompetencyDto mapSubCompetencyToDto(Competency sub) {
        SubCompetencyDto dto = new SubCompetencyDto();
        dto.setId(sub.getId());
        dto.setName(sub.getName());
        dto.setDescription(sub.getDescription());

        // 하위 역량에 속한 문항들
        if (sub.getQuestions() != null && !sub.getQuestions().isEmpty()) {
            dto.setQuestions(
                    sub.getQuestions().stream()
                            .filter(AssessmentQuestion::isActive)
                            .sorted(Comparator.comparingInt(AssessmentQuestion::getDisplayOrder))
                            .map(this::mapQuestionToDto)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    /**
     * 문항 Entity -> DTO 변환
     */
    private QuestionDto mapQuestionToDto(AssessmentQuestion q
    ) {
        QuestionDto dto = new QuestionDto();
        dto.setId(q.getId());
        dto.setQuestionText(q.getQuestionText());
        dto.setQuestionType(q.getQuestionType());

        // 문항에 속한 보기들
        if (q.getOptions() != null && !q.getOptions().isEmpty()) {
            dto.setOptions(
                    q.getOptions().stream()
                            .sorted(Comparator.comparingInt(AssessmentOption::getDisplayOrder))
                            .map(this::mapOptionToDto)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    /**
     * 보기 Entity -> DTO 변환
     */
    private OptionDto mapOptionToDto(AssessmentOption o) {
        OptionDto dto = new OptionDto();
        dto.setId(o.getId());
        dto.setOptionText(o.getOptionText());
        dto.setScore(o.getScore());
        return dto;
    }

    /**
     * 5. 임시저장 또는 최종제출 처리
     *
     * @param dto   폼에서 전송된 DTO
     * @param userId    현재 사용자 ID
     */
    @Transactional
    public void saveOrSubmitResponses(AssessmentSubmitDto dto, Long userId) {

        // 1. Result를 조회해서 현재 로그인한 유저의 것인지 확인
        AssessmentResult result = assessmentResultRepository.findById(dto.getResultId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 진단입니다."));

        if (!result.getUser().getId().equals(userId)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }

        // 2. 이미 'COMPLETED' 된 진단인지 확인
        if (!result.isDraft()) {
            throw new IllegalStateException("이미 완료된 진단입니다.");
        }

        // 3. DTO에 담겨 온 ID들을 기반으로
        //    관련 엔티티를 DB에서 한 번에 조회(IN 쿼리)

        // 3-1. 문항 Map (Key: ID, Value: Entity)
        Map<Long, AssessmentQuestion> questionMap = assessmentQuestionRepository
                .findAllById(dto.getResponses().keySet()).stream()
                .collect(Collectors.toMap(AssessmentQuestion::getId, Function.identity()));

        // 3-2. 보기 Map (Key: ID, Value: Entity)
        Map<Long, AssessmentOption> optionMap = assessmentOptionRepository
                .findAllById(dto.getResponses().values()).stream()
                .collect(Collectors.toMap(AssessmentOption::getId, Function.identity()));

        // 4. 기존 응답(Response)을 모두 삭제하고, 새 응답으로 교체
        result.getResponses().clear();

        // 5. DTO의 응답 맵을 순회하며 새 AssessmentResponse 엔티티 생성
        for (Map.Entry<Long, Long> entry : dto.getResponses().entrySet()) {
            AssessmentQuestion question = questionMap.get(entry.getKey());
            AssessmentOption option = optionMap.get(entry.getValue());

            // 혹시 DTO에 유효하지 않은 ID가 있어도 무시
            if (question == null || option == null) {
                continue;
            }

            // 5-1. 새 응답(Response) 생성
            AssessmentResponse newResponse = AssessmentResponse.builder()
                    .assessmentResult(result)   // 부모 설정
                    .question(question)         // 부모 설정
                    .assessmentOption(option)   // 부모 설정
                    .build();

            // 5-2. Result의 목록에 추가
            result.getResponses().add(newResponse);
        }

        // 6. [최종 처리] '최종제출'인 경우, Result의 상태를 변경
        if ("submit".equals(dto.getAction())) {
            result.completeSubmission();
        }

        // 7. 서비스가 종료되면 @Transactional의 더티체킹으로
        // result 엔티티의 모든 변경 사항 DB에 저장
    }


}
