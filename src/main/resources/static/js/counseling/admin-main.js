document.addEventListener('DOMContentLoaded', async function() {
    await loadDashboardData();
});

async function loadDashboardData() {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch('/api/counseling/statistics/admin/dashboard', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            document.getElementById('totalReservations').textContent = `${data.totalReservations || 0}건`;
            document.getElementById('pendingApprovals').textContent = `${data.pendingApprovals || 0}건`;
            document.getElementById('activeCounselors').textContent = `${data.activeCounselors || 0}명`;
            document.getElementById('avgSatisfaction').textContent = `${data.avgSatisfaction || 0}/5.0`;
            
            await loadPendingList();
            await loadCounselorList();
        }
    } catch (error) {
        console.error('대시보드 데이터 로드 실패:', error);
    }
}

async function loadPendingList() {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch('/api/counseling/management/approvals?status=PENDING&size=5', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            const list = data.content || [];
            const tbody = document.getElementById('pendingList');
            
            if (list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" class="text-center">승인 대기 내역이 없습니다.</td></tr>';
            } else {
                tbody.innerHTML = list.map(item => `
                    <tr>
                        <td>${item.studentName}</td>
                        <td>${getFieldName(item.counselingField)}</td>
                        <td>${formatDate(item.createdAt)}</td>
                        <td><a href="/counseling/admin/approvals" class="btn btn-sm btn-primary">관리</a></td>
                    </tr>
                `).join('');
            }
        }
    } catch (error) {
        console.error('승인 대기 목록 로드 실패:', error);
    }
}

async function loadCounselorList() {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch('/api/counseling/management/counselors?isActive=true&size=5', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            const list = data.content || [];
            const tbody = document.getElementById('counselorList');
            
            if (list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" class="text-center">상담사 데이터가 없습니다.</td></tr>';
            } else {
                tbody.innerHTML = list.map(item => `
                    <tr>
                        <td>${item.name}</td>
                        <td>${getFieldName(item.counselingField)}</td>
                        <td>${item.monthlyCount || 0}건</td>
                        <td>${item.avgSatisfaction || 0}/5.0</td>
                    </tr>
                `).join('');
            }
        }
    } catch (error) {
        console.error('상담사 목록 로드 실패:', error);
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

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR');
}