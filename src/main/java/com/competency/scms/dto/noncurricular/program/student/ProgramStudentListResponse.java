package com.competency.scms.dto.noncurricular.program.student;

import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.program.ProgramCategoryType;
import com.competency.scms.domain.noncurricular.program.ProgramStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ProgramStudentListResponse {

    private Long id;                  // prog_id
    private String title;             // 프로그램명
    private String deptName;          // 운영부서명
    private ProgramCategoryType category;

    private LocalDate recruitStart;
    private LocalDate recruitEnd;

    private LocalDate programStart;
    private LocalDate programEnd;

    private int applied;              // 신청 인원
    private int capacity;             // 정원 (maxParticipants 기준)

    private ProgramStatus status;     // 그대로 내려보냄 (JS에서 badge 매핑)

    private String thumbnailUrl;      // 대표 이미지

    private static LocalDate toDate(LocalDateTime dt) {
        return dt == null ? null : dt.toLocalDate();
    }

    public static ProgramStudentListResponse from(Program program, int appliedCount) {
        return ProgramStudentListResponse.builder()
                .id(program.getProgramId())
                .title(program.getTitle())
                .deptName(program.getDepartment() != null ? program.getDepartment().getName() : null)
                .category(program.getCategory())
                .recruitStart(toDate(program.getRecruitStartAt()))
                .recruitEnd(toDate(program.getRecruitEndAt()))
                .programStart(toDate(program.getProgramStartAt()))
                .programEnd(toDate(program.getProgramEndAt()))
                .applied(appliedCount)
                .capacity(program.getMaxParticipants() != null
                        ? program.getMaxParticipants()
                        : (program.getCapacity() != null ? program.getCapacity() : 0))
                .status(program.getStatus())
                .thumbnailUrl(program.getThumbnailUrl())
                .build();
    }
}
