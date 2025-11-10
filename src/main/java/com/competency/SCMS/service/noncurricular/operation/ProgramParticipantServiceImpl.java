package com.competency.SCMS.service.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.ApplicationStatus;
import com.competency.SCMS.domain.noncurricular.operation.ProgramApplication;
import com.competency.SCMS.dto.noncurricular.operation.*;
import com.competency.SCMS.repository.noncurricular.operation.ProgramApplicationRepository;
import com.competency.SCMS.repository.noncurricular.operation.ProgramApplicationSpecsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramParticipantServiceImpl implements ProgramParticipantService {

    private final ProgramApplicationRepository applicationRepository;


    @Override
    @Transactional(readOnly = true)
    public ParticipantPageResponseDto search(Long programId, ParticipantSearchConditionDto cond, Pageable pageable) {
        Page<ProgramApplication> page = applicationRepository.findAll(
                ProgramApplicationSpecsRepository.filter(programId, cond.getStatus(), cond.getScheduleId(), cond.getQ()),
                pageable
        );

        List<ParticipantListItemDto> rows = page.map(pa ->
                ParticipantListItemDto.builder()
                        .applicationId(pa.getApplicationId())
                        .name(pa.getStudent().getName())
                        .studentNo(pa.getStudent().getStudentNum())
                        .dept(pa.getStudent().getDepartment())
                        .grade(pa.getStudent().getGrade())
                        .phone(pa.getStudent().getPhone())
                        .appType(pa.getStatus() != null ? pa.getStatus().name() : null)
                        .scheduleId(pa.getSchedule() != null ? pa.getSchedule().getScheduleId() : null)
                        .scheduleName(pa.getSchedule() != null ? pa.getSchedule().getProgram().getTitle() : null)
                        .status(pa.getStatus())
                        .build()
        ).getContent();

        long total = page.getTotalElements();

        ParticipantStatsDto stats = new ParticipantStatsDto(
                applicationRepository.countAllByProgramId(programId),
                applicationRepository.countByProgramIdAndStatus(programId, ApplicationStatus.PENDING),
                applicationRepository.countByProgramIdAndStatus(programId, ApplicationStatus.APPROVED),
                applicationRepository.countByProgramIdAndStatus(programId, ApplicationStatus.REJECTED),
                applicationRepository.countByProgramIdAndStatus(programId, ApplicationStatus.CANCELED)
        );

        return ParticipantPageResponseDto.builder()
                .content(rows)
                .total(total)
                .stats(stats)
                .build();
    }

    @Override
    public void approve(Long programId, Long applicationId, String reason) {
        ProgramApplication pa = getOwned(programId, applicationId);
        if (pa.getStatus() == ApplicationStatus.CANCELED) return; // 정책에 맞게 처리
        pa.setStatus(ApplicationStatus.APPROVED);
        pa.setReasonText(reason);
        // 도메인 이벤트/로그 기록 필요시 추가
    }

    @Override
    public void reject(Long programId, Long applicationId, String reason) {
        ProgramApplication pa = getOwned(programId, applicationId);
        pa.setStatus(ApplicationStatus.REJECTED);
        pa.setReasonText(reason);
    }

    @Override
    public void cancel(Long programId, Long applicationId, String reason) {
        ProgramApplication pa = getOwned(programId, applicationId);
        pa.setStatus(ApplicationStatus.CANCELED);
        pa.setReasonText(reason);
    }

    @Override
    public void notifyToApplicants(Long programId, NotifyRequestDto req) {
        // 실제로는 이메일/SMS 발송 Queue/프로바이더 연동
        // 여기서는 존재 검증 정도만
        if (req.getApplicationIds() == null || req.getApplicationIds().isEmpty()) return;
        applicationRepository.findAllById(req.getApplicationIds())
                .forEach(pa -> {
                    if (!pa.getProgram().getProgramId().equals(programId)) {
                        throw new IllegalArgumentException("Program mismatch in notify");
                    }
                    // TODO: 발송 로직
                });
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportExcel(Long programId, ParticipantSearchConditionDto cond) {
        // 간단 CSV (엑셀에서 열림). 실제로는 Apache POI로 xlsx 생성 추천
        Page<ProgramApplication> page = applicationRepository.findAll(
                ProgramApplicationSpecsRepository.filter(programId, cond.getStatus(), cond.getScheduleId(), cond.getQ()),
                Pageable.unpaged()
        );
        StringBuilder sb = new StringBuilder("applicationId,name,studentNo,dept,grade,phone,appType,scheduleId,scheduleName,status\n");
        for (ProgramApplication pa : page.getContent()) {
            sb.append(pa.getApplicationId()).append(',')
                    .append(s(pa.getStudent().getName())).append(',')
                    .append(s(pa.getStudent().getStudentNum())).append(',')
                    .append(s(pa.getStudent().getDepartment())).append(',')
                    .append(pa.getStudent().getGrade() == null ? "" : pa.getStudent().getGrade()).append(',')
                    .append(s(pa.getStudent().getPhone())).append(',')
                    .append(pa.getStatus() == null ? "" : pa.getStatus().name()).append(',')
                    .append(pa.getSchedule() == null ? "" : pa.getSchedule().getScheduleId()).append(',')
                    .append(pa.getSchedule() == null ? "" : s(pa.getSchedule().getProgram().getTitle())).append(',')
                    .append(pa.getStatus() == null ? "" : pa.getStatus().name())
                    .append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String s(Object v){ return v == null ? "" : String.valueOf(v).replace(",", " "); }

    private ProgramApplication getOwned(Long programId, Long applicationId){
        ProgramApplication pa = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + applicationId));
        if (!pa.getProgram().getProgramId().equals(programId)){
            throw new IllegalArgumentException("Program mismatch");
        }
        return pa;
    }
}
