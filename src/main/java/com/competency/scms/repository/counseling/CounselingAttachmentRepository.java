package com.competency.scms.repository.counseling;

import com.competency.scms.domain.counseling.CounselingAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CounselingAttachmentRepository extends JpaRepository<CounselingAttachment, Long> {
    List<CounselingAttachment> findByReservationId(Long reservationId);
}
