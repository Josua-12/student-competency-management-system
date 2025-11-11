package com.competency.scms.controller.competency;

import com.competency.scms.dto.competency.CompetencyFormDto;
import com.competency.scms.dto.competency.CompetencyTreeDto;
import com.competency.scms.dto.competency.QuestionFormDto;
import com.competency.scms.dto.competency.QuestionListDto;
import com.competency.scms.service.competency.CompetencyAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/competency")
@RequiredArgsConstructor
public class CompetencyAdminController {

    private final CompetencyAdminService competencyAdminService;

    /**
     * 1. 관리자 페이지 반환
     */
    @GetMapping
    public String competencyAdminPage() {
        return "competency/competency";
    }

    /**
     * 2. TUI-Tree에 필요한 역량 계층 구조 조회
     */
    @GetMapping("/api/tree")
    @ResponseBody
    public List<CompetencyTreeDto> getCompetencyTree() {
        return competencyAdminService.getCompetencyTree();
    }

    /**
     * 3. 역량 1개 상세 조회 (폼 채우기)
     */
    @GetMapping("/api/competencies/{id}")
    @ResponseBody
    public ResponseEntity<CompetencyFormDto> getCompetencyDetails(@PathVariable("id") Long id) {
        try {
            CompetencyFormDto dto = competencyAdminService.getCompetencyDetails(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 4. 역량 저장/수정
     * (ID가 없으면 생성, 있으면 수정)
     */
    @PostMapping("/api/competencies")
    @ResponseBody
    public ResponseEntity<?> saveOrUpdateCompetency(@RequestBody CompetencyFormDto dto) {
        try {
            Long savedId = competencyAdminService.saveOrUpdateCompetency(dto);
            return ResponseEntity.ok(Map.of("message", "저장되었습니다.", "id", savedId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 5. 역량 삭제
     */
    @DeleteMapping("/api/competencies/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCompetency(@PathVariable("id") Long id) {
        try {
            competencyAdminService.deleteCompetency(id);
            return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "삭제 중 오류가 발생했습니다."));
        }
    }

    /**
     * 6. 특정 역량에 속한 '문앙 목록' 조회
     */
    @GetMapping("/api/competecies/{id}/questions")
    @ResponseBody
    public ResponseEntity<List<QuestionListDto>> getQuestionsByCompetency(@PathVariable("id") Long competencyId) {
        try {
            List<QuestionListDto> questions = competencyAdminService.getQuestionsByCompetencyId(competencyId);
            return ResponseEntity.ok(questions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 7. '문항' 및 '항목' 저장/수정
     */
    @PostMapping("/api/questions")
    @ResponseBody
    public ResponseEntity<?> saveOrUpdateQuestion(@RequestBody QuestionFormDto dto) {
        try {
            Long savedQuestionId = competencyAdminService.saveOrUpdateQuestion(dto);
            return ResponseEntity.ok(Map.of("message", "문항이 저장되었습니다.", "id", savedQuestionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 8. '문항' 삭제
     */
    @DeleteMapping("/api/questions/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteQuestion(@PathVariable("id") Long questionId) {
        try {
            competencyAdminService.deleteQuestion(questionId);
            return ResponseEntity.ok(Map.of("message", "묺앙이 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 9. '문항' 1개 상세 조회 (수정용)
     */
    @GetMapping("/api/questions/{id}/details")
    @ResponseBody
    public ResponseEntity<?> getQuestionDetailsForEdit(@PathVariable("id") Long questionId) {
        try {
            QuestionFormDto dto = competencyAdminService.getQuestionDetails(questionId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}