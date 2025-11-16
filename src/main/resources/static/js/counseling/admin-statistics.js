document.addEventListener('DOMContentLoaded', async function() {
    initDatePicker();
    await loadStatistics();
    document.getElementById('searchBtn').addEventListener('click', loadStatistics);
});

function initDatePicker() {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    document.getElementById('startDate').valueAsDate = firstDay;
    document.getElementById('endDate').valueAsDate = today;
}

async function loadStatistics() {
    const token = localStorage.getItem('accessToken');
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    
    try {
        const response = await fetch(`/api/counseling/statistics/admin/summary?startDate=${startDate}&endDate=${endDate}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            document.getElementById('totalCounseling').textContent = `${data.totalCounseling || 0}건`;
            document.getElementById('completedCounseling').textContent = `${data.completedCounseling || 0}건`;
            document.getElementById('avgSatisfaction').textContent = `${data.avgSatisfaction || 0}/5.0`;
            document.getElementById('activeCounselors').textContent = `${data.activeCounselors || 0}명`;
            
            await loadCounselorStats();
            renderCharts(data);
        }
    } catch (error) {
        console.error('통계 데이터 로드 실패:', error);
    }
}

async function loadCounselorStats() {
    const token = localStorage.getItem('accessToken');
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    
    try {
        const response = await fetch(`/api/counseling/statistics/admin/counselors?startDate=${startDate}&endDate=${endDate}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            renderCounselorTable(data);
        }
    } catch (error) {
        console.error('상담사 통계 로드 실패:', error);
    }
}

function renderCounselorTable(list) {
    const tbody = document.getElementById('counselorTableBody');
    if (list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">데이터가 없습니다.</td></tr>';
        return;
    }
    tbody.innerHTML = list.map(item => `
        <tr>
            <td>${item.counselorName}</td>
            <td>${getFieldName(item.field)}</td>
            <td>${item.totalCount || 0}</td>
            <td>${item.completedCount || 0}</td>
            <td>${item.cancelledCount || 0}</td>
            <td>${item.rejectedCount || 0}</td>
            <td>${item.avgSatisfaction || 0}</td>
            <td>${item.responseRate || 0}</td>
        </tr>
    `).join('');
}

function renderCharts(data) {
    if (window.typeChart) window.typeChart.destroy();
    if (window.monthlyChart) window.monthlyChart.destroy();
    if (window.satisfactionChart) window.satisfactionChart.destroy();
    if (window.statusChart) window.statusChart.destroy();
    
    window.typeChart = toastui.Chart.pieChart({
        el: document.getElementById('typeChart'),
        data: {
            categories: ['진로상담', '학업상담', '심리상담', '취업상담'],
            series: [
                { name: '진로상담', data: data.careerCount || 0 },
                { name: '학업상담', data: data.learningCount || 0 },
                { name: '심리상담', data: data.psychologicalCount || 0 },
                { name: '취업상담', data: data.employmentCount || 0 }
            ]
        },
        options: { chart: { width: 400, height: 300 } }
    });
    
    window.monthlyChart = toastui.Chart.lineChart({
        el: document.getElementById('monthlyChart'),
        data: {
            categories: data.monthlyLabels || [],
            series: [{ name: '상담 건수', data: data.monthlyData || [] }]
        },
        options: { chart: { width: 400, height: 300 }, series: { spline: true } }
    });
    
    window.satisfactionChart = toastui.Chart.columnChart({
        el: document.getElementById('satisfactionChart'),
        data: {
            categories: ['1점', '2점', '3점', '4점', '5점'],
            series: [{ name: '응답 수', data: data.satisfactionDistribution || [0,0,0,0,0] }]
        },
        options: { chart: { width: 400, height: 200 } }
    });
    
    window.statusChart = toastui.Chart.pieChart({
        el: document.getElementById('statusChart'),
        data: {
            categories: ['완료', '진행중', '취소', '거부'],
            series: [
                { name: '완료', data: data.completedCounseling || 0 },
                { name: '진행중', data: data.ongoingCount || 0 },
                { name: '취소', data: data.cancelledCount || 0 },
                { name: '거부', data: data.rejectedCount || 0 }
            ]
        },
        options: { chart: { width: 400, height: 200 } }
    });
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
