package com.competency.scms.service.noncurricular.noncurriDashboard;

import com.competency.scms.dto.noncurricular.noncurriDashboard.student.StudentDashboardResponse;

public interface StudentDashboardService {

    StudentDashboardResponse getDashboard(Long studentId);
}

