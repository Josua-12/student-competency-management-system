package com.competency.SCMS.domain.counseling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CounselingOffReason {

    ANNUAL_LEAVE("연차휴가"),
    HALF_DAY_LEAVE("반차"),
    SICK_LEAVE("병가"),
    FAMILY_EVENT_LEAVE("경조휴가"),
    MATERNITY_LEAVE("출산휴가"),
    PARENTAL_LEAVE("육아휴직"),
    OFFICIAL_LEAVE("공가"),
    ETC("기타 개인사정");

    private final String displayName;

}
