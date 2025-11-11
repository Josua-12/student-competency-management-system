package com.competency.scms.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller  // ← @RestController가 아님!
@RequestMapping("/auth")
@Slf4j
public class AuthViewController {

    /**
     * 로그인 페이지 표시
     */
    @GetMapping("/login")
    public String loginPage() {
        log.info("로그인 페이지 요청");
        return "auth/login";
    }
    
    // 테스트용 POST 매핑 추가
    @PostMapping("/login")
    public String loginTest() {
        log.info("로그인 POST 요청 수신!");
        return "redirect:/main";
    }
}
