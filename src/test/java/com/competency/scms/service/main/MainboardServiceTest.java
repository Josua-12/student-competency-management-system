package com.competency.scms.service.main;

import com.competency.scms.domain.competency.Competency;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.dto.dashboard.DashboardResponseDto;
import com.competency.scms.repository.competency.CompetencyRepository;
import com.competency.scms.repository.counseling.CounselorRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import com.competency.scms.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private CounselorRepository counselorRepository;
    @Mock
    private CompetencyRepository competencyRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private User testUser;
    private Competency testCompetency;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userNum(20240001)
                .name("테스트사용자")
                .email("test@example.com")
                .role(UserRole.STUDENT)
                .build();

        testCompetency = Competency.builder()
                .name("의사소통능력")
                .build();
    }

    @Test
    void 대시보드_데이터_조회_성공() {
        // given
        String userNum = "20240001";
        when(userRepository.findByUserNum(20240001)).thenReturn(Optional.of(testUser));
        when(programRepository.count()).thenReturn(5L);
        when(counselorRepository.count()).thenReturn(3L);
        when(competencyRepository.findAll()).thenReturn(Arrays.asList(testCompetency));

        // when
        DashboardResponseDto response = dashboardService.getMainDashboardData(userNum);

        // then
        assertThat(response.getUserName()).isEqualTo("테스트사용자");
        assertThat(response.getUserEmail()).isEqualTo("test@example.com");
        assertThat(response.getMileage()).isEqualTo(0);
        assertThat(response.getProgramCount()).isEqualTo(5);
        assertThat(response.getCounselingCount()).isEqualTo(3);
        assertThat(response.getCompetencyScore()).hasSize(1);
        assertThat(response.getCompetencyScore().get(0).getCompetencyName()).isEqualTo("의사소통능력");
    }

    @Test
    void 대시보드_데이터_조회_실패_사용자없음() {
        // given
        String userNum = "99999999";
        when(userRepository.findByUserNum(99999999)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> dashboardService.getMainDashboardData(userNum))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("대시보드 데이터를 조회할 수 없습니다.");
    }

    @Test
    void 대시보드_데이터_조회_실패_잘못된사용자번호() {
        // given
        String userNum = "invalid";

        // when & then
        assertThatThrownBy(() -> dashboardService.getMainDashboardData(userNum))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("대시보드 데이터를 조회할 수 없습니다.");
    }
}
