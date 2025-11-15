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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noncurricular-operator/mileages")
public class MileageController {

    private final MileageService mileageService;
    private final UserRepository userRepository;

    // 1) 포인트 대상자 조회 (상단 테이블)
    @GetMapping("/eligible")
    public List<MileageEligibleRowDto> getEligible(
            @RequestParam("progId") Long programId,
            @RequestParam(value = "schdId", required = false) Long scheduleId,
            @RequestParam(value = "q", required = false) String keyword,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "dept", required = false) String dept
    ) {
        MileageEligibleSearchConditionDto cond = MileageEligibleSearchConditionDto.builder()
                .programId(programId)
                .scheduleId(scheduleId)
                .keyword(keyword)
                .from(from)
                .to(to)
                .deptCode(dept)
                .build();
        return mileageService.searchEligible(cond);
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
    public List<MileageAssignItemDto> uploadExcel(@RequestParam("file") MultipartFile file) {
        // TODO: 파일 파싱 로직 (Apache POI 등)
        // 여기선 더미로 빈 리스트 반환
        return List.of();
    }

    // 4) 임시저장
    @PostMapping("/draft")
    public ResponseEntity<Void> saveDraft(@RequestBody MileageAssignRequestDto request) {
        User operator = getCurrentUser();
        mileageService.saveDraft(request, operator);
        return ResponseEntity.ok().build();
    }

    // 5) 일괄적용
    @PostMapping("/commit")
    public ResponseEntity<Void> commit(
            @RequestBody MileageAssignRequestDto request,
            @RequestParam(value = "mode", required = false, defaultValue = "all") String mode
    ) {
        User operator= getCurrentUser();
        if ("single".equalsIgnoreCase(mode)) {
            mileageService.commitPartial(request, operator);
        } else {
            mileageService.commitAll(request, operator);
        }
        return ResponseEntity.ok().build();
    }

    // 6) 이력조회
    @GetMapping("/history")
    public List<MileageHistoryRowDto> history(
            @RequestParam("progId") Long programId,
            @RequestParam(value = "schdId", required = false) Long scheduleId) {

        return mileageService.getHistory(programId, scheduleId);
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

