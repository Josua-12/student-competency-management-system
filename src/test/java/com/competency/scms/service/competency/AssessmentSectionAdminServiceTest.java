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
    @DisplayName("진단 섹션 상세 조회하면 데이터 잘 나오는지 확인")
    void getSectionDetails_성공() {
        // 준비: 가짜 섹션 데이터 하나 만듦
        Long sectionId = 1L;
        AssessmentSection fakeSection = AssessmentSection.builder()
                .id(sectionId)
                .title("테스트 진단")
                .description("테스트 설명")
                .isActive(true)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .build();

        // 리포지토리에서 ID로 찾으면 저 가짜 데이터 반환하라고 시킴
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(fakeSection));

        // 실행
        AssessmentSectionFormDto resultDto = assessmentSectionAdminService.getSectionDetails(sectionId);

        // 검증: ID랑 제목 제대로 들어왔나?
        assertNotNull(resultDto);
        assertEquals(sectionId, resultDto.getId());
        assertEquals("테스트 진단", resultDto.getTitle());
    }

    @Test
    @DisplayName("없는 섹션 ID 조회하면 예외 터지는지 테스트")
    void getSectionDetails_실패_존재하지않는ID() {
        // 준비: 없는 ID 요청
        Long fakeId = 99L;
        when(sectionRepository.findById(fakeId)).thenReturn(Optional.empty());

        // 실행 및 검증: 예외 발생하는지 보고, 메시지도 맞는지 체크
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assessmentSectionAdminService.getSectionDetails(fakeId);
        });

        assertEquals("존재하지 않는 섹션입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("새로운 진단 섹션 생성하고 저장 잘 되는지")
    void saveOrUpdateSection_새로생성_성공() {
        // 준비: 폼에서 넘어온 DTO 데이터
        AssessmentSectionFormDto newDto = new AssessmentSectionFormDto();
        newDto.setTitle("새로운 진단");
        newDto.setDescription("새로운 설명");
        newDto.setActive(true);

        // 저장된 후 리턴될 객체 세팅
        Long newId = 1L;
        AssessmentSection savedSection = AssessmentSection.builder()
                .id(newId)
                .title("새로운 진단")
                .build();

        when(sectionRepository.save(any(AssessmentSection.class))).thenReturn(savedSection);

        // 실행
        Long resultId = assessmentSectionAdminService.saveOrUpdateSection(newDto);

        // 검증: ID 잘 받았고, save 메소드가 딱 한 번 호출됐는지 확인
        assertNotNull(resultId);
        assertEquals(newId, resultId);
        verify(sectionRepository, times(1)).save(any(AssessmentSection.class));
    }

    @Test
    @DisplayName("기존에 있는 진단 섹션 수정하면 내용 잘 바뀌는지")
    void saveOrUpdateSection_기존수정_성공() {
        // 준비: 수정할 내용 담은 DTO
        Long existingId = 1L;
        String updatedTitle = "수정된 진단 제목";

        AssessmentSectionFormDto updateDto = new AssessmentSectionFormDto();
        updateDto.setId(existingId);
        updateDto.setTitle(updatedTitle);
        updateDto.setDescription("수정된 설명");
        updateDto.setActive(true);

        // 원래 DB에 있던 데이터
        AssessmentSection originalSection = AssessmentSection.builder()
                .id(existingId)
                .title("원본 제목")
                .description("원본 설명")
                .build();

        // 찾으면 기존거 주고, 저장하면 수정된거 준다고 설정
        when(sectionRepository.findById(existingId)).thenReturn(Optional.of(originalSection));
        when(sectionRepository.save(originalSection)).thenReturn(originalSection);

        // 실행
        Long resultId = assessmentSectionAdminService.saveOrUpdateSection(updateDto);

        // 검증: 제목이 업데이트 됐는지 확인
        assertNotNull(resultId);
        assertEquals(existingId, resultId);
        assertEquals(updatedTitle, originalSection.getTitle());

        // 조회랑 저장 둘 다 한 번씩 호출됐나?
        verify(sectionRepository, times(1)).findById(existingId);
        verify(sectionRepository, times(1)).save(originalSection);
    }

    @Test
    @DisplayName("수정하려는데 ID가 없으면 예외 뱉는지 확인")
    void saveOrUpdateSection_수정_실패_존재하지않는ID() {
        // 준비: 없는 ID로 수정 시도
        Long fakeId = 99L;
        AssessmentSectionFormDto updateDto = new AssessmentSectionFormDto();
        updateDto.setId(fakeId);
        updateDto.setTitle("수정 시도 제목");

        // 조회했는데 없음
        when(sectionRepository.findById(fakeId)).thenReturn(Optional.empty());

        // 실행 및 검증
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assessmentSectionAdminService.saveOrUpdateSection(updateDto);
        });

        assertEquals("존재하지 않는 섹션입니다.", exception.getMessage());

        // 검증: 조회는 했어도 저장은 절대 하면 안됨
        verify(sectionRepository, times(1)).findById(fakeId);
        verify(sectionRepository, never()).save(any());
    }

    @Test
    @DisplayName("진단 섹션 삭제 요청하면 리포지토리 삭제 호출되는지")
    void deleteSection_성공() {
        // 준비
        Long sectionId = 1L;
        // 삭제하기 전에 존재 여부 확인(True)
        when(sectionRepository.existsById(sectionId)).thenReturn(true);

        // 실행
        assessmentSectionAdminService.deleteSection(sectionId);

        // 검증: 진짜 삭제 메소드 불렸나 확인
        verify(sectionRepository, times(1)).existsById(sectionId);
        verify(sectionRepository, times(1)).deleteById(sectionId);
    }

    @Test
    @DisplayName("삭제하려는데 없으면 예외 터지는지")
    void deleteSection_실패_존재하지않는ID() {
        // 준비
        Long fakeId = 99L;
        // 없다고 설정
        when(sectionRepository.existsById(fakeId)).thenReturn(false);

        // 실행 및 검증: 예외 발생해야 함
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assessmentSectionAdminService.deleteSection(fakeId);
        });

        assertEquals("존재하지 않는 섹션입니다.", exception.getMessage());

        // 없으니까 삭제 메소드는 호출 안 돼야 함
        verify(sectionRepository, times(1)).existsById(fakeId);
        verify(sectionRepository, never()).deleteById(fakeId);
    }
}