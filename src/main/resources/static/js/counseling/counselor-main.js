const API_BASE = '/api/counseling';

let currentDate = new Date();

document.addEventListener('DOMContentLoaded', function() {
    loadDashboardStats();
    loadTodaySchedule();
    setupDateNavigation();
});

async function loadDashboardStats() {
    try {
        const token = localStorage.getItem('accessToken');
        const [pending, assigned] = await Promise.all([
            fetch(`${API_BASE}/reservations?status=PENDING`, {headers: {'Authorization': `Bearer ${token}`}}),
            fetch(`${API_BASE}/reservations/assigned`, {headers: {'Authorization': `Bearer ${token}`}})
        ]);

        if (pending.ok && assigned.ok) {
            const pendingData = await pending.json();
            const assignedData = await assigned.json();
            
            document.querySelector('.text-primary + .h5').textContent = `${pendingData.totalElements || 0}건`;
            document.querySelector('.text-success + .h5').textContent = `${assignedData.content?.filter(r => isToday(r.reservationDate)).length || 0}건`;
        }
    } catch (error) {
        console.error('통계 로드 실패:', error);
    }
}

async function loadTodaySchedule() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`${API_BASE}/reservations/assigned`, {
            headers: {'Authorization': `Bearer ${token}`}
        });

        if (response.ok) {
            const data = await response.json();
            renderScheduleTable(data.content || []);
        }
    } catch (error) {
        console.error('일정 로드 실패:', error);
    }
}

function renderScheduleTable(reservations) {
    const tbody = document.querySelector('table tbody');
    if (reservations.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">일정이 없습니다.</td></tr>';
        return;
    }
    tbody.innerHTML = reservations.map(r => `
        <tr>
            <td>${r.startTime}</td>
            <td>${r.studentName}</td>
            <td>${r.counselingField}</td>
            <td><span class="badge bg-${getStatusColor(r.status)}">${getStatusText(r.status)}</span></td>
        </tr>
    `).join('');
}

function setupDateNavigation() {
    document.getElementById('prevDay').addEventListener('click', () => changeDate(-1));
    document.getElementById('nextDay').addEventListener('click', () => changeDate(1));
    updateDateDisplay();
}

function changeDate(days) {
    currentDate.setDate(currentDate.getDate() + days);
    updateDateDisplay();
    loadTodaySchedule();
}

function updateDateDisplay() {
    const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'short' };
    document.getElementById('currentDate').textContent = currentDate.toLocaleDateString('ko-KR', options);
}

function isToday(dateStr) {
    const today = new Date().toISOString().split('T')[0];
    return dateStr === today;
}

function getStatusColor(status) {
    const colors = {PENDING: 'warning', CONFIRMED: 'success', COMPLETED: 'info', CANCELLED: 'secondary', REJECTED: 'danger'};
    return colors[status] || 'secondary';
}

function getStatusText(status) {
    const texts = {PENDING: '대기', CONFIRMED: '확정', COMPLETED: '완료', CANCELLED: '취소', REJECTED: '거절'};
    return texts[status] || status;
}
