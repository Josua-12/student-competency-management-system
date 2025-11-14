// AssessmentHistoryService.java
package com.competency.scms.service.competency.mypage;

import com.competency.scms.dto.competency.mypage.AssessmentHistoryDto;
import com.competency.scms.repository.competency.AssessmentResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssessmentHistoryService {

    private final AssessmentResultRepository assessmentResultRepository;

    public List<AssessmentHistoryDto> getUserAssessmentHistory(Long userId) {
        return assessmentResultRepository.findCompletedWithSectionByUserId(userId)
                .stream()
                .map(AssessmentHistoryDto::from)
                .toList();
    }
}

