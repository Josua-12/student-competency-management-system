package com.competency.scms.dto.noncurricular.program.op;

import com.competency.scms.domain.noncurricular.program.FundSource;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OperationDto {
    private String onlineUrl;
    private Integer budget; // 원
    private FundSource fundSrc;
}
