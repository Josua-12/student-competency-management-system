package com.competency.SCMS.service.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.dto.FileDto;
import com.competency.SCMS.dto.noncurricular.linkCompetency.CompetencyDto;
import com.competency.SCMS.dto.noncurricular.operation.ApprovalHistoryDto;
import com.competency.SCMS.dto.noncurricular.program.*;
import com.competency.SCMS.repository.FileRepository;
import com.competency.SCMS.repository.noncurricular.linkCompetency.LinkCompetencyRepository;
import com.competency.SCMS.repository.noncurricular.operation.ApprovalHistoryRepository;
import com.competency.SCMS.repository.noncurricular.program.ProgramRepository;
import com.competency.SCMS.repository.noncurricular.program.ProgramScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramQueryServiceImpl implements ProgramQueryService {

    private final ProgramRepository programRepository;
    private final ProgramRepository programRepository;
    private final ProgramScheduleRepository scheduleRepository;
    private final LinkCompetencyRepository competencyLinkRepository;
    private final FileRepository fileRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;

    @Override
    public Page<ProgramListRow> search(ProgramSearchCond cond, Pageable pageable) {
        return programRepository.searchForOperatorList(cond, pageable);
    }

    @Override
    public ProgramDetailDto getDetailForOperator(Long operatorId, Long programId) {
        // 권한 체크(작성자/소속 운영자 등 정책 맞춰서 교체)
        // 예: if (!programRepository.existsByIdAndCreatedBy(programId, operatorId)) throw new AccessDeniedException(...);

        Program program = programRepository.findWithDeptAndCategoryById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found: " + programId));

        ProgramBasicDto basic = ProgramBasicDto.builder()
                .id(program.getProgramId())
                .title(program.getTitle())
                .deptName(program.getOwner().getDepartment()!=null ? program.getOwner().getDepartment() : .getName() : "-")
                .categoryName(program.getCategory()!=null ? program.getCategory().getName() : "-")
                .status(program.getStatus().name())
                .mileage(program.getMileage())
                .periodText(formatPeriod(program))
                .location(program.getLocation())
                .desc(program.getDescription())
                .thumbnailUrl(program.getThumbnailUrl()) // 없으면 null
                .build();

        List<ScheduleDto> schedules = scheduleRepository.findAllByProgram_IdOrderByRoundNoAsc(programId).stream()
                .map(s -> ScheduleDto.builder()
                        .roundNo(s.getRoundNo())
                        .date(s.getDate().toString())
                        .timeRange(s.getStartTime()+" ~ "+s.getEndTime())
                        .content(s.getContent())
                        .build())
                .toList();

        List<CompetencyDto> competencies = competencyLinkRepository.findAllByProgram_Id(programId).stream()
                .map(link -> CompetencyDto.builder()
                        .id(link.getCompetency().getId())
                        .name(link.getCompetency().getName())
                        .build())
                .toList();

        DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<FileDto> files = fileRepository.findAllByProgram_IdOrderByCreatedAtAsc(programId).stream()
                .map(f -> FileDto.builder()
                        .id(f.getId())
                        .groupId(f.getGroupId())
                        .name(f.getOriginalName())
                        .size(f.getSize())
                        .uploadedAt(f.getCreatedAt()!=null ? f.getCreatedAt().toLocalDate().format(d) : "")
                        .url("/files/"+ f.getGroupId()+"/"+f.getId())
                        .build())
                .toList();

        DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<ApprovalHistoryDto> approvals = approvalHistoryRepository.findAllByProgram_IdOrderByCreatedAtDesc(programId).stream()
                .map(a -> ApprovalHistoryDto.builder()
                        .requestedAt(a.getCreatedAt()!=null ? a.getCreatedAt().format(dt) : "")
                        .status(a.getStatus().name())
                        .actor(a.getActorName()+"("+a.getActorRole()+")")
                        .comment(a.getComment())
                        .build())
                .toList();

        return ProgramDetailDto.builder()
                .program(basic)
                .schedules(schedules)
                .competencies(competencies)
                .files(files)
                .approvals(approvals)
                .build();
    }

    private String formatPeriod(Program p){
        if (p.getStartDate()==null && p.getEndDate()==null) return "-";
        String s = p.getStartDate()!=null ? p.getStartDate().toString() : "";
        String e = p.getEndDate()!=null ? p.getEndDate().toString() : "";
        return s + " ~ " + e;
    }
}