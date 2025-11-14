package com.competency.scms.controller.counsel;

import com.competency.scms.domain.counseling.CounselingField;
import com.competency.scms.dto.counsel.CounselingScheduleDto;
import com.competency.scms.service.counsel.CounselingScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/counseling/schedule")
@RequiredArgsConstructor
public class CounselingScheduleApiController {

    private final CounselingScheduleService scheduleService;

    // 특정 날짜의 예약 가능한 시간대 조회 (시간대별 상담사 그룹화)
    @GetMapping("/available-slots")
    public ResponseEntity<List<CounselingScheduleDto.AvailableSlot>> getAvailableSlots(
            @RequestParam LocalDate date,
            @RequestParam CounselingField field) {
        
        List<CounselingScheduleDto.AvailableSlot> slots = scheduleService.getAvailableSlotsGrouped(date, field);
        return ResponseEntity.ok(slots);
    }

    // 특정 날짜와 시간대의 예약 가능한 상담사 조회
    @GetMapping("/available-counselors")
    public ResponseEntity<List<CounselingScheduleDto.AvailableSlot>> getAvailableCounselors(
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam CounselingField field) {
        
        List<CounselingScheduleDto.AvailableSlot> counselors = scheduleService.getAvailableCounselorsForSlot(date, startTime, field);
        return ResponseEntity.ok(counselors);
    }

    // 월간 일정 조회
    @GetMapping("/monthly")
    public ResponseEntity<List<CounselingScheduleDto.MonthlySchedule>> getMonthlySchedules(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam CounselingField field,
            @RequestParam(required = false) Long subfieldId) {
        
        List<CounselingScheduleDto.MonthlySchedule> schedules = scheduleService.getMonthlySchedules(startDate, endDate, field, subfieldId);
        return ResponseEntity.ok(schedules);
    }
}