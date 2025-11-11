package com.competency.scms.service.counsel;

import com.competency.scms.domain.counseling.CounselingRecord;
import com.competency.scms.domain.counseling.CounselingReservation;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.dto.counsel.CounselingRecordDto;
import com.competency.scms.exception.BusinessException;
import com.competency.scms.exception.ErrorCode;
import com.competency.scms.repository.counseling.CounselingRecordRepository;
import com.competency.scms.repository.counseling.CounselingReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingRecordService {

    private final CounselingRecordRepository recordRepository;
    private final CounselingReservationRepository reservationRepository;

    // CNSL-012: 상담일지 작성
    @Transactional
    public Long createRecord(CounselingRecordDto.CreateRequest request, User counselor) {
        CounselingReservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        if (!reservation.getCounselor().getId().equals(counselor.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        CounselingRecord record = new CounselingRecord();
        record.setReservation(reservation);
        record.setCounselor(counselor);
        record.setStudent(reservation.getStudent());
        record.setCategory(reservation.getSubField());
        record.setRecordContent(request.getRecordContent());
        record.setCounselorMemo(request.getCounselorMemo());
        record.setPublic(request.getIsPublic() != null ? request.getIsPublic() : false);
        record.setCounselingDate(reservation.getReservationDateTime());
        
        CounselingRecord saved = recordRepository.save(record);
        return saved.getId();
    }

    // CNSL-012: 상담일지 수정
    @Transactional
    public void updateRecord(Long recordId, CounselingRecordDto.UpdateRequest request, User counselor) {
        CounselingRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RECORD_NOT_FOUND));
        
        if (!record.getCounselor().getId().equals(counselor.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        record.setRecordContent(request.getRecordContent());
        record.setCounselorMemo(request.getCounselorMemo());
        if (request.getIsPublic() != null) {
            record.setPublic(request.getIsPublic());
        }
    }

    // CNSL-013: 상담일지 목록 조회
    public Page<CounselingRecordDto.ListResponse> getRecordList(User counselor, Pageable pageable) {
        Page<CounselingRecord> records = recordRepository.findByCounselorOrderByCreatedAtDesc(counselor, pageable);
        return records.map(this::toListResponse);
    }

    // CNSL-014: 상담일지 상세 조회
    public CounselingRecordDto.DetailResponse getRecordDetail(Long recordId, User currentUser) {
        CounselingRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RECORD_NOT_FOUND));
        
        validateAccessPermission(record, currentUser);
        return toDetailResponse(record);
    }

    private void validateAccessPermission(CounselingRecord record, User currentUser) {
        boolean isCounselor = currentUser.getId().equals(record.getCounselor().getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        
        if (!isCounselor && !isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private CounselingRecordDto.ListResponse toListResponse(CounselingRecord record) {
        CounselingRecordDto.ListResponse response = new CounselingRecordDto.ListResponse();
        response.setId(record.getId());
        response.setStudentName(record.getStudent().getName());
        response.setStudentId(String.valueOf(record.getStudent().getUserNum()));
        response.setSubfieldName(record.getCategory().getSubfieldName());
        response.setCounselingDate(record.getCounselingDate());
        response.setIsPublic(record.isPublic());
        response.setCreatedAt(record.getCreatedAt());
        return response;
    }

    private CounselingRecordDto.DetailResponse toDetailResponse(CounselingRecord record) {
        CounselingRecordDto.DetailResponse response = new CounselingRecordDto.DetailResponse();
        response.setId(record.getId());
        response.setStudentName(record.getStudent().getName());
        response.setStudentId(String.valueOf(record.getStudent().getUserNum()));
        response.setSubfieldName(record.getCategory().getSubfieldName());
        response.setRecordContent(record.getRecordContent());
        response.setCounselorMemo(record.getCounselorMemo());
        response.setIsPublic(record.isPublic());
        response.setCounselingDate(record.getCounselingDate());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());
        return response;
    }
}
