package com.competency.SCMS.domain.counseling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("대기중"),          // 학생이 신청, 상담사 배정 대기
    CONFIRMED("확정"),          // 상담사 배정 및 확정
    COMPLETED("완료"),          // 상담 완료
    CANCELLED("취소"),          // 학생이 취소
    REJECTED("거절"),           // 관리자(상담사 배정 전) 및 상담사(예약시 상담사 배정된 경우)가 거절
    NO_SHOW("미출석");          // 예약 시간에 불참

    private final String displayName;


}