package com.competency.scms.service.noncurricular.mileage;

import com.competency.scms.domain.noncurricular.mileage.MileageRecord;
import com.competency.scms.domain.user.User;
import com.competency.scms.repository.noncurricular.mileage.MileageRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentMileageService {

    private final MileageRecordRepository mileageRecordRepository;

    public Map<String, Object> getStudentMileageHistory(User student) {
        List<MileageRecord> records = mileageRecordRepository.findByStudentOrderByCreatedAtDesc(student);
        
        int totalEarned = records.stream()
                .filter(r -> r.getPoints() > 0)
                .mapToInt(MileageRecord::getPoints)
                .sum();
        
        int totalUsed = Math.abs(records.stream()
                .filter(r -> r.getPoints() < 0)
                .mapToInt(MileageRecord::getPoints)
                .sum());
        
        int currentPoints = totalEarned - totalUsed;
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEarned", totalEarned);
        summary.put("totalUsed", totalUsed);
        summary.put("currentPoints", currentPoints);
        
        List<Map<String, Object>> recordList = records.stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", r.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    map.put("programName", r.getProgram() != null ? r.getProgram().getTitle() : "직접 지급");
                    map.put("type", r.getType().name());
                    map.put("points", r.getPoints());
                    map.put("reason", r.getRemarks() != null ? r.getRemarks() : r.getReason() != null ? r.getReason().name() : "");
                    map.put("remarks", r.getRemarks() != null ? r.getRemarks() : "");
                    return map;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("summary", summary);
        result.put("records", recordList);
        
        return result;
    }
}
