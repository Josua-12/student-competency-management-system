package com.competency.SCMS.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RadarChartData {
    private List<String> labels;    // "자기관리", "문제해결", ...
    private List<Double> scores;
}
