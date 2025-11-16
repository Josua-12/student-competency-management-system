document.addEventListener('DOMContentLoaded', async function() {
    await loadReservations();
    
    // 검색 버튼 이벤트
    document.querySelector('.btn-primary').addEventListener('click', function() {
        loadReservations();
    });
    
    // 상세 모달 이벤트
    document.getElementById('detailModal').addEventListener('show.bs.modal', function(event) {
        const button = event.relatedTarget;
        const reservationId = button.getAttribute('data-id');
        if (reservationId) {
            loadReservationDetail(reservationId);
        }
    });
    
    // 승인 모달 이벤트
    let currentReservationId = null;
    document.getElementById('approveModal').addEventListener('show.bs.modal', async function(event) {
        const button = event.relatedTarget;
        currentReservationId = button.getAttribute('data-id');
        if (currentReservationId) {
            await loadReservationForApproval(currentReservationId);
        }
    });
    
    // 승인 버튼 클릭
    document.querySelector('#approveModal .btn-success').addEventListener('click', async function() {
        if (!currentReservationId) return;
        
        const confirmedDate = document.querySelector('#approveModal input[name="confirmedDate"]').value;
        const confirmedStartTime = document.querySelector('#approveModal input[name="confirmedStartTime"]').value;
        const confirmedEndTime = document.querySelector('#approveModal input[name="confirmedEndTime"]').value;
        const memo = document.querySelector('#approveModal textarea').value;
        
        if (!confirmedDate || !confirmedStartTime || !confirmedEndTime) {
            alert('확정 날짜와 시간을 모두 입력해주세요.');
            return;
        }
        
        await approveReservation(currentReservationId, confirmedDate, confirmedStartTime, confirmedEndTime, memo);
    });
    
    // 거부 모달 이벤트
    let rejectReservationId = null;
    document.getElementById('rejectModal').addEventListener('show.bs.modal', function(event) {
        const button = event.relatedTarget;
        rejectReservationId = button.getAttribute('data-id');
        document.querySelector('#rejectModal textarea').value = '';
    });
    
    // 거부 버튼 클릭
    document.querySelector('#rejectModal .btn-danger').addEventListener('click', async function() {
        if (!rejectReservationId) return;
        
        const rejectReason = document.querySelector('#rejectModal textarea').value;
        
        if (!rejectReason.trim()) {
            alert('거부 사유를 입력해주세요.');
            return;
        }
        
        await rejectReservation(rejectReservationId, rejectReason);
    });
});

async function loadReservations() {
    const token = localStorage.getItem('accessToken');
    const params = getSearchParams();
    
    try {
        const response = await fetch(`/api/counseling/reservations/assigned?${params}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            let list = data.content || [];
            
            const statusFilter = document.querySelectorAll('.form-select')[0].value;
            if (statusFilter) {
                list = list.filter(item => item.status === statusFilter);
            }
            
            const fieldFilter = document.querySelectorAll('.form-select')[1].value;
            if (fieldFilter) {
                list = list.filter(item => item.counselingField === fieldFilter);
            }
            
            const startDate = document.querySelectorAll('input[type="date"]')[0].value;
            const endDate = document.querySelectorAll('input[type="date"]')[1].value;
            if (startDate) {
                list = list.filter(item => item.reservationDate >= startDate);
            }
            if (endDate) {
                list = list.filter(item => item.reservationDate <= endDate);
            }
            
            document.getElementById('totalCount').textContent = `${list.length}건`;
            renderReservationTable(list);
        }
    } catch (error) {
        console.error('예약 목록 로드 실패:', error);
    }
}

function renderReservationTable(list) {
    const tbody = document.getElementById('reservationTableBody');
    
    if (list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">예약 내역이 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = list.map(item => {
        const statusBadge = item.status === 'PENDING' ? 'bg-warning' : 
                           item.status === 'CONFIRMED' ? 'bg-success' : 'bg-danger';
        const statusText = item.status === 'PENDING' ? '대기중' : 
                          item.status === 'CONFIRMED' ? '승인됨' : '거부됨';
        
        let buttons = '';
        if (item.status === 'PENDING') {
            buttons = `
                <button class="btn btn-sm btn-success" data-bs-toggle="modal" data-bs-target="#approveModal" data-id="${item.id}">승인</button>
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
                <td>${item.id || '-'}</td>
                <td>${item.studentName}</td>
                <td>${getFieldName(item.counselingField)}</td>
                <td>${formatDateTime(requestedDateTime)}</td>
                <td>${formatDateTime(confirmedDateTime) || '-'}</td>
                <td>${formatDate(item.createdAt)}</td>
                <td><span class="badge ${statusBadge}">${statusText}</span></td>
                <td>${buttons}</td>
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
    if (!dateStr) return null;
    const date = new Date(dateStr);
    return date.toLocaleString('ko-KR', {year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'});
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR');
}

function getSearchParams() {
    const status = document.querySelectorAll('.form-select')[0].value;
    const field = document.querySelectorAll('.form-select')[1].value;
    const startDate = document.querySelectorAll('input[type="date"]')[0].value;
    const endDate = document.querySelectorAll('input[type="date"]')[1].value;
    
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    if (field) params.append('field', field);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    
    return params.toString();
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
            
            if (detail.counselingField === 'EMPLOYMENT') {
                loadAttachments(reservationId);
            } else {
                document.getElementById('detailAttachmentsSection').style.display = 'none';
            }
        }
    } catch (error) {
        console.error('상세 정보 로드 실패:', error);
    }
}

function renderReservationDetail(detail) {
    document.getElementById('detailReservationId').textContent = `CNSL-${detail.id}`;
    document.getElementById('detailField').textContent = getFieldName(detail.counselingField);
    document.getElementById('detailCategory').textContent = detail.subFieldName || '-';
    document.getElementById('detailRequestedDateTime').textContent = 
        detail.reservationDate && detail.startTime ? 
        formatDateTime(`${detail.reservationDate}T${detail.startTime}`) : '-';
    document.getElementById('detailConfirmedDateTime').textContent = 
        detail.confirmedDate && detail.confirmedStartTime ? 
        formatDateTime(`${detail.confirmedDate}T${detail.confirmedStartTime}`) : '-';
    document.getElementById('detailCounselor').textContent = detail.counselorName || '-';
    document.getElementById('detailStatus').textContent = getStatusText(detail.status);
    document.getElementById('detailStatus').className = `badge ${getStatusBadge(detail.status)}`;
    document.getElementById('detailContent').textContent = detail.requestContent || '-';
    
    if (detail.memo) {
        document.getElementById('detailMemoSection').style.display = 'block';
        document.getElementById('detailMemo').textContent = detail.memo;
    } else {
        document.getElementById('detailMemoSection').style.display = 'none';
    }
    
    if (detail.rejectReason) {
        document.getElementById('detailRejectReasonRow').style.display = 'flex';
        document.getElementById('detailRejectReasonText').textContent = detail.rejectReason;
    } else {
        document.getElementById('detailRejectReasonRow').style.display = 'none';
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

async function loadAttachments(reservationId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}/attachments`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const attachments = await response.json();
            const section = document.getElementById('detailAttachmentsSection');
            const container = document.getElementById('detailAttachments');
            
            if (attachments.length === 0) {
                section.style.display = 'none';
                return;
            }
            
            section.style.display = 'block';
            container.innerHTML = '';
            
            attachments.forEach(att => {
                const typeDisplay = att.attachmentType === 'RESUME' ? '이력서' : 
                                   att.attachmentType === 'COVER_LETTER' ? '자기소개서' : '서류';
                const link = document.createElement('a');
                link.href = `/api/counseling/reservations/attachments/${att.id}/download`;
                link.download = att.originalName;
                link.className = 'btn btn-sm btn-outline-secondary me-2 mb-2';
                link.innerHTML = `<i class="bi bi-download"></i> ${typeDisplay}: ${att.originalName}`;
                container.appendChild(link);
            });
        }
    } catch (error) {
        console.error('첨부파일 로드 실패:', error);
    }
}

async function loadReservationForApproval(reservationId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const detail = await response.json();
            document.querySelector('#approveModal input[name="confirmedDate"]').value = detail.reservationDate || '';
            document.querySelector('#approveModal input[name="confirmedStartTime"]').value = detail.startTime || '';
            document.querySelector('#approveModal input[name="confirmedEndTime"]').value = detail.endTime || '';
            document.querySelector('#approveModal textarea').value = '';
        }
    } catch (error) {
        console.error('예약 정보 로드 실패:', error);
    }
}

async function approveReservation(reservationId, confirmedDate, confirmedStartTime, confirmedEndTime, memo) {
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
                counselorId: 1,
                studentId: 1,
                confirmedDate,
                confirmedStartTime,
                confirmedEndTime,
                memo
            })
        });
        
        if (response.ok) {
            alert('예약이 승인되었습니다.');
            bootstrap.Modal.getInstance(document.getElementById('approveModal')).hide();
            await loadReservations();
        } else {
            const errorData = await response.json().catch(() => ({}));
            alert(errorData.message || '승인 처리 중 오류가 발생했습니다.');
        }
    } catch (error) {
        console.error('승인 처리 실패:', error);
        alert('승인 처리 중 오류가 발생했습니다.');
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
            alert('예약이 거부되었습니다.');
            bootstrap.Modal.getInstance(document.getElementById('rejectModal')).hide();
            await loadReservations();
        } else {
            alert('거부 처리 중 오류가 발생했습니다.');
        }
    } catch (error) {
        console.error('거부 처리 실패:', error);
        alert('거부 처리 중 오류가 발생했습니다.');
    }
}
