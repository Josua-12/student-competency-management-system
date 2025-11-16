package com.competency.scms.dto.counsel;

import com.competency.scms.domain.counseling.AttachmentType;
import lombok.Data;

@Data
public class CounselingAttachmentDto {
    private Long id;
    private String originalName;
    private String storedPath;
    private Long fileSize;
    private AttachmentType attachmentType;
}
