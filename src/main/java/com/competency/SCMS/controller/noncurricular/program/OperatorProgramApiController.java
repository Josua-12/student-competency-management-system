package com.competency.SCMS.controller.noncurricular.program;


import com.competency.SCMS.dto.noncurricular.program.ProgramPageResponseDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramSearchCondDto;
import com.competency.SCMS.service.noncurricular.program.ProgramCommandService;
import com.competency.SCMS.service.noncurricular.program.ProgramQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operator/programs")
public class OperatorProgramApiController {

    private final ProgramQueryService programQueryService;
    private final ProgramCommandService programCommandService;

    // 목록 조회(검색/정렬/페이징)
    @GetMapping
    public ProgramPageResponseDto list(ProgramSearchCondDto cond, @PageableDefault(size = 10) Pageable pageable){
        var page = programQueryService.search(cond, pageable);
        return ProgramPageResponseDto.from(page);
    }

    // 단건 승인요청
    @PostMapping("/{programId}/approval-request")
    public void requestApproval(@PathVariable Long programId){
        programCommandService.requestApproval(programId);
    }

    // 일괄 승인요청
    @PostMapping("/approval-request")
    public void requestApprovalBulk(@RequestParam("ids") java.util.List<Long> ids){
        programCommandService.requestApproval(ids);
    }

    // 단건 삭제
    @DeleteMapping("/{programId}")
    public void delete(@PathVariable Long programId){
        programCommandService.delete(programId);
    }

    // 일괄 삭제
    @DeleteMapping
    public void deleteBulk(@RequestParam("ids") java.util.List<Long> ids){
        programCommandService.delete(ids);
    }
}
