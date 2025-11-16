// 1. 차트 객체를 전역으로 관리 (수정/삭제 시 필요)
let currentChart = null;

// 2. 차트 생성 함수
function createChart(type = 'line', labels, dataRows) {
    const container = document.getElementById('historyChart');

    // 기존 차트가 있으면 파괴
    if (currentChart) {
        currentChart.destroy();
    }

    // 꺾은선/막대 차트 공통 옵션
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
        legend: {
            visible: true
        },
        tooltip: {
            formatter: (value) => `${Number(value).toFixed(1)}점`
        },
        theme: {
            chart: {
                fontFamily: 'Noto Sans KR, sans-serif'
            }
        },
        responsive: true
    };

    try {
        if (type === 'line') {
            // 꺾은선 차트 데이터 가공
            const chartCategories = labels;
            const chartSeries = dataRows.map(row => ({
                name: row.assessmentTitle,
                data: row.scores
            }));

            currentChart = new toastui.Chart.lineChart({
                el: container,
                data: { categories: chartCategories, series: chartSeries },
                options: { ...chartOptions, title: '역량 변화 추이 (꺾은선)' }
            });

        } else if (type === 'bar') {
            // 막대 차트 데이터 가공
            const barCategories = dataRows.map(row => row.assessmentTitle); // 👈 필드명 일치
            const barSeries = labels.map((label, index) => ({
                name: label,
                data: dataRows.map(row => row.scores[index])
            }));

            currentChart = new toastui.Chart.barChart({
                el: container,
                data: { categories: barCategories, series: barSeries },
                options: { ...chartOptions, title: '역량 변화 추이 (막대)' }
            });
        }
    } catch (error) {
        console.error("차트 생성 중 오류 발생:", error);
        container.innerHTML = `<div class="d-flex justify-content-center align-items-center h-100 text-danger"><i class="fas fa-exclamation-circle me-2"></i> 차트 로드 실패.</div>`;
    }
}

// 3. 페이지 로딩이 완료되면 실행
document.addEventListener('DOMContentLoaded', () => {
    const btnLine = document.getElementById('btnShowLineChart');
    const btnBar = document.getElementById('btnShowBarChart');

    // 4.  HTML의 <script th:inline>에서 선언해준 '데이터'를 여기서 사용!
    // (competencyLabels, historyData 변수는 HTML에 의해 전역 변수로 생성됨)
    if (typeof competencyLabels === 'undefined' || typeof historyData === 'undefined') {
        console.error("Thymeleaf 데이터(competencyLabels, historyData)가 로드되지 않았습니다.");
        return;
    }


    // 6. 초기 차트 로드 (진짜 데이터로 꺾은선 차트 그리기)
    createChart('line', competencyLabels, historyData);

    // 7. 차트 전환 버튼 이벤트
    if (btnLine && btnBar) {
        btnLine.addEventListener('click', () => {
            createChart('line', competencyLabels, historyData); // 진짜 데이터로 그리기
            btnLine.classList.add('active');
            btnBar.classList.remove('active');
        });

        btnBar.addEventListener('click', () => {
            createChart('bar', competencyLabels, historyData); // 진짜 데이터로 그리기
            btnBar.classList.add('active');
            btnLine.classList.remove('active');
        });
    }
});