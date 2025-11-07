package com.competency.SCMS.dto.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.FundSource;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OperationDto {
    private String onlineUrl;
    private Integer budget; // 원
    private FundSource fundSrc;
}
