package com.competency.scms.dto.competency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssessmentSectionFormDto {
    private Long id;

    @NotBlank(message = "진단명은 필수입니다.")
    private String title;

    private String description;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDateTime startDate;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDateTime endDate;

    private boolean isActive = true;
}
