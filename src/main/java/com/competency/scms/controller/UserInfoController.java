package com.competency.scms.controller;

import com.competency.scms.dto.user.UserInfoResponseDto;
import com.competency.scms.dto.user.UserUpdateDto;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.user.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user-info")
@RequiredArgsConstructor
@Slf4j
public class UserInfoController {
    
    private final UserInfoService userInfoService;
    
    @GetMapping
    public ResponseEntity<UserInfoResponseDto> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("사용자 정보 조회 요청 - userId: {}", userDetails.getId());
        UserInfoResponseDto response = userInfoService.getUserInfo(userDetails.getId());
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping
    public ResponseEntity<Map<String, String>> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateDto dto) {
        log.info("사용자 정보 수정 요청 - userId: {}", userDetails.getId());
        userInfoService.updateUserInfo(userDetails.getId(), dto);
        return ResponseEntity.ok(Map.of("message", "사용자 정보가 성공적으로 수정되었습니다."));
    }
}