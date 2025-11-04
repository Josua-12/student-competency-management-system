package com.competency.SCMS.controller;

import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    @PostMapping("/admin/users/{userId}/status")
    public String updateUserStatus(@PathVariable Integer userId, @RequestParam Boolean locked, RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        user.setLocked(locked);
        userService.save(user);
        redirectAttributes.addFlashAttribute("message", locked ? "계정이 비활성화되었습니다." : "계정이 활성화되었습니다.");
        return "redirect:/admin/users/" + userId;
    }
}
