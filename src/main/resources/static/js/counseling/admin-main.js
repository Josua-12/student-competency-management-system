document.addEventListener('DOMContentLoaded', async function() {
    await loadDashboardData();
});

async function loadDashboardData() {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch('/api/counseling/admin/dashboard', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            const UNIT_COUNT = '건';
            const UNIT_PERSON = '명';
            const RATING_MAX = '5.0';
            
            document.getElementById('totalReservations').textContent = `${data.totalReservations || 0}${UNIT_COUNT}`;
            document.getElementById('pendingApprovals').textContent = `${data.pendingApprovals || 0}${UNIT_COUNT}`;
            document.getElementById('activeCounselors').textContent = `${data.activeCounselors || 0}${UNIT_PERSON}`;
            document.getElementById('avgSatisfaction').textContent = `${data.avgSatisfaction || 0}/${RATING_MAX}`;
            
            await loadPendingList();
            await loadCounselorList();
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
    }
}

async function loadPendingList() {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch('/api/counseling/admin/approvals?status=PENDING&size=5', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            const list = data.content || [];
            const tbody = document.getElementById('pendingList');
            
            if (list.length === 0) {
                tbody.innerHTML = `<tr><td colspan="4" class="text-center">${MESSAGES.NO_PENDING}</td></tr>`;
            } else {
                tbody.innerHTML = list.map(item => `
                    <tr>
                        <td>${item.studentName}</td>
                        <td>${item.counselingType}</td>
                        <td>${formatDate(item.createdAt)}</td>
                        <td><a href="/counseling/admin/approvals" class="btn btn-sm btn-primary">관리</a></td>
                    </tr>
                `).join('');
            }
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
    }
}

async function loadCounselorList() {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch('/api/counseling/admin/counselors', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            const list = data || [];
            const tbody = document.getElementById('counselorList');
            
            if (list.length === 0) {
                tbody.innerHTML = `<tr><td colspan="4" class="text-center">${MESSAGES.NO_COUNSELOR}</td></tr>`;
            } else {
                tbody.innerHTML = list.map(item => `
                    <tr>
                        <td>${item.name}</td>
                        <td>${item.field}</td>
                        <td>0건</td>
                        <td>0/5.0</td>
                    </tr>
                `).join('');
            }
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

const MESSAGES = {
    NO_PENDING: '승인 대기 내역이 없습니다.',
    NO_COUNSELOR: '상담사 데이터가 없습니다.',
    LOAD_ERROR: '데이터 로드 실패'
};

function getFieldName(field) {
    return FIELD_NAMES[field] || field;
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR');
}