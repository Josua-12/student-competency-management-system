package com.competency.SCMS.service.noncurricular.program;


import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.dto.noncurricular.program.ProgramOpenRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProgramOpenService {

    Program load(Long programId);

    Long saveDraft(Long programId,
                   ProgramOpenRequest dto,
                   MultipartFile poster,
                   List<MultipartFile> guides,
                   List<MultipartFile> attachments);

    Long submitApproval(Long programId,
                        ProgramOpenRequest dto,
                        MultipartFile poster,
                        List<MultipartFile> guides,
                        List<MultipartFile> attachments);

    void deleteDraft(Long programId);
}