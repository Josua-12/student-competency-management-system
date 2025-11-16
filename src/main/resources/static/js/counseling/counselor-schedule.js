let currentWeekStart = new Date();

// 페이지 로드 시 데이터 로드
document.addEventListener('DOMContentLoaded', function() {
    loadBaseSchedule();
    loadOffRequests();
    loadConfirmedReservations();
    loadWeeklySchedule();
});

// 기본 근무시간표 로드
async function loadBaseSchedule() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/api/counseling/schedule/my-schedule', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const schedules = await response.json();
            renderBaseSchedule(schedules);
        }
    } catch (error) {
        console.error('기본 근무시간표 로드 실패:', error);
    }
}

function renderBaseSchedule(schedules) {
    const tbody = document.querySelector('.card:nth-of-type(1) tbody');
    const timeSlots = [
        {label: '09:00-10:00', key: 'slot0910'},
        {label: '10:00-11:00', key: 'slot1011'},
        {label: '11:00-12:00', key: 'slot1112'},
        {label: '14:00-15:00', key: 'slot1415'},
        {label: '15:00-16:00', key: 'slot1516'}
    ];
    
    const dayMap = {0: 'mon', 1: 'tue', 2: 'wed', 3: 'thu', 4: 'fri'};
    const scheduleMap = {};
    
    schedules.forEach(s => {
        const day = dayMap[s.dayOfWeek];
        scheduleMap[day] = s;
    });
    
    tbody.innerHTML = timeSlots.map(slot => {
        const cells = ['mon', 'tue', 'wed', 'thu', 'fri'].map(day => {
            const schedule = scheduleMap[day];
            const isWorking = schedule && schedule[slot.key];
            return isWorking ? 
                '<td class="bg-success text-white text-center">근무</td>' :
                '<td class="bg-light text-center">-</td>';
        }).join('');
        
        return `<tr><td>${slot.label}</td>${cells}</tr>`;
    }).join('');
}

// 휴무 신청 내역 로드
async function loadOffRequests() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/api/counseling/schedules/my-off-requests', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const requests = await response.json();
            renderOffRequests(requests);
        }
    } catch (error) {
        console.error('휴무 신청 내역 로드 실패:', error);
    }
}

function renderOffRequests(requests) {
    const tbody = document.querySelector('.card:nth-of-type(2) tbody');
    
    if (requests.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">휴무 신청 내역이 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = requests.map(req => {
        const statusBadge = req.status === 'PENDING' ? 'bg-warning' : 
                           req.status === 'APPROVED' ? 'bg-success' : 'bg-danger';
        const statusText = req.status === 'PENDING' ? '대기중' : 
                          req.status === 'APPROVED' ? '승인됨' : '거부됨';
        
        const buttons = req.status === 'PENDING' ? 
            `<button class="btn btn-sm btn-outline-primary" onclick="editOffRequest(${req.id})">수정</button>
             <button class="btn btn-sm btn-outline-danger" onclick="cancelOffRequest(${req.id})">취소</button>` :
            `<button class="btn btn-sm btn-outline-info" onclick="viewOffRequest(${req.id})">상세</button>`;
        
        return `
            <tr>
                <td>${formatDate(req.createdAt)}</td>
                <td>${formatDate(req.startDate)} ~ ${formatDate(req.endDate)}</td>
                <td>${req.reason}</td>
                <td><span class="badge ${statusBadge}">${statusText}</span></td>
                <td>${buttons}</td>
            </tr>
        `;
    }).join('');
}

// 주간 일정 로드
async function loadWeeklySchedule() {
    const weekStart = getWeekStart(currentWeekStart);
    const weekEnd = new Date(weekStart);
    weekEnd.setDate(weekEnd.getDate() + 4);
    
    document.getElementById('currentWeek').textContent = 
        `${weekStart.getFullYear()}년 ${weekStart.getMonth() + 1}월 ${weekStart.getDate()}일 - ${weekEnd.getMonth() + 1}월 ${weekEnd.getDate()}일`;
    
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`/api/counseling/schedules/weekly?startDate=${formatDateParam(weekStart)}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            renderWeeklySchedule(data, weekStart);
        }
    } catch (error) {
        console.error('주간 일정 로드 실패:', error);
    }
}

function renderWeeklySchedule(data, weekStart) {
    const thead = document.querySelector('.card:nth-of-type(3) thead tr');
    const tbody = document.querySelector('.card:nth-of-type(3) tbody');
    
    const days = ['월요일', '화요일', '수요일', '목요일', '금요일'];
    thead.innerHTML = '<th width="100">시간</th>' + days.map((day, i) => {
        const date = new Date(weekStart);
        date.setDate(date.getDate() + i);
        return `<th>${day}<br><small>${date.getMonth() + 1}/${date.getDate()}</small></th>`;
    }).join('');
    
    const timeSlots = [
        {label: '09:00-10:00', start: '09:00', end: '10:00'},
        {label: '10:00-11:00', start: '10:00', end: '11:00'},
        {label: '11:00-12:00', start: '11:00', end: '12:00'},
        {label: '14:00-15:00', start: '14:00', end: '15:00'},
        {label: '15:00-16:00', start: '15:00', end: '16:00'}
    ];
    
    tbody.innerHTML = timeSlots.map(slot => {
        const cells = days.map((_, i) => {
            const date = new Date(weekStart);
            date.setDate(date.getDate() + i);
            const dateStr = formatDateParam(date);
            const slotData = data.find(d => d.date === dateStr && d.startTime === slot.start);
            
            if (!slotData) return '<td class="bg-light text-center">-</td>';
            if (slotData.isOff) return `<td class="bg-danger text-white text-center">휴무</td>`;
            if (slotData.isReserved) return `<td class="bg-warning text-center">예약됨<br><small>${slotData.studentName || ''}</small></td>`;
            return '<td class="bg-success text-white text-center">근무가능</td>';
        }).join('');
        
        return `<tr><td>${slot.label}</td>${cells}</tr>`;
    }).join('');
}

// 주간 네비게이션
document.getElementById('prevWeek')?.addEventListener('click', function() {
    currentWeekStart = new Date(currentWeekStart);
    currentWeekStart.setDate(currentWeekStart.getDate() - 7);
    loadWeeklySchedule();
});

document.getElementById('nextWeek')?.addEventListener('click', function() {
    currentWeekStart = new Date(currentWeekStart);
    currentWeekStart.setDate(currentWeekStart.getDate() + 7);
    loadWeeklySchedule();
});

// 휴무 신청
document.getElementById('submitOffRequest').addEventListener('click', async function() {
    const modal = document.getElementById('offScheduleModal');
    const reason = modal.querySelector('select').value;
    const startDate = modal.querySelectorAll('input[type="date"]')[0].value;
    const endDate = modal.querySelectorAll('input[type="date"]')[1].value;
    const detailReason = modal.querySelector('textarea').value;
    
    if (!reason || !startDate || !endDate) {
        alert('필수 항목을 모두 입력해주세요.');
        return;
    }
    
    if (new Date(startDate) > new Date(endDate)) {
        alert('종료일은 시작일보다 빠를 수 없습니다.');
        return;
    }
    
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/api/counseling/schedules/off-requests', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({reason, startDate, endDate, detailReason})
        });
        
        if (response.ok) {
            alert('휴무 신청이 완료되었습니다.');
            bootstrap.Modal.getInstance(modal).hide();
            modal.querySelector('select').value = '';
            modal.querySelectorAll('input[type="date"]').forEach(input => input.value = '');
            modal.querySelector('textarea').value = '';
            loadOffRequests();
            loadWeeklySchedule();
        } else {
            const error = await response.json();
            alert(error.message || '휴무 신청에 실패했습니다.');
        }
    } catch (error) {
        console.error('휴무 신청 실패:', error);
        alert('휴무 신청 중 오류가 발생했습니다.');
    }
});

async function cancelOffRequest(id) {
    if (!confirm('휴무 신청을 취소하시겠습니까?')) return;
    
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`/api/counseling/schedules/off-requests/${id}`, {
            method: 'DELETE',
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            alert('취소되었습니다.');
            loadOffRequests();
            loadWeeklySchedule();
        } else {
            const error = await response.json();
            alert(error.message || '취소에 실패했습니다.');
        }
    } catch (error) {
        console.error('취소 실패:', error);
        alert('취소 중 오류가 발생했습니다.');
    }
}

function editOffRequest(id) {
    alert('수정 기능은 추후 구현 예정입니다.');
}

function viewOffRequest(id) {
    alert('상세 보기 기능은 추후 구현 예정입니다.');
}

function getWeekStart(date) {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(d.setDate(diff));
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR');
}

function formatDateParam(date) {
    return date.toISOString().split('T')[0];
}

// 확정된 예약 목록 로드
async function loadConfirmedReservations() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/api/counseling/reservations/assigned', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            renderConfirmedReservations(data.content || []);
        }
    } catch (error) {
        console.error('확정된 예약 목록 로드 실패:', error);
    }
}

function renderConfirmedReservations(reservations) {
    const tbody = document.getElementById('confirmedReservationsBody');
    
    const confirmedOnly = reservations.filter(res => res.status === 'CONFIRMED');
    
    if (confirmedOnly.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">확정된 예약이 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = confirmedOnly.map(res => {
        const confirmedDateTime = res.confirmedDate && res.confirmedStartTime ? 
            `${res.confirmedDate}T${res.confirmedStartTime}` : null;
        const isPast = confirmedDateTime && new Date(confirmedDateTime) < new Date();
        const statusBadge = isPast ? 'bg-warning' : 'bg-success';
        const statusText = isPast ? '진행완료' : '예정';
        
        const button = isPast ? 
            `<button class="btn btn-sm btn-primary" onclick="openCompleteModal(${res.id})"><i class="bi bi-check-circle"></i> 완료처리</button>` :
            `<button class="btn btn-sm btn-outline-info" onclick="viewReservation(${res.id})"><i class="bi bi-eye"></i> 상세</button>`;
        
        return `
            <tr>
                <td>${res.studentName}</td>
                <td>${getFieldName(res.counselingField)}</td>
                <td>${confirmedDateTime ? formatDateTime(confirmedDateTime) : '-'}</td>
                <td><span class="badge ${statusBadge}">${statusText}</span></td>
                <td>${button}</td>
            </tr>
        `;
    }).join('');
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

function formatDateTime(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    return date.toLocaleString('ko-KR', {year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'});
}

function openCompleteModal(reservationId) {
    document.getElementById('completeReservationId').value = reservationId;
    document.getElementById('completeFiles').value = '';
    const modal = new bootstrap.Modal(document.getElementById('completeModal'));
    modal.show();
}

async function viewReservation(reservationId) {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`/api/counseling/reservations/${reservationId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const detail = await response.json();
            renderReservationDetailModal(detail);
            const modal = new bootstrap.Modal(document.getElementById('reservationDetailModal'));
            modal.show();
        }
    } catch (error) {
        console.error('상세 정보 로드 실패:', error);
        alert('상세 정보를 불러오는 중 오류가 발생했습니다.');
    }
}

function renderReservationDetailModal(detail) {
    document.getElementById('detailReservationId').textContent = `CNSL-${detail.id}`;
    document.getElementById('detailStudentName').textContent = detail.studentName || '-';
    document.getElementById('detailField').textContent = getFieldName(detail.counselingField);
    document.getElementById('detailCategory').textContent = detail.subFieldName || '-';
    document.getElementById('detailRequestedDateTime').textContent = 
        detail.reservationDate && detail.startTime ? 
        formatDateTime(`${detail.reservationDate}T${detail.startTime}`) : '-';
    document.getElementById('detailConfirmedDateTime').textContent = 
        detail.confirmedDate && detail.confirmedStartTime ? 
        formatDateTime(`${detail.confirmedDate}T${detail.confirmedStartTime}`) : '-';
    document.getElementById('detailStatus').textContent = getStatusText(detail.status);
    document.getElementById('detailStatus').className = `badge ${getStatusBadge(detail.status)}`;
    document.getElementById('detailContent').textContent = detail.requestContent || '-';
    
    if (detail.memo) {
        document.getElementById('detailMemoSection').style.display = 'block';
        document.getElementById('detailMemo').textContent = detail.memo;
    } else {
        document.getElementById('detailMemoSection').style.display = 'none';
    }
}

function getStatusText(status) {
    const statusMap = {
        'PENDING': '대기중',
        'CONFIRMED': '승인됨',
        'COMPLETED': '완료됨',
        'CANCELLED': '취소됨',
        'REJECTED': '거부됨'
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

// 상담 완료 처리
document.getElementById('submitComplete').addEventListener('click', async function() {
    const reservationId = document.getElementById('completeReservationId').value;
    const filesInput = document.getElementById('completeFiles');
    const formData = new FormData();
    
    if (filesInput.files.length > 0) {
        for (let i = 0; i < filesInput.files.length; i++) {
            formData.append('files', filesInput.files[i]);
        }
    }
    
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`/api/counseling/reservations/${reservationId}/complete`, {
            method: 'POST',
            headers: {'Authorization': `Bearer ${token}`},
            body: formData
        });
        
        if (response.ok) {
            alert('상담이 완료 처리되었습니다.');
            bootstrap.Modal.getInstance(document.getElementById('completeModal')).hide();
            loadConfirmedReservations();
        } else {
            const error = await response.json();
            alert(error.message || '완료 처리에 실패했습니다.');
        }
    } catch (error) {
        console.error('완료 처리 실패:', error);
        alert('완료 처리 중 오류가 발생했습니다.');
    }
});

window.cancelOffRequest = cancelOffRequest;
window.editOffRequest = editOffRequest;
window.viewOffRequest = viewOffRequest;
window.openCompleteModal = openCompleteModal;
window.viewReservation = viewReservation;
