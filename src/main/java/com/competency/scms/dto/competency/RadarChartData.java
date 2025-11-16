package com.competency.scms.dto.competency;

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

    // 추가 비교균 점수
    private List<Double> deptScores;    //학과 평균
    private List<Double> univScores;    //학교 평균
}
