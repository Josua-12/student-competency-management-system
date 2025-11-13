package com.competency.scms.controller.counsel;

import com.competency.scms.domain.Department;
import com.competency.scms.domain.counseling.*;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.repository.DepartmentRepository;
import com.competency.scms.repository.counseling.*;
import com.competency.scms.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.competency.scms.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)    //MySQL 사용
@Transactional      // 각 테스트 후 롤백하여 DB 정리
public class CounselingRepositoryTest { //@Query문만 검사

    //--테스트에 필수적 Repos--//
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CounselorRepository counselorRepository;
    @Autowired
    private CounselingReservationRepository counselingReservationRepository;
    @Autowired
    private SatisfactionQuestionRepository satisfactionQuestionRepository;
    @Autowired
    private CounselingSubFieldRepository subFieldRepository;
    @Autowired
    private TestCounselingReservationRepository testCounselingReservationRepository;

    //--쿼리문 있는 Repos--//
    @Autowired
    private CounselingRecordRepository counselingRecordRepository;
    @Autowired
    private CounselingSatisfactionRepository counselingSatisfactionRepository;
    @Autowired
    private CounselingScheduleRepository counselingScheduleRepository;
    @Autowired
    private SatisfactionAnswerRepository satisfactionAnswerRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    private User testStudent;
    private User testCounselor;
    private CounselingReservation testReservation;
    private CounselingReservation testReservation2;
    private CounselingSubField subfield;
    private CounselingSatisfaction testSatisfaction;
    private SatisfactionQuestion testQuestion;
    private SatisfactionAnswer testAnswer;
    private CounselingRecord counselingRecord;
    private CounselingBaseSchedule counselingBaseSchedule;


    @BeforeEach
    void setup(){
        testStudent = User.builder()
                .role(UserRole.STUDENT)
                .userNum(12345)
                .name("김학생")
                .email("student@gmail.com")
                .phone("010-1234-5678")
                .password("password")
                .birthDate(LocalDate.of(2000,1,1))
                .department(ensureDept("ECONOMICS", "경제학과")
                .grade(1)
                .build();
        userRepository.save(testStudent);

        testCounselor = User.builder()
                .role(UserRole.COUNSELOR)
                .userNum(12345)
                .name("이상담")
                .email("counselor@gmail.com")
                .phone("010-1234-5678")
                .password("password")
                .birthDate(LocalDate.of(2000,1,1))
                .build();
        userRepository.save(testCounselor);

        subfield = CounselingSubField.builder()
                .counselingField(CounselingField.CAREER)
                .subfieldName("하위분야이름")
                .description("직업상담의 하위분야입니다")
                .build();
        subFieldRepository.save(subfield);

        // 예약 2개
        testReservation = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,11))
                .startTime(LocalTime.of(11,0))
                .endTime(LocalTime.of(12,0))
                .createdAt(LocalDateTime.of(2025,10,10,9,10))
                .requestContent("상담신청합니다.")
                .build();
        counselingReservationRepository.save(testReservation);

        testReservation2 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(9,0))
                .endTime(LocalTime.of(10,0))
                .createdAt(LocalDateTime.of(2025,10,20,9,10))
                .requestContent("두 번째 상담 신청합니다.")
                .build();
        counselingReservationRepository.save(testReservation2);

        // 질문 1개
        testQuestion = new SatisfactionQuestion();
        testQuestion.setQuestionText("상담에 만족하셨나요?");
        testQuestion.setQuestionType(SatisfactionQuestion.QuestionType.RATING);
        testQuestion.setDisplayOrder(1);
        satisfactionQuestionRepository.save(testQuestion);

    }

    @Test
    @DisplayName("테스트1 - 질문별 답변 개수")
    void testCountByQuestion(){
        //Given: 테스트 데이터 준비
        CounselingSatisfaction testSatisfaction = new CounselingSatisfaction();
        testSatisfaction.setReservation(testReservation);
        testSatisfaction.setStudent(testStudent);
        testSatisfaction.setCounselor(testCounselor);
        counselingSatisfactionRepository.save(testSatisfaction);

        testAnswer = new SatisfactionAnswer();
        testAnswer.setSatisfaction(testSatisfaction);
        testAnswer.setQuestion(testQuestion);
        testAnswer.setRatingValue(5);
        satisfactionAnswerRepository.save(testAnswer);

        //When: 질문별 답변 개수 조회
        Long count = satisfactionAnswerRepository.countByQuestion(testQuestion);

        //Then: 결과 검증
        assertNotNull(count, "답변 개수는 null이 아니어야 합니다");
        assertEquals(1L, count, "답변 개수는 1개여야 합니다.");
    }

    @Test
    @DisplayName("테스트2 - 질문별 평균점수")
    void testGetAverageRatingByQuestion(){
        //Given: 테스트 데이터 준비

        //추가 예약 2개 생성 (테스트2 전용) -- 제약조건 충돌발생 방지하기 위함
        CounselingReservation reservation1 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,12,1))
                .startTime(LocalTime.of(14,0))
                .endTime(LocalTime.of(15,0))
                .requestContent("평균점수 테스트용 예약1")
                .build();
        counselingReservationRepository.save(reservation1);

        CounselingReservation reservation2 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,12,2))
                .startTime(LocalTime.of(10,0))
                .endTime(LocalTime.of(11,0))
                .requestContent("평균점수 테스트용 예약2")
                .build();
        counselingReservationRepository.save(reservation2);

        // 만족도 2개 생성
        CounselingSatisfaction testSatisfaction = new CounselingSatisfaction();
        testSatisfaction.setReservation(reservation1);
        testSatisfaction.setStudent(testStudent);
        testSatisfaction.setCounselor(testCounselor);
        counselingSatisfactionRepository.save(testSatisfaction);

        CounselingSatisfaction testSatisfaction2 = new CounselingSatisfaction();
        testSatisfaction2.setReservation(reservation2);
        testSatisfaction2.setStudent(testStudent);
        testSatisfaction2.setCounselor(testCounselor);
        counselingSatisfactionRepository.save(testSatisfaction2);

        // 답변 2개 생성 (5점, 3점)
        SatisfactionAnswer testAnswer = new SatisfactionAnswer();
        testAnswer.setSatisfaction(testSatisfaction);
        testAnswer.setQuestion(testQuestion);
        testAnswer.setRatingValue(5);
        satisfactionAnswerRepository.save(testAnswer);

        SatisfactionAnswer testAnswer2 = new SatisfactionAnswer();
        testAnswer2.setSatisfaction(testSatisfaction2);
        testAnswer2.setQuestion(testQuestion);
        testAnswer2.setRatingValue(3);
        satisfactionAnswerRepository.save(testAnswer2);

        //When: 질문별 평균점수 조회
        Double average = satisfactionAnswerRepository.getAverageRatingByQuestion(testQuestion);

        //Then: 결과 검증 (5+3) / 2 = 4.0
        assertNotNull(average, "평균 점수는 null이 아니어야 합니다");
        assertEquals(4.0, average, 0.01,"평균 점수는 4.0이어야 합니다.");

    }

    @Test
    @DisplayName("테스트3 - 상태별 예약 개수")
    void testCountGroupByStatus(){
        //GIVEN
        CounselingReservation testReservation3 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(10,0))
                .endTime(LocalTime.of(11,0))
                .requestContent("취소할 상담 신청합니다.")
                .status(ReservationStatus.CANCELLED)
                .build();
        counselingReservationRepository.save(testReservation3);

        CounselingReservation testReservation4 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(11,0))
                .endTime(LocalTime.of(12,0))
                .requestContent("취소할건데 상담 신청합니다.")
                .status(ReservationStatus.CANCELLED)
                .build();
        counselingReservationRepository.save(testReservation4);

        CounselingReservation testReservation5 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(12,0))
                .endTime(LocalTime.of(13,0))
                .requestContent("확정될 상담 신청합니다.")
                .status(ReservationStatus.CONFIRMED)
                .build();
        counselingReservationRepository.save(testReservation5);

        CounselingReservation testReservation6 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(13,0))
                .endTime(LocalTime.of(14,0))
                .requestContent("완료될 상담 신청합니다.")
                .status(ReservationStatus.COMPLETED)
                .build();
        counselingReservationRepository.save(testReservation6);

        CounselingReservation testReservation7 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(14,0))
                .endTime(LocalTime.of(15,0))
                .requestContent("거절될 상담 신청합니다.")
                .status(ReservationStatus.REJECTED)
                .build();
        counselingReservationRepository.save(testReservation7);

        CounselingReservation testReservation8 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(15,0))
                .endTime(LocalTime.of(16,0))
                .requestContent("미출석할 상담 신청합니다.")
                .status(ReservationStatus.NO_SHOW)
                .build();
        counselingReservationRepository.save(testReservation8);

        //WHEN
        List<Object[]> result = counselingReservationRepository.countGroupByStatus();

        // THEN
        assertThat(result).hasSize(6);

        assertThat(result).anyMatch(r -> r[0] == ReservationStatus.PENDING && (Long) r[1] == 2L);
        assertThat(result).anyMatch(r -> r[0] == ReservationStatus.CANCELLED && (Long) r[1] == 2L);
        assertThat(result).anyMatch(r -> r[0] == ReservationStatus.CONFIRMED && (Long) r[1] == 1L);
        assertThat(result).anyMatch(r -> r[0] == ReservationStatus.COMPLETED && (Long) r[1] == 1L);
        assertThat(result).anyMatch(r -> r[0] == ReservationStatus.REJECTED && (Long) r[1] == 1L);
        assertThat(result).anyMatch(r -> r[0] == ReservationStatus.NO_SHOW && (Long) r[1] == 1L);
    }

    @Test
    @DisplayName("테스트4 - 기간별 만족도 조회")
    void testFindBySubmittedAtBetween(){
        //GIVEN
        CounselingReservation testReservation3 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(10,0))
                .endTime(LocalTime.of(11,0))
                .requestContent("취소할 상담 신청합니다.")
                .status(ReservationStatus.CANCELLED)
                .build();
        counselingReservationRepository.save(testReservation3);

        CounselingSatisfaction satisfaction1 = new CounselingSatisfaction();
        satisfaction1.setReservation(testReservation);
        satisfaction1.setStudent(testStudent);
        satisfaction1.setCounselor(testCounselor);
        counselingSatisfactionRepository.saveAndFlush(satisfaction1);
        counselingSatisfactionRepository.updateSubmittedAt(satisfaction1.getId(), LocalDateTime.of(2025, 1, 10, 10, 0));

        CounselingSatisfaction satisfaction2 = new CounselingSatisfaction();
        satisfaction2.setReservation(testReservation2);
        satisfaction2.setStudent(testStudent);
        satisfaction2.setCounselor(testCounselor);
        counselingSatisfactionRepository.saveAndFlush(satisfaction2);
        counselingSatisfactionRepository.updateSubmittedAt(satisfaction2.getId(), LocalDateTime.of(2025, 1, 15, 14, 0));

        CounselingSatisfaction satisfaction3 = new CounselingSatisfaction();
        satisfaction3.setReservation(testReservation3);
        satisfaction3.setStudent(testStudent);
        satisfaction3.setCounselor(testCounselor);
        counselingSatisfactionRepository.saveAndFlush(satisfaction3);
        counselingSatisfactionRepository.updateSubmittedAt(satisfaction3.getId(), LocalDateTime.of(2025, 1, 20, 16, 0));

        //WHEN
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 12, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 18, 23, 59);
        List<CounselingSatisfaction> result = counselingSatisfactionRepository.findBySubmittedAtBetween(startDate, endDate);

        //THEN
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("테스트5 - 기간별 예약 조회")
    void testFindByCreatedAtBetween(){
        //GIVEN
        CounselingReservation testReservation3 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(15,0))
                .endTime(LocalTime.of(16,0))
                .requestContent("미출석할 상담 신청합니다.")
                .status(ReservationStatus.NO_SHOW)
                .build();
        counselingReservationRepository.saveAndFlush(testReservation3);
        testCounselingReservationRepository.updateCreatedAt(testReservation3.getId(), LocalDateTime.of(2025,10,11,9,10));

        CounselingReservation testReservation4 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(16,0))
                .endTime(LocalTime.of(17,0))
                .requestContent("미출석할 두번째 상담 신청합니다.")
                .status(ReservationStatus.NO_SHOW)
                .build();
        counselingReservationRepository.saveAndFlush(testReservation4);
        testCounselingReservationRepository.updateCreatedAt(testReservation4.getId(), LocalDateTime.of(2025,11,11,9,10));

        testCounselingReservationRepository.updateCreatedAt(testReservation.getId(), LocalDateTime.of(2025,10,10,9,10));
        testCounselingReservationRepository.updateCreatedAt(testReservation2.getId(), LocalDateTime.of(2025,10,20,9,10));

        //WHEN
        LocalDateTime startDate = LocalDateTime.of(2025,10,11,0,0);
        LocalDateTime endDate = LocalDateTime.of(2025,10,20,23,59);
        Page<CounselingReservation> result = counselingReservationRepository.findByCreatedAtBetween(startDate, endDate, Pageable.unpaged());

        //THEN
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("requestContent")
                .containsExactlyInAnyOrder("두 번째 상담 신청합니다.", "미출석할 상담 신청합니다.");
    }

    @Test
    @DisplayName("테스트 6 - 공개된 상담 기록 기간별 조회")
    void testFindByIsPublicTrueAndCounselingDateBetween(){
        // GIVEN
        CounselingRecord testCounselingRecord1 = CounselingRecord.builder()
                .reservation(testReservation)
                .counselor(testCounselor)
                .student(testStudent)
                .subfield(subfield)
                .recordContent("첫 학습 상담")
                .counselorMemo("학생이 조언을 열린 마음으로 받아들이며, 개선 의욕이 있다.")
                .counselingDate(LocalDateTime.of(2025,11,10,9,10))
                .isPublic(true)
                .build();
        counselingRecordRepository.save(testCounselingRecord1);

        CounselingRecord testCounselingRecord2 = CounselingRecord.builder()
                .reservation(testReservation2)
                .counselor(testCounselor)
                .student(testStudent)
                .subfield(subfield)
                .recordContent("두 번째 학습 상담")
                .counselorMemo("학생이 다른 과목에도 흥미를 가지기 시작했다.")
                .counselingDate(LocalDateTime.of(2025,11,11,9,00))
                .isPublic(true)
                .build();
        counselingRecordRepository.save(testCounselingRecord2);

        //WHEN
        LocalDateTime startDate = LocalDateTime.of(2025,10,11,0,0);
        LocalDateTime endDate = LocalDateTime.of(2025,11,20,23,59);
        Page<CounselingRecord> result = counselingRecordRepository.findByIsPublicTrueAndCounselingDateBetween(startDate, endDate, Pageable.unpaged());

        //THEN
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("recordContent")
                .containsExactlyInAnyOrder("첫 학습 상담", "두 번째 학습 상담");
    }

    @Test
    @DisplayName("테스트7 - 상담사별 기간별 상담 기록 조회")
    void testFindByCounselorAndCounselingDateBetween(){
        // GIVEN
        CounselingRecord testCounselingRecord1 = CounselingRecord.builder()
                .reservation(testReservation)
                .counselor(testCounselor)
                .student(testStudent)
                .subfield(subfield)
                .recordContent("첫 학습 상담")
                .counselorMemo("학생이 조언을 열린 마음으로 받아들이며, 개선 의욕이 있다.")
                .counselingDate(LocalDateTime.of(2025,11,10,9,10))
                .build();
        counselingRecordRepository.save(testCounselingRecord1);

        CounselingRecord testCounselingRecord2 = CounselingRecord.builder()
                .reservation(testReservation2)
                .counselor(testCounselor)
                .student(testStudent)
                .subfield(subfield)
                .recordContent("두 번째 학습 상담")
                .counselorMemo("학생이 다른 과목에도 흥미를 가지기 시작했다.")
                .counselingDate(LocalDateTime.of(2025,11,11,9,00))
                .build();
        counselingRecordRepository.save(testCounselingRecord2);

        //WHEN
        LocalDateTime startDate = LocalDateTime.of(2025,10,11,0,0);
        LocalDateTime endDate = LocalDateTime.of(2025,11,20,23,59);
        Page<CounselingRecord> result = counselingRecordRepository.findByCounselorAndCounselingDateBetween(testCounselor, startDate, endDate, Pageable.unpaged());

        //THEN
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("recordContent")
                .containsExactlyInAnyOrder("첫 학습 상담", "두 번째 학습 상담");
    }

    @Test
    @DisplayName("테스트8 - 상담사별 기간별 만족도 조회")
    void testFindByCounselorAndSubmittedAtBetween(){
        //GIVEN
        CounselingReservation testReservation3 = CounselingReservation.builder()
                .student(testStudent)
                .counselor(testCounselor)
                .counselingField(CounselingField.CAREER)
                .subField(subfield)
                .reservationDate(LocalDate.of(2025,11,15))
                .startTime(LocalTime.of(10,0))
                .endTime(LocalTime.of(11,0))
                .requestContent("취소할 상담 신청합니다.")
                .status(ReservationStatus.CANCELLED)
                .build();
        counselingReservationRepository.save(testReservation3);

        CounselingSatisfaction satisfaction1 = new CounselingSatisfaction();
        satisfaction1.setReservation(testReservation);
        satisfaction1.setStudent(testStudent);
        satisfaction1.setCounselor(testCounselor);
        counselingSatisfactionRepository.saveAndFlush(satisfaction1);
        counselingSatisfactionRepository.updateSubmittedAt(satisfaction1.getId(), LocalDateTime.of(2025, 1, 10, 10, 0));

        CounselingSatisfaction satisfaction2 = new CounselingSatisfaction();
        satisfaction2.setReservation(testReservation2);
        satisfaction2.setStudent(testStudent);
        satisfaction2.setCounselor(testCounselor);
        counselingSatisfactionRepository.saveAndFlush(satisfaction2);
        counselingSatisfactionRepository.updateSubmittedAt(satisfaction2.getId(), LocalDateTime.of(2025, 1, 15, 14, 0));

        CounselingSatisfaction satisfaction3 = new CounselingSatisfaction();
        satisfaction3.setReservation(testReservation3);
        satisfaction3.setStudent(testStudent);
        satisfaction3.setCounselor(testCounselor);
        counselingSatisfactionRepository.saveAndFlush(satisfaction3);
        counselingSatisfactionRepository.updateSubmittedAt(satisfaction3.getId(), LocalDateTime.of(2025, 1, 17, 16, 0));

        //WHEN
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 12, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 18, 23, 59);
        List<CounselingSatisfaction> result = counselingSatisfactionRepository.findByCounselorAndSubmittedAtBetween(testCounselor, startDate, endDate);

        //THEN
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("테스트9 - 상담사별 기간별 일정 조회")
    void testFindByCounselorAndScheduleDateBetween(){
        //GIVEN
        CounselingBaseSchedule schedule1 = new CounselingBaseSchedule();
        schedule1.setCounselor(testCounselor);
        schedule1.setDayOfWeek(DayOfWeek.MONDAY);
        schedule1.setSlot0910(false);
        schedule1.setSlot1011(false);
        schedule1.setSlot1213(true);
        counselingScheduleRepository.save(schedule1);

        CounselingBaseSchedule schedule2 = new CounselingBaseSchedule();
        schedule2.setCounselor(testCounselor);
        schedule2.setDayOfWeek(DayOfWeek.WEDNESDAY);
        schedule2.setSlot1314(false);
        schedule2.setSlot1415(false);
        counselingScheduleRepository.save(schedule2);

        //WHEN
        Page<CounselingBaseSchedule> result = counselingScheduleRepository.findByCounselorOrderByDayOfWeek(testCounselor, Pageable.unpaged());

        //THEN
        assertThat(result.getContent()).hasSize(2);
        
        CounselingBaseSchedule mondaySchedule = result.getContent().get(0);
        assertThat(mondaySchedule.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(mondaySchedule.getSlot0910()).isFalse();
        assertThat(mondaySchedule.getSlot1011()).isFalse();
        assertThat(mondaySchedule.getSlot1112()).isTrue();
        assertThat(mondaySchedule.getSlot1213()).isTrue();
        assertThat(mondaySchedule.getSlot1314()).isTrue();
        assertThat(mondaySchedule.getSlot1415()).isTrue();
        assertThat(mondaySchedule.getSlot1516()).isTrue();
        assertThat(mondaySchedule.getSlot1617()).isTrue();
        assertThat(mondaySchedule.getSlot1718()).isTrue();
        
        CounselingBaseSchedule wednesdaySchedule = result.getContent().get(1);
        assertThat(wednesdaySchedule.getDayOfWeek()).isEqualTo(DayOfWeek.WEDNESDAY);
        assertThat(wednesdaySchedule.getSlot0910()).isTrue();
        assertThat(wednesdaySchedule.getSlot1011()).isTrue();
        assertThat(wednesdaySchedule.getSlot1112()).isTrue();
        assertThat(wednesdaySchedule.getSlot1213()).isFalse();
        assertThat(wednesdaySchedule.getSlot1314()).isFalse();
        assertThat(wednesdaySchedule.getSlot1415()).isFalse();
        assertThat(wednesdaySchedule.getSlot1516()).isTrue();
        assertThat(wednesdaySchedule.getSlot1617()).isTrue();
        assertThat(wednesdaySchedule.getSlot1718()).isTrue();
    }

    @Test
    @DisplayName("테스트10 - 키워드로 예약 검색")
    void testFindByKeyword(){
        //GIVEN
        //WHEN
        Page<CounselingReservation> result = counselingReservationRepository.findByKeyword("김학", Pageable.unpaged()); //'김학생'의 예약 찾기
        Page<CounselingReservation> result2 = counselingReservationRepository.findByKeyword("이상", Pageable.unpaged()); //'이상담'의 예약 찾기
        //THEN
        assertThat(result.getContent()).hasSize(2);
        assertThat(result2.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("테스트11 - 상담 유형별 통계")
    void testCountByStatusGroupByCounselingField(){
        //GIVEN
        testReservation.setStatus(ReservationStatus.COMPLETED);
        testReservation2.setStatus(ReservationStatus.COMPLETED);
        counselingReservationRepository.save(testReservation);
        counselingReservationRepository.save(testReservation2);

        //WHEN
        List<Object[]> result = counselingReservationRepository.countByStatusGroupByCounselingField(ReservationStatus.COMPLETED);

        //THEN
        assertThat(result).hasSize(1);      // counselingField = CAREER 로 한 개
        assertThat(result.get(0)[0]).isEqualTo(CounselingField.CAREER);
        assertThat(result.get(0)[1]).isEqualTo(2L);
    }

    @Test
    @DisplayName("테스트12 - 상담사별 특정 상태들의 예약 조회")
    void testFindByCounselorAndStatusIn(){
        //GIVEN
        testReservation.setStatus(ReservationStatus.CONFIRMED);
        testReservation2.setStatus(ReservationStatus.COMPLETED);
        counselingReservationRepository.save(testReservation);
        counselingReservationRepository.save(testReservation2);

        //WHEN
        List<ReservationStatus> statuses = List.of(ReservationStatus.CONFIRMED, ReservationStatus.COMPLETED);
        Page<CounselingReservation> result = counselingReservationRepository.findByCounselorAndStatusIn(testCounselor, statuses, Pageable.unpaged());

        //THEN
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("status")
                .containsExactlyInAnyOrder(ReservationStatus.CONFIRMED, ReservationStatus.COMPLETED);
    }

    @Test
    @DisplayName("테스트13 - 상담사별 특정 상태의 예약 일정순 조회")
    void testFindByCounselorAndStatusOrderByConfirmedDateTimeAsc(){
        //GIVEN
        testReservation.setStatus(ReservationStatus.CONFIRMED);
        testReservation.setConfirmedDate(LocalDate.of(2025,11,11));
        testReservation.setConfirmedStartTime(LocalTime.of(11,0));
        testReservation2.setStatus(ReservationStatus.CONFIRMED);
        testReservation2.setConfirmedDate(LocalDate.of(2025,11,15));
        testReservation2.setConfirmedStartTime(LocalTime.of(9,0));
        counselingReservationRepository.save(testReservation);
        counselingReservationRepository.save(testReservation2);

        //WHEN
        Page<CounselingReservation> result = counselingReservationRepository.findByCounselorAndStatusOrderByConfirmedDateTimeAsc(testCounselor, ReservationStatus.CONFIRMED, Pageable.unpaged());

        //THEN
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("reservationDate")
                .containsExactly(LocalDate.of(2025,11,11), LocalDate.of(2025,11,15));
    }

    @Test
    @DisplayName("테스트14 - 특정 날짜에 가능한 상담사 조회")
    void testFindAvailableCounselorsOnDate(){
        //GIVEN
        CounselingBaseSchedule schedule = new CounselingBaseSchedule();
        schedule.setCounselor(testCounselor);
        schedule.setDayOfWeek(DayOfWeek.MONDAY);
        schedule.setSlot0910(true);
        counselingScheduleRepository.save(schedule);

        //WHEN
        Page<CounselingBaseSchedule> result = counselingScheduleRepository.findAvailableSchedulesByDayOfWeek(DayOfWeek.MONDAY, Pageable.unpaged());

        //THEN
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("테스트15 - 상담원별 상태별 예약 개수")
    void testCountByCounselorGroupByStatus(){
        //GIVEN
        testReservation.setStatus(ReservationStatus.CONFIRMED);
        testReservation2.setStatus(ReservationStatus.COMPLETED);
        counselingReservationRepository.save(testReservation);
        counselingReservationRepository.save(testReservation2);

        //WHEN
        Page<Object[]> result = counselingReservationRepository.countByCounselorGroupByStatus(Pageable.unpaged());

        //THEN
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).anyMatch(r -> 
                r[0].equals(testCounselor) && r[1] == ReservationStatus.CONFIRMED && (Long) r[2] == 1L);
        assertThat(result.getContent()).anyMatch(r -> 
                r[0].equals(testCounselor) && r[1] == ReservationStatus.COMPLETED && (Long) r[2] == 1L);
    }

    private Department ensureDept(String code, String name) {
        return departmentRepository.findByCode(code)
                .orElseGet(() -> departmentRepository.save(
                        Department.builder().code(code).name(name).build()));
    }








}
