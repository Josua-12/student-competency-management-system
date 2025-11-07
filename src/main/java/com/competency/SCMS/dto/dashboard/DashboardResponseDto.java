package com.competency.SCMS.dto.dashboard;

import com.competency.SCMS.dto.competency.CompetencyScoreDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramBasicDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDto {
    private String userName;
    private Integer mileage;
    private Integer programCount;
    private Integer counselingCount;
    private List<CompetencyScoreDto> competencyScore;
    private List<ProgramBasicDto> recentPrograms;


}
