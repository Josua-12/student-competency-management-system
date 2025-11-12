package com.competency.scms.domain.noncurricular.report;

import com.competency.scms.domain.BaseEntity;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "program_reports",
        indexes = {
                @Index(name = "ix_report_program", columnList = "prog_id"),
                @Index(name = "ix_report_user", columnList = "writer_id"),
                @Index(name = "ix_report_status", columnList = "status")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgramReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    /** 대상 프로그램 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id", nullable = false)
    private Program program;

    /** 작성자 */
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="writer_id", nullable=false)
    private User writer;

    /** 보고서 유형 (학생 or 운영자) */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private ReportType type;

    /** 보고서 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReportStatus status = ReportStatus.DRAFT;

    /** 보고서 제목 */
    @Column(name = "title", length = 200)
    private String title;

    /** 보고서 본문 */
    @Lob
    @Column(name = "content")
    private String content;

    /** 첨부파일 경로 (파일 서버 경로 or S3 Key 등) */
    @Column(name = "attachment_path", length = 300)
    private String attachmentPath;

    /** 반려 사유 */
    @Column(name = "reject_reason", length = 255)
    private String rejectReason;
}

