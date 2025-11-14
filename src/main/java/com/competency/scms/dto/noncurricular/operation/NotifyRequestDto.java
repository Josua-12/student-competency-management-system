package com.competency.scms.dto.noncurricular.operation;

import lombok.Value;

import java.util.List;

@Value
public class NotifyRequestDto {
    List<Long> applicationIds;
    List<String> channel; // ["EMAIL","SMS"]
}
