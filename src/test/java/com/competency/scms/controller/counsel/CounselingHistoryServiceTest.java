package com.competency.scms.controller.counsel;

import com.competency.scms.domain.counseling.CounselingField;
import com.competency.scms.domain.counseling.CounselingReservation;
import com.competency.scms.domain.counseling.CounselingSubField;
import com.competency.scms.domain.counseling.ReservationStatus;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.dto.counsel.CounselingHistoryDto;
import com.competency.scms.repository.counseling.CounselingReservationRepository;

import com.competency.scms.service.counsel.CounselingHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CounselingHistoryServiceTest {

    @Mock
    private CounselingReservationRepository reservationRepository;

    @InjectMocks
    private CounselingHistoryService counselingHistoryService;

    @Test
    void getAllHistory_상담사권한_Page반환() {
        // Given
        User counselor = new User();
        counselor.setRole(UserRole.COUNSELOR);
        User student = new User();
        student.setUserNum(20240001);
        student.setName("김학생");
        CounselingSubField subField = new CounselingSubField();
        subField.setSubfieldName("진로상담");
        CounselingReservation reservation = new CounselingReservation();
        reservation.setStudent(student);
        reservation.setCounselor(counselor);
        reservation.setCounselingField(CounselingField.CAREER);
        reservation.setSubField(subField);
        Pageable pageable = PageRequest.of(0, 10);
        Page<CounselingReservation> testPage = new PageImpl<>(Arrays.asList(reservation));
        when(reservationRepository.findByCounselorOrderByCreatedAtDesc(eq(counselor), any(Pageable.class)))
            .thenReturn(testPage);
        
        // When
        Page<CounselingHistoryDto.HistoryResponse> result = 
            counselingHistoryService.getAllHistory(new CounselingHistoryDto.SearchCondition(), counselor, pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void getCounselorHistory_상담사별조회_Page반환() {
        // Given
        User counselor = new User();
        Pageable pageable = PageRequest.of(0, 10);
        Page<CounselingReservation> testPage = new PageImpl<>(Arrays.asList());
        when(reservationRepository.findByCounselorOrderByCreatedAtDesc(counselor, pageable))
            .thenReturn(testPage);
        
        // When
        Page<CounselingHistoryDto.HistoryResponse> result = 
            counselingHistoryService.getCounselorHistory(counselor, pageable);
        
        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void getCounselorStatus_상담사현황조회_StatusResponse반환() {
        // Given
        User counselor = new User();
        counselor.setName("상담사");
        List<ReservationStatus> statuses = Arrays.asList(ReservationStatus.PENDING, ReservationStatus.CONFIRMED, ReservationStatus.COMPLETED);
        Page<CounselingReservation> testPage = new PageImpl<>(Arrays.asList());
        when(reservationRepository.findByCounselorAndStatusIn(eq(counselor), eq(statuses), any(Pageable.class)))
            .thenReturn(testPage);
        
        // When
        CounselingHistoryDto.StatusResponse result = counselingHistoryService.getCounselorStatus(counselor);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCounselorName()).isEqualTo("상담사");
    }
}
