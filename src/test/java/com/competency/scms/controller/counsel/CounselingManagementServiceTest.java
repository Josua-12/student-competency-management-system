package com.competency.scms.controller.counsel;

import com.competency.scms.domain.counseling.CounselingField;
import com.competency.scms.domain.counseling.CounselingSubField;
import com.competency.scms.domain.counseling.Counselor;
import com.competency.scms.domain.counseling.SatisfactionQuestion;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingManagementDto;
import com.competency.scms.repository.counseling.*;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.service.counsel.CounselingManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CounselingManagementServiceTest {

    @Mock
    private CounselingSubFieldRepository subFieldRepository;
    @Mock
    private CounselorRepository counselorRepository;
    @Mock
    private SatisfactionQuestionRepository satisfactionQuestionRepository;
    @Mock
    private SatisfactionAnswerRepository satisfactionAnswerRepository;
    @Mock
    private QuestionOptionRepository optionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CounselingManagementService counselingManagementService;

    @Test
    void createCategory_정상등록_Long반환() {
        // Given
        CounselingManagementDto.CategoryRequest request = new CounselingManagementDto.CategoryRequest();
        request.setCounselingField(CounselingField.ACADEMIC);
        request.setSubfieldName("학업상담");
        CounselingSubField saved = new CounselingSubField();
        saved.setId(1L);
        when(subFieldRepository.save(any(CounselingSubField.class))).thenReturn(saved);
        
        // When
        Long result = counselingManagementService.createCategory(request);
        
        // Then
        assertThat(result).isEqualTo(1L);
        verify(subFieldRepository).save(any(CounselingSubField.class));
    }

    @Test
    void createQuestion_정상등록_Long반환() {
        // Given
        CounselingManagementDto.QuestionRequest request = new CounselingManagementDto.QuestionRequest();
        request.setQuestionText("만족도");
        request.setQuestionType("RATING");
        request.setDisplayOrder(1);
        request.setIsRequired(true);
        SatisfactionQuestion saved = new SatisfactionQuestion();
        saved.setId(1L);
        when(satisfactionQuestionRepository.save(any(SatisfactionQuestion.class))).thenReturn(saved);
        
        // When
        Long result = counselingManagementService.createQuestion(request);
        
        // Then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void updateCategory_정상수정_void반환() {
        // Given
        CounselingSubField category = new CounselingSubField();
        category.setId(1L);
        when(subFieldRepository.findById(1L)).thenReturn(Optional.of(category));
        CounselingManagementDto.CategoryRequest request = new CounselingManagementDto.CategoryRequest();
        request.setCounselingField(CounselingField.CAREER);
        
        // When
        counselingManagementService.updateCategory(1L, request);
        
        // Then
        verify(subFieldRepository).findById(1L);
    }

    @Test
    void deleteCategory_정상삭제_void반환() {
        // Given
        CounselingSubField category = new CounselingSubField();
        when(subFieldRepository.findById(1L)).thenReturn(Optional.of(category));
        
        // When
        counselingManagementService.deleteCategory(1L);
        
        // Then
        verify(subFieldRepository).findById(1L);
    }

    @Test
    void createCounselor_정상등록_void반환() {
        // Given
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        CounselingManagementDto.CounselorRequest request = new CounselingManagementDto.CounselorRequest();
        request.setUserId(1L);
        request.setCounselingField(CounselingField.ACADEMIC);
        
        // When
        counselingManagementService.createCounselor(request);
        
        // Then
        verify(counselorRepository).save(any(Counselor.class));
    }

    @Test
    void updateCounselor_정상수정_void반환() {
        // Given
        User user = new User();
        user.setId(1L);
        Counselor counselor = new Counselor();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(counselorRepository.findByCounselorId(1L)).thenReturn(Optional.of(counselor));
        CounselingManagementDto.CounselorRequest request = new CounselingManagementDto.CounselorRequest();
        request.setCounselingField(CounselingField.CAREER);
        
        // When
        counselingManagementService.updateCounselor(1L, request);
        
        // Then
        verify(counselorRepository).findByCounselorId(1L);
    }

    @Test
    void deleteCounselor_정상삭제_void반환() {
        // Given
        User user = new User();
        user.setId(1L);
        Counselor counselor = new Counselor();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(counselorRepository.findByCounselorId(1L)).thenReturn(Optional.of(counselor));
        
        // When
        counselingManagementService.deleteCounselor(1L);
        
        // Then
        verify(counselorRepository).findByCounselorId(1L);
    }

    @Test
    void updateQuestion_정상수정_void반환() {
        // Given
        SatisfactionQuestion question = new SatisfactionQuestion();
        question.setIsSystemDefault(false);
        when(satisfactionQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
        CounselingManagementDto.QuestionRequest request = new CounselingManagementDto.QuestionRequest();
        request.setQuestionText("수정");
        request.setQuestionType("TEXT");
        request.setDisplayOrder(1);
        request.setIsRequired(true);
        
        // When
        counselingManagementService.updateQuestion(1L, request);
        
        // Then
        verify(satisfactionQuestionRepository).findById(1L);
    }

    @Test
    void deleteQuestion_정상삭제_void반환() {
        // Given
        SatisfactionQuestion question = new SatisfactionQuestion();
        question.setIsSystemDefault(false);
        when(satisfactionQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
        
        // When
        counselingManagementService.deleteQuestion(1L);
        
        // Then
        verify(satisfactionQuestionRepository).findById(1L);
    }

    @Test
    void getAllCategories_List반환() {
        // Given
        when(subFieldRepository.findAll()).thenReturn(Arrays.asList(new CounselingSubField()));
        
        // When
        var result = counselingManagementService.getAllCategories();
        
        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void getAllCounselors_Page반환() {
        // Given
        Page<Counselor> mockPage = new PageImpl<>(Arrays.asList());
        when(counselorRepository.findByIsActiveTrueOrderByCreatedAtDesc(any())).thenReturn(mockPage);
        
        // When
        Page<CounselingManagementDto.CounselorResponse> result = 
            counselingManagementService.getAllCounselors(PageRequest.of(0, 10));
        
        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void getAllQuestions_List반환() {
        // Given
        when(satisfactionQuestionRepository.findByIsActiveTrueOrderByDisplayOrderAsc()).thenReturn(Arrays.asList());
        
        // When
        var result = counselingManagementService.getAllQuestions();
        
        // Then
        assertThat(result).isNotNull();
    }
}
