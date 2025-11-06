package com.competency.SCMS.dto.noncurricular.program;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class ProgramPageResponse {
    private List<ProgramListRow> content;
    private int page;        // 0-based
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static ProgramPageResponse from(Page<ProgramListRow> p){
        return new ProgramPageResponse(
                p.getContent(), p.getNumber(), p.getSize(),
                p.getTotalElements(), p.getTotalPages(),
                p.isFirst(), p.isLast()
        );
    }
}