package com.competency.scms.service.counsel;

import com.competency.scms.domain.counseling.CounselingBaseSchedule;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingScheduleDto;
import com.competency.scms.repository.counseling.CounselingScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingScheduleService {

    private final CounselingScheduleRepository scheduleRepository;

    // CNSL-006: 상담 일정 등록/수정
    @Transactional
    public void saveSchedule(User counselor, DayOfWeek dayOfWeek, CounselingBaseSchedule schedule) {
        Optional<CounselingBaseSchedule> existing = scheduleRepository.findByCounselorAndDayOfWeek(counselor, dayOfWeek);
        
        if (existing.isPresent()) {
            CounselingBaseSchedule existingSchedule = existing.get();
            updateScheduleSlots(existingSchedule, schedule);
        } else {
            schedule.setCounselor(counselor);
            schedule.setDayOfWeek(dayOfWeek);
            scheduleRepository.save(schedule);
        }
    }

    // CNSL-007: 상담 시간대 관리
    public List<CounselingScheduleDto.TimeSlotResponse> getTimeSlots(User counselor, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Optional<CounselingBaseSchedule> schedule = scheduleRepository.findByCounselorAndDayOfWeek(counselor, dayOfWeek);
        
        if (schedule.isEmpty()) {
            return List.of();
        }
        
        return convertToTimeSlotResponses(schedule.get());
    }

    private void updateScheduleSlots(CounselingBaseSchedule baseSchedule, CounselingBaseSchedule newSchedule) {
        baseSchedule.setSlot0910(newSchedule.getSlot0910());
        baseSchedule.setSlot1011(newSchedule.getSlot1011());
        baseSchedule.setSlot1112(newSchedule.getSlot1112());
        baseSchedule.setSlot1213(newSchedule.getSlot1213());
        baseSchedule.setSlot1314(newSchedule.getSlot1314());
        baseSchedule.setSlot1415(newSchedule.getSlot1415());
        baseSchedule.setSlot1516(newSchedule.getSlot1516());
        baseSchedule.setSlot1617(newSchedule.getSlot1617());
        baseSchedule.setSlot1718(newSchedule.getSlot1718());
    }

    private List<CounselingScheduleDto.TimeSlotResponse> convertToTimeSlotResponses(CounselingBaseSchedule schedule) {
        return List.of(
            createTimeSlotResponse(schedule.getId(), 9, 10, schedule.getSlot0910()),
            createTimeSlotResponse(schedule.getId(), 10, 11, schedule.getSlot1011()),
            createTimeSlotResponse(schedule.getId(), 11, 12, schedule.getSlot1112()),
            createTimeSlotResponse(schedule.getId(), 12, 13, schedule.getSlot1213()),
            createTimeSlotResponse(schedule.getId(), 13, 14, schedule.getSlot1314()),
            createTimeSlotResponse(schedule.getId(), 14, 15, schedule.getSlot1415()),
            createTimeSlotResponse(schedule.getId(), 15, 16, schedule.getSlot1516()),
            createTimeSlotResponse(schedule.getId(), 16, 17, schedule.getSlot1617()),
            createTimeSlotResponse(schedule.getId(), 17, 18, schedule.getSlot1718())
        );
    }

    private CounselingScheduleDto.TimeSlotResponse createTimeSlotResponse(Long scheduleId, int startHour, int endHour, Boolean isAvailable) {
        CounselingScheduleDto.TimeSlotResponse response = new CounselingScheduleDto.TimeSlotResponse();
        response.setId(scheduleId);
        response.setStartTime(java.time.LocalTime.of(startHour, 0));
        response.setEndTime(java.time.LocalTime.of(endHour, 0));
        response.setIsAvailable(isAvailable);
        response.setIsReserved(false);
        return response;
    }

    // 상담사별 일정 조회 (요일순)
    public Page<CounselingBaseSchedule> getCounselorSchedulesByDayOfWeek(User counselor, Pageable pageable) {
        return scheduleRepository.findByCounselorOrderByDayOfWeek(counselor, pageable);
    }

    // 특정 요일에 예약 가능한 상담사 조회
    public Page<CounselingBaseSchedule> getAvailableSchedulesByDayOfWeek(DayOfWeek dayOfWeek, Pageable pageable) {
        return scheduleRepository.findAvailableSchedulesByDayOfWeek(dayOfWeek, pageable);
    }
}
