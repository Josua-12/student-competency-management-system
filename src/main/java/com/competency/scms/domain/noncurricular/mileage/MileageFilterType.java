package com.competency.scms.domain.noncurricular.mileage;

/**
 * 이수내역 조회 시 포인트 유/무 필터
 */
public enum MileageFilterType {
    ALL,        // 전체
    HAS_POINT,  // 포인트 > 0
    NO_POINT    // 포인트 = 0
}
