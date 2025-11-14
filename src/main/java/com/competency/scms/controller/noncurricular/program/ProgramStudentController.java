package com.competency.scms.controller.noncurricular.program;


import com.competency.scms.dto.noncurricular.program.student.ProgramStudentListResponse;
import com.competency.scms.dto.noncurricular.program.student.ProgramStudentSearchRequest;
import com.competency.scms.service.noncurricular.program.ProgramStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student/noncurricular")
public class ProgramStudentController {

    private final ProgramStudentService programStudentService;

    /**
     * GET /student/noncurricular/list
     * ?keyword=&category=&status=&dept=&from=&to=&page=&size=
     */
    @GetMapping("/list")
    public Page<ProgramStudentListResponse> getProgramList(ProgramStudentSearchRequest condition,
                                                           @PageableDefault(size = 9) Pageable pageable) {
        return programStudentService.getProgramListForStudent(condition, pageable);
    }

    // ▼ 이하는 추후 상세/신청 API 연동용 시그니처 예시

//    @GetMapping("/{programId}")
//    public ProgramStudentDetailResponse getProgramDetail(@PathVariable Long programId) { … }
//
//    @PostMapping("/{programId}/apply")
//    public ResponseEntity<Void> apply(@PathVariable Long programId, @AuthenticationPrincipal UserPrincipal user) { … }
}


