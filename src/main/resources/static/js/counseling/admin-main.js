const API_BASE = '/api/counseling';

document.addEventListener('DOMContentLoaded', function() {
    loadAdminStats();
    loadPendingApprovals();
    loadCounselorStats();
});

async function loadAdminStats() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`${API_BASE}/statistics/overview`, {
            headers: {'Authorization': `Bearer ${token}`}
        });

        if (response.ok) {
            const stats = await response.json();
            updateStatCards(stats);
        }
    } catch (error) {
        console.error('통계 로드 실패:', error);
    }
}

function updateStatCards(stats) {
    const cards = document.querySelectorAll('.card-body .h5');
    if (!cards || cards.length < 4) return;
    
    cards[0].textContent = `${stats.totalReservations || 0}건`;
    cards[1].textContent = `${stats.pendingCount || 0}건`;
    cards[2].textContent = `${stats.activeCounselors || 0}명`;
    cards[3].textContent = `${stats.avgSatisfaction || '0.0'}/5.0`;
}

async function loadPendingApprovals() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`${API_BASE}/reservations?status=PENDING`, {
            headers: {'Authorization': `Bearer ${token}`}
        });

        if (response.ok) {
            const data = await response.json();
            renderPendingTable(data.content || []);
        }
    } catch (error) {
        console.error('승인 대기 목록 로드 실패:', error);
    }
}

function renderPendingTable(reservations) {
    const tbody = document.querySelector('.table tbody');
    if (!tbody) return;
    
    if (reservations.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">승인 대기 내역이 없습니다.</td></tr>';
        return;
    }
    tbody.innerHTML = reservations.slice(0, 5).map(r => `
        <tr>
            <td>${r.studentName || '학생'}</td>
            <td>${getFieldName(r.counselingField)}</td>
            <td>${formatDate(r.createdAt)}</td>
            <td><a href="/counseling/admin/approvals?id=${r.id}" class="btn btn-sm btn-primary">처리</a></td>
        </tr>
    `).join('');
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

async function loadCounselorStats() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`${API_BASE}/statistics/counselors`, {
            headers: {'Authorization': `Bearer ${token}`}
        });

        if (response.ok) {
            const counselors = await response.json();
            renderCounselorTable(counselors);
        }
    } catch (error) {
        console.error('상담사 통계 로드 실패:', error);
    }
}

function renderCounselorTable(counselors) {
    const tbody = document.querySelectorAll('.table')[1]?.querySelector('tbody');
    if (!tbody) return;
    
    if (counselors.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">상담사 데이터가 없습니다.</td></tr>';
        return;
    }
    tbody.innerHTML = counselors.slice(0, 5).map(c => `
        <tr>
            <td>${c.name || c.counselorName}</td>
            <td>${c.specialization || getFieldName(c.counselingField)}</td>
            <td>${c.monthlyCount || 0}건</td>
            <td>${c.avgSatisfaction || '0.0'}</td>
        </tr>
    `).join('');
}

function formatDate(dateStr) {
    return new Date(dateStr).toLocaleDateString('ko-KR', {month: '2-digit', day: '2-digit'});
}
