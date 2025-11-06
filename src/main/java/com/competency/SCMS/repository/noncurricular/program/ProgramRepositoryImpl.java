package com.competency.SCMS.repository.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.dto.noncurricular.program.ProgramListRow;
import com.competency.SCMS.dto.noncurricular.program.ProgramSearchCond;
import com.competency.SCMS.dto.noncurricular.program.ProgramSort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProgramRepositoryImpl implements ProgramRepository.ProgramRepositoryCustom {

    private final EntityManager em;

    @Override
    public Page<ProgramListRow> searchForOperatorList(ProgramSearchCond cond, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // SELECT
        CriteriaQuery<ProgramListRow> cq = cb.createQuery(ProgramListRow.class);
        Root<Program> p = cq.from(Program.class);

        // WHERE
        List<Predicate> preds = new ArrayList<>();

        if (notEmpty(cond.getQ())) {
            String like = "%" + cond.getQ().toLowerCase() + "%";
            preds.add(cb.or(
                    cb.like(cb.lower(p.get("title")), like),
                    cb.like(cb.lower(p.get("code")), like)
            ));
        }
        if (notEmpty(cond.getDept()))      preds.add(cb.equal(p.get("departmentName"), cond.getDept()));
        if (notEmpty(cond.getRecruit()))   preds.add(cb.equal(p.get("recruitStatus"), cond.getRecruit()));
        if (notEmpty(cond.getApproval()))  preds.add(cb.equal(p.get("approvalStatus"), cond.getApproval()));
        if (notEmpty(cond.getStatus()))    preds.add(cb.equal(p.get("status"), cond.getStatus()));
        if (cond.getMileMin()!=null)       preds.add(cb.greaterThanOrEqualTo(p.get("mileage"), cond.getMileMin()));
        if (cond.getMileMax()!=null)       preds.add(cb.lessThanOrEqualTo(p.get("mileage"), cond.getMileMax()));
        if (cond.getAppStartFrom()!=null)  preds.add(cb.greaterThanOrEqualTo(p.get("applyStartDate"), cond.getAppStartFrom()));
        if (cond.getAppStartTo()!=null)    preds.add(cb.lessThanOrEqualTo(p.get("applyStartDate"), cond.getAppStartTo()));
        if (cond.getRunStartFrom()!=null)  preds.add(cb.greaterThanOrEqualTo(p.get("runStartDate"), cond.getRunStartFrom()));
        if (cond.getRunStartTo()!=null)    preds.add(cb.lessThanOrEqualTo(p.get("runStartDate"), cond.getRunStartTo()));

        cq.where(preds.toArray(new Predicate[0]));

        // ORDER BY
        ProgramSort sort = cond.getSort() == null ? ProgramSort.CREATED_DESC : cond.getSort();
        List<Order> orders = new ArrayList<>();
        switch (sort) {
            case CREATED_ASC -> orders.add(cb.asc(p.get("createdDate")));
            case CREATED_DESC -> orders.add(cb.desc(p.get("createdDate")));
            case APPSTART_ASC -> orders.add(cb.asc(p.get("applyStartDate")));
            case APPSTART_DESC -> orders.add(cb.desc(p.get("applyStartDate")));
            case RUNSTART_ASC -> orders.add(cb.asc(p.get("runStartDate")));
            case RUNSTART_DESC -> orders.add(cb.desc(p.get("runStartDate")));
            case TITLE_ASC -> orders.add(cb.asc(p.get("title")));
            case TITLE_DESC -> orders.add(cb.desc(p.get("title")));
        }
        cq.orderBy(orders);

        // SELECT → DTO(ProgramListRow) 매핑
        cq.select(cb.construct(
                ProgramListRow.class,
                p.get("id"),
                p.get("code"),
                p.get("title"),
                p.get("departmentName"),
                p.get("recruitStatus"),
                p.get("applyStartDate"),
                p.get("applyEndDate"),
                p.get("runStartDate"),
                p.get("runEndDate"),
                p.get("capacity"),
                p.get("appliedCount"),
                p.get("mileage"),
                p.get("approvalStatus"),
                p.get("status"),
                p.get("writerName"),
                p.get("createdDate")
        ));

        // 페이지 조회
        TypedQuery<ProgramListRow> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<ProgramListRow> content = query.getResultList();

        // COUNT
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Program> cp = countCq.from(Program.class);
        List<Predicate> countPreds = new ArrayList<>();
        if (notEmpty(cond.getQ())) {
            String like = "%" + cond.getQ().toLowerCase() + "%";
            countPreds.add(cb.or(cb.like(cb.lower(cp.get("title")), like),
                    cb.like(cb.lower(cp.get("code")), like)));
        }
        if (notEmpty(cond.getDept()))      countPreds.add(cb.equal(cp.get("departmentName"), cond.getDept()));
        if (notEmpty(cond.getRecruit()))   countPreds.add(cb.equal(cp.get("recruitStatus"), cond.getRecruit()));
        if (notEmpty(cond.getApproval()))  countPreds.add(cb.equal(cp.get("approvalStatus"), cond.getApproval()));
        if (notEmpty(cond.getStatus()))    countPreds.add(cb.equal(cp.get("status"), cond.getStatus()));
        if (cond.getMileMin()!=null)       countPreds.add(cb.greaterThanOrEqualTo(cp.get("mileage"), cond.getMileMin()));
        if (cond.getMileMax()!=null)       countPreds.add(cb.lessThanOrEqualTo(cp.get("mileage"), cond.getMileMax()));
        if (cond.getAppStartFrom()!=null)  countPreds.add(cb.greaterThanOrEqualTo(cp.get("applyStartDate"), cond.getAppStartFrom()));
        if (cond.getAppStartTo()!=null)    countPreds.add(cb.lessThanOrEqualTo(cp.get("applyStartDate"), cond.getAppStartTo()));
        if (cond.getRunStartFrom()!=null)  countPreds.add(cb.greaterThanOrEqualTo(cp.get("runStartDate"), cond.getRunStartFrom()));
        if (cond.getRunStartTo()!=null)    countPreds.add(cb.lessThanOrEqualTo(cp.get("runStartDate"), cond.getRunStartTo()));

        countCq.select(cb.count(cp)).where(countPreds.toArray(new Predicate[0]));
        long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private boolean notEmpty(String s){ return s!=null && !s.isBlank(); }
}