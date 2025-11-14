package com.competency.scms.service.login;


import com.competency.scms.dto.user.MonthlyLoginStatDto;
import com.competency.scms.repository.user.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;

    // 월별 접속자 통계
    public List<MonthlyLoginStatDto> getMonthlyLoginStats() {
        List<Object[]> stats = loginHistoryRepository.getMonthlyUserStats();
        return stats.stream()
                .map(row -> new MonthlyLoginStatDto(
                        (Integer) row[0],
                        ((Number) row[1]).intValue()
                ))
                .collect(Collectors.toList());
    }

    // 오늘 하루 접속자 수
    public long getTodayLoginCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return loginHistoryRepository.countByLoginAtAfter(startOfDay);
    }
}
