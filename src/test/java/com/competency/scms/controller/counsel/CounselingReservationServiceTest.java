package com.competency.scms.controller.counsel;

import com.competency.scms.domain.counseling.CounselingReservation;
import com.competency.scms.domain.counseling.CounselingSubField;
import com.competency.scms.domain.counseling.ReservationStatus;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.dto.counsel.CounselingReservationDto;
import com.competency.scms.repository.counseling.CounselingReservationRepository;
import com.competency.scms.repository.counseling.CounselingSubFieldRepository;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.service.counsel.CounselingReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CounselingReservationServiceTest {

    @Mock
    private CounselingReservationRepository reservationRepository;
    @Mock
    private CounselingSubFieldRepository counselingFieldRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CounselingReservationService counselingReservationService;

    @Test
    void createReservation_Long반환() {
        // Given
        User student = new User();
        student.setRole(UserRole.STUDENT);
        CounselingSubField subField = new CounselingSubField();
        when(counselingFieldRepository.findById(1L)).thenReturn(Optional.of(subField));
        CounselingReservation saved = new CounselingReservation();
        saved.setId(1L);
        when(reservationRepository.save(any(CounselingReservation.class))).thenReturn(saved);
        CounselingReservationDto.CreateRequest request = new CounselingReservationDto.CreateRequest();
        request.setSubFieldId(1L);
        
        // When
        Long result = counselingReservationService.createReservation(request, student);
        
        // Then
        assertThat(result).isEqualTo(1L);
        verify(reservationRepository).save(any(CounselingReservation.class));
    }

    @Test
    void cancelReservation_void반환() {
        // Given
        User student = new User();
        student.setId(1L);
        CounselingReservation reservation = new CounselingReservation();
        reservation.setStudent(student);
        reservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        CounselingReservationDto.CancelRequest request = new CounselingReservationDto.CancelRequest();
        request.setCancelReason("취소");
        
        // When
        counselingReservationService.cancelReservation(1L, request, student);
        
        // Then
        verify(reservationRepository).findById(1L);
    }

    @Test
    void approveReservation_void반환() {
        // Given
        User counselor = new User();
        counselor.setRole(UserRole.COUNSELOR);
        CounselingReservation reservation = new CounselingReservation();
        reservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        
        // When
        counselingReservationService.approveReservation(1L, LocalDateTime.now(), "승인", counselor);
        
        // Then
        verify(reservationRepository).findById(1L);
    }

    @Test
    void rejectReservation_void반환() {
        // Given
        User counselor = new User();
        CounselingReservation reservation = new CounselingReservation();
        reservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        
        // When
        counselingReservationService.rejectReservation(1L, "거부", counselor);
        
        // Then
        verify(reservationRepository).findById(1L);
    }

    @Test
    void getMyReservations_Page반환() {
        // Given
        User student = new User();
        Page<CounselingReservation> mockPage = new PageImpl<>(Arrays.asList());
        when(reservationRepository.findByStudentOrderByCreatedAtDesc(student, PageRequest.of(0, 10)))
            .thenReturn(mockPage);
        
        // When
        Page<CounselingReservationDto.ListResponse> result = 
            counselingReservationService.getMyReservations(student, new CounselingReservationDto.SearchCondition(), PageRequest.of(0, 10));
        
        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void getAssignedReservations_Page반환() {
        // Given
        User counselor = new User();
        Page<CounselingReservation> mockPage = new PageImpl<>(Arrays.asList());
        when(reservationRepository.findByCounselorAndStatusOrderByConfirmedDateTimeAsc(counselor, ReservationStatus.CONFIRMED, PageRequest.of(0, 10)))
            .thenReturn(mockPage);
        
        // When
        Page<CounselingReservationDto.ListResponse> result = 
            counselingReservationService.getAssignedReservations(counselor, PageRequest.of(0, 10));
        
        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void getReservationDetail_DetailResponse반환() {
        // Given
        User student = new User();
        student.setId(1L);
        CounselingReservation reservation = new CounselingReservation();
        CounselingSubField subField = new CounselingSubField();
        subField.setSubfieldName("진로상담");
        reservation.setStudent(student);
        reservation.setSubField(subField);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        
        // When
        CounselingReservationDto.DetailResponse result = 
            counselingReservationService.getReservationDetail(1L, student);
        
        // Then
        assertThat(result).isNotNull();
    }
}
