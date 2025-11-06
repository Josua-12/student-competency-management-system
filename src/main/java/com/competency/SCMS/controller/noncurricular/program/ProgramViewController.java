package com.competency.SCMS.controller.noncurricular.program;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/operator/noncurricular/programs")
public class ProgramViewController {

    @GetMapping("/{programId}")
    public String detail(@PathVariable Long programId) {
        // Thymeleaf 미사용 → 정적 템플릿 반환만
        return "operator/noncurricular/programs/detail";
    }
}
