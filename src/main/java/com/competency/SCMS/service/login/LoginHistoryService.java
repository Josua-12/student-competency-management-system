//package com.competency.SCMS.service.login;
//
//
//import com.competency.SCMS.dto.user.MonthlyLoginStatDto;
//import com.competency.SCMS.repository.user.LoginHistoryRepository;
//import com.competency.SCMS.repository.user.MonthlyUserStats;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class LoginHistoryService {
//    private final LoginHistoryRepository loginHistoryRepository;
//
//    // 월별 접속자 통계
//    public List<MonthlyLoginStatDto> getMonthlyLoginStats() {
//        List<MonthlyUserStats> rows = loginHistoryRepository.getMonthlyUserStats();
//
//        return rows.stream()
//                .map(r -> new MonthlyLoginStatDto(
//                        r.getMonth(),                     // Integer
//                        r.getVisitorCount().intValue()    // Long -> int
//                ))
//                .collect(Collectors.toList());
//    }
////        List<Object[]> stats = loginHistoryRepository.getMonthlyUserStats();
////        return stats.stream()
////                .map(row -> new MonthlyLoginStatDto(
////                        (Integer) row[0],
////                        ((Number) row[1]).intValue()
////                ))
////                .collect(Collectors.toList());
//    }
//
//    // 오늘 하루 접속자 수
//    public long getTodayLoginCount() {
//        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
//        return loginHistoryRepository.countByLoginAtAfter(startOfDay);
//    }
//}
