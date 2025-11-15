package com.competency.scms.controller.noncurricular.program;

import com.competency.scms.dto.noncurricular.program.op.ProgramDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noncurricular-operator/programs")
public class ProgramQueryApiController {

    private final com.competency.scms.service.noncurricular.program.ProgramQueryService programQueryService;

    @GetMapping("/{programId}")
    public ProgramDetailDto getDetail(@PathVariable Long programId, Authentication auth) {
        Long operatorId = getOperatorId(auth);
        return programQueryService.getDetailForOperator(operatorId, programId);
    }

    private Long getOperatorId(Authentication auth){
        // TODO: 실제 principal에서 operatorId 추출
        return 1L;
    }
}
