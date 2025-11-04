package com.competency.SCMS.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(Model model) {
        model.addAttribute("error", "해당 사용자를 찾을 수 없습니다.");
        return "error/custom-error";
    }
}
