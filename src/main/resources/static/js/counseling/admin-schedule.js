// 상담분야 선택 시 상담사 목록 업데이트
const FIELD_NAMES = {
    'PSYCHOLOGICAL': '심리상담',
    'CAREER': '진로상담',
    'EMPLOYMENT': '취업상담',
    'LEARNING': '학습상담'
};

const STATUS_BADGE = {
    'PENDING': 'bg-warning',
    'APPROVED': 'bg-success',
    'REJECTED': 'bg-danger'
};

const STATUS_TEXT = {
    'PENDING': '대기중',
    'APPROVED': '승인됨',
    'REJECTED': '거부됨'
};

const MESSAGES = {
    SELECT_FIELD: '상담분류를 선택해주세요.',
    SELECT_COUNSELOR: '상담사를 선택해주세요.',
    SELECT_COUNSELOR_PLACEHOLDER: '상담사를 선택하세요',
    NO_OFF_REQUEST: '휴무 신청 내역이 없습니다.',
    SCHEDULE_SAVED: '일정이 저장되었습니다.',
    SCHEDULE_SAVE_FAILED: '일정 저장에 실패했습니다.',
    SCHEDULE_SAVE_ERROR: '일정 저장 중 오류가 발생했습니다.',
    APPROVED: '승인되었습니다.',
    APPROVE_FAILED: '승인에 실패했습니다.',
    APPROVE_ERROR: '승인 중 오류가 발생했습니다.',
    REJECTED: '거부되었습니다.',
    REJECT_FAILED: '거부에 실패했습니다.',
    REJECT_ERROR: '거부 중 오류가 발생했습니다.',
    REQUIRED_REASON: '거부 사유를 입력해주세요.',
    VIEW_DETAIL_TODO: '상세 보기 기능은 추후 구현 예정입니다.',
    LOAD_ERROR: '로드 실패'
};

// 상담분류 선택 시 상담사 목록 업데이트
document.getElementById('fieldSelect').addEventListener('change', async function() {
    const counselorSelect = document.getElementById('counselorSelect');
    const selectedField = this.value;
    
    counselorSelect.innerHTML = `<option value="">${MESSAGES.SELECT_COUNSELOR_PLACEHOLDER}</option>`;
    
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
            console.error(`${MESSAGES.LOAD_ERROR}:`, error);
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
        alert(MESSAGES.SELECT_FIELD);
        return;
    }
    if (!counselorSelect.value) {
        alert(MESSAGES.SELECT_COUNSELOR);
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
                if (day && schedule.isBaseSchedule) {
                    const time = schedule.startTime.substring(0, 2) + schedule.endTime.substring(0, 2);
                    const checkbox = document.querySelector(`input[name="slot${time}_${day}"]`);
                    if (checkbox) checkbox.checked = true;
                }
            });
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
    }
}

// 휴무 신청 목록 로드 (API 미구현)
async function loadOffRequests(counselorId) {
    const tbody = document.querySelector('#offScheduleSection tbody');
    tbody.innerHTML = `<tr><td colspan="6" class="text-center">${MESSAGES.NO_OFF_REQUEST}</td></tr>`;
    // TODO: API 구현 후 활성화
    // try {
    //     const token = localStorage.getItem('accessToken');
    //     const response = await fetch(`/api/counseling/schedules/off-requests/${counselorId}`, {
    //         headers: {'Authorization': `Bearer ${token}`}
    //     });
    //     ...
    // } catch (error) {
    //     console.error(`${MESSAGES.LOAD_ERROR}:`, error);
    // }
}

document.getElementById('saveSchedule').addEventListener('click', async function() {
    const counselorId = document.getElementById('counselorSelect').value;
    if (!counselorId) {
        alert(MESSAGES.SELECT_COUNSELOR);
        return;
    }
    
    const schedules = [];
    const dayMap = {mon: 1, tue: 2, wed: 3, thu: 4, fri: 5};
    const timeMap = {
        '0910': ['09:00', '10:00'], '1011': ['10:00', '11:00'], '1112': ['11:00', '12:00'],
        '1213': ['12:00', '13:00'], '1314': ['13:00', '14:00'], '1415': ['14:00', '15:00'], 
        '1516': ['15:00', '16:00'], '1617': ['16:00', '17:00'], '1718': ['17:00', '18:00']
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
            alert(MESSAGES.SCHEDULE_SAVED);
            loadExistingSchedule(counselorId);
        } else {
            alert(MESSAGES.SCHEDULE_SAVE_FAILED);
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
        alert(MESSAGES.SCHEDULE_SAVE_ERROR);
    }
});

let currentRequestId = null;

// 휴무 신청 승인 (API 미구현)
function approveOffRequest(requestId) {
    alert('휴무 신청 승인 기능은 API 구현 후 사용 가능합니다.');
    // TODO: API 구현 후 활성화
}

// 승인 확인 버튼 (API 미구현)
document.getElementById('confirmApprove')?.addEventListener('click', async function() {
    alert('휴무 신청 승인 기능은 API 구현 후 사용 가능합니다.');
});

// 휴무 신청 거부 (API 미구현)
function rejectOffRequest(requestId) {
    alert('휴무 신청 거부 기능은 API 구현 후 사용 가능합니다.');
    // TODO: API 구현 후 활성화
}

// 거부 확인 버튼 (API 미구현)
document.getElementById('confirmReject')?.addEventListener('click', async function() {
    alert('휴무 신청 거부 기능은 API 구현 후 사용 가능합니다.');
});

// 상세 보기
function viewOffRequest(requestId) {
    alert(MESSAGES.VIEW_DETAIL_TODO);
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR');
}

window.approveOffRequest = approveOffRequest;
window.rejectOffRequest = rejectOffRequest;
window.viewOffRequest = viewOffRequest;