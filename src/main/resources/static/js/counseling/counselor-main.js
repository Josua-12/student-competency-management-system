let currentDate = new Date();

document.addEventListener('DOMContentLoaded', async function() {
    await loadDashboardData();
    await loadSchedule();
    
    document.getElementById('prevDay').addEventListener('click', function() {
        currentDate.setDate(currentDate.getDate() - 1);
        updateDateDisplay();
        loadSchedule();
    });
    
    document.getElementById('nextDay').addEventListener('click', function() {
        currentDate.setDate(currentDate.getDate() + 1);
        updateDateDisplay();
        loadSchedule();
    });
    
    updateDateDisplay();
});

async function loadDashboardData() {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch('/api/counseling/history/status', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            document.getElementById('pendingReservations').textContent = `${data.pendingCount || 0}건`;
            document.getElementById('todayCounseling').textContent = `${data.todayCount || data.completedCount || 0}건`;
            document.getElementById('monthlyCounseling').textContent = `${data.monthlyCount || data.totalCount || 0}건`;
            document.getElementById('avgSatisfaction').textContent = `${data.avgSatisfaction || 0.0}/5.0`;
        }
    } catch (error) {
        console.error('대시보드 데이터 로드 실패:', error);
    }
}

async function loadSchedule() {
    const token = localStorage.getItem('accessToken');
    const targetDate = currentDate.toISOString().split('T')[0];
    
    try {
        const response = await fetch(`/api/counseling/reservations/assigned`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const result = await response.json();
            const data = (result.content || []).filter(item => 
                item.confirmedDate === targetDate && (item.status === 'CONFIRMED' || item.status === 'COMPLETED')
            );
            const tbody = document.getElementById('scheduleTableBody');
            
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center">일정이 없습니다.</td></tr>';
            } else {
                tbody.innerHTML = data.map(item => {
                    const statusBadge = item.status === 'CONFIRMED' ? 'bg-success' : 'bg-primary';
                    const statusText = item.status === 'CONFIRMED' ? '예정' : '완료';
                    
                    return `
                        <tr>
                            <td>${formatTime(item.confirmedStartTime)} - ${formatTime(item.confirmedEndTime)}</td>
                            <td>${item.studentName}</td>
                            <td>${getFieldName(item.counselingField)}</td>
                            <td><span class="badge ${statusBadge}">${statusText}</span></td>
                            <td><button class="btn btn-sm btn-outline-primary" onclick="showScheduleDetail(${item.id})">상세</button></td>
                        </tr>
                    `;
                }).join('');
            }
        }
    } catch (error) {
        console.error('일정 로드 실패:', error);
    }
}

function updateDateDisplay() {
    const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'short' };
    const dateStr = currentDate.toLocaleDateString('ko-KR', options);
    document.getElementById('currentDate').textContent = dateStr;
}

async function loadTodaySchedule() {
    currentDate = new Date();
    updateDateDisplay();
    await loadSchedule();
}

function getFieldName(field) {
    const fieldNames = {
        'PSYCHOLOGICAL': '심리상담',
        'CAREER': '진로상담',
        'EMPLOYMENT': '취업상담',
        'LEARNING': '학습상담'
    };
    return fieldNames[field] || field;
}

function formatTime(timeStr) {
    if (!timeStr) return '-';
    return timeStr.substring(0, 5);
}

async function completeReservation(reservationId) {
    if (!confirm('상담을 완료 처리하시겠습니까?')) return;
    
    const token = localStorage.getItem('accessToken');
    const formData = new FormData();
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}/complete`, {
            method: 'POST',
            headers: {'Authorization': `Bearer ${token}`},
            body: formData
        });
        
        if (response.ok) {
            alert('상담이 완료 처리되었습니다.');
            await loadSchedule();
            await loadDashboardData();
        } else {
            alert('완료 처리 중 오류가 발생했습니다.');
        }
    } catch (error) {
        console.error('완료 처리 실패:', error);
        alert('완료 처리 중 오류가 발생했습니다.');
    }
}

async function showScheduleDetail(reservationId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const detail = await response.json();
            renderScheduleDetail(detail);
            const modal = new bootstrap.Modal(document.getElementById('scheduleDetailModal'));
            modal.show();
        }
    } catch (error) {
        console.error('상세 정보 로드 실패:', error);
    }
}

function renderScheduleDetail(detail) {
    document.getElementById('scheduleDetailStudentName').textContent = detail.studentName || '-';
    document.getElementById('scheduleDetailField').textContent = getFieldName(detail.counselingField);
    document.getElementById('scheduleDetailCategory').textContent = detail.subFieldName || '-';
    
    const dateTime = detail.confirmedDate && detail.confirmedStartTime ? 
        `${detail.confirmedDate} ${detail.confirmedStartTime}` : '-';
    document.getElementById('scheduleDetailDateTime').textContent = dateTime;
    
    document.getElementById('scheduleDetailStatus').textContent = getStatusText(detail.status);
    document.getElementById('scheduleDetailStatus').className = `badge ${getStatusBadge(detail.status)}`;
    document.getElementById('scheduleDetailContent').textContent = detail.requestContent || '-';
    
    // 완료 버튼 표시
    const completeSection = document.getElementById('scheduleCompleteSection');
    if (detail.status === 'CONFIRMED') {
        completeSection.style.display = 'block';
        document.getElementById('scheduleCompleteBtn').onclick = () => completeFromSchedule(detail.id);
    } else {
        completeSection.style.display = 'none';
    }
}

function getStatusText(status) {
    const statusMap = {
        'PENDING': '대기중',
        'CONFIRMED': '확정',
        'COMPLETED': '완료',
        'CANCELLED': '취소',
        'REJECTED': '거절'
    };
    return statusMap[status] || status;
}

function getStatusBadge(status) {
    const badgeMap = {
        'PENDING': 'bg-warning',
        'CONFIRMED': 'bg-success',
        'COMPLETED': 'bg-primary',
        'CANCELLED': 'bg-secondary',
        'REJECTED': 'bg-danger'
    };
    return badgeMap[status] || 'bg-secondary';
}

async function completeFromSchedule(reservationId) {
    if (!confirm('상담을 완료 처리하시겠습니까?')) return;
    
    const token = localStorage.getItem('accessToken');
    const fileInput = document.getElementById('scheduleCompleteFiles');
    const formData = new FormData();
    
    if (fileInput.files.length > 0) {
        for (let i = 0; i < fileInput.files.length; i++) {
            formData.append('files', fileInput.files[i]);
        }
    }
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}/complete`, {
            method: 'POST',
            headers: {'Authorization': `Bearer ${token}`},
            body: formData
        });
        
        if (response.ok) {
            alert('상담이 완료 처리되었습니다.');
            const modal = bootstrap.Modal.getInstance(document.getElementById('scheduleDetailModal'));
            if (modal) modal.hide();
            document.body.classList.remove('modal-open');
            document.querySelector('.modal-backdrop')?.remove();
            await loadSchedule();
            await loadDashboardData();
        } else {
            alert('완료 처리 중 오류가 발생했습니다.');
        }
    } catch (error) {
        console.error('완료 처리 실패:', error);
        alert('완룼 처리 중 오류가 발생했습니다.');
    }
}

// 전역 함수로 등록
window.loadTodaySchedule = loadTodaySchedule;
window.loadDashboardData = loadDashboardData;
window.completeReservation = completeReservation;
window.loadSchedule = loadSchedule;
window.showScheduleDetail = showScheduleDetail;