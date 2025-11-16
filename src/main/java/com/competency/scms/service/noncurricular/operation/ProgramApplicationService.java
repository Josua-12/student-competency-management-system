package com.competency.scms.service.noncurricular.operation;

import com.competency.scms.domain.noncurricular.operation.ApplicationStatus;
import com.competency.scms.domain.noncurricular.operation.ApprovalStatus;
import com.competency.scms.domain.noncurricular.operation.ProgramApplication;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationListDto;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationSearchConditionDto;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationSummaryDto;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProgramApplicationService {

    private final ProgramApplicationRepository programApplicationRepository;

    public Page<StudentApplicationListDto> getStudentApplicationList(
            StudentApplicationSearchConditionDto search,
            Pageable pageable) {

        // TODO: 나중에 검색 조건 붙일 때 QueryDSL/Specification으로 확장
        return programApplicationRepository.findAll(pageable)
                .map(this::toListDto);
    }

    public StudentApplicationSummaryDto getStudentApplicationSummary(
            StudentApplicationSearchConditionDto search) {

        // 지금은 검색조건 무시하고 전체 기준으로 집계
        long total = programApplicationRepository.count();
        long approved = programApplicationRepository.countByStatus(ApplicationStatus.APPROVED);
        long pending = programApplicationRepository.countByStatus(ApplicationStatus.PENDING);
        long rejected = programApplicationRepository.countByStatus(ApplicationStatus.REJECTED);
        long canceled = programApplicationRepository.countByStatus(ApplicationStatus.CANCELED);

        StudentApplicationSummaryDto dto = new StudentApplicationSummaryDto();
        dto.setTotalCount(total);
        dto.setApprovedCount(approved);
        dto.setPendingCount(pending);
        dto.setRejectedCount(rejected);
        dto.setCanceledCount(canceled);

        return dto;
    }

    private StudentApplicationListDto toListDto(ProgramApplication entity) {
        StudentApplicationListDto dto = new StudentApplicationListDto();
        // TODO: 엔티티 필드에 맞게 매핑
        // dto.setProgramName(entity.getProgram().getTitle());
        // dto.setStatus(entity.getStatus().getLabel()); 등등
        return dto;
    }

}

