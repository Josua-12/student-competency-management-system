package com.competency.scms.domain.noncurricular.program;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 학생 마이페이지 - 이수내역 화면용 View 전용 상태
 * (신청상태 + 프로그램 상태를 조합해서 산출)
 */
@Getter
@RequiredArgsConstructor
public enum CompletionStatus {

    COMPLETED("이수완료"),
    NOT_COMPLETED("미이수 (반려/취소 등)"),
    IN_PROGRESS("이수중");

    private final String label;
}
