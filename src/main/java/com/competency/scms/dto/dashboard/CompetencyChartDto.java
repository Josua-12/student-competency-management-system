package com.competency.scms.dto.dashboard;

import java.util.List;

public record CompetencyChartDto(
        List<String> labels,
        List<Double> scores
) {
    public static CompetencyChartDto of(List<String> labels, List<Double> scores) {
        return new CompetencyChartDto(labels, scores);
    }
}