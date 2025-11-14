package com.competency.scms.dto.user;

import com.competency.scms.domain.user.User;

public record UserInfoResponseDto(
        Integer userNum,
        String name,
        String email,
        String phone,
        String department,
        Integer grade
) {
    public static UserInfoResponseDto from(User user) {
        return new UserInfoResponseDto(
                user.getUserNum(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getDepartment() != null ? user.getDepartment().getName() : null,
                user.getGrade()
        );
    }
}