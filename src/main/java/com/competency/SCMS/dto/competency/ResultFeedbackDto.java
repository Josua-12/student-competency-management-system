package com.competency.SCMS.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultFeedbackDto {
    private String competencyName;
    private String advice;
}
