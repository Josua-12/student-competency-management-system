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
                .filter(student -> "STUDENT".equals(student.getRole().name()))
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
                    
                    List<Map<String, Object>> activities = records.stream()
                            .map(r -> {
                                Map<String, Object> activity = new HashMap<>();
                                activity.put("date", r.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                activity.put("program", r.getProgram() != null ? r.getProgram().getTitle() : "직접지급");
                                activity.put("programId", r.getProgram() != null ? r.getProgram().getProgramId() : null);
                                activity.put("points", r.getPoints());
                                activity.put("type", r.getType().name());
                                return activity;
                            })
                            .collect(Collectors.toList());
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentNo", student.getUserNum().toString());
                    map.put("name", student.getName());
                    map.put("department", student.getDepartment() != null ? student.getDepartment().getName() : "");
                    map.put("grade", student.getGrade());
                    map.put("totalEarned", totalEarned);
                    map.put("totalUsed", totalUsed);
                    map.put("currentPoints", totalEarned - totalUsed);
                    map.put("lastActivity", lastActivity);
                    map.put("activities", activities);
                    
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
                .filter(dept -> !dept.getName().contains("관리자") && !dept.getName().contains("운영자") && !dept.getName().contains("시스템") && !dept.getName().contains("상담센터"))
                .map(dept -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", dept.getCode());
                    map.put("name", dept.getName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getEligibleStudents(Long programId) {
        var program = programRepository.findById(programId);
        if (program.isEmpty()) {
            return List.of();
        }
        
        var applications = program.get().getProgramApplications();
        if (applications == null || applications.isEmpty()) {
            return List.of();
        }
        
        return applications.stream()
                .map(app -> {
                    User student = app.getStudent();
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentNo", student.getUserNum().toString());
                    map.put("name", student.getName());
                    map.put("dept", student.getDepartment() != null ? student.getDepartment().getName() : "");
                    map.put("grade", student.getGrade());
                    map.put("completionStatus", app.getStatus().name());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void commitMileagePoints(Long programId, List<Map<String, Object>> students) {
        var program = programRepository.findById(programId).orElseThrow();
        
        students.forEach(studentData -> {
            String studentNo = studentData.get("studentNo").toString();
            int points = Integer.parseInt(studentData.get("points").toString());
            String remarks = studentData.get("remarks") != null ? studentData.get("remarks").toString() : "";
            
            User student = userRepository.findByUserNum(Integer.parseInt(studentNo)).orElseThrow();
            
            MileageRecord record = MileageRecord.builder()
                    .student(student)
                    .program(program)
                    .type(com.competency.scms.domain.noncurricular.mileage.MileageType.EARN)
                    .reason(com.competency.scms.domain.noncurricular.mileage.MileageReason.PROGRAM_COMPLETION)
                    .points(points)
                    .remarks(remarks)
                    .build();
            
            mileageRecordRepository.save(record);
        });
    }
}
