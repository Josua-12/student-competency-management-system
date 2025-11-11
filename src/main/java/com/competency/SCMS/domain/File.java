package com.competency.SCMS.domain;

import com.competency.SCMS.domain.noncurricular.program.FileType;
import com.competency.SCMS.domain.noncurricular.program.Program;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "program_files")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class File {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 파일 그룹 아이디(첨부 묶음 식별) */
    @Column(nullable = false)
    private Long groupId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private FileType fileType; // POSTER/GUIDE/ATTACHMENT

    /** 원본 파일명 */
    @Column(nullable = false, length = 255)
    private String originalName;

    /** 저장 파일 경로(또는 해시명) - 필요 시 */
    @Column(length = 500)
    private String storedPath;

    /** 파일 크기(bytes) */
    @Column(nullable = false)
    private Long size;

    /** 컨텐츠 타입 */
    @Column(length = 100)
    private String contentType;

    /** 업로드 일시 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 소속 프로그램 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prog_id")
    private Program program;
}
