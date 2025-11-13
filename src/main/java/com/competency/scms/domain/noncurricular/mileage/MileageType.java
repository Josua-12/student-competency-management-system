package com.competency.scms.domain.noncurricular.mileage;

/** 마일리지 거래유형 */
public enum MileageType {
    EARN,   // 부여
    USE,    // 차감
    ADJUST; // 조정

    public int apply(int current, int points) {
        return switch (this) {
            case EARN   -> current + points;
            case USE    -> current - points;   // 차감은 양수 입력 후 내부에서 - 처리
            case ADJUST -> current + points;   // 조정은 +/− 포함
        };
    }
}

