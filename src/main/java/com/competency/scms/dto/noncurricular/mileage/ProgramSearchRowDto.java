package com.competency.scms.dto.noncurricular.mileage;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProgramSearchRowDto {
    Long id;            // progId
    String title;       // 제목
    String deptName;    // 부서명
    String periodText;  // 기간(문자열)
    String status;      // 상태(DRAFT/APPROVED/COMPLETED...)
    
    public ProgramSearchRowDto(Long id, String title, String deptName, String periodText, Object status) {
        this.id = id;
        this.title = title;
        this.deptName = deptName;
        this.periodText = periodText;
        this.status = status != null ? status.toString() : null;
    }
}
