package com.competency.scms.controller.mypage;

import com.competency.scms.dto.user.mypage.PasswordChangeDto;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserInfoApiController {

    private final UserInfoService userInfoService;

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("사용자 정보 조회 API 호출 - userId: {}", userDetails.getId());

        UserInfoResponseDto response = userInfoService.getUserInfo(userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/info")
    public ResponseEntity<Map<String, String>> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateDto dto) {
        log.info("사용자 정보 수정 API 호출 - userId: {}", userDetails.getId());

        userInfoService.updateUserInfo(userDetails.getId(), dto);
        return ResponseEntity.ok(Map.of("message", "정보가 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordChangeDto dto) {
        log.info("비밀번호 변경 API 호출 - userId: {}", userDetails.getId());

        try {
            userInfoService.changePassword(userDetails.getId(), dto);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
