package com.competency.scms.controller.mypage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserInfoApiController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        log.info("사용자 정보 조회 API 호출");

        // 개발용 더미 데이터
        return ResponseEntity.ok(Map.of(
                "studentId", "20241234",
                "name", "홍길동",
                "birthDate", "2000-01-01",
                "email", "student@test.com",
                "address", "서울시 강남구"
        ));
    }

    @PatchMapping("/info")
    public ResponseEntity<Map<String, Object>> updateUserInfo(@RequestBody Map<String, String> request) {
        log.info("사용자 정보 수정 API 호출: {}", request);

        // 개발용: 요청받은 데이터 그대로 반환
        return ResponseEntity.ok(Map.of(
                "email", request.get("email"),
                "address", request.get("address"),
                "message", "정보가 성공적으로 수정되었습니다."
        ));
    }

    @PatchMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> request) {
        log.info("비밀번호 변경 API 호출");

        // 개발용: 항상 성공 응답
        return ResponseEntity.ok(Map.of(
                "message", "비밀번호가 성공적으로 변경되었습니다."
        ));
    }
}
