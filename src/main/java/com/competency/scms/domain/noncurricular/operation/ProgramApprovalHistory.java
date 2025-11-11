package com.competency.scms.domain.noncurricular.operation;

import com.competency.scms.domain.noncurricular.program.Program;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "program_approval_histories")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class ProgramApprovalHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 소속 프로그램 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id")
    private Program program;

    /** 처리 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApprovalStatus status;  // REQ, WAIT, DONE, REJ

    /** 처리자 이름 / 역할(표시용) */
    @Column(nullable = false, length = 100)
    private String actorName;

    @Column(length = 50)
    private String actorRole; // 부서관리자, 시스템관리자 등

    /** 의견 */
    @Column(length = 1000)
    private String comment;

    /** 생성 일시 */
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
