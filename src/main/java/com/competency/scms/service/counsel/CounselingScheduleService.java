package com.competency.scms.service.counsel;

import com.competency.scms.domain.counseling.CounselingBaseSchedule;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingScheduleDto;
import com.competency.scms.repository.counseling.CounselingScheduleRepository;
import lombok.RequiredArgsConstructor;
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

    private void updateScheduleSlots(CounselingBaseSchedule existing, CounselingBaseSchedule newSchedule) {
        existing.setSlot0910(newSchedule.getSlot0910());
        existing.setSlot1011(newSchedule.getSlot1011());
        existing.setSlot1112(newSchedule.getSlot1112());
        existing.setSlot1213(newSchedule.getSlot1213());
        existing.setSlot1314(newSchedule.getSlot1314());
        existing.setSlot1415(newSchedule.getSlot1415());
        existing.setSlot1516(newSchedule.getSlot1516());
        existing.setSlot1617(newSchedule.getSlot1617());
        existing.setSlot1718(newSchedule.getSlot1718());
    }

    private List<CounselingScheduleDto.TimeSlotResponse> convertToTimeSlotResponses(CounselingBaseSchedule schedule) {
        // 구현 필요: 시간대별 응답 변환
        return List.of();
    }
}
