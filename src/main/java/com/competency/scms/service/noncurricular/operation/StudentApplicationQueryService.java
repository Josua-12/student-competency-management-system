package com.competency.scms.service.noncurricular.operation;

import com.competency.scms.domain.noncurricular.operation.ApplicationStatus;
import com.competency.scms.domain.noncurricular.operation.AttendanceStatus;
import com.competency.scms.domain.noncurricular.operation.ProgramApplication;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.program.ProgramCategoryType;
import com.competency.scms.dto.noncurricular.mypage.*;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationListItemDto;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationListResultDto;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationSearchConditionDto;
import com.competency.scms.dto.noncurricular.operation.application.StudentApplicationSummaryDto;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import com.competency.scms.repository.noncurricular.operation.ProgramAttendanceRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentApplicationQueryService {

    private final ProgramApplicationRepository applicationRepository;
    private final ProgramAttendanceRepository attendanceRepository;

    public StudentApplicationListResultDto getStudentApplications(
            Long studentId,
            StudentApplicationSearchConditionDto search,
            Pageable pageable
    ) {
        // 날짜 변환 (LocalDate → LocalDateTime)
        LocalDateTime from = null;
        LocalDateTime to = null;
        if (search.getFromDate() != null) {
            from = search.getFromDate().atStartOfDay();
        }
        if (search.getToDate() != null) {
            // 끝 날짜의 다음날 0시 직전까지 포함
            to = search.getToDate().plusDays(1).atStartOfDay();
        }

        String keyword = normalize(search.getKeyword());
        ProgramCategoryType category = search.getCategory();
        ApplicationStatus status = search.getStatus();

        // 1) 신청내역 페이지 조회
        Page<ProgramApplication> page = applicationRepository.searchStudentApplications(
                studentId,
                from,
                to,
                keyword,
                category,
                status,
                pageable
        );

        List<ProgramApplication> apps = page.getContent();

        // 2) 이수 여부 계산: 출석 PRESENT 인 신청 ID들 조회
        List<Long> applicationIds = apps.stream()
                .map(ProgramApplication::getApplicationId)
                .toList();

        Set<Long> completedAppIds = applicationIds.isEmpty()
                ? Collections.emptySet()
                : new HashSet<>(
                attendanceRepository.findApplicationIdsByStatus(
                        applicationIds, AttendanceStatus.PRESENT
                )
        );

        // 3) DTO 매핑
        List<StudentApplicationListItemDto> dtoList = apps.stream()
                .map(app -> toListItemDto(app, completedAppIds.contains(app.getApplicationId())))
                .collect(Collectors.toList());

        // 4) completion 필터(COMPLETED / NOT_COMPLETED) 적용
        String completion = normalize(search.getCompletion());
        if ("COMPLETED".equalsIgnoreCase(completion)) {
            dtoList = dtoList.stream()
                    .filter(StudentApplicationListItemDto::isCertificateAvailable)
                    .toList();
        } else if ("NOT_COMPLETED".equalsIgnoreCase(completion)) {
            dtoList = dtoList.stream()
                    .filter(dto -> !dto.isCertificateAvailable())
                    .toList();
        }

        Page<StudentApplicationListItemDto> dtoPage =
                new PageImpl<>(dtoList, pageable, page.getTotalElements());

        // 5) 요약 영역(상단 카드) 계산
        StudentApplicationSummaryDto summary = buildSummary(studentId, from, to, keyword, category);

        return new StudentApplicationListResultDto(summary, dtoPage);
    }

    private StudentApplicationSummaryDto buildSummary(
            Long studentId,
            LocalDateTime from,
            LocalDateTime to,
            String keyword,
            ProgramCategoryType category
    ) {
        long total    = applicationRepository.countStudentApplications(studentId, from, to, keyword, category, null);
        long pending  = applicationRepository.countStudentApplications(studentId, from, to, keyword, category, ApplicationStatus.PENDING);
        long approved = applicationRepository.countStudentApplications(studentId, from, to, keyword, category, ApplicationStatus.APPROVED);
        long rejected = applicationRepository.countStudentApplications(studentId, from, to, keyword, category, ApplicationStatus.REJECTED);
        long canceled = applicationRepository.countStudentApplications(studentId, from, to, keyword, category, ApplicationStatus.CANCELED);

        return new StudentApplicationSummaryDto(total, approved, pending, rejected, canceled);
    }

    private StudentApplicationListItemDto toListItemDto(ProgramApplication app, boolean completed) {
        Program program = app.getProgram();

        LocalDateTime start = program.getProgramStartAt();
        LocalDateTime end   = program.getProgramEndAt();

        String period = null;
        if (start != null && end != null) {
            period = String.format("%s ~ %s",
                    start.toLocalDate(),
                    end.toLocalDate());
        }

        String activitySummary = period; // 추후에 회차(schedule) 기준으로 바꾸고 싶으면 여기 수정

        boolean cancelAvailable = isCancelAvailable(app, start);
        boolean surveyAvailable = isSurveyAvailable(app, completed);
        boolean certificateAvailable = completed;

        ProgramCategoryType categoryType = program.getCategory();
        String categoryName = categoryType != null ? categoryType.getLabel() : null;

        return StudentApplicationListItemDto.builder()
                .applicationId(app.getApplicationId())
                .programId(program.getProgramId())
                .programTitle(program.getTitle())
                .categoryName(categoryName)
                .programPeriod(period)
                .appliedAt(app.getAppliedAt())
                .status(app.getStatus())
                .statusLabel(toStatusLabel(app.getStatus()))
                .mileage(program.getMileage())
                .completionLabel(completed ? "이수" : "미이수")
                .activitySummary(activitySummary)
                .cancelAvailable(cancelAvailable)
                .surveyAvailable(surveyAvailable)
                .certificateAvailable(certificateAvailable)
                .programStartDate(start != null ? start.toLocalDate() : null)
                .build();
    }

    private boolean isCancelAvailable(ProgramApplication app, LocalDateTime programStartAt) {
        if (app.getStatus() != ApplicationStatus.PENDING
                && app.getStatus() != ApplicationStatus.APPROVED) {
            return false;
        }
        if (programStartAt == null) {
            return false;
        }
        // 운영 시작 전까지만 취소 가능
        return LocalDateTime.now().isBefore(programStartAt);
    }

    private boolean isSurveyAvailable(ProgramApplication app, boolean completed) {
        // 예시 정책: 이수 완료된 건에 대해서만 만족도 조사를 허용한다고 가정
        // 실제로는 설문 제출 여부 필드 있으면 같이 체크
        return completed;
    }

    private String toStatusLabel(ApplicationStatus status) {
        if (status == null) return "";
        return switch (status) {
            case PENDING -> "대기";
            case APPROVED -> "승인";
            case REJECTED -> "반려";
            case CANCELED -> "취소";
            case WAITLISTED -> "대기열";
        };
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

