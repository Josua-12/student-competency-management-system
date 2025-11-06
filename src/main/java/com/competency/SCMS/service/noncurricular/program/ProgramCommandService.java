package com.competency.SCMS.service.noncurricular.program;

import java.util.List;

public interface ProgramCommandService {
    void requestApproval(Long programId);
    void requestApproval(List<Long> programIds);
    void delete(Long programId);
    void delete(List<Long> programIds);
}

