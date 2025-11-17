package com.competency.scms.controller.noncurricular.mileage;

import com.competency.scms.service.noncurricular.mileage.OperatorMileageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OperatorMileageController {

    private final OperatorMileageService operatorMileageService;

    @GetMapping("/api/noncurricular-operator/mileages/stats")
    public Map<String, Object> getStats() {
        return operatorMileageService.getStats();
    }

    @GetMapping("/api/noncurricular-operator/mileages/students")
    public List<Map<String, Object>> getStudents() {
        return operatorMileageService.getStudentsList();
    }

    @GetMapping("/api/noncurricular-operator/mileages/programs/list")
    public List<Map<String, Object>> getPrograms() {
        return operatorMileageService.getProgramsList();
    }

    @GetMapping("/api/noncurricular-operator/programs/list")
    public List<Map<String, Object>> getProgramsAlias() {
        return operatorMileageService.getProgramsList();
    }

    @GetMapping("/api/noncurricular-operator/mileages/departments")
    public List<Map<String, Object>> getDepartments() {
        return operatorMileageService.getDepartmentsList();
    }

    @GetMapping("/api/noncurricular-operator/programs/{id}/schedules")
    public List<Map<String, Object>> getProgramSchedules(@PathVariable Long id) {
        return List.of(Map.of("id", 1L, "name", "전체"));
    }

    @PutMapping("/api/noncurricular-operator/programs/{id}/default-points")
    public ResponseEntity<Map<String, Object>> updateDefaultPoints(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        int points = Integer.parseInt(request.get("points").toString());
        boolean applyToExisting = Boolean.parseBoolean(request.get("applyToExisting").toString());
        
        return ResponseEntity.ok(Map.of("success", true, "message", "기본 포인트가 수정되었습니다."));
    }

    @PostMapping("/api/noncurricular-operator/mileages/batch")
    public ResponseEntity<Map<String, Object>> batchUpdatePoints(@RequestBody Map<String, Object> request) {
        int points = Integer.parseInt(request.get("points").toString());
        @SuppressWarnings("unchecked")
        List<String> studentNos = (List<String>) request.get("studentNos");
        
        return ResponseEntity.ok(Map.of("success", true, "count", studentNos.size()));
    }
}

