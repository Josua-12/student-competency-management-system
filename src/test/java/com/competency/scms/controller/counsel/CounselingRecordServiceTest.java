package com.competency.scms.controller.counsel;

import com.competency.scms.domain.counseling.CounselingRecord;
import com.competency.scms.domain.counseling.CounselingReservation;
import com.competency.scms.domain.counseling.CounselingSubField;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.dto.counsel.CounselingRecordDto;
import com.competency.scms.repository.counseling.CounselingRecordRepository;
import com.competency.scms.repository.counseling.CounselingReservationRepository;
import com.competency.scms.service.counsel.CounselingRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CounselingRecordServiceTest {

    @Mock
    private CounselingRecordRepository recordRepository;
    @Mock
    private CounselingReservationRepository reservationRepository;

    @InjectMocks
    private CounselingRecordService counselingRecordService;

    @Test
    void createRecord() {
        // Given
        User counselor = new User();
        counselor.setId(1L);
        CounselingReservation reservation = new CounselingReservation();
        reservation.setCounselor(counselor);
        reservation.setStudent(new User());
        reservation.setReservationDate(LocalDate.now());
        reservation.setStartTime(LocalTime.of(10, 0));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        CounselingRecord saved = new CounselingRecord();
        saved.setId(1L);
        when(recordRepository.save(any(CounselingRecord.class))).thenReturn(saved);
        CounselingRecordDto.CreateRequest request = new CounselingRecordDto.CreateRequest();
        request.setReservationId(1L);
        request.setRecordContent("내용");
        
        // When
        Long result = counselingRecordService.createRecord(request, counselor);
        
        // Then
        assertThat(result).isEqualTo(1L);
        verify(recordRepository).save(any(CounselingRecord.class));
    }

    @Test
    void updateRecord() {
        // Given
        User counselor = new User();
        counselor.setId(1L);
        CounselingRecord record = new CounselingRecord();
        record.setCounselor(counselor);
        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));
        CounselingRecordDto.UpdateRequest request = new CounselingRecordDto.UpdateRequest();
        request.setRecordContent("수정");
        
        // When
        counselingRecordService.updateRecord(1L, request, counselor);
        
        // Then
        verify(recordRepository).findById(1L);
    }

    @Test
    void getRecordList() {
        // Given
        User counselor = new User();
        Page<CounselingRecord> mockPage = new PageImpl<>(Arrays.asList());
        when(recordRepository.findByCounselorOrderByCreatedAtDesc(counselor, PageRequest.of(0, 10)))
            .thenReturn(mockPage);
        
        // When
        Page<CounselingRecordDto.ListResponse> result = 
            counselingRecordService.getRecordList(counselor, PageRequest.of(0, 10));
        
        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void getRecordDetail_DetailResponse반환() {
        // Given
        User counselor = new User();
        counselor.setId(1L);
        counselor.setRole(UserRole.COUNSELOR);
        CounselingSubField subField = new CounselingSubField();
        subField.setSubfieldName("진로상담");
        CounselingRecord record = new CounselingRecord();
        record.setCounselor(counselor);
        record.setStudent(new User());
        record.setSubfield(subField);
        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));
        
        // When
        CounselingRecordDto.DetailResponse result = counselingRecordService.getRecordDetail(1L, counselor);
        
        // Then
        assertThat(result).isNotNull();
    }
}
