document.addEventListener('DOMContentLoaded', function() {
    // 최신 역량 진단 결과를 기반으로 Chart.js 레이더 차트 생성
    function renderMainRadarChart() {
        const competencyLabels = ['문제해결', '협업능력', '창의적사고', '리더십'];
        const latestScores = [4.8, 4.2, 4.6, 3.7]; // 최신 데이터
        
        const ctx = document.getElementById('mainChartCanvas').getContext('2d');

        new Chart(ctx, {
            type: 'radar',
            data: {
                labels: competencyLabels,
                datasets: [{
                    label: '2025년 1차 진단',
                    data: latestScores,
                    backgroundColor: 'rgba(13, 110, 253, 0.2)',
                    borderColor: 'rgba(13, 110, 253, 1)',
                    borderWidth: 2,
                    pointBackgroundColor: 'rgba(13, 110, 253, 1)',
                    pointBorderColor: '#fff',
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    r: {
                        angleLines: { display: true },
                        suggestedMin: 0,
                        suggestedMax: 5,
                        pointLabels: { font: { size: 12 } },
                        ticks: { display: false }
                    }
                },
                plugins: {
                    legend: { display: false },
                    tooltip: { 
                        callbacks: { 
                            label: (context) => `${context.label}: ${context.parsed.r.toFixed(1)}점` 
                        } 
                    }
                }
            }
        });
    }

    // 차트 초기화
    renderMainRadarChart();
});
