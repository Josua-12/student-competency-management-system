package com.competency.scms.controller.counsel;

import com.competency.scms.domain.counseling.CounselingReservation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository //테스트용 Repository
public interface TestCounselingReservationRepository extends JpaRepository<CounselingReservation, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE counseling_reservations SET created_at = :createdAt WHERE id = :id", nativeQuery = true)
    void updateCreatedAt(@Param("id") Long id, @Param("createdAt") LocalDateTime createdAt);
}

