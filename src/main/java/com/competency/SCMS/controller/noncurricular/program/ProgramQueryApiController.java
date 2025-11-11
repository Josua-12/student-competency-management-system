package com.competency.SCMS.controller.noncurricular.program;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operator/noncurricular/programs")
public class ProgramQueryApiController {

    private final com.competency.SCMS.service.noncurricular.program.ProgramQueryService programQueryService;

    @GetMapping("/{programId}")
    public com.competency.SCMS.dto.noncurricular.program.ProgramDetailDto getDetail(@PathVariable Long programId, Authentication auth) {
        Long operatorId = getOperatorId(auth);
        return programQueryService.getDetailForOperator(operatorId, programId);
    }

    private Long getOperatorId(Authentication auth){
        // TODO: 실제 principal에서 operatorId 추출
        return 1L;
    }
}
