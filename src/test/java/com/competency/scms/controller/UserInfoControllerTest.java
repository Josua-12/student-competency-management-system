package com.competency.scms.controller;

import com.competency.scms.dto.user.UserInfoResponseDto;
import com.competency.scms.dto.user.UserUpdateDto;
import com.competency.scms.service.user.UserInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserInfoController.class)
class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getUserInfo_성공() throws Exception {
        UserInfoResponseDto response = new UserInfoResponseDto(
                202212345, "홍길동", "test@example.com", "010-1234-5678", "컴퓨터공학과", 3
        );
        
        given(userInfoService.getUserInfo(any())).willReturn(response);

        mockMvc.perform(get("/api/user-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNum").value(202212345))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    void updateUserInfo_성공() throws Exception {
        UserUpdateDto dto = new UserUpdateDto("new@example.com", "010-9876-5432");

        mockMvc.perform(patch("/api/user-info")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보가 성공적으로 수정되었습니다."));
    }

    @Test
    @WithMockUser
    void updateUserInfo_유효성검사실패() throws Exception {
        UserUpdateDto dto = new UserUpdateDto("invalid-email", null);

        mockMvc.perform(patch("/api/user-info")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}