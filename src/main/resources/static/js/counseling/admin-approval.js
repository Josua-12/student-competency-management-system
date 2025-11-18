document.addEventListener('DOMContentLoaded', function() {
    loadApprovals();
    loadCounselors();
    setupEventListeners();
});

let currentReservationId = null;

async function loadApprovals() {
    const token = localStorage.getItem('accessToken');
    
    // 검색 조건 수집
    const status = document.getElementById('statusFilter')?.value || '';
    const counselingType = document.getElementById('typeFilter')?.value || '';
    const counselorId = document.getElementById('counselorFilter')?.value || '';
    const startDate = document.getElementById('startDate')?.value || '';
    const endDate = document.getElementById('endDate')?.value || '';
    
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    if (counselingType) params.append('field', counselingType);
    if (counselorId) params.append('counselorId', counselorId);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    
    try {
        const url = `/api/counseling/admin/approvals${params.toString() ? '?' + params.toString() : ''}`;
        const response = await fetch(url, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            displayApprovals(data.content || []);
            document.getElementById('totalCount').textContent = `${data.totalElements || 0}건`;
        }
    } catch (error) {
        console.error('승인 목록 로드 실패:', error);
    }
}

function displayApprovals(approvals) {
    const tbody = document.getElementById('approvalTableBody');
    
    if (approvals.length === 0) {
        tbody.innerHTML = '<tr><td colspan="10" class="text-center">승인 대기 내역이 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = approvals.map(approval => `
        <tr>
            <td><input type="checkbox" value="${approval.id}"></td>
            <td>${approval.id}</td>
            <td>${approval.studentName}(${approval.studentId})</td>
            <td>${approval.counselingType}</td>
            <td>${formatDateTime(approval.requestedDateTime)}</td>
            <td>${approval.confirmedDateTime ? formatDateTime(approval.confirmedDateTime) : '-'}</td>
            <td>${formatDate(approval.createdAt)}</td>
            <td>${approval.counselorName || '-'}</td>
            <td><span class="badge ${getStatusBadgeClass(approval.status)}">${getStatusText(approval.status)}</span></td>
            <td>
                <div class="btn-group">
                    <button class="btn btn-sm btn-info" onclick="viewDetail(${approval.id})">상세</button>
                    ${approval.status === 'PENDING' ? `
                        <button class="btn btn-sm btn-success" onclick="showApprovalModal(${approval.id})">승인</button>
                        <button class="btn btn-sm btn-danger" onclick="showRejectModal(${approval.id})">거부</button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join('');
}

async function loadCounselors() {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch('/api/counseling/admin/counselors', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const counselors = await response.json();
            const select = document.getElementById('assignCounselorSelect');
            const filterSelect = document.getElementById('counselorFilter');
            
            select.innerHTML = '<option value="">상담사를 선택하세요</option>';
            filterSelect.innerHTML = '<option value="">전체</option>';
            
            counselors.forEach(counselor => {
                select.innerHTML += `<option value="${counselor.id}">${counselor.name}(${counselor.field})</option>`;
                filterSelect.innerHTML += `<option value="${counselor.id}">${counselor.name}</option>`;
            });
        }
    } catch (error) {
        console.error('상담사 목록 로드 실패:', error);
    }
}

function setupEventListeners() {
    // 전체 선택 체크박스
    document.getElementById('selectAll').addEventListener('change', function() {
        const checkboxes = document.querySelectorAll('#approvalTableBody input[type="checkbox"]');
        checkboxes.forEach(cb => cb.checked = this.checked);
        updateBulkButtons();
    });
    
    // 체크박스 상태 변경 감지
    document.addEventListener('change', function(e) {
        if (e.target.type === 'checkbox' && e.target.closest('#approvalTableBody')) {
            updateBulkButtons();
        }
    });
    
    // 검색 버튼
    document.getElementById('searchBtn').addEventListener('click', function() {
        loadApprovals();
    });
    
    // 일괄 승인 버튼
    document.getElementById('bulkApprove').addEventListener('click', bulkApprove);
    
    // 일괄 거부 버튼
    document.getElementById('bulkReject').addEventListener('click', bulkReject);
    
    // 승인 모달 제출
    document.querySelector('#assignModal .btn-success').addEventListener('click', submitApproval);
    
    // 거부 모달 제출
    document.querySelector('#rejectModal .btn-danger').addEventListener('click', submitReject);
}

function showApprovalModal(reservationId) {
    currentReservationId = reservationId;
    const modal = new bootstrap.Modal(document.getElementById('assignModal'));
    
    // 기본값 설정
    const today = new Date().toISOString().split('T')[0];
    document.querySelector('input[name="confirmedDate"]').value = today;
    document.querySelector('input[name="confirmedStartTime"]').value = '09:00';
    document.querySelector('input[name="confirmedEndTime"]').value = '09:40';
    
    modal.show();
}

function showRejectModal(reservationId) {
    currentReservationId = reservationId;
    const modal = new bootstrap.Modal(document.getElementById('rejectModal'));
    modal.show();
}

async function submitApproval() {
    const token = localStorage.getItem('accessToken');
    const counselorId = document.getElementById('assignCounselorSelect').value;
    const confirmedDate = document.querySelector('input[name="confirmedDate"]').value;
    const confirmedStartTime = document.querySelector('input[name="confirmedStartTime"]').value;
    const confirmedEndTime = document.querySelector('input[name="confirmedEndTime"]').value;
    const memo = document.querySelector('#assignModal textarea').value;
    
    if (!counselorId || !confirmedDate || !confirmedStartTime || !confirmedEndTime) {
        alert('모든 필수 항목을 입력해주세요.');
        return;
    }
    
    try {
        const response = await fetch(`/api/counseling/admin/approvals/${currentReservationId}/approve`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                counselorId: parseInt(counselorId),
                confirmedDate: confirmedDate,
                confirmedStartTime: confirmedStartTime,
                confirmedEndTime: confirmedEndTime,
                memo: memo
            })
        });
        
        if (response.ok) {
            alert('승인이 완료되었습니다.');
            bootstrap.Modal.getInstance(document.getElementById('assignModal')).hide();
            loadApprovals();
        } else {
            alert('승인 처리 중 오류가 발생했습니다.');
        }
    } catch (error) {
        console.error('승인 처리 실패:', error);
        alert('승인 처리 중 오류가 발생했습니다.');
    }
}

async function submitReject() {
    const token = localStorage.getItem('accessToken');
    const rejectReason = document.querySelector('#rejectModal textarea').value;
    
    if (!rejectReason.trim()) {
        alert('거부 사유를 입력해주세요.');
        return;
    }
    
    try {
        const response = await fetch(`/api/counseling/admin/approvals/${currentReservationId}/reject`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                rejectReason: rejectReason
            })
        });
        
        if (response.ok) {
            alert('거부가 완료되었습니다.');
            bootstrap.Modal.getInstance(document.getElementById('rejectModal')).hide();
            loadApprovals();
        } else {
            alert('거부 처리 중 오류가 발생했습니다.');
        }
    } catch (error) {
        console.error('거부 처리 실패:', error);
        alert('거부 처리 중 오류가 발생했습니다.');
    }
}

async function viewDetail(reservationId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            showDetailModal(data);
        } else {
            alert('상세 정보를 불러오는데 실패했습니다.');
        }
    } catch (error) {
        console.error('상세 조회 실패:', error);
        alert('상세 정보를 불러오는데 오류가 발생했습니다.');
    }
}

function showDetailModal(data) {
    // 상세 모달에 데이터 채우기
    const modal = document.getElementById('detailModal');
    const rows = modal.querySelectorAll('.row');
    
    if (rows.length >= 7) {
        rows[0].querySelector('.col-sm-9').textContent = data.id || '-';
        rows[1].querySelector('.col-sm-9').textContent = `${data.studentName || '-'}`;
        rows[2].querySelector('.col-sm-9').textContent = data.subFieldName || data.counselingType || '-';
        rows[3].querySelector('.col-sm-9').textContent = formatDateTime(data.requestedDateTime) || '-';
        rows[4].querySelector('.col-sm-9').textContent = data.confirmedDateTime ? formatDateTime(data.confirmedDateTime) : '-';
        rows[5].querySelector('.col-sm-9').textContent = data.requestContent || '-';
        rows[6].querySelector('.col-sm-9').textContent = formatDate(data.createdAt) || '-';
        
        // 거부 사유 표시
        const rejectRow = document.getElementById('rejectReasonRow');
        const rejectText = document.getElementById('rejectReasonText');
        if (data.status === 'REJECTED' && data.rejectReason) {
            rejectRow.style.display = 'flex';
            rejectText.textContent = data.rejectReason;
        } else {
            rejectRow.style.display = 'none';
        }
    }
    
    new bootstrap.Modal(modal).show();
}

function getStatusBadgeClass(status) {
    const classes = {
        'PENDING': 'bg-warning',
        'CONFIRMED': 'bg-success',
        'REJECTED': 'bg-danger',
        'COMPLETED': 'bg-primary',
        'CANCELLED': 'bg-secondary'
    };
    return classes[status] || 'bg-secondary';
}

function getStatusText(status) {
    const texts = {
        'PENDING': '대기중',
        'CONFIRMED': '확정됨',
        'REJECTED': '거부됨',
        'COMPLETED': '완료됨',
        'CANCELLED': '취소됨'
    };
    return texts[status] || status;
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR');
}

function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return '-';
    return new Date(dateTimeStr).toLocaleString('ko-KR');
}

function updateBulkButtons() {
    const checkedBoxes = document.querySelectorAll('#approvalTableBody input[type="checkbox"]:checked');
    const bulkApproveBtn = document.getElementById('bulkApprove');
    const bulkRejectBtn = document.getElementById('bulkReject');
    
    if (checkedBoxes.length > 0) {
        bulkApproveBtn.disabled = false;
        bulkRejectBtn.disabled = false;
    } else {
        bulkApproveBtn.disabled = true;
        bulkRejectBtn.disabled = true;
    }
}

function getSelectedIds() {
    const checkedBoxes = document.querySelectorAll('#approvalTableBody input[type="checkbox"]:checked');
    return Array.from(checkedBoxes).map(cb => parseInt(cb.value));
}

async function bulkApprove() {
    const selectedIds = getSelectedIds();
    if (selectedIds.length === 0) {
        alert('승인할 항목을 선택해주세요.');
        return;
    }
    
    // 선택된 예약들의 상담사 배정 여부 확인
    const selectedRows = document.querySelectorAll('#approvalTableBody input[type="checkbox"]:checked');
    const hasUnassignedCounselor = Array.from(selectedRows).some(checkbox => {
        const row = checkbox.closest('tr');
        const counselorCell = row.cells[7]; // 상담사 열
        return counselorCell.textContent.trim() === '-';
    });
    
    if (hasUnassignedCounselor) {
        alert('상담사가 배정되지 않은 예약이 있어 일괄 승인을 할 수 없습니다.\n개별로 상담사를 배정하고 승인해주세요.');
        return;
    }
    
    const memo = prompt(`선택된 ${selectedIds.length}건의 예약을 승인하시겠습니까?\n승인 메시지를 입력해주세요:`);
    
    if (memo === null) return; // 취소
    if (!memo.trim()) {
        alert('승인 메시지를 입력해주세요.');
        return;
    }
    
    let successCount = 0;
    let errorCount = 0;
    
    for (const id of selectedIds) {
        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch(`/api/counseling/admin/approvals/${id}/approve`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    counselorId: await getAssignedCounselorId(id),
                    confirmedDate: new Date().toISOString().split('T')[0],
                    confirmedStartTime: '09:00',
                    confirmedEndTime: '09:40',
                    memo: memo
                })
            });
            
            if (response.ok) {
                successCount++;
            } else {
                errorCount++;
            }
        } catch (error) {
            console.error(`예약 ${id} 승인 실패:`, error);
            errorCount++;
        }
    }
    
    alert(`일괄 승인 완료: 성공 ${successCount}건, 실패 ${errorCount}건`);
    loadApprovals();
}

async function bulkReject() {
    const selectedIds = getSelectedIds();
    if (selectedIds.length === 0) {
        alert('거부할 항목을 선택해주세요.');
        return;
    }
    
    const reason = prompt(`선택된 ${selectedIds.length}건의 예약을 거부하시겠습니까?\n거부 사유를 입력해주세요:`);
    
    if (reason === null) return; // 취소
    if (!reason.trim()) {
        alert('거부 사유를 입력해주세요.');
        return;
    }
    
    let successCount = 0;
    let errorCount = 0;
    
    for (const id of selectedIds) {
        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch(`/api/counseling/admin/approvals/${id}/reject`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    rejectReason: reason
                })
            });
            
            if (response.ok) {
                successCount++;
            } else {
                errorCount++;
            }
        } catch (error) {
            console.error(`예약 ${id} 거부 실패:`, error);
            errorCount++;
        }
    }
    
    alert(`일괄 거부 완료: 성공 ${successCount}건, 실패 ${errorCount}건`);
    loadApprovals();
}

async function getAssignedCounselorId(reservationId) {
    const token = localStorage.getItem('accessToken');
    try {
        const response = await fetch(`/api/counseling/reservations/${reservationId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            return data.counselorId || 1; // 기본 상담사 ID
        }
    } catch (error) {
        console.error('상담사 ID 조회 실패:', error);
    }
    return 1; // 기본값
}