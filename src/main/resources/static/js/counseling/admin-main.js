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
    if (stats.totalReservations) cards[0].textContent = `${stats.totalReservations}건`;
    if (stats.pendingCount) cards[1].textContent = `${stats.pendingCount}건`;
    if (stats.activeCounselors) cards[2].textContent = `${stats.activeCounselors}명`;
    if (stats.avgSatisfaction) cards[3].textContent = `${stats.avgSatisfaction}/5.0`;
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
    tbody.innerHTML = reservations.slice(0, 5).map(r => `
        <tr>
            <td>${r.studentName}</td>
            <td>${r.counselingField}</td>
            <td>${formatDate(r.createdAt)}</td>
            <td><a href="/counseling/admin/approvals?id=${r.id}" class="btn btn-sm btn-primary">처리</a></td>
        </tr>
    `).join('');
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
    if (tbody) {
        tbody.innerHTML = counselors.slice(0, 5).map(c => `
            <tr>
                <td>${c.name}</td>
                <td>${c.specialization}</td>
                <td>${c.monthlyCount}건</td>
                <td>${c.avgSatisfaction}</td>
            </tr>
        `).join('');
    }
}

function formatDate(dateStr) {
    return new Date(dateStr).toLocaleDateString('ko-KR', {month: '2-digit', day: '2-digit'});
}
