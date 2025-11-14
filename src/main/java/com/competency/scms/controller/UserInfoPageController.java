package com.competency.scms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class UserInfoPageController {
    
    @GetMapping("/user-info")
    public String userInfoPage() {
        return "mypage/user-info-edit";
    }
}