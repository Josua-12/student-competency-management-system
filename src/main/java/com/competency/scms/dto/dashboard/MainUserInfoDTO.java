package com.competency.scms.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainUserInfoDTO {
    private Long userId;
    private String userName;
    private String userEmail;
    private Integer mileage;
    private Integer programCount;
    private Integer consultationCount;
    private String profileImageUrl;
}
