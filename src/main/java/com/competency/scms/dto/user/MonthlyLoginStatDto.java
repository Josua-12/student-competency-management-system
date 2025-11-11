package com.competency.SCMS.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyLoginStatDto {
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer visitorCount;

    public MonthlyLoginStatDto(Integer integer, int i) {
    }
}
