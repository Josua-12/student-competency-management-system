document.addEventListener('DOMContentLoaded', async function() {
    await loadHistoryData();
    
    document.querySelector('.card .btn-primary').addEventListener('click', function() {
        loadHistoryData();
    });
    
    document.getElementById('historyDetailModal').addEventListener('show.bs.modal', async function(event) {
        const button = event.relatedTarget;
        const reservationId = button.getAttribute('data-id');
        if (reservationId) {
            await loadHistoryDetail(reservationId);
        }
    });
    
    let recordReservationId = null;
    document.getElementById('recordModal').addEventListener('show.bs.modal', function(event) {
        const button = event.relatedTarget;
        recordReservationId = button.getAttribute('data-id');
        document.querySelector('#recordModal input').value = '';
        document.querySelectorAll('#recordModal textarea').forEach(ta => ta.value = '');
    });
    
    document.querySelector('#recordModal .btn-primary').addEventListener('click', async function() {
        if (!recordReservationId) return;
        const content = document.querySelectorAll('#recordModal textarea')[0].value;
        const notes = document.querySelectorAll('#recordModal textarea')[1].value;
        
        if (!content) {
            alert('상담 내용을 입력해주세요.');
            return;
        }
        
        await saveRecord(recordReservationId, content, notes);
    });
});

async function loadHistoryData() {
    const token = localStorage.getItem('accessToken');
    const status = document.querySelector('.card select[class*="form-select"]:nth-of-type(1)')?.value || '';
    const field = document.querySelector('.card select[class*="form-select"]:nth-of-type(2)')?.value || '';
    const startDate = document.querySelectorAll('.card input[type="date"]')[0]?.value || '';
    const endDate = document.querySelectorAll('.card input[type="date"]')[1]?.value || '';
    const searchText = document.querySelector('.card input[type="text"]')?.value || '';
    
    try {
        const statsResponse = await fetch('/api/counseling/history/status', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (statsResponse.ok) {
            const stats = await statsResponse.json();
            document.getElementById('totalHistory').textContent = `${stats.totalCount || 0}건`;
            document.getElementById('completedHistory').textContent = `${stats.completedCount || 0}건`;
            document.getElementById('monthlyHistory').textContent = `${stats.pendingCount || 0}건`;
            document.getElementById('avgSatisfaction').textContent = `0/5.0`;
        }
        
        const listResponse = await fetch('/api/counseling/history/counselor', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (listResponse.ok) {
            const data = await listResponse.json();
            let list = data.content || [];
            
            if (status) list = list.filter(item => item.status === status);
            if (field) list = list.filter(item => item.counselingField === field);
            if (startDate) list = list.filter(item => new Date(item.counselingDate) >= new Date(startDate));
            if (endDate) list = list.filter(item => new Date(item.counselingDate) <= new Date(endDate));
            if (searchText) list = list.filter(item => 
                item.studentName?.includes(searchText) || item.studentId?.includes(searchText)
            );
            
            document.getElementById('totalCount').textContent = `${list.length}건`;
            renderHistoryTable(list);
        }
    } catch (error) {
        console.error('이력 데이터 로드 실패:', error);
    }
}

function renderHistoryTable(list) {
    const tbody = document.getElementById('historyTableBody');
    
    if (list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">상담 이력이 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = list.map(item => {
        const statusBadge = item.status === 'COMPLETED' ? 'bg-success' : 
                           item.status === 'CANCELLED' ? 'bg-danger' : 'bg-secondary';
        const statusText = item.status === 'COMPLETED' ? '완료' : 
                          item.status === 'CANCELLED' ? '취소' : '거절';
        
        let recordBadge = '-';
        if (item.status === 'COMPLETED') {
            recordBadge = item.hasRecord ? 
                `<button class="btn btn-sm btn-outline-success" onclick="viewRecord(${item.id})">보기</button>` : 
                `<button class="btn btn-sm btn-warning" data-bs-toggle="modal" data-bs-target="#recordModal" data-id="${item.id}">작성하기</button>`;
        }
        
        return `
            <tr>
                <td>${formatDate(item.counselingDate)}</td>
                <td>${item.studentName || '-'}<br><small class="text-muted">${item.studentNumber || ''}</small></td>
                <td>${item.department || '-'}</td>
                <td>${getFieldName(item.counselingField)}</td>
                <td><span class="badge ${statusBadge}">${statusText}</span></td>
                <td>${item.satisfaction ? `${item.satisfaction}/5.0` : '-'}</td>
                <td>${recordBadge}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#historyDetailModal" data-id="${item.id}">상세</button>
                </td>
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

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR');
}

async function loadHistoryDetail(reservationId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const detail = await response.json();
            renderHistoryDetail(detail);
        }
    } catch (error) {
        console.error('상세 정보 로드 실패:', error);
    }
}

function renderHistoryDetail(detail) {
    document.getElementById('detailStudentName').textContent = detail.studentName || '-';
    document.getElementById('detailField').textContent = getFieldName(detail.counselingField);
    document.getElementById('detailCategory').textContent = detail.subFieldName || '-';
    
    const dateTime = detail.confirmedDate && detail.confirmedStartTime ? 
        `${formatDate(detail.confirmedDate)} ${detail.confirmedStartTime}` : '-';
    document.getElementById('detailDateTime').textContent = dateTime;
    
    document.getElementById('detailStatus').textContent = getStatusText(detail.status);
    document.getElementById('detailStatus').className = `badge ${getStatusBadge(detail.status)}`;
    document.getElementById('detailContent').textContent = detail.requestContent || '-';
    
    const reasonSection = document.getElementById('detailReasonSection');
    if (detail.status === 'CANCELLED' || detail.status === 'REJECTED') {
        document.getElementById('detailReasonLabel').textContent = detail.status === 'CANCELLED' ? '취소 사유:' : '거절 사유:';
        document.getElementById('detailReason').textContent = detail.cancelReason || detail.rejectReason || '사유가 기록되지 않았습니다.';
        reasonSection.style.display = 'flex';
    } else {
        reasonSection.style.display = 'none';
    }
}

function getStatusText(status) {
    const statusMap = {
        'PENDING': '대기중',
        'CONFIRMED': '승인됨',
        'COMPLETED': '완료',
        'CANCELLED': '취소',
        'REJECTED': '거부'
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

async function saveRecord(reservationId, content, notes) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/records`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                reservationId: parseInt(reservationId),
                recordContent: content,
                counselorMemo: notes,
                isPublic: true
            })
        });
        
        if (response.ok) {
            alert('상담일지가 저장되었습니다.');
            bootstrap.Modal.getInstance(document.getElementById('recordModal')).hide();
            await loadHistoryData();
        } else {
            alert('저장 중 오류가 발생했습니다.');
        }
    } catch (error) {
        console.error('저장 실패:', error);
        alert('저장 중 오류가 발생했습니다.');
    }
}

async function viewRecord(reservationId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const detail = await response.json();
            if (detail.recordId) {
                const recordResponse = await fetch(`/api/counseling/records/${detail.recordId}`, {
                    headers: {'Authorization': `Bearer ${token}`}
                });
                if (recordResponse.ok) {
                    const record = await recordResponse.json();
                    showRecordModal(record);
                }
            }
        }
    } catch (error) {
        console.error('일지 조회 실패:', error);
    }
}

function showRecordModal(record) {
    window.currentRecordId = record.id;
    document.getElementById('viewRecordContent').textContent = record.recordContent || '-';
    document.getElementById('viewRecordMemo').textContent = record.counselorMemo || '-';
    const modal = new bootstrap.Modal(document.getElementById('viewRecordModal'));
    modal.show();
}

window.viewRecord = viewRecord;
