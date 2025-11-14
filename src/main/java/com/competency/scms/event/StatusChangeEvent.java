package com.competency.scms.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatusChangeEvent {
    private final String email;
    private final String type; // "PROGRAM" or "COUNSELING"
    private final String itemName; // 프로그램명 또는 상담유형
    private final String status; // "APPROVED", "REJECTED", "CANCELLED"
    private final String date; // 상담일시 (상담인 경우만)
}
