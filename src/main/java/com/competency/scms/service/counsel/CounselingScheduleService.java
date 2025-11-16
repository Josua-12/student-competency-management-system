package com.competency.scms.service.counsel;

import com.competency.scms.domain.counseling.CounselingBaseSchedule;
import com.competency.scms.domain.counseling.CounselingOverrideSchedule;
import com.competency.scms.domain.counseling.CounselingField;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingScheduleDto;
import com.competency.scms.repository.counseling.CounselingScheduleRepository;
import com.competency.scms.repository.counseling.CounselingReservationRepository;
import com.competency.scms.repository.counseling.CounselorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingScheduleService {

    private final CounselingScheduleRepository scheduleRepository;
    private final CounselingReservationRepository reservationRepository;
    private final CounselorRepository counselorRepository;

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

    // 특정 날짜와 시간대의 예약 가능한 상담사 조회
    public List<CounselingScheduleDto.AvailableSlot> getAvailableCounselorsForSlot(LocalDate date, LocalTime startTime, CounselingField field) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int hour = startTime.getHour();
        
        List<Long> fieldCounselorUserIds = counselorRepository.findByCounselingFieldAndIsActiveTrue(field)
            .stream()
            .map(c -> c.getUser().getId())
            .collect(Collectors.toList());
        
        List<CounselingBaseSchedule> baseSchedules = scheduleRepository.findAvailableSchedulesByDayOfWeek(dayOfWeek, Pageable.unpaged()).getContent();
        
        return baseSchedules.stream()
            .filter(schedule -> fieldCounselorUserIds.contains(schedule.getCounselor().getId()))
            .filter(schedule -> isSlotAvailable(schedule, hour, date, schedule.getCounselor()))
            .map(schedule -> {
                CounselingScheduleDto.AvailableSlot slot = new CounselingScheduleDto.AvailableSlot();
                slot.setDate(date);
                slot.setStartTime(startTime);
                slot.setEndTime(startTime.plusHours(1));
                slot.setCounselorId(schedule.getCounselor().getId());
                slot.setCounselorName(schedule.getCounselor().getName());
                return slot;
            })
            .collect(Collectors.toList());
    }

    // 특정 날짜의 예약 가능한 시간대 조회
    public List<CounselingScheduleDto.AvailableSlot> getAvailableSlots(LocalDate date, CounselingField field) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        
        List<Long> fieldCounselorUserIds = counselorRepository.findByCounselingFieldAndIsActiveTrue(field)
            .stream()
            .map(c -> c.getUser().getId())
            .collect(Collectors.toList());
        
        List<CounselingBaseSchedule> baseSchedules = scheduleRepository.findAvailableSchedulesByDayOfWeek(dayOfWeek, Pageable.unpaged()).getContent();
        
        List<CounselingScheduleDto.AvailableSlot> availableSlots = new ArrayList<>();
        
        for (CounselingBaseSchedule schedule : baseSchedules) {
            User counselor = schedule.getCounselor();
            
            if (!fieldCounselorUserIds.contains(counselor.getId())) {
                continue;
            }
            
            for (int hour = 9; hour <= 17; hour++) {
                if (isSlotAvailable(schedule, hour, date, counselor)) {
                    CounselingScheduleDto.AvailableSlot slot = new CounselingScheduleDto.AvailableSlot();
                    slot.setDate(date);
                    slot.setStartTime(LocalTime.of(hour, 0));
                    slot.setEndTime(LocalTime.of(hour + 1, 0));
                    slot.setCounselorId(counselor.getId());
                    slot.setCounselorName(counselor.getName());
                    availableSlots.add(slot);
                }
            }
        }
        
        return availableSlots;
    }

    public List<CounselingScheduleDto.AvailableSlot> getAvailableSlotsGrouped(LocalDate date, CounselingField field) {
        return getAvailableSlots(date, field);
    }

    private boolean isCounselorForField(User counselor, CounselingField field) {
        return counselorRepository.findByCounselorId(counselor.getId())
            .map(c -> c.getCounselingField() == field && c.getIsActive())
            .orElse(false);
    }

    private boolean isSlotAvailable(CounselingBaseSchedule baseSchedule, int hour, LocalDate date, User counselor) {
        // 기본 스케줄에서 해당 시간대가 가능한지 확인
        Boolean baseAvailable = baseSchedule.getSlotAvailability(hour);
        if (baseAvailable == null || !baseAvailable) {
            return false;
        }
        
        // 예외 스케줄 확인 (Override Schedule)
        List<CounselingOverrideSchedule> overrides = scheduleRepository.findOverrideSchedules(counselor, date);
        for (CounselingOverrideSchedule override : overrides) {
            Boolean overrideSlot = getOverrideSlotAvailability(override, hour);
            if (overrideSlot != null && !overrideSlot) {
                return false; // 예외 스케줄에서 OFF로 설정됨
            }
        }
        
        // 이미 예약된 시간대인지 확인
        LocalTime startTime = LocalTime.of(hour, 0);
        boolean isReserved = reservationRepository.existsByCounselorAndReservationDateAndStartTime(counselor, date, startTime);
        
        return !isReserved;
    }

    private Boolean getOverrideSlotAvailability(CounselingOverrideSchedule override, int hour) {
        return switch(hour) {
            case 9 -> override.getSlot0910();
            case 10 -> override.getSlot1011();
            case 11 -> override.getSlot1112();
            case 12 -> override.getSlot1213();
            case 13 -> override.getSlot1314();
            case 14 -> override.getSlot1415();
            case 15 -> override.getSlot1516();
            case 16 -> override.getSlot1617();
            case 17 -> override.getSlot1718();
            default -> null;
        };
    }

    // 상담사 본인의 기본 근무시간표 조회
    public List<Map<String, Object>> getMyBaseSchedule() {
        User currentUser = getCurrentUser();
        List<CounselingBaseSchedule> schedules = scheduleRepository.findByCounselorOrderByDayOfWeek(currentUser, Pageable.unpaged()).getContent();
        
        return schedules.stream().map(schedule -> {
            Map<String, Object> map = new HashMap<>();
            map.put("dayOfWeek", schedule.getDayOfWeek().getValue() - 1);
            map.put("slot0910", schedule.getSlot0910());
            map.put("slot1011", schedule.getSlot1011());
            map.put("slot1112", schedule.getSlot1112());
            map.put("slot1415", schedule.getSlot1415());
            map.put("slot1516", schedule.getSlot1516());
            return map;
        }).collect(Collectors.toList());
    }

    private User getCurrentUser() {
        return (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // 기본 달력 날짜 계산 (금요일 17:00 이후면 다음 주 월요일)
    public LocalDate getDefaultCalendarDate() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        LocalTime now = LocalTime.now();
        
        // 금요일 17:00 이후면 다음 주 월요일로
        if (dayOfWeek == DayOfWeek.FRIDAY && now.isAfter(LocalTime.of(17, 0))) {
            return today.plusDays(3); // 금요일 + 3일 = 다음 주 월요일
        }
        // 토요일이면 다음 주 월요일로
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            return today.plusDays(2);
        }
        // 일요일이면 다음 날(월요일)로
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            return today.plusDays(1);
        }
        
        return today;
    }

    // 월간 일정 조회
    public List<CounselingScheduleDto.MonthlySchedule> getMonthlySchedules(LocalDate startDate, LocalDate endDate, CounselingField field, Long subfieldId) {
        List<CounselingScheduleDto.MonthlySchedule> schedules = new ArrayList<>();
        
        // 해당 필드의 활성화된 상담사 목록 먼저 조회
        List<com.competency.scms.domain.counseling.Counselor> fieldCounselors = counselorRepository.findByCounselingFieldAndIsActiveTrue(field);
        List<Long> fieldCounselorUserIds = fieldCounselors.stream()
            .map(c -> c.getUser().getId())
            .collect(Collectors.toList());
        
        System.out.println("[DEBUG] Field: " + field + ", Counselor User IDs: " + fieldCounselorUserIds);
        fieldCounselors.forEach(c -> System.out.println("  - " + c.getUser().getName() + " (User ID: " + c.getUser().getId() + ", Field: " + c.getCounselingField() + ")"));
        
        if (fieldCounselorUserIds.isEmpty()) {
            return schedules;
        }
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) continue;
            
            List<CounselingBaseSchedule> baseSchedules = scheduleRepository.findAvailableSchedulesByDayOfWeek(dayOfWeek, Pageable.unpaged()).getContent();
            
            for (CounselingBaseSchedule schedule : baseSchedules) {
                User counselor = schedule.getCounselor();
                
                // 해당 필드의 상담사가 아니면 스킵
                if (!fieldCounselorUserIds.contains(counselor.getId())) {
                    continue;
                }
                
                var counselorInfo = counselorRepository.findByCounselorId(counselor.getId()).orElse(null);
                if (counselorInfo == null) continue;
                
                if (field == CounselingField.EMPLOYMENT) {
                    if (counselorInfo.getSpecializations().isEmpty()) continue;
                    
                    var subfield = counselorInfo.getSpecializations().get(0);
                    
                    if (subfieldId != null && !subfield.getId().equals(subfieldId)) continue;
                    
                    for (int hour = 9; hour <= 17; hour++) {
                        Boolean baseAvailable = schedule.getSlotAvailability(hour);
                        if (baseAvailable == null || !baseAvailable) continue;
                        
                        LocalTime startTime = LocalTime.of(hour, 0);
                        boolean isReserved = reservationRepository.existsByCounselorAndReservationDateAndStartTime(counselor, date, startTime);
                        
                        CounselingScheduleDto.MonthlySchedule monthlySchedule = new CounselingScheduleDto.MonthlySchedule();
                        monthlySchedule.setDate(date.toString());
                        monthlySchedule.setStartTime(LocalTime.of(hour, 0));
                        monthlySchedule.setEndTime(LocalTime.of(hour + 1, 0));
                        monthlySchedule.setCounselorId(counselor.getId());
                        monthlySchedule.setCounselorName(counselor.getName());
                        monthlySchedule.setSubfieldId(subfield.getId());
                        monthlySchedule.setSubfieldName(subfield.getSubfieldName());
                        monthlySchedule.setIsAvailable(!isReserved);
                        schedules.add(monthlySchedule);
                    }
                } else {
                    for (int hour = 9; hour <= 17; hour++) {
                        Boolean baseAvailable = schedule.getSlotAvailability(hour);
                        if (baseAvailable == null || !baseAvailable) continue;
                        
                        LocalTime startTime = LocalTime.of(hour, 0);
                        boolean isReserved = reservationRepository.existsByCounselorAndReservationDateAndStartTime(counselor, date, startTime);
                        
                        CounselingScheduleDto.MonthlySchedule monthlySchedule = new CounselingScheduleDto.MonthlySchedule();
                        monthlySchedule.setDate(date.toString());
                        monthlySchedule.setStartTime(LocalTime.of(hour, 0));
                        monthlySchedule.setEndTime(LocalTime.of(hour + 1, 0));
                        monthlySchedule.setCounselorId(counselor.getId());
                        monthlySchedule.setCounselorName(counselor.getName());
                        monthlySchedule.setSubfieldId(null);
                        monthlySchedule.setSubfieldName(counselorInfo.getSpecialization());
                        monthlySchedule.setIsAvailable(!isReserved);
                        schedules.add(monthlySchedule);
                    }
                }
            }
        }
        
        return schedules;
    }
}
