package com.competency.scms.service.noncurricular.mileage;

import com.competency.scms.domain.noncurricular.mileage.MileageRecord;
import com.competency.scms.domain.user.User;
import com.competency.scms.repository.DepartmentRepository;
import com.competency.scms.repository.noncurricular.mileage.MileageRecordRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import com.competency.scms.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperatorMileageService {

    private final MileageRecordRepository mileageRecordRepository;
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;

    public Map<String, Object> getStats() {
        List<MileageRecord> allRecords = mileageRecordRepository.findAll();
        
        int totalEarned = allRecords.stream()
                .filter(r -> r.getPoints() > 0)
                .mapToInt(MileageRecord::getPoints)
                .sum();
        
        int totalUsed = Math.abs(allRecords.stream()
                .filter(r -> r.getPoints() < 0)
                .mapToInt(MileageRecord::getPoints)
                .sum());
        
        long activeStudents = allRecords.stream()
                .map(r -> r.getStudent().getId())
                .distinct()
                .count();
        
        int avgPoints = activeStudents > 0 ? (totalEarned - totalUsed) / (int) activeStudents : 0;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEarned", totalEarned);
        stats.put("totalUsed", totalUsed);
        stats.put("activeStudents", activeStudents);
        stats.put("avgPoints", avgPoints);
        
        return stats;
    }

    public List<Map<String, Object>> getStudentsList() {
        List<MileageRecord> allRecords = mileageRecordRepository.findAll();
        
        return allRecords.stream()
                .map(MileageRecord::getStudent)
                .distinct()
                .map(student -> {
                    List<MileageRecord> records = mileageRecordRepository.findByStudentOrderByCreatedAtDesc(student);
                    
                    int totalEarned = records.stream()
                            .filter(r -> r.getPoints() > 0)
                            .mapToInt(MileageRecord::getPoints)
                            .sum();
                    
                    int totalUsed = Math.abs(records.stream()
                            .filter(r -> r.getPoints() < 0)
                            .mapToInt(MileageRecord::getPoints)
                            .sum());
                    
                    String lastActivity = records.isEmpty() ? "-" : 
                            records.get(0).getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentNo", student.getUserNum().toString());
                    map.put("name", student.getName());
                    map.put("department", student.getDepartment() != null ? student.getDepartment().getName() : "");
                    map.put("grade", student.getGrade());
                    map.put("totalEarned", totalEarned);
                    map.put("totalUsed", totalUsed);
                    map.put("currentPoints", totalEarned - totalUsed);
                    map.put("lastActivity", lastActivity);
                    
                    return map;
                })
                .filter(map -> (int)map.get("totalEarned") > 0 || (int)map.get("totalUsed") > 0)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getProgramsList() {
        return programRepository.findAll().stream()
                .map(program -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", program.getProgramId());
                    map.put("title", program.getTitle());
                    map.put("defaultPoints", program.getMileage());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDepartmentsList() {
        return departmentRepository.findAll().stream()
                .map(dept -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", dept.getCode());
                    map.put("name", dept.getName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getEligibleStudents(Long programId) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole().name().equals("STUDENT"))
                .map(student -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentNo", student.getUserNum().toString());
                    map.put("name", student.getName());
                    map.put("dept", student.getDepartment() != null ? student.getDepartment().getName() : "");
                    map.put("grade", student.getGrade());
                    map.put("completionStatus", "이수완료");
                    return map;
                })
                .collect(Collectors.toList());
    }
}
