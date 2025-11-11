package com.competency.scms.domain.noncurricular.program;

import com.competency.scms.domain.BaseEntity;
import com.competency.scms.domain.noncurricular.operation.ProgramAttendance;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "program_schedules",
        uniqueConstraints = @UniqueConstraint(
                name="uq_program_schedules", columnNames = {"prog_id","session_no"}
        ),
        indexes = {
                @Index(name = "ix_schedules_prog", columnList = "prog_id"),
                // ▼ DB 컬럼명이 start_at/end_at 이라면 아래처럼 맞춰줘
                // @Index(name = "ix_schedules_datetime", columnList = "start_at,end_at")
                // 그리고 필드에도 @Column(name="start_at") / @Column(name="end_at") 적용
                // 만약 DB 컬럼명을 start_time/end_time 으로 잡을거면 아래처럼 바꿔
                @Index(name = "ix_schedules_datetime", columnList = "start_time,end_time")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgramSchedule extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schd_id")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    @Column(name = "session_no", nullable = false)
    private Integer sessionNo;

    /** 일자 */
    @Column(name = "date") // DB 컬럼명이 다르면 맞춰줘
    private LocalDate date;

    /** 시작/종료 시간 */
    @Column(name = "start_time") // DB가 start_at이면 name="start_at"
    private LocalTime startTime;
    @Column(name = "end_time")   // DB가 end_at이면 name="end_at"
    private LocalTime endTime;

    @Column(name = "place_text", length = 200)
    private String placeText;

    @Column(name = "capacity_ovr")
    private Integer capacityOverride;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type", length = 20)
    private AttendanceType attendanceType;

    @Column(length = 255)
    private String remarks;

    @Builder.Default
    @OneToMany(mappedBy="schedule", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ProgramAttendance> programAttendanceList = new ArrayList<>();

    /** 화면 표시용 회차명: "1회차(2025-11-10 10:00)" */
    @Transient
    public String getName() {
        String no = (sessionNo != null) ? sessionNo + "회차" : "회차";
        String d  = (date != null) ? date.toString() : "-";
        String t  = (startTime != null) ? startTime.toString() : null;
        return t == null ? String.format("%s (%s)", no, d)
                : String.format("%s (%s %s)", no, d, t);
    }

    /** 화면 표시용 기간: "2025-11-10 10:00 ~ 12:00" */
    @Transient
    public String getPeriodText() {
        String d  = (date != null) ? date.toString() : "-";
        String s  = (startTime != null) ? startTime.toString() : "";
        String e  = (endTime != null) ? endTime.toString() : "";
        if (s.isEmpty() && e.isEmpty()) return d;
        if (!s.isEmpty() && e.isEmpty()) return d + " " + s;
        if (s.isEmpty()) return d + " ~ " + e;
        return d + " " + s + " ~ " + e;
    }
}


//
//@Entity
//@Table(name = "program_schedules",
//        uniqueConstraints = @UniqueConstraint(name="uq_program_schedules", columnNames = {"prog_id","session_no"}),
//        indexes = {
//                @Index(name = "ix_schedules_prog",     columnList = "prog_id"),
//                @Index(name = "ix_schedules_datetime", columnList = "start_at,end_at")
//        }
//)
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//public class ProgramSchedule extends BaseEntity {
//
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "schd_id")
//    private Long scheduleId;
//
//    /** 소속 프로그램 */
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "prog_id", nullable = false)
//    private Program program;
//
//    /** 회차 번호 (1, 2, 3...) */
//    @Column(name = "session_no", nullable = false)
//    private Integer sessionNo;
//
//    /** 일자 */
//    private LocalDate date;
//
//    /** 시작/종료 시간 */
//    private LocalTime startTime;
//    private LocalTime endTime;
//
//    @Column(name = "place_text", length = 200)
//    private String placeText;
//
//    @Column(name = "capacity_ovr")
//    private Integer capacityOverride;
//
//    @Enumerated(EnumType.STRING)
//    @Column(length = 20)
//    private AttendanceType attendanceType;
//
//
//    /** 세부내용 */
//    @Column(length = 255)
//    private String remarks;
//
//    @OneToMany(mappedBy="schedule", cascade=CascadeType.ALL, orphanRemoval=true)
//    private List<ProgramAttendance> programAttendanceList = new ArrayList<>();
//
//    public String getName() {
//        String start = (date != null) ? date.toLocalDate().toString() : "";
//        return String.format("회차 (%s)", start);
//    }
//
//}

