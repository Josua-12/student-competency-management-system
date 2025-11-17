package com.competency.scms.controller.noncurricular.mileage;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.noncurricular.mileage.*;
import com.competency.scms.dto.noncurricular.operation.*;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.noncurricular.mileage.MileageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noncurricular-operator/mileages")
public class MileageController {

    private final UserRepository userRepository;
    private final com.competency.scms.service.noncurricular.mileage.OperatorMileageService operatorMileageService;

    // 1) 포인트 대상자 조회 (상단 테이블)
    @GetMapping("/eligible")
    public List<Map<String, Object>> getEligible(
            @RequestParam("progId") Long programId) {
        return operatorMileageService.getEligibleStudents(programId);
    }

    // 2) 양식 다운로드 (간단히 CSV or 엑셀)
    @GetMapping("/template")
    public ResponseEntity<String> downloadTemplate() {
        // 실제에선 application/vnd.ms-excel 로 파일 내려주면 됨.
        String header = "studentNo,points,type,description\n";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"mileage_template.csv\"")
                .body(header);
    }

    // 3) 엑셀 업로드 → 미리보기용 DTO 리스트 반환
    @PostMapping("/upload")
    public List<Map<String, Object>> uploadExcel(@RequestParam("file") MultipartFile file) {
        return List.of();
    }

    // 4) 임시저장
    @PostMapping("/draft")
    public ResponseEntity<Map<String, Object>> saveDraft(@RequestBody Map<String, Object> request) {
        Long programId = Long.valueOf(request.get("programId").toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> students = (List<Map<String, Object>>) request.get("students");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", students.size() + "명의 학생 포인트가 임시저장되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 5) 일괄적용
    @PostMapping("/commit")
    public ResponseEntity<Map<String, Object>> commit(@RequestBody Map<String, Object> request) {
        Long programId = Long.valueOf(request.get("programId").toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> students = (List<Map<String, Object>>) request.get("students");
        
        operatorMileageService.commitMileagePoints(programId, students);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", students.size() + "명의 학생에게 포인트가 성공적으로 등록되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 6) 이력조회
    @GetMapping("/history")
    public List<Map<String, Object>> history() {
        return List.of();
    }

    // TODO: 실제 구현은 SecurityContext 에서 사용자 ID 가져오기
    private User getCurrentUser() {
        // SecurityContext에서 인증 객체 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("사용자 인증 정보를 찾을 수 없습니다.");
        }

        Object principal = auth.getPrincipal();

        // principal이 UserDetails 구현체일 경우
        if (principal instanceof CustomUserDetails userDetails) {
            return userRepository.findByUserNum(userDetails.getUser().getUserNum())
                    .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));
        }

        throw new IllegalStateException("올바르지 않은 인증 정보입니다.");
    }
}

