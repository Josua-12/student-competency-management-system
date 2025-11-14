package com.competency.scms.dto.noncurricular.program.student;

import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.program.ProgramCategoryType;
import com.competency.scms.domain.noncurricular.program.ProgramStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class ProgramStudentListResponse {

    private Long id;
    private String title;
    private String deptName;
    private ProgramCategoryType category;

    private LocalDate recruitStart;
    private LocalDate recruitEnd;

    private LocalDate programStart;
    private LocalDate programEnd;

    private int applied;      // 신청 인원
    private int capacity;     // 정원

    private ProgramStatus status;

    private String thumbnailUrl;

    public static ProgramStudentListResponse from(Program program, int appliedCount) {
        return ProgramStudentListResponse.builder()
                .id(program.getProgramId())
                .title(program.getTitle())
                .deptName(program.getDepartment().getName()) // Department 엔티티에 맞게 수정
                .category(program.getCategory())
                .recruitStart(program.getRecruitStartAt().toLocalDate())
                .recruitEnd(program.getRecruitEndAt().toLocalDate())
                .programStart(program.getProgramStartAt().toLocalDate())
                .programEnd(program.getProgramEndAt().toLocalDate())
                .applied(appliedCount)
                .capacity(program.getCapacity())             // Program에 정원 필드 이름 맞춰서 수정
                .status(program.getStatus())
                .thumbnailUrl(program.getThumbnailUrl())     // Program에 썸네일 경로 필드 하나 두는 걸 추천
                .build();
    }
}

