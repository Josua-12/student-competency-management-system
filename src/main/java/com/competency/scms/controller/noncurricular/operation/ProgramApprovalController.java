package com.competency.scms.controller.noncurricular.operation;

import com.competency.scms.service.noncurricular.operation.ProgramApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ProgramApprovalController {

    private final ProgramApprovalService programApprovalService;

    @PostMapping("/noncurricular/operator/programs/bulk-approve")
    public String bulkApprove(
            @RequestParam("ids") String ids,
            RedirectAttributes redirectAttributes) {

        List<Long> programIds = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .toList();

        programApprovalService.requestBulkApproval(programIds);

        redirectAttributes.addFlashAttribute("message",
                "선택한 프로그램에 승인요청을 전송했습니다.");
        return "redirect:/noncurricular/operator/programs";
    }
}
