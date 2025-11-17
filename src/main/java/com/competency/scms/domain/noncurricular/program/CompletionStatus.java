package com.competency.scms.domain.noncurricular.program;

/**
 * 학생 마이페이지 - 이수내역 화면용 View 전용 상태
 * (신청상태 + 프로그램 상태를 조합해서 산출)
 */
public enum CompletionStatus {
    COMPLETED,      // 이수완료
    IN_PROGRESS,    // 이수중
    NOT_COMPLETED   // 미이수 (반려/취소 등)
}

