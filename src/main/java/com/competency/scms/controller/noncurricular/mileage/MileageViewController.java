package com.competency.scms.controller.noncurricular.mileage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/noncurricular-mileage")
public class MileageViewController {

    @GetMapping("/operator/mileage")
    public String operatorPoints(Model model) {
        model.addAttribute("pageTitle", "비교과 포인트 조회");
        model.addAttribute("content", "noncurricular/mileage/operator-points");
        return "noncurricular/fix-screen/noncurricular-layout";
    }

    @GetMapping("/operator/mileage/register")
    public String operatorPointsRegister(Model model) {
        model.addAttribute("pageTitle", "비교과 포인트 등록");
        model.addAttribute("content", "noncurricular/mileage/operator-points-register");
        return "noncurricular/fix-screen/noncurricular-layout";
    }
}
