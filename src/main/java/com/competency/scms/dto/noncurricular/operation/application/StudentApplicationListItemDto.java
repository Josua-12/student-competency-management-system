package com.competency.scms.dto.noncurricular.operation.application;

import com.competency.scms.domain.noncurricular.operation.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class StudentApplicationListItemDto {

    private Long applicationId;    // 신청 PK
    private Long programId;        // 프로그램 PK

    private String programTitle;   // 프로그램명
    private String categoryName;   // 분류 (예: 진로/취업)
    private String programPeriod;  // 운영기간 텍스트 "2025-03-10 ~ 2025-03-12"

    private LocalDateTime appliedAt;   // 신청일

    private ApplicationStatus status;  // 상태 enum
    private String statusLabel;        // 상태 한글 라벨 ("대기", "승인" ...)

    private Integer mileage;       // 비교과 포인트

    private String completionLabel;    // "이수" / "미이수"
    private String activitySummary;    // 활동 일정 요약(회차 정보 등)

    private boolean cancelAvailable;      // 신청취소 버튼 활성 여부
    private boolean surveyAvailable;      // 만족도 조사 버튼 활성 여부
    private boolean certificateAvailable; // 이수증 발급 버튼 활성 여부

    private LocalDate programStartDate;   // data-start-date 용 (HTML data 속성)
}

