package com.competency.SCMS.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedResultDto {
    private Long id;
    private String title;
    private LocalDateTime submittedAt;
}
