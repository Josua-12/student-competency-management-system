package com.competency.scms.domain.noncurricular.report;

import com.competency.scms.domain.BaseEntity;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.noncurricular.program.ProgramSchedule;

import jakarta.persistence.*;
import lombok.*;

/**
 * 비교과 프로그램 결과보고서(학생 / 운영자 공용)
 */
@Entity
@Table(name = "program_reports",
        indexes = {
                @Index(name = "ix_report_program",   columnList = "prog_id"),
                @Index(name = "ix_report_schedule",  columnList = "schd_id"),
                @Index(name = "ix_report_user",      columnList = "writer_id"),
                @Index(name = "ix_report_status",    columnList = "status"),
                @Index(name = "ix_report_type",      columnList = "report_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    /** 대상 프로그램 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    /** 대상 회차(필수는 아님) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schd_id")
    private ProgramSchedule schedule;

    /** 작성자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    /** 보고서 유형(학생 / 운영자) */
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", length = 20, nullable = false)
    private ReportType reportType;

    /** 상태(임시저장 / 제출 / 반려 / 승인) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReportStatus status;

    // ===== 공통 메타정보(운영자 탭 상단) =====

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "dept_name", length = 100)
    private String deptName;

    /** 운영기간 텍스트(예: 2025-05-01 ~ 2025-05-31) */
    @Column(name = "period_text", length = 100)
    private String periodText;

    @Column(name = "run_place", length = 200)
    private String runPlace;

    @Column(name = "contact", length = 100)
    private String contact;

    // ===== 통계 요약(KPI/통계 표) =====

    @Column(name = "apply_cnt")
    private Integer applyCount;

    @Column(name = "attend_cnt")
    private Integer attendCount;

    @Column(name = "complete_cnt")
    private Integer completeCount;

    @Column(name = "fail_cnt")
    private Integer failCount;

    @Column(name = "survey_cnt")
    private Integer surveyCount;

    @Column(name = "avg_rating")
    private Double averageRating;

    @Column(name = "mileage_sum")
    private Integer mileageSum;

    // ===== 운영자용 본문 (HTML의 textarea들) =====

    @Lob
    @Column(name = "overview")
    private String overview;

    @Lob
    @Column(name = "result_summary")
    private String resultSummary;

    @Lob
    @Column(name = "satisfaction_summary")
    private String satisfactionSummary;

    @Lob
    @Column(name = "competency_mapping")
    private String competencyMapping;

    @Lob
    @Column(name = "issues")
    private String issues;

    @Lob
    @Column(name = "improvement")
    private String improvement;

    // ===== 학생용 본문(학생 탭) =====

    @Lob
    @Column(name = "stu_goal")
    private String studentGoal;

    @Lob
    @Column(name = "stu_activity")
    private String studentActivity;

    @Lob
    @Column(name = "stu_reflection")
    private String studentReflection;

    @Lob
    @Column(name = "stu_plan")
    private String studentPlan;

    /** 첨부파일 경로 (간단 버전: 문자열. 필요하면 JSON/별도 테이블로 확장) */
    @Column(name = "attachment_path", length = 300)
    private String attachmentPath;

    /** 반려 사유 */
    @Column(name = "reject_reason", length = 255)
    private String rejectReason;
}

//@Entity
//@Table(name = "program_reports",
//        indexes = {
//                @Index(name = "ix_report_program", columnList = "prog_id"),
//                @Index(name = "ix_report_user", columnList = "writer_id"),
//                @Index(name = "ix_report_status", columnList = "status")
//        }
//)
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//public class ProgramReport extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "report_id")
//    private Long reportId;
//
//    /** 대상 프로그램 */
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "prog_id", nullable = false)
//    private Program program;
//
//    /** 작성자 */
//    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="writer_id", nullable=false)
//    private User writer;
//
//    /** 보고서 유형 (학생 or 운영자) */
//    @Enumerated(EnumType.STRING)
//    @Column(name = "type", length = 20, nullable = false)
//    private ReportType type;
//
//    /** 보고서 상태 */
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", length = 20, nullable = false)
//    private ReportStatus status = ReportStatus.DRAFT;
//
//    /** 보고서 제목 */
//    @Column(name = "title", length = 200)
//    private String title;
//
//    /** 보고서 본문 */
//    @Lob
//    @Column(name = "content")
//    private String content;
//
//    /** 첨부파일 경로 (파일 서버 경로 or S3 Key 등) */
//    @Column(name = "attachment_path", length = 300)
//    private String attachmentPath;
//
//    /** 반려 사유 */
//    @Column(name = "reject_reason", length = 255)
//    private String rejectReason;
//}

