package com.competency.SCMS.service.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.ProgramSatisfaction;
import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.domain.noncurricular.program.ProgramStatus;
import com.competency.SCMS.dto.noncurricular.operation.*;
import com.competency.SCMS.repository.noncurricular.operation.SatisfactionRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SatisfactionResultServiceImpl implements SatisfactionResultService {

    private final SatisfactionRepository satisfactionRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public SatisfactionPageResponseDto search(SatisfactionSearchConditionDto cond) {
        int page = cond.getPage() != null ? cond.getPage() : 0;
        int size = cond.getSize() != null ? cond.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"));

        Specification<ProgramSatisfaction> spec = buildSpec(cond);
        Page<ProgramSatisfaction> paging = satisfactionRepository.findAll(spec, pageable);

        // 목록 DTO 매핑
        List<SatisfactionListItemDto> rows = paging.getContent().stream()
                .map(this::toListItem)
                .collect(Collectors.toList());

        // KPI/차트 집계 (동일 조건으로 JPQL 수행)
        Aggregation agg = aggregate(cond);

        SatisfactionPageResponseDto resp = SatisfactionPageResponseDto.builder()
                .content(rows)
                .totalElements(paging.getTotalElements())
                .avgRating(agg.avgRating)
                .count(agg.count)
                .responseRate(agg.responseRate)
                .posRatio(agg.posRatio)
                .negRatio(agg.negRatio)
                .chart(SatisfactionChartDto.builder()
                        .bySchedule(ChartSeriesDto.builder()
                                .labels(agg.byScheduleLabels).values(agg.byScheduleValues).build())
                        .histogram(ChartHistogramDto.builder()
                                .labels(Arrays.asList(1,2,3,4,5))
                                .values(agg.histogramValues).build())
                        .build())
                .build();

        return resp;
    }

    @Override
    public SatisfactionDetailDto getById(Long id) {
        ProgramSatisfaction s = satisfactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Satisfaction not found: " + id));
        return toDetail(s);
    }

    /* ---------- Spec(동적 where) ---------- */
    private Specification<ProgramSatisfaction> buildSpec(SatisfactionSearchConditionDto c) {
        return (root, q, cb) -> {
            List<jakarta.persistence.criteria.Predicate> ps = new ArrayList<>();

            if (c.getProgramId() != null) {
                ps.add(cb.equal(root.get("program").get("programId"), c.getProgramId()));
            }
            if (c.getScheduleId() != null) {
                ps.add(cb.equal(root.get("schedule").get("scheduleId"), c.getScheduleId()));
            }
            if (StringUtils.hasText(c.getDept())) {
                // Program.department(code or name)에 맞춰 조정
                ps.add(cb.equal(root.get("program").get("department"), c.getDept()));
            }
            if (StringUtils.hasText(c.getCategory())) {
                ps.add(cb.equal(root.get("program").get("category"), c.getCategory()));
            }
            if (StringUtils.hasText(c.getStatus())) {
                try {
                    ProgramStatus st = ProgramStatus.valueOf(c.getStatus());
                    ps.add(cb.equal(root.get("program").get("status"), st));
                } catch (Exception ignore) { /* 잘못된 값이면 필터 생략 */ }
            }
            if (c.getSubmittedFrom() != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("submittedAt"),
                        c.getSubmittedFrom().atStartOfDay()));
            }
            if (c.getSubmittedTo() != null) {
                ps.add(cb.lessThan(root.get("submittedAt"),
                        c.getSubmittedTo().plusDays(1).atStartOfDay()));
            }
            if (c.getRatingMin() != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("rating"), c.getRatingMin()));
            }
            if (c.getRatingMax() != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("rating"), c.getRatingMax()));
            }
            if (StringUtils.hasText(c.getKeyword())) {
                ps.add(cb.like(cb.lower(root.get("feedback")), "%" + c.getKeyword().toLowerCase() + "%"));
            }
            return cb.and(ps.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    /* ---------- DTO 매핑 ---------- */
    private SatisfactionListItemDto toListItem(ProgramSatisfaction s) {
        String scheduleName =
                (s.getSchedule() != null && s.getSchedule().getName() != null)
                        ? s.getSchedule().getName() : "-";
        String title =
                (s.getProgram() != null && s.getProgram().getTitle() != null)
                        ? s.getProgram().getTitle() : "-";

        return SatisfactionListItemDto.builder()
                .id(s.getSatisfactionId())
                .programId(s.getProgram() != null ? s.getProgram().getProgramId() : null)
                .programTitle(title)
                .scheduleId(s.getSchedule() != null ? s.getSchedule().getScheduleId() : null)
                .scheduleName(scheduleName)
                .studentNoMasked(maskStudentNo(
                        s.getStudent().getId() != null ? s.getStudent().getId() : null
                ))
                .studentNameMasked("익명")
                .rating(s.getRating())
                .feedback(s.getComment())
                // 엔티티에 submittedAt이 없으므로 createdAt 사용
                .submittedAt(s.getCreatedAt())
                .build();
    }

    private SatisfactionDetailDto toDetail(ProgramSatisfaction s) {
        String scheduleName =
                (s.getSchedule() != null && s.getSchedule().getName() != null)
                        ? s.getSchedule().getName() : "-";
        String title =
                (s.getProgram() != null && s.getProgram().getTitle() != null)
                        ? s.getProgram().getTitle() : "-";

        return SatisfactionDetailDto.builder()
                .id(s.getSatisfactionId())
                .programId(s.getProgram() != null ? s.getProgram().getProgramId() : null)
                .programTitle(title)
                .scheduleId(s.getSchedule() != null ? s.getSchedule().getScheduleId() : null)
                .scheduleName(scheduleName)
                .studentNoMasked(maskStudentNo(
                        s.getStudent().getId() != null ? s.getStudent().getId() : null
                ))
                .studentNameMasked("익명")
                .rating(s.getRating())
                .feedback(s.getComment())
                .submittedAt(s.getCreatedAt())
                .build();
    }

    /* ---------- 유틸 : 리플렉션 기반 안전 접근자 ---------- */
    @Nullable
    private static Long tryGetLong(Object target, String getter) {
        try {
            if (target == null) return null;
            var m = target.getClass().getMethod(getter);
            var v = m.invoke(target);
            return (v instanceof Number) ? ((Number) v).longValue() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private static String tryGetString(Object target, String getter) {
        try {
            if (target == null) return null;
            var m = target.getClass().getMethod(getter);
            var v = m.invoke(target);
            return (v != null) ? String.valueOf(v) : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private static java.time.LocalDateTime tryGetLocalDateTime(Object target, String getter) {
        try {
            if (target == null) return null;
            var m = target.getClass().getMethod(getter);
            var v = m.invoke(target);
            return (v instanceof java.time.LocalDateTime) ? (java.time.LocalDateTime) v : null;
        } catch (Exception e) {
            return null;
        }
    }

    @SafeVarargs
    private static <T> T firstNonNull(T... values) {
        for (T v : values) if (v != null) return v;
        return null;
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) if (v != null && !v.isBlank()) return v;
        return null;
    }

//    private SatisfactionListItemDto toListItem(ProgramSatisfaction s) {
//        String scheduleName = s.getSchedule() != null ? s.getSchedule().getName() : "-";
//        String title = s.getProgram() != null ? s.getProgram().getTitle() : "-";
//        return SatisfactionListItemDto.builder()
//                .id(s.getId())
//                .programId(s.getProgram() != null ? s.getProgram().getProgramId() : null)
//                .programTitle(title)
//                .scheduleId(s.getSchedule() != null ? s.getSchedule().getScheduleId() : null)
//                .scheduleName(scheduleName)
//                .studentNoMasked(maskStudentNo(s.getStudentId()))
//                .studentNameMasked("익명") // 필요 시 학생 조인/마스킹 로직 삽입
//                .rating(s.getRating())
//                .feedback(s.getFeedback())
//                .submittedAt(s.getSubmittedAt())
//                .build();
//    }
//
//    private SatisfactionDetailDto toDetail(ProgramSatisfaction s) {
//        return SatisfactionDetailDto.builder()
//                .id(s.getId())
//                .programId(s.getProgram() != null ? s.getProgram().getProgramId() : null)
//                .programTitle(s.getProgram() != null ? s.getProgram().getTitle() : "-")
//                .scheduleId(s.getSchedule() != null ? s.getSchedule().getScheduleId() : null)
//                .scheduleName(s.getSchedule() != null ? s.getSchedule().getName() : "-")
//                .studentNoMasked(maskStudentNo(s.getStudentId()))
//                .studentNameMasked("익명")
//                .rating(s.getRating())
//                .feedback(s.getFeedback())
//                .submittedAt(s.getSubmittedAt())
//                .build();
//    }

    private String maskStudentNo(Long studentId) {
        if (studentId == null) return "-";
        String s = String.valueOf(studentId);
        return s.length() >= 4 ? s.substring(0,4) + "****" : s + "****";
    }

    /* ---------- 집계/차트 ---------- */
    private static class Aggregation {
        Double avgRating;
        Long count;
        Double responseRate;
        Double posRatio;
        Double negRatio;
        List<String> byScheduleLabels = new ArrayList<>();
        List<Double> byScheduleValues = new ArrayList<>();
        List<Long> histogramValues = Arrays.asList(0L,0L,0L,0L,0L);
    }

    private Aggregation aggregate(SatisfactionSearchConditionDto c) {
        Aggregation ag = new Aggregation();

        StringBuilder where = new StringBuilder(" where 1=1 ");
        Map<String, Object> p = new HashMap<>();

        if (c.getProgramId() != null) { where.append(" and s.program.programId = :pid"); p.put("pid", c.getProgramId()); }
        if (c.getScheduleId() != null) { where.append(" and s.schedule.scheduleId = :sid"); p.put("sid", c.getScheduleId()); }
        if (StringUtils.hasText(c.getDept())) { where.append(" and s.program.department = :dept"); p.put("dept", c.getDept()); }
        if (StringUtils.hasText(c.getCategory())) { where.append(" and s.program.category = :cat"); p.put("cat", c.getCategory()); }
        if (StringUtils.hasText(c.getStatus())) {
            try { ProgramStatus st = ProgramStatus.valueOf(c.getStatus());
                where.append(" and s.program.status = :st"); p.put("st", st);
            } catch (Exception ignore) {}
        }
        if (c.getSubmittedFrom() != null) { where.append(" and s.submittedAt >= :from"); p.put("from", c.getSubmittedFrom().atStartOfDay()); }
        if (c.getSubmittedTo() != null)   { where.append(" and s.submittedAt < :to");   p.put("to",   c.getSubmittedTo().plusDays(1).atStartOfDay()); }
        if (c.getRatingMin() != null)     { where.append(" and s.rating >= :rmin");     p.put("rmin", c.getRatingMin()); }
        if (c.getRatingMax() != null)     { where.append(" and s.rating <= :rmax");     p.put("rmax", c.getRatingMax()); }
        if (StringUtils.hasText(c.getKeyword())) {
            where.append(" and lower(s.feedback) like :kw"); p.put("kw", "%"+c.getKeyword().toLowerCase()+"%");
        }

        // 평균/카운트 (Typed)
        String q1 = "select avg(s.rating), count(s) from Satisfaction s" + where;
        Object[] a1 = createQuery(q1, Object[].class, p).getSingleResult();
        ag.avgRating = a1[0] != null ? ((Number) a1[0]).doubleValue() : null;
        ag.count     = a1[1] != null ? ((Number) a1[1]).longValue()    : 0L;

        // 응답률 (예: ProgramAttendance의 PRESENT 수 기준)
        try {
            String qAttend = "select count(a) from ProgramAttendance a where a.status = com.competency.SCMS.domain.noncurricular.operation.AttendanceStatus.PRESENT"
                    + (c.getProgramId()!=null ? " and a.schedule.program.programId = :pid" : "");
            Map<String,Object> p2 = new HashMap<>();
            if (c.getProgramId()!=null) p2.put("pid", c.getProgramId());
            Long participants = createQuery(qAttend, Long.class, p2).getSingleResult();
            ag.responseRate = (participants != null && participants > 0) ? ag.count.doubleValue() / participants : null;
        } catch (Exception ignore){ ag.responseRate = null; }

        // 회차별 평균평점: name(Transient) 사용 금지 → sessionNo 등으로 받아서 라벨은 자바에서 조립
        String q2 =
                "select s.schedule.sessionNo, s.schedule.date, s.schedule.startTime, avg(s.rating) " +
                        "from Satisfaction s " + where +
                        " group by s.schedule.sessionNo, s.schedule.date, s.schedule.startTime " +
                        " order by min(s.schedule.sessionNo) asc";

        List<Object[]> rows = createQuery(q2, Object[].class, p).getResultList();
        for (Object[] r : rows) {
            Integer no        = (Integer) r[0];
            java.time.LocalDate  d = (java.time.LocalDate)  r[1];
            java.time.LocalTime  t = (java.time.LocalTime)  r[2];
            String label = (no != null ? no + "회차" : "회차")
                    + " (" + (d != null ? d : "-") + (t != null ? " " + t : "") + ")";
            ag.byScheduleLabels.add(label);
            ag.byScheduleValues.add(r[3] != null ? ((Number) r[3]).doubleValue() : 0.0);
        }

        // 히스토그램 (1~5)
        List<Long> hist = new ArrayList<>(Arrays.asList(0L,0L,0L,0L,0L));
        for (int rv = 1; rv <= 5; rv++) {
            String qh = "select count(s) from Satisfaction s " + where + " and s.rating = :rv";
            Map<String,Object> ph = new HashMap<>(p); ph.put("rv", rv);
            Long cnt = createQuery(qh, Long.class, ph).getSingleResult();
            hist.set(rv-1, cnt == null ? 0L : cnt);
        }
        ag.histogramValues = hist;

        return ag;
    }

//    private Double ratioCount(String cond, String baseWhere, Map<String,Object> p, Long total){
//        if (total==null || total==0) return null;
//        String q = "select count(s) from Satisfaction s " + baseWhere + " and " + cond;
//        Long c = (Long) createQuery(q, p).getSingleResult();
//        return c != null ? (double)c / total : null;
//    }

//    private TypedQuery<?> createQuery(String jpql, Map<String,Object> params){
//        TypedQuery<?> q = em.createQuery(jpql, Object.class);
//        params.forEach(q::setParameter);
//        return q;
//    }

    private Double ratioCount(String cond, String baseWhere, Map<String,Object> baseParams, Long total) {
        if (total == null || total == 0) return null;

        String q = "select count(s) from Satisfaction s " + baseWhere + " and " + cond;

        // 원본 맵을 건드리지 않도록 복사본 사용
        Map<String,Object> params = (baseParams != null) ? new HashMap<>(baseParams) : new HashMap<>();

        Long c = createQuery(q, Long.class, params).getSingleResult();
        return (c != null) ? c.doubleValue() / total : null;
    }

    private <T> TypedQuery<T> createQuery(String jpql, Class<T> type, Map<String,Object> params) {
        TypedQuery<T> q = em.createQuery(jpql, type);
        if (params != null) params.forEach(q::setParameter);
        return q;
    }
}
