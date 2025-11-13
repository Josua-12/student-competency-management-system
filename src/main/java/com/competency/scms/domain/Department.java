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
    private Long id;

    /** 부서 코드(선택) */
    @Column(length = 50, unique = true)
    private String code;

    /** 부서명 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 상위부서(선택) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Department parent;

    /** 사용 여부(선택) */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}