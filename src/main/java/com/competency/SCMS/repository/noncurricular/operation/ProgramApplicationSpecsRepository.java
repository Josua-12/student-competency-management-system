package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.ApplicationStatus;
import com.competency.SCMS.domain.noncurricular.operation.ProgramApplication;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;


public class ProgramApplicationSpecsRepository {

    private ProgramApplicationSpecsRepository(){}

    public static Specification<ProgramApplication> filter(Long programId, String status, Long scheduleId, String keyword) {
        return (root, query, cb) -> {
            Predicate p = cb.equal(root.get("program").get("id"), programId);

            if (status != null && !status.isBlank()) {
                p = cb.and(p, cb.equal(root.get("status"), ApplicationStatus.valueOf(status)));
            }
            if (scheduleId != null) {
                p = cb.and(p, cb.equal(root.get("schedule").get("id"), scheduleId));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim() + "%";
                Predicate byName = cb.like(root.get("applicantName"), like);
                Predicate byStdNo = cb.like(root.get("studentNo"), like);
                Predicate byDept = cb.like(root.get("departmentName"), like);
                Predicate byPhone = cb.like(root.get("phone"), like);
                p = cb.and(p, cb.or(byName, byStdNo, byDept, byPhone));
            }
            return p;
        };
    }
}
