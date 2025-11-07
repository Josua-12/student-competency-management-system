package com.competency.SCMS.service.noncurricular.program;

import com.competency.SCMS.domain.File;
import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramSchedule;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramQueryServiceImpl implements ProgramQueryService {

    private final ProgramRepository programRepository;
    private final ProgramScheduleRepository scheduleRepository;
    private final LinkCompetencyRepository competencyLinkRepository;
    private final FileRepository programFileRepository;
    private final ApprovalHistoryRepository programApprovalHistoryRepository;

    @Override
    public Page<ProgramListItemDto> search(ProgramSearchCond cond, Pageable pageable) {
        Specification<Program> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (cond.getKeyword()!=null && !cond.getKeyword().isBlank()) {
                ps.add(cb.like(cb.lower(root.get("title")),
                        "%" + cond.getKeyword().toLowerCase() + "%"));
            }
            if (cond.getDeptId()!=null) {
                ps.add(cb.equal(root.get("department").get("id"), cond.getDeptId()));
            }
            if (cond.getCategoryId()!=null) {
                ps.add(cb.equal(root.get("category").get("id"), cond.getCategoryId()));
            }
            if (cond.getStatus()!=null) {
                ps.add(cb.equal(root.get("status"), cond.getStatus()));
            }
            // Program 엔티티에 startDate/endDate가 실제로 있을 때만 사용.
            if (cond.getFrom()!=null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("startDate"), cond.getFrom()));
            }
            if (cond.getTo()!=null) {
                ps.add(cb.lessThanOrEqualTo(root.get("endDate"), cond.getTo()));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };

        Page<Program> page = programRepository.findAll(spec, pageable);

        return page.map(p -> ProgramListItemDto.builder()
                .id(p.getProgramId())
                .title(p.getTitle())
                .deptName(p.getDepartment()!=null ? p.getDepartment().getName() : "-")
                .categoryName(p.getCategory()!=null ? p.getCategory().getName() : "-")
                .status(p.getStatus()!=null ? p.getStatus().name() : "-")
                .mileage(p.getMileage())
                .periodText(formatPeriod(p))
                .thumbnailUrl(p.getThumbnailUrl())
                .build());
    }

    @Override
    public ProgramDetailDto getDetailForOperator(Long operatorId, Long programId) {
        // TODO: 접근권한 검증(작성자/소속부서 운영자 등)

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found: " + programId));

        ProgramBasicDto basic = ProgramBasicDto.builder()
                .id(program.getProgramId())
                .title(n(program.getTitle()))
                .deptName(program.getOwner()!=null && program.getOwner().getDepartment()!=null
                        ? n(program.getDepartment().getName()) : "-")
                .categoryName(program.getCategory()!=null ? n(program.getCategory().getName()) : "-")
                .status(program.getStatus()!=null ? program.getStatus().name() : "-")
                .mileage(program.getMileage())
                .periodText(formatPeriod(program))       // ✅ Program 기간 표기
                .location(n(program.getLocation()))
                .desc(n(program.getDescription()))
                .thumbnailUrl(n(program.getThumbnailUrl()))
                .build();

        List<ScheduleDto> schedules = scheduleRepository
                .findAllByProgram_IdOrderByRoundNoAsc(programId).stream()
                .map(s -> ScheduleDto.builder()
                        .roundNo(s.getSessionNo())
                        .date(s.getDate()!=null ? s.getDate().toString() : "")
                        .timeRange((s.getStartTime()!=null?s.getStartTime().toString():"")
                                + " ~ "
                                + (s.getEndTime()!=null?s.getEndTime().toString():""))
                        .content(n(s.getRemarks()))
                        .build())
                .toList();

        List<CompetencyDto> competencies = competencyLinkRepository
                .findAllByProgram_Id(programId).stream()
                .map(link -> CompetencyDto.builder()
                        .id(link.getCompetency().getId())
                        .name(n(link.getCompetency().getName()))
                        .build())
                .toList();

        List<FileDto> files = programFileRepository
                .findAllByProgram_IdOrderByCreatedAtAsc(programId).stream()
                .map(this::toFileDto)
                .toList();

        DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<ApprovalHistoryDto> approvals = programApprovalHistoryRepository
                .findAllByProgram_IdOrderByCreatedAtDesc(programId).stream()
                .map(a -> ApprovalHistoryDto.builder()
                        .requestedAt(a.getCreatedAt()!=null ? a.getCreatedAt().format(dt) : "")
                        .status(a.getStatus()!=null ? a.getStatus().name() : "-")
                        .actor(n(a.getActorName()) + (a.getActorRole()!=null ? "("+a.getActorRole()+")" : ""))
                        .comment(n(a.getComment()))
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

    private String n(String s){ return s==null? "" : s; }

    private FileDto toFileDto(File f){
        String uploaded = (f.getCreatedAt()!=null)? f.getCreatedAt().toLocalDate().toString() : "";
        return FileDto.builder()
                .id(f.getId())
                .groupId(f.getGroupId())
                .name(n(f.getOriginalName()))
                .size(f.getSize())
                .uploadedAt(uploaded)
                .url("/files/"+ f.getGroupId()+"/"+f.getId())
                .build();
    }

    // Program 전체 기간 (엔티티가 programStartAt/programEndAt을 쓰는 경우)
    private String formatPeriod(Program p){
        String s = p.getProgramStartAt()!=null ? p.getProgramStartAt().toLocalDate().toString() : "";
        String e = p.getProgramEndAt()!=null ? p.getProgramEndAt().toLocalDate().toString() : "";
        return (s.isEmpty() && e.isEmpty()) ? "-" : (s + " ~ " + e);
    }

    // 회차(일정)용
    private String formatPeriod(ProgramSchedule s) {
        String date = (s.getDate() != null) ? s.getDate().toString() : "";
        String start = (s.getStartTime() != null) ? s.getStartTime().toString() : "";
        String end = (s.getEndTime() != null) ? s.getEndTime().toString() : "";

        if (date.isEmpty() && start.isEmpty() && end.isEmpty()) return "-";
        return date + (start.isEmpty() && end.isEmpty() ? "" : " (" + start + " ~ " + end + ")");
    }
}
