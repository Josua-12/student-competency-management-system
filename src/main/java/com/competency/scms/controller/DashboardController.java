package com.competency.scms.controller;

import com.competency.scms.domain.user.User;
import com.competency.scms.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final UserRepository userRepository;

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication auth) {
        String identifier = auth.getName();
        log.info("JWT에서 추출한 식별자: {}", identifier);

        User user;

        // 이메일 형식인지 확인
        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + identifier));
        } else {
            try {
                Integer userNum = Integer.parseInt(identifier);
                user = userRepository.findByUserNum(userNum)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + identifier));
            } catch (NumberFormatException e) {
                throw new RuntimeException("잘못된 사용자 식별자: " + identifier);
            }
        }

        log.info("조회된 사용자: 이름={}, 이메일={}", user.getName(), user.getEmail());

        return ResponseEntity.ok(Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "mileage", 0,
                "programCount", 0
        ));
    }

    @GetMapping("/competency")
    public ResponseEntity<Map<String, Object>> getCompetency() {
        return ResponseEntity.ok(Map.of(
                "chart", Map.of("labels", List.of(), "datasets", List.of()),
                "list", List.of()
        ));
    }

    @GetMapping("/consultations")
    public ResponseEntity<List<Map<String, Object>>> getConsultations() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/programs")
    public ResponseEntity<List<Map<String, Object>>> getPrograms() {
        return ResponseEntity.ok(List.of());
    }
}
