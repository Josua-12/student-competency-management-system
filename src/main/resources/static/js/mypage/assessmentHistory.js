const competencyLabels = ['문제해결', '협업능력', '창의적사고', '리더십'];
const historyData = [
    { diagnosisTitle: "2024년 1차 진단", scores: [4.2, 3.8, 4.0, 3.1] },
    { diagnosisTitle: "2024년 2차 진단", scores: [4.6, 4.0, 4.5, 3.5] },
    { diagnosisTitle: "2025년 1차 진단", scores: [4.8, 4.2, 4.6, 3.7] }
];

const chartCategories = competencyLabels;
const chartSeries = historyData.map(row => ({
    name: row.diagnosisTitle,
    data: row.scores
}));

const chartOptions = {
    chart: {
        width: 'auto',
        height: 320
    },
    yAxis: {
        min: 0,
        max: 5,
        title: '점수 (5점 만점)'
    },
    xAxis: {
        title: '역량 항목'
    },
    series: {
        showDot: true,
        colors: ['#4e73df', '#1cc88a', '#36b9cc']
    },
    legend: {
        visible: true
    },
    tooltip: {
        formatter: (value) => `${value.toFixed(1)}점`
    },
    theme: {
        chart: {
            fontFamily: 'Noto Sans KR, sans-serif'
        },
        title: {
            fontSize: 16
        }
    }
};

let currentChart = null;

function createChart(type = 'line') {
    const container = document.getElementById('historyChart');

    if (currentChart) {
        currentChart.destroy();
    }

    const data = {
        categories: chartCategories,
        series: chartSeries
    };

    try {
        if (type === 'line') {
            currentChart = new toastui.Chart.lineChart({
                el: container,
                data: data,
                options: {
                    ...chartOptions,
                    title: '역량 변화 추이 (꺾은선)',
                    responsive: true
                }
            });
        } else if (type === 'bar') {
            const barCategories = historyData.map(row => row.diagnosisTitle);
            const barSeries = competencyLabels.map((label, index) => ({
                name: label,
                data: historyData.map(row => row.scores[index])
            }));

            currentChart = new toastui.Chart.barChart({
                el: container,
                data: {
                    categories: barCategories,
                    series: barSeries
                },
                options: {
                    ...chartOptions,
                    title: '역량 변화 추이 (막대)',
                    responsive: true
                }
            });
        }

        window.addEventListener('resize', () => {
             if (currentChart) {
                currentChart.resize();
            }
        });

    } catch (error) {
        console.error("차트 생성 중 오류 발생:", error);
        container.innerHTML = `<div class="d-flex justify-content-center align-items-center h-100 text-danger"><i class="fas fa-exclamation-circle me-2"></i> 차트 로드 실패. 콘솔 확인.</div>`;
    }
}

function populateTable() {
    const tableBody = document.getElementById('scoreTableBody');
    const tableHeaders = document.querySelectorAll('.history-table thead th[data-label-placeholder]');
    tableHeaders.forEach((th, index) => {
        if (competencyLabels[index]) {
            th.textContent = competencyLabels[index];
        }
    });

    tableBody.innerHTML = '';

    if (historyData.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">진단 이력이 없습니다.</td></tr>`;
        return;
    }

    historyData.forEach(row => {
        const tr = document.createElement('tr');
        let scoreCells = row.scores.map(score => `<td class="text-center">${score.toFixed(1)}</td>`).join('');
        tr.innerHTML = `
            <td class="px-4">${row.diagnosisTitle}</td>
            ${scoreCells}
            <td class="text-center">
                <a href="#" class="btn btn-outline-secondary btn-sm rounded-pill" title="상세보기"><i class="fas fa-search"></i></a>
            </td>
        `;
        tableBody.appendChild(tr);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const btnLine = document.getElementById('btnShowLineChart');
    const btnBar = document.getElementById('btnShowBarChart');

    populateTable();
    createChart('line');

    if (btnLine && btnBar) {
        btnLine.addEventListener('click', () => {
            createChart('line');
            btnLine.classList.add('active');
            btnBar.classList.remove('active');
        });

        btnBar.addEventListener('click', () => {
            createChart('bar');
            btnBar.classList.add('active');
            btnLine.classList.remove('active');
        });
    }
});
