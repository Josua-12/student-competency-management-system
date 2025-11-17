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
        const response = await fetch(`/api/counseling/statistics/overall?startDate=${startDate}&endDate=${endDate}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            document.getElementById('totalCounseling').textContent = `${data.totalCounseling || 0}${UNIT_COUNT}`;
            document.getElementById('completedCounseling').textContent = `${data.completedCounseling || 0}${UNIT_COUNT}`;
            document.getElementById('avgSatisfaction').textContent = `${data.avgSatisfaction || 0}/${RATING_MAX}`;
            document.getElementById('activeCounselors').textContent = `${data.activeCounselors || 0}${UNIT_PERSON}`;
            
            await loadCounselorStats();
            renderCharts(data);
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
    }
}

async function loadCounselorStats() {
    const token = localStorage.getItem('accessToken');
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    
    try {
        const response = await fetch(`/api/counseling/statistics/counselor?startDate=${startDate}&endDate=${endDate}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            renderCounselorTable(data);
        }
    } catch (error) {
        console.error(`${MESSAGES.LOAD_ERROR}:`, error);
    }
}

function renderCounselorTable(list) {
    const tbody = document.getElementById('counselorTableBody');
    if (list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center">${MESSAGES.NO_DATA}</td></tr>`;
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
    
    const typeCategories = [CHART_LABELS.CAREER, CHART_LABELS.LEARNING, CHART_LABELS.PSYCHOLOGICAL, CHART_LABELS.EMPLOYMENT];
    window.typeChart = toastui.Chart.pieChart({
        el: document.getElementById('typeChart'),
        data: {
            categories: typeCategories,
            series: [
                { name: CHART_LABELS.CAREER, data: data.careerCount || 0 },
                { name: CHART_LABELS.LEARNING, data: data.learningCount || 0 },
                { name: CHART_LABELS.PSYCHOLOGICAL, data: data.psychologicalCount || 0 },
                { name: CHART_LABELS.EMPLOYMENT, data: data.employmentCount || 0 }
            ]
        },
        options: { chart: { width: 400, height: 300 } }
    });
    
    window.monthlyChart = toastui.Chart.lineChart({
        el: document.getElementById('monthlyChart'),
        data: {
            categories: data.monthlyLabels || [],
            series: [{ name: CHART_LABELS.COUNT, data: data.monthlyData || [] }]
        },
        options: { chart: { width: 400, height: 300 }, series: { spline: true } }
    });
    
    const satisfactionCategories = ['1점', '2점', '3점', '4점', '5점'];
    window.satisfactionChart = toastui.Chart.columnChart({
        el: document.getElementById('satisfactionChart'),
        data: {
            categories: satisfactionCategories,
            series: [{ name: CHART_LABELS.RESPONSE_COUNT, data: data.satisfactionDistribution || [0,0,0,0,0] }]
        },
        options: { chart: { width: 400, height: 200 } }
    });
    
    const statusCategories = [CHART_LABELS.COMPLETED, CHART_LABELS.ONGOING, CHART_LABELS.CANCELLED, CHART_LABELS.REJECTED];
    window.statusChart = toastui.Chart.pieChart({
        el: document.getElementById('statusChart'),
        data: {
            categories: statusCategories,
            series: [
                { name: CHART_LABELS.COMPLETED, data: data.completedCounseling || 0 },
                { name: CHART_LABELS.ONGOING, data: data.ongoingCount || 0 },
                { name: CHART_LABELS.CANCELLED, data: data.cancelledCount || 0 },
                { name: CHART_LABELS.REJECTED, data: data.rejectedCount || 0 }
            ]
        },
        options: { chart: { width: 400, height: 200 } }
    });
}

const FIELD_NAMES = {
    'PSYCHOLOGICAL': '심리상담',
    'CAREER': '진로상담',
    'EMPLOYMENT': '취업상담',
    'LEARNING': '학습상담'
};

const CHART_LABELS = {
    CAREER: '진로상담',
    LEARNING: '학업상담',
    PSYCHOLOGICAL: '심리상담',
    EMPLOYMENT: '취업상담',
    COMPLETED: '완료',
    ONGOING: '진행중',
    CANCELLED: '취소',
    REJECTED: '거부',
    COUNT: '상담 건수',
    RESPONSE_COUNT: '응답 수'
};

const MESSAGES = {
    NO_DATA: '데이터가 없습니다.',
    LOAD_ERROR: '로드 실패'
};

const UNIT_COUNT = '건';
const UNIT_PERSON = '명';
const RATING_MAX = '5.0';

function getFieldName(field) {
    return FIELD_NAMES[field] || field;
}
