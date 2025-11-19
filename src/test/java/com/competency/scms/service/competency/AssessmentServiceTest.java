package com.competency.scms.service.competency;

import com.competency.scms.domain.competency.*;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.competency.AssessmentSubmitDto;
import com.competency.scms.repository.competency.*;
import com.competency.scms.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    // 서비스가 의존하는 리포지토리들 전부 Mock 처리 (가짜 객체)
    @Mock private AssessmentSectionRepository assessmentSectionRepository;
    @Mock private AssessmentResultRepository assessmentResultRepository;
    @Mock private AssessmentResponseRepository assessmentResponseRepository;
    @Mock private AssessmentQuestionRepository assessmentQuestionRepository;
    @Mock private AssessmentOptionRepository assessmentOptionRepository;
    @Mock private CompetencyRepository competencyRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private AssessmentService assessmentService;

    @Test
    @DisplayName("진단 처음 시작할 때 DB에 기록 없으면 새로 만들어주는지 확인")
    void startOrResumeAssessment_처음시작_성공() {
        // 준비
        Long userId = 1L;
        Long sectionId = 10L;

        // 1. 아직 진단 기록(DRAFT)이 없다고 가정
        when(assessmentResultRepository.findByAssessmentSectionIdAndUserIdAndStatus(sectionId, userId, AssessmentResultStatus.DRAFT))
                .thenReturn(Optional.empty());

        // 2. 유저랑 섹션 정보는 있다고 침
        User user = User.builder().id(userId).build();
        AssessmentSection section = AssessmentSection.builder().id(sectionId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(assessmentSectionRepository.findById(sectionId)).thenReturn(Optional.of(section));

        // 3. save 호출되면 저장된 객체 리턴한다고 설정
        AssessmentResult newResult = AssessmentResult.builder().id(100L).status(AssessmentResultStatus.DRAFT).build();
        when(assessmentResultRepository.save(any(AssessmentResult.class))).thenReturn(newResult);

        // 실행
        AssessmentResult result = assessmentService.startOrResumeAssessment(sectionId, userId);

        // 검증
        assertNotNull(result);
        assertEquals(100L, result.getId());
        // save가 한 번 호출됐어야 함 (새로 만드는 거니까)
        verify(assessmentResultRepository, times(1)).save(any(AssessmentResult.class));
    }

    @Test
    @DisplayName("하다가 만 진단 있으면 새로 안 만들고 그거 다시 불러오는지")
    void startOrResumeAssessment_이어하기_성공() {
        // 준비
        Long userId = 1L;
        Long sectionId = 10L;

        // 이미 하던거(DRAFT) 있다고 가정
        AssessmentResult existingResult = AssessmentResult.builder().id(50L).status(AssessmentResultStatus.DRAFT).build();

        when(assessmentResultRepository.findByAssessmentSectionIdAndUserIdAndStatus(sectionId, userId, AssessmentResultStatus.DRAFT))
                .thenReturn(Optional.of(existingResult));

        // 실행
        AssessmentResult result = assessmentService.startOrResumeAssessment(sectionId, userId);

        // 검증
        assertEquals(50L, result.getId());
        // 이미 있는거 줬으니까 save는 호출 안 돼야 정상
        verify(assessmentResultRepository, never()).save(any(AssessmentResult.class));
    }

    @Test
    @DisplayName("답안 제출하면 상태가 완료(COMPLETED)로 바뀌는지 테스트")
    void saveOrSubmitResponses_최종제출_성공() {
        // 준비
        Long userId = 1L;
        Long resultId = 200L;

        // 내 진단 결과 가져오기 (아직 DRAFT 상태)
        User user = User.builder().id(userId).build();
        AssessmentResult myResult = AssessmentResult.builder()
                .id(resultId)
                .user(user)
                .status(AssessmentResultStatus.DRAFT)
                .build(); // responses 리스트는 내부에서 초기화됨

        when(assessmentResultRepository.findById(resultId)).thenReturn(Optional.of(myResult));

        // 제출할 데이터 (문항 1번에 보기 2번 선택함)
        AssessmentSubmitDto submitDto = new AssessmentSubmitDto();
        submitDto.setResultId(resultId);
        submitDto.setAction("submit"); // submit이어야 완료 처리됨

        Map<Long, Long> responses = new HashMap<>();
        responses.put(10L, 20L); // 문항ID 10, 보기ID 20
        submitDto.setResponses(responses);

        // 문항이랑 보기 DB 조회 모킹
        AssessmentQuestion q = AssessmentQuestion.builder().id(10L).build();
        AssessmentOption o = AssessmentOption.builder().id(20L).build();

        // 단순화: 리포지토리 조회가 빈 리스트를 반환해도 에러는 안 남
        // 여기선 상세 저장 로직보다 '상태가 COMPLETED로 변하는지' 확인이 주 목적이라 생략함

        // 실행
        assessmentService.saveOrSubmitResponses(submitDto, userId);

        // 검증
        // 상태가 COMPLETED로 바꼈나?
        assertEquals(AssessmentResultStatus.COMPLETED, myResult.getStatus());
    }

    @Test
    @DisplayName("남의 진단 결과 보려고 하면 '접근 권한 없습니다' 뜨는지 (보안)")
    void getAssessmentPageData_남의꺼접근_실패() {
        // 준비: 로그인한 유저는 1번인데, 진단 결과 주인은 2번임
        Long loginUserId = 1L;
        Long targetResultId = 999L;

        User ownerUser = User.builder().id(2L).build(); // 주인은 2번
        AssessmentResult targetResult = AssessmentResult.builder()
                .id(targetResultId)
                .user(ownerUser)
                .build();

        when(assessmentResultRepository.findById(targetResultId)).thenReturn(Optional.of(targetResult));

        // 실행 및 검증: SecurityException 터져야 함
        assertThrows(SecurityException.class, () -> {
            assessmentService.getAssessmentPageData(targetResultId, loginUserId);
        });
    }
}