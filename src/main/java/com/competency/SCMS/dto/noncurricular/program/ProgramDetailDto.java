package com.competency.SCMS.dto.noncurricular.program;

import com.competency.SCMS.dto.FileDto;
import com.competency.SCMS.dto.noncurricular.linkCompetency.CompetencyDto;
import com.competency.SCMS.dto.noncurricular.operation.ApprovalHistoryDto;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import java.util.List;

@Value @Builder
public class ProgramDetailDto {
    ProgramBasicDto program;
    @Singular List<ScheduleDto> schedules;
    @Singular List<CompetencyDto> competencies;
    @Singular List<FileDto> files;
    @Singular List<ApprovalHistoryDto> approvals;
}
