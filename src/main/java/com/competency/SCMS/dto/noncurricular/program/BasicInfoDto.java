package com.competency.SCMS.dto.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.CompetencyArea;
import com.competency.SCMS.domain.noncurricular.program.ProgramCategoryType;
import com.competency.SCMS.domain.noncurricular.program.RunType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BasicInfoDto {

    @NotBlank
    private String name;

    @NotNull
    private ProgramCategoryType category;

    @NotNull
    private CompetencyArea competencyArea;

    @NotBlank
    private String department; // 부서코드 or 부서명(후속 통일 필요)

    @NotBlank
    private String ownerName;

    @NotBlank
    private String ownerTel;

    @Email @NotBlank
    private String ownerEmail;

    @NotBlank
    private String summary;

    @NotNull
    private LocalDate runStart;

    @NotNull
    private LocalDate runEnd;

    @NotNull
    private LocalDateTime appStart;

    @NotNull
    private LocalDateTime appEnd;

    @NotNull @Min(1)
    private Integer capacity;

    // eligibleGrades: ["ALL"] 또는 ["1","2"...]로 들어오므로 변환 규칙 서비스에서 수행
    private List<String> eligibleGrades;

    private List<String> eligibleMajors;

    @NotNull
    private RunType runType;

    private String location;

    @NotBlank
    private String completionCriteria;

    private boolean surveyRequired;

    @Min(0)
    private Integer points;

    private List<String> competencyMappings;
}
