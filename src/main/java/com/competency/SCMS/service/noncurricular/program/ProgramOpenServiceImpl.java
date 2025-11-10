package com.competency.SCMS.service.noncurricular.program;


import com.competency.SCMS.domain.Department;
import com.competency.SCMS.domain.File;
import com.competency.SCMS.domain.noncurricular.operation.ApprovalStatus;
import com.competency.SCMS.domain.noncurricular.program.*;
import com.competency.SCMS.dto.noncurricular.program.BasicInfoDto;
import com.competency.SCMS.dto.noncurricular.program.OperationDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramOpenRequestDto;
import com.competency.SCMS.dto.noncurricular.program.ScheduleDto;
import com.competency.SCMS.repository.DepartmentRepository;
import com.competency.SCMS.repository.FileRepository;
import com.competency.SCMS.repository.noncurricular.program.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramOpenServiceImpl implements ProgramOpenService {

    private final ProgramRepository programRepository;
    private final ProgramScheduleRepository scheduleRepository;
    private final FileRepository fileRepository;
    private final DepartmentRepository departmentRepository;
    private final ProgramCategoryRepository programCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Program load(Long programId) {
        return programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));
    }

    @Override
    public Long saveDraft(Long programId, ProgramOpenRequestDto dto,
                          MultipartFile poster,
                          List<MultipartFile> guides,
                          List<MultipartFile> attachments) {

        Program program = (programId != null)
                ? programRepository.findById(programId).orElse(new Program())
                : new Program();

        mapToEntity(program, dto, false);
        Program saved = programRepository.save(program);

        handleFiles(saved, poster, guides, attachments);
        replaceSchedules(saved, dto.getSchedules());

        return saved.getProgramId();
    }

    @Override
    public Long submitApproval(Long programId, ProgramOpenRequestDto dto,
                               MultipartFile poster,
                               List<MultipartFile> guides,
                               List<MultipartFile> attachments) {

        Long id = saveDraft(programId, dto, poster, guides, attachments);
        Program p = programRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + id));
        p.setApprovalStatus(ApprovalStatus.WAIT); // 검토중으로 전환
        return p.getProgramId();
    }

    @Override
    public void deleteDraft(Long programId) {
        programRepository.deleteById(programId);
    }

    // ========= 내부 유틸 =========

    private void mapToEntity(Program p, ProgramOpenRequestDto req, boolean submitting) {
        BasicInfoDto b = req.getBasic();
        OperationDto o = req.getOperation();

        // 1) 카테고리 enum
        p.setCategory(b.getCategory());

        // 2) 부서 문자열 -> 엔티티 변환 (부서코드 기준 예시)
        Department dept = departmentRepository
                .findByCode(b.getDepartment()) // 부서명이면 findByName(...)로 변경
                .orElseThrow(() -> new IllegalArgumentException("Invalid department: " + b.getDepartment()));
        p.setDepartment(dept);

        // 3) 단순 필드
        p.setOwnerName(b.getOwnerName());
        p.setOwnerTel(b.getOwnerTel());
        p.setOwnerEmail(b.getOwnerEmail());
        p.setSummary(b.getSummary());

        p.setCompetencyArea(b.getCompetencyArea()); // enum 패키지 맞추면 OK
        p.setRunType(b.getRunType());               // enum 패키지 맞추면 OK
        p.setLocation(b.getLocation());
        p.setCompletionCriteria(b.getCompletionCriteria());
        p.setSurveyRequired(b.isSurveyRequired());
        p.setPoints(b.getPoints() == null ? 0 : b.getPoints());

        // 4) 날짜형 변환 (LocalDate -> LocalDateTime)
        // 운영기간: [00:00:00 ~ 23:59:59]로 해석 예시
        p.setProgramStartAt(b.getRunStart().atStartOfDay());
        p.setProgramEndAt(b.getRunEnd().atTime(23, 59, 59));

        // 모집기간은 DTO가 LocalDateTime이므로 바로 대입
        p.setRecruitStartAt(b.getAppStart());
        p.setRecruitEndAt(b.getAppEnd());

        // 정원
        p.setCapacity(b.getCapacity());

        // 자격/역량매핑 리스트 (엔티티에 ElementCollection 필드가 있다면 그대로 대입)
        p.setEligibleGrades(emptyIfNull(b.getEligibleGrades()));
        p.setEligibleMajors(emptyIfNull(b.getEligibleMajors()));
        p.setCompetencyMappings(emptyIfNull(b.getCompetencyMappings()));

        // OperationDto에 있는 나머지 세부값 맵핑 (예산/재원/온라인URL 등)
        if (o != null) {
            p.setBudget(o.getBudget());
            p.setFundSrc(o.getFundSrc());
            p.setOnlineUrl(o.getOnlineUrl());
        }

        // 제출 상태라면 승인상태/초안 상태 등 전이 로직
        if (submitting) {
            p.requestApproval(); // ApprovalStatus.REQ
        }
    }

    private static <T> List<T> emptyIfNull(List<T> src) {
        return (src == null) ? List.of() : src;
    }

    private List<String> normalizeGrades(List<String> raw) {
        // "ALL" 또는 "1","2","3","4" → 내부 저장은 "ALL" 또는 "G1"... 로 통일
        List<String> out = new ArrayList<>();
        if (raw.contains("ALL")) { out.add("ALL"); return out; }
        for (String s : raw) {
            String v = s.trim();
            if (v.matches("\\d")) out.add("G" + v);
            else out.add(v); // 이미 G1 형태면 그대로
        }
        return out;
    }

    private void replaceSchedules(Program p, List<ScheduleDto> list) {
        // orphanRemoval=true 이므로 전부 비우고 다시 추가
        p.getSchedules().clear();
        if (list == null) return;
        for (ScheduleDto s : list) {
            ProgramSchedule ps = ProgramSchedule.builder()
                    .sessionNo(s.getRoundNo())
                    .date(s.getDate())
                    .startTime(s.getStartAt())
                    .endTime(s.getEndAt())
                    .placeText(s.getPlace())
                    .capacityOverride(s.getCapacity())
                    .attendanceType(s.getAttendanceType())
                    .build();
            p.addSchedule(ps);
        }
    }

    private void handleFiles(Program p,
                             MultipartFile poster,
                             List<MultipartFile> guides,
                             List<MultipartFile> attachments) {
        // 실제 저장소 연동은 프로젝트 정책에 맞춰 구현(로컬/S3 등)
        // 여기선 metadata만 예시로 추가

        if (poster != null && !poster.isEmpty()) {
            File f = File.builder()
                    .fileType(FileType.POSTER)
                    .originalName(poster.getOriginalFilename())
                    .storedPath("/uploads/" + poster.getOriginalFilename()) // TODO: 실제 경로
                    .size(poster.getSize())
                    .build();
            p.addFile(f);
        }

        if (guides != null) {
            for (MultipartFile g : guides) {
                if (g.isEmpty()) continue;
                File f = File.builder()
                        .fileType(FileType.GUIDE)
                        .originalName(g.getOriginalFilename())
                        .storedPath("/uploads/" + g.getOriginalFilename())
                        .size(g.getSize())
                        .build();
                p.addFile(f);
            }
        }

        if (attachments != null) {
            for (MultipartFile a : attachments) {
                if (a.isEmpty()) continue;
                File f = File.builder()
                        .fileType(FileType.ATTACHMENT)
                        .originalName(a.getOriginalFilename())
                        .storedPath("/uploads/" + a.getOriginalFilename())
                        .size(a.getSize())
                        .build();
                p.addFile(f);
            }
        }
    }
}

