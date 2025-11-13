package com.competency.scms.service.noncurricular.mileage;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.noncurricular.mileage.*;

import java.util.List;

public interface MileageService {

    List<MileageEligibleRowDto> searchEligible(MileageEligibleSearchConditionDto cond);

    void saveDraft(MileageAssignRequestDto request, User operator);

    void commitAll(MileageAssignRequestDto request, User operator);

    void commitPartial(MileageAssignRequestDto request, User operator);

    List<MileageHistoryRowDto> getHistory(Long programId, Long scheduleId);
}

