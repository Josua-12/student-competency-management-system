package com.competency.SCMS.domain.counseling;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;

@Entity
@Table(name = "counseling_override_schedules")
public class CounselingOverrideSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User counselor;

    @Column(nullable = false)
    private LocalDate startDate;   // 휴가 시작일 (예외 적용일)  하루짜리 휴가는 starDate ==endDate
    private LocalDate endDate;     // 휴가 종료일 (예외 종료일)

    private Boolean slot0910 = null;
    private Boolean slot1011 = null;
    private Boolean slot1112 = null;
    private Boolean slot1213 = null;
    private Boolean slot1314 = null;
    private Boolean slot1415 = null;
    private Boolean slot1516 = null;
    private Boolean slot1617 = null;
    private Boolean slot1718 = null;

    @Enumerated(EnumType.STRING)
    private CounselingOffReason reason; // 병가, 반차, 출산휴가 등

}