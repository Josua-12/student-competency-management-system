package com.competency.scms.dto.noncurricular.operation.completion;

import com.competency.scms.domain.noncurricular.operation.ApplicationStatus;
import com.competency.scms.domain.noncurricular.program.CompletionStatus;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.operation.ProgramApplication;
import com.competency.scms.domain.noncurricular.program.ProgramStatus;
import com.competency.scms.domain.noncurricular.linkCompetency.LinkCompetency;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCompletionListItemDto {

    /** 이수내역 ID - 여기서는 신청 ID를 그대로 사용 */
    private Long completionId;

    private Long programId;
    private String programTitle;
    private String programTermLabel;
    private String departmentName;
    private String parentDepartmentName;

    private String categoryLabel;

    private LocalDate activityStartDate;
    private LocalDate activityEndDate;
    private LocalDate completionDate;   // 화면의 "이수일"

    private CompletionStatus completionStatus;
    private Integer point;              // 프로그램 마일리지(기본값)
    private boolean pointPlanned;


    /** 핵심역량 이름 리스트 (문자열 join해서 보여주기 용도) */
    private List<String> competencyNames;

    /** 만족도 상태 텍스트 (예: "미제출", "제출완료" 등) */
    private String satisfactionStatusText;
    private boolean certificateAvailable;

    // ==========================
    //   팩토리 메서드
    // ==========================

    public static StudentCompletionListItemDto from(ProgramApplication app) {
        Program p = app.getProgram();

        LocalDate start = p.getProgramStartAt() != null
                ? p.getProgramStartAt().toLocalDate()
                : null;
        LocalDate end = p.getProgramEndAt() != null
                ? p.getProgramEndAt().toLocalDate()
                : null;

        CompletionStatus completionStatus = toCompletionStatus(app);

        // Program 엔티티에 category / department / mileage 등이 이미 정의되어 있음
        String deptName = p.getDepartment() != null ? p.getDepartment().getName() : null;
        String parentDeptName = (p.getDepartment() != null && p.getDepartment().getParent() != null)
                ? p.getDepartment().getParent().getName()
                : null;

        // ProgramCategoryType 에 label 이 있으면 label 사용, 없으면 name()
        String categoryLabel = p.getCategory() != null
                ? p.getCategory().getLabel()
                : null;

        // 핵심역량명 추출 (LinkCompetency → Competency.name)
        List<String> competencyNames = p.getLinkCompetencies() == null ? List.of() :
                p.getLinkCompetencies().stream()
                        .map(LinkCompetency::getCompetency)
                        .filter(c -> c != null)
                        .map(c -> c.getName())
                        .collect(Collectors.toList());

        // 이수일은 일단 프로그램 종료일 기준으로 설정 (추후 Attendance/Report 기준으로 교체 가능)
        LocalDate completionDate = end;

        return StudentCompletionListItemDto.builder()
                .completionId(app.getApplicationId())
                .programId(p.getProgramId())
                .programTitle(p.getTitle())
                .departmentName(deptName)
                .parentDepartmentName(parentDeptName)
                .categoryLabel(categoryLabel)
                .activityStartDate(start)
                .activityEndDate(end)
                .completionDate(completionDate)
                .completionStatus(completionStatus)
                .point(p.getMileage())
                .competencyNames(competencyNames)
                .satisfactionStatusText("미제출") // TODO: program_satisfaction 연동 시 갱신
                .build();
    }

    private static CompletionStatus toCompletionStatus(ProgramApplication app) {
        ProgramStatus programStatus = app.getProgram().getStatus();
        ApplicationStatus appStatus = app.getStatus();

        // 매우 단순한 룰: 추후 출석/보고서/이수판정 로직에 맞춰 수정
        if (appStatus == ApplicationStatus.APPROVED
                && (programStatus == ProgramStatus.CLOSED || programStatus == ProgramStatus.CANCELED)) {
            return CompletionStatus.COMPLETED;
        }

        if (appStatus == ApplicationStatus.REJECTED || appStatus == ApplicationStatus.CANCELED) {
            return CompletionStatus.NOT_COMPLETED;
        }

        return CompletionStatus.IN_PROGRESS;
    }
}
