document.addEventListener('DOMContentLoaded', () => {
    renderMyPageChart();
});

function renderMyPageChart() {
    const canvasId = 'mainChartCanvas';
    const ctx = document.getElementById(canvasId);

    // 텍스트를 넣을 엘리먼트들 가져오기
    const avgScoreEl = document.getElementById('latestAvgScore');
    const dateEl = document.getElementById('assessmentDateStr');

    const btnDetail = document.getElementById('btnDetailView');

    // HTML에서 넘겨받은 데이터 확인
    const data = window.latestResultData;

    console.log("📊 마이페이지 차트 데이터:", data); // [확인용] 콘솔을 꼭 확인해봐!

    if (!ctx) return;

    // 1. 데이터가 없는 경우 (null)
    if (!data) {
        const parent = ctx.parentElement;
        parent.innerHTML = `
            <div class="text-center py-5 text-muted" style="background-color: #f8f9fa; border-radius: 10px;">
                <i class="fas fa-chart-pie fa-2x mb-3 opacity-25"></i>
                <p class="small mb-2">아직 완료된 진단 결과가 없습니다.</p>
                <a href="/student/assessment" class="btn btn-sm btn-primary rounded-pill px-3">진단 시작하기</a>
            </div>
        `;
        if (avgScoreEl) avgScoreEl.textContent = "-";
        if (dateEl) dateEl.textContent = "진단 이력이 없습니다.";
        return;
    }

    // 2. 데이터가 있는 경우 - 텍스트 업데이트
    if (data.radarChartData && data.radarChartData.scores) {
        const scores = data.radarChartData.scores;

        // 평균 계산
        const sum = scores.reduce((a, b) => a + b, 0);
        const avg = (sum / scores.length) || 0; // 0으로 나누기 방지

        // 날짜 포맷팅 (ex: 2025.01.20)
        let dateStr = "";
        if (data.submittedAt) {
            const d = new Date(data.submittedAt);
            dateStr = `${d.getFullYear()}.${String(d.getMonth()+1).padStart(2,'0')}.${String(d.getDate()).padStart(2,'0')} 진단`;
        }

        // 화면에 출력
        if (avgScoreEl) {
            avgScoreEl.innerHTML = `${avg.toFixed(1)} <span class="fs-6 text-muted">/ 5.0</span>`;
        }
        if (dateEl) {
            dateEl.textContent = `평균 점수 (${dateStr})`;
        }

        if (btnDetail && data.resultId) {
            btnDetail.href = `/student/assessment/result/${data.resultId}`;
        }
    }

    // 3. 차트 그리기
    new Chart(ctx, {
        type: 'radar',
        data: {
            labels: data.radarChartData.labels,
            datasets: [{
                label: '나의 역량',
                data: data.radarChartData.scores, // 여기가 점수 데이터!

                // 스타일링 (파란색 채우기)
                backgroundColor: 'rgba(13, 110, 253, 0.2)', // 부트스트랩 Primary 색상 (투명도)
                borderColor: 'rgba(13, 110, 253, 1)',     // 테두리 진하게
                borderWidth: 2,
                pointBackgroundColor: 'rgba(13, 110, 253, 1)',
                pointBorderColor: '#fff',
                pointHoverBackgroundColor: '#fff',
                pointHoverBorderColor: 'rgba(13, 110, 253, 1)',
                fill: true // ⭐️ 이 옵션이 있어야 색이 채워짐
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                r: {
                    min: 0,
                    max: 5, // 5점 만점 고정
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        display: false, // 눈금 숫자 숨김 (깔끔하게)
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.1)' // 거미줄 색상 연하게
                    },
                    pointLabels: {
                        font: {
                            size: 12,
                            family: "'Pretendard', sans-serif",
                            weight: '600'
                        },
                        color: '#333'
                    }
                }
            },
            plugins: {
                legend: { display: false }, // 범례 숨김
                tooltip: {
                    enabled: true,
                    callbacks: {
                        label: function(context) {
                            return context.label + ': ' + Number(context.raw).toFixed(2) + '점';
                        }
                    }
                }
            }
        }
    });
}