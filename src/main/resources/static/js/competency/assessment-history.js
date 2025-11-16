<!-- 4. JAVASCRIPT (assessment-history.js 내용 통합) -->
<script>
    // assessment-history.js

    // Canvas 환경을 위해 임시 데이터를 사용합니다.
    // (반응형 햄버거 메뉴 기능은 Bootstrap 5의 JS/CSS를 통해 HTML에서 처리됩니다.)

    // 임시 데이터
    const competencyLabels = ['문제해결', '협업능력', '창의적사고', '리더십'];
    const historyData = [
        { diagnosisTitle: "2024년 1차 진단", scores: [4.2, 3.8, 4.0, 3.1] },
        { diagnosisTitle: "2024년 2차 진단", scores: [4.6, 4.0, 4.5, 3.5] },
        { diagnosisTitle: "2025년 1차 진단", scores: [4.8, 4.2, 4.6, 3.7] }
    ];

    // TOAST UI Chart 데이터 준비
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

    // TOAST UI Chart 생성 함수
    function createChart(type = 'line') {
        const container = document.getElementById('historyChart');

        // 기존 차트가 있으면 제거
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
                // 막대 차트 데이터 구조 변환
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

            // 반응형 처리: 차트 크기 조정
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


    // 테이블 데이터 채우기
    function populateTable() {
        const tableBody = document.getElementById('scoreTableBody');
        // 테이블 헤더의 레이블을 동적으로 업데이트
        const tableHeaders = document.querySelectorAll('.history-table thead th[data-label-placeholder]');
        tableHeaders.forEach((th, index) => {
            if (competencyLabels[index]) {
                th.textContent = competencyLabels[index];
            }
        });

        // 테이블 내용 비우기
        tableBody.innerHTML = '';

        // 데이터가 없는 경우 처리
        if (historyData.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">진단 이력이 없습니다.</td></tr>`;
            return;
        }

        // 데이터로 테이블 채우기
        historyData.forEach(row => {
            const tr = document.createElement('tr');
            // 점수 셀 생성 (소수점 첫째 자리까지 표시)
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


    // DOM 로드 완료 후 실행
    document.addEventListener('DOMContentLoaded', () => {
        const btnLine = document.getElementById('btnShowLineChart');
        const btnBar = document.getElementById('btnShowBarChart');

        // 테이블 데이터 먼저 채우기
        populateTable();

        // 초기 차트 로드 (기본값: 꺾은선)
        createChart('line');

        // 차트 버튼이 있을 때만 이벤트 리스너 설정
        if (btnLine && btnBar) {
            // 꺾은선 버튼 클릭 이벤트
            btnLine.addEventListener('click', () => {
                createChart('line');
                btnLine.classList.add('active');
                btnBar.classList.remove('active');
            });

            // 막대 버튼 클릭 이벤트
            btnBar.addEventListener('click', () => {
                createChart('bar');
                btnBar.classList.add('active');
                btnLine.classList.remove('active');
            });
        }
    });