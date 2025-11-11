package com.competency.SCMS.dto.common;

import lombok.Data;

@Data
public class PageRequest {
    private Integer page = 0;
    private Integer size = 10;
    
    public int getOffset() {
        return page * size;
    }
}
