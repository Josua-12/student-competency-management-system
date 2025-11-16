package com.competency.scms.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AssessmentHistoryPageDto {
    private List<String> competencyLabels;
    private List<AssessmentHistoryDto2> historyData;
}
