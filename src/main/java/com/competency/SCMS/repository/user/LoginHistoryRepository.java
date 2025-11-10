//package com.competency.SCMS.repository.user;
//
//import com.competency.SCMS.domain.user.LoginHistory;
//import com.competency.SCMS.dto.user.MonthlyLoginStatDto;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Repository
//public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Integer> {
//    // 월별 접속자 통계
////    @Query("SELECT MONTH(l.login_at) AS month, COUNT(DISTINCT l.user_id) AS visitorCount FROM LoginHistory l GROUP BY MONTH(l.login_at)")
////    List<Object[]> getMonthlyUserStats();
//
//    @Query("""
//select MONTH(l.loginAt) as month,
//       count(distinct l.user.id) as visitorCount
//from LoginHistory l
//group by MONTH(l.loginAt)
//order by MONTH(l.loginAt)
//""")
//    List<MonthlyUserStats> getMonthlyUserStats();
////    long countByLoginAtAfter(LocalDateTime localDateTime);
//    // 오늘 날짜 이후(=오늘부터 지금까지) 로그인 기록 개수
//    long countByLoginAtAfter(LocalDateTime dateTime);
//}
