package com.competency.scms.dto.noncurricular.program.op;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class ProgramPageResponseDto {
    private List<ProgramListRowDto> content;
    private int page;        // 0-based
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static ProgramPageResponseDto from(Page<ProgramListRowDto> p){
        return new ProgramPageResponseDto(
                p.getContent(), p.getNumber(), p.getSize(),
                p.getTotalElements(), p.getTotalPages(),
                p.isFirst(), p.isLast()
        );
    }
}