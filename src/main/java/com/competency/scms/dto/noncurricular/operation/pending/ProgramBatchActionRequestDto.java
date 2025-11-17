package com.competency.scms.dto.noncurricular.operation.pending;

import lombok.Data;

import java.util.List;

@Data
public class ProgramBatchActionRequestDto {

    /** 일괄 처리할 progId 리스트 */
    private List<Long> progIds;

    /** (반려 시) 사유 */
    private String reason;
}
