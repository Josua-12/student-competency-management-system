package com.competency.SCMS.domain.noncurricular.program;
import com.competency.SCMS.domain.BaseEntity;
import com.competency.SCMS.domain.Department;
import com.competency.SCMS.domain.File;
import com.competency.SCMS.domain.noncurricular.linkCompetency.LinkCompetency;
import com.competency.SCMS.domain.noncurricular.operation.ApprovalStatus;
import com.competency.SCMS.domain.noncurricular.operation.ProgramApplication;
import com.competency.SCMS.domain.noncurricular.operation.ProgramApprovalHistory;
import com.competency.SCMS.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "programs",
        indexes = {
                @Index(name = "ix_programs_category", columnList = "category"), // enum 저장 컬럼,
                @Index(name = "ix_programs_status",        columnList = "status"),
                @Index(name = "ix_programs_recruit_range", columnList = "recruit_start_at,recruit_end_at"),
                @Index(name = "ix_programs_prog_range",    columnList = "program_start_at,program_end_at"),
                @Index(name = "ix_programs_title",         columnList = "title")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_programs_code", columnNames = "prog_code")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Program extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prog_id")
    private Long programId;

    // 개설자(운영자/부서관리자 등)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)

    private User owner;

    /**
     * 운영부서
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Department department;

    @Column(nullable = false)
    private Integer maxParticipants;
    private Integer minParticipants;
    @Column(nullable = false)
    private Integer currentParticipants = 0;
    private String location;

    @Column(name = "prog_code", length = 50, nullable = false)
    private String code;

    /**
     * 프로그램명
     */
    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String summary;

    @Column(name = "description")
    private String description;

    /**
     * 카테고리
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30, nullable = false)
    private ProgramCategoryType category;

    @Column(name = "organizer_user_id", nullable = false)
    private Long organizerUserId; // 추후 User 엔티티로 교체 가능

    public Long getId() {
        return programId;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ProgramStatus status = ProgramStatus.DRAFT;

    /**
     * 신청 시작/종료일
     */
    @Column(name = "recruit_start_at")
    private LocalDateTime recruitStartAt;

    @Column(name = "recruit_end_at")
    private LocalDateTime recruitEndAt;

    /**
     * 운영 시작/종료일
     */
    @Column(name = "program_start_at")
    private LocalDateTime programStartAt;

    @Column(name = "program_end_at")
    private LocalDateTime programEndAt;

    private Integer capacity;

    @Column(name = "location_text", length = 200)
    private String locationText;

    @Column(name = "online_yn", nullable = false)
    private boolean online = false;

    @Column(nullable = false)
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    /**
     * 마일리지 점수
     */
    private Integer mileage;

    /**
     * 대표 이미지
     */
    @Column(length = 500)
    private String thumbnailUrl;

    /**
     * 작성자(운영자 ID)
     */
    private Long createdBy;

    public void requestApproval() {
        this.approvalStatus = ApprovalStatus.REQ;
    }

    // 운영/성과 연관 (핵심만 양방향)
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramApplication> programApplications = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkCompetency> linkCompetencies = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramApprovalHistory> approvalHistories = new ArrayList<>();

    public void addSchedule(ProgramSchedule s) {
        s.setProgram(this);
        this.schedules.add(s);
    }

    // =========================
    // ▼▼▼ 항목 추가 ▼▼▼
    // =========================

    /**
     * 역량영역 (ex. 핵심역량 대분류)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "competency_area", length = 30)
    private CompetencyArea competencyArea;

    /**
     * 담당자 연락 정보
     */
    @Column(name = "owner_name", length = 50)
    private String ownerName;

    @Column(name = "owner_tel", length = 30)
    private String ownerTel;

    @Column(name = "owner_email", length = 120)
    private String ownerEmail;

    /**
     * 운영방식 (온라인/오프라인/혼합 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "run_type", length = 20)
    private RunType runType;

    /**
     * 이수기준
     */
    @Column(name = "completion_criteria", length = 300)
    private String completionCriteria;

    /**
     * 만족도 설문 필수 여부
     */
    @Column(name = "survey_required", nullable = false)
    private boolean surveyRequired = false;

    /**
     * 부여 포인트 (별도 포인트 컬럼이 필요할 경우)
     */
    @Column(name = "points")
    private Integer points;

    /**
     * 온라인 접속 URL (온라인/혼합형 시)
     */
    @Column(name = "online_url", length = 500)
    private String onlineUrl;

    /**
     * 예산
     */
    @Column(name = "budget")
    private Integer budget;

    /**
     * 재원 구분
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "fund_src", length = 20)
    private FundSource fundSrc;

    /**
     * 신청자격(학년)
     */
    @ElementCollection
    @CollectionTable(name = "program_eligible_grades", joinColumns = @JoinColumn(name = "prog_id"))
    @Column(name = "grade", length = 10)
    private List<String> eligibleGrades = new ArrayList<>();

    /**
     * 신청자격(학과)
     */
    @ElementCollection
    @CollectionTable(name = "program_eligible_majors", joinColumns = @JoinColumn(name = "prog_id"))
    @Column(name = "major", length = 100)
    private List<String> eligibleMajors = new ArrayList<>();

    /**
     * 역량 매핑(코드 리스트) — LinkCompetency와 병행 가능
     */
    @ElementCollection
    @CollectionTable(name = "program_competency_mappings", joinColumns = @JoinColumn(name = "prog_id"))
    @Column(name = "competency_code", length = 50)
    private List<String> competencyMappings = new ArrayList<>();

    /**
     * 파일 추가 편의 메서드
     */
    public void addFile(File f) {
        f.setProgram(this);
        this.files.add(f);
    }
    // =========================
    // ▲▲▲ (2) 항목 추가 끝 ▲▲▲
    // =========================
}
