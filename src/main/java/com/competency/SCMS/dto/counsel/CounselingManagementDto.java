package com.competency.SCMS.dto.counsel;

import com.competency.SCMS.domain.counseling.CounselingField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CounselingManagementDto {
    
    // 상담분류 관리
    @Data
    public static class CategoryRequest {
        @NotNull(message = "상담 분야는 필수입니다")
        private CounselingField counselingField;
        @NotBlank(message = "하위 분야는 필수입니다")
        @Size(max = 100, message = "하위 분야는 100자를 초과할 수 없습니다")
        private String subfieldName;
        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
        private String description;
        private Boolean isActive;
    }
    
    @Data
    public static class CategoryResponse {
        private Long id;
        private CounselingField counselingField;
        private String subfieldName;
        private String description;
        private Boolean isActive;
    }
    
    // 상담원 관리
    @Data
    public static class CounselorRequest {
        @NotNull(message = "사용자 ID는 필수입니다")
        private Long userId;
        @NotNull(message = "상담 분야는 필수입니다")
        private CounselingField counselingField;
        private String specialization;
        private Boolean isActive;
    }
    
    @Data
    public static class CounselorResponse {
        private Long userId;
        private String name;
        private String email;
        private CounselingField counselingField;
        private String specialization;
        private Boolean isActive;
    }
    
    // 만족도 문항 관리
    @Data
    public static class QuestionRequest {
        @NotBlank(message = "문항 내용은 필수입니다")
        @Size(max = 1000, message = "질문은 1000자를 초과할 수 없습니다")
        private String questionText;
        @NotBlank(message = "문항 유형은 필수입니다")
        private String questionType;  // "RATING", "TEXT", "MULTIPLE_CHOICE"
        private CounselingField counselingField;
        private Long categoryId;
        @NotNull(message = "표시 순서는 필수입니다")
        @Min(value = 1, message = "표시 순서는 1 이상이어야 합니다")
        private Integer displayOrder;
        private Boolean isRequired;
        private Boolean isActive;
        @Valid
        private List<OptionRequest> options;
        
        @Data
        public static class OptionRequest {
            @NotBlank(message = "옵션 내용은 필수입니다")
            private String optionText;
            @NotNull(message = "옵션 값은 필수입니다")
            private Integer optionValue;
            @NotNull(message = "표시 순서는 필수입니다")
            private Integer displayOrder;
        }
    }
    
    @Data
    public static class QuestionResponse {
        private Long id;
        private String questionText;
        private String questionType;  // "RATING", "TEXT", "MULTIPLE_CHOICE"
        private CounselingField counselingField;
        private String subfieldName;
        private Integer displayOrder;
        private Boolean isRequired;
        private Boolean isSystemDefault;
        private Boolean isActive;
        private List<OptionResponse> options;
        
        @Data
        public static class OptionResponse {
            private Long id;
            private String optionText;
            private Integer optionValue;
            private Integer displayOrder;
        }
    }
}