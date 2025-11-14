package com.competency.scms.controller.competency;

import com.competency.scms.domain.competency.AssessmentResult;
import com.competency.scms.dto.competency.*;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.competency.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user/assessment")
public class AssessmentController {
    private final AssessmentService assessmentService;

    /**
     * '진단하기' 메인 페이지
     * - 참여 가능한 진단 목록
     * - 완료된 진단 결과 목록
     */
    @GetMapping
    public String assessmentMain(Model model,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails.getUser().getId();

        // 1. '진단목록' 탭에 필요한 데이터 조회
        List<AssessmentSectionListDto> sections =
                assessmentService.getAssessmentSectionsForUser(currentUserId);

        // 2. '진단결과' 탭에 필요한 데이터 조회
        List<CompletedResultDto> results =
                assessmentService.getCompletedResultsForUser(currentUserId);

        // 3. Model에 데이터 추가
        model.addAttribute("assessmentSections", sections);
        model.addAttribute("completedResults", results);

        // 4. 뷰 반환
        return "competency/assessmentMain";
    }

    /**
     * '진단하기' 버튼 클릭 (진단 시작하기)
     *
     * @param sectionId   진단 섹션 ID
     * @param userDetails 현재 로그인 사용자
     * @return 진단 수행 페이지로 리다이렉트
     */
    @GetMapping("/start/{sectionId}")
    public String startAssessment(@PathVariable("sectionId") Long sectionId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails.getUser().getId();

        // 1. 서비스 호출 (DRAFT Result를 찾거나 생성)
        AssessmentResult result = assessmentService.startOrResumeAssessment(sectionId, currentUserId);

        // 2. 생성되거나 찾아낸 Result의 PK를 가지고
        //    실제 '진단 수행 페이지'로 리다이렉트
        return "redirect:/user/assessment/page/" + result.getId();
    }

    /**
     * 실제 진단 수행 페이지
     *
     * @param resultId    진단 결과 ID
     * @param userDetails
     * @param model
     * @return
     */
    @GetMapping("/page/{resultId}")
    public String showAssessmentPage(@PathVariable("resultId") Long resultId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails,
                                     Model model) {
        try {
            Long currentUserId = userDetails.getUser().getId();

            // 1. 서비스 호출 (DTO 받아오기)
            AssessmentPageDto pageData = assessmentService.getAssessmentPageData(resultId, currentUserId);

            // 2. 모델에 DTO 추가
            model.addAttribute("diagnosisForm", pageData);

            // 3. 뷰 반환
            return "competency/assessmentStart";
        } catch (IllegalArgumentException e) {
            return "redirect:/user/assessment/result/" + resultId;
        } catch (SecurityException | IllegalStateException e) {
            return "redirect:/user/assessment";
        }
    }

    /**
     * 임시저장 / 최종제출 처리
     * @param submitDto
     * @param userDetails
     * @return
     */
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleSubmitAssessment(
            @RequestBody AssessmentSubmitDto submitDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long currentId = userDetails.getUser().getId();

            // 1. 서비스 호출 (핵심 로직 실행)
            assessmentService.saveOrSubmitResponses(submitDto, currentId);

            // 2. 성공 응답 생성
            response.put("status", "success");

            // 3. DTO의 'action' 값에 따라 분기
            if ("submit".equals(submitDto.getAction())) {
                response.put("message", "진단을 성공적으로 제출했습니다.");
                response.put("redirectUrl", "/user/assessment/result/" + submitDto.getResultId());
            } else {
                response.put("message", "답변이 임시저장되었습니다.");
                response.put("redirectUrl", "/user/assessment/page/" + submitDto.getResultId()) ;
            }

            return ResponseEntity.ok(response);

        } catch (SecurityException | IllegalArgumentException e) {
            // 4. 권한이 없거나, 존재하지 않는 진단
            response.put("status", "error");
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            response.put("status", "error");
            response.put("message", "이미 제출이 완료된 진단입니다.");
            response.put("redirectUrl", "/user/assessment/result/" + submitDto.getResultId());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 최종 진단 결과 페이지
     * @param resultId
     * @param userDetails
     * @param model
     * @return
     */
    @GetMapping("/result/{resultId}")
    public String viewResult(@PathVariable("resultId") Long resultId,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {

        try {
            Long currentUserId = userDetails.getUser().getId();

            // 점수 계산 및 DTO 조립
            AssessmentResultData resultData =
                    assessmentService.getAssessmentResultData(resultId, currentUserId);

            model.addAttribute("assessmentResultData", resultData);

            return "competency/assessmentResult";
        } catch (IllegalStateException e) {
            // 아직 완료되지 않은 진단
            return "redirect:/user/assessment/page/" + resultId;
        } catch (SecurityException | IllegalArgumentException e) {
            // 권한 없거나 존재하지 않는 진단
            return "redirect:/user/assessment";
        }
    }
}
