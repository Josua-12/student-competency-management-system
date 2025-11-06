/**
 * 메인 대시보드 JavaScript
 */

document.addEventListener('DOMContentLoaded', function() {

    // 1️⃣ 핵심역량 그래프 초기화
    initCompetencyChart();

    // 2️⃣ 상담 이력 스크롤 설정
    setupConsultationHistory();
});

/**
 * 핵심역량 Radar 차트 초기화
 * Chart.js 사용
 */
function initCompetencyChart() {
    const chartCanvas = document.getElementById('competencyChart');
    if (!chartCanvas) return;

    // 서버에서 전달받은 역량 데이터 추출
    const competencyData = [];
    const competencyLabels = [];
    const competencyColors = [];

    document.querySelectorAll('.score-item').forEach((item) => {
        const name = item.querySelector('.score-name').textContent;
        const value = parseFloat(item.querySelector('.score-value').textContent);
        const colorDiv = item.style.backgroundColor || '#667eea';

        competencyLabels.push(name);
        competencyData.push(value);
        competencyColors.push(colorDiv);
    });

    // Chart.js 설정
    const ctx = chartCanvas.getContext('2d');
    new Chart(ctx, {
        type: 'radar',
        data: {
            labels: competencyLabels,
            datasets: [{
                label: '역량 점수',
                data: competencyData,
                backgroundColor: 'rgba(102, 126, 234, 0.2)',
                borderColor: '#667eea',
                borderWidth: 2,
                pointBackgroundColor: '#667eea',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 5,
                pointHoverRadius: 7,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            scales: {
                r: {
                    min: 0,
                    max: 100,
                    ticks: {
                        stepSize: 20,
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });
}

/**
 * 상담 이력 스크롤 설정
 */
function setupConsultationHistory() {
    const historyContainer = document.querySelector('.consultation-history');
    if (!historyContainer) return;

    // 스크롤 시 shadow 표시
    historyContainer.addEventListener('scroll', function() {
        if (this.scrollTop > 0) {
            this.style.boxShadow = 'inset 0 2px 4px rgba(0, 0, 0, 0.1)';
        }
    });
}
