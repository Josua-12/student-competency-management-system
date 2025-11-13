package com.competency.scms.controller.noncurricular.mileage;

import com.competency.scms.dto.noncurricular.mileage.ProgramSearchRowDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.competency.scms.domain.noncurricular.program.ProgramStatus;
import com.competency.scms.domain.noncurricular.program.ProgramSchedule;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import com.competency.scms.repository.noncurricular.program.ProgramScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/op/programs")
public class SelectProgramRoundController {

    private final ProgramRepository programRepository;
    private final ProgramScheduleRepository scheduleRepository;

    // 모달 검색
    @GetMapping("/search")
    public Page<ProgramSearchRowDto> searchPrograms(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ProgramStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return programRepository.searchForMileage(
                q == null || q.isBlank() ? null : q.trim(),
                status,
                PageRequest.of(page, size));
    }

    // 회차 콤보
    @GetMapping("/{progId}/schedules")
    public List<ProgramSchedule> findSchedules(@PathVariable Long progId) {
        return scheduleRepository.findAllByProgram_ProgramIdOrderByDateAsc(progId);

    }
}
