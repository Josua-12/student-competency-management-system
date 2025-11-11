package com.competency.SCMS.repository.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.dto.noncurricular.program.ProgramListRowDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramSearchCondDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProgramRepositoryImpl implements ProgramRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Program> findDetailById(Long programId) {
        // 최소 수정: 단순 조회 (필요하면 fetch join JPQL로 교체)
        return Optional.ofNullable(em.find(Program.class, programId));
    }

    @Override
    public Page<ProgramListRowDto> search(ProgramSearchCondDto cond, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ===== Select 쿼리 =====
        CriteriaQuery<ProgramListRowDto> cq = cb.createQuery(ProgramListRowDto.class);
        Root<Program> p = cq.from(Program.class);
        Join<Object, Object> d = p.join("department", JoinType.LEFT); // 부서
        Join<Object, Object> owner = p.join("owner", JoinType.LEFT);  // 작성자(등록자)

        List<Predicate> preds = new ArrayList<>();

        // 키워드(제목/코드)
        if (hasText(cond.getQ())) {
            String like = "%" + cond.getQ().toLowerCase() + "%";
            preds.add(cb.or(
                    cb.like(cb.lower(p.get("title")), like),
                    cb.like(cb.lower(p.get("code")), like)
            ));
        }

        // 부서명/부서ID
        if (hasText(cond.getDept())) {
            preds.add(cb.equal(cb.lower(d.get("name")), cond.getDept().toLowerCase()));
        }
        if (cond.getDeptId() != null) {
            preds.add(cb.equal(d.get("id"), cond.getDeptId()));
        }

        // 승인상태 / 프로그램상태(둘 다 Enum)
        if (cond.getApproval() != null) {
            preds.add(cb.equal(p.get("approvalStatus"), cond.getApproval()));
        }
        if (cond.getStatus() != null) {
            preds.add(cb.equal(p.get("status"), cond.getStatus()));
        }

        // 마일리지 범위
        if (cond.getMileMin() != null) {
            preds.add(cb.greaterThanOrEqualTo(p.get("mileage"), cond.getMileMin()));
        }
        if (cond.getMileMax() != null) {
            preds.add(cb.lessThanOrEqualTo(p.get("mileage"), cond.getMileMax()));
        }

        // 모집 시작일(= recruitStartAt) 범위 - 엔티티는 LocalDateTime, 조건은 LocalDate → date()로 비교
        if (cond.getAppStartFrom() != null) {
            preds.add(cb.greaterThanOrEqualTo(
                    cb.function("date", LocalDate.class, p.get("recruitStartAt")),
                    cond.getAppStartFrom()
            ));
        }
        if (cond.getAppStartTo() != null) {
            preds.add(cb.lessThanOrEqualTo(
                    cb.function("date", LocalDate.class, p.get("recruitStartAt")),
                    cond.getAppStartTo()
            ));
        }

        // 운영 시작일(= programStartAt) 범위
        if (cond.getRunStartFrom() != null) {
            preds.add(cb.greaterThanOrEqualTo(
                    cb.function("date", LocalDate.class, p.get("programStartAt")),
                    cond.getRunStartFrom()
            ));
        }
        if (cond.getRunStartTo() != null) {
            preds.add(cb.lessThanOrEqualTo(
                    cb.function("date", LocalDate.class, p.get("programStartAt")),
                    cond.getRunStartTo()
            ));
        }

        cq.where(preds.toArray(new Predicate[0]));

        // 정렬 처리 (pageable.sort 에서 허용 컬럼만 매핑)
        List<Order> orders = new ArrayList<>();
        if (pageable.getSort().isUnsorted()) {
            // 기본 정렬: 최신 등록일 내림차순
            orders.add(cb.desc(p.get("createdAt")));
        } else {
            for (Sort.Order o : pageable.getSort()) {
                Path<?> path = resolveSortablePath(p, o.getProperty(), cb);
                if (path != null) {
                    orders.add(o.isAscending() ? cb.asc(path) : cb.desc(path));
                }
            }
        }
        cq.orderBy(orders);

        // SELECT 필드 매핑 → ProgramListRow 생성자에 정확히 맞춤
        cq.select(cb.construct(
                ProgramListRowDto.class,
                p.get("programId"), // Long id
                p.get("code"),      // String code
                p.get("title"),     // String title
                d.get("name"),      // String dept
                cb.literal(null),   // String recruit (엔티티에 직접 필드 없음 → 필요시 서비스에서 계산)
                cb.function("date", LocalDate.class, p.get("recruitStartAt")),  // LocalDate appStart
                cb.function("date", LocalDate.class, p.get("recruitEndAt")),    // LocalDate appEnd
                cb.function("date", LocalDate.class, p.get("programStartAt")),  // LocalDate runStart
                cb.function("date", LocalDate.class, p.get("programEndAt")),    // LocalDate runEnd
                p.get("maxParticipants"),       // Integer capacity (필드 명 통일 필요 시 여기만 바꾸면 됨)
                p.get("currentParticipants"),   // Integer applied
                p.get("mileage"),               // Integer mileage
                p.get("approvalStatus"),        // ApprovalStatus approval
                p.get("status"),                // ProgramStatus status
                owner.get("name"),              // String writer
                cb.function("date", LocalDate.class, p.get("createdAt")) // LocalDate created
        ));

        TypedQuery<ProgramListRowDto> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<ProgramListRowDto> content = query.getResultList();

        // ===== Count 쿼리 =====
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Program> cp = countCq.from(Program.class);
        Join<Object, Object> cd = cp.join("department", JoinType.LEFT);

        List<Predicate> countPreds = new ArrayList<>();

        if (hasText(cond.getQ())) {
            String like = "%" + cond.getQ().toLowerCase() + "%";
            countPreds.add(cb.or(
                    cb.like(cb.lower(cp.get("title")), like),
                    cb.like(cb.lower(cp.get("code")), like)
            ));
        }
        if (hasText(cond.getDept())) {
            countPreds.add(cb.equal(cb.lower(cd.get("name")), cond.getDept().toLowerCase()));
        }
        if (cond.getDeptId() != null) {
            countPreds.add(cb.equal(cd.get("id"), cond.getDeptId()));
        }
        if (cond.getApproval() != null) {
            countPreds.add(cb.equal(cp.get("approvalStatus"), cond.getApproval()));
        }
        if (cond.getStatus() != null) {
            countPreds.add(cb.equal(cp.get("status"), cond.getStatus()));
        }
        if (cond.getMileMin() != null) {
            countPreds.add(cb.greaterThanOrEqualTo(cp.get("mileage"), cond.getMileMin()));
        }
        if (cond.getMileMax() != null) {
            countPreds.add(cb.lessThanOrEqualTo(cp.get("mileage"), cond.getMileMax()));
        }
        if (cond.getAppStartFrom() != null) {
            countPreds.add(cb.greaterThanOrEqualTo(
                    cb.function("date", LocalDate.class, cp.get("recruitStartAt")),
                    cond.getAppStartFrom()
            ));
        }
        if (cond.getAppStartTo() != null) {
            countPreds.add(cb.lessThanOrEqualTo(
                    cb.function("date", LocalDate.class, cp.get("recruitStartAt")),
                    cond.getAppStartTo()
            ));
        }
        if (cond.getRunStartFrom() != null) {
            countPreds.add(cb.greaterThanOrEqualTo(
                    cb.function("date", LocalDate.class, cp.get("programStartAt")),
                    cond.getRunStartFrom()
            ));
        }
        if (cond.getRunStartTo() != null) {
            countPreds.add(cb.lessThanOrEqualTo(
                    cb.function("date", LocalDate.class, cp.get("programStartAt")),
                    cond.getRunStartTo()
            ));
        }

        countCq.select(cb.count(cp)).where(countPreds.toArray(new Predicate[0]));
        long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private boolean hasText(String s) {
        return StringUtils.hasText(s);
    }

    /**
     * 정렬 가능한 속성만 안전하게 경로로 변환
     * 허용: createdAt, recruitStartAt, programStartAt, title
     */
    private Path<?> resolveSortablePath(Root<Program> p, String property, CriteriaBuilder cb) {
        return switch (property) {
            case "createdAt" -> p.get("createdAt");
            case "recruitStartAt" -> p.get("recruitStartAt");
            case "programStartAt" -> p.get("programStartAt");
            case "title" -> p.get("title");
            default -> null; // 허용하지 않은 정렬키는 무시
        };
    }
}
