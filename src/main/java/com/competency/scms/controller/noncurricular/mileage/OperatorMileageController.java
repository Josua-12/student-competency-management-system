package com.competency.scms.controller.noncurricular.mileage;

import com.competency.scms.service.noncurricular.mileage.OperatorMileageService;
import lombok.RequiredArgsConstructor;
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
}

