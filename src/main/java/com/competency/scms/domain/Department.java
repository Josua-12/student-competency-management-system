package com.competency.scms.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long deptId;

    /** 부서 코드(선택) */
    @Column(length = 50, unique = true)
    private String code;

    /** 부서명 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 상위부서(선택) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_dept_id")
    private Department parent;

    public Long getId() {
        return deptId;
    }

    /** 사용 여부(선택) */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}