document.addEventListener('DOMContentLoaded', async function() {
    await loadDashboardData();
    await loadTodaySchedule();
});

async function loadDashboardData() {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch('/api/counseling/history/status', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            document.getElementById('pendingReservations').textContent = `${data.pendingCount || 0}건`;
            document.getElementById('todayCounseling').textContent = `${data.todayCount || data.completedCount || 0}건`;
            document.getElementById('monthlyCounseling').textContent = `${data.monthlyCount || data.totalCount || 0}건`;
            document.getElementById('avgSatisfaction').textContent = `${data.avgSatisfaction || 0}/5.0`;
        }
    } catch (error) {
        console.error('대시보드 데이터 로드 실패:', error);
    }
}

async function loadTodaySchedule() {
    const token = localStorage.getItem('accessToken');
    const today = new Date().toISOString().split('T')[0];
    
    try {
        const response = await fetch(`/api/counseling/reservations/assigned`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const result = await response.json();
            const data = (result.content || []).filter(item => 
                item.confirmedDate === today && (item.status === 'CONFIRMED' || item.status === 'COMPLETED')
            );
            const tbody = document.getElementById('scheduleTableBody');
            
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" class="text-center">일정이 없습니다.</td></tr>';
            } else {
                tbody.innerHTML = data.map(item => {
                    const statusBadge = item.status === 'CONFIRMED' ? 'bg-success' : 'bg-primary';
                    const statusText = item.status === 'CONFIRMED' ? '예정' : '완료';
                    
                    return `
                        <tr>
                            <td>${formatTime(item.confirmedStartTime)} - ${formatTime(item.confirmedEndTime)}</td>
                            <td>${item.studentName}</td>
                            <td>${getFieldName(item.counselingField)}</td>
                            <td><span class="badge ${statusBadge}">${statusText}</span></td>
                        </tr>
                    `;
                }).join('');
            }
        }
    } catch (error) {
        console.error('일정 로드 실패:', error);
    }
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

function formatTime(timeStr) {
    if (!timeStr) return '-';
    return timeStr.substring(0, 5);
}