package com.competency.scms.dto.dashboard;

import com.competency.scms.dto.competency.CompetencyScoreDto;
import com.competency.scms.dto.noncurricular.program.ProgramBasicDto;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDto {
    private String userName;
    private String userEmail;
    private String profileImageUrl;
    private Integer mileage;
    private Integer programCount;
    private Integer counselingCount;
    private List<CompetencyScoreDto> competencyScore;
    private List<ProgramBasicDto> recentPrograms;


}
