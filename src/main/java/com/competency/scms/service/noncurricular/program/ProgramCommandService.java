package com.competency.SCMS.service.noncurricular.program;

import java.util.List;

public interface ProgramCommandService {
    void requestApproval(Long programId);
    void requestApproval(List<Long> programIds);
    void delete(Long programId);
    void delete(List<Long> programIds);
    void update(Long programId, Object updateCommand, Long operatorId); // 필요 시 커맨드 DTO 정의
    void requestApproval(Long programId, Long operatorId);
    void delete(Long programId, Long operatorId);
}

