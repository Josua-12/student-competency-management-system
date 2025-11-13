package com.competency.scms.controller.counsel;

import com.competency.scms.domain.counseling.CounselingSatisfaction;
import com.competency.scms.domain.counseling.CounselingReservation;
import com.competency.scms.domain.counseling.ReservationStatus;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingSatisfactionDto;
import com.competency.scms.repository.counseling.CounselingReservationRepository;
import com.competency.scms.repository.counseling.CounselingSatisfactionRepository;
import com.competency.scms.repository.counseling.QuestionOptionRepository;
import com.competency.scms.repository.counseling.SatisfactionQuestionRepository;
import com.competency.scms.service.counsel.CounselingSatisfactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CounselingSatisfactionServiceTest {

    @Mock
    private CounselingSatisfactionRepository satisfactionRepository;
    @Mock
    private CounselingReservationRepository reservationRepository;
    @Mock
    private SatisfactionQuestionRepository questionRepository;
    @Mock
    private QuestionOptionRepository optionRepository;

    @InjectMocks
    private CounselingSatisfactionService counselingSatisfactionService;

    @Test
    void submitSatisfaction_Long반환() {
        // Given
        User student = new User();
        student.setId(1L);
        CounselingReservation reservation = new CounselingReservation();
        reservation.setStudent(student);
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setCounselor(new User());
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(satisfactionRepository.findByReservationId(1L)).thenReturn(Optional.empty());
        CounselingSatisfaction saved = new CounselingSatisfaction();
        saved.setId(1L);
        when(satisfactionRepository.save(any(CounselingSatisfaction.class))).thenReturn(saved);
        CounselingSatisfactionDto.SubmitRequest request = new CounselingSatisfactionDto.SubmitRequest();
        request.setReservationId(1L);
        request.setAnswers(Arrays.asList());
        
        // When
        Long result = counselingSatisfactionService.submitSatisfaction(request, student);
        
        // Then
        assertThat(result).isEqualTo(1L);
        verify(satisfactionRepository).save(any(CounselingSatisfaction.class));
    }

    @Test
    void getSurvey_SurveyResponse반환() {
        // Given
        User student = new User();
        student.setId(1L);
        User counselor = new User();
        counselor.setName("상담사");
        CounselingReservation reservation = new CounselingReservation();
        reservation.setStudent(student);
        reservation.setCounselor(counselor);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(questionRepository.findByIsActiveTrueOrderByDisplayOrderAsc()).thenReturn(Arrays.asList());
        
        // When
        CounselingSatisfactionDto.SurveyResponse result = counselingSatisfactionService.getSurvey(1L, student);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReservationId()).isEqualTo(1L);
        assertThat(result.getCounselorName()).isEqualTo("상담사");
    }
}
