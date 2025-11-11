package com.competency.scms.service.counsel;

import com.competency.scms.domain.counseling.*;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingSatisfactionDto;
import com.competency.scms.exception.BusinessException;
import com.competency.scms.exception.ErrorCode;
import com.competency.scms.repository.counseling.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingSatisfactionService {

    private final CounselingSatisfactionRepository satisfactionRepository;
    private final CounselingReservationRepository reservationRepository;
    private final SatisfactionQuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;

    // CNSL-005: 상담만족도 제출
    @Transactional
    public Long submitSatisfaction(CounselingSatisfactionDto.SubmitRequest request, User student) {
        CounselingReservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        if (!reservation.getStudent().getId().equals(student.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        
        // 이미 제출된 만족도가 있는지 확인
        if (satisfactionRepository.findByReservationId(request.getReservationId()).isPresent()) {
            throw new BusinessException(ErrorCode.SATISFACTION_ALREADY_SUBMITTED);
        }
        
        CounselingSatisfaction satisfaction = new CounselingSatisfaction();
        satisfaction.setReservation(reservation);
        satisfaction.setStudent(student);
        satisfaction.setCounselor(reservation.getCounselor());
        satisfaction.setSubmittedAt(LocalDateTime.now());
        
        List<SatisfactionAnswer> answers = new ArrayList<>();
        for (CounselingSatisfactionDto.SubmitRequest.AnswerRequest answerReq : request.getAnswers()) {
            SatisfactionQuestion question = questionRepository.findById(answerReq.getQuestionId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
            
            SatisfactionAnswer answer = new SatisfactionAnswer();
            answer.setSatisfaction(satisfaction);
            answer.setQuestion(question);
            answer.setAnswerText(answerReq.getAnswerText());
            answer.setRatingValue(answerReq.getRatingValue());
            
            if (answerReq.getSelectedOptionId() != null) {
                QuestionOption option = optionRepository.findById(answerReq.getSelectedOptionId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
                answer.setSelectedOption(option);
            }
            
            answers.add(answer);
        }
        
        satisfaction.setAnswers(answers);
        CounselingSatisfaction saved = satisfactionRepository.save(satisfaction);
        return saved.getId();
    }

    // 만족도 설문 조회
    public CounselingSatisfactionDto.SurveyResponse getSurvey(Long reservationId, User student) {
        CounselingReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        if (!reservation.getStudent().getId().equals(student.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        List<SatisfactionQuestion> questions = questionRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        
        CounselingSatisfactionDto.SurveyResponse response = new CounselingSatisfactionDto.SurveyResponse();
        response.setReservationId(reservationId);
        response.setCounselorName(reservation.getCounselor().getName());
        response.setQuestions(questions.stream().map(this::toQuestionResponse).collect(Collectors.toList()));
        
        return response;
    }

    private CounselingSatisfactionDto.SurveyResponse.QuestionResponse toQuestionResponse(SatisfactionQuestion question) {
        CounselingSatisfactionDto.SurveyResponse.QuestionResponse response = 
                new CounselingSatisfactionDto.SurveyResponse.QuestionResponse();
        response.setQuestionId(question.getId());
        response.setQuestionText(question.getQuestionText());
        response.setQuestionType(question.getQuestionType().name());
        response.setIsRequired(question.getIsRequired());
        
        if (question.getQuestionType() == SatisfactionQuestion.QuestionType.MULTIPLE_CHOICE) {
            response.setOptions(question.getOptions().stream()
                    .filter(QuestionOption::getIsActive)
                    .map(this::toOptionResponse)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }

    private CounselingSatisfactionDto.SurveyResponse.QuestionResponse.OptionResponse toOptionResponse(QuestionOption option) {
        CounselingSatisfactionDto.SurveyResponse.QuestionResponse.OptionResponse response = 
                new CounselingSatisfactionDto.SurveyResponse.QuestionResponse.OptionResponse();
        response.setOptionId(option.getId());
        response.setOptionText(option.getOptionText());
        response.setOptionValue(option.getOptionValue());
        return response;
    }
}
