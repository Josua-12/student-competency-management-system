package com.competency.scms.service.competency;

import com.competency.scms.domain.competency.AssessmentQuestion;
import com.competency.scms.domain.competency.Competency;
import com.competency.scms.dto.competency.CompetencyFormDto;
import com.competency.scms.dto.competency.CompetencyTreeDto;
import com.competency.scms.dto.competency.OptionFormDto;
import com.competency.scms.dto.competency.QuestionFormDto;
import com.competency.scms.repository.competency.AssessmentOptionRepository;
import com.competency.scms.repository.competency.AssessmentQuestionRepository;
import com.competency.scms.repository.competency.CompetencyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetencyAdminServiceTest {

    // 필요한 리포지토리들 가짜로 만들기
    @Mock
    private CompetencyRepository competencyRepository;
    @Mock
    private AssessmentQuestionRepository questionRepository;
    @Mock
    private AssessmentOptionRepository optionRepository;

    // 테스트할 서비스에 가짜 객체 주입
    @InjectMocks
    private CompetencyAdminService competencyAdminService;

    @Test
    @DisplayName("역량 트리 조회하면 부모랑 자식 구조가 잘 나오는지 확인")
    void getCompetencyTree_트리구조_확인() {
        // 준비: 부모 역량 하나랑 그 밑에 자식 역량 하나 대충 만듦
        Competency root = Competency.builder()
                .id(1L)
                .name("핵심역량")
                .compCode("CORE_01")
                .displayOrder(1)
                .children(new ArrayList<>()) // 자식 리스트 초기화
                .build();

        Competency child = Competency.builder()
                .id(2L)
                .name("하위역량")
                .compCode("SUB_01")
                .displayOrder(1)
                .children(new ArrayList<>())
                .build();

        // 부모-자식 연결
        root.getChildren().add(child);

        // 리포지토리 호출되면 저 부모 역량 리스트 반환하라고 시킴
        when(competencyRepository.findByParentIsNull()).thenReturn(List.of(root));

        // 실행: 서비스 호출
        List<CompetencyTreeDto> result = competencyAdminService.getCompetencyTree();

        // 확인: 잘 가져왔나?
        assertNotNull(result);
        assertEquals(1, result.size()); // 루트는 하나여야 함
        assertEquals("핵심역량 (CORE_01)", result.get(0).getText());
        assertEquals(1, result.get(0).getChildren().size()); // 자식도 하나 있어야 함
    }

    @Test
    @DisplayName("새로운 역량 저장할 때 중복 코드 없으면 잘 저장되는지")
    void saveOrUpdateCompetency_신규저장_성공() {
        // 준비: 저장할 폼 데이터
        CompetencyFormDto dto = new CompetencyFormDto();
        dto.setName("새 역량");
        dto.setCompCode("NEW_001");

        // 저장된 후 리턴될 엔티티 흉내
        Competency savedEntity = Competency.builder().id(10L).build();

        // 중복 체크 통과시키고 (false), 저장은 성공시키기
        when(competencyRepository.existsByCompCode("NEW_001")).thenReturn(false);
        when(competencyRepository.save(any(Competency.class))).thenReturn(savedEntity);

        // 실행
        Long resultId = competencyAdminService.saveOrUpdateCompetency(dto);

        // 확인
        assertEquals(10L, resultId); // ID 잘 받아왔나
        verify(competencyRepository, times(1)).save(any(Competency.class)); // save 호출 됐나
    }

    @Test
    @DisplayName("역량 코드 중복되면 저장 안되고 에러 뱉는지 확인")
    void saveOrUpdateCompetency_중복코드_실패() {
        // 준비
        CompetencyFormDto dto = new CompetencyFormDto();
        dto.setCompCode("DUPLICATE_CODE");

        // 이미 있다고 구라치기 (true 반환)
        when(competencyRepository.existsByCompCode("DUPLICATE_CODE")).thenReturn(true);

        // 실행 및 확인: 예외 터지나?
        assertThrows(IllegalArgumentException.class, () -> {
            competencyAdminService.saveOrUpdateCompetency(dto);
        });

        // 저장은 절대 호출되면 안됨
        verify(competencyRepository, never()).save(any());
    }

    @Test
    @DisplayName("문항이랑 보기들 같이 저장할 때 잘 들어가는지 테스트")
    void saveOrUpdateQuestion_문항보기_저장() {
        // 준비: 문항 폼 데이터 (역량 ID 포함)
        QuestionFormDto dto = new QuestionFormDto();
        dto.setCompetencyId(1L);
        dto.setQuestionText("질문입니다.");

        // 보기 옵션 2개 추가
        List<OptionFormDto> options = new ArrayList<>();
        OptionFormDto opt1 = new OptionFormDto(); opt1.setOptionText("그렇다"); opt1.setScore(5);
        OptionFormDto opt2 = new OptionFormDto(); opt2.setOptionText("아니다"); opt2.setScore(1);
        options.add(opt1);
        options.add(opt2);
        dto.setOptions(options);

        // 부모 역량 찾으면 있다고 해줌
        Competency parent = Competency.builder().id(1L).build();
        when(competencyRepository.findById(1L)).thenReturn(Optional.of(parent));

        // 저장된 문항 엔티티 (ID 100번 부여받았다고 가정)
        AssessmentQuestion savedQ = AssessmentQuestion.builder().id(100L).build();
        when(questionRepository.save(any(AssessmentQuestion.class))).thenReturn(savedQ);

        // 실행
        Long qId = competencyAdminService.saveOrUpdateQuestion(dto);

        // 확인
        assertEquals(100L, qId);
        verify(questionRepository, times(1)).save(any(AssessmentQuestion.class));
    }

    @Test
    @DisplayName("역량 삭제하려는데 없는 ID면 에러 터지는지")
    void deleteCompetency_실패_없는ID() {
        // 준비: 없는 ID 999번
        Long fakeId = 999L;
        when(competencyRepository.existsById(fakeId)).thenReturn(false);

        // 실행 및 확인
        assertThrows(IllegalArgumentException.class, () -> {
            competencyAdminService.deleteCompetency(fakeId);
        });
    }
}