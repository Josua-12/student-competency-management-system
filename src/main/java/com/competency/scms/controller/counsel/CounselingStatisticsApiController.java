package com.competency.scms.controller.counsel;

import com.competency.scms.dto.counsel.CounselingStatisticsDto;
import com.competency.scms.service.counsel.CounselingStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counseling/statistics")
@RequiredArgsConstructor
public class CounselingStatisticsApiController {

    private final CounselingStatisticsService statisticsService;

    // CNSL-018: 상담 유형별 통계
    @GetMapping("/type")
    public ResponseEntity<CounselingStatisticsDto.TypeStatistics> getTypeStatistics() {
        CounselingStatisticsDto.TypeStatistics statistics = statisticsService.getTypeStatistics();
        return ResponseEntity.ok(statistics);
    }

    // CNSL-019: 전체 상담 현황/이력 통계
    @GetMapping("/overall")
    public ResponseEntity<CounselingStatisticsDto.OverallStatistics> getOverallStatistics() {
        CounselingStatisticsDto.OverallStatistics statistics = statisticsService.getOverallStatistics();
        return ResponseEntity.ok(statistics);
    }

    // CNSL-020: 상담만족도 결과 조회
    @GetMapping("/satisfaction")
    public ResponseEntity<List<CounselingStatisticsDto.SatisfactionResult>> getSatisfactionResults() {
        List<CounselingStatisticsDto.SatisfactionResult> results = statisticsService.getSatisfactionResults();
        return ResponseEntity.ok(results);
    }

    // CNSL-021: 상담원별 현황
    @GetMapping("/counselor")
    public ResponseEntity<List<CounselingStatisticsDto.CounselorStatistics>> getCounselorStatistics() {
        List<CounselingStatisticsDto.CounselorStatistics> statistics = statisticsService.getCounselorStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    // 관리자 대시보드용 전체 통계 조회
    @GetMapping("/overview")
    public ResponseEntity<CounselingStatisticsDto.OverallStatistics> getOverview() {
        return ResponseEntity.ok(statisticsService.getOverallStatistics());
    }
    
    // 관리자 대시보드용 상담사별 통계 조회
    @GetMapping("/counselors")
    public ResponseEntity<List<CounselingStatisticsDto.CounselorStatistics>> getCounselors() {
        return ResponseEntity.ok(statisticsService.getCounselorStatistics());
    }
}
