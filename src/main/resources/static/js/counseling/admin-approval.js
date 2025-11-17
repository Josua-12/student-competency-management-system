document.addEventListener('DOMContentLoaded', async function() {
    await loadCounselors();
    await loadApprovals();
    
    document.querySelector('.card .btn-primary')?.addEventListener('click', function() {
        loadApprovals();
    });
    
    let currentReservationId = null;
    document.getElementById('assignModal').addEventListener('show.bs.modal', async function(event) {
        const button = event.relatedTarget;
        currentReservationId = button.getAttribute('data-id');
        if (currentReservationId) {
            await loadReservationForAssign(currentReservationId);
        }
    });
    
    document.querySelector('#assignModal .btn-success').addEventListener('click', async function() {
        if (!currentReservationId) return;
        
        const counselorId = document.getElementById('assignCounselorSelect').value;
        const confirmedDate = document.querySelector('#assignModal input[name="confirmedDate"]').value;
        const confirmedStartTime = document.querySelector('#assignModal input[name="confirmedStartTime"]').value;
        const confirmedEndTime = document.querySelector('#assignModal input[name="confirmedEndTime"]').value;
        const memo = document.querySelector('#assignModal textarea').value;
        
        if (!counselorId || !confirmedDate || !confirmedStartTime || !confirmedEndTime) {
            alert(MESSAGES.REQUIRED_FIELDS);
            return;
        }
        
        await assignAndApprove(currentReservationId, counselorId, confirmedDate, confirmedStartTime, confirmedEndTime, memo);
    });
    
    let rejectReservationId = null;
    document.getElementById('rejectModal').addEventListener('show.bs.modal', function(event) {
        const button = event.relatedTarget;
        rejectReservationId = button.getAttribute('data-id');
        document.querySelector('#rejectModal textarea').value = '';
    });
    
    document.querySelector('#rejectModal .btn-danger').addEventListener('click', async function() {
        if (!rejectReservationId) return;
        
        const rejectReason = document.querySelector('#rejectModal textarea').value;
        
        if (!rejectReason.trim()) {
            alert(MESSAGES.REQUIRED_REASON);
            return;
        }
        
        await rejectReservation(rejectReservationId, rejectReason);
    });
    
    document.getElementById('detailModal').addEventListener('show.bs.modal', async function(event) {
        const button = event.relatedTarget;
        const reservationId = button.getAttribute('data-id');
        if (reservationId) {
            await loadReservationDetail(reservationId);
        }
    });
});

async function loadApprovals() {
    const token = localStorage.getItem('accessToken');
    const status = document.getElementById('statusFilter')?.value || '';
    const field = document.getElementById('fieldFilter')?.value || '';
    const counselor = document.getElementById('counselorFilter')?.value || '';
    const startDate = document.querySelectorAll('.card input[type="date"]')[0]?.value || '';
    const endDate = document.querySelectorAll('.card input[type="date"]')[1]?.value || '';
    const searchText = document.querySelector('.card input[type="text"]')?.value || '';
    
    try {
        const response = await fetch('/api/counseling/reservations', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            let list = data.content || [];
            
            if (status) list = list.filter(item => item.status === status);
            if (field) list = list.filter(item => item.counselingField === field);
            if (counselor) list = list.filter(item => item.counselorId == counselor);
            if (startDate) list = list.filter(item => new Date(item.reservationDate) >= new Date(startDate));
            if (endDate) list = list.filter(item => new Date(item.reservationDate) <= new Date(endDate));
            if (searchText) list = list.filter(item => 
                item.studentName?.includes(searchText) || item.id?.toString().includes(searchText)
            );
            
            document.getElementById('totalCount').textContent = `${list.length}${UNIT_COUNT}`;
            renderApprovalTable(list);
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
    }
}

function renderApprovalTable(list) {
    const tbody = document.getElementById('approvalTableBody');
    if (list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="10" class="text-center">${MESSAGES.NO_RESERVATION}</td></tr>`;
        return;
    }
    
    tbody.innerHTML = list.map(item => {
        const statusBadge = STATUS_BADGE[item.status] || STATUS_BADGE.REJECTED;
        const statusText = STATUS_TEXT[item.status] || item.status;
        
        let buttons = '';
        if (item.status === 'PENDING') {
            buttons = `
                <button class="btn btn-sm btn-success" data-bs-toggle="modal" data-bs-target="#assignModal" data-id="${item.id}">배정</button>
                <button class="btn btn-sm btn-danger" data-bs-toggle="modal" data-bs-target="#rejectModal" data-id="${item.id}">거부</button>
            `;
        }
        buttons += `<button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#detailModal" data-id="${item.id}">상세</button>`;
        
        const requestedDateTime = item.reservationDate && item.startTime ? 
            `${item.reservationDate}T${item.startTime}` : null;
        const confirmedDateTime = item.confirmedDate && item.confirmedStartTime ? 
            `${item.confirmedDate}T${item.confirmedStartTime}` : null;
        
        return `
            <tr>
                <td><input type="checkbox" class="row-checkbox" value="${item.id}"></td>
                <td>${item.id || '-'}</td>
                <td>${item.studentName}</td>
                <td>${getFieldName(item.counselingField)}</td>
                <td>${formatDateTime(requestedDateTime)}</td>
                <td>${formatDateTime(confirmedDateTime) || '-'}</td>
                <td>${formatDate(item.createdAt)}</td>
                <td>${item.counselorName || '-'}</td>
                <td><span class="badge ${statusBadge}">${statusText}</span></td>
                <td>${buttons}</td>
            </tr>
        `;
    }).join('');
}

function formatDateTime(dateStr) {
    if (!dateStr) return null;
    const date = new Date(dateStr);
    return date.toLocaleString('ko-KR', {year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'});
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR');
}

async function loadCounselors() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/api/counseling/management/counselors', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            const counselors = data.content || data;
            
            const counselorFilter = document.getElementById('counselorFilter');
            const assignCounselorSelect = document.getElementById('assignCounselorSelect');
            
            counselors.forEach(counselor => {
                const filterOption = document.createElement('option');
                filterOption.value = counselor.userId;
                filterOption.textContent = counselor.name;
                counselorFilter.appendChild(filterOption);
                
                const assignOption = document.createElement('option');
                assignOption.value = counselor.userId;
                const SPECIALIST_SUFFIX = '전문';
                assignOption.textContent = `${counselor.name} (${getFieldName(counselor.counselingField)} ${SPECIALIST_SUFFIX})`;
                assignCounselorSelect.appendChild(assignOption);
            });
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
    }
}

const FIELD_NAMES = {
    'PSYCHOLOGICAL': '심리상담',
    'CAREER': '진로상담',
    'EMPLOYMENT': '취업상담',
    'LEARNING': '학습상담'
};

const STATUS_BADGE = {
    'PENDING': 'bg-warning',
    'CONFIRMED': 'bg-success',
    'REJECTED': 'bg-danger'
};

const STATUS_TEXT = {
    'PENDING': '대기중',
    'CONFIRMED': '승인됨',
    'REJECTED': '거부됨'
};

const MESSAGES = {
    NO_RESERVATION: '예약 내역이 없습니다.',
    REQUIRED_FIELDS: '모든 필수 항목을 입력해주세요.',
    REQUIRED_REASON: '거부 사유를 입력해주세요.',
    ASSIGN_SUCCESS: '상담사가 배정되고 예약이 승인되었습니다.',
    REJECT_SUCCESS: '예약이 거부되었습니다.',
    PROCESS_ERROR: '처리 중 오류가 발생했습니다.',
    REJECT_ERROR: '거부 처리 중 오류가 발생했습니다.',
    LOAD_ERROR: '로드 실패'
};

const UNIT_COUNT = '건';

function getFieldName(field) {
    return FIELD_NAMES[field] || field;
}

async function loadReservationForAssign(reservationId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const detail = await response.json();
            document.querySelector('#assignModal input[name="confirmedDate"]').value = detail.reservationDate || '';
            document.querySelector('#assignModal input[name="confirmedStartTime"]').value = detail.startTime || '';
            document.querySelector('#assignModal input[name="confirmedEndTime"]').value = detail.endTime || '';
            document.querySelector('#assignModal textarea').value = '';
            
            if (detail.counselorId) {
                document.getElementById('assignCounselorSelect').value = detail.counselorId;
            }
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
    }
}

async function assignAndApprove(reservationId, counselorId, confirmedDate, confirmedStartTime, confirmedEndTime, memo) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}/approve`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                reservationId: parseInt(reservationId),
                counselorId: parseInt(counselorId),
                confirmedDate,
                confirmedStartTime,
                confirmedEndTime,
                memo
            })
        });
        
        if (response.ok) {
            alert(MESSAGES.ASSIGN_SUCCESS);
            bootstrap.Modal.getInstance(document.getElementById('assignModal')).hide();
            await loadApprovals();
        } else {
            alert(MESSAGES.PROCESS_ERROR);
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
        alert(MESSAGES.PROCESS_ERROR);
    }
}

async function rejectReservation(reservationId, rejectReason) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}/reject?rejectReason=${encodeURIComponent(rejectReason)}`, {
            method: 'POST',
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            alert(MESSAGES.REJECT_SUCCESS);
            bootstrap.Modal.getInstance(document.getElementById('rejectModal')).hide();
            await loadApprovals();
        } else {
            alert(MESSAGES.REJECT_ERROR);
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
        alert(MESSAGES.REJECT_ERROR);
    }
}

async function loadReservationDetail(reservationId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const detail = await response.json();
            renderReservationDetail(detail);
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
    }
}

function renderReservationDetail(detail) {
    const modal = document.getElementById('detailModal');
    const rows = modal.querySelectorAll('.row');
    
    const ID_PREFIX = 'CNSL-';
    rows[0].querySelector('.col-sm-9').textContent = `${ID_PREFIX}${detail.id}`;
    rows[1].querySelector('.col-sm-9').textContent = `${detail.studentName} (${detail.studentNumber || '-'}) / ${detail.department || '-'}`;
    rows[2].querySelector('.col-sm-9').textContent = getFieldName(detail.counselingField);
    rows[3].querySelector('.col-sm-9').textContent = detail.reservationDate && detail.startTime ? 
        formatDateTime(`${detail.reservationDate}T${detail.startTime}`) : '-';
    rows[4].querySelector('.col-sm-9').textContent = detail.confirmedDate && detail.confirmedStartTime ? 
        formatDateTime(`${detail.confirmedDate}T${detail.confirmedStartTime}`) : '-';
    rows[5].querySelector('.col-sm-9').textContent = detail.requestContent || '-';
    rows[6].querySelector('.col-sm-9').textContent = formatDate(detail.createdAt);
    
    if (detail.rejectReason) {
        document.getElementById('rejectReasonRow').style.display = 'flex';
        document.getElementById('rejectReasonText').textContent = detail.rejectReason;
    } else {
        document.getElementById('rejectReasonRow').style.display = 'none';
    }
}
