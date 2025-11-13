package com.competency.scms.service.counsel;

import com.competency.scms.domain.counseling.CounselingReservation;
import com.competency.scms.dto.counsel.CounselingReservationDto;

public class CounselingMapper {

    public CounselingReservationDto.ListResponse toListResponse(CounselingReservation reservation) {
        var response = new CounselingReservationDto.ListResponse();
        response.setId(reservation.getId());
        response.setStudentName(reservation.getStudent().getName());
        response.setCounselingField(reservation.getCounselingField());
        response.setSubFieldName(reservation.getSubField().getSubfieldName());
        response.setReservationDate(reservation.getReservationDate());
        response.setStartTime(reservation.getStartTime());
        response.setEndTime(reservation.getEndTime());
        response.setConfirmedDate(reservation.getConfirmedDate());
        response.setConfirmedStartTime(reservation.getConfirmedStartTime());
        response.setConfirmedEndTime(reservation.getConfirmedEndTime());
        response.setStatus(reservation.getStatus());
        response.setCounselorName(reservation.getCounselor() != null ? reservation.getCounselor().getName() : null);
        return response;
    }

    public CounselingReservationDto.DetailResponse toDetailResponse(CounselingReservation reservation) {
        var response = new CounselingReservationDto.DetailResponse();
        response.setId(reservation.getId());
        response.setStudentName(reservation.getStudent().getName());
        response.setCounselingField(reservation.getCounselingField());
        response.setSubFieldName(reservation.getSubField().getSubfieldName());
        response.setReservationDate(reservation.getReservationDate());
        response.setStartTime(reservation.getStartTime());
        response.setEndTime(reservation.getEndTime());
        response.setConfirmedDate(reservation.getConfirmedDate());
        response.setConfirmedStartTime(reservation.getConfirmedStartTime());
        response.setConfirmedEndTime(reservation.getConfirmedEndTime());
        response.setRequestContent(reservation.getRequestContent());
        response.setStatus(reservation.getStatus());
        response.setCounselorName(reservation.getCounselor() != null ? reservation.getCounselor().getName() : null);
        response.setMemo(reservation.getMemo());
        response.setRejectReason(reservation.getRejectReason());
        response.setCancelReason(reservation.getCancelReason());
        return response;
    }
}