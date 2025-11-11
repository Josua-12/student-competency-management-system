package com.competency.SCMS.controller.counsel;

import com.competency.SCMS.domain.counseling.*;
import com.competency.SCMS.domain.user.UserRole;
import com.competency.SCMS.repository.counseling.*;
import com.competency.SCMS.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.competency.SCMS.domain.user.User;

import java.time.LocalDate;
import java.time.LocalTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
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

    //--쿼리문 있는 Repos--//
    @Autowired
    private CounselingRecordRepository counselingRecordRepository;
    @Autowired
    private CounselingSatisfactionRepository counselingSatisfactionRepository;
    @Autowired
    private CounselingScheduleRepository counselingScheduleRepository;
    @Autowired
    private SatisfactionAnswerRepository satisfactionAnswerRepository;

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
                .department("경제학과")
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
                .requestContent("또 상담 신청합니다.")
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
        assert count == 1L;
    }

    @Test
    @DisplayName("테스트2 - 질문별 평균점수")
    void testGetAverageRatingByQuestion(){
        //Given: 테스트 데이터 준비
        CounselingSatisfaction testSatisfaction = new CounselingSatisfaction();
        testSatisfaction.setReservation(testReservation);
        testSatisfaction.setStudent(testStudent);
        testSatisfaction.setCounselor(testCounselor);
        counselingSatisfactionRepository.save(testSatisfaction);

        CounselingSatisfaction testSatisfaction2 = new CounselingSatisfaction();
        testSatisfaction2.setReservation(testReservation);
        testSatisfaction2.setStudent(testStudent);
        testSatisfaction2.setCounselor(testCounselor);
        counselingSatisfactionRepository.save(testSatisfaction2);

        SatisfactionAnswer testAnswer = new SatisfactionAnswer();
        testAnswer.setSatisfaction(testSatisfaction2);
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

        //Then: 결과 검증
        assert average == 4;

    }










}
