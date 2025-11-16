package com.competency.scms.domain.counseling;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "counseling_attachments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselingAttachment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private CounselingReservation reservation;
    
    @Column(nullable = false)
    private String originalName;
    
    @Column(nullable = false)
    private String storedPath;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column
    private String contentType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttachmentType attachmentType;
    
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
}
