package com.competency.SCMS.dto.competency;

import lombok.Data;

import java.util.Map;

@Data
public class AssessmentSubmitDto {

    // 어떤 진단 결과에 대한 저장인지
    private Long resultId;

    // '임시저장' 인지 '최종제출' 인지
    private String action;

    // 사용자의 모든 응답 (Key: 문항ID, Value: 선택한 보기 ID)
    // (th:name_="|responses[${q.id}]|" 태그와 매핑됨)
    private Map<Long, Long> responses;
}
