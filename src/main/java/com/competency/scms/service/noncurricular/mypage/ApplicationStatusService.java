package com.competency.scms.service.noncurricular.mypage;

import com.competency.scms.dto.noncurricular.mypage.ApplicationStatusDto;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationStatusService {

    private final ProgramApplicationRepository programApplicationRepository;

    public List<ApplicationStatusDto> getUserApplicationStatus(Long userId) {
        return programApplicationRepository.findMyApplications(userId, null, PageRequest.of(0, 100))
                .getContent()
                .stream()
                .map(ApplicationStatusDto::from)
                .toList();
    }
}
