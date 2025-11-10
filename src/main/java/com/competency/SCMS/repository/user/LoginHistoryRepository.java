package com.competency.SCMS.repository.user;

import com.competency.SCMS.domain.user.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Integer> {
    // 월별 접속자 통계
    @Query("SELECT MONTH(l.login_at) AS month, COUNT(DISTINCT l.user_id) AS visitorCount FROM LoginHistory l GROUP BY MONTH(l.login_at)")
    List<Object[]> getMonthlyUserStats();

    long countByLoginAtAfter(LocalDateTime localDateTime);
}
