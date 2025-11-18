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
        
        // 시스템 기본 질문 + 해당 상담 분야 전용 질문만 조회
        List<SatisfactionQuestion> questions = questionRepository.findByIsActiveTrueAndCounselingFieldIsNullOrCounselingFieldOrderByDisplayOrderAsc(reservation.getCounselingField());
        
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

    // 제출된 만족도 조회
    public CounselingSatisfactionDto.ResultResponse getResult(Long reservationId, User user) {
        CounselingSatisfaction satisfaction = satisfactionRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SATISFACTION_NOT_FOUND));
        
        // 학생 본인이거나 담당 상담사인 경우 조회 가능
        if (!satisfaction.getStudent().getId().equals(user.getId()) && 
            !satisfaction.getCounselor().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        CounselingSatisfactionDto.ResultResponse response = new CounselingSatisfactionDto.ResultResponse();
        response.setSatisfactionId(satisfaction.getId());
        response.setReservationId(reservationId);
        response.setCounselorName(satisfaction.getCounselor().getName());
        response.setSubmittedAt(satisfaction.getSubmittedAt());
        response.setAnswers(satisfaction.getAnswers().stream().map(this::toAnswerResponse).collect(Collectors.toList()));
        
        return response;
    }

    private CounselingSatisfactionDto.ResultResponse.AnswerResponse toAnswerResponse(SatisfactionAnswer answer) {
        CounselingSatisfactionDto.ResultResponse.AnswerResponse response = 
                new CounselingSatisfactionDto.ResultResponse.AnswerResponse();
        response.setQuestionId(answer.getQuestion().getId());
        response.setQuestionText(answer.getQuestion().getQuestionText());
        response.setQuestionType(answer.getQuestion().getQuestionType().name());
        response.setAnswerText(answer.getAnswerText());
        response.setRatingValue(answer.getRatingValue());
        if (answer.getSelectedOption() != null) {
            response.setSelectedOptionId(answer.getSelectedOption().getId());
            response.setSelectedOptionText(answer.getSelectedOption().getOptionText());
        }
        return response;
    }

    // 만족도 수정
    @Transactional
    public void updateSatisfaction(Long satisfactionId, CounselingSatisfactionDto.SubmitRequest request, User student) {
        CounselingSatisfaction satisfaction = satisfactionRepository.findById(satisfactionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SATISFACTION_NOT_FOUND));
        
        if (!satisfaction.getStudent().getId().equals(student.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        List<SatisfactionAnswer> newAnswers = new ArrayList<>();
        
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
            
            newAnswers.add(answer);
        }
        
        satisfaction.getAnswers().clear();
        satisfactionRepository.flush();
        satisfaction.getAnswers().addAll(newAnswers);
        satisfaction.setSubmittedAt(LocalDateTime.now());
    }
    
    // 상담사 만족도 요약
    public CounselingSatisfactionDto.SummaryResponse getCounselorSummary(User counselor) {
        List<CounselingSatisfaction> satisfactions = satisfactionRepository.findByCounselor(counselor);
        
        CounselingSatisfactionDto.SummaryResponse response = new CounselingSatisfactionDto.SummaryResponse();
        
        if (satisfactions.isEmpty()) {
            response.setAvgSatisfaction(0.0);
            response.setResponseRate(0.0);
            response.setTotalResponses(0L);
            response.setReusageRate(0.0);
            return response;
        }
        
        // 평균 만족도 계산
        double totalAvg = 0.0;
        int validSatisfactionCount = 0;
        
        for (CounselingSatisfaction satisfaction : satisfactions) {
            List<SatisfactionAnswer> ratingAnswers = satisfaction.getAnswers().stream()
                .filter(answer -> answer.getQuestion().getQuestionType() == SatisfactionQuestion.QuestionType.RATING)
                .filter(answer -> answer.getRatingValue() != null)
                .toList();
            
            if (!ratingAnswers.isEmpty()) {
                double satisfactionAvg = ratingAnswers.stream()
                    .mapToInt(SatisfactionAnswer::getRatingValue)
                    .average()
                    .orElse(0.0);
                
                totalAvg += satisfactionAvg;
                validSatisfactionCount++;
            }
        }
        
        double avgSatisfaction = validSatisfactionCount > 0 ? 
            Math.round((totalAvg / validSatisfactionCount) * 10.0) / 10.0 : 0.0;
        
        // 전체 상담 건수 대비 응답률
        long totalCompletedReservations = reservationRepository.countByCounselorAndStatus(
            counselor, ReservationStatus.COMPLETED);
        double responseRate = totalCompletedReservations > 0 ? 
            Math.round((double) satisfactions.size() / totalCompletedReservations * 100.0 * 10.0) / 10.0 : 0.0;
        
        response.setAvgSatisfaction(avgSatisfaction);
        response.setResponseRate(responseRate);
        response.setTotalResponses((long) satisfactions.size());
        response.setReusageRate(0.0); // 재이용 의향은 별도 계산 필요
        
        return response;
    }
    
    // 상담사 만족도 분포
    public CounselingSatisfactionDto.DistributionResponse getCounselorDistribution(User counselor) {
        List<CounselingSatisfaction> satisfactions = satisfactionRepository.findByCounselor(counselor);
        
        long score5Count = 0, score4Count = 0, score3Count = 0, score2Count = 0, score1Count = 0;
        
        for (CounselingSatisfaction satisfaction : satisfactions) {
            List<SatisfactionAnswer> ratingAnswers = satisfaction.getAnswers().stream()
                .filter(answer -> answer.getQuestion().getQuestionType() == SatisfactionQuestion.QuestionType.RATING)
                .filter(answer -> answer.getRatingValue() != null)
                .toList();
            
            for (SatisfactionAnswer answer : ratingAnswers) {
                switch (answer.getRatingValue()) {
                    case 5: score5Count++; break;
                    case 4: score4Count++; break;
                    case 3: score3Count++; break;
                    case 2: score2Count++; break;
                    case 1: score1Count++; break;
                }
            }
        }
        
        long totalCount = score5Count + score4Count + score3Count + score2Count + score1Count;
        
        CounselingSatisfactionDto.DistributionResponse response = new CounselingSatisfactionDto.DistributionResponse();
        response.setScore5Count(score5Count);
        response.setScore4Count(score4Count);
        response.setScore3Count(score3Count);
        response.setScore2Count(score2Count);
        response.setScore1Count(score1Count);
        
        if (totalCount > 0) {
            response.setScore5Percent(Math.round((double) score5Count / totalCount * 100.0 * 10.0) / 10.0);
            response.setScore4Percent(Math.round((double) score4Count / totalCount * 100.0 * 10.0) / 10.0);
            response.setScore3Percent(Math.round((double) score3Count / totalCount * 100.0 * 10.0) / 10.0);
            response.setScore2Percent(Math.round((double) score2Count / totalCount * 100.0 * 10.0) / 10.0);
            response.setScore1Percent(Math.round((double) score1Count / totalCount * 100.0 * 10.0) / 10.0);
        } else {
            response.setScore5Percent(0.0);
            response.setScore4Percent(0.0);
            response.setScore3Percent(0.0);
            response.setScore2Percent(0.0);
            response.setScore1Percent(0.0);
        }
        
        return response;
    }
    
    // 상담사 월별 만족도 추이
    public CounselingSatisfactionDto.MonthlyResponse getCounselorMonthly(User counselor) {
        List<CounselingSatisfaction> satisfactions = satisfactionRepository.findByCounselorOrderBySubmittedAtDesc(counselor);
        
        java.util.Map<String, java.util.List<Double>> monthlyData = new java.util.LinkedHashMap<>();
        
        for (CounselingSatisfaction satisfaction : satisfactions) {
            String month = satisfaction.getSubmittedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
            
            List<SatisfactionAnswer> ratingAnswers = satisfaction.getAnswers().stream()
                .filter(answer -> answer.getQuestion().getQuestionType() == SatisfactionQuestion.QuestionType.RATING)
                .filter(answer -> answer.getRatingValue() != null)
                .toList();
            
            if (!ratingAnswers.isEmpty()) {
                double satisfactionAvg = ratingAnswers.stream()
                    .mapToInt(SatisfactionAnswer::getRatingValue)
                    .average()
                    .orElse(0.0);
                
                monthlyData.computeIfAbsent(month, k -> new java.util.ArrayList<>()).add(satisfactionAvg);
            }
        }
        
        java.util.List<String> months = new java.util.ArrayList<>();
        java.util.List<Double> avgSatisfactions = new java.util.ArrayList<>();
        
        monthlyData.entrySet().stream()
            .sorted(java.util.Map.Entry.comparingByKey())
            .forEach(entry -> {
                months.add(entry.getKey());
                double monthAvg = entry.getValue().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
                avgSatisfactions.add(Math.round(monthAvg * 10.0) / 10.0);
            });
        
        CounselingSatisfactionDto.MonthlyResponse response = new CounselingSatisfactionDto.MonthlyResponse();
        response.setMonths(months);
        response.setAvgSatisfactions(avgSatisfactions);
        
        return response;
    }
}
