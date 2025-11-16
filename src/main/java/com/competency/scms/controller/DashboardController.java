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
    public ResponseEntity<Map<String, Object>> getCompetency(Authentication auth) {
        String identifier = auth.getName();
        User user;
        
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
        
        // 실제 역량 진단 결과 조회 (임시 데이터)
        // TODO: 실제 AssessmentResult 엔티티에서 조회
        // 예시: 진단 결과가 없는 경우
        boolean hasAssessmentResult = false; // 실제로는 DB에서 조회
        
        if (hasAssessmentResult) {
            return ResponseEntity.ok(Map.of(
                    "hasResult", true,
                    "labels", List.of("소통역량", "학습역량", "문제해결역량", "팀워크역량"),
                    "scores", List.of(4.2, 3.8, 4.0, 3.5),
                    "lastAssessmentDate", "2025-11-01"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "hasResult", false
            ));
        }
    }

    @GetMapping("/consultations")
    public ResponseEntity<List<Map<String, Object>>> getConsultations() {
        return ResponseEntity.ok(List.of(
                Map.of("counselorName", "김상담", "reservationDate", "2025-11-15", "status", "COMPLETED", "type", "진로상담"),
                Map.of("counselorName", "이상담", "reservationDate", "2025-11-13", "status", "COMPLETED", "type", "학업상담"),
                Map.of("counselorName", "박상담", "reservationDate", "2025-11-11", "status", "PENDING", "type", "일반상담")
        ));
    }

    @GetMapping("/programs")
    public ResponseEntity<List<Map<String, Object>>> getPrograms() {
        return ResponseEntity.ok(List.of(
                Map.of("id", 1, "title", "AI 프로그래밍 워크샵", "category", "ACADEMIC", 
                       "applicationDeadline", "2025-11-23", "status", "모집중", 
                       "currentParticipants", 15, "maxParticipants", 30),
                Map.of("id", 2, "title", "리더십 캐프", "category", "LEADERSHIP", 
                       "applicationDeadline", "2025-11-30", "status", "모집중", 
                       "currentParticipants", 8, "maxParticipants", 20),
                Map.of("id", 3, "title", "창업 아이디어 경진대회", "category", "CAREER", 
                       "applicationDeadline", "2025-12-07", "status", "모집중", 
                       "currentParticipants", 25, "maxParticipants", 50)
        ));
    }
}
