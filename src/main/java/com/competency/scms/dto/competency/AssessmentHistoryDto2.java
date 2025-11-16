package com.competency.scms.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AssessmentHistoryDto2 {
    private Long resultId;
    private String assessmentTitle;
    private List<Double> scores;
}
