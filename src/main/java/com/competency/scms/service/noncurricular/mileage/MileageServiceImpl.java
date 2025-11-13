package com.competency.scms.service.noncurricular.mileage;

import com.competency.scms.domain.noncurricular.mileage.MileageRecord;
import com.competency.scms.domain.noncurricular.mileage.MileageType;
import com.competency.scms.domain.noncurricular.operation.ProgramApplication;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.noncurricular.mileage.*;
import com.competency.scms.repository.noncurricular.mileage.MileageRecordRepository;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import com.competency.scms.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class MileageServiceImpl implements MileageService {

    private final ProgramApplicationRepository applicationRepository;
    private final MileageRecordRepository mileageRecordRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MileageEligibleRowDto> searchEligible(MileageEligibleSearchConditionDto cond) {
        List<ProgramApplication> apps =
                applicationRepository.findEligibleForMileage(cond.getProgramId(), cond.getScheduleId());

        // TODO: cond.keyword, dept, 기간 필터는 여기서 추가 필터링 또는 JPQL로 이동
        return apps.stream()
                .map(pa -> {
                    var s = pa.getStudent();
                    Integer existing = mileageRecordRepository
                            .sumPointsByProgramAndStudent(pa.getProgram().getProgramId(), pa.getStudent());
                    return MileageEligibleRowDto.builder()
                            .applicationId(pa.getApplicationId())
                            .studentId(s.getId())
                            .studentNo(s.getUserNum().toString())
                            .name(s.getName())
                            .dept(s.getDepartment().getName())
                            .grade(s.getGrade())
                            .completionStatus(pa.getStatus().name())
                            .existingPoints(existing)
                            .build();
                })
                .collect(toList());
    }

    @Override
    public void saveDraft(MileageAssignRequestDto request, User operatorId) {
        // 단순하게도: 임시저장도 mileage_records 에 바로 넣되,
        // 추후 확정 플래그를 둘 수도 있음. 여기서는 스킵.
        commitInternal(request, operatorId, false);
    }

    @Override
    public void commitAll(MileageAssignRequestDto request, User operatorId) {
        commitInternal(request, operatorId, true);
    }

    @Override
    public void commitPartial(MileageAssignRequestDto request, User operatorId) {
        commitInternal(request, operatorId, true);
    }

    private void commitInternal(MileageAssignRequestDto request, User operatorId, boolean validate) {
        // 필요하면 validate = true 일 때 중복/포인트 한도 체크
        for (MileageAssignItemDto item : request.getItems()) {
            MileageRecord record = new MileageRecord();
            record.setProgram(request.getProgramId());
            record.setStudent(item.getStudent());
            record.setPoints(item.getPoints());
            record.setType(item.getType() != null ? item.getType() : MileageType.EARN);
            record.setRemarks(item.getDescription());
            record.setCreatedBy(operatorId);
            // createdAt 은 @PrePersist 또는 DB DEFAULT 사용
            mileageRecordRepository.save(record);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MileageHistoryRowDto> getHistory(Long programId, Long scheduleId) {
        var list = mileageRecordRepository.findHistory(programId, scheduleId);
        return list.stream()
                .map(mr -> MileageHistoryRowDto.builder()
                        .createdAt(mr.getCreatedAt())
                        .studentNo(mr.getStudent().toString())
                        .name(mr.getStudent().getName())
                        .type(mr.getType())
                        .points(mr.getPoints())
                        .description(mr.getProgram().getDescription())
                        .createdByName(mr.getCreatedBy().getName())
                        .build())
                .collect(toList());
    }
}

