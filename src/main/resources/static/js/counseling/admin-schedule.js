// 상담분야 선택 시 상담사 목록 업데이트
document.getElementById('fieldSelect').addEventListener('change', async function() {
    const counselorSelect = document.getElementById('counselorSelect');
    const selectedField = this.value;
    
    counselorSelect.innerHTML = '<option value="">상담사를 선택하세요</option>';
    
    if (selectedField) {
        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch(`/api/counseling/management/counselors?field=${selectedField}`, {
                headers: {'Authorization': `Bearer ${token}`}
            });
            
            if (response.ok) {
                const data = await response.json();
                const counselors = data.content || data;
                
                counselorSelect.disabled = false;
                counselors.forEach(counselor => {
                    const option = document.createElement('option');
                    option.value = counselor.userId;
                    option.textContent = counselor.name;
                    counselorSelect.appendChild(option);
                });
            }
        } catch (error) {
            console.error('상담사 목록 로드 실패:', error);
            counselorSelect.disabled = true;
        }
    } else {
        counselorSelect.disabled = true;
    }
});

// 상담사 선택 시 일정 옵션 표시
document.getElementById('loadSchedule').addEventListener('click', function() {
    const fieldSelect = document.getElementById('fieldSelect');
    const counselorSelect = document.getElementById('counselorSelect');
    
    if (!fieldSelect.value) {
        alert('상담분야를 선택해주세요.');
        return;
    }
    if (!counselorSelect.value) {
        alert('상담사를 선택해주세요.');
        return;
    }
    
    document.getElementById('scheduleSection').style.display = 'block';
    document.getElementById('offScheduleSection').style.display = 'block';
    loadExistingSchedule(counselorSelect.value);
    loadOffRequests(counselorSelect.value);
});

async function loadExistingSchedule(counselorId) {
    const token = localStorage.getItem('accessToken');
    try {
        const response = await fetch(`/api/counseling/schedules/base/${counselorId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        document.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
        
        if (response.ok) {
            const schedules = await response.json();
            const dayMap = {1: 'mon', 2: 'tue', 3: 'wed', 4: 'thu', 5: 'fri'};
            
            schedules.forEach(schedule => {
                const day = dayMap[schedule.dayOfWeek];
                if (day) {
                    const time = schedule.startTime.substring(0, 2) + schedule.endTime.substring(0, 2);
                    const checkbox = document.querySelector(`input[name="slot${time}_${day}"]`);
                    if (checkbox) checkbox.checked = true;
                }
            });
        }
    } catch (error) {
        console.error('일정 로드 실패:', error);
    }
}

// 휴무 신청 목록 로드
async function loadOffRequests(counselorId) {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`/api/counseling/schedules/off-requests/${counselorId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const requests = await response.json();
            const tbody = document.querySelector('#offScheduleSection tbody');
            
            if (requests.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center">휴무 신청 내역이 없습니다.</td></tr>';
                return;
            }
            
            tbody.innerHTML = requests.map(req => {
                const statusBadge = req.status === 'PENDING' ? 'bg-warning' : 
                                   req.status === 'APPROVED' ? 'bg-success' : 'bg-danger';
                const statusText = req.status === 'PENDING' ? '대기중' : 
                                  req.status === 'APPROVED' ? '승인됨' : '거절됨';
                
                const buttons = req.status === 'PENDING' ? 
                    `<button class="btn btn-sm btn-success" onclick="approveOffRequest(${req.id})">승인</button>
                     <button class="btn btn-sm btn-danger" onclick="rejectOffRequest(${req.id})">거절</button>` :
                    `<button class="btn btn-sm btn-outline-info" onclick="viewOffRequest(${req.id})">상세</button>`;
                
                return `
                    <tr>
                        <td>${formatDate(req.createdAt)}</td>
                        <td>${formatDate(req.startDate)} ~ ${formatDate(req.endDate)}</td>
                        <td>${req.reason}</td>
                        <td>${req.detailReason || '-'}</td>
                        <td><span class="badge ${statusBadge}">${statusText}</span></td>
                        <td>${buttons}</td>
                    </tr>
                `;
            }).join('');
        }
    } catch (error) {
        console.error('휴무 신청 목록 로드 실패:', error);
    }
}

document.getElementById('saveSchedule').addEventListener('click', async function() {
    const counselorId = document.getElementById('counselorSelect').value;
    if (!counselorId) {
        alert('상담사를 선택해주세요.');
        return;
    }
    
    const schedules = [];
    const dayMap = {mon: 1, tue: 2, wed: 3, thu: 4, fri: 5};
    const timeMap = {
        '0910': ['09:00', '10:00'], '1011': ['10:00', '11:00'], '1112': ['11:00', '12:00'],
        '1314': ['13:00', '14:00'], '1415': ['14:00', '15:00'], '1516': ['15:00', '16:00'],
        '1617': ['16:00', '17:00'], '1718': ['17:00', '18:00']
    };
    
    document.querySelectorAll('input[type="checkbox"]:checked').forEach(cb => {
        const [slot, day] = cb.name.replace('slot', '').split('_');
        const [startTime, endTime] = timeMap[slot];
        schedules.push({dayOfWeek: dayMap[day], startTime, endTime});
    });
    
    const token = localStorage.getItem('accessToken');
    try {
        const response = await fetch(`/api/counseling/schedules/base/${counselorId}`, {
            method: 'PUT',
            headers: {'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json'},
            body: JSON.stringify(schedules)
        });
        
        if (response.ok) {
            alert('일정이 저장되었습니다.');
            loadExistingSchedule(counselorId);
        } else {
            alert('일정 저장에 실패했습니다.');
        }
    } catch (error) {
        console.error('일정 저장 실패:', error);
        alert('일정 저장 중 오류가 발생했습니다.');
    }
});

let currentRequestId = null;

// 휴무 신청 승인
function approveOffRequest(requestId) {
    currentRequestId = requestId;
    const modal = new bootstrap.Modal(document.getElementById('approveModal'));
    modal.show();
}

// 승인 확인 버튼
document.getElementById('confirmApprove').addEventListener('click', async function() {
    if (!currentRequestId) return;
    
    const memo = document.querySelector('#approveModal textarea').value;
    
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`/api/counseling/schedules/off-requests/${currentRequestId}/approve`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({memo})
        });
        
        if (response.ok) {
            alert('승인되었습니다.');
            bootstrap.Modal.getInstance(document.getElementById('approveModal')).hide();
            loadOffRequests(document.getElementById('counselorSelect').value);
            currentRequestId = null;
        } else {
            alert('승인에 실패했습니다.');
        }
    } catch (error) {
        console.error('승인 실패:', error);
        alert('승인 중 오류가 발생했습니다.');
    }
});

// 휴무 신청 거절
function rejectOffRequest(requestId) {
    currentRequestId = requestId;
    const modal = new bootstrap.Modal(document.getElementById('rejectModal'));
    document.querySelector('#rejectModal textarea').value = '';
    modal.show();
}

// 거절 확인 버튼
document.getElementById('confirmReject').addEventListener('click', async function() {
    if (!currentRequestId) return;
    
    const reason = document.querySelector('#rejectModal textarea').value;
    if (!reason.trim()) {
        alert('거절 사유를 입력해주세요.');
        return;
    }
    
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`/api/counseling/schedules/off-requests/${currentRequestId}/reject`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({reason})
        });
        
        if (response.ok) {
            alert('거절되었습니다.');
            bootstrap.Modal.getInstance(document.getElementById('rejectModal')).hide();
            loadOffRequests(document.getElementById('counselorSelect').value);
            currentRequestId = null;
        } else {
            alert('거절에 실패했습니다.');
        }
    } catch (error) {
        console.error('거절 실패:', error);
        alert('거절 중 오류가 발생했습니다.');
    }
});

// 상세 보기
function viewOffRequest(requestId) {
    alert('상세 보기 기능은 추후 구현 예정입니다.');
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR');
}

window.approveOffRequest = approveOffRequest;
window.rejectOffRequest = rejectOffRequest;
window.viewOffRequest = viewOffRequest;