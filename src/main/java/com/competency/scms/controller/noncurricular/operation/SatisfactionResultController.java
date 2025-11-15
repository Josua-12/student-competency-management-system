package com.competency.scms.controller.noncurricular.operation;

import com.competency.scms.dto.noncurricular.operation.*;
import com.competency.scms.service.noncurricular.operation.SatisfactionResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noncurricular-operator/satisfaction-results")
public class SatisfactionResultController {

    private final SatisfactionResultService service;

    @GetMapping
    public SatisfactionPageResponseDto search(
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String submittedFrom, // yyyy-MM-dd
            @RequestParam(required = false) String submittedTo,
            @RequestParam(required = false) Integer ratingMin,
            @RequestParam(required = false) Integer ratingMax,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        SatisfactionSearchConditionDto cond = SatisfactionSearchConditionDto.builder()
                .programId(programId)
                .scheduleId(scheduleId)
                .dept(dept)
                .category(category)
                .status(status)
                .submittedFrom(parseDate(submittedFrom))
                .submittedTo(parseDate(submittedTo))
                .ratingMin(ratingMin)
                .ratingMax(ratingMax)
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();
        return service.search(cond);
    }

    @GetMapping("/{id}")
    public SatisfactionDetailDto getOne(@PathVariable Long id){
        return service.getById(id);
    }

    // 엑셀/PDF는 기존 설계대로 구현(여기선 엔드포인트 시그니처만)
    // @GetMapping("/export") ...
    // @PostMapping("/report") ...
    // 필요 시 content-disposition 으로 파일 응답

    private java.time.LocalDate parseDate(String s){
        if (s==null || s.isBlank()) return null;
        return java.time.LocalDate.parse(s);
    }
}

