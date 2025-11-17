package com.competency.scms.dto.noncurricular.operation.pending;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class ProgramPendingListResultDto {

    private List<ProgramPendingListItemDto> content;

    private int page;          // 현재 페이지 (0-based)
    private int size;          // 페이지 크기
    private long totalElements;
    private int totalPages;

    public static ProgramPendingListResultDto fromPage(Page<ProgramPendingListItemDto> page) {
        return ProgramPendingListResultDto.builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}

