package com.competency.SCMS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
        return "auth/login";  // templates/auth/login.html로 이동
    }
}
