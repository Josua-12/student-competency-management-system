package com.competency.scms.controller.noncurricular.program;

import com.competency.scms.service.noncurricular.program.ProgramCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noncurricular-operator/programs")
public class ProgramCommandController {

    private final ProgramCommandService programCommandService;

    @PutMapping("/{programId}")
    public ResponseEntity<Void> update(@PathVariable Long programId,
                                       @RequestBody Object updateCmd,
                                       Authentication auth) {
        programCommandService.update(programId, updateCmd, getOperatorId(auth));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{programId}/approval")
    public ResponseEntity<Void> requestApproval(@PathVariable Long programId, Authentication auth) {
        programCommandService.requestApproval(programId, getOperatorId(auth));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{programId}")
    public ResponseEntity<Void> delete(@PathVariable Long programId, Authentication auth) {
        programCommandService.delete(programId, getOperatorId(auth));
        return ResponseEntity.noContent().build();
    }

    private Long getOperatorId(Authentication auth){
        // TODO: principal에서 operatorId 추출
        return 1L;
    }
}
