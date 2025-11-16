package com.competency.scms.service.noncurricular.program;

import com.competency.scms.dto.noncurricular.program.student.ProgramStudentListResponse;
import com.competency.scms.dto.noncurricular.program.student.ProgramStudentSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProgramStudentService {

    Page<ProgramStudentListResponse> getProgramListForStudent(ProgramStudentSearchRequest condition,
                                                              Pageable pageable);
}
