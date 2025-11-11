package com.competency.scms.service.counsel;

import com.competency.scms.domain.counseling.*;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingManagementDto;
import com.competency.scms.exception.BusinessException;
import com.competency.scms.exception.ErrorCode;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.repository.counseling.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingManagementService {

    private final CounselingSubFieldRepository subFieldRepository;
    private final CounselorRepository counselorRepository;
    private final SatisfactionQuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final UserRepository userRepository;

    // CNSL-022: 상담분류관리
    @Transactional
    public Long createCategory(CounselingManagementDto.CategoryRequest request) {
        CounselingSubField category = new CounselingSubField();
        category.setCounselingField(request.getCounselingField());
        category.setSubfieldName(request.getSubfieldName());
        category.setDescription(request.getDescription());
        category.setIsActive(true);
        
        CounselingSubField saved = subFieldRepository.save(category);
        return saved.getId();
    }

    @Transactional
    public void updateCategory(Long categoryId, CounselingManagementDto.CategoryRequest request) {
        CounselingSubField category = subFieldRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        
        category.setCounselingField(request.getCounselingField());
        category.setSubfieldName(request.getSubfieldName());
        category.setDescription(request.getDescription());
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        CounselingSubField category = subFieldRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        
        category.setIsActive(false);
    }

    public List<CounselingManagementDto.CategoryResponse> getAllCategories() {
        List<CounselingSubField> categories = subFieldRepository.findAll();
        return categories.stream().map(this::toCategoryResponse).collect(Collectors.toList());
    }

    // CNSL-023: 상담원관리
    @Transactional
    public void createCounselor(CounselingManagementDto.CounselorRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        Counselor counselor = new Counselor();
        counselor.setCounselorId(user.getId());
        counselor.setCounselingField(request.getCounselingField());
        counselor.setSpecialization(request.getSpecialization());
        counselor.setIsActive(true);
        
        counselorRepository.save(counselor);
    }

    @Transactional
    public void updateCounselor(Long userId, CounselingManagementDto.CounselorRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        Counselor counselor = counselorRepository.findByCounselorId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COUNSELOR_NOT_FOUND));
        
        counselor.setCounselingField(request.getCounselingField());
        counselor.setSpecialization(request.getSpecialization());
    }

    @Transactional
    public void deleteCounselor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        Counselor counselor = counselorRepository.findByCounselorId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COUNSELOR_NOT_FOUND));
        
        counselor.setIsActive(false);
    }

    public Page<CounselingManagementDto.CounselorResponse> getAllCounselors(Pageable pageable) {
        Page<Counselor> counselors = counselorRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
        return counselors.map(this::toCounselorResponse);
    }

    // CNSL-024: 상담만족도 문항관리
    @Transactional
    public Long createQuestion(CounselingManagementDto.QuestionRequest request) {
        SatisfactionQuestion question = new SatisfactionQuestion();
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(SatisfactionQuestion.QuestionType.valueOf(request.getQuestionType()));
        question.setCounselingField(request.getCounselingField());
        question.setDisplayOrder(request.getDisplayOrder());
        question.setIsRequired(request.getIsRequired());
        question.setIsActive(true);
        question.setIsSystemDefault(false);
        
        SatisfactionQuestion saved = questionRepository.save(question);
        
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            List<QuestionOption> options = new ArrayList<>();
            for (CounselingManagementDto.QuestionRequest.OptionRequest optionReq : request.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setQuestion(saved);
                option.setOptionText(optionReq.getOptionText());
                option.setOptionValue(optionReq.getOptionValue());
                option.setDisplayOrder(optionReq.getDisplayOrder());
                option.setIsActive(true);
                options.add(option);
            }
            optionRepository.saveAll(options);
        }
        
        return saved.getId();
    }

    @Transactional
    public void updateQuestion(Long questionId, CounselingManagementDto.QuestionRequest request) {
        SatisfactionQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        
        if (question.getIsSystemDefault()) {
            throw new BusinessException(ErrorCode.CANNOT_MODIFY_SYSTEM_QUESTION);
        }
        
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(SatisfactionQuestion.QuestionType.valueOf(request.getQuestionType()));
        question.setCounselingField(request.getCounselingField());
        question.setDisplayOrder(request.getDisplayOrder());
        question.setIsRequired(request.getIsRequired());
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        SatisfactionQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        
        if (question.getIsSystemDefault()) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_SYSTEM_QUESTION);
        }
        
        question.setIsActive(false);
    }

    public List<CounselingManagementDto.QuestionResponse> getAllQuestions() {
        List<SatisfactionQuestion> questions = questionRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        return questions.stream().map(this::toQuestionResponse).collect(Collectors.toList());
    }

    private CounselingManagementDto.CategoryResponse toCategoryResponse(CounselingSubField category) {
        CounselingManagementDto.CategoryResponse response = new CounselingManagementDto.CategoryResponse();
        response.setId(category.getId());
        response.setCounselingField(category.getCounselingField());
        response.setSubfieldName(category.getSubfieldName());
        response.setDescription(category.getDescription());
        response.setIsActive(category.getIsActive());
        return response;
    }

    private CounselingManagementDto.CounselorResponse toCounselorResponse(Counselor counselor) {
        CounselingManagementDto.CounselorResponse response = new CounselingManagementDto.CounselorResponse();
        response.setUserId(counselor.getCounselorId());
        response.setName(counselor.getUser().getName());
        response.setEmail(counselor.getUser().getEmail());
        response.setCounselingField(counselor.getCounselingField());
        response.setSpecialization(counselor.getSpecialization());
        response.setIsActive(counselor.getIsActive());
        return response;
    }

    private CounselingManagementDto.QuestionResponse toQuestionResponse(SatisfactionQuestion question) {
        CounselingManagementDto.QuestionResponse response = new CounselingManagementDto.QuestionResponse();
        response.setId(question.getId());
        response.setQuestionText(question.getQuestionText());
        response.setQuestionType(question.getQuestionType().name());
        response.setCounselingField(question.getCounselingField());
        response.setDisplayOrder(question.getDisplayOrder());
        response.setIsRequired(question.getIsRequired());
        response.setIsSystemDefault(question.getIsSystemDefault());
        
        if (question.getQuestionType() == SatisfactionQuestion.QuestionType.MULTIPLE_CHOICE) {
            response.setOptions(question.getOptions().stream()
                    .filter(QuestionOption::getIsActive)
                    .map(this::toOptionResponse)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }

    private CounselingManagementDto.QuestionResponse.OptionResponse toOptionResponse(QuestionOption option) {
        CounselingManagementDto.QuestionResponse.OptionResponse response = 
                new CounselingManagementDto.QuestionResponse.OptionResponse();
        response.setId(option.getId());
        response.setOptionText(option.getOptionText());
        response.setOptionValue(option.getOptionValue());
        response.setDisplayOrder(option.getDisplayOrder());
        return response;
    }
}
