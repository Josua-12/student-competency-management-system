package com.competency.scms.service.noncurricular.operation;

import com.competency.scms.domain.noncurricular.mileage.MileageFilterType;
import com.competency.scms.domain.noncurricular.operation.ProgramApplication;
import com.competency.scms.domain.noncurricular.operation.SatisfactionStatus;
import com.competency.scms.domain.noncurricular.program.CompletionStatus;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.dto.noncurricular.operation.completion.StudentCompletionListItemDto;
import com.competency.scms.dto.noncurricular.operation.completion.StudentCompletionSearchConditionDto;
import com.competency.scms.dto.noncurricular.operation.completion.StudentCompletionSummaryDto;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentCompletionQueryServiceImpl implements StudentCompletionQueryService {

    private final ProgramApplicationRepository programApplicationRepository;

    @Override
    public Page<StudentCompletionListItemDto> getStudentCompletionPage(
            Long studentId,
            StudentCompletionSearchConditionDto condition,
            Pageable pageable) {

        Specification<ProgramApplication> spec = buildSpec(studentId, condition);

        Page<ProgramApplication> page = programApplicationRepository.findAll(spec, pageable);

        return page.map(this::toListItemDto);
    }

    @Override
    public StudentCompletionSummaryDto getStudentCompletionSummary(
            Long studentId,
            StudentCompletionSearchConditionDto condition) {

        int year = Year.now().getValue();

        long totalCompleted = programApplicationRepository.countAllCompletedByStudent(studentId);
        long yearCompleted = programApplicationRepository.countCompletedByStudentAndYear(studentId, year);

        int totalPoint = programApplicationRepository.sumCompletedPointByStudent(studentId);
        int yearPoint = programApplicationRepository.sumCompletedPointByStudentAndYear(studentId, year);

        return StudentCompletionSummaryDto.builder()
                .totalCompletedCount(totalCompleted)
                .thisYearCompletedCount(yearCompleted)
                .totalPoint(totalPoint)
                .thisYearPoint(yearPoint)
                .year(year + "년 기준")
                .build();
    }

    /**
     * 검색조건을 JPA Specification으로 조립
     */
    private Specification<ProgramApplication> buildSpec(Long studentId,
                                                        StudentCompletionSearchConditionDto c) {

        return (root, query, cb) -> {
            query.distinct(true);

            Predicate predicate = cb.equal(root.get("student").get("id"), studentId);

            // 프로그램 조인
            Join<Object, Object> program = (Join<Object, Object>) root.join("program", JoinType.INNER);
            Join<Object, Object> department = (Join<Object, Object>) program.join("department", JoinType.LEFT);

            // 이수기간 (completionDate 기준)
            if (c.getFromDate() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("completionDate"), c.getFromDate()));
            }
            if (c.getToDate() != null) {
                // inclusive
                LocalDate to = c.getToDate().plusDays(1);
                predicate = cb.and(predicate,
                        cb.lessThan(root.get("completionDate"), to));
            }

            // 프로그램명
            if (c.getProgramName() != null && !c.getProgramName().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(program.get("title"), "%" + c.getProgramName().trim() + "%"));
            }

            // 운영부서
            if (c.getDepartmentId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(department.get("id"), c.getDepartmentId()));
            }

            // 이수상태
            if (c.getCompletionStatus() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("completionStatus"), c.getCompletionStatus()));
            }

            // 포인트 구분
            if (c.getPointType() != null && c.getPointType() != MileageFilterType.ALL) {
                if (c.getPointType() == MileageFilterType.HAS_POINT) {
                    predicate = cb.and(predicate,
                            cb.greaterThan(root.get("earnedPoint"), 0));
                } else if (c.getPointType() == MileageFilterType.NO_POINT) {
                    predicate = cb.and(predicate,
                            cb.equal(root.get("earnedPoint"), 0));
                }
            }

            // 핵심역량 (LinkCompetency 등으로 many-to-many 조인 가정)
            if (c.getCompetencyId() != null) {
                Join<Object, Object> linkCompetency = (Join<Object, Object>) program.join("linkCompetencies", JoinType.LEFT);
                predicate = cb.and(predicate,
                        cb.equal(linkCompetency.get("competency").get("id"), c.getCompetencyId()));
            }

            return predicate;
        };
    }

    /**
     * 엔티티 -> 리스트용 DTO 변환
     * (실제 필드명/관계는 프로젝트 엔티티에 맞게 수정)
     */
    private StudentCompletionListItemDto toListItemDto(ProgramApplication pa) {

        Program program = pa.getProgram();

        // ===== 부서 =====
        String deptName = program.getDepartment() != null
                ? program.getDepartment().getName()
                : null;

        String parentDeptName = (program.getDepartment() != null
                && program.getDepartment().getParent() != null)
                ? program.getDepartment().getParent().getName()
                : null;

        // ===== 분류/유형 =====
        String categoryName = program.getCategory() != null
                ? program.getCategory().getLabel()
                : null;

        String programTypeName = program.getRunType() != null
                ? program.getRunType().getLabel()
                : null;

        // ===== 역량명 리스트 =====
        List<String> competencyNames = program.getLinkCompetencies().stream()
                .map(lc -> lc.getCompetency().getName())
                .toList();

        // ===== 학년도/학기 라벨 (programTermLabel) =====
        String programTermLabel = null;
        if (program.getProgramStartAt() != null) {
            int year = program.getProgramStartAt().getYear();
            int month = program.getProgramStartAt().getMonthValue();
            String semester = (month <= 6) ? "1학기" : "2학기";
            programTermLabel = year + "학년도 " + semester + " 비교과";
        }

        // ===== 만족도 상태 (이 필드는 ProgramApplication/Program에 맞게 필드 추가 필요) =====
        SatisfactionStatus satisfactionStatus;
        if (!program.isSurveyRequired()) {              // surveyRequired 사용 예시
            satisfactionStatus = SatisfactionStatus.NOT_TARGET;
        } else if (pa.isSatisfactionSubmitted()) {      // ProgramApplication에 boolean 필드 추가 필요
            satisfactionStatus = SatisfactionStatus.SUBMITTED;
        } else {
            satisfactionStatus = SatisfactionStatus.NOT_SUBMITTED;
        }

        boolean certificateAvailable = pa.getCompletionStatus() == CompletionStatus.COMPLETED;

        boolean pointPlanned = pa.getCompletionStatus() != CompletionStatus.COMPLETED
                && pa.getEarnedPoint() > 0;

        return StudentCompletionListItemDto.builder()
                .completionId(pa.getApplicationId())
                .programId(program.getProgramId())

                .programTitle(program.getTitle())
                .programTermLabel(programTermLabel)

                .departmentName(deptName)
                .parentDepartmentName(parentDeptName)
                .categoryLabel(categoryName)
                .programTermLabel(programTypeName)
                .competencyNames(competencyNames)

                .activityStartDate(program.getProgramStartAt() != null
                        ? program.getProgramStartAt().toLocalDate()
                        : null)
                .activityEndDate(program.getProgramEndAt() != null
                        ? program.getProgramEndAt().toLocalDate()
                        : null)
                .completionDate(pa.getCompletionDate() != null
                        ? pa.getCompletionDate().toLocalDate()
                        : null)


                .completionStatus(pa.getCompletionStatus())
                .point(pa.getEarnedPoint())
                .pointPlanned(pointPlanned)
                .satisfactionStatusText(satisfactionStatus.getLabel())
                .certificateAvailable(certificateAvailable)
                .build();
    }
}

