package com.competency.scms.controller.counsel;

import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.dto.counsel.*;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.counsel.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
    CounselingController.class,
    CounselingReservationApiController.class,
    CounselingRecordApiController.class,
    CounselingHistoryApiController.class,
    CounselingManagementApiController.class,
    CounselingSatisfactionApiController.class,
    CounselingStatisticsApiController.class
})
class CounselingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CounselingReservationService reservationService;

    @MockBean
    private CounselingRecordService recordService;

    @MockBean
    private CounselingHistoryService historyService;

    @MockBean
    private CounselingManagementService managementService;

    @MockBean
    private CounselingSatisfactionService satisfactionService;

    @MockBean
    private CounselingStatisticsService statisticsService;

    @MockBean
    private CounselingScheduleService scheduleService;

    private User studentUser;
    private User counselorUser;
    private User adminUser;
    private CustomUserDetails studentDetails;
    private CustomUserDetails counselorDetails;
    private CustomUserDetails adminDetails;

    @BeforeEach
    void setup() {
        studentUser = User.builder()
                .id(1L)
                .userNum(20250001)
                .name("학생")
                .email("student@test.com")
                .password("password")
                .birthDate(LocalDate.of(2000, 1, 1))
                .role(UserRole.STUDENT)
                .locked(false)
                .build();

        counselorUser = User.builder()
                .id(2L)
                .userNum(20250002)
                .name("상담사")
                .email("counselor@test.com")
                .password("password")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(UserRole.COUNSELOR)
                .locked(false)
                .build();

        adminUser = User.builder()
                .id(3L)
                .userNum(20250003)
                .name("관리자")
                .email("admin@test.com")
                .password("password")
                .birthDate(LocalDate.of(1985, 1, 1))
                .role(UserRole.ADMIN)
                .locked(false)
                .build();

        studentDetails = new CustomUserDetails(studentUser);
        counselorDetails = new CustomUserDetails(counselorUser);
        adminDetails = new CustomUserDetails(adminUser);
    }



    @Test
    @DisplayName("테스트1 - 학생 상담 메인 페이지 조회")
    @WithMockUser(roles = "STUDENT")
    void studentCounselingMain() throws Exception {
        mockMvc.perform(get("/counseling/student"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/student/main"));
    }

    @Test
    @DisplayName("테스트2 - 학생 상담 신청 현황 조회")
    @WithMockUser(roles = "STUDENT")
    void studentCounselingStatus() throws Exception {
        mockMvc.perform(get("/counseling/student/status"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/student/state"));
    }

    @Test
    @DisplayName("테스트3 - 상담사 메인 페이지 조회")
    @WithMockUser(roles = "COUNSELOR")
    void counselorMain() throws Exception {
        mockMvc.perform(get("/counseling/counselor"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/counselor/counselor-main"));
    }

    @Test
    @DisplayName("테스트4 - 상담사 일정 관리 페이지 조회")
    @WithMockUser(roles = "COUNSELOR")
    void counselorSchedule() throws Exception {
        mockMvc.perform(get("/counseling/counselor/schedule"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/counselor/schedule-management"));
    }

    @Test
    @DisplayName("테스트5 - 상담사 예약 승인 관리 페이지 조회")
    @WithMockUser(roles = "COUNSELOR")
    void counselorReservations() throws Exception {
        mockMvc.perform(get("/counseling/counselor/reservations"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/counselor/reservation-management"));
    }

    @Test
    @DisplayName("테스트6 - 상담사 상담일지 관리 페이지 조회")
    @WithMockUser(roles = "COUNSELOR")
    void counselorRecords() throws Exception {
        mockMvc.perform(get("/counseling/counselor/records"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/counselor/record-management"));
    }

    @Test
    @DisplayName("테스트7 - 상담사 상담 이력 조회 페이지")
    @WithMockUser(roles = "COUNSELOR")
    void counselorHistory() throws Exception {
        mockMvc.perform(get("/counseling/counselor/history"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/counselor/history-management"));
    }

    @Test
    @DisplayName("테스트8 - 상담사 만족도 조회 페이지")
    @WithMockUser(roles = "COUNSELOR")
    void counselorSatisfaction() throws Exception {
        mockMvc.perform(get("/counseling/counselor/satisfaction"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/counselor/satisfaction-results"));
    }

    @Test
    @DisplayName("테스트9 - 관리자 메인 페이지 조회")
    @WithMockUser(roles = "ADMIN")
    void adminMain() throws Exception {
        mockMvc.perform(get("/counseling/admin"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/admin/admin-main"));
    }

    @Test
    @DisplayName("테스트10 - 관리자 상담 승인 관리 페이지 조회")
    @WithMockUser(roles = "ADMIN")
    void adminApprovals() throws Exception {
        mockMvc.perform(get("/counseling/admin/approvals"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/admin/approval-management"));
    }

    @Test
    @DisplayName("테스트11 - 관리자 상담 통계 페이지 조회")
    @WithMockUser(roles = "ADMIN")
    void adminStatistics() throws Exception {
        mockMvc.perform(get("/counseling/admin/statistics"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/admin/statistics"));
    }

    @Test
    @DisplayName("테스트12 - 관리자 상담 기초 관리 페이지 조회")
    @WithMockUser(roles = "ADMIN")
    void adminSettings() throws Exception {
        mockMvc.perform(get("/counseling/admin/settings"))
            .andExpect(status().isOk())
            .andExpect(view().name("counseling/admin/basic-settings"));
    }

    @Test
    @DisplayName("테스트13 - 상담 예약 등록")
    @WithMockUser(roles = "STUDENT")
    void createReservation() throws Exception {
        given(reservationService.createReservation(any(), any())).willReturn(1L);

        mockMvc.perform(post("/api/counseling/reservations")
                .with(csrf())
                .with(user(studentDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"counselorId\":1,\"requestedDateTime\":\"2024-01-01T10:00:00\",\"content\":\"상담 신청\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1L));
    }

    @Test
    @DisplayName("테스트14 - 상담 예약 목록 조회")
    @WithMockUser(roles = "STUDENT")
    void getMyReservations() throws Exception {
        Page<CounselingReservationDto.ListResponse> page = new PageImpl<>(List.of());
        given(reservationService.getMyReservations(any(), any(), any())).willReturn(page);

        mockMvc.perform(get("/api/counseling/reservations")
                .with(user(studentDetails)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트15 - 상담 예약 상세 조회")
    @WithMockUser(roles = "STUDENT")
    void getReservationDetail() throws Exception {
        given(reservationService.getReservationDetail(anyLong(), any()))
            .willReturn(new CounselingReservationDto.DetailResponse());

        mockMvc.perform(get("/api/counseling/reservations/1")
                .with(user(studentDetails)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트16 - 상담 예약 취소")
    @WithMockUser(roles = "STUDENT")
    void cancelReservation() throws Exception {
        mockMvc.perform(post("/api/counseling/reservations/1/cancel")
                .with(csrf())
                .with(user(studentDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cancelReason\":\"개인 사정\"}"))
            .andExpect(status().isOk());

        verify(reservationService).cancelReservation(eq(1L), any(), any());
    }

    @Test
    @DisplayName("테스트17 - 상담 승인")
    @WithMockUser(roles = "COUNSELOR")
    void approveReservation() throws Exception {
        mockMvc.perform(post("/api/counseling/reservations/1/approve")
                .with(csrf())
                .with(user(counselorDetails))
                .param("confirmedDateTime", "2024-01-01T10:00:00")
                .param("memo", "승인"))
            .andExpect(status().isOk());

        verify(reservationService).approveReservation(eq(1L), any(LocalDateTime.class), eq("승인"), any());
    }

    @Test
    @DisplayName("테스트18 - 상담 거부")
    @WithMockUser(roles = "COUNSELOR")
    void rejectReservation() throws Exception {
        mockMvc.perform(post("/api/counseling/reservations/1/reject")
                .with(csrf())
                .with(user(counselorDetails))
                .param("rejectReason", "일정 불가"))
            .andExpect(status().isOk());

        verify(reservationService).rejectReservation(eq(1L), eq("일정 불가"), any());
    }

    @Test
    @DisplayName("테스트19 - 배정된 상담 일정 조회")
    @WithMockUser(roles = "COUNSELOR")
    void getAssignedReservations() throws Exception {
        Page<CounselingReservationDto.ListResponse> page = new PageImpl<>(List.of());
        given(reservationService.getAssignedReservations(any(), any())).willReturn(page);

        mockMvc.perform(get("/api/counseling/reservations/assigned")
                .with(user(counselorDetails)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트20 - 상담일지 작성")
    @WithMockUser(roles = "COUNSELOR")
    void createRecord() throws Exception {
        given(recordService.createRecord(any(), any())).willReturn(1L);

        mockMvc.perform(post("/api/counseling/records")
                .with(csrf())
                .with(user(counselorDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reservationId\":1,\"content\":\"상담 내용\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1L));
    }

    @Test
    @DisplayName("테스트21 - 상담일지 수정")
    @WithMockUser(roles = "COUNSELOR")
    void updateRecord() throws Exception {
        mockMvc.perform(put("/api/counseling/records/1")
                .with(csrf())
                .with(user(counselorDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"수정된 내용\"}"))
            .andExpect(status().isOk());

        verify(recordService).updateRecord(eq(1L), any(), any());
    }

    @Test
    @DisplayName("테스트22 - 상담일지 목록 조회")
    @WithMockUser(roles = "COUNSELOR")
    void getRecordList() throws Exception {
        Page<CounselingRecordDto.ListResponse> page = new PageImpl<>(List.of());
        given(recordService.getRecordList(any(), any())).willReturn(page);

        mockMvc.perform(get("/api/counseling/records")
                .with(user(counselorDetails)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트23 - 상담일지 상세 조회")
    @WithMockUser(roles = "COUNSELOR")
    void getRecordDetail() throws Exception {
        given(recordService.getRecordDetail(anyLong(), any()))
            .willReturn(new CounselingRecordDto.DetailResponse());

        mockMvc.perform(get("/api/counseling/records/1")
                .with(user(counselorDetails)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트24 - 전체 상담 이력 조회")
    @WithMockUser(roles = "ADMIN")
    void getAllHistory() throws Exception {
        Page<CounselingHistoryDto.HistoryResponse> page = new PageImpl<>(List.of());
        given(historyService.getAllHistory(any(), any(), any())).willReturn(page);

        mockMvc.perform(get("/api/counseling/history")
                .with(user(adminDetails)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트25 - 상담사별 상담 이력 조회")
    @WithMockUser(roles = "COUNSELOR")
    void getCounselorHistory() throws Exception {
        Page<CounselingHistoryDto.HistoryResponse> page = new PageImpl<>(List.of());
        given(historyService.getCounselorHistory(any(), any())).willReturn(page);

        mockMvc.perform(get("/api/counseling/history/counselor")
                .with(user(counselorDetails)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트26 - 상담사 본인 담당 상담 현황")
    @WithMockUser(roles = "COUNSELOR")
    void getCounselorStatus() throws Exception {
        given(historyService.getCounselorStatus(any()))
            .willReturn(new CounselingHistoryDto.StatusResponse());

        mockMvc.perform(get("/api/counseling/history/status")
                .with(user(counselorDetails)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트27 - 상담분류 생성")
    @WithMockUser(roles = "ADMIN")
    void createCategory() throws Exception {
        given(managementService.createCategory(any())).willReturn(1L);

        mockMvc.perform(post("/api/counseling/management/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"진로상담\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1L));
    }

    @Test
    @DisplayName("테스트28 - 상담분류 수정")
    @WithMockUser(roles = "ADMIN")
    void updateCategory() throws Exception {
        mockMvc.perform(put("/api/counseling/management/categories/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"학습상담\"}"))
            .andExpect(status().isOk());

        verify(managementService).updateCategory(eq(1L), any());
    }

    @Test
    @DisplayName("테스트29 - 상담분류 삭제")
    @WithMockUser(roles = "ADMIN")
    void deleteCategory() throws Exception {
        mockMvc.perform(delete("/api/counseling/management/categories/1")
                .with(csrf()))
            .andExpect(status().isOk());

        verify(managementService).deleteCategory(1L);
    }

    @Test
    @DisplayName("테스트30 - 상담분류 목록 조회")
    @WithMockUser(roles = "ADMIN")
    void getAllCategories() throws Exception {
        given(managementService.getAllCategories()).willReturn(List.of());

        mockMvc.perform(get("/api/counseling/management/categories"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트31 - 상담원 생성")
    @WithMockUser(roles = "ADMIN")
    void createCounselor() throws Exception {
        mockMvc.perform(post("/api/counseling/management/counselors")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1,\"fieldIds\":[1,2]}"))
            .andExpect(status().isOk());

        verify(managementService).createCounselor(any());
    }

    @Test
    @DisplayName("테스트32 - 상담원 수정")
    @WithMockUser(roles = "ADMIN")
    void updateCounselor() throws Exception {
        mockMvc.perform(put("/api/counseling/management/counselors/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fieldIds\":[1,2,3]}"))
            .andExpect(status().isOk());

        verify(managementService).updateCounselor(eq(1L), any());
    }

    @Test
    @DisplayName("테스트33 - 상담원 삭제")
    @WithMockUser(roles = "ADMIN")
    void deleteCounselor() throws Exception {
        mockMvc.perform(delete("/api/counseling/management/counselors/1")
                .with(csrf()))
            .andExpect(status().isOk());

        verify(managementService).deleteCounselor(1L);
    }

    @Test
    @DisplayName("테스트34 - 상담원 목록 조회")
    @WithMockUser(roles = "ADMIN")
    void getAllCounselors() throws Exception {
        Page<CounselingManagementDto.CounselorResponse> page = new PageImpl<>(List.of());
        given(managementService.getAllCounselors(any())).willReturn(page);

        mockMvc.perform(get("/api/counseling/management/counselors"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트35 - 만족도 문항 생성")
    @WithMockUser(roles = "ADMIN")
    void createQuestion() throws Exception {
        given(managementService.createQuestion(any())).willReturn(1L);

        mockMvc.perform(post("/api/counseling/management/questions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"상담에 만족하셨나요?\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1L));
    }

    @Test
    @DisplayName("테스트36 - 만족도 문항 수정")
    @WithMockUser(roles = "ADMIN")
    void updateQuestion() throws Exception {
        mockMvc.perform(put("/api/counseling/management/questions/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"상담이 도움이 되었나요?\"}"))
            .andExpect(status().isOk());

        verify(managementService).updateQuestion(eq(1L), any());
    }

    @Test
    @DisplayName("테스트37 - 만족도 문항 삭제")
    @WithMockUser(roles = "ADMIN")
    void deleteQuestion() throws Exception {
        mockMvc.perform(delete("/api/counseling/management/questions/1")
                .with(csrf()))
            .andExpect(status().isOk());

        verify(managementService).deleteQuestion(1L);
    }

    @Test
    @DisplayName("테스트38 - 만족도 문항 목록 조회")
    @WithMockUser(roles = "ADMIN")
    void getAllQuestions() throws Exception {
        given(managementService.getAllQuestions()).willReturn(List.of());

        mockMvc.perform(get("/api/counseling/management/questions"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트39 - 상담만족도 제출")
    @WithMockUser(roles = "STUDENT")
    void submitSatisfaction() throws Exception {
        given(satisfactionService.submitSatisfaction(any(), any())).willReturn(1L);

        mockMvc.perform(post("/api/counseling/satisfaction")
                .with(csrf())
                .with(user(studentDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reservationId\":1,\"answers\":[]}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1L));
    }

    @Test
    @DisplayName("테스트40 - 만족도 설문 조회")
    @WithMockUser(roles = "STUDENT")
    void getSurvey() throws Exception {
        given(satisfactionService.getSurvey(anyLong(), any()))
            .willReturn(new CounselingSatisfactionDto.SurveyResponse());

        mockMvc.perform(get("/api/counseling/satisfaction/survey/1")
                .with(user(studentDetails)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트41 - 상담 유형별 통계")
    @WithMockUser(roles = "ADMIN")
    void getTypeStatistics() throws Exception {
        given(statisticsService.getTypeStatistics())
            .willReturn(new CounselingStatisticsDto.TypeStatistics());

        mockMvc.perform(get("/api/counseling/statistics/type"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트42 - 전체 상담 현황/이력 통계")
    @WithMockUser(roles = "ADMIN")
    void getOverallStatistics() throws Exception {
        given(statisticsService.getOverallStatistics())
            .willReturn(new CounselingStatisticsDto.OverallStatistics());

        mockMvc.perform(get("/api/counseling/statistics/overall"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트43 - 상담만족도 결과 조회")
    @WithMockUser(roles = "ADMIN")
    void getSatisfactionResults() throws Exception {
        given(statisticsService.getSatisfactionResults()).willReturn(List.of());

        mockMvc.perform(get("/api/counseling/statistics/satisfaction"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("테스트44 - 상담원별 현황")
    @WithMockUser(roles = "ADMIN")
    void getCounselorStatistics() throws Exception {
        given(statisticsService.getCounselorStatistics()).willReturn(List.of());

        mockMvc.perform(get("/api/counseling/statistics/counselor"))
            .andExpect(status().isOk());
    }
}
