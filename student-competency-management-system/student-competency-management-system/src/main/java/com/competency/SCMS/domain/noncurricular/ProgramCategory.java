package com.competency.SCMS.domain.noncurricular;


import com.competency.SCMS.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "program_categories",
        indexes = {
                @Index(name = "ix_program_categories_parent", columnList = "parent_id"),
                @Index(name = "ix_program_categories_sort",   columnList = "sort_order")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_program_categories_code", columnNames = "catg_code")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgramCategory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catg_id")
    private Long id;

    @Column(name = "catg_code", length = 50, nullable = false)
    private String code;

    @Column(name = "catg_name", length = 100, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProgramCategory parent;

    @Column(name = "use_yn", nullable = false)
    private boolean useYn = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @Column(length = 255)
    private String description;
}

