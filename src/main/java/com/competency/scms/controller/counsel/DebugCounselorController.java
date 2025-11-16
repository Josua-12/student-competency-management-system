package com.competency.scms.controller.counsel;

import com.competency.scms.domain.counseling.CounselingField;
import com.competency.scms.domain.counseling.Counselor;
import com.competency.scms.repository.counseling.CounselorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug/counselors")
@RequiredArgsConstructor
public class DebugCounselorController {
    
    private final CounselorRepository counselorRepository;
    
    @GetMapping("/all")
    public Map<String, Object> getAllCounselors() {
        List<Counselor> all = counselorRepository.findAll();
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", all.size());
        result.put("counselors", all.stream().map(c -> {
            Map<String, Object> info = new HashMap<>();
            info.put("id", c.getCounselorId());
            info.put("name", c.getUser().getName());
            info.put("field", c.getCounselingField());
            info.put("isActive", c.getIsActive());
            return info;
        }).collect(Collectors.toList()));
        
        return result;
    }
    
    @GetMapping("/by-field")
    public Map<String, Object> getCounselorsByField() {
        Map<String, Object> result = new HashMap<>();
        
        for (CounselingField field : CounselingField.values()) {
            List<Counselor> counselors = counselorRepository.findByCounselingFieldAndIsActiveTrue(field);
            result.put(field.name(), counselors.stream()
                .map(c -> c.getUser().getName() + " (ID: " + c.getCounselorId() + ")")
                .collect(Collectors.toList()));
        }
        
        return result;
    }
}
