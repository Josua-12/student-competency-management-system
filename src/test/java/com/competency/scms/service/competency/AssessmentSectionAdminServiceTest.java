package com.competency.scms.service.competency;

import com.competency.scms.domain.competency.AssessmentSection;
import com.competency.scms.dto.competency.AssessmentSectionFormDto;
import com.competency.scms.repository.competency.AssessmentSectionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AssessmentSectionAdminServiceTest {

    @Mock
    private AssessmentSectionRepository sectionRepository;

    @InjectMocks
    private AssessmentSectionAdminService assessmentSectionAdminService;


    @Test
    @DisplayName("시나리오 1: 진단 섹션 상세 조회 (성공)")
    void getSectionDetails_성공() {
        // Arrange (준비)
        Long sectionId = 1L;
        AssessmentSection fakeSection = AssessmentSection.builder()
                .id(sectionId)
                .title("테스트 진단")
                .description("테스트 설명")
                .isActive(true)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .build();

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(fakeSection));


        AssessmentSectionFormDto resultDto = assessmentSectionAdminService.getSectionDetails(sectionId);

        // Assert (검증)
        assertNotNull(resultDto);
        assertEquals(sectionId, resultDto.getId());
        assertEquals("테스트 진단", resultDto.getTitle());
    }

    @Test
    @DisplayName("시나리오 2: 존재하지 않는 섹션 조회 (실패 - 예외 테스트)")
    void getSectionDetails_실패_존재하지않는ID() {
        // Arrange (준비)
        Long fakeId = 99L;

        when(sectionRepository.findById(fakeId)).thenReturn(Optional.empty());

        // Act & Assert (실행 및 검증)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assessmentSectionAdminService.getSectionDetails(fakeId);
        });

        // 2-2. (추가 검증) 발생한 예외의 메시지가 우리가 서비스에 정의한 메시지와 일치하는지 확인
        assertEquals("존재하지 않는 섹션입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("시나리오 3: 새 진단 섹션 생성 (성공)")
    void saveOrUpdateSection_새로생성_성공() {
        // Arrange (준비)
        AssessmentSectionFormDto newDto = new AssessmentSectionFormDto();
        newDto.setTitle("새로운 진단");
        newDto.setDescription("새로운 설명");
        newDto.setActive(true);

        Long newId = 1L;
        AssessmentSection savedSection = AssessmentSection.builder()
                .id(newId)
                .title("새로운 진단")
                .description("새로운 설명")
                .isActive(true)
                .build();

        when(sectionRepository.save(any(AssessmentSection.class))).thenReturn(savedSection);

        // Act (실행)
        // 2-1. 실제 서비스 메서드를 호출
        Long resultId = assessmentSectionAdminService.saveOrUpdateSection(newDto);

        // Assert (검증)
        // 3-1. 반환된 ID가 우리가 준비한 1L과 일치하는지 확인
        assertNotNull(resultId);
        assertEquals(newId, resultId);

        // 3-2.리포지토리의 save() 메서드가 '정확히 1번' 호출되었는지 검증
        verify(sectionRepository, times(1)).save(any(AssessmentSection.class));
    }

    @Test
    @DisplayName("시나리오 4: 새 진단 섹션 수정 (성공)")
    void saveOrUpdateSection_기존수정_성공() {
        // Arrange (준비)
        // 1-1. '수정'에 사용할 DTO (ID가 1L로 존재함)
        Long existingId = 1L;
        String updatedTitle = "수정된 진단 제목";

        AssessmentSectionFormDto updateDto = new AssessmentSectionFormDto();
        updateDto.setId(existingId);
        updateDto.setTitle(updatedTitle);
        updateDto.setDescription("수정된 설명");
        updateDto.setActive(true);

        AssessmentSection originalSection = AssessmentSection.builder()
                .id(existingId)
                .title("원본 제목")
                .description("원본 설명")
                .build();

        // 1-3. Mockito에게 "findById(1L)가 호출되면, originalSection을 반환해줘"
        when(sectionRepository.findById(existingId)).thenReturn(Optional.of(originalSection));

        // 1-4. Mockito에게 "save(originalSection)가 호출되면, (수정된) originalSection을 반환해줘"
        //      (서비스 로직이 originalSection 객체 자체를 수정하고 save 하므로)
        when(sectionRepository.save(originalSection)).thenReturn(originalSection);

        // Act (실행)
        // 2-1. 실제 '수정' 로직이 포함된 서비스 메서드를 호출
        Long resultId = assessmentSectionAdminService.saveOrUpdateSection(updateDto);

        // Assert (검증)
        // 3-1. 반환된 ID가 우리가 넣은 1L과 일치하는지 확인
        assertNotNull(resultId);
        assertEquals(existingId, resultId);

        // 3-2. (핵심 검증) originalSection 객체의 제목이
        //      서비스 로직에 의해 "수정된 진단 제목"으로 바뀌었는지 확인
        assertEquals(updatedTitle, originalSection.getTitle());
        assertEquals("수정된 설명", originalSection.getDescription());

        // 3-3. findById와 save가 각각 '정확히 1번' 호출되었는지 검증
        verify(sectionRepository, times(1)).findById(existingId);
        verify(sectionRepository, times(1)).save(originalSection);

    }

}